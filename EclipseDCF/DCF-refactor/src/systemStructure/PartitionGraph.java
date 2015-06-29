
package systemStructure;



import genLib.tools.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.nabelab.solar.Literal;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.pfield.PField;

import logicLanguage.IndepLiteral;
import problemDistribution.DCFProblem;
import problemDistribution.DistributedConsequenceFindingProblem;
import problemDistribution.XMLDistributedProblem;
import solarInterface.IndepPField;
import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;
import stats.ConsFindingSystemStats;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Network;
import agLib.linkingGraph.LinkingGraph;
import agLib.masStats.BasicAgentCommStats;
import base.ConsFindingLocalTheory;
import base.TheoryAgent;




/**

 * @author Viel Charlotte

 * 

 */

public class PartitionGraph<ConsFindingAgent extends TheoryAgent> {

	HashMap<Pair<Integer,Integer>, Collection<PLiteral>> ConnectGraphLabels;
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
	public PartitionGraph(DistributedConsequenceFindingProblem<SolProblem> data, String grapheName, 
			CanalComm syst, AgentBuilder<ConsFindingAgent> builder,long deadline) {
		long start=System.currentTimeMillis();
		agents = new ArrayList<ConsFindingAgent>();
		ConnectGraph = new ArrayList<List<Integer>>();
		this.nbAgents = data.getNbAgents();
	
		//typeCF=typeConsFinding;
		for (int i = 0; i < nbAgents; i++) {
			ConnectGraph.add(new ArrayList<Integer>());
		}
		ConnectGraphLabels = new HashMap<Pair<Integer,Integer>, Collection<PLiteral>>();
		
		this.grapheName = grapheName;
		
		createGraph(syst, builder, data, deadline);
		//computeLocalCommLanguages();
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
	public <CFAg extends TheoryAgent> PartitionGraph<CFAg> loadFromDcf(String filenameNoExt, String grapheName, 
			 CanalComm syst, AgentBuilder<CFAg> builder, long deadline) throws Exception {

		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new DCFProblem(filenameNoExt);
		
		return new PartitionGraph<CFAg>(data, grapheName, syst, builder, deadline);
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
	public static <CFAg extends TheoryAgent> PartitionGraph<CFAg> loadFromXml(String filenameNoExt, String grapheName, 
			 CanalComm syst, AgentBuilder<CFAg> builder, long deadline) {

		boolean withIndivLanguages = false;
		
		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new XMLDistributedProblem(filenameNoExt, grapheName, withIndivLanguages);
		
		return new PartitionGraph<CFAg>(data, grapheName, syst, builder, deadline);
	}
	
	/**
	 * Create a graph with the agents and edges, but without labels on the edges.
	 * 
	 * @param filenameNoExt : name of the XML file without extension containing the agents
	 * @param grapheName : name of the XML file without extension containing the edges
	 * @param ma : multi-agents system
	 * @param nbAgents : number of agents
	 */
	private void createGraph(CanalComm syst,
			AgentBuilder<ConsFindingAgent> builder, DistributedConsequenceFindingProblem<SolProblem> data, long deadline) {

		// load graph
		nbAgents=data.getNbAgents();
		LinkingGraph graph = new LinkingGraph(grapheName+nbAgents);
		linkingGraphToGraph(graph);
		
		//create communication network
		Network net = new Network(graph, syst, Network.NET_STATIC);
		BasicAgentCommStats agSystemStats = new BasicAgentCommStats();
		cSys = new CommunicationModule(syst, syst, net, agSystemStats);
		
		// add the agents to the graph.
		ConsFindingSystemStats stats = new ConsFindingSystemStats();
		ConsFindingLocalTheory loc;
		ConsFindingAgentStats das;
		ConsFindingAgent agent;		
  		    // For all the agents
		for (int i = 0; i < data.getNbAgents(); i++) {
			loc = new ConsFindingLocalTheory(data.getDistTheory().get(i), i);
			das = new ConsFindingAgentStats();
			stats.addAgStat(das);
			agent = builder.createAgent(i, loc, syst, net, das);  // note that all agents share same net, so modif cSys network will affect everyone
			agents.add(agent);
			graph.setAgent(i, agent.getComm());  // this also modifies net (and all cAg) as it is built on graph		
		}
	
	}

	//TODO
	//methods
	// buildLocalCommLanguages
	// buildInputAndOutputLanguages
	// 
	
	
	/**
	 * Compute the input and output languages of each agent, and put them as label on edge (i,-1) and (-1,i).
	 *
	 */
	public static final int ALL=-1;
	
	public void computeOutputInputLanguages() throws ParseException {
		Pair<Integer,Integer> set1,set2;

		for (int i = 0; i < nbAgents; i++) {
			ConsFindingAgent agent = agents.get(i);
			HashSet<PLiteral> output= new HashSet<PLiteral>();
			//HashSet<IndepLiteral> input= new HashSet<IndepLiteral>();
			for (int j = 0; j < nbAgents; j++) {
				if (j==i) continue;
				ConsFindingAgent voisin = agents.get(j);
				HashSet<PLiteral> commonLiterals = searchCommonLiterals(agent,voisin);
				output.addAll(commonLiterals);				
			}
			set1 = new Pair<Integer,Integer>(i,ALL);
			ConnectGraphLabels.put(set1, output);
			set2 = new Pair<Integer,Integer>(ALL,i);
			HashSet<PLiteral> cLit2=new HashSet<PLiteral>();
			for (PLiteral lit:output){
				PLiteral toAdd = new PLiteral(lit);
				toAdd.negate();
				cLit2.add(toAdd);
			}
			ConnectGraphLabels.put(set2, cLit2);
		}
	}

	public PField getOutputLanguage(int i){
		List<PLiteral> lits=new ArrayList<PLiteral>();
		Pair<Integer,Integer> key = new Pair<Integer,Integer>(i,ALL);
		lits.addAll(ConnectGraphLabels.get(key));
		ConsFindingAgent ag = agents.get(i);
		return IndepPField.createPField(ag.getEnv(), ag.getOptions(), lits);
	}
	
	
	public PField getInputLanguage(int i){
		List<PLiteral> lits=new ArrayList<PLiteral>();
		Pair<Integer,Integer> key = new Pair<Integer,Integer>(ALL,i);
		lits.addAll(ConnectGraphLabels.get(key));
		ConsFindingAgent ag = agents.get(i);
		return IndepPField.createPField(ag.getEnv(), ag.getOptions(), lits);
	}
	
	public PField getCommLanguage(int i, int j){
		List<PLiteral> lits=new ArrayList<PLiteral>();
		Pair<Integer,Integer> key = new Pair<Integer,Integer>(i,j);
		lits.addAll(ConnectGraphLabels.get(key));
		ConsFindingAgent ag = agents.get(i);
		return IndepPField.createPField(ag.getEnv(), ag.getOptions(), lits);
	}

	/**
	 * Put the common language on the edges between all pairs of agents.
	 * @throws ParseException 
	 *
	 */
	public void computeLocalCommLanguages() throws ParseException {
		Pair<Integer,Integer> set1,set2;

		for (int i = 0; i < nbAgents; i++) {
			ConsFindingAgent agent = agents.get(i);
			List<ConsFindingAgent> voisins = getVoisins(agents.get(i));
			for (int j = 0; j < voisins.size(); j++) {
				ConsFindingAgent voisin = voisins.get(j);
				
				HashSet<PLiteral> commonLiterals = searchCommonLiterals(agent,voisin);
				set1 = new Pair<Integer,Integer>(i,identifier(voisin));
				ConnectGraphLabels.put(set1, commonLiterals);
				set2 = new Pair<Integer,Integer>(identifier(voisin),i);
				if (polarizedCommLang){
					HashSet<PLiteral> cLit2=new HashSet<PLiteral>();
					for (PLiteral lit:commonLiterals){
						PLiteral toAdd = new PLiteral(lit);
						toAdd.negate();
						cLit2.add(toAdd);
					}
					ConnectGraphLabels.put(set2, cLit2);
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
	 * @throws ParseException 
	 */
	private HashSet<PLiteral> searchCommonLiterals(TheoryAgent a1, TheoryAgent a2) throws ParseException {
		Collection<PLiteral> listeC1 = new ArrayList<PLiteral>();
		Collection<PLiteral> listeC2 = new ArrayList<PLiteral>();
		
		if (polarizedCommLang){
			listeC1 = IndepPField.toPLiterals((List<Literal>) a1.getVocabulary(a1.getEnv()));
			listeC2 = IndepPField.toPLiterals((List<Literal>) a2.getNegatedVocabulary(a2.getEnv()));
		}
		else {
			listeC1 = IndepPField.toPLiterals((List<Literal>) a1.getFullVocabulary(a1.getEnv()));
			listeC2 = IndepPField.toPLiterals((List<Literal>) a2.getFullVocabulary(a2.getEnv()));
		}
		
		
		listeC1.retainAll(listeC2);
		HashSet<PLiteral> hs = new HashSet<PLiteral>();
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
	
	private List<Pair<Integer,Integer>> cycleToEdge(List<Integer> cycle){
		List<Pair<Integer,Integer>> result=new ArrayList<Pair<Integer,Integer>>();
		for (int i=1;i<cycle.size();i++){
			Pair<Integer,Integer> edge=new Pair<Integer,Integer>(cycle.get(i-1),cycle.get(i));
			result.add(edge);
		}
		Pair<Integer,Integer> lastEdge=new Pair<Integer,Integer>(cycle.get(cycle.size()-1),cycle.get(0));
		result.add(lastEdge);
		
		return result;
	}

	//note: suppose each edge of the target graph corresponds two arcs (communicational links are bidirectional)
	public void fitToTargetGraph(LinkingGraph graph){
		
	}
	
	
	public void makeStarFormation(int root){
		List<Integer> candPath= new ArrayList<Integer>();
		List<List<Integer>> altPaths=new ArrayList<List<Integer>>();
		for (int i=0;i<nbAgents;i++){
			if (i!=root) for (int j=i+1;j<nbAgents;j++){
				if (j!=root){
					//check edge existence
					Pair<Integer,Integer> edgeToRemove=new Pair<Integer,Integer>(i,j);
					Collection<PLiteral> labels = ConnectGraphLabels.get(edgeToRemove);
					if (labels!=null){// || ConnectGraph.get(i).contains(j) || ConnectGraph.get(j).contains(i)){
						//build path through root
						altPaths.clear();
						candPath.clear();
						candPath.add(i);candPath.add(root);candPath.add(j);
						altPaths.add(candPath);
						//remove edge through this path
						removeEdge(edgeToRemove, altPaths);	
					}
				}
			}
		}
	}
	
	public void makeCircuitFormation(List<Integer> agentOrder){
		Collection<Pair<Integer,Integer>> originalEdges=new ArrayList<Pair<Integer,Integer>>();
		originalEdges.addAll(ConnectGraphLabels.keySet());
		List<Pair<Integer,Integer>> keptEdges=cycleToEdge(agentOrder);
		
		for (Pair<Integer, Integer> edge:originalEdges){
			if (!keptEdges.contains(edge) && !keptEdges.contains(edge.switched()) && ConnectGraphLabels.keySet().contains(edge)){
				List<List<Integer>> altPaths=getForwardBackwardPath(edge.getLeft(),edge.getRight(),agentOrder);
				removeEdge(edge,altPaths);
			}
		}
	}
	
	
	public List<List<Integer>> getForwardBackwardPath(int start, int end, List<Integer> order){
		//forward
		List<Integer> forward=new ArrayList<Integer>();
		int i=0;
		while (order.get(i)!=start) i++;
		int indStart=i;
		forward.add(order.get(i));
		while (order.get(i)!=end){
			i++;
			if (i>=order.size()) i=0;
			forward.add(order.get(i));
		}		
		//backward
		List<Integer> backward=new ArrayList<Integer>();
		i=indStart;
		backward.add(order.get(i));
		while (order.get(i)!=end){
			i--;
			if (i<0) i=order.size()-1;
			backward.add(order.get(i));
		}
		//result
		List<List<Integer>> res=new ArrayList<List<Integer>>();
		res.add(forward);res.add(backward);
		return res;
	}
	
	
	
	public List<List<Integer>> findPaths(int start, int end, int maxNumber){
		List<Integer> curMinPath=null;
		List<List<Integer>> res=new ArrayList<List<Integer>>();
		
		curMinPath=searchMinimalPath(curMinPath,start,end);
		while (res.size()<maxNumber && curMinPath!=null){
			res.add(curMinPath);
			curMinPath=searchMinimalPath(curMinPath,start,end);
		}
		
		return res;
	}
	
	/**
	 * Returns the shortest cycle in the graph g.
	 * @param cycleBase
	 * @param previousMin since removing edge cannot reduce the length of a cycle, the search can stop if we found a cycle of the same length than than the minimal length of previous step
	 * @return Vector<Integer>
	 */
	private List<Integer> searchMinimalPath(List<Integer> prevMinPath, int origin, int target) {
		List<Integer> minPath=prevMinPath;
		int startingIndex;
		if (prevMinPath==null){
			// initialize at size 2
			minPath=new ArrayList<Integer>();
			minPath.add(origin);
			minPath.add(-1);
			startingIndex=1;
		}
		else {
			startingIndex=prevMinPath.size()-1;
		}
		int currentInd=setNextPathValue(minPath,startingIndex, target);
		while (currentInd>=0 && minPath.size()<=nbAgents){
			currentInd=setNextPathValue(minPath,currentInd, target);
			// avoid already found paths
			if (currentInd<minPath.size() && minPath.get(currentInd)==target){
				currentInd--; //backtrack - won't happen with currentInd=0 unless target=origin
			}				
		}
		if (minPath.size()>nbAgents)
			return null;
		return minPath;
	}
	
	
	private int setNextPathValue(List<Integer> path, int indexOfValueToIncrement, int target){
		if (verbose) System.out.println(path.subList(0, indexOfValueToIncrement));
		if (indexOfValueToIncrement>=path.size()){
			//check if  we have a path to correct destination 
			if (path.get(path.size()-1)==target)
					return -1; // cycle found
			else //backtrack
				return indexOfValueToIncrement-1;
		}
		else if (indexOfValueToIncrement>0){  
			//in this case, just seek the next valid value for current increment level (not using nodes in the path), or 
			//if current level has exhausted possible values, backtrack to previous increment level  
			return setNextValueRegularCase(path, indexOfValueToIncrement, true);
		}
		else { //indexofValueToIncrement == 0  // make it subfunciton 2
			//in this case, either try the next number for starting value, or
			// if fixed start or no more values to try, increase size and restart (from 0 or fixed starting value)
			// last argument to true means that for path, we only look for path starting with the initial value
			return setNextValueLevel0(path, indexOfValueToIncrement, true);
		}
	}

	private int setNextCycleValue(List<Integer> path, int indexOfValueToIncrement){
		if (verbose) System.out.println(path.subList(0, indexOfValueToIncrement));
		if (indexOfValueToIncrement>=path.size()){
			//check if  we have a cycle  
			List<Integer> neighbours=getVoisins(path.get(path.size()-1));
			if (neighbours.contains(path.get(0)))
					return -1; // cycle found
			else //backtrack
				return indexOfValueToIncrement-1;
		}
		else if (indexOfValueToIncrement>0){  
			//in this case, just seek the next valid value for current increment level (not using nodes in the path) and go to next inc level, or 
			//if current level has exhausted possible values, backtrack to previous increment level  
			return setNextValueRegularCase(path, indexOfValueToIncrement, false);
		}
		else { //indexofValueToIncrement == 0  
			//in this case, either try the next number for starting value, or
			// if fixed start or no more values to try, increase size and restart (from 0 or fixed starting value)
			// last argument to false means that for path, we try all possible starting point
			return setNextValueLevel0(path, indexOfValueToIncrement, false);
		}
	}

	
	private int setNextValueRegularCase(List<Integer> path, int indexOfValueToIncrement, boolean fixedStart){
		int previousValue=path.get(indexOfValueToIncrement);
		int prevAgent=path.get(indexOfValueToIncrement-1);
		List<Integer> neighbours=getVoisins(prevAgent);
		int i=0;
		while(i<neighbours.size() && 
				(neighbours.get(i)<=previousValue || 
						path.subList(0,indexOfValueToIncrement).contains(neighbours.get(i)) ) )  
			i++;
		if (i<neighbours.size()){
			// set this value and go to next index
			int defVal=path.get(0);
			if (fixedStart) defVal=-1;
			path.set(indexOfValueToIncrement, neighbours.get(i));
			for (int j=indexOfValueToIncrement+1;j<path.size();j++)
				path.set(j, defVal);
			indexOfValueToIncrement++;
			return indexOfValueToIncrement;
		}
		else //backtrack
			return indexOfValueToIncrement-1;
	}
	private int setNextValueLevel0(List<Integer> path, int indexOfValueToIncrement, boolean fixedStart){
		int previousValue=path.get(indexOfValueToIncrement);
		if (!fixedStart && previousValue<nbAgents-1){
			// set this value and go to next index
			path.set(0, previousValue+1);
			for (int j=indexOfValueToIncrement+1;j<path.size();j++)
				path.set(j, path.get(0));
			indexOfValueToIncrement++;
			return indexOfValueToIncrement; //ie 1				
		}
		else { // increase size and reset
			if (fixedStart){
				path.set(0, previousValue);
				for (int j=1;j<path.size();j++)
					path.set(j, -1);
			}
			else {
				for (int j=0;j<path.size();j++)
					path.set(j, 0);
			}
			path.add(0);
			return 1;
		}
	}
	
	private List<Pair<Integer,Integer>> pathToEdge(List<Integer> path){
		List<Pair<Integer,Integer>> result=new ArrayList<Pair<Integer,Integer>>();
		for (int i=1;i<path.size();i++){
			Pair<Integer,Integer> edge=new Pair<Integer,Integer>(path.get(i-1),path.get(i));
			result.add(edge);
		}
		return result;
	}

	
	//note : remove an "edge" (that is, 2 arcs) by using given alternate path
	public void removeEdge(Pair<Integer,Integer> edge, List<List<Integer>> alternatePaths){
		//Initialisations
		List<List<Pair<Integer,Integer>>> paths=new ArrayList<List<Pair<Integer,Integer>>>();
		for (List<Integer> altPath:alternatePaths)
				paths.add(pathToEdge(altPath));
		Pair<Integer,Integer> rev=edge.switched();
		Collection <PLiteral> addLabel=new ArrayList<PLiteral>();
		addLabel.addAll(ConnectGraphLabels.get(edge));
		// add each literal of severed edge label to best option among proposed alternative paths
		for (PLiteral lit:addLabel){
			int min=-1;
			List<Pair<Integer,Integer>> minPath=paths.get(0);
			for (List<Pair<Integer,Integer>> path:paths){
				int eval=evaluateAddLitToPath(lit,path);
				if (min<0 || eval<min){
					min=eval;
					minPath=path;
				}
			}
			addLiteralToPath(lit,minPath);
		}		
		//remove from connectGraphLabels
		ConnectGraphLabels.remove(edge);
		ConnectGraphLabels.remove(rev);
		
		//remove from ConnectGraph
		ConnectGraph.get(edge.getLeft()).remove((Integer)edge.getRight());
		ConnectGraph.get(edge.getRight()).remove((Integer)edge.getLeft());
		
		//remove from CSys network
		cSys.getNetwork().offlineDeconnectLink(edge.getLeft(), edge.getRight());
		
	}
	
	private void addLiteralToPath(PLiteral lit, List<Pair<Integer,Integer>> path){
		Collection <PLiteral> tempTarget=new ArrayList<PLiteral>();
		for (Pair<Integer,Integer> e:path){
			//avoiding redundancy (counting on symmetry of labels not to do it twice)
			tempTarget.clear();
			tempTarget.addAll(ConnectGraphLabels.get(e));
			if (!tempTarget.contains(lit)){
				ConnectGraphLabels.get(e).add(lit); //Hashset, so no redundancies created ??
				PLiteral toadd = new PLiteral(lit);
				toadd.negate();
				ConnectGraphLabels.get(e.switched()).add(toadd);
			}
		}		
	}
	
	private int evaluateAddLitToPath(PLiteral lit, List<Pair<Integer,Integer>> path){
		int n=0;
		Collection <PLiteral> tempTarget=new ArrayList<PLiteral>();
		for (Pair<Integer,Integer> e:path){
			//avoiding redundancy (counting on symmetry of labels not to do it twice)
			tempTarget.clear();
			tempTarget.addAll(ConnectGraphLabels.get(e));
			if (!tempTarget.contains(lit))
				n++;
		}
		return n;
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

	public ConsFindingAgent getAgent(int i){
		return agents.get(i);
	}

	public static boolean polarizedCommLang=true;
	public static boolean verbose=false;
}

