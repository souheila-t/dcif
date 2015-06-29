package mars;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logicLanguage.CNF;
import logicLanguage.CommLanguage;
import logicLanguage.UnitClauseCNF;
import mars.agents.DiagAgentBasic;
import mars.agents.DiagAgentCL;
import mars.bilateralProtocol.MarsLocalProtIncCtxComp;
import mars.bilateralProtocol.MarsLocalProtocol_ECAI;
import mars.reasoning.FullHypothesis;
import mars.reasoning.LocalTheory;
import mars.stats.DiagAgentStats;
import mars.stats.MARSystemStats;

import org.nabelab.solar.parser.ParseException;

import problemDistribution.DistributionParameters;
import problemDistribution.XMLDistributedProblem;
import solarInterface.CFSolver;
import solarInterface.IndepPField;
import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Network;
import agLib.agentCommunicationSystem.SystemMessage;
import agLib.agentCommunicationSystem.SystemMessageTypes;
import agLib.agentCommunicationSystem.ThreadAgent;
import agLib.linkingGraph.LinkingGraph;
import agLib.masStats.BasicAgentCommStats;
import agLib.masStats.ExpResult;

public class MARS implements Agent,SystemMessageTypes,Runnable{

	public static final String version="0.3.1395";

	/*public DistDiagSystem(String theoName, String pbName, DistributionParameters p, LinkingGraph g, DiagStats ds) throws FileNotFoundException, ParseException{
		WEnv env= new WEnv();
		agents=new ArrayList<DiagAgent>();
		distParam=p;
		stats=ds;
		CanalComm syst=new CanalComm(this);
		net=new Network(g,syst,Network.NET_STATIC);		
		cSys=new CommunicationModule(syst, syst, net, ds);
		solver=new ExclusiveSolver();
		try {
			data=new DCFP(env,solver,theoName,pbName,distParam, ds);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i=0;i<distParam.getNbAgent();i++){
			LocalTheory loc=data.getDistTheory().get(i);
			DiagAgent ag=new DiagAgent("ag".concat(Integer.toString(i)),
					loc, 
					cSys.getComm(),
					net, 
					ds);
			g.setAgent(i, ag.getComm());
			agents.add(ag);
		}
	}*/

	public MARS(String filenameNoExt, LinkingGraph g, MARSystemStats dss, 
			boolean incremental, boolean withIndivLanguages) throws FileNotFoundException, ParseException{
		agents=new ArrayList<ConsensusDiagnoserAgent<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF>>();
		stats=dss;
		agSystemStats=new BasicAgentCommStats();
		CanalComm syst=new CanalComm(this);
		net=new Network(g,syst,Network.NET_STATIC);		
		cSys=new CommunicationModule(syst, syst, net, agSystemStats);
		solver=new CFSolver();
		data=new XMLDistributedProblem(filenameNoExt, g, withIndivLanguages);
		for (int i=0;i<data.getNbAgents();i++){
			LocalTheory loc=data.getDistTheory().get(i);
			DiagAgentStats das=new DiagAgentStats();
			stats.addAgStat(das);			
			ConsensusDiagnoserAgent<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag;
			if (withIndivLanguages)
				ag=new DiagAgentCL("ag"+i,loc,
					cSys.getComm(),net, das,incremental);
			else
				ag=new DiagAgentBasic("ag"+i,loc,
						cSys.getComm(),net, das,incremental);
			g.setAgent(i, ag.getComm());
			agents.add(ag);
		}
		if (withIndivLanguages){
			CanalComm[] arrayCC=new CanalComm[data.getNbAgents()];
			int k=0;
			for (Agent ag:agents) arrayCC[k++] = ag.getComm();
			for (int i=0;i<data.getNbAgents();i++){
				CommLanguage l=((DiagAgentCL)agents.get(i)).getRefToCommLanguage();
				l.addAllData(arrayCC, data.getCommonLanguages(i));
				l.removeAgent(agents.get(i).getComm());
	//			agents.get(i).setContextLanguages();
			}
		}
		else {
			IndepPField language=IndepPField.parse("pf("+data.getCommonLanguage()+")");
			for (Agent ag: agents)
				if (ag instanceof DiagAgentBasic){
					((DiagAgentBasic)ag).setCommonLanguage(language);
		//			((DiagAgentBasic)ag).setContextLanguages();
				}
		}
	}
	
	public CanalComm getComm() {
		return cSys.getComm();
	}

	public boolean isAlive() {
		if (systemThread==null) {
			getComm().die();
		}
		return systemThread!=null;
	}
	
	public void start(){
		for (ConsensusDiagnoserAgent<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag:agents){
			ag.start();
		}
		net.Init();
		for (ThreadAgent ag:agents){
			cSys.send(new SystemMessage(SYS_START,null), ag.getComm());
		}
		for (int n=agents.size();n>0;n--){
			cSys.getComm().get();
		}
		for (Agent ag:agents){
			cSys.send(new SystemMessage(SYS_LAUNCH,null), ag.getComm());
			break;
		}
		if (systemThread == null) {
			systemThread = new Thread(this,"System");
			systemThread.start();
		}
		
	}
	
	public synchronized void run(){
		Thread myThread = Thread.currentThread();
		while (systemThread == myThread && !finished){
			try {
				//wait for other agents to put value
				wait(50);
			} catch (InterruptedException e) { }
			finished=true;
			for (Agent ag:agents) {
				if (!ag.isDormant()) {
					finished=false;
					break;
				}
			}
		}
		for (Agent ag:agents) cSys.send(new SystemMessage(SYS_FINISH,cSys.getComm()),ag.getComm());
		System.out.println(" Finished !");
		
	}
	
