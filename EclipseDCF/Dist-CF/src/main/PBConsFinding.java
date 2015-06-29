/**

 * 

 */
package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import problemDistribution.DistributedConsequenceFindingProblem;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import solarInterface.CFSolver;
import solarInterface.SolProblem;

import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.SystemMessage;
import agLib.agentCommunicationSystem.SystemMessageTypes;

import stats.ConsFindingAgentStats;
import systemStructure.Tree;

import agents.ConsFindingAgent;
import agents.PBAgent;
import agents.PBDiagnoser;

import communication.PBMessage;
import communication.PBMessageTypes;
import communication.protocol.LocalPBProtocol;
import communication.protocol.MainPBProtocol;

/**
 * @author Viel Charlotte
 *
 */
public class PBConsFinding implements Agent, SystemMessageTypes, PBMessageTypes, 
		Runnable, PBDiagnoser<CNF> {
	
	public PBConsFinding(){
		super();
	}
	
	public PBConsFinding(DistributedConsequenceFindingProblem<SolProblem> data,
			int method, String methodRoot, long deadline) throws Exception{	
		//set method
		this.deadline=deadline;
		sys = new CanalComm(this);
		this.nbAgents = data.getNbAgents();
		typeConsFinding = method;
		stats = new ConsFindingAgentStats();
		System.out.println("Building tree...");
		if (methodRoot==null || methodRoot.length()==0)
			methodRoot="FixedRoot-0";
		if (methodRoot.startsWith("FixedRoot-")){
			int root=Integer.parseInt(methodRoot.substring(methodRoot.indexOf("-")+1));
			tree = new Tree(data, "Clique_", root, cSys, sys, typeConsFinding, deadline);
		}
		else	
			tree = new Tree(data, "Clique_", methodRoot, cSys, sys, typeConsFinding, deadline);
		cSys = tree.getCommunicationModule();
		tree.save("lastTree", true);
		System.out.println("Tree finished.");		
		LocalPBProtocol baseLocalProt = new LocalPBProtocol(cSys, this);
		baseLocalProt.setTypeConsFinding(typeConsFinding);
		MainPBProtocol gbProtocol = new MainPBProtocol(cSys, baseLocalProt);
		cSys.setProtocol(gbProtocol);
	}
	
	
	
	public PBConsFinding(String filenameWithExt, String grapheName, int root, 
			int nbAgents, int typeConsFinding, int lengthLimit, long deadline) throws Exception {
		long start = System.currentTimeMillis();
		sys = new CanalComm(this);
		this.nbAgents = nbAgents;
		this.typeConsFinding = typeConsFinding;
		stats = new ConsFindingAgentStats();
		String filenameNoExt=filenameWithExt.substring(0,filenameWithExt.lastIndexOf('.'));
		if (filenameWithExt.endsWith(".xml"))
			tree = Tree.TreeFromXml(filenameNoExt, grapheName, cSys, sys, typeConsFinding, deadline);
		else if (filenameWithExt.endsWith(".dcf"))
			tree = Tree.TreeFromDcf(filenameNoExt, grapheName, root, cSys, sys, typeConsFinding, lengthLimit);
		cSys = tree.getCommunicationModule();
		tree.save("lastTree2", true);
		
		long middle = System.currentTimeMillis();
		
		LocalPBProtocol baseLocalProt = new LocalPBProtocol(cSys, this);
		baseLocalProt.setTypeConsFinding(typeConsFinding);
		MainPBProtocol gbProtocol = new MainPBProtocol(cSys, baseLocalProt);
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
		for (ConsFindingAgent agent : tree.getAgents()) {
			System.out.println(agent.name + " :");
			System.out.println("  Inference steps\t: " + agent.stats.getCounter(ConsFindingAgentStats.DCF_NB_EXTENSIONS));
			s+=(Long)agent.stats.get(ConsFindingAgentStats.DCF_NB_EXTENSIONS);
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
	
	/**
	 * Start the thread and the experiment.
	 */
	public synchronized void startExpe(){
		this.start();
		while (!finished){
			try {
				wait(1000);
			} catch (InterruptedException e) { }	
		}
	}
	
	
	class TimeUpTask extends TimerTask{
		public void run() {
			timeUp();		
		}
	}

	/**
	 * Start the system.
	 */
	public void start(){
		cSys.Init();
		for (int i = 0; i < nbAgents; i++) {
			CanalComm agent = tree.getAgents().get(i).getComm();
			SystemMessage mToSend = new SystemMessage(SYS_START, sys);
			sendMessage(mToSend, agent);
		}
		for (ConsFindingAgent ag:tree.getAgents()){
			ag.start();
		}
		timer = new Timer();
		long delay=deadline-System.currentTimeMillis();
		if (delay>0){
			System.out.println("Delay : "+delay);
			timer.schedule(new TimeUpTask(), delay);
		}
					
		
		List<CanalComm> leaves = tree.getLeaves();
		switch(typeConsFinding) {
		case LocalPBProtocol.SEQUENTIAL :
		case LocalPBProtocol.HYBRID :
			for (CanalComm leaf : leaves) {
				PBMessage mToSend = new PBMessage(PBM_BEGIN);
				sendMessage(mToSend, leaf);
			}
			break;
			
		case LocalPBProtocol.PARALLEL :
			for (ConsFindingAgent ag : tree.getAgents()) {
				CanalComm agent = ag.getComm();
				PBMessage mToSend = new PBMessage(PBM_BEGIN);
				sendMessage(mToSend, agent);
			}
			break;
		}
		if (systemThread == null) {
			systemThread = new Thread(this,"System");
			systemThread.start();
		}
	}
	
	public void timeUp(){
/*		for (ConsFindingAgent ag : tree.getAgents()) {
			CanalComm agent = ag.getComm();
			SystemMessage mToSend = new SystemMessage(SYS_TIMEUP, sys);
			sendMessage(mToSend, agent);
		}*/
		System.out.println("TIME UP !");
		SystemMessage mToSend = new SystemMessage(SYS_TIMEUP, sys);
		sendMessage(mToSend, sys);

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
        		if (m instanceof PBMessage)  {
        			switch(m.getCode()) {
        			case PBM_SEND_CONSEQ:
        				IndepClause conseq=new IndepClause(m.getArgument().toString());
        				boolean added=consequences.add(conseq);
        				if (verbose){
        					if (added)
            					System.out.println("New Consequence : "+conseq);
            				else
            					System.out.println("Consequence not minimal");
        				}
        				break;
        			case PBM_END:
        				finish();
        				break;
        			}
        		}
        		if (m instanceof SystemMessage)  {
        			switch(m.getCode()){
          			case SYS_FINISH:
         				finish();
         				break;
        			case SYS_TIMEUP:
        				timeOut=true;
        				finish();
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
		timer.cancel();
		CNF reducedCons=new CNF();
		for (IndepClause cl:consequences)
			reducedCons.addAndReduce(cl);
		consequences=reducedCons;
		for (ConsFindingAgent ag:tree.getAgents()) {
			CanalComm agent = ag.getComm();
			SystemMessage mToSend = new SystemMessage(SYS_FINISH, cSys.getComm());
			sendMessage(mToSend, agent);
		}
		if (timeOut)
			System.out.println("------------ TIMED OUT ---------------");
		finished = true;	
	}

	/**
	 * Returns a string representation of the object.
	 * @return String
	 */
	public String toString() {
		return "Sys";
	}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#addToTheory(java.lang.Object, agentCommunicationSystem.CanalComm)
	 */
	public boolean addToTheory(CNF ruleSet, CanalComm from) {
		return false;
	}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#consFinding(int, java.util.ArrayList)
	 */

	public void consFinding(ArrayList<IndepClause> topClauses, boolean addConsToTheory, boolean originalTopClause) {}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#getSons()
	 */
	public ArrayList<CanalComm> getSons() {
		ArrayList<CanalComm> root = new ArrayList<CanalComm>();
		for (ConsFindingAgent agent : tree.getAgents()) {
			if(agent.isRoot()) {
				root.add(agent.getComm());
				break;
			}
		}
		return root;
	}

	
	
	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#isCarcDone()
	 */

	public boolean isCarcDone() {
		return false;
	}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#sendEndToParent()
	 */
	public void sendEndToParent() {}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#getNbEndReceived()
	 */

	public int getNbEndReceived() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see agents.PBDiagnoser#incNbEndReceived()
	 */
	public void incNbEndReceived() {}
	
	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    pb-dcf [Options] -NbAg=N filename.dcf");
		System.out.println("-NbAg=N  indicates the number of agents (must be compatible with the given file)");
		System.out.println("Options");
		System.out.println("-method=C  indicates which method to use \n"+
						   "               S for sequential (default) \n"+
						   "               P for parallel\n" +
						   "		       H for hybrid");
		System.out.println("-graph=prefix  indicates which graph topology to use (name before _ in filename of xml graph) (by default Clique) \n");
		System.out.println("-root=N  indicates which agent should be taken as root (by default, agent 0) \n");
		System.out.println("-d=N  indicates the depth limit");
		System.out.println("-l=N  indicates the length limit");
		System.out.println("-inc  indicates to use incremental computations");
		System.out.println("-prune  indicates to use pruning of consequences");
		System.out.println("-vcomm  verbose communications");
		System.out.println("-vsolv  verbose computations");
		System.out.println("-vagent  verbose agent");
	}
	

	public static void main(String [] args) {

		int i=0;
		int nbAgents = 0;
		boolean prune=false;
		boolean inc=false;
		int root=0;
		int depth=-1;
		int length=-1;
		int method=LocalPBProtocol.SEQUENTIAL;
		String graphName = "Clique_";
		CanalComm.verbose=false;
		CFSolver.verbose=false;
		PBAgent.verbose=false;
		
		while (i<args.length && args[i].startsWith("-")) {
			if (args[i].startsWith("-nbAg=")){
				nbAgents=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].trim().equals("-prune")){
				prune=true;
				i++;
				continue;
			}
			if (args[i].trim().equals("-vcomm")){
				CanalComm.verbose=true;
				i++;
				continue;
			}
			if (args[i].trim().equals("-vsolv")){
				CFSolver.verbose=true;
				i++;
				continue;
			}
			if (args[i].trim().equals("-vagent")){
				PBAgent.verbose=true;
				i++;
				continue;
			}
			if (args[i].trim().equals("-inc")){
				inc=true;
				i++;
				continue;
			}
			if (args[i].startsWith("-root=")){
				root=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].startsWith("-d=")){
				depth=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].startsWith("-l=")){
				length=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].startsWith("-method=")){
				char m=args[i].substring(args[i].indexOf("=")+1).charAt(0);
				switch(m){
				case 'S':case 's':
					method=LocalPBProtocol.SEQUENTIAL;
				break;
				case 'P':case 'p':
					method=LocalPBProtocol.PARALLEL;
				break;
				case 'H':case 'h':
					method=LocalPBProtocol.HYBRID;
				}
				i++;
				continue;
			}
			if (args[i].startsWith("-graph=")){
				graphName=args[i].substring(args[i].indexOf("=")+1).trim();
				if (!graphName.endsWith("_"))
					graphName=graphName+"_";
				
				i++;
				continue;
			}
			else{
				printHelp();
				return;
			}
		}
		if (args.length<=i){
			printHelp();
			return;			
		}
		String filenameWithExt=args[i].trim();

		PBAgent.refinedPF=true;
		PBAgent.pruneCsq=prune;
		PBAgent.incremental=inc;
		try {
			new PBConsFinding(filenameWithExt, graphName, root, nbAgents, method, length, System.currentTimeMillis()+600000);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	protected Tree tree;
	protected CommunicationModule cSys;
	protected CanalComm sys;
	protected int nbAgents;
	public boolean finished = false;
	private volatile Thread systemThread = null;
	private int typeConsFinding;
	protected ConsFindingAgentStats stats;
	public CNF consequences = new CNF();
	private long deadline=-1;
	public Timer timer;
	private boolean timeOut=false;
	public static boolean verbose=false;
}

