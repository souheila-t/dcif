package cnfPb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;

import genLib.io.LoaderTool;
import genLib.io.Parser;

public class CnfReader implements Parser{

	public CnfReader(String filename) throws Exception{
		super();
		load(filename);
	}
	
	public void parse(BufferedReader input) throws IOException {
		int lineNumber=0;
		String line=LoaderTool.getNextLine(input, 'c');
		while (line!=null){
			parseFileLine(line, lineNumber);
			line=LoaderTool.getNextLine(input, 'c');
			lineNumber++;
		}
		if (!cnfBuffer.isEmpty())
			processBufferedClause();
	}
	
	protected void parseFileLine(String line, int lineNumber){
			if (line.startsWith("p"))
				parsePbLine(line);
			else 
				parseCnf(line);
	}

	protected void parsePbLine(String line){
		//Format : "p FORMAT NB_VAR NB_CLAUSE"
		// FORMAT must be "cnf"
		String fvc=line.substring(2).trim();
		String format=fvc.substring(0,fvc.indexOf(' ')).trim();
		if (!format.equalsIgnoreCase("cnf")) 
			return; // TODO raise Exception ??
		String vc=fvc.substring(fvc.indexOf(' ')+1).trim();
		String nb_var=vc.substring(0,vc.indexOf(' ')).trim();
		String nb_cl=vc.substring(vc.indexOf(' ')).trim();
		nbVar=Integer.parseInt(nb_var);
		nbClauses=Integer.parseInt(nb_cl);
	}

	protected void parseCnf(String line){
		List<Integer> lineInt=parseNumberList(line," ");
		
		for (Integer elt:lineInt){
			if (elt == 0){
				processBufferedClause();
				cnfBuffer.clear();
			}
			else {
				cnfBuffer.add(elt);
			}
		}
	}
	
	protected void processBufferedClause(){
		List<IndepLiteral> clauseElts=new ArrayList<IndepLiteral>();
		for (Integer elt:cnfBuffer){
			String pname="p"+Math.abs(elt);
			IndepLiteral newElt=new IndepLiteral(elt>0,pname,new ArrayList<String>());
			clauseElts.add(newElt);
		}
		String cname="c"+currentClause;
		currentClause++;
		IndepClause newCl=new IndepClause(cname,clauseElts);
		problem.add(newCl);
	}
	
	protected static List<Integer> parseNumberList(String input, String separator){
		List<Integer> result=new ArrayList<Integer> ();
		int elt;
		String head, tail;
		tail=input.trim();
		if (tail.startsWith(separator))
			tail=tail.substring(separator.length());
		int sepInd=tail.indexOf(separator);
		while (sepInd>0){
			head=tail.substring(0,sepInd).trim();
			tail=tail.substring(sepInd+separator.length()).trim();
			sepInd=tail.indexOf(separator);
			elt=Integer.parseInt(head);
			result.add(elt);
		}
		if (tail.length()>0){
			elt=Integer.parseInt(tail);
			result.add(elt);
		}
		return result;
	}
	
	
	public void load(String cnffilename) throws Exception{
		LoaderTool.load(cnffilename, ".cnf", this);
	 }

	public CNF getProblem(){
		return problem;
	}
	
	
	protected int nbVar;
	protected int nbClauses;
	protected CNF problem=new CNF();
	
	protected int currentClause=0;
	protected List<Integer> cnfBuffer=new ArrayList<Integer>();
	
}
