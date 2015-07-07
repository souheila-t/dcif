package old_solarInterface;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import old_logicLanguage.CNF;
import old_logicLanguage.IndepClause;

import org.nabelab.solar.CFP;
import org.nabelab.solar.Conseq;
import org.nabelab.solar.Env;
import org.nabelab.solar.ExitStatus;
import org.nabelab.solar.SOLAR;
import org.nabelab.solar.Strategy;
import org.nabelab.solar.parser.ParseException;

import agLib.masStats.StatCounter;




public class CFSolver {
	// complementary SOLAR stats :
	public static final int SOLST_CPU_TIME=-1;
	public static final int SOLST_INF=-2;

	public static int solveToIndepClause(SolProblem pb, long deadline, List<StatCounter<Integer>> ctr, 
			Collection<IndepClause> resultingCons, boolean incremental, boolean trueNewC){
		List<Conseq> tempRes=new ArrayList<Conseq>();
		int status;
		if (!incremental){
			if(verbose) System.out.println("Starting SolveToIndepClause...");
			Env env=new Env();
			CFP problem=pb.toCFP(env);
			if(verbose) System.out.println("Launching solver...");
			if(verbose) System.out.println(problem);
			solve(env,problem,pb.getDepthLimit(),deadline, ctr);
		//	problem.getConseqSet().validate();
			if(verbose) System.out.println("Solver finished.");
			tempRes=problem.getConseqSet().get();
			status=problem.getStatus();
		}
		else {
			status=incSolve(pb,deadline, ctr,tempRes);
		}
		if(verbose) System.out.println("Converting to IndepClause...");
		List<IndepClause> tempRes2=new ArrayList<IndepClause>();
		for (Conseq cons:tempRes){
			tempRes2.add(new IndepClause(cons));
		}
		if (trueNewC && !pb.getTopClauses(true).isEmpty()){
			if(verbose) System.out.println("Pruning NewCarc...");
			//CNF res=pruneNewCarc(pb.getAxioms(true), tempRes2, ctr, typeStat);
			CNF res=pruneNewCarc(pb.getAxioms(true), pb.getDepthLimit(), deadline, tempRes2, null);
			resultingCons.addAll(res);
		}			
		else 
			resultingCons.addAll(tempRes2);
		if(verbose) System.out.println("SolveToIndepClause Finished!");
		return status;
	}
	
	public static int incSolve(SolProblem pb, long deadline, List<StatCounter<Integer>> ctr, 
			Collection<Conseq> resultingCons){
		int status=ExitStatus.SATISFIABLE;
		List<IndepClause> top=pb.getTopClauses(false);
		List<IndepClause> base=pb.getAxioms(false);
		if (top.isEmpty()){
			top=base;
			base=new ArrayList<IndepClause>();
		}
		
		for (int i=0;i<top.size();i++) {
			IndepClause cl=top.get(i);
			Env env=new Env();
			CFP problem=new SolProblem(base,CNF.singleCNF(cl),pb.getPField()).toCFP(env);
			solve(env, problem, pb.getDepthLimit(), deadline, ctr);
			problem.getConseqSet().validate();
			
			List<Conseq> tempRes=problem.getConseqSet().get();
			resultingCons.addAll(tempRes);
			if (problem.getStatus()==ExitStatus.UNSATISFIABLE){
				resultingCons.retainAll(tempRes); 
				status=ExitStatus.UNSATISFIABLE;
				return status;
			}
			if (problem.getStatus()==ExitStatus.UNKNOWN)
				status=ExitStatus.UNKNOWN;
			base.add(cl);
		}
		return status;
	}
	
	
	public static CNF pruneNewCarc( CNF axioms, int depthLimit, long deadline, List<? extends IndepClause> conseq,
			List<StatCounter<Integer>> ctr)  {
		CNF newcarc = new CNF();
    	    		
    	for (IndepClause ncl : conseq){
    		if (!isConsequence(ncl,axioms,depthLimit,deadline, ctr))
    				newcarc.add(ncl);
    	}
    	return newcarc;
	}

