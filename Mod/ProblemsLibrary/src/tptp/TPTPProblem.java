/**
 * 
 */
package tptp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.pfield.PField;

import cnfPb.SolProblemWithHeader;
import logicLanguage.CNF;
import logicLanguage.PbFormula;
import solarInterface.IndepPField;
import solarInterface.SolProblem;


/**
 * @author Gauvain Bourgne
 *
 * This class aims at loading files problem.p, imported from TPTP database, and have them in memory.
 * It can then notably be converted to solar problem files problem.sol according to different 
 * conversion methods (to determine production field and chose what should be axiom or top-clause
 * according to the roles given by TPTP problems).
 */
public class TPTPProblem {

	public final static char COMMENTAIRE='%';
	public final static int SCV_PF_REFUT=0;
	public final static int SCV_PF_CONJ=1;
	public final static int SCV_PF_NEGHYP=2;
	public final static int SCV_PF_ALL=3;
	public final static int SCV_CL_EXCLUDE=0;
	public final static int SCV_CL_INCLUDE=1;
	public final static int SCV_CL_TOPCLAUSE=2;
	
	public final static String MET_ALL_FULLCARC="C0";
	public final static String MET_ALL_REFUT="R";
	public final static String MET_NCONJ_CARC="C1";
	public final static String MET_TCONJ_FULLNEWC="C0i1";
	public final static String MET_TCONJ_REFUT="Ri1";
	public final static String MET_THYP_FULLNEWC="C0i2";
	public final static String MET_THYP_REFUT="Ri2";
	public final static String MET_TCH_FULLNEWC="C0i3";
	public final static String MET_TCH_REFUT="Ri3";
	public final static String MET_THYP_NCONJ_NEWC="C1i2";
	public final static String MET_ABDUCTION="C2i1";
	
	public TPTPProblem() {
		super();
	}

