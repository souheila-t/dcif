package convert;
import java.io.BufferedReader;
import java.io.IOException;

import logicLanguage.IndepClause;

import solarInterface.IndepPField;
import solarInterface.SolProblem;
import tools.Arguments;

import io.LoaderTool;
import io.Parser;


public class BioNetFileParser implements Parser {

	
	
	public void parse(BufferedReader bIn) throws IOException{
		String line=LoaderTool.getNextLine(bIn, '%');
		//TODO : le Header TPTP ?
		int i=0;
		while (line!=null){
			if (line.startsWith("source"))
				parseSource(line);
			if (line.startsWith("goal"))
				parseGoal(line);
			if (line.startsWith("R"))
				parseReaction(line);
			line=LoaderTool.getNextLine(bIn, '%');
			i++;
			if (i%10==0)
				System.out.print(".");
			if (i%100==0)
				System.out.println();
		}
		
	}

	private void parseSource(String line){
		String source=line.substring(line.indexOf(",")+1,line.length()).replace('-', '_');
		Arguments sources=parseCompound(source);
		for (String m:sources)
			this.solFileLines.add("cnf(source"+m+", TOP_CLAUSE, ["+m+"]).");
		//Parse source compound		
		//Add source formulas
					
	}
	
	
	private void parseGoal(String line){
		//TODO
		String goal=line.substring(line.indexOf(",")+1,line.length()).replace('-', '_');
		Arguments goals=parseCompound(goal);
		String pf="pf("+goals.toString()+"<=1).";
		this.solFileLines.add(pf);
					
	}
	
	
	private void parseReaction(String line) {
		String reaction,reactant,product;//,enzyme;
		Arguments products,reactants;
		//extract arguments
		Arguments arg=Arguments.parse("["+line+"]");
		//attribute arguments to correct string
		reaction="r_"+arg.get(0).trim().replace('-', '_');
		reactant=arg.get(1).trim().replace('-', '_');
		reactants=parseCompound(reactant);
		if (reactants.size()>1) 
			reactant="c_"+reactant.replace(':', '_');
		else 
			reactant="m_"+reactant.replace(':', '_');
		//enzyme="e_"+arg.get(2).trim(); //should be empty string in examples files
		product=arg.get(2).trim().replace('-', '_');			
		products=parseCompound(product);			
		if (products.size()>1) 
			product="c_"+product.replace(':', '_');
		else 
			product="m_"+product.replace(':', '_');
		
		solFileLines.add(generateReactantFormula(reaction,reactant));
		solFileLines.add(generateCombinationFormula(reactant,reactants));
		solFileLines.add(generateProductFormula(reaction,product));
		solFileLines.addAll(generateDecompositionFormula(product,products));
		//generateReactant formula and compound combination
		//generateProduct formaul and compound decomposition
		//IndepClause clause=new IndepClause("name",formula);
		//result.addClause(clause,false); // false : not top clause
	}

	private String generateReactantFormula(String reaction, String reactant){
		String res="cnf(activ"+reaction.substring(2)+", AXIOM, ["+reaction+", -"+reactant+"]).";
		return res;
	}

	private String generateProductFormula(String reaction, String product){
		String res="cnf(prod"+reaction.substring(2)+", AXIOM, ["+product+", -"+reaction+"]).";
		return res;
	}
	
	private String generateCombinationFormula(String compound, Arguments metabolites){
		if (metabolites.size()<=1) 
			return "";
		String res="cnf(combi"+compound.substring(2)+", AXIOM, ["+compound;
		for (String m:metabolites)
			res+=", -"+m;
		res+="]).";
		return res;
	}

	private Arguments generateDecompositionFormula(String compound, Arguments metabolites){
		Arguments res=new Arguments();
		if (metabolites.size()<=1) 
			return res;
		String temp;
		int i=1;
		for (String m:metabolites){
			temp="cnf(dec"+compound.substring(2)+i+", AXIOM, ["+m+", -"+compound+"]).";
			res.add(temp);
			i++;
		}
		return res;
	}

	private Arguments parseCompound(String compound){
		Arguments arg;
		arg=Arguments.parse("["+compound.replace(':',',')+"]");
		Arguments res=new Arguments();
		for (String m:arg)
			res.add("m_"+m);
		return res;
	}
	
	
	public Arguments getSolFileLines() {
		return solFileLines;
	}
	public String toString(){
		String res="";
		for (String s:solFileLines)
			res+=s+"\n";
		return res;
	}

	private Arguments solFileLines=new Arguments();
	//private SolProblem result;
}
