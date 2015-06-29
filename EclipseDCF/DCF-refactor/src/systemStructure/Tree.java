
package systemStructure;



import genLib.io.LoaderTool;
import genLib.io.Parser;
import genLib.io.Saver;
import genLib.tools.Arguments;
import genLib.tools.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

import logicLanguage.IndepLiteral;
import problemDistribution.DCFProblem;
import problemDistribution.DistributedConsequenceFindingProblem;
import problemDistribution.XMLDistributedProblem;
import solarInterface.IndepPField;
import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;
import agLib.agentCommunicationSystem.BasicAgent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agents.ConsFindingAgent;
import agents.PBAgent;
import cnfPb.SolProblemWithHeader;

import communication.protocol.LocalPBProtocol;



/**

 * @author Viel Charlotte, Gauvain Bourgne

 *

 */

public class Tree extends PartitionGraph implements Parser, Saver{

	int root;

	


	/**
	 * Create the tree with the files and for the multi-agent system ma.
	 */

	public Tree(DistributedConsequenceFindingProblem<SolProblem> data, String grapheName,
			CommunicationModule cSys, CanalComm sys, int typeConsFinding, long deadline) {

		super(data, grapheName, cSys, sys, typeConsFinding, deadline);
		long start=System.currentTimeMillis();
		if (useVariantCycleCut)
			cycleCutBis();
		else
			cycleCut();
		long middle=System.currentTimeMillis();
		System.out.println("Time for cutting cycle: "+ (double)(middle-start)/1000+"s");
		//setAdditionalLit(data.getGbPField());
		initAgents(sys, data.getGbPField());
		long end=System.currentTimeMillis();
		System.out.println("Time for initializating agents and root (A): "+ (double)(end-middle)/1000+"s");
		
	}

	/**
	 * Create the tree with the files and for the multi-agent system ma.
	 */
	public Tree(DistributedConsequenceFindingProblem<SolProblem> data, String grapheName, int root,
			CommunicationModule cSys, CanalComm sys, int typeConsFinding, long deadline) {

		super(data, grapheName, cSys, sys, typeConsFinding, deadline);
		long start=System.currentTimeMillis();
		if (useVariantCycleCut)
			cycleCutBis();
		else
			cycleCut();
		long middle=System.currentTimeMillis();
		System.out.println("Time for cutting cycle: "+ (double)(middle-start)/1000+"s");
		//setAdditionalLit(data.getGbPField());
		this.root=root;
		initAgents(sys, data.getGbPField());
		long end=System.currentTimeMillis();
		System.out.println("Time for initializating agents and root: (B)"+ (double)(end-middle)/1000+"s");
		
	}

	/**
	 * Create the tree with the files and for the multi-agent system ma.
	 */
	public Tree(DistributedConsequenceFindingProblem<SolProblem> data, String grapheName, String rootHeuristic,
			CommunicationModule cSys, CanalComm sys, int typeConsFinding, long deadline) {

		super(data, grapheName, cSys, sys, typeConsFinding, deadline);
		long start=System.currentTimeMillis();
		if (useVariantCycleCut)
			cycleCutBis();
		else
			cycleCut();
		long middle=System.currentTimeMillis();
		System.out.println("Time for cutting cycle: "+ (double)(middle-start)/1000+"s");
		root=chooseRoot(rootHeuristicCode(rootHeuristic));
		//setAdditionalLit(data.getGbPField());
		initAgents(sys, data.getGbPField());
		long end=System.currentTimeMillis();
		System.out.println("Time for initializating agents and root: (C)"+ (double)(end-middle)/1000+"s");
	}

	

	/**
	 * Create the tree with the files and for the multi-agent system ma.
	 */

	public static Tree TreeFromXml(String filenameNoExt, String grapheName,
			CommunicationModule cSys, CanalComm sys, int typeConsFinding, long deadline) {

		boolean withIndivLanguages = false;

		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new XMLDistributedProblem(filenameNoExt, grapheName, withIndivLanguages);

		return new Tree(data, grapheName, cSys, sys, typeConsFinding, deadline);
	}



	/**
	 * Create the tree with the files and for the multi-agent system ma.
	 * @throws Exception 
	 */

