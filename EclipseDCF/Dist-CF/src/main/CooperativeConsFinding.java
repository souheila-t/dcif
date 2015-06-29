/**

 * 

 */
package main;

import java.util.ArrayList;
import java.util.List;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;
import solarInterface.CFSolver;
import stats.ConsFindingAgentStats;
import systemStructure.Graph;
import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.SystemMessage;
import agLib.agentCommunicationSystem.SystemMessageTypes;
import agLib.agentCommunicationSystem.protocols.LocalProtocol;
import agents.ConsFindingAgent;
import agents.CooperativeAgent;
import agents.CooperativeDiagnoser;
import agents.PBAgent;

import communication.PBMessage;
import communication.PBMessageTypes;
import communication.protocol.LocalCooperativeProtocol;
import communication.protocol.LocalPBProtocol;
import communication.protocol.MainCooperativeProtocol;


/**
 * @author Viel Charlotte
 *
 */
public class CooperativeConsFinding implements Agent, SystemMessageTypes, PBMessageTypes, 
	Runnable, CooperativeDiagnoser<CNF> {
	
	/**
	 * 
	 * @param filenameNoExt
	 * @param grapheName
	 * @param nbAgents
	 * @throws Exception 
	 */
	public CooperativeConsFinding(String filenameWithExt, String grapheName,
			int nbAgents, long deadline) throws Exception {
		
		long start = System.currentTimeMillis();
		sys = new CanalComm(this);
		this.nbAgents= nbAgents;
		stats = new ConsFindingAgentStats();
		String filenameNoExt=filenameWithExt.substring(0,filenameWithExt.lastIndexOf('.'));
		if (filenameWithExt.endsWith(".xml"))
			society = Graph.loadFromXml(filenameNoExt, grapheName, cSys, sys, LocalPBProtocol.COOPERARTIVE_CF_AGENT, deadline);
		else if (filenameWithExt.endsWith(".dcf"))
			society = Graph.loadFromDcf(filenameNoExt, grapheName, cSys, sys, LocalPBProtocol.COOPERARTIVE_CF_AGENT, deadline);
		cSys = society.getCommunicationModule();
		
		long middle = System.currentTimeMillis();
		
		LocalProtocol baseLocalProt = new LocalCooperativeProtocol(cSys, this);
		MainCooperativeProtocol gbProtocol = new MainCooperativeProtocol(cSys, baseLocalProt);
		cSys.setProtocol(gbProtocol);
		
		startExpe();
		long end = System.currentTimeMillis();
		
		System.out.println(""+consequences.size()+" CHARACTERISTIC CLAUSES");
		System.out.println();
		for (IndepClause c:consequences){
			System.out.println(c);
		}
		System.out.println();
		System.out.println("\nTotal execution time was " + (end - start) + " ms.\n");
		System.out.println("\nExecution time was " + (end - middle) + " ms.\n");
		int s=0;
		int m=0;
		int c=0;
		int r=0;
		for (ConsFindingAgent agent : society.getAgents()) {
			System.out.println(agent.name + " :");
			System.out.println("  Extensions ops\t: " + agent.stats.get(ConsFindingAgentStats.DCF_NB_EXTENSIONS));
			s+=agent.stats.get(ConsFindingAgentStats.DCF_NB_EXTENSIONS).intValue();
			System.out.println("  Sent messages\t\t: " + agent.stats.sentMessages.nb);
			m+=agent.stats.sentMessages.nb;
			System.out.println("  Sent clauses\t\t: " + agent.stats.clausesSent.nb);
			c+=agent.stats.clausesSent.nb;
			System.out.println("  Sent consequences \t: " + agent.stats.conseqSent.nb);
			r+=agent.stats.conseqSent.nb;
			System.out.println("  Received messages\t: " + agent.stats.receivedMessages.nb);
		}
		System.out.println("System :");
		System.out.println("  Sent messages\t\t: " + stats.sentMessages.nb);
		m+=stats.sentMessages.nb;
		System.out.println("  Received messages\t: " + stats.receivedMessages.nb);
		
		System.out.println("Total :");
		System.out.println("  Inference steps\t: " + s);
		System.out.println("  Nb messages\t\t: " + m);
		System.out.println("  Sent clauses\t\t: " + c);
		System.out.println("  Sent consequences\t: " + r);
		System.out.println("  Nb minimal conseq\t: " + consequences.size());
	}

	public synchronized void startExpe(){
		this.start();
		while (!finished){
			try {
				wait(500);
			} catch (InterruptedException e) { }	
		}

	}
	
	public String toString() {
		return "Sys";
	}


	/* (non-Javadoc)
	 * @see agentCommunicationSystem.Agent#getComm()
	 */
	public CanalComm getComm() {
		return sys;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.Agent#isAlive()
	 */

	public boolean isAlive() {
		if (systemThread==null) {
			getComm().die();
		}
		return systemThread!=null;
	}


	/* (non-Javadoc)
	 * @see agentCommunicationSystem.Agent#isDormant()
	 */
	public boolean isDormant() {
		return false;
	}

	/* (non-Javadoc)
	 * @see agentCommunicationSystem.Agent#setDormant(boolean)
	 */
	public void setDormant(boolean val) {}
	
	public void start() {
		cSys.Init();
		for (ConsFindingAgent agent : society.getAgents()) {
			agent.start();
		}

		if (systemThread == null) {
			systemThread = new Thread(this,"System");
			systemThread.start();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run() {
		long start = System.currentTimeMillis();
		Thread myThread = Thread.currentThread();
		boolean allDormant=false;
		while (systemThread == myThread && !finished){
			finished=allDormant;
			try {
				//wait for other agents to put value
				wait(50);
				//check messages
				if (!cSys.getComm().isEmpty()){
					Message<?> m = cSys.getComm().get();
		        	if (m!=null) {
		        		if (m instanceof PBMessage)  {
		        			switch(m.getCode()) {
		        			case PBM_SEND_CONSEQ:
		        				IndepClause conseq=new IndepClause(m.getArgument().toString());
		        				boolean added=consequences.add(conseq);
		        				if (added)
		        					System.out.println("New Consequence : "+conseq);
		        				else
		        					System.out.println("Consequence not minimal");
		        				break;
		        			}
		        		}
		        		m=null;
		        	}					
				}					
			} catch (InterruptedException e) { }
			allDormant = true;
			for (Agent ag: society.getAgents())
				if (!ag.isDormant()) {
					allDormant=false;
					finished = false;
					break;
				}			
		}
		for (Agent ag:society.getAgents()) cSys.send(new SystemMessage(SYS_FINISH,cSys.getComm()),ag.getComm());
		System.out.println(" Finished !");
		long end = System.currentTimeMillis();
		
		System.out.println("\nExecution time was " + (end - start) + " ms.");
		for (ConsFindingAgent agent : society.getAgents()) {
			System.out.println(agent.name + " :");
			System.out.println("  Extensions ops\t:" + agent.stats.get(ConsFindingAgentStats.DCF_NB_EXTENSIONS));
			System.out.println("  Sent messages\t:" + agent.stats.sentMessages.nb);
			System.out.println("  Sent clauses\t\t: " + agent.stats.clausesSent.nb);
			System.out.println("  Received messages\t:" + agent.stats.receivedMessages.nb);
		}
		
	}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#addToTheory(java.lang.Object, agentCommunicationSystem.CanalComm)
	 */
	public boolean addToTheory(CNF ruleSet, CanalComm from) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see agents.CooperativeDiagnoser#askAllLiterals(agentCommunicationSystem.CanalComm)
	 */
	public void askAllResolvableLiterals(CanalComm ag) {}

	/* (non-Javadoc)
	 * @see agents.CooperativeDiagnoser#sendAllLiterals(agentCommunicationSystem.CanalComm)
	 */
		public void sendAllResolvableLiterals(CanalComm ag) {}


	/* (non-Javadoc)
	 * @see agents.CooperativeDiagnoser#updateCommLanguage(agentCommunicationSystem.CanalComm, java.util.ArrayList)
	 */
	public void updateCommLanguage(CanalComm ag, List<IndepLiteral> listeLit) {}


	/* (non-Javadoc)
	 * @see agents.CooperativeDiagnoser#beginExchange()
	 */
	public void consFinding(ArrayList<IndepClause> topClauses, boolean addConsToTheory, boolean originalTopClause) {}
	
	/* (non-Javadoc)
	 * @see agents.CooperativeDiagnoser#getNbNeighbors()
	 */

	public int getNbNeighbors() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see agents.CooperativeDiagnoser#addToListClausesSent(agentCommunicationSystem.CanalComm, logicLanguage.IndepClause)
	 */

	public boolean addToListClausesSent(CanalComm sender, IndepClause clause) {
		return false;
	}

	
	
	
	
	
	
	
	
	
	
	public static void main(String [] args) {
		
		int i=0;
		int nbAgents = 0;
		int depth=-1;
		int length=-1;
		String graphName = "Clique_";
		CanalComm.verbose=true;
		CFSolver.verbose=true;
		CooperativeAgent.verbose=true;

		
		String filenameWithExt = "glucolysis-corrected-TC3-123450.dcf";
		CooperativeAgent.newCarcPb =true;
		nbAgents = 6;
		depth=80;
		//IndepPField pField = IndepPField.parse("[+bankbook, -bankbook, +card, -card]");
	
		PBAgent.refinedPF=true;
		
		try {
			CooperativeConsFinding c=new CooperativeConsFinding(filenameWithExt, graphName, nbAgents, System.currentTimeMillis()+600000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

	
	
	
	private volatile Thread systemThread = null;
//	protected DistributionParameters distParam;
//	protected CFSolver solver;
//	protected XMLDistributedProblem data;
//	protected Network net;
	protected Graph society;
	protected CommunicationModule cSys;
	//protected ArrayList<CooperativeAgent> agents;
	protected int nbAgents;
	protected ConsFindingAgentStats stats;
	//protected BasicAgentCommStats agSystemStats;
	protected boolean finished=false;
	protected CanalComm sys;
	
	public CNF consequences = new CNF();

	
}


