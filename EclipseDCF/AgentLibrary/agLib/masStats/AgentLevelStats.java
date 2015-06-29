package agLib.masStats;

import java.util.List;

import genLib.tools.Pair;

public interface AgentLevelStats {
	
	//records everything that need to be at agent level
	// suppose a number of int and double counter are defined, and have a type
	// precise definition is to be left for application
	// serves as a building block for
	//    -> BasicStatSystem (aggregation of Agent level + own measure)
	//	  -> BasicStatAgentSnapshot
	//		finally Snapshot interpreted as List<Pair<Label,Value>>
	//	  -> BasicStatAgentSnapshotAggregation (aggregation for multiple experiments)
	//		finally this can be done with ExpResult	
	// abstract datas :
	// get Coun
	// - nbMeasures
	// - measures
	
	//regular mesures
		//getter/setter
	public StatCounter<?> getCounter(int ctrCode);
		//get Number. label, listIndex and results compilation
	public Number getMeasure(int measureCode, int measureType);
	public String getFullLabel(int measureCode, int measureType);
	public List<Integer> getAllActiveCtrCodes();
	public List<Pair<String,Number>>getAllResults();
	// summary definition
		//get Number. label, listIndex and results compilation
	public Number get(int summaryMeasureCode);
	public String getSummaryLabel(int summaryMeasurecode);
	public List<Integer> getSummaryCodes();
	
	
}