	//remove consequences that are consequences of the others
	public static void pruneConseqSet( List<? extends IndepClause> conseqList, int depthLimit, long deadline, 
			List<StatCounter<Integer>> ctr)  {
    	int saveMaxLength=lengthLimit;
    	lengthLimit=1;
    	for (int i=conseqList.size()-1;i>=0;i--){
    		IndepClause cons=conseqList.get(i);
    		List<IndepClause> otherCons=new ArrayList<IndepClause>(conseqList);
    		otherCons.remove(i);
    		if (isConsequence(cons,otherCons, depthLimit, deadline, ctr))
    				conseqList.remove(i);
    	}
    	lengthLimit=saveMaxLength;
	}

	//remove clauses of toPrune that are consequences of base or (if included is true) base U toPrune 
	public static List<IndepClause> pruneClauseSetFromCons( Collection<? extends IndepClause> toPrune,Collection<? extends IndepClause> base, boolean include, 
			int depthLimit, long deadline, List<StatCounter<Integer>> ctr)  {
    //	int saveMaxLength=lengthLimit;
    //	lengthLimit=1;
    	List<IndepClause> toRetain=new ArrayList<IndepClause>();
    	List<IndepClause> theory=new ArrayList<IndepClause>();
    	if (base!=null) theory.addAll(base);
    	if (include) theory.addAll(toPrune);
    	for (IndepClause cl:toPrune){
    		if (include)
    			theory.remove(cl);
    		if (!isConsequence(cl,theory, depthLimit, deadline, ctr)){
    			toRetain.add(cl);
    			if (include) theory.add(cl);
    		}
    	}
    //	lengthLimit=saveMaxLength;
    	return toRetain;
	}
	
	
	public static boolean isConsequence(IndepClause goal, 
			List<? extends IndepClause> axioms, int depthLimit, long deadline, List<StatCounter<Integer>> ctr){
		CNF topClauses=new CNF();
		topClauses.addAll(goal.getNegation(true));
		
		Env env=new Env();
		CFP problem=new SolProblem(axioms,topClauses,new IndepPField()).toCFP(env);
		
		solve(env, problem, depthLimit, deadline, ctr);
		
		return (problem.getStatus()==ExitStatus.UNSATISFIABLE);
	}

	
	
	
	
	public static void solve(Env env, CFP problem, int depthLimit, long deadline, List<StatCounter<Integer>> statCtr){
		SOLAR s;
		long timeLimit= (deadline- System.currentTimeMillis());
		if (deadline!=-1){
			if (timeLimit<0)
				timeLimit=50;
			problem.getOptions().setTimeLimit(timeLimit);
		//	System.out.println("Time Limit :"+ timeLimit);
		}
		
			
		if (depthLimit>=0)
			problem.getOptions().setDepthLimit(depthLimit);
		//problem.getOptions().setMaxLenConseqs(lengthLimit);
		problem.getOptions().setStrategy(Strategy.DF);
	/*	while (busy){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		busy=true; */
	//	notifyAll();
		if (verbose) System.out.println(">>>>>>>>>>>> Solving at depth : "+depthLimit+"( real "+problem.getOptions().getDepthLimit()+")");
		s = new SOLAR(env, problem);          // Create a SOLAR system.
		List<Long> measures=new ArrayList<Long>();
		if (statCtr!=null)
			for (StatCounter<Integer> ctr:statCtr){
				int k=ctr.getKey();
				measures.add(getSolStat(s,k));
		}
		try {
			s.solve();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (statCtr!=null)
			for (int i=0;i<statCtr.size();i++){		
				int k=statCtr.get(i).getKey();
				measures.set(i,getSolStat(s,k)-measures.get(i));
				statCtr.get(i).inc(measures.get(i).intValue());
		}
		
		if (verbose) System.out.println(">>>>>>>>>>>> Solve - measure : "+measures);
	//	busy=false;
	//	notifyAll();
	}
	
	public static Long getSolStat(SOLAR s, int key){
		if (key>0)
			return s.getStats().get(key);
		switch (key){
		case SOLST_CPU_TIME:
			return s.getCPUTime();
		case SOLST_INF:
			return s.getStats().inf();
		}
		return new Long(0);
	}
	
	public static boolean verbose=true;
	public static int lengthLimit=-1;
	
	
}
