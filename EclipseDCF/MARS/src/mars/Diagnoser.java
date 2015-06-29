package mars;

import agLib.agentCommunicationSystem.CanalComm;

public interface Diagnoser<Hypothesis,Context,Rules,Manifestations> {
		//GETTERS
	public Hypothesis getHypothesis(CanalComm ag);
	public Context getContext(CanalComm ag);
		//SETTERS
	//agCtx is the agent that computed the adopted context together with the agent a0 invoking the 
	// method (even if the hypothesis was not computed by it, but by a0)
	public void adoptHyp(Hypothesis hypothesis, Context context, CanalComm from);
	public void adoptCtx(Hypothesis hypothesis, Context context, CanalComm producedWith);
	//return true iff the modification of the theory caused a change of fav Hypothesis
	public boolean addToTheory(Rules ruleSet, CanalComm from);
	public boolean addManifestations(Manifestations obsSet);
	//remover
	//public void removeRulesSubsumedByAny(Rules excludedRules);
		//COMPUTATIONS
	public boolean computeContext(Hypothesis hyp,
			Context previouslyComputedClauses, 
			Context newClauses, 
			Context result, boolean proof,
			CanalComm sender);
	public boolean getUnexplainedManif(Hypothesis hypothesis, Manifestations unexplObs);
	public Rules getProof(Manifestations manif,boolean fullExplain);
		//TESTS ON HYP
	// criterion to stop searching for hypothesis -> find minimal hyp if this always return false
	public boolean sufficientHyp(Hypothesis hypothesis);
	public boolean betterThanOwnHyp(Hypothesis hypothesis);
	//test on theory
	//public boolean isConsequence(Rules queriedFacts);
		//OTHER
	public void blockHyp(Hypothesis hyp, boolean temporary);
	public void unBlockAllHyp();
	/*	
	
	public boolean getRefutation(List<Clause> hypothesis, List<Clause> refut);
	public boolean getUnexplainedObs(List<Clause> hypothesis, List<Clause> unexplObs, boolean all);
	
	
	public void adoptHyp(List<Clause> hypothesis);
	public void adoptHyp(List<Clause> hypothesis, List<Clause> context);
	public void adoptContext(List<Clause> context);
	public boolean pruneTheory(Clause exclude);
	public boolean hasClause(Clause cl);
	
	*/
}
