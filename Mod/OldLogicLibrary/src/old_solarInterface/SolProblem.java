/**
 * 
 */
package old_solarInterface;

import genLib.io.LoaderTool;
import genLib.io.Parser;
import genLib.io.Saver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import old_logicLanguage.CNF;
import old_logicLanguage.ClauseConnectionNetwork;
import old_logicLanguage.IndepClause;
import old_logicLanguage.PbFormula;

import org.nabelab.solar.CFP;
import org.nabelab.solar.Clause;
import org.nabelab.solar.Env;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;

/**
 * @author Gauvain Bourgne
 *
 * This class describe a solar problem. It can be used to load or generate files .sol
 * It is the output of sol conversion of TPTPProblem, and also the input of distribution generators
 * (class DCFProblem)
 */
public class SolProblem implements Parser, Saver{

	public SolProblem() {
		this.name = "unnamed";
		this.pf = new IndepPField();
		this.problem = "";
		this.status = "";
		this.type = "";
	}

	public SolProblem(String name, String problem, String status, String type, IndepPField pf) {
		this.name = name;
		this.pf = pf;
		this.problem = problem;
		this.status = status;
		this.type = type;
	}

	public SolProblem(SolProblem pb, boolean includeClauses) {
		this.name = pb.name;
		this.pf = pb.pf;
		this.problem = pb.problem;
		this.status = pb.status;
		this.type = pb.type;
		this.depthLimit=pb.depthLimit;
		if (includeClauses){
			axioms.addAll(pb.axioms);
			top_clauses.addAll(pb.top_clauses);
		}
	}
	
	public SolProblem(String name, String problem, String status, String type, 
						Collection<? extends IndepClause> axioms,  
						Collection<? extends IndepClause> top_clauses, 
						IndepPField pf) {
		this(axioms,top_clauses,pf);
		this.name = name;
		this.problem = problem;
		this.status = status;
		this.type = type;
	}
	
	public SolProblem(Collection<? extends IndepClause> axioms,  
			Collection<? extends IndepClause> top_clauses, 
			IndepPField pf){
		this.pf = pf;
		if (axioms instanceof CNF)
			this.axioms.addAll((CNF)axioms);
		else
			this.axioms.addAll(axioms);
		if (top_clauses instanceof CNF)
			this.top_clauses.addAll((CNF)top_clauses);
		else
			this.top_clauses.addAll(top_clauses);
	}
	
	public SolProblem(String filename) throws Exception{
		this.name = filename;
		this.pf = new IndepPField();
		this.problem = "";
		this.status = "";
		this.type = "";
		load(filename);
	}
	public SolProblem NumberedCopy(){
		int i;
		SolProblem res=new SolProblem(name, problem, status, type, pf);
		for (i=0;i<getNbClauses();i++){
			if (isTopClause(i))
				res.addTopClause(getClause(i).rename(""+i));
			else
				res.addAxiom(getClause(i).rename(""+i));
		}
		// Note : the production field is not affected 
		return res;
	}
	
	public int getNbClauses(){
		return axioms.size()+top_clauses.size();
	}
	
	public IndepPField getPField(){
		return pf;
	}
	
	public void setPField(IndepPField pfield){
		pf=pfield;
	}

	public String getName(){
		return name;
	}
	
	public IndepClause getClause(int indice){
		if (indice<top_clauses.size())
			return top_clauses.get(indice);
		else if (indice<getNbClauses())
			return axioms.get(indice-top_clauses.size());
		return null;
	}
	
	public boolean isTopClause(int indice){
		return (indice<top_clauses.size());
	}
	
	//to CFP
	public void addClause(IndepClause clause, boolean topClause){
		if (topClause)
			addTopClause(clause);
		else
			addAxiom(clause);
	}
	
	public void addAxiom(IndepClause axiom){
	//	if (axioms.contains(axiom))
	//		return false;
		axioms.add(axiom);
	//	return true;
	}
	public void addAxioms(CNF axiomList){
	//	boolean modif=false;
	//	for (IndepClause cl:axiomList){
	//		modif=addAxiom(cl)||modif;
	//	}
	//	return modif;
		axioms.addAll(axiomList);
	}
	public void addTopClause(IndepClause topClause){
	//	if (top_clauses.contains(topClause))
	//		return false;
		top_clauses.add(topClause);
	//	return true;
	}
	public void addTopClauses(CNF clauseList){
	//	boolean modif=false;
	//	for (IndepClause cl:clauseList){
	//		modif=addTopClause(cl)||modif;
	//	}
	//	return modif;
		top_clauses.addAll(clauseList);
	}
	
