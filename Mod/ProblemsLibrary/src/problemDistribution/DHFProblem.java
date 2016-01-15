package problemDistribution;

import genLib.io.LoaderTool;
import genLib.io.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logicLanguage.IndepClause;

import solarInterface.IndepPField;
import solarInterface.SolProblem;
import genLib.tools.Arguments;


public class DHFProblem implements Parser{

	
	
	
	//load .dhf
	public void load(String filename) throws Exception{
		LoaderTool.load(filename, ".dhf", this);
		shareHypField();
	 }
	
	public void parse(BufferedReader bIn) throws IOException{
		String line=LoaderTool.getNextLine(bIn, '%');
		//TODO : le Header TPTP ?
		while (line!=null){
			if (line.startsWith("agent"))
				parseAgent(line);
			if (line.startsWith("cnf"))
				parseCnf(line);
			if (line.startsWith("pf"))
				hypField=IndepPField.parse(line);
			line=LoaderTool.getNextLine(bIn, '%');
		}
	}

	private void parseAgent(String line){
		String ag=line.substring("agent(".length(),line.lastIndexOf(")."));
		if (!agents.contains(ag)){
			agents.add(ag);
			localProblems.add(new HypothesisFormationPb());
		}			
		
		
	}
	private void parseCnf(String line) throws IOException{
		String agent, name, role, formula;
		//extract arguments
		String temp=line.substring(line.indexOf("(")+1, line.lastIndexOf(")."));
		Arguments arg=Arguments.parse("["+temp+"]");
		//attribute arguments to correct string
		name=arg.get(0).trim();
		role=arg.get(1).trim();
		formula=arg.get(2).trim();
		agent=arg.get(3).trim();			
		
		IndepClause clause=new IndepClause(name,formula);
		HypothesisFormationPb pb=getProblem(agent);
	//	if (pb==null) throw new IOException("Declare an agent before using it - agent "+agent+"in cnf "+line);
		if (role.equals("top_clause") || role.equals("manifestation"))
			pb.addManifestation(clause);
		else
			pb.addClauseToLocTheory(clause);
	}

	
	private void shareHypField(){
		for (HypothesisFormationPb pb:localProblems){
			pb.setHypothesisField(hypField);
		}
	}
	
	public int getIndex(String ag){
		int ind=agents.indexOf(ag);
		return (ind);
	}
	public List<String> getAgents(){
		return agents;
	}
	public HypothesisFormationPb getProblem(String ag){
		if (getIndex(ag)>=0)
			return localProblems.get(getIndex(ag));
		return null;
	}
	
	public HypothesisFormationPb getProblem(int index){
		if (index>0 && index<localProblems.size())
			return localProblems.get(index);
		return null;
	}
	
	public int getNbAgents() {
		return agents.size();
	}

	
	public List<HypothesisFormationPb> localProblems=new ArrayList<HypothesisFormationPb>();
	public List<String> agents=new ArrayList<String>();
	public IndepPField hypField;
	
}
