package problemDistribution;

import java.io.PrintStream;
import java.util.ArrayList;

import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;

import solarInterface.SolProblem;
import genLib.io.LoaderTool;
import genLib.io.Saver;

public class DCFProblemOrderedCNFSaver extends DCFProblem implements Saver{

	public DCFProblemOrderedCNFSaver(String filenameNoExt) throws Exception{
		super(filenameNoExt);		
	}
	
	
	public void save(String filename, boolean replace) throws Exception{
		LoaderTool.save(filename, ".o.cnf", this, replace);
	}
	
	public void buildSymbolTable(){
		for (SolProblem locPb : localProblems)
			for (IndepLiteral lit:locPb.getAllClauses().getPredicates()){
				if (!symbolTable.contains(lit))
					symbolTable.add(lit);
		}			
	}
	
	public void save(PrintStream p){
		//Initializations
		buildSymbolTable();
		int[] agPbSize= new int[localProblems.size()];
		int nbCl=0;
		for (int ag=0;ag<localProblems.size();ag++){
			agPbSize[ag]=localProblems.get(ag).getNbClauses();
			nbCl+=agPbSize[ag];
		}	
		
		// print cnf first line ("p cnf v c" where c is number of clauses and v number of variables
		p.println("p cnf "+symbolTable.size()+" "+nbCl);
		
		// compute repartition and print it
		String repartLine="c ";
		for (int ag=0;ag<localProblems.size();ag++)
			repartLine+=""+agPbSize[ag]+" ";
		p.println(repartLine);
		p.println();	
		// print translated formula in correct order
		for (int ag=0;ag<localProblems.size();ag++)
			for (int i=0;i<agPbSize[ag];i++)
				p.println(getCnfFormatClause(ag,i));
	}
	
	public String getCnfFormatClause(int agent, int clause){
		SolProblem pb=localProblems.get(agent);
		IndepClause c=pb.getClause(clause);
		String result="";
		for (IndepLiteral lit : c.getLiterals()){
			result+=getCodeLiteral(lit).toString()+" ";
		}
		result+="0";
		
		return result;
	}
	
	public Integer getCodeLiteral(IndepLiteral l){
		Integer c=symbolTable.indexOf(l.getPositiveVersion())+1;
		if (l.isPositive())
			return c;
		else
			return -c;
	}

	public ArrayList<IndepLiteral> symbolTable=new ArrayList<IndepLiteral>();
}