	public synchronized MARSystemStats startExpe(){
		this.start();
		while (!finished){//TODO remettre !finished
			try {
				wait(500);
				//if (stats.equals(null)) break;
			} catch (InterruptedException e) { }	
		}
		data.computeRedundancy();
		stats.getSystCounter(MARSystemStats.DSS_MANIFREDUNDANCY).set(data.getCurrentObsRedundancy());
		stats.getSystCounter(MARSystemStats.DSS_THEOREDUNDANCY).set(data.getCurrentRuleRedundancy());
		return this.stats;
			
	}
	public boolean isDormant(){
		return false;
	}
	public void setDormant(boolean val){
	}
	
	public String toString(){
		return "Sys";
	}
	
	
	public static void main(String[] args){
		CanalComm.verbose=true;
		DiagAgentBasic.verbose=true;
	//	DiagBasicLocalProtocol.verbose=true;
		MarsLocalProtIncCtxComp.verbose=true;
		MarsLocalProtocol_ECAI.verbose=true;
		LocalTheory.verbose=false;
		CFSolver.verbose=false;
		
		boolean incremental=false;
		boolean useCommLanguages=false;
		
//		String filenameNoExt=new String("t08r1-dist1-6ag");
//		String filenameNoExt=new String("test1storderDistbis");
		String filenameNoExt=new String("DAREscheduleVariant");
//		String filenameNoExt=new String("chain8");
//		String filenameNoExt="exEcai-2ag";
//		String theoryName=new String("FCtest1st"); //"s2_coreTheo"
//		String problemName=new String("1");
		String[] files={"exEcai-2ag"};
		//,"exEcai-3ag","exEcai-3ag",
	//			"test1storderDistbis","test1storderDistbis","chain8","DAREscheduleBisW2"};
	//	String[] files={"chain8-2","chain8-4","chain8-4"};
		
		int[] nbAgents={2,4,4};//,3,4,5,6,8,10,12,15,20,30,40};
		String[] topo={"Clique_","Circuit_", "Ligne_"};//"Ligne_","Circuit_","Arbk2n","Arbk3n","Regk4n","SmWk4p01n","SmWk4p05n"};
		//int[] indDepTopo={0,1,1,1,1,4,4,4};
		int[] indDepTopo={0,1,2};//,8,8,8,8,8,8,8};
	//	double[] distParam={2,2,1,1};
		boolean[][] protIncLang={{false,false}//,								
				//				{true,false},
				//			  	 {false,false},
				//			  	{true,true},
								};
		int nbExpeDefault=1;
		int nbExpe;
		
	for (int t=0;t<topo.length;t++) {
		String grapheName=topo[t];
		int n=indDepTopo[t];
		filenameNoExt=files[t];
		
	//for (;n<nbAgents.length;n++) {
		
		LinkingGraph g=new LinkingGraph(grapheName.concat(Integer.toString(nbAgents[n])));
		if (nbAgents[n]==2 || ((nbAgents[n]==3)&&(topo[t].equals("Ligne_"))))
			nbExpe=1;
		else
			nbExpe=nbExpeDefault;
	for (int p=0;p<protIncLang.length;p++){
		incremental=protIncLang[p][0];
		useCommLanguages=protIncLang[p][1];
	//	DistributionParameters p= new DistributionParameters(nbAgents[n],distParam);
			
		ExpResult resultComplete=new ExpResult();
		ExpResult resultSummarized=new ExpResult();
		String prot="";
		if (incremental) prot+="Inc"; else prot+="ECAI";
		if (useCommLanguages) prot+="-CL"; 
	//	String filename="zTest"+nbExpe+"_"+theoryName+"-"+problemName+"_"+grapheName+p;
		String filename="v"+version+"e"+nbExpe+"_"+filenameNoExt+"_"+prot+"_"+grapheName+nbAgents[n];
		System.out.println(filename);
		
		
		for (int i=0;i<nbExpe;i++){
			MARSystemStats temp=new MARSystemStats();
			MARS dds;
			System.out.print("Turn "+Integer.toString(i)+" : ");
			try {
				dds=new MARS(filenameNoExt,g, temp,incremental,useCommLanguages);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
			
			//launch the system...
			MARSystemStats dss=dds.startExpe();
			resultComplete.addResult(dss.getAllResults());
			resultSummarized.addResult(dss.getSummaryResults());
			//if (results.get(i).getAdequacy()<1)
			//	break;
		/*	System.out.println();
			System.out.println("*****************************");
			System.out.println();
			System.out.println(dds.data.getDistTheory()); */
			
		}

		
		System.out.println();
		System.out.println("*****************************");
		System.out.println();
		try {
			resultComplete.stocker(filename+"All",true, true, true, true);
			resultSummarized.stocker(filename+"Summ", true, true, true, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//for prot
	//}// for nb agent	
	}// for topo
	
	}
	
	
	protected DistributionParameters distParam;
	protected CFSolver solver;
	protected XMLDistributedProblem data;
	protected Network net;
	protected CommunicationModule cSys;
	protected List<ConsensusDiagnoserAgent<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF>> agents;
	protected MARSystemStats stats;
	protected BasicAgentCommStats agSystemStats;
	protected boolean finished=false;

	private volatile Thread systemThread = null;
	
}
