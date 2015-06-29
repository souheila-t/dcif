package mars.stats;

import java.util.ArrayList;
import java.util.List;

import logicLanguage.IndepClause;
import masStats.BasicSystemStats;
import masStats.StatCounter;

import tools.Aggregators;


public class MARSystemStats extends BasicSystemStats {
	
	//system measure
	public static final int DSS_THEOREDUNDANCY=1;
	public static final int DSS_MANIFREDUNDANCY=2;
	public static final int DSS_TOTALEXTERNALSUBSUMES=3;
	public static final int SMC_THEOREDUNDANCY=1;
	public static final int SMC_MANIFREDUNDANCY=2;
	public static final int SMC_TOTALEXTERNALSUBSUMES=3;
	public static final int SMC_TOTALEXTERNALSUBSUMESLIT=4;
	public static final int SMC_DEP_ALLAGENT=5;
	public static final int SMC_INC_ALLAGENT=3;
	
	public MARSystemStats(){
		addNewCounter(DSS_THEOREDUNDANCY);
		addNewCounter(DSS_MANIFREDUNDANCY);
		addNewCounter(DSS_TOTALEXTERNALSUBSUMES);
		IndepClause.counter=getSystCounter(DSS_TOTALEXTERNALSUBSUMES);
	}
	
	public MARSystemStats(int nbAgent){
		//initialize counters:
		this();
		// add agent stats
		for (int i=0;i<nbAgent;i++)
			addAgStat(new DiagAgentStats());
	}
	
	
	//TODO need to define Summary : 
	public Number get(int summaryMeasureCode){
		switch(summaryMeasureCode){
		case SMC_THEOREDUNDANCY:return getSystMeasure(DSS_THEOREDUNDANCY, 
								StatCounter.KEY_VALUE);
		case SMC_MANIFREDUNDANCY:return getSystMeasure(DSS_MANIFREDUNDANCY, 
				StatCounter.KEY_VALUE);
		case SMC_TOTALEXTERNALSUBSUMES:return getSystMeasure(DSS_TOTALEXTERNALSUBSUMES, 
				StatCounter.KEY_NB);
		case SMC_TOTALEXTERNALSUBSUMESLIT:return getSystMeasure(DSS_TOTALEXTERNALSUBSUMES, 
				StatCounter.KEY_TOTSIZE);		
		}
		if (summaryMeasureCode>=SMC_DEP_ALLAGENT){
			int code=summaryMeasureCode-SMC_DEP_ALLAGENT;
			int codeCtr=code/SMC_INC_ALLAGENT;
			int type=code%SMC_INC_ALLAGENT;
			switch(type){
			case 0:return getMeasure(codeCtr, StatCounter.KEY_MAXSIZE, Aggregators.AGG_MAX);
			case 1:return getMeasure(codeCtr, StatCounter.KEY_NB, Aggregators.AGG_SUM);
			case 2:return getMeasure(codeCtr, StatCounter.KEY_TOTSIZE, Aggregators.AGG_SUM);
			}
		}
		return -1;
	}
	
	public String getSummaryLabel(int summaryMeasureCode){
		String res="";
		switch(summaryMeasureCode){
		case SMC_THEOREDUNDANCY:return "Theory redundancy";
		case SMC_MANIFREDUNDANCY:return "Manifestation redundancy";
		case SMC_TOTALEXTERNALSUBSUMES:return "Nb external subsumes (clause)";
		case SMC_TOTALEXTERNALSUBSUMESLIT:return "Nb external subsumes (literal)";		
		}
		if (summaryMeasureCode>=SMC_DEP_ALLAGENT){
			int code=summaryMeasureCode-SMC_DEP_ALLAGENT;
			int codeCtr=code/SMC_INC_ALLAGENT;
			int type=code%SMC_INC_ALLAGENT;
			switch(type){
			case 0:return "Global "+agData.get(0).getFullLabel(codeCtr,StatCounter.KEY_MAXSIZE);
			case 1:return ""+agData.get(0).getFullLabel(codeCtr,StatCounter.KEY_NB);
			case 2:return ""+agData.get(0).getFullLabel(codeCtr,StatCounter.KEY_TOTSIZE);
			}
		}
		return res;			
	}
	
	public List<Integer> getSummaryCodes(){
		ArrayList<Integer> res=new ArrayList<Integer>();
		res.add(SMC_THEOREDUNDANCY);
		res.add(SMC_MANIFREDUNDANCY);
		res.add(SMC_TOTALEXTERNALSUBSUMES);
		res.add(SMC_TOTALEXTERNALSUBSUMESLIT);
		if (!agData.isEmpty()){
			List<Integer> allIndexes=agData.get(0).getAllActiveCtrCodes();
			for (Integer i:allIndexes){
				int j=SMC_DEP_ALLAGENT+i*SMC_INC_ALLAGENT;
				res.add(j);
				res.add(j+1);
				res.add(j+2);
			}		
		}
		return res;		
	}

	
	//need to define new types of counter and constructor to add them
	public String getFullLabel(int measureCode, int measureType, int aggCode){
		if (measureType==KEY_SUMMARY)
			return getSummaryLabel(measureCode);
		String res="";
		if (aggCode==KEY_SYSTMEASURE){
			res+=StatCounter.label(measureType);
			switch(measureCode){
			case DSS_THEOREDUNDANCY: res+="Theory redundancy"; break;
			case DSS_MANIFREDUNDANCY: res+="Manifestations redundancy"; break;
			case DSS_TOTALEXTERNALSUBSUMES: res+="Total number of external subsumes"; break;
			}
			return res;
		}
		else if (!agData.isEmpty()){
			res=Aggregators.label(aggCode);
			res+=agData.get(0).getFullLabel(measureCode, measureType).toLowerCase();
		}
		return res;
	}
	
	
	//TODO compute or increment counter when it is needed
	//   - set IndepClause.counter in initialisation
}
