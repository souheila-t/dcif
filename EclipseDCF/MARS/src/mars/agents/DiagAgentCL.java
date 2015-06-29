package mars.agents;

import java.io.FileNotFoundException;

import logicLanguage.CNF;
import logicLanguage.CommLanguage;
import logicLanguage.UnitClauseCNF;
import mars.ConsensusDiagnoserAgent;
import mars.reasoning.FullHypothesis;
import mars.reasoning.LocalTheory;
import mars.stats.DiagAgentStats;

import org.nabelab.solar.parser.ParseException;

import solarInterface.IndepPField;
import agentCommunicationSystem.CanalComm;
import agentCommunicationSystem.Network;

public class DiagAgentCL extends DiagAgentBasic implements ConsensusDiagnoserAgent<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF>{
	
	////CONSTRUCTORS
	
	public DiagAgentCL(String name,LocalTheory theory, 
			CanalComm cSyst,Network net, DiagAgentStats stats, boolean incremental){
		super(name,theory,cSyst,net,stats,incremental);
		commLanguage=new CommLanguage(this);
		
	}
	
	//// RE-IMPLEMENTATION OF ConsensusDiagnoser

	//GETTERS
	
	/**
	 * @return a copy of the agent's favorite hypothesis
	 */
	public UnitClauseCNF getHypothesis(CanalComm otherAgent){
		UnitClauseCNF hyp=new UnitClauseCNF();
		hyp.directAddAll(agentTheory.getFavHypothesis());
		
		if (!agentTheory.hasPartialHyp())
			return hyp;
		IndepPField commLang=null;
		if (otherAgent!=null)
			commLang=getRefToCommLanguage().getLanguage(otherAgent);
		try {
			hyp.clear();
			hyp=agentTheory.getFavPartialHypothesis(commLang);
		} catch (FileNotFoundException e) {e.printStackTrace();} 
		  catch (ParseException e) {e.printStackTrace();}
		return hyp;
	}
	
	/**
	 * @return a copy of the agent's favorite hypothesis
	 */
	public CNF getContext(CanalComm target){
		CNF adaptedCtx=CNF.copy(getContext());
		IndepPField restr=getRefToCommLanguage().getLanguage(target);
		try {
			adaptedCtx.restrictToClausesPartlyInPField(restr);
		} catch (ParseException e) {e.printStackTrace();}
		return adaptedCtx;
	}

		//SETTERS
	public void adoptHyp(UnitClauseCNF hypothesis, CNF context, CanalComm from){
	//must avoid to override previous context as it may contains informations
	// from other agents which are not in the language of partialContext
	// Also need to recompute the context to see if new informations from partial Context
	// have other consequence in the agent gull language
		agentTheory.adoptHypWithContext(hypothesis, context,false, 
				getRefToCommLanguage().getLanguage(from));
	}

	public void adoptCtx(UnitClauseCNF hypothesis, CNF context, CanalComm from){
		//must avoid to override previous context as it may contains informations
		// from other agents which are not in the language of partialContext
		// Also need to recompute the context to see if new informations from partial Context
		// have other consequence in the agent gull language
			agentTheory.adoptHypWithContext(hypothesis, context, true,
					getRefToCommLanguage().getLanguage(from));
	}

	
	
	//COMPUTATIONS
	public boolean computeContext(UnitClauseCNF hyp,
			CNF previouslyComputedClauses, 
			CNF newClauses, 
			CNF result, boolean proof, 
			CanalComm sender) {
		boolean sat=false;
		try {
			// define pf as commLanguage + literals of hyp
			IndepPField pf=commLanguage.getLanguage(sender);
			pf=pf.addToLiterals(previouslyComputedClauses.getVocabulary());
			pf=pf.addToLiterals(hyp.getVocabulary());
			// compute context
			sat=agentTheory.computeContext(previouslyComputedClauses, newClauses, pf, 
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_EXTERNALCTX),
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_EXTERNALCTXNC),proof,hyp,
					result);
			CNF oldRes=CNF.copy(hyp);
			oldRes.retainAll(result);
			result.restrictToClausesPartlyInPField(commLanguage.getLanguage(sender));
			result.addAll(oldRes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sat;
	}

	
	
	
	////GETTERS AND SETTERS
	
	public CommLanguage getRefToCommLanguage() {
		return commLanguage;
	}

	
	
	
	
	
	//// ATTRIBUTES
	
	protected CommLanguage commLanguage;
	


}
