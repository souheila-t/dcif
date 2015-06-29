/**
 * 
 */

package systemStructure;



import genLib.tools.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import communication.protocol.LocalPBProtocol;




import logicLanguage.CNF;
import logicLanguage.IndepLiteral;


import problemDistribution.DCFProblem;
import problemDistribution.DistributedConsequenceFindingProblem;
import problemDistribution.XMLDistributedProblem;

import solarInterface.SolProblem;


import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Network;
import agLib.linkingGraph.LinkingGraph;
import agLib.masStats.BasicAgentCommStats;

import stats.ConsFindingAgentStats;
import stats.ConsFindingSystemStats;
import theory.ConsFindingLocalTheory;
import agents.ConsFindingAgent;
import agents.CooperativeAgent;
import agents.PBAgent;



/**

 * @author Viel Charlotte

 * 

 */

public class Graph {

	HashMap<Pair<Integer,Integer>, Collection<IndepLiteral>> ConnectGraphLabels;
	List<List<Integer>> ConnectGraph;
	protected List<ConsFindingAgent> agents;
	public int nbAgents;
	protected int typeCF;
	CommunicationModule cSys;
	public String filenameNoExt;
	public String grapheName;
	
	/**
	 * @return cSys, the communication module
	 * @see agentCommuncationSystem.CommunicationModule
	 */

	public CommunicationModule getCommunicationModule() {
		return cSys;
	}


	/**
	 * Create a graph with an XML file containing the agents, an XML file containing
	 * how the agents will be connected, the number of agents and the multi-agents
	 * system that will have this graph.
	 * 
	 * @param filenameNoExt : name of the XML file without extension containing the agents
	 * @param grapheName : name of the XML file without extension containing the edges
	 * @param ma : multi-agents system
	 * @param nbAgents : number of agents
	 */
	public Graph(DistributedConsequenceFindingProblem<SolProblem> data, String grapheName, 
			CommunicationModule cSys, CanalComm syst, int typeConsFinding, long deadline) {
		long start=System.currentTimeMillis();
		agents = new ArrayList<ConsFindingAgent>();
		ConnectGraph = new ArrayList<List<Integer>>();
		this.nbAgents = data.getNbAgents();
	
		typeCF=typeConsFinding;
		for (int i = 0; i <nbAgents; i++) {
			ConnectGraph.add(new ArrayList<Integer>());
		}
		ConnectGraphLabels = new HashMap<Pair<Integer,Integer>, Collection<IndepLiteral>>();
		
		this.grapheName = grapheName;
		
		createGraph(cSys, syst, typeConsFinding, data, deadline);
		setLabels();
		this.nbAgents = data.getNbAgents();
		long end=System.currentTimeMillis();
		System.out.println("Time for creating graph: "+ (double)(end-start)/1000+"s");
	}



	/**
	 * Create a graph with an .dcf file containing the agents (and their theories), 
	 * an XML file containing how the agents will be connected, and the multi-agents
	 * communication system that will have this graph.
	 * 
	 * @param filenameNoExt : name of the XML file without extension containing the agents
	 * @param grapheName : name of the XML file without extension containing the edges
	 * @param ma : multi-agents system
	 * @param nbAgents : number of agents
	 * @throws Exception 
	 */
	public static Graph loadFromDcf(String filenameNoExt, String grapheName, 
			 CommunicationModule cSys, CanalComm syst, int typeConsFinding, long deadline) throws Exception {

		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new DCFProblem(filenameNoExt);
		
		return new Graph(data, grapheName, cSys, syst, typeConsFinding, deadline);
	}


	/**
	 * Create a graph with an XML file containing the agents, an XML file containing
	 * how the agents will be connected, the number of agents and the multi-agents
	 * system that will have this graph.
	 * 
	 * @param filenameNoExt : name of the XML file without extension containing the agents
	 * @param grapheName : name of the XML file without extension containing the edges
	 * @param ma : multi-agents system
	 * @param nbAgents : number of agents
	 */
	public static Graph loadFromXml(String filenameNoExt, String grapheName, 
			 CommunicationModule cSys, CanalComm syst, int typeConsFinding, long deadline) {

		boolean withIndivLanguages = false;
		
		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new XMLDistributedProblem(filenameNoExt, grapheName, withIndivLanguages);
		
		return new Graph(data, grapheName, cSys, syst, typeConsFinding, deadline);
	}
	
