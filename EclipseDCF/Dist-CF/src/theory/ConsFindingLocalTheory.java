/**

 * 

 */

package theory;



import java.util.List;



import logicLanguage.CNF;

import logicLanguage.IndepClause;




import org.nabelab.solar.ClauseTypes;

import org.nabelab.solar.ExitStatus;

import org.nabelab.solar.Stats;

import org.nabelab.solar.TermTypes;

import org.nabelab.solar.parser.ParseException;



import agLib.masStats.StatCounter;
import agents.PBAgent;



import solarInterface.CFSolver;

import solarInterface.IndepPField;

import solarInterface.SolProblem;


/**
 * @author Viel Charlotte and Gauvain Bourgne
 *
 */

public class ConsFindingLocalTheory //extends SolProblem 
			implements ExitStatus, ClauseTypes, TermTypes {

	
	public ConsFindingLocalTheory(SolProblem theory, int id) {
		this.theory=new SolProblem(theory,true);
		refNumber=id;
	}


	public ConsFindingLocalTheory(int id) {
		refNumber=id;
	}


	public SolProblem getTheory(boolean ref){
		if (ref)
			return theory;
		else
			return new SolProblem(theory,true);
	}

	/**
	 * @param rules the rules to add 
	 */
	public boolean addToTheory(CNF clauses)  {
		if (clauses.isEmpty()) return false;
		boolean added = false;

		for (IndepClause cl:clauses)
			added=added||!theory.getAllClauses().contains(cl);

		theory.addAxioms(clauses);

		if (!added)
			System.out.println(" already in theory of ag" + this.refNumber);
		return added;
	}



	////	TOOLS FOR COMPUTATIONS
	
	public CNF consequenceFinding(IndepPField pField, List<IndepClause> topClauses,
					List<StatCounter<Integer>> ctr, boolean usePrevTopClauses, long deadline) {
		
		SolProblem pb;
		if (!usePrevTopClauses)
			pb=new SolProblem(theory.getAllClauses(),topClauses,pField);
		else{
			CNF tc= new CNF();
			tc.addAll(theory.getTopClauses(true));
			tc.addAll(topClauses);
			pb=new SolProblem(theory.getAxioms(false),tc,pField);
		}
		pb.setDepthLimit(theory.getDepthLimit());	
		CNF result = new CNF();
		boolean incremental=PBAgent.incremental;
		// TODO !!! Attention : trueNewCarc was at false for PB base approaches... Not sure if better. TO TEST.
		boolean trueNewCarc=!(topClauses==null || topClauses.isEmpty());
		CFSolver.solveToIndepClause(pb, deadline, ctr, result, incremental, trueNewCarc);
		return result;
	}

	

	public String toString() {
		return theory.toString();		
	}


//	public static ConsFindingLocalTheory toPBLocalTheory(LocalTheory loc) {

//		return null;

//	}

	//public static trueNewCarc=false;
	
	protected SolProblem theory=new SolProblem();
	public int refNumber=0;
	public static final int CARC = 0;
	public static final int NEW_CARC = 1;	

//	private boolean verbose;

}

