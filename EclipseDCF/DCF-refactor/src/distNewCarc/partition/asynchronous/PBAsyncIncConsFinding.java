/**

 * 

 */
package distNewCarc.partition.asynchronous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.nabelab.solar.Clause;

import distNewCarc.partition.IncConsFindingAgent;
import distNewCarc.partition.PBICFAgentBuilder;
import logicLanguage.IndepClause;
import problemDistribution.DCFProblem;
import problemDistribution.DistributedConsequenceFindingProblem;
import problemDistribution.XMLDistributedProblem;
import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;
import systemStructure.AgentBuilder;
import systemStructure.PartitionGraph;
import systemStructure.Tree;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.SystemMessage;
import base.ActivityChecker;
import base.CFSystemAgent;

/**
 * @author Gauvain Bourgne
 *
 */
public class PBAsyncIncConsFinding  {
	
	public PBAsyncIncConsFinding(){
		super();
	}
	
	public PBAsyncIncConsFinding(DistributedConsequenceFindingProblem<SolProblem> data, boolean newCons, boolean inDepthPrune, long deadline) throws Exception{	
		
		//create system agent
		system=new CFSystemAgent();
		sys = system.getComm();
		
		//other data (useful??)
		this.nbAgents = data.getNbAgents();
		stats = new ConsFindingAgentStats();
		
		// Set Partition Graph (and create agents)
		newConsAsAxiom=newCons;
		inDepthPruning=inDepthPrune;
		AgentBuilder<IncConsFindingAgent> builder=new PBICFAgentBuilder(newConsAsAxiom,inDepthPruning);
		graph=new PartitionGraph<IncConsFindingAgent>(data, "Clique_", sys,  builder,deadline);
		//set protocol
		for (IncConsFindingAgent ag:graph.getAgents()){
			ag.setProtocol(new AsyncProtocol(ag.getCommModule(),ag,sys));
		}
			//compute useful languages
		graph.computeOutputInputLanguages();
			//change structure if necessary (and adapt networks of cAg) : here, not needed
		    //set agent output and input languages (and set hierarchy if needed)
		for (IncConsFindingAgent ag:graph.getAgents()){
			int id=graph.identifier(ag);
			ag.setOutputLanguage(graph.getOutputLanguage(id));
			for (IncConsFindingAgent ag2:graph.getAgents()){
				if (ag!=ag2){
					int id2=graph.identifier(ag2);
					ag.setInputLanguage(graph.getInputLanguage(id2), ag2.getComm());
				}
			}				
		}
		cSys = graph.getCommunicationModule();
		system.setCommunicationModule(cSys);
		
		//create ActivityChecker
		activ=new ActivityChecker(cSys, graph.getAgents());
	}
	
	
	
	/**
	 * Start the thread and the experiment.
	 * @return false if timeout
	 */
	public synchronized boolean startExpe(long deadline){
		for (IncConsFindingAgent ag:graph.getAgents())
			ag.start();
		system.start();
		activ.start();
		
		while (!system.finished){
			try {
				wait(1000);
				if(deadline != -1 && System.currentTimeMillis() > deadline){
					//for (IncConsFindingAgent ag:graph.getAgents())
						//ag.stop();
					activ.stop();
					cSys.send(new SystemMessage(CFSystemAgent.SYS_TIMEUP,null), sys);
					while(!system.finished)
						wait(100);
					break;
				}
			} catch (InterruptedException e) { }	
		}
		return !system.timeOut;
	}
	
	public Collection<Clause> getOutput(){
		return system.consequences;
	}
	
	public List<ConsFindingAgentStats> getAllStats(){
		List<ConsFindingAgentStats> result=new ArrayList<ConsFindingAgentStats>();
		for (IncConsFindingAgent ag:graph.getAgents()){
			result.add(ag.stats);
		}
		return result;
	}
	

	/*
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
			new PBIncConsFinding(filenameWithExt, graphName, root, nbAgents, method, length, System.currentTimeMillis()+600000);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/

	protected PartitionGraph<IncConsFindingAgent> graph;
	protected CommunicationModule cSys;
	protected CanalComm sys;
	protected CFSystemAgent system;
	protected ActivityChecker activ;
	protected int nbAgents;
	protected ConsFindingAgentStats stats;
	public static boolean verbose=false;
	
	protected boolean newConsAsAxiom=false;
	protected boolean inDepthPruning=false;

}