	/**
	 * Create a graph with the agents and edges, but without labels on the edges.
	 * 
	 * @param filenameNoExt : name of the XML file without extension containing the agents
	 * @param grapheName : name of the XML file without extension containing the edges
	 * @param ma : multi-agents system
	 * @param nbAgents : number of agents
	 */
	private void createGraph(CommunicationModule cSys, CanalComm syst,
			int typeConsFinding, DistributedConsequenceFindingProblem<SolProblem> data, long deadline) {

		// Create and set the number of agents in the graph.
		LinkingGraph graph = new LinkingGraph(grapheName+data.getNbAgents());
		nbAgents=data.getNbAgents();
		Network net = new Network(graph, syst, Network.NET_STATIC);
		BasicAgentCommStats agSystemStats = new BasicAgentCommStats();
		cSys = new CommunicationModule(syst, syst, net, agSystemStats);
		this.cSys = cSys;
		
		// add the agents to the graph.
		ConsFindingSystemStats stats = new ConsFindingSystemStats();
		ConsFindingLocalTheory loc;
		ConsFindingAgentStats das;
		ConsFindingAgent agent;
		linkingGraphToGraph(graph);
		
		// For all the agents
		for (int i = 0; i < data.getNbAgents(); i++) {
			// TODO
			loc = new ConsFindingLocalTheory(data.getDistTheory().get(i), i);
			das = new ConsFindingAgentStats();
			stats.addAgStat(das);
			if (typeCF==LocalPBProtocol.COOPERARTIVE_CF_AGENT)
				agent = new CooperativeAgent("ag" + i, loc, cSys.getComm(), net, das, data.getNbAgents(), data.getGbPField(), deadline);
			else
				agent = new PBAgent("ag" + i, loc, cSys.getComm(), net, das, typeConsFinding, deadline);
			agents.add(agent);
			graph.setAgent(i, agent.getComm());		}
	
	}

	/**
	 * Put the common language on the edges between all pairs of agents.
	 *
	 */
	private void setLabels() {
		Pair<Integer,Integer> set1,set2;

		for (int i = 0; i < nbAgents; i++) {
			ConsFindingAgent agent = agents.get(i);
			List<ConsFindingAgent> voisins = getVoisins(agents.get(i));
			for (int j = 0; j < voisins.size(); j++) {
				ConsFindingAgent voisin = voisins.get(j);
				
				HashSet<IndepLiteral> commonLiterals = searchCommonLiterals(
						agent.getAgentTheory().getTheory(false).getAllClauses(),
						voisin.getAgentTheory().getTheory(false).getAllClauses() 
						);
				set1 = new Pair<Integer,Integer>(i,identifier(voisin));
				ConnectGraphLabels.put(set1, commonLiterals);
				set2 = new Pair<Integer,Integer>(identifier(voisin),i);
				if (polarizedCommLang){
					HashSet<IndepLiteral> cLit2=new HashSet<IndepLiteral>();
					for (IndepLiteral lit:commonLiterals){
						cLit2.add(lit.negate(false));
					}
				}
				else
					ConnectGraphLabels.put(set2, commonLiterals);
			}
		}
	}
	
	/**
	 * Return the neighboring agents of agent.
	 * 
	 * @param agent
	 * @return list of neighboring agents
	 */
	public ArrayList<ConsFindingAgent> getVoisins(ConsFindingAgent agent) {
		ArrayList <ConsFindingAgent> res = new ArrayList<ConsFindingAgent>();
		for (Integer indNeigh : ConnectGraph.get(identifier(agent))) {
			res.add(agents.get(indNeigh.intValue()));
		}		
		return res;
	}
	
	/**
	 * Returns the index of the agent.
	 * 
	 * @param inconnu
	 * @return int
	 */
	public int identifier(ConsFindingAgent inconnu){
		int indice = agents.indexOf(inconnu);
		return indice;
	}

	/**
	 * Returns the index of all neighboring agents of agent.
	 * 
	 * @param agent
	 * @return list of index of the neighboring agents
	 */
	public ArrayList<Integer> getVoisins(int indice){
		ArrayList <Integer> res = new ArrayList <Integer>();
		for (Integer indNeigh:ConnectGraph.get(indice)) {
			res.add(indNeigh);
		}		
		return res;
	}

	/**
	 * Find the commons literals of two CNF.
	 * It is used to create the common communicating language between two agents.
	 * 
	 * @param c1 : CNF
	 * @param c2 : CNF
	 * @return an array with literals in common
	 */
	private HashSet<IndepLiteral> searchCommonLiterals(CNF c1, CNF c2) {
		List<IndepLiteral> listeC1 = new ArrayList<IndepLiteral>();
		List<IndepLiteral> listeC2 = new ArrayList<IndepLiteral>();
		
		if (polarizedCommLang){
			listeC1 = c1.getVocabulary();
			listeC2 = c2.getNegatedVocabulary();
		}
		else {
			listeC1 = c1.getFullVocabulary();
			listeC2 = c2.getFullVocabulary();
		}
		
		
		listeC1.retainAll(listeC2);
		HashSet<IndepLiteral> hs = new HashSet<IndepLiteral>();
		hs.addAll(listeC1);
		return hs;
	}
	
	
	/**
	 * Test if the graph has cycles or not.
	 * 
	 * @return boolean
	 */
	protected boolean hasCycles() {
		boolean visite[] = new boolean[nbAgents];
		for (int i = 0; i < visite.length; i++) {
			visite[i] = false;
		}
		
		boolean res = depthFirstSearch(0, -1, visite);
		
		return res;
	}
	
