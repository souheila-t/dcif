package mars.stats;

import java.util.ArrayList;
import java.util.List;

import mars.bilateralProtocol.DiagMessage;
import mars.reasoning.LocalTheory;
import masStats.BasicAgentCommStats;
import masStats.StatCounter;

import tools.Aggregators;


import agentCommunicationSystem.Message;
import agentCommunicationSystem.SystemMessage;
import agentCommunicationSystem.protocols.consensus.GlobalWeightedMessage;

public class DiagAgentStats extends BasicAgentCommStats {
	//stats on messages <1000 ->2000>
	//code sent 1000 +typeClass*100 + typeMessage
	//code received 2000 +typeClass*100 + typeMessage
	public final int CLASSKEY_SYSTEMMESSAGE=0;
	public final int CLASSKEY_DIAGMESSAGE=1;
	public final int CLASSKEY_GOLBALWEIGHTEDMESSAGE=2;
	//stats on computations step
	// codeCtr 0-... <1000 
	public static final int BCS_NBCSTEP=0;
	public static final int BCS_NBCSTEP_HYP=1;
	public static final int BCS_NBCSTEP_EXTENDEDHYP=2;
	public static final int BCS_NBCSTEP_OWNCTX=3;
	public static final int BCS_NBCSTEP_EXTERNALCTX=4;
	public static final int BCS_NBCSTEP_COVEREDMANIF=5;
	public static final int BCS_NBCSTEP_PROVEMANIF=6;
	public static final int BCS_NBCSTEP_ADDMANIF=7;
	public static final int BCS_NBCSTEP_ADDTHEORY=8;
	public static final int BCS_NBCSTEP_HYPNC=9;
	public static final int BCS_NBCSTEP_EXTENDEDHYPNC=10;
	public static final int BCS_NBCSTEP_OWNCTXNC=11;
	public static final int BCS_NBCSTEP_EXTERNALCTXNC=12;
	public static final int NBCSTEP_SIZE=13;
	
	public DiagAgentStats(){
		//initialize counters:
		for (int i=0;i<NBCSTEP_SIZE;i++){
			computationStepsStat.add(new StatCounter<Integer>(i));
		}
	}
	
	
	//New Classes of Messages to override for applications
	@SuppressWarnings("unchecked")
	public Class<? extends Message> getClass(int classKey) throws ClassNotFoundException{
		switch(classKey){
		case CLASSKEY_SYSTEMMESSAGE:
			return SystemMessage.class;
		case CLASSKEY_DIAGMESSAGE:
			return DiagMessage.class;
		case CLASSKEY_GOLBALWEIGHTEDMESSAGE:
			return GlobalWeightedMessage.class;
		}
		return (Message.class);
	}
	@SuppressWarnings("unchecked")
	public int getClassKey(Class<? extends Message> cl) {
		if (cl.equals(SystemMessage.class))
				//cl.newInstance() instanceof SystemMessage)
			return CLASSKEY_SYSTEMMESSAGE;
		if (cl.equals(DiagMessage.class))
			return CLASSKEY_DIAGMESSAGE;
		if (cl.equals(GlobalWeightedMessage.class))
			return CLASSKEY_GOLBALWEIGHTEDMESSAGE;
		return -1;
	}

	//MUST NOT CALL getCounter(n) with n=codeCombinedCtr, otherwise, infinite recursion
	public List<StatCounter<?>> getListForCombinedCtr(int codeCombinedCtr){
		List<StatCounter<?>> res=new ArrayList<StatCounter<?>>();
		switch(codeCombinedCtr){
		case BCS_NBCSTEP:
			res.add(getCounter(BCS_NBCSTEP_ADDMANIF));
			res.add(getCounter(BCS_NBCSTEP_ADDTHEORY));
			res.add(getCounter(BCS_NBCSTEP_COVEREDMANIF));
			res.add(getCounter(BCS_NBCSTEP_EXTENDEDHYP));
			res.add(getCounter(BCS_NBCSTEP_EXTENDEDHYPNC));
			res.add(getCounter(BCS_NBCSTEP_EXTERNALCTX));
			res.add(getCounter(BCS_NBCSTEP_EXTERNALCTXNC));
			res.add(getCounter(BCS_NBCSTEP_HYP));
			res.add(getCounter(BCS_NBCSTEP_HYPNC));
			res.add(getCounter(BCS_NBCSTEP_OWNCTX));
			res.add(getCounter(BCS_NBCSTEP_OWNCTXNC));
			res.add(getCounter(BCS_NBCSTEP_PROVEMANIF));
			break;
		}
		return res;
	}
	
