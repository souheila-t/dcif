package cnfPb;

import genLib.io.LoaderTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.pfield.PField;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import solarInterface.IndepPField;
import solarInterface.SolProblem;
import genLib.tools.Arguments;

public class SolProblemWithHeader extends SolProblem {

	public SolProblemWithHeader(String name, String problem, String status, String type, List<Clause> axioms, List<Clause>top_clauses, PField pf){
		super(name, problem, status,type, axioms, top_clauses, pf);
	}
	
	public SolProblemWithHeader(SolProblem pb) throws ParseException{
		super(pb,true);
		computeHeader();
	}
	
	
	public void parse(BufferedReader bIn) throws IOException{
		String line=LoaderTool.getNextLine(bIn, '$');
		//TODO : le Header TPTP ?
		while (line!=null){
			if (line.startsWith("%"))
				parseHeader(line);
			else{
				try {
					parseSolFileLine(line);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					throw new IOException(e);
				}
			}
			line=LoaderTool.getNextLine(bIn, '%');
		}
	}

	public void parseHeader(String line){
		if (line.startsWith("%Problem: ")) 
			problem=line.substring(line.indexOf(':')+1).trim();
		else if (line.startsWith("%Status: ")) 
			status=line.substring(line.indexOf(':')+1).trim();
		else if (line.startsWith("%Type: ")) 
			type=line.substring(line.indexOf(':')+1).trim();
		else if (line.startsWith("%Syntax: ")) 
			return;		
		else if (line.startsWith("%     Number of clauses: ")) 
				nbClauses=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());	
		else if (line.startsWith("%     Number of predicates: ")) 
				nbPred=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());	
		else if (line.startsWith("%     Number of functors: ")) 
				nbFunc=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());	
		else if (line.startsWith("%     Number of constants: ")) 
				nbConst=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());	
		else if (line.startsWith("%     Number of atoms: ")) 
				nbAtoms=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());
		else if (line.startsWith("%     Maximal clause size: ")) 
				maxClSize=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());
		else if (line.startsWith("%     Average clause size: ")) 
				avgClSize=Double.parseDouble(line.substring(line.indexOf(':')+1).trim());
		else if (line.startsWith("%     Proportion of atoms in PField: ")) 
				pFieldProp=Double.parseDouble(line.substring(line.indexOf(':')+1).trim());
		else if (line.startsWith("%     Clause size distribution: ")) 
				parseDistrib(line.substring(line.indexOf(':')+1).trim());
		else if (line.startsWith("%     Maximal term depth: ")) 
				maxTermDepth=Integer.parseInt(line.substring(line.indexOf(':')+1).trim());
		else if (line.startsWith("%     SPC: ")) 
				spcCode=line.substring(line.indexOf(':')+1).trim();
		else if (name==null || name.equals("unnamed")) 
			name=line.substring(line.indexOf('%')+1).trim();
		
	}
	
	protected void parseDistrib(String distrib){
		Arguments arg=Arguments.parse(distrib);
		clSize=new int[arg.size()];
		for (int i=0;i<arg.size();i++){
			clSize[i]=Integer.parseInt(arg.get(i));
		}
	}
	
	public void printHeader(PrintStream p){
		super.printHeader(p);
		p.println("%Syntax: ");		
		p.println("%     Number of clauses: "+nbClauses);	
		p.println("%     Number of predicates: "+nbPred);	
		 p.println("%     Number of functors: "+nbFunc);	
		 p.println("%     Number of constants: "+nbConst);	
		 p.println("%     Number of atoms: "+nbAtoms);	
		 p.println("%     Maximal clause size: "+maxClSize);	
		 p.println("%     Average clause size: "+avgClSize);	
		 p.println("%     Clause size distribution: "+clSize);
		 p.println("%     Proportion of atoms in PField: "+pFieldProp);	
		 p.println("%     Maximal term depth: "+maxTermDepth);	
		 p.println("%     SPC: "+spcCode);
		p.println();		
	}	
	
	public void computeHeader() throws ParseException{
		CNF fullTheory=new CNF();
		fullTheory.addAll(getClauses());

		nbClauses=fullTheory.size();
		nbPred=CNF.getPredicates(getEnv(), fullTheory).size();
		
		List<Integer> distrib=new ArrayList<Integer>();
		int maxSize=0;
		nbAtoms=0;
		int nbPFAtoms=0;
		for (Clause c:fullTheory){
			int size=c.getLiterals().size();
			nbAtoms+=size;
			if (size>maxSize){
				for (int i=maxSize;i<=size;i++)
					distrib.add(0);
				maxSize=size;
			}
			distrib.set(size, distrib.get(size)+1);
			nbPFAtoms+=IndepClause.countOccurences(getEnv(), c, IndepPField.toLiterals(getEnv(), getPField().getPLiterals())); //TODO do not work if pfield is not only freed literals 
		}
		maxClSize=maxSize;
		avgClSize=(double)nbAtoms/nbClauses;
		pFieldProp=(double)nbPFAtoms/nbAtoms;
		clSize=new int[maxClSize];
		for (int i=0;i<maxClSize;i++)
			clSize[i]=distrib.get(i+1).intValue();
		nbFunc=getEnv().getSymTable().getFunctions().size();
		nbConst=getEnv().getSymTable().getConstants().size();
	}	
	
	
	
	public int getNbClauses() {
		return nbClauses;
	}
	public void setNbClauses(int nbClauses) {
		this.nbClauses = nbClauses;
	}
	public int getNbPred() {
		return nbPred;
	}
	public void setNbPred(int nbPred) {
		this.nbPred = nbPred;
	}
	public int getNbFunc() {
		return nbFunc;
	}
	public void setNbFunc(int nbFunc) {
		this.nbFunc = nbFunc;
	}
	public int getNbConst() {
		return nbConst;
	}
	public void setNbConst(int nbConst) {
		this.nbConst = nbConst;
	}
	public int getNbAtoms() {
		return nbAtoms;
	}
	public void setNbAtoms(int nbAtoms) {
		this.nbAtoms = nbAtoms;
	}
	public int getMaxClSize() {
		return maxClSize;
	}
	public void setMaxClSize(int maxClSize) {
		this.maxClSize = maxClSize;
	}
	public double getAvgClSize() {
		return avgClSize;
	}
	public void setAvgClSize(double avgClSize) {
		this.avgClSize = avgClSize;
	}
	public double getPFieldProp() {
		return pFieldProp;
	}

	public void setPFieldProp(double pFieldProp) {
		this.pFieldProp = pFieldProp;
	}
	public int[] getClSize() {
		return clSize;
	}
	public void setClSize(int[] clSize) {
		this.clSize = clSize;
	}
	public int getMaxTermDepth() {
		return maxTermDepth;
	}
	public void setMaxTermDepth(int maxTermDepth) {
		this.maxTermDepth = maxTermDepth;
	}
	public String getSpcCode() {
		return spcCode;
	}
	public void setSpcCode(String spcCode) {
		this.spcCode = spcCode;
	}





	//Syntax elements of the header	
	/**
	 * Number of clauses
	 */
	int nbClauses=-1;	
	/**
	 * Number of predicates
	 */
	int nbPred=-1;	
	/**
	 * Number of functors
	 */
	int nbFunc=-1;	
	/**
	 * Number of constants
	 */
	int nbConst=-1;	
	/**
	 * Number of atoms
	 */
	int nbAtoms=-1;	
	/**
	 * Maximal clause size
	 */
	int maxClSize=-1;	
	/**
	 * Average clause size
	 */
	double avgClSize=-1;
	/**
	 * Proportion of atoms in the theory that are in the production field
	 */
	double pFieldProp=-1;


	/**
	 * Clause size distribution. Index i correspond to size i+1.
	 * So the array goes from size 1 to maxSize (supposedly no clause of size 0)
	 */
	int[] clSize;
	/**
	 * Maximal term depth
	 */
	int maxTermDepth=-1;	
	/**
	 * SPC
	 */
	String spcCode="not checked";	

}