	public CNF getTopClauses(boolean ref){
		if (ref)
			return top_clauses;
		else 
			return CNF.copy(top_clauses);
	}
	
	public CNF getAxioms(boolean ref){
		if (ref)
			return axioms;
		else 
			return CNF.copy(axioms);
	}

	public CNF getAllClauses(){
		CNF res=new CNF();
		res.addAll(axioms);
		res.addAll(top_clauses);
		return res;
	}

	//count nb predicates
	
	
	//load .sol
	public void load(String filename) throws Exception{
		LoaderTool.load(filename, ".sol", this);
	 }
	
	public void parse(BufferedReader bIn) throws IOException{
		String line=LoaderTool.getNextLine(bIn, '%');
		//TODO : le Header TPTP ?
		while (line!=null){
			parseSolFileLine(line);
			line=LoaderTool.getNextLine(bIn, '%');
		}
	}
	
	public void parseSolFileLine(String line){
		if (line.startsWith("cnf"))
			parseCnf(line);
		if (line.startsWith("pf"))
			pf=IndepPField.parse(line);
	}

	private void parseCnf(String line){
		PbFormula temp=PbFormula.parseFormulaBlock(line,"SOLAR");
		IndepClause clause=temp.toIndepClause();
		if (temp.getRole().equals("top_clause"))
			top_clauses.add(clause);
		else
			axioms.add(clause);
	}
	
	//distribution avec ASP
	public void generateDlvAspFacts(PrintStream p){
		for (IndepClause cl:axioms){
			PbFormula f=new PbFormula(cl,"axiom");
			p.println(f.convertTo("asp"));
		}
		for (IndepClause cl:top_clauses){
			PbFormula f=new PbFormula(cl,"top_clause");
			p.println(f.convertTo("asp"));
		}
	}
	
	public void generateConnectionClaspFacts(PrintStream p){
		CNF theory=new CNF();
		theory.addAll(axioms);
		theory.addAll(top_clauses);
		ClauseConnectionNetwork network=new ClauseConnectionNetwork(theory);
		p.println(network.convertToAsp());
	}
	
	public void saveConnectionGraph(String filename, boolean replace) throws Exception{
		CNF theory=new CNF();
		theory.addAll(axioms);
		theory.addAll(top_clauses);
		ClauseConnectionNetwork network=new ClauseConnectionNetwork(theory);
		LoaderTool.save(filename, ".gra", network.convertToGraph(), replace);
	}
	
	
	public void save(String filename, boolean replace) throws Exception{
		LoaderTool.save(filename, ".sol", this, replace);
	}
	
	public void save(PrintStream p){
		printHeader(p);
		for (IndepClause cl:top_clauses){
			p.println(cl.toSolFileLine("top_clause"));
		}
		for (IndepClause cl:axioms){
			p.println(cl.toSolFileLine("axiom"));
		}
		p.println();
		p.println(pf.toSolFileLine());
	}
	
	public void printHeader(PrintStream p){
		p.println("%"+name);
		p.println("%Problem: "+problem);
		p.println("%Status: "+status);
		p.println("%Type: "+type);
		p.println();		
	}
	
	public CFP toCFP(Env env){
		//initialize options and problem 
		Options opt=new Options(env);
		//opt.setDepthLimit(depthLimit);
		opt.setUsedClausesOp(true);
		CFP problem= new CFP(env, opt);
		for (IndepClause cl : axioms)
			problem.addClause(cl.toClause(env));
		for (IndepClause cl : top_clauses)
			problem.addClause(cl.toClause(env,Clause.TOP_CLAUSE));
		if (pf!=null)
			try {
				problem.setPField(pf.toPField(env, problem.getOptions()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		
		return problem;

	}
	
	
	public int getDepthLimit() {
		return depthLimit;
	}

	public void setDepthLimit(int depthLimit) {
		this.depthLimit = depthLimit;
	}

	
	
	
		
	/**
	 * Name of the problem (also filename - file 'name'.sol)
	 */
	protected String name;
	/**
	 * description of the problem
	 */
	protected String problem;	
	/**
	 * Status of the problem (SAT or UNSAT in our cases)
	 */
	protected String status;
	/**
	 * Types of the problems (CF,CARC,NEWCF,NEWCARC,REFUT,NEWREFUT)
	 */
	protected String type;	
	/**
	 * All the clauses that should be used as top-clauses
	 */
	protected CNF top_clauses=new CNF();
	/**
	 * All the axioms clauses
	 */
	protected CNF axioms=new CNF();
	/**
	 * The production field
	 */
	protected IndepPField pf=new IndepPField();
	
	protected int depthLimit=-1;

	
}