	public static Tree TreeFromDcf(String filenameNoExt, String grapheName, int root,
			CommunicationModule cSys, CanalComm sys, int typeConsFinding, long deadline) throws Exception {

		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new DCFProblem(filenameNoExt);
		
		return new Tree(data, grapheName, cSys, sys, typeConsFinding, deadline);
	}


	

	/**
	 * Create the tree with the files and for the multi-agent system ma.
	 * @throws Exception 
	 */

	public static Tree TreeFromDcf(String filenameNoExt, String grapheName, int root,
			CommunicationModule cSys, CanalComm sys, int typeConsFinding, int maxLength, long deadline) throws Exception {
		
		DistributedConsequenceFindingProblem<SolProblem> data;
		data = new DCFProblem(filenameNoExt);
		data.setMaxLength(maxLength);

		return new Tree(data, grapheName, root, cSys, sys, typeConsFinding, deadline);
	}


	public int getRoot() {
		return root;
	}

	/**
	 * Algorithm Cycle Cut.
	 * Transforms the graph in a tree.
	 * 
	 * @return Graph
	 */
	@SuppressWarnings("unchecked")
	public void cycleCut() {
		List<Integer> shortestCycle;
		HashSet<IndepLiteral> edgeLabel;
		Pair<Integer,Integer> minEdge;
		
		System.out.println("Begin Cyclecut");
		int minSize = 3;
		List<List<Integer>> cycles = searchCycles(minSize);
		
		
		while((cycles.size() > 0)) {
			shortestCycle = getShortestCycle(cycles,minSize);
			minSize= shortestCycle.size();
			minEdge = getMinimalLabelEdge(shortestCycle);			
			removeEdgeFromCycle(minEdge, shortestCycle);
			cycles = searchCycles(minSize);
		}
		
	}
	
	/**
	 * Algorithm Cycle Cut.
	 * Transforms the graph in a tree.
	 * 
	 * @return Graph
	 */
	@SuppressWarnings("unchecked")
	public void cycleCutBis() {
		List<Integer> shortestCycle=null;
		Pair<Integer,Integer> minEdge=new Pair<Integer,Integer>(0,0);
		
		System.out.println("Begin Cyclecut");
		int nbEdges=ConnectGraphLabels.entrySet().size();
		
		while((nbEdges>=nbAgents*2)) { // edges being undirected are counted two times in label set (to account for polarity)
			shortestCycle = this.searchMinimalCycle(shortestCycle, minEdge.getLeft());
			minEdge = getMinimalLabelEdge(shortestCycle);			
			removeEdgeFromCycle(minEdge, shortestCycle);
			nbEdges=ConnectGraphLabels.entrySet().size();
			if (verbose) System.out.println("Remaining Edges : "+nbEdges/2);
		}
		
	}

	
	
	/**
	 * Returns the shortest cycle in the graph g.
	 * @param cycleBase
	 * @param previousMin since removing edge cannot reduce the length of a cycle, the search can stop if we found a cycle of the same length than than the minimal length of previous step
	 * @return Vector<Integer>
	 */
	private List<Integer> getShortestCycle(List<List<Integer>> cycleBase, int previousMin) {
		
		List<Integer> minCycle=cycleBase.get(0);
		for (int i=0;i<cycleBase.size();i++){
			if (minCycle.size()<=previousMin) return minCycle;
			List<Integer> cycle=cycleBase.get(i);
			if (cycle.size()<minCycle.size())
				minCycle=cycle;			
			for (int j=i+1;j<cycleBase.size();j++){
				List<Integer> combi=combineCycleMin(cycle,cycleBase.get(j),minCycle.size());
				if (combi!=null)
					minCycle=combi;
			}				
		}
		return minCycle;
	}