	public TPTPProblem(String filename) {
		super();
		try {
			load(filename);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void load(String filename) throws Exception{
		
		File accesFichier = new File(filename+".p") ;  	
	 	if (!accesFichier.exists())
	 		throw new Exception("File " + accesFichier.getAbsolutePath() + 
	 				          " does not exist !") ;
		Reader fIn         ; // flux de sortie
		BufferedReader bIn ; // buffer associe
		try {	// ouverture des flux	
				fIn   = new FileReader(accesFichier)  ;
				bIn   = new BufferedReader(fIn)       ;
				//parse
				name=filename;
				parse(bIn, true);
				// ferme les flux
				bIn.close() ;
				fIn.close() ;				
			} // fin try
			catch(IOException e){
				System.err.println("Input/Output problem:\n"+e) ; throw new Exception (e) ;}  
			catch(Exception e){
				System.err.println("Problem in file " + filename+":\n"+e);throw new Exception(e);} 
			finally{
				bIn = null ;fIn = null ;
			} // end try-catch-finally
	 }	


 	/**
	 *  return first line which is neither a comment nor a empty line
	 */
	public static String getNextFormula(BufferedReader bIn) throws IOException
	{
	    String line = bIn.readLine();
		while ( line != null &&	  		    // pas fin fichier
			    ( line.length()  == 0 || line.charAt(0) == COMMENTAIRE )){  // CR ||commentaire
			line = bIn.readLine();
		}
		String formula=line;
		while ( line != null &&!line.contains(").")){
			line = bIn.readLine();
			formula+= line;
		}
		if (line!=null && formula.contains(")."))
				return formula;
		return null;
	}

	
	/**
	 * parse the input.
	 * Do not deal with "include" for now 
	 * @param bIn: input (BufferedReader)
	 * @param header: indicates if there are standard header in the file
	 */
	public void parse(BufferedReader bIn, boolean header){
		try {
			// parse header
			if (header)
				parseHeader(bIn);
			// parse formulas
			parseFormulas(bIn);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private void parseHeader(BufferedReader bIn) throws IOException{
		// parse header
		String line = bIn.readLine() ;
		// Problem full name
		while (line != null && !line.startsWith("% Problem"))
			line = bIn.readLine();
		problem=line.substring(line.indexOf(':')+1);
		// Status
		while (line != null && !line.startsWith("% Status")){
			line = bIn.readLine();
		}
		status=line.substring(line.indexOf(':')+1);
		// Type specification
		while (line != null && !line.startsWith("% SPC")){
			line = bIn.readLine();
		}
		type=line.substring(line.indexOf(':')+1);	
	}
	
	private void parseFormulas(BufferedReader bIn) throws IOException{
		String formula = getNextFormula(bIn);
		while (formula !=null ){
			//parse formula
			PbFormula f=PbFormula.parseFormulaBlock(formula, "TPTP");
			//store it
			if (f.getRole().equals("negated_conjecture"))
				negated_conjectures.add(f);
			else if (f.getRole().equals("hypothesis"))
				hypotheses.add(f);
			else
				axioms.add(f);
			//get next one
			formula = getNextFormula(bIn);
		}
	}
		
	/**
	 * Convert the TPTPProblem to a SolProblem.
	 * @param method, the method used to convert the TPTP problem. There are several options:
	 * "ALL_FULLCARC" or "C0" considers all clauses as axioms and use the whole vocabulary as production field
	 * "ALL_REFUT" or "R" considers all clauses as axioms and tries to find contradiction
	 * "NCONJ_CARC" or "C1" considers axioms and hypotheses as axioms and use the vocabulary of conjecture 
	 * 				to make the production field
	 * "TCONJ_FULLNEWC" or "C0i1" considers axioms and hypotheses as axioms, negated_conjecture as top-clause
	 * 					and use the whole vocabulary as pf
	 * "TCONJ_REFUT" or "Ri1" considers axioms and hypotheses as axioms, negated_conjecture as top-clause
	 * 					and tries to find contradiction in the Newcarc
	 * "THYP_FULLNEWC" or "C0i2" considers axioms and negated_conjecture as axioms, hypothesis as top-clause
	 * 					and use the whole vocabulary as pf
	 * "THYP_REFUT" or "Ri2" considers axioms and negated_conjecture as axioms, hypothesis as top-clause
	 * 					and tries to find contradiction in the Newcarc
	 * "TCH_FULLNEWC" or "C0i3" considers only axioms as axioms, hypothesis and negated_conjecture as top-clause
	 * 					and use the whole vocabulary as pf
	 * "TCH_REFUT" or "Ri3" considers only axioms as axioms, hypothesis and negated_conjecture as top-clause
	 * 					and tries to find contradiction in the Newcarc
	 * "THYP_NCONJ_NEWC" or "C1i2" considers axioms as such and hypotheses as top-clauses and use the 
	 * 						vocabulary of conjecture to make the production field
	 * "ABDUCTION" or "C2i1" consider axioms as such and negated_conjecture as top-clauses and use the 
	 * 						vocabulary of negated hypothesis to make the production field
	 * @throws ParseException 
	 */
	public SolProblem convertToSolProblem(Env env, Options opt, String method) throws ParseException{
	/*This function only sort out the different code. Actual conversion is in subfunction.*/
		if (method.equals("ALL_FULLCARC") || method.equals(MET_ALL_FULLCARC))
			return convertToSolPb(env, opt, SCV_CL_INCLUDE,SCV_CL_INCLUDE,SCV_PF_ALL,"CF"); //C0
		if (method.equals("ALL_REFUT") || method.equals(MET_ALL_REFUT))
			return convertToSolPb(env, opt, SCV_CL_INCLUDE,SCV_CL_INCLUDE,SCV_PF_REFUT,"REFUT"); //R
		if (method.equals("NCONJ_CARC")|| method.equals(MET_NCONJ_CARC))
			return convertToSolPb(env, opt, SCV_CL_INCLUDE,SCV_CL_EXCLUDE,SCV_PF_CONJ,"CARC"); //C1
		if (method.equals("TCONJ_FULLNEWC") || method.equals(MET_TCONJ_FULLNEWC))
			return convertToSolPb(env, opt, SCV_CL_INCLUDE,SCV_CL_TOPCLAUSE,SCV_PF_ALL,"NEWCF"); //C0i1
		if (method.equals("TCONJ_REFUT") || method.equals(MET_TCONJ_REFUT))
			return convertToSolPb(env, opt, SCV_CL_INCLUDE,SCV_CL_TOPCLAUSE,SCV_PF_REFUT,"NEWREFUT"); //Ri1
		if (method.equals("THYP_FULLNEWC") || method.equals(MET_THYP_FULLNEWC))
			return convertToSolPb(env, opt, SCV_CL_TOPCLAUSE,SCV_CL_INCLUDE,SCV_PF_ALL,"NEWCF"); //C0i2
		if (method.equals("THYP_REFUT") || method.equals(MET_THYP_REFUT))
			return convertToSolPb(env, opt, SCV_CL_TOPCLAUSE,SCV_CL_INCLUDE,SCV_PF_REFUT,"NEWREFUT"); //Ri2
		if (method.equals("TCH_FULLNEWC") || method.equals(MET_TCH_FULLNEWC))
			return convertToSolPb(env, opt, SCV_CL_TOPCLAUSE,SCV_CL_TOPCLAUSE,SCV_PF_ALL,"NEWCF"); //C0i3
		if (method.equals("TCH_REFUT") || method.equals(MET_TCH_REFUT))
			return convertToSolPb(env, opt, SCV_CL_TOPCLAUSE,SCV_CL_TOPCLAUSE,SCV_PF_REFUT,"NEWREFUT"); //Ri3
		if (method.equals("THYP_NCONJ_NEWC") || method.equals(MET_THYP_NCONJ_NEWC))
			return convertToSolPb(env, opt, SCV_CL_TOPCLAUSE,SCV_CL_EXCLUDE,SCV_PF_CONJ,"NEWCARC"); //C1i2
		if (method.equals("ABDUCTION") || method.equals(MET_ABDUCTION))
			return convertToSolPb(env, opt, SCV_CL_EXCLUDE,SCV_CL_TOPCLAUSE,SCV_PF_NEGHYP,"NEWCARC"); //C2i1
		return null;
	}
	
	private SolProblem convertToSolPb(Env env, Options opt, int codeHyp, int codeNegConj, int codePf, String type) throws ParseException{
		CNF axioms=new CNF();
		CNF top_clauses=new CNF();
		CNF hypo=new CNF();
		CNF neg_conjecture=new CNF();
		for (PbFormula f:hypotheses)
			hypo.add(f.toClause(env));
		for (PbFormula f:negated_conjectures)
			neg_conjecture.add(f.toClause(env));
		// fill axioms and top_clauses
		for (PbFormula f:this.axioms)
			axioms.add(f.toClause(env));
		if (codeHyp==SCV_CL_INCLUDE) 
			axioms.addAll(hypo);
		else if (codeHyp==SCV_CL_TOPCLAUSE) 
			top_clauses.addAll(hypo);
		if (codeNegConj==SCV_CL_INCLUDE)
			axioms.addAll(neg_conjecture);
		else if (codeNegConj==SCV_CL_TOPCLAUSE) 
			top_clauses.addAll(neg_conjecture);
		// determine pfield
		PField pf=new PField(env, opt);
		CNF baseLanguage=new CNF();
		switch(codePf){ 
		case SCV_PF_ALL:
			baseLanguage.addAll(axioms);
			baseLanguage.addAll(top_clauses);
			IndepPField.addLiterals(pf, CNF.getFullVocabulary(env, baseLanguage));
			break;
		case SCV_PF_CONJ:
			pf=IndepPField.fitToCNF(env, opt, neg_conjecture, !sat());
			break;
		case SCV_PF_NEGHYP:
			baseLanguage.addAll(hypo);
			pf=IndepPField.fitToCNF(env, opt, hypo, true);
			break;
		case SCV_PF_REFUT: // nothing to do
		}
		SolProblemWithHeader result= new SolProblemWithHeader(name, problem, status,type, axioms, top_clauses, pf);
		result.setNbClauses(nbClauses);
		result.setNbPred(nbPred);
		result.setNbFunc(nbFunc);
		result.setNbConst(nbConst);
		result.setNbAtoms(nbAtoms);
		result.setMaxClSize(maxClSize);
		result.setAvgClSize(avgClSize);
		result.setClSize(clSize);
		result.setMaxTermDepth(maxTermDepth);
		result.setSpcCode(spcCode);
		return result;
	}
	
	public boolean hasAxioms(){
		return !axioms.isEmpty();
	}
	
	public boolean hasHypothesis(){
		return !hypotheses.isEmpty();
	}
	
	public boolean hasNegConj(){
		return !negated_conjectures.isEmpty();
	}
	
	public boolean sat(){
		String s=status.trim().toUpperCase();
		return s.startsWith("SAT");
	}
	
	/**
	 * Name of the problem (also filename - file 'name'.p)
	 */
	String name;
	/**
	 * description of the problem
	 */
	String problem;	
	/**
	 * TPTP status of the problem (SAT or UNSAT in our cases)
	 */
	String status;
	/**
	 * TPTP types of the problems (CNF_RFO, CNF_RFO_NEQ, ...)
	 */
	String type;	
	/**
	 * All the formulas of the problem that have the role "negated_conjecture"
	 */
	List<PbFormula> negated_conjectures=new ArrayList<PbFormula>();
	/**
	 * All the formulas of the problem that have the role "hypothesis"
	 */
	List<PbFormula> hypotheses=new ArrayList<PbFormula>();
	/**
	 * All the formulas of the problem that have the role "axiom", or any other role than 
	 * "hypothesis" and "negated_conjecture"
	 */
	List<PbFormula> axioms=new ArrayList<PbFormula>();
	//SYNTAX
	/**
	 * Number of clauses
	 */
	int nbClauses;	
	/**
	 * Number of predicates
	 */
	int nbPred;	
	/**
	 * Number of functors
	 */
	int nbFunc;	
	/**
	 * Number of constants
	 */
	int nbConst;	
	/**
	 * Number of atoms
	 */
	int nbAtoms;	
	/**
	 * Maximal clause size
	 */
	int maxClSize;	
	/**
	 * Average clause size
	 */
	int avgClSize;	
	/**
	 * Clause size distribution
	 */
	int[] clSize;
	/**
	 * Maximal term depth
	 */
	int maxTermDepth;	
	/**
	 * SPC
	 */
	String spcCode;	


}
