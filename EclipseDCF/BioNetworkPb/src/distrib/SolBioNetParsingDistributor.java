package distrib;

import io.LoaderTool;
import io.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import logicLanguage.PbFormula;
import tools.Arguments;



public class SolBioNetParsingDistributor implements Parser{
	
	
	
	public void parse(BufferedReader bIn) throws IOException{
		String line=LoaderTool.getNextLine(bIn, '%');
		//TODO : le Header TPTP ?
		int i=0;
		while (line!=null){
			if (line.startsWith("cnf"))
				parseCnf(line);
			if (line.startsWith("pf"))
				pfLine=line;
			line=LoaderTool.getNextLine(bIn, '%');
			i++;
			if (i%10==0)
				System.out.print(".");
			if (i%100==0)
				System.out.println(i/100);
		}
		reduce(nbAg);
	}
	
	private void parseCnf(String line){
		if (line.startsWith("cnf(source"))
			parseSource(line);
		if (line.startsWith("cnf(activ"))
			parseActiv(line);
		if (line.startsWith("cnf(prod"))
			parseProd(line);
		if (line.startsWith("cnf(dec"))
			parseDec(line);
		if (line.startsWith("cnf(combi"))
			parseCombi(line);
	}

	private void parseSource(String line){	
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		Arguments formula=Arguments.parse(temp.getFormula());
		String reactant=formula.get(0);
		int cMet=addMetabolite(reactant);
		addFormula(cMet,temp);
	}

	private void parseActiv(String line){	
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		Arguments formula=Arguments.parse(temp.getFormula());
		String reactant=formula.get(0);
		//String reaction=formula.get(1);
		if ((formula.get(0).startsWith("r_") || formula.get(0).startsWith("+r_")))
			reactant=formula.get(1);
		int cMet=addMetabolite(reactant);
		addFormula(cMet,temp);
	}

	private void parseProd(String line){	
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		Arguments formula=Arguments.parse(temp.getFormula());
		String product=formula.get(0);
		if (formula.get(0).startsWith("-r_"))
			product=formula.get(1);
		int cMet=addMetabolite(product);
		addFormula(cMet,temp);
	}
	
	private void parseDec(String line){	
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		Arguments formula=Arguments.parse(temp.getFormula());
		String s_component=formula.get(0);
		String s_compound=formula.get(1);
		if (formula.get(0).startsWith("-c_")){
			s_component=formula.get(1);
			s_compound=formula.get(0);
		}
		int compound=addMetabolite(s_compound);
		int component=addMetabolite(s_component);
		int g1=getGroup(compound);
		int g2=getGroup(component);
		fusionGroups(g1,g2,true);
		links.get(component).add(compound);
		links.get(compound).add(component);
		addFormula(compound,temp);
	}
	
	
	private void parseCombi(String line){	
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		Arguments formula=Arguments.parse(temp.getFormula());
		List<Integer> components=new ArrayList<Integer>();
		int compound=-1;
		for (String elt:formula)
			if (elt.startsWith("-m_"))
				components.add(addMetabolite(elt));
			else
				compound=addMetabolite(elt);
		int g1,g2;
		for (Integer e:components){
				g1=getGroup(compound);
				g2=getGroup(e);
				fusionGroups(g1,g2,true);
				links.get(e).add(compound);
				links.get(compound).add(e);
		}
		addFormula(compound,temp);
	}
	
	private String purgeSign(String lit){
		if (lit.startsWith("-") || lit.startsWith("+"))
				return lit.substring(1);
		return lit;
	}
	

	private int addMetabolite(String metabolite){
		String met=purgeSign(metabolite);
		int ind=metabolites.indexOf(met);
		if (ind==-1){
			ind=metabolites.size();
			metabolites.add(met);
			formulas.add(new LinkedList<PbFormula>());
			List<Integer> ng=new LinkedList<Integer>();
			ng.add(ind);
			groups.add(ng);
			links.add(new ArrayList<Integer>());
		}
		return ind;
	}

	
	private void addFormula(int ind, PbFormula formula){
		formulas.get(ind).add(formula);
	}
	
	private int getGroup(int elt){
		for (int i=0;i<groups.size();i++){
			List<Integer> g=groups.get(i);
			if (g.contains(elt))
				return i;
		}
		return -1;
	}
	
	private void fusionGroups(int g1, int g2, boolean limit){
		if (g1!=g2){
			int n1=groups.get(g1).size();
			int n2=groups.get(g2).size();
			if (n1+n2<maxMetab || !limit){
				groups.get(g1).addAll(groups.get(g2));
				groups.remove(g2);
			}
		}
	}
	
	public int countFormulas(List<Integer> group){
		int nbFormulas=0;
		for (Integer metab:group)
				nbFormulas+=formulas.get(metab).size();
		return nbFormulas;
	
	}
	
	public int countFormulas(int indGroup){
		List<Integer> elt=groups.get(indGroup);
		int nbFormulas=0;
		for (Integer metab:elt)
				nbFormulas+=formulas.get(metab).size();
		return nbFormulas;
	
	}
	public void reduce(int nbAg){
		CompareNbFormula ascend=new CompareNbFormula(this,true);
		CompareNbFormula descend=new CompareNbFormula(this,false);
		Collections.sort(groups,descend);
		List<List<Integer>> minGroups=new ArrayList<List<Integer>>();
		minGroups.addAll(groups.subList(nbAg, groups.size()-1));
		for (int i=groups.size()-1;i>=nbAg;i--)
			groups.remove(i);
		Collections.reverse(groups);
		while (!minGroups.isEmpty()){
			groups.get(0).addAll(minGroups.get(0));
			minGroups.remove(0);
			Collections.sort(groups,ascend);
		}
	}
	
	public String toString(){
		int i=0;
		String res="\nTOTAL : "+groups.size()+" groups. \n";
		for (List<Integer> elt:groups){
			res+="\n Group "+i+": "+elt.size()+" metabolites and compounds. \n";
			int nbFormulas=0;
			for (Integer metab:elt){
				res+=metabolites.get(metab)+", ";
				nbFormulas+=formulas.get(metab).size();
			}
			res+="\n => "+nbFormulas+" formulas.\n";
			i++;
		}
		return res;
	}
	
	public List<String> metabolites=new ArrayList<String>();
	public List<List<Integer>> links=new ArrayList<List<Integer>>();
	public List<List<Integer>> groups=new LinkedList<List<Integer>>();
	public List<List<PbFormula>> formulas=new ArrayList<List<PbFormula>>();
	public String pfLine="pf().";
	public static int maxMetab=2000;
	public static int nbAg=2;
}
