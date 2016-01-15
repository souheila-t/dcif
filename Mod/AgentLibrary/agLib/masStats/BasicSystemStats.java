package agLib.masStats;

import java.util.ArrayList;
import java.util.List;

import genLib.tools.Aggregators;
import genLib.tools.Pair;



public class BasicSystemStats {
	public static final int KEY_SYSTMEASURE=-1;
	public static final int KEY_SUMMARY=-100;

	public Number getAgentAggMeasure(int codeCtrAgMeasure, int typeMeasure, int codeAggregator){
		List<Number> agMeasures=getAgMeasures(codeCtrAgMeasure, typeMeasure);
		Number res=Aggregators.aggregate(agMeasures, codeAggregator);
		return res;
	}
	
	// typeMeasure is either (Nb, TotSize, MaxSize, MinSize) or Summary
	public List<Number> getAgMeasures(int codeCtr, int typeMeasure){
		List<Number> res=new ArrayList<Number>();
		
		for (AgentLevelStats agStat:agData){
			Number n=agStat.getMeasure(codeCtr,typeMeasure);
			res.add(n);
		}
		return res;
	}
	
	//codeAggregator should be -1 if it is a systemMeasure
	public Number getMeasure(int ctrCode, int typeMeasure, int codeAggregator){
		if (typeMeasure==KEY_SUMMARY)
			return get(ctrCode);
		if (codeAggregator==KEY_SYSTMEASURE)
			return getSystMeasure(ctrCode,typeMeasure);
		else
			return getAgentAggMeasure(ctrCode, typeMeasure, codeAggregator);
	}
	
	public Number get(int summaryMeasureCode){
		//by default, 
		//from 1000 and onwards, give the average of Double value and the sum of Long values
		// for the agents summary values
		if (summaryMeasureCode>1000 && !agData.isEmpty()){
			int agSummCode=summaryMeasureCode-1000;
			List<Number> values=new ArrayList<Number>();
			for (AgentLevelStats agStat:agData){
				values.add(agStat.get(agSummCode));
			}
			int codeAgg;
			if (Aggregators.isInt(values.get(0)))
				codeAgg=Aggregators.AGG_SUM;
			else
				codeAgg=Aggregators.AGG_AVERAGE;
			Number res=Aggregators.aggregate(values, codeAgg);
			return res;
		}
		return -1;			
	}	
	
	//override to add own indexes
	public List<Integer> getSummaryCodes(){
		ArrayList<Integer> res=new ArrayList<Integer>();
		if (!agData.isEmpty()){
			List<Integer> summIndexes=agData.get(0).getSummaryCodes();
			for (Integer i:summIndexes)
				res.add(1000+i);
		}
		return res;		
	}

	public String getFullLabel(int measureCode, int measureType, int aggCode){
		if (measureType==KEY_SUMMARY)
			return getSummaryLabel(measureCode);
		String res="";
		if (aggCode==KEY_SYSTMEASURE){
			// get measure name // to override
			res+=StatCounter.label(measureType);
			switch(measureCode){
			}		
		}
		else if (!agData.isEmpty()){
			res=Aggregators.label(aggCode);
			res+=agData.get(0).getFullLabel(measureCode, measureType).toLowerCase();
		}
		return res;
	}
	
	public String getSummaryLabel(int summaryMeasureCode){
		String res="";
		if (summaryMeasureCode>=1000 && !agData.isEmpty()){
			int summCodeAg=summaryMeasureCode-1000;			
			if (Aggregators.isInt(agData.get(0).get(summCodeAg)))
				res+=Aggregators.label(Aggregators.AGG_SUM);
			else
				res+=Aggregators.label(Aggregators.AGG_AVERAGE);
			res+=agData.get(0).getSummaryLabel(summCodeAg);
		}
		return res;			
	}
	
	//include all aggregation of agData
	// returns a pair <codeCtr, codeAgg>
	public List<Pair<Integer,Integer>> getAllActiveCtrCodes(){
		ArrayList<Pair<Integer,Integer>> res=new ArrayList<Pair<Integer,Integer>>();
		for (StatCounter<Integer> ctr:systData)
			res.add(Pair.create(ctr.key,KEY_SYSTMEASURE));
		if (!agData.isEmpty()){
			List<Integer> ctrList=agData.get(0).getAllActiveCtrCodes();
			for (Integer i:ctrList)
				for (Integer j:Aggregators.getAllCode())
					res.add(Pair.create(i, j));
		}
		return res;			
	}

	public StatCounter<?> getSystCounter(int codeCtr){
		for (StatCounter<Integer> ctr:systData)
			if (ctr.getKey().equals(codeCtr))
				return ctr;
		return null;
	}
	
	public Number getSystMeasure(int code,int typeMeasure){
		if (getSystCounter(code)!=null)
			return getSystCounter(code).get(typeMeasure);
		return -1;
	}
	
	
	public List<Pair<String,Number>>getAllResults(){
		List<Pair<String,Number>> res=new ArrayList<Pair<String,Number>>();
		for (Pair<Integer,Integer> p:getAllActiveCtrCodes()){
			int ctrCode=p.getLeft();
			int aggCode=p.getRight();
			StatCounter<?> ctr=null;
			if (aggCode==KEY_SYSTMEASURE)
				ctr=this.getSystCounter(ctrCode);
			else if (!agData.isEmpty())
				ctr=agData.get(0).getCounter(ctrCode);
			if (ctr==null) continue;
			if (ctr.hasValue)
				res.add(Pair.create(this.getFullLabel(ctrCode, StatCounter.KEY_VALUE, aggCode), 
									getMeasure(ctrCode,StatCounter.KEY_VALUE,aggCode)));
			if (ctr.hasNb)
				res.add(Pair.create(this.getFullLabel(ctrCode, StatCounter.KEY_NB, aggCode), 
						getMeasure(ctrCode,StatCounter.KEY_NB,aggCode)));
			if (ctr.hasSize){
				res.add(Pair.create(this.getFullLabel(ctrCode, StatCounter.KEY_TOTSIZE, aggCode), 
						getMeasure(ctrCode,StatCounter.KEY_TOTSIZE,aggCode)));
				res.add(Pair.create(this.getFullLabel(ctrCode, StatCounter.KEY_MAXSIZE, aggCode), 
						getMeasure(ctrCode,StatCounter.KEY_MAXSIZE,aggCode)));
				res.add(Pair.create(this.getFullLabel(ctrCode, StatCounter.KEY_MINSIZE, aggCode), 
						getMeasure(ctrCode,StatCounter.KEY_MINSIZE,aggCode)));
			}	
		}
		return res;
	}
	
	public List<Pair<String,Number>>getSummaryResults(){
		List<Pair<String,Number>> res=new ArrayList<Pair<String,Number>>();
		List<Integer> list=this.getSummaryCodes();
		
		for (Integer summCode:list){
			String label=getSummaryLabel(summCode);
			Number val=get(summCode);
			res.add(Pair.create(label,val));					
		}
		return res;
	}
	
	
	
	public void addNewCounter(Integer code){
		if (getSystCounter(code)==null)
			systData.add(new StatCounter<Integer>(code));
	}
	
	public void addAgStat(AgentLevelStats bsa){
		agData.add(bsa);
	}
	
	
	
	//agent data
	protected List<AgentLevelStats> agData= new ArrayList<AgentLevelStats>();
	protected List<StatCounter<Integer>> systData=new ArrayList<StatCounter<Integer>>();
}