	/**
	 * Do a depth first search in the graph from the node sommet and return true 
	 * if it finds a cycle.
	 * 
	 * @param sommet : base node.
	 * @param visite : array saying if a node has been visited or not.
	 * @return boolean
	 */
	protected boolean depthFirstSearch(int sommet, int source, boolean visite[]) {
		visite[sommet] = true;
		for (int i = 0; i < ConnectGraph.get(sommet).size(); i++) {
			int voisin = (int)(ConnectGraph.get(sommet).get(i));
			if(voisin != source) {
				if(visite[voisin]) {
					return true;
				}
				depthFirstSearch(voisin, sommet, visite);
			}
		}
		return false;
	}
	
	/**
	 * Returns cycles in a graph.
	 * 
	 * @return ArrayList<ArrayList<Integer>>
	 */
	public List<List<Integer>> searchCycles(int sufficientlySmallSize) {
		int[] visite = new int[nbAgents];
		List<List<Integer>> ancestors = new ArrayList<List<Integer>>();
		List<List<Integer>> listeCycles = new ArrayList<List<Integer>>();
		Stack<Integer> pile = new Stack<Integer>();
		
		for (int i = 0; i < nbAgents; i++) {
			visite[i] = 0;
			ancestors.add(new ArrayList<Integer>());
		}
		
		widthSearch(0, visite, pile, ancestors, listeCycles, sufficientlySmallSize);
	return listeCycles;
	}
	
	/**
	 * Do a depth-first search to find cycles in the graph.
	 * 
	 * @param node
	 * @param visite
	 * @param pile
	 * @param ancestors should be initialized as a list of 'nb-agent' empty integer lists. 
	 * Will contain for each node, the list of its ancestors (first element being the root, and last one the direct parent)
	 * @param listeCycles
	 */
	private void widthSearch(int node, int[] visite, Stack<Integer> pile,
			List<List<Integer>> ancestors,List<List<Integer>> listeCycles, int sufficientlySmallSize) {
		
		visite[node] = 2;
		for (Integer voisin:ConnectGraph.get(node)) {
			if(visite[voisin] == 0) { //unmarked node : no other way to it yet
				pile.push(voisin);
				ancestors.get(voisin).addAll(ancestors.get(node)); 
				ancestors.get(voisin).add(node);
				visite[voisin] = 1;
			} else if(visite[voisin] == 1) { // already visited through antoher way -> cycle
				List<Integer> cycle=getCycle(node,voisin,ancestors);
				listeCycles.add(cycle);
				if (cycle.size()<=sufficientlySmallSize)
					return;
			}
		}
		if(!pile.isEmpty()) {
			int next = pile.pop();
			widthSearch(next, visite, pile, ancestors, listeCycles, sufficientlySmallSize);
		}
	}
	
	
	private List<Integer> getCycle(int node, int voisin, List<List<Integer>> ancestors){
		List<Integer> cycle= new ArrayList<Integer> ();
		List<Integer> nAnc=ancestors.get(node);
		List<Integer> vAnc=ancestors.get(voisin);
		int ind=0;
		while (ind < vAnc.size() && ind < nAnc.size() && vAnc.get(ind)==nAnc.get(ind))
			ind++;
		//add from last common ancestor of node (nAnc(ind-1) to node, then add from voisin to last different ancestor of vAnc (vAnc(ind)?)
		for (int i=ind-1;i<nAnc.size();i++) //ind!=0 as root is common
			cycle.add(nAnc.get(i));
		cycle.add(node);
		cycle.add(voisin);
		for (int j=vAnc.size()-1;j>=ind;j--)
			cycle.add(vAnc.get(j));
		return cycle;
	}
	
	
	
	/**
	 * Transforms a linkingGraph into a graph.
	 * 
	 * @param graph
	 */
	private void linkingGraphToGraph(LinkingGraph graph) {
		for (int i = 0; i < graph.ConnectGraph.size(); i++) {
			for (int j = 0; j < graph.ConnectGraph.get(i).size(); j++) {
				ConnectGraph.get(i).add(graph.ConnectGraph.get(i).get(j));
			}
		}
	}

	public List<ConsFindingAgent> getAgents(){
		return agents;
	}

	public static boolean polarizedCommLang=true;

}