	/**
	 * Returns the shortest cycle in the graph g.
	 * @param cycleBase
	 * @param previousMin since removing edge cannot reduce the length of a cycle, the search can stop if we found a cycle of the same length than than the minimal length of previous step
	 * @return Vector<Integer>
	 */
	private List<Integer> searchMinimalCycle(List<Integer> prevMinCycle, int origineLastEdgeCut) {
		List<Integer> minCycle=prevMinCycle;
		int startingIndex;
		if (prevMinCycle==null){
			// initialize at size 3
			minCycle=new ArrayList<Integer>();
			minCycle.add(0);
			minCycle.add(0);
			minCycle.add(0);
			startingIndex=1;
		}
		else {
			startingIndex=prevMinCycle.indexOf(origineLastEdgeCut)+1;
		}
		int currentInd=setNextValue(minCycle,startingIndex);
		while (currentInd>=0)
			currentInd=setNextValue(minCycle,currentInd);
		return minCycle;
	}

	private int setNextValue(List<Integer> cycle, int indexOfValueToIncrement){
		if (verbose) System.out.println(cycle.subList(0, indexOfValueToIncrement));
		if (indexOfValueToIncrement>=cycle.size()){
			//check if path can close with first element 
			List<Integer> neighbours=getVoisins(cycle.get(cycle.size()-1));
			if (neighbours.contains(cycle.get(0)))
					return -1; // cycle found
			else //backtrack
				return indexOfValueToIncrement-1;
		}
		else if (indexOfValueToIncrement>0){
			int previousValue=cycle.get(indexOfValueToIncrement);
			int prevAgent=cycle.get(indexOfValueToIncrement-1);
			List<Integer> neighbours=getVoisins(prevAgent);
			int i=0;
			while(i<neighbours.size() && 
					(neighbours.get(i)<=previousValue || 
							cycle.subList(0,indexOfValueToIncrement).contains(neighbours.get(i)) ) )  
				i++;
			if (i<neighbours.size()){
				// set this value and go to next index
				cycle.set(indexOfValueToIncrement, neighbours.get(i));
				for (int j=indexOfValueToIncrement+1;j<cycle.size();j++)
					cycle.set(j, cycle.get(0));
				indexOfValueToIncrement++;
				return indexOfValueToIncrement;
			}
			else //backtrack
				return indexOfValueToIncrement-1;
		}
		else { //indexofValueToIncrement == 0
			int previousValue=cycle.get(indexOfValueToIncrement);
			if (previousValue<nbAgents-1){
				// set this value and go to next index
				cycle.set(0, previousValue+1);
				for (int j=indexOfValueToIncrement+1;j<cycle.size();j++)
					cycle.set(j, cycle.get(0));
				indexOfValueToIncrement++;
				return indexOfValueToIncrement;				
			}
			else { // increase size and reset
				for (int j=0;j<cycle.size();j++)
					cycle.set(j, 0);
				cycle.add(0);
				return 1;
			}
		}
	}
	
	
	// return the combination or two cycle if it is smaller than maxSize, or null otherwise
	// combination is done by exclusive sum of the edges
	private List<Integer> combineCycleMin(List<Integer> cycle1, List<Integer> cycle2, int maxSize){
		List<Pair<Integer,Integer>> edges1=cycleToEdge(cycle1);
		List<Pair<Integer,Integer>> edges2=cycleToEdge(cycle2);
		List<Pair<Integer,Integer>> common=new ArrayList<Pair<Integer,Integer>>();
		common.addAll(edges1);
		common.retainAll(edges2);
		if (edges1.size()+edges2.size()-2*common.size()<maxSize){
			List<Pair<Integer,Integer>> combi=new ArrayList<Pair<Integer,Integer>>();
			combi.addAll(edges1);
			combi.addAll(edges2);
			combi.removeAll(common);
			return edgesToCycle(combi,true);
		}
		return null;
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
	
	private List<Integer> edgesToCycle(List<Pair<Integer,Integer>> edges){
		List<Pair<Integer,Integer>> toCheck=new ArrayList<Pair<Integer,Integer>>();
		toCheck.addAll(edges);
		List<Integer> result=new ArrayList<Integer>();
		Pair<Integer,Integer> current;
		int next;
		int i=0;
		while (toCheck.size()>i) {
			current=toCheck.remove(i);
			next=current.getRight();
			result.add(current.getLeft());
			i=0;
			while (i<toCheck.size() && toCheck.get(i).getLeft()!=next)
				i++;
		}
		if (!toCheck.isEmpty())
			System.err.println(edges+" IS NOT A CYCLE !!!");
				
		return result;
	}

	private List<Integer> edgesToCycle(List<Pair<Integer,Integer>> edges, boolean allowTurn){
		List<Pair<Integer,Integer>> toCheck=new ArrayList<Pair<Integer,Integer>>();
		toCheck.addAll(edges);
		List<Integer> result=new ArrayList<Integer>();
		Pair<Integer,Integer> current;
		int next;
		int i=0;
		while (toCheck.size()>0) {
			current=toCheck.remove(i);
			next=current.getRight();
			result.add(current.getLeft());
			i=0;
			boolean turned=false;
			while (i<toCheck.size() && toCheck.get(i).getLeft()!=next){
				i++;
				if (allowTurn && !turned && i>=toCheck.size()){
					for (int k=0;k<toCheck.size();k++)
						toCheck.set(k, toCheck.get(k).switched());
					turned=true;
					i=0;
				}
			}
		}
		if (!toCheck.isEmpty())
			System.err.println(edges+" IS NOT A CYCLE !!!");
								
		return result;
	}

	
	/**
	 * Returns the shortest label in the cycle.
	 * 
	 * @param shortestCycle
	 * @return tab with HashSet<IndepLiteral> edgeLabel, int edgeFirstElt, int edge2ndElt 
	 */
	private Pair<Integer,Integer> getMinimalLabelEdge(List<Integer> shortestCycle) {
		List<Pair<Integer,Integer>> cycle=cycleToEdge(shortestCycle);
		Pair<Integer,Integer> minEdge=cycle.get(0);
		int pathSize=cycle.size()-1;
		int minSize=pathSize*ConnectGraphLabels.get(minEdge).size();
		Collection<IndepLiteral> label=new ArrayList<IndepLiteral>();
		Collection<IndepLiteral> tempLabel=new ArrayList<IndepLiteral> ();
		int size;
		for (Pair<Integer,Integer> edge:cycle){
			label.clear();
			label.addAll(ConnectGraphLabels.get(edge.switched())); //label is the one in the other direction
			// initialize as if label was added to all remaining edges
			size=pathSize*label.size();
			// substract factorized labels
			if (size>0)
				for (Pair<Integer,Integer> edge2:cycle)
					if (edge2!=edge){
						tempLabel.clear();
						tempLabel.addAll(ConnectGraphLabels.get(edge2));
						tempLabel.retainAll(label);
						size=size-tempLabel.size();
			}
			if (size<minSize){
				minEdge=edge;
				minSize=size;
			}
		}	
		return minEdge;
	}
	

	/**
	 * Remove the edge between ag1 and ag2 and add its label to all the other edges
	 * of the cycle shortestCycle.
	 * 
	 * @param edge
	 * @param shortestCycle
	 */
	private void removeEdgeFromCycle(Pair<Integer,Integer> edge, List<Integer> shortestCycle) {
		List<Pair<Integer,Integer>> cycle=cycleToEdge(shortestCycle);
		Pair<Integer,Integer> rev=edge.switched();
		//Collection <IndepLiteral> addLabel=ConnectGraphLabels.get(edge);
		Collection <IndepLiteral> addLabelRev=new ArrayList<IndepLiteral>();
		Collection <IndepLiteral> tempTarget=new ArrayList<IndepLiteral>();
		addLabelRev.addAll(ConnectGraphLabels.get(rev));
		for (Pair<Integer,Integer> e:cycle)
			if (e!=edge) {
				//avoiding redundancy (counting on symmetry of labels not to do it twice)
				for (IndepLiteral lit:addLabelRev){
					tempTarget.clear();
					tempTarget.addAll(ConnectGraphLabels.get(e));
					if (!tempTarget.contains(lit)){
						ConnectGraphLabels.get(e).add(lit); //Hashset, so no redundancies created ??
						ConnectGraphLabels.get(e.switched()).add(lit.negate(false));
					}
				}
			}
				
		//remove from connectGraphLabels
		ConnectGraphLabels.remove(edge);
		ConnectGraphLabels.remove(rev);
		if (verbose)
			System.out.println("removed edge : (" + edge+ ")");
		
		//remove from ConnectGraph
		ConnectGraph.get(edge.getLeft()).remove((Integer)edge.getRight());
		ConnectGraph.get(edge.getRight()).remove((Integer)edge.getLeft());
	}
	
	/**
	 * Returns if the node is a leaf.
	 * 
	 * @param node
	 * @return boolean
	 */
	private boolean isLeaf(int node) {
		if(ConnectGraph.get(node).size() == 1 && node != root) return true;
		if(ConnectGraph.get(node).size() == 0 && node == root) return true;
		return false;
	}

	/**
	 * Returns all the leaves of the graph.
	 * 
	 * @return ArrayList<CanalComm>
	 */
	public List<CanalComm> getLeaves() {
		List<CanalComm> leaves = new ArrayList<CanalComm>();
		for (int i = 0; i < agents.size(); i++) {
			if(isLeaf(i)) {
				leaves.add(agents.get(i).getComm());
			}
		}
		return leaves;
	}
	
	/**
	 * Initialize the initial knowledge of the agents including the parent of each
	 * node and if the node is the root. 
	 */
	protected void initAgents(CanalComm syst, IndepPField pField) {
		
		setRootAndLanguage();
		
		for (int ag=0;ag<agents.size();ag++){
			agents.get(ag).setCommSyst(syst);
			if (typeCF!=LocalPBProtocol.COOPERARTIVE_CF_AGENT)
				((PBAgent)agents.get(ag)).setOriginalPField(pField);
		}
	}
	
	protected void setRootAndLanguage() {
		int parent;
		ConsFindingAgent fils;
		Stack<Integer> pile = new Stack<Integer>();
		
		System.out.println("Chosen Root : "+root);
		for (int i=0;i<agents.size();i++){
			agents.get(i).setRoot(i==root);
		}
		pile.push(root);
		while(!pile.isEmpty()) {
			parent = pile.pop();
			for (int index : ConnectGraph.get(parent)) {
				if(index != agents.get(parent).getParentIndex()||agents.get(parent).isRoot()) {
					fils = agents.get(index);
					fils.setParentIndex(parent);
					fils.setParent(agents.get(parent).getComm());
					
					pile.push(index);
					setSonLanguage(fils, index, parent);
				}
			}
		}
	}
	
	

	private void setSonLanguage(ConsFindingAgent fils, int filsInd, int parent){
		Pair<Integer,Integer> set;
		List<IndepLiteral> language=new ArrayList<IndepLiteral>();

		language = new ArrayList<IndepLiteral>();
		set = new Pair<Integer,Integer>(filsInd, parent);
		
		language.addAll(ConnectGraphLabels.get(set));
		
		// TODO: Check first attribution should not be useful. check if it can be removed
		if (typeCF!=LocalPBProtocol.COOPERARTIVE_CF_AGENT)
			((PBAgent)agents.get(parent)).getCommLanguage().addData(fils.getComm(), language);
		if (typeCF!=LocalPBProtocol.COOPERARTIVE_CF_AGENT)
			((PBAgent)agents.get(filsInd)).getCommLanguage().addData(agents.get(parent).getComm(), language);
		
	}

/*	private void setAdditionalLit(IndepPField pField) {
		int ag;
		HashSet<Integer> set;
		List<IndepLiteral> arg = pField.getLiterals();
		for (PBAgent agent : agents) {
			ag = identifier(agent);
			
			for (int neighbor : ConnectGraph.get(ag)) {
				set = new HashSet<Integer>();
				set.add(ag);
				set.add(neighbor);
				ConnectGraphLabels.get(set).addAll(arg);
			}
		}
	}
*/	

	public String toString() {
		String s = "\n";
		for (ConsFindingAgent agent : agents) {
			s = s + "Theory of " + agent.name + " : " + 
				agent.getAgentTheory().getTheory(true).toString() + "\n";
		}
		s = s + ConnectGraphLabels.toString() + "\n";
		
		return s;
	}

	
	
	
	// ROOT HEURISTICS
	
	public int chooseRoot(int rootHeuristic){
		switch(rootHeuristic){
		case RCH_MAXCLSIZE: return chooseMaxAvgClSizeRoot();
		
		}
		
		return 0;
	}
	
	public int chooseMaxAvgClSizeRoot(){
		double maxAvgClSize=0;
		int res=0;
		
		for (int i=0;i<agents.size();i++){
			ConsFindingAgent ag=agents.get(i);
			SolProblemWithHeader pb=new SolProblemWithHeader(ag.getAgentTheory().getTheory(true));
			double size=pb.getAvgClSize();
			if (size>maxAvgClSize) {
				maxAvgClSize=size;
				res=i;
			}
		}
		
		return res;
	}
	
	public int chooseMaxPfieldPropRoot(){
		double max=0;
		int res=0;
		
		for (int i=0;i<agents.size();i++){
			ConsFindingAgent ag=agents.get(i);
			SolProblemWithHeader pb=new SolProblemWithHeader(ag.getAgentTheory().getTheory(true));
			double prop=pb.getPFieldProp();
			if (prop>max) {
				max=prop;
				res=i;
			}
		}
		
		return res;
	}
	

	public int rootHeuristicCode(String rootCommand){
		if (rootCommand.equalsIgnoreCase("MaxClSize"))
			return RCH_MAXCLSIZE;
		if (rootCommand.equalsIgnoreCase("MaxPF"))
			return RCH_MAXPFPROP;
		return 0;
	}
	
	public static final int RCH_MAXCLSIZE=0;
	public static final int RCH_MAXPFPROP=1;
	
	public static boolean verbose=true;




	public void load(String filename) throws Exception{
		LoaderTool.load(filename, ".tree", this);
	 }

	public void save(String filename, boolean replace) throws Exception{
		LoaderTool.save(filename, ".tree", this, replace);
	}
	
	
	/* (non-Javadoc)
	 * @see io.Parser#parse(java.io.BufferedReader)
	 */
	public void parse(BufferedReader bIn) throws IOException {
		String line=LoaderTool.getNextLine(bIn, '%');
		while (line!=null){
			if (line.startsWith("edge("))
				parseEdge(line);
			if (line.startsWith("nbAgents("))
				parseNbAgents(line);
			line=LoaderTool.getNextLine(bIn, '%');
		}
	}

	
	private void parseNbAgents(String line){
		String nbAg=line.substring("nbAgents(".length(),line.lastIndexOf(")."));
		nbAgents=Integer.parseInt(nbAg);
	}
	private void parseEdge(String line){
		String startEdge,endEdge,labels;
		//extract arguments
		String temp=line.substring(line.indexOf("(")+1, line.lastIndexOf(")."));
		Arguments arg=Arguments.parse("["+temp+"]");
		//attribute arguments to correct string
		startEdge=arg.get(0).trim();
		endEdge=arg.get(1).trim();
		labels=arg.get(2).trim();
		
		int e1=Integer.parseInt(startEdge);
		int e2=Integer.parseInt(endEdge);
		Pair<Integer,Integer> edge=new Pair<Integer,Integer>(e1,e2);
		Arguments labelList=Arguments.parse(labels);
		HashSet<IndepLiteral> litSet=new HashSet<IndepLiteral>();
		for (String slit:labelList){
			IndepLiteral lit=IndepLiteral.parse(slit);
			litSet.add(lit);
		}
		ConnectGraphLabels.put(edge, litSet);
		ConnectGraph.get(e1).add(e2);
		
	}

	/* (non-Javadoc)
	 * @see io.Saver#save(java.io.PrintStream)
	 */
	public void save(PrintStream output) {
		output.println("nbAgents("+nbAgents+").");
		for (Entry<Pair<Integer,Integer>,Collection<IndepLiteral>> labeledEdge:ConnectGraphLabels.entrySet()){
			Pair<Integer,Integer> edge=labeledEdge.getKey();
			Collection<IndepLiteral> label=labeledEdge.getValue();
			output.println("edge("+edge.getLeft()+","+edge.getRight()+","+label+").");
		}
	}

	
/*	HashMap<Pair<Integer,Integer>, Collection<IndepLiteral>> ConnectGraphLabels;
	List<List<Integer>> ConnectGraph;
	protected List<ConsFindingAgent> agents;
	public int nbAgents;
	protected int typeCF;
	CommunicationModule cSys;
	public String filenameNoExt;
	public String grapheName; */
	
	public static boolean useVariantCycleCut=true;
	
	
	
}

