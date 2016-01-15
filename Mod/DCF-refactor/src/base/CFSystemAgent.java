/**

 * 

 */
package base;

import java.util.List;
import java.util.Vector;

import org.nabelab.solar.Clause;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import stats.ConsFindingAgentStats;
import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.SystemMessage;
import agLib.agentCommunicationSystem.SystemMessageTypes;
import agLib.agentCommunicationSystem.ThreadAgent;
import agLib.agentCommunicationSystem.protocols.EmptyMainProtocol;

/**
 * @author Viel Charlotte
 *
 */
public class CFSystemAgent implements Agent, SystemMessageTypes, Runnable {
	
	public CFSystemAgent(){
		super();
		sys=new CanalComm(this);
	}
	
	public CFSystemAgent(CommunicationModule commModule) {	
		//set method
		cSys= commModule;
		sys = commModule.commSystem;
		//set protocol
		cSys.setProtocol(new EmptyMainProtocol(cSys));
	}
	
	public void setCommunicationModule(CommunicationModule commModule) {
		cSys=commModule;
		//set protocol
		cSys.setProtocol(new EmptyMainProtocol(cSys));
	}

	
	
//	class TimeUpTask extends TimerTask{
//		public void run() {
//			timeUp();		
//		}
//	}

	/**
	 * Start the system.
	 */
	public void start(){
		cSys.Init();													//initialise comm system
		List<CanalComm> agents=cSys.getNeighbours();
		for (CanalComm agent:agents) {									//send systart message
			SystemMessage mToSend = new SystemMessage(SYS_START, sys);
			sendMessage(mToSend, agent);
		}
	//	for (ConsFindingAgent ag:tree.getAgents()){
	//		ag.start();
	//	}
		for (int n=agents.size();n>0;n--){
			cSys.getComm().get();										//Ensures every agent has started
		}
		if (launchAll)													//launch agents
			for (CanalComm ag:agents){
				cSys.send(new SystemMessage(SYS_LAUNCH,null), ag);
			}
		else {
			cSys.send(new SystemMessage(SYS_LAUNCH,null), agents.get(startingAg));
		}
		if (systemThread == null) {
			systemThread = new Thread(this,"System");
			systemThread.start();
		}					
	}
	
		
	/**
	 * Send a message mToSend to the target. Allows to count the message
	 * sent with the stats.
	 * 
	 * @param mToSend
	 * @param target
	 */
	private void sendMessage(Message<?> mToSend, CanalComm target) {
		stats.getCounter(ConsFindingAgentStats.CTR_SENT).inc(1);
		cSys.send(mToSend, target);
	}
	

	/** 
	 * @see agLib.agentCommunicationSystem.Agent#getComm()
	 */
	public CanalComm getComm() {
		return sys;
	}

	/**
	 * @see agLib.agentCommunicationSystem.Agent#isAlive()
	 */
	public boolean isAlive() {
		if (systemThread==null) {
			getComm().die();
		}
		return systemThread!=null;
	}

	/**
	 * @see agLib.agentCommunicationSystem.Agent#isDormant()
	 */
	public boolean isDormant() {
		return false;
	}

	/**
	 * @see agLib.agentCommunicationSystem.Agent#setDormant(boolean)
	 */
	public void setDormant(boolean val) {}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run() {
        
		Thread myThread = Thread.currentThread();
		while (systemThread == myThread && !finished){
        	Message<?> m = cSys.getComm().get();
    		
        	if (cSys.gereParProtocol(m)) {
        		stats.getCounter(ConsFindingAgentStats.CTR_RECEIVED).inc(1);
        		cSys.receiveMessage(m);
        	}
        	if (m!=null) {
        		if (m instanceof CFMessage)  {
        			switch(m.getCode()) {
        			case CFMessage.PBM_SEND_CONSEQS:
        				for (Clause conseq:((CFMessage)m).getArgument()){
        					boolean added=consequences.add(conseq);
        					if (verbose){
        						if (added)
        							System.out.println("New Consequence : "+conseq);
        						else
        							System.out.println("Consequence not minimal");
        					}
        				}
        				break;
        			}
        		}
        		if (m instanceof SystemMessage)  {
        			switch(m.getCode()){
          			case SYS_FINISH:
         				finish();
         				break;
        			case SYS_TIMEUP:
        				timeout();
        				break;
        	//			timeOut=true;
        	//			finish();
        			}
        		}
        		m=null;
        	}
        }	
	}
	
	/**
	 * Finish the system.
	 */
	public void finish() {
		CNF reducedCons=new CNF();
		for (Clause cl:consequences)
			reducedCons.addAndReduce(cl);
		consequences=reducedCons;
		for (CanalComm agent:cSys.getNeighbours()) {
			SystemMessage mToSend = new SystemMessage(SYS_FINISH, cSys.getComm());
			sendMessage(mToSend, agent);
		}
		finished = true;	
	}
	
	/**
	 * Timeout
	 */
	public void timeout() {
		List<CanalComm> agents=cSys.getNeighbours();
		List<CanalComm> toRemove = new Vector<CanalComm>();
		
		for (CanalComm agent:agents)
			((ThreadAgent)agent.getOwner()).stop();			//Stop agents
		
		while(agents.size() != 0){
			for (CanalComm agent:agents) {	
				if (agent.getAgentThread() == null){		//Wait until all agents have stopped
					agent.finalize();
					toRemove.add(agent);
				}
			}
			agents.removeAll(toRemove);
			toRemove.clear();
		}
		CNF reducedCons=new CNF();
		for (Clause cl:consequences)
			reducedCons.addAndReduce(cl);
		consequences=reducedCons;
		timeOut = true;
		finished = true;
	}

	/**
	 * Returns a string representation of the object.
	 * @return String
	 */
	public String toString() {
		return "Sys";
	}
	
	

	//PARAMETERS
	public boolean launchAll=true;
	public int startingAg=0;
	
//	protected Tree tree;
	protected CommunicationModule cSys;
	protected CanalComm sys;
//	protected int nbAgents;
	public boolean finished = false;
	private volatile Thread systemThread = null;
//	private int typeConsFinding;
	public ConsFindingAgentStats stats=new ConsFindingAgentStats();
	public CNF consequences = new CNF();
//	private long deadline=-1;
//	public Timer timer;
	public boolean timeOut=false;
	public static boolean verbose=false;
}

