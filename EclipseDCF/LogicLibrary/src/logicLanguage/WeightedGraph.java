package logicLanguage;

import genLib.io.Saver;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import genLib.tools.Pair;

public class WeightedGraph<Node> implements Saver {

	public WeightedGraph(List<List<Pair<Integer, Integer>>> graph, List<Node> nodes, int nbEdges){
		this.graph=graph;
		this.nodes=nodes;
		this.nbEdges=nbEdges;
	}
	
	public String toMetisFormat(){
		String res=""+nodes.size()+" "+nbEdges+" 1 \n";
		for (List<Pair<Integer,Integer>> neighbors:graph){
			for (Pair<Integer,Integer> p:neighbors){
				res+=(p.getLeft()+1)+" "+p.getRight()+" ";
			}
			res+="\n";
		}
		return res;
	}
	
	public void save(PrintStream output) {
		output.print(toMetisFormat());
	}
	
	protected List<List<Pair<Integer, Integer>>> graph=new ArrayList<List<Pair<Integer,Integer>>>();
	protected List<Node> nodes;
	protected int nbEdges;
	
	
}