	// new Counters and related 'full' measures	
	public StatCounter<?> getCounter(int codeCtr){
		if (isMessageMeasureCode(codeCtr))
			return super.getCounter(codeCtr);
		if (codeCtr==BCS_NBCSTEP)
			computationStepsStat.set(0, 
					StatCounter.merging(0, getListForCombinedCtr(BCS_NBCSTEP),
							Aggregators.AGG_AVERAGE));
		if (codeCtr<computationStepsStat.size())
			return computationStepsStat.get(codeCtr);
		return null;
	}
	
	public Number getMeasure(int codeCtr, int typeMeasure){
		if (isMessageMeasureCode(codeCtr))
			return super.getMeasure(codeCtr,typeMeasure);
		if (codeCtr<computationStepsStat.size())
			return getCounter(codeCtr).get(typeMeasure);
		return -1;
	}
	
	public List<Integer> getAllActiveCtrCodes() {
		List<Integer> res=super.getAllActiveCtrCodes();		
		//add at the start the new counters
		for (int i=0;i<NBCSTEP_SIZE;i++)
			res.add(i, i);
		return res;
	}
	
	public String getFullLabel(int codeCtr, int measureType) {
		if (measureType==KEY_SUMMARY)
			return getSummaryLabel(codeCtr);
		if (isMessageMeasureCode(codeCtr))
			return super.getFullLabel(codeCtr, measureType);
		String res="";
		res+=StatCounter.label(measureType);
		// get messageKey name
		switch(codeCtr){
		case BCS_NBCSTEP: return res+"solve step";
		case BCS_NBCSTEP_HYP: return res+"solve for hyp";
		case BCS_NBCSTEP_EXTENDEDHYP: return res+"solve for extended hyp";
		case BCS_NBCSTEP_OWNCTX: return res+"solve for own context";
		case BCS_NBCSTEP_EXTERNALCTX: return res+"solve for external context";
		case BCS_NBCSTEP_COVEREDMANIF: return res+"solve for covered manif.";
		case BCS_NBCSTEP_PROVEMANIF: return res+"solve for prove manif";
		case BCS_NBCSTEP_ADDMANIF: return res+"solve for add manif";
		case BCS_NBCSTEP_ADDTHEORY: return res+"solve for addToTheory";
		case BCS_NBCSTEP_HYPNC: return res+"solve for hyp (NC)";
		case BCS_NBCSTEP_EXTENDEDHYPNC: return res+"solve for extended hyp (NC)";
		case BCS_NBCSTEP_OWNCTXNC: return res+"solve for own context (NC)";
		case BCS_NBCSTEP_EXTERNALCTXNC: return res+"solve for external context (NC)";
		}
		return res;
	}

	public String getSummaryLabel(int summaryMeasureCode) {
		//TODO
		return super.getSummaryLabel(summaryMeasureCode);
	}
	public Number get(int summaryMeasureCode) {
		//TODO
		return super.get(summaryMeasureCode);
	}	
	public List<Integer> getSummaryCodes() {
		//TODO
		return super.getSummaryCodes();
	}

	
	// APPL SPECIFICS
	public void setCountersAT(LocalTheory th){
		th.setStatCounters(
				getCounter(BCS_NBCSTEP_HYP),
				getCounter(BCS_NBCSTEP_HYPNC),
				getCounter(BCS_NBCSTEP_EXTENDEDHYP),
				getCounter(BCS_NBCSTEP_EXTENDEDHYPNC),
				getCounter(BCS_NBCSTEP_OWNCTX),
				getCounter(BCS_NBCSTEP_OWNCTXNC));
	}

	//data storing
	private List<StatCounter<Integer>> computationStepsStat=new ArrayList<StatCounter<Integer>>();

}
