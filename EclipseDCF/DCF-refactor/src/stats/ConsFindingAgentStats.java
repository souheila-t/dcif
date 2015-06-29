/**

 * 

 */

package stats;



import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Stats;

import agLib.masStats.BasicAgentCommStats;
import agLib.masStats.StatCounter;

import solarInterface.CFSolver;





/**

 * @author Viel Charlotte

 *

 */

public class ConsFindingAgentStats extends BasicAgentCommStats {

//	public final int SMC_NBSENTMESSAGES=1;
//	public final int SMC_NBRECEIVEDMESSAGES=2;
//	public final int SMC_TOTSIZESENT=3;
//	public final int SMC_TOTSIZERECEIVED=4;
	public static final int NB_INHERITED_SMC=4;
	
	// DCF_ constants serve as code for counter as well as favored summary measure for said counter
	public static final int DCF_INFERENCESTEPS = NB_INHERITED_SMC + 1;
	public static final int DCF_NBCLAUSESSENT = NB_INHERITED_SMC + 2;
	public static final int DCF_NBCONSEQSENT = NB_INHERITED_SMC + 3;
	public static final int DCF_SOL_CPU_TIME = NB_INHERITED_SMC + 4;
	public static final int DCF_NB_EXTENSIONS = NB_INHERITED_SMC + 5;
	public static final int DCF_NB_SKIP = NB_INHERITED_SMC + 6;
	public static final int DCF_NB_FACTORING = NB_INHERITED_SMC + 7;
	public static final int DCF_NB_MERGE = NB_INHERITED_SMC + 8;
	public static final int DCF_NB_REDUCTION = NB_INHERITED_SMC + 9;
	public static final int DCF_NB_ID_REDUCTION = NB_INHERITED_SMC + 10;

	public static final int INF = 100000000;

	protected static final int IND_CPU_TIME = 0;
	protected static final int IND_INF = 1;
	protected static final int IND_EXTENSION = 2;
	protected static final int IND_SKIP = 3;
	protected static final int IND_FACTORING = 4;
	protected static final int IND_MERGE = 5;
	protected static final int IND_REDUCTION = 6;
	protected static final int IND_ID_REDUCTION = 7;
	

	/**
	 * 
	 */

	public ConsFindingAgentStats() {
		super();
		receivedMessages.maxSize = INF;
		sentMessages.maxSize = INF;
		solarStats.add(new StatCounter<Integer>(CFSolver.SOLST_CPU_TIME));  
		solarStats.add(new StatCounter<Integer>(CFSolver.SOLST_INF));
		solarStats.add(new StatCounter<Integer>(Stats.EXTENSION));
		solarStats.add(new StatCounter<Integer>(Stats.SKIP));
		solarStats.add(new StatCounter<Integer>(Stats.FACTORING));
		solarStats.add(new StatCounter<Integer>(Stats.MERGE));
		solarStats.add(new StatCounter<Integer>(Stats.REDUCTION));
		solarStats.add(new StatCounter<Integer>(Stats.IDENTICAL_REDUCTION));		
	}
	

	public StatCounter<?> getCounter(int codeCtr){
		StatCounter<?> res = super.getCounter(codeCtr);
		if(res == null) {
			switch(codeCtr) {
			case DCF_INFERENCESTEPS : return solarStats.get(IND_INF);
			case DCF_NBCLAUSESSENT : return  clausesSent;
			case DCF_NBCONSEQSENT : return  conseqSent;
			case DCF_SOL_CPU_TIME: return solarStats.get(IND_CPU_TIME);
			case DCF_NB_EXTENSIONS: return solarStats.get(IND_EXTENSION);
			case DCF_NB_SKIP: return solarStats.get(IND_SKIP);
			case DCF_NB_FACTORING: return solarStats.get(IND_FACTORING);
			case DCF_NB_MERGE: return solarStats.get(IND_MERGE);
			case DCF_NB_REDUCTION: return solarStats.get(IND_REDUCTION);
			case DCF_NB_ID_REDUCTION: return solarStats.get(IND_ID_REDUCTION);

			}
		} else if(res != null) {
			return res;
		}
		return null;		
	}

	public Number get(int summaryMeasureCode) {
		switch (summaryMeasureCode){
		case SMC_NBSENTMESSAGES:
			return getMeasure(CTR_SENT,StatCounter.KEY_NB);
		case SMC_NBRECEIVEDMESSAGES:
			return getMeasure(CTR_RECEIVED,StatCounter.KEY_NB);
		case SMC_TOTSIZESENT:
			return getMeasure(CTR_SENT,StatCounter.KEY_TOTSIZE);
		case SMC_TOTSIZERECEIVED:
			return getMeasure(CTR_RECEIVED,StatCounter.KEY_TOTSIZE);
		case DCF_INFERENCESTEPS : return solarStats.get(IND_INF).get(StatCounter.KEY_TOTSIZE);
		case DCF_NBCLAUSESSENT : return  clausesSent.get(StatCounter.KEY_NB);
		case DCF_NBCONSEQSENT : return  conseqSent.get(StatCounter.KEY_NB);
		case DCF_SOL_CPU_TIME: return solarStats.get(IND_CPU_TIME).get(StatCounter.KEY_TOTSIZE);
		case DCF_NB_EXTENSIONS: return solarStats.get(IND_EXTENSION).get(StatCounter.KEY_TOTSIZE);
		case DCF_NB_SKIP: return solarStats.get(IND_SKIP).get(StatCounter.KEY_TOTSIZE);
		case DCF_NB_FACTORING: return solarStats.get(IND_FACTORING).get(StatCounter.KEY_TOTSIZE);
		case DCF_NB_MERGE: return solarStats.get(IND_MERGE).get(StatCounter.KEY_TOTSIZE);
		case DCF_NB_REDUCTION: return solarStats.get(IND_REDUCTION).get(StatCounter.KEY_TOTSIZE);
		case DCF_NB_ID_REDUCTION: return solarStats.get(IND_ID_REDUCTION).get(StatCounter.KEY_TOTSIZE);
		}
		return null;
	}
	
	public List<StatCounter<Integer>> getSolarCtrList(){
		return solarStats;
	}
	
	public StatCounter<Integer> clausesSent = new StatCounter<Integer>(DCF_NBCLAUSESSENT);
	public StatCounter<Integer> conseqSent = new StatCounter<Integer>(DCF_NBCONSEQSENT);
	public List<StatCounter<Integer>> solarStats = new ArrayList<StatCounter<Integer>>();
	
}

