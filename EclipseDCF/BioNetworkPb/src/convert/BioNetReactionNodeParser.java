package convert;

import io.LoaderTool;
import io.Parser;

import java.io.BufferedReader;
import java.io.IOException;

import logicLanguage.PbFormula;

import tools.Arguments;

public class BioNetReactionNodeParser implements Parser {

	public void parse(BufferedReader bIn) throws IOException{
		String line=LoaderTool.getNextLine(bIn, '%');
		//TODO : le Header TPTP ?
		int i=0;
		while (line!=null){
			if (line.startsWith("cnf"))
				parseCnf(line);
			if (line.startsWith("pf"))
				parseGoal(line);
			line=LoaderTool.getNextLine(bIn, '%');
			i++;
			if (i%10==0)
				System.out.print(".");
			if (i%100==0)
				System.out.println();
		}
		
	}
	
	private void parseCnf(String line){
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		Arguments formula=Arguments.parse(temp.getFormula());
		if (formula.size()==1 &&
			(formula.get(0).startsWith("reactionnode") || formula.get(0).startsWith("+reactionnode")))
				parseReactionNodeFact(formula.get(0));
	}

	private void parseReactionNodeFact(String reactionNode){
		String reaction,reactant,product;//,enzyme;
		Arguments products,reactants;
		//extract arguments
			//cnf(an, axiom, [reactionnode(isocitrate, d4d2d1d3, compound(citrate,transaconitate))]).
		Arguments arg=Arguments.parse("["+reactionNode.substring(reactionNode.indexOf("(")+1,reactionNode.lastIndexOf(')'))+"]");
		//attribute arguments to correct string
		reaction="r_"+arg.get(1).trim()+"_"+reactionNumber;
		reactionNumber++;
		reactant=arg.get(0).trim();
		if (reactant.startsWith("compound(") || reactant.startsWith("+compound(")){
			reactants=parseCompound(reactant);
			reactant="c_"+reactant.replace('(', 'C').replace(')','C').replace(',', '_');
			while (reactant.contains(" ")) reactant=reactant.replace(" ", "");
		}
		else {
			reactant="m_"+reactant;
			reactants=new Arguments();
			reactants.add(reactant);
		}
			
		//enzyme="e_"+arg.get(2).trim(); //should be empty string in examples files
		product=arg.get(2).trim();			
		if (product.startsWith("compound(") || product.startsWith("+compound(")){
			products=parseCompound(product);
			product="c_"+product.replace('(', 'C').replace(')','C').replace(',', '_');
			while (product.contains(" ")) product=product.replace(" ", "");
		}
		else {
			product="m_"+product;
			products=new Arguments();
			products.add(product);
		}
		
		solFileLines.add(generateReactantFormula(reaction,reactant));
		solFileLines.add(generateCombinationFormula(reactant,reactants));
		solFileLines.add(generateProductFormula(reaction,product));
		solFileLines.addAll(generateDecompositionFormula(product,products));
	}

	private Arguments parseCompound(String compound){
		//compound(citrate,transaconitate)
		Arguments arg;
		arg=Arguments.parse("["+compound.substring(compound.indexOf("(")+1,compound.lastIndexOf(')'))+"]");
		Arguments res=new Arguments();
		for (String m:arg)
			res.add("m_"+m);
		return res;
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
		String goal=line.substring(line.indexOf(",")+1,line.length()).replace('-', '_');
		Arguments goals=parseCompound(goal);
		String pf="pf("+goals.toString()+"<=1).";
		this.solFileLines.add(pf);				
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
	private static int reactionNumber=0;
}
