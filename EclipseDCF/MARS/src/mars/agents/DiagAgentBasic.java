package mars.agents;

import java.io.FileNotFoundException;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.UnitClauseCNF;
import mars.ConsensusDiagnoserAgent;
import mars.bilateralProtocol.MarsLocalProtocol_ECAI;
import mars.bilateralProtocol.MarsLocalProtIncCtxComp;
import mars.reasoning.FullHypothesis;
import mars.reasoning.LocalTheory;
import mars.stats.DiagAgentStats;

import org.nabelab.solar.parser.ParseException;

import solarInterface.IndepPField;
import agentCommunicationSystem.BasicAgent;
import agentCommunicationSystem.CanalComm;
import agentCommunicationSystem.CommunicationModule;
import agentCommunicationSystem.Network;
import agentCommunicationSystem.protocols.LocalProtocol;
import agentCommunicationSystem.protocols.consensus.HypTrackingWeightedContinuousGbProtocol;

public class DiagAgentBasic extends BasicAgent implements 
		ConsensusDiagnoserAgent<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF>{

	////CONSTRUCTORS
	
	public DiagAgentBasic(String name,LocalTheory theory, 
			CanalComm cSyst,Network net, DiagAgentStats stats, boolean incremental){
		agentTheory=theory;
		this.stats=stats;
		stats.setCountersAT(agentTheory);
		//agentThread = null;
		this.name=name;
		cAg=new CommunicationModule(new CanalComm(this), cSyst, net, stats);
		//DiagBasicLocalProtocol baseLocalProt=new DiagBasicLocalProtocol(cAg,this);
		//DiagIncremECAIProtocol baseLocalProt=new DiagIncremECAIProtocol(cAg,this);
		LocalProtocol baseLocalProt;
		if (incremental)
			baseLocalProt=new MarsLocalProtIncCtxComp(cAg,this);
		else
			baseLocalProt=new MarsLocalProtocol_ECAI(cAg,this);
		
		HypTrackingWeightedContinuousGbProtocol<FullHypothesis> gbProtocol=
			new HypTrackingWeightedContinuousGbProtocol<FullHypothesis>(cAg,this,baseLocalProt);
		((MarsLocalProtIncCtxComp)baseLocalProt).setTracker(gbProtocol);
	//	protocol=gbProtocol;
		cAg.setProtocol(gbProtocol);		
	}
	
	
	//// IMPLEMENTATION OF ConsensusDiagnoser
	
	//GETTERS
	/**
	 * @return a copy of the agent's full hypothesis, with context restricted to comm language 
	 * with otherAgent 
	 */
	public FullHypothesis getOwnCssValue(CanalComm otherAgent){
		FullHypothesis result=new FullHypothesis(
				getHypothesis(otherAgent),
				getContext(otherAgent));
		return result;
	}

	/**
	 * @return a copy of the agent's favorite hypothesis
	 */
	/**
	 * @return a copy of the agent's favorite hypothesis
	 */
	public UnitClauseCNF getHypothesis(CanalComm otherAgent){
		UnitClauseCNF hyp=new UnitClauseCNF();
		hyp.addAll(agentTheory.getFavHypothesis());
		return hyp;
	}
	
	/**
	 * @return a copy of the agent's favorite hypothesis context
	 */
	public CNF getContext(CanalComm target){
		CNF adaptedCtx=CNF.copy(getContext());
		return adaptedCtx;
	}

		//SETTERS
	public void adoptHyp(UnitClauseCNF hypothesis, CNF context, CanalComm from){
		//must avoid to override previous context as it may contains informations
		// from other agents which are not in the language of partialContext
		// Also need to recompute the context to see if new informations from partial Context
		// have other consequence in the agent gull language
			agentTheory.adoptHypWithContext(hypothesis, context,false, 
					commonLanguage);
	}

	public void adoptCtx(UnitClauseCNF hypothesis, CNF context, CanalComm from){
		//must avoid to override previous context as it may contains informations
		// from other agents which are not in the language of partialContext
		// Also need to recompute the context to see if new informations from partial Context
		// have other consequence in the agent gull language
			agentTheory.adoptHypWithContext(hypothesis, context, true,
					commonLanguage);
	}

	//return true iff the modification of the theory caused a change of fav Hypothesis
	public boolean addToTheory(CNF ruleSet,CanalComm from){
		UnitClauseCNF prevHyp=getHypothesis(from);
		boolean modif;
		try {
			modif=agentTheory.addToTheory(ruleSet,
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_ADDTHEORY),false);
			if (modif) {
				UnitClauseCNF newHyp=getHypothesis(from);
				boolean changeHyp=!prevHyp.isEquiv(newHyp);
				return changeHyp;
			}
							
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// else theory not modified :
		return false;
		
	}
	public boolean addManifestations(UnitClauseCNF manifSet){
	//	UnitClauseCNF prevHyp=getHypothesis();
		boolean modifHyp;
		try {
			modifHyp = agentTheory.addManifestations(manifSet,
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_ADDMANIF));
				// boolean changeHyp=!prevHyp.isEquiv(getHypothesis());
			return modifHyp;							
		} catch (ParseException e) {e.printStackTrace();}
		// else hyp not modified :
		return false;	
	}
	
		//COMPUTATIONS
	public boolean computeContext(UnitClauseCNF hyp,
			CNF previouslyComputedClauses, 
			CNF newClauses, 
			CNF result, boolean prove,
			CanalComm sender) {
		boolean sat=false;
		try {
			// define pf as commLanguage + literals of hyp
			IndepPField pf=commonLanguage;
			pf=pf.addToLiterals(hyp.getVocabulary()); // TODO vocabulary or Literals ? grounding...
			// compute context
			sat=agentTheory.computeContext(previouslyComputedClauses, newClauses, pf, 
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_EXTERNALCTX),
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_EXTERNALCTXNC),
					prove, hyp,
					result);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sat;
	}
		
	
	public boolean getUnexplainedManif(UnitClauseCNF hypothesis,
			UnitClauseCNF unexplManif) {
		unexplManif.clear();
		for (IndepClause manif : agentTheory.getManifestations()) {
				unexplManif.add(manif);
		}
		try {
			unexplManif.removeAllSubsumedbyAny(
					agentTheory.getCoveredManif(hypothesis, 
							stats.getCounter(DiagAgentStats.BCS_NBCSTEP_COVEREDMANIF)));
		} catch (Exception e) {e.printStackTrace();}
		
		return (!unexplManif.isEmpty());
	}

	public CNF getProof(UnitClauseCNF manifs, boolean fullExplain) {
		CNF proof=new CNF();
		try {
			CNF temp=new CNF();
			for (IndepClause manif:manifs)
				if (agentTheory.proveManif(getHypothesis(null), manif,fullExplain,
						stats.getCounter(DiagAgentStats.BCS_NBCSTEP_PROVEMANIF),temp))
					proof.addAll(temp);				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return proof;
	}

	//TESTS ON HYP
	public boolean equalOwnCssValue(FullHypothesis hyp, CanalComm otherAgent){
		return getOwnCssValue(otherAgent).isEquiv(hyp);
	}
	
	// criterion to stop searching for hypothesis -> find minimal hyp if this always return false
	public boolean sufficientHyp(UnitClauseCNF hypothesis){
		try {
			return !agentTheory.isPartialHyp(hypothesis);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	// Note: not strict preference (used to decide whether to propose OwnHyp)
	public boolean betterThanOwnHyp(UnitClauseCNF hypothesis) {
		try {
			return agentTheory.isPreferredToOwnHyp(hypothesis);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	//BLOCKERS
	public void blockHyp(UnitClauseCNF hyp, boolean temporary){
		try {
			agentTheory.blockHyp(hyp, temporary,
					stats.getCounter(DiagAgentStats.BCS_NBCSTEP_ADDTHEORY));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void unBlockAllHyp(){
		agentTheory.unblockAllHyp();
	}
	
	
	
	////GETTERS AND SETTERS

	public CNF getContext() {
		return agentTheory.getContext();
	}	
	
	public void setCommonLanguage(IndepPField pf){
		commonLanguage=pf;
	}
	//// START/FINISH/RUN
	
	public void start() {
		super.start();
		getHypothesis(null);
		if (verbose) System.out.println("\n"+"Theory of "+this.name+" : \n"+agentTheory.toString());
	}
	
	public void finish() {
		if (verbose) System.out.println("\n"+"Theory of "+this.name+" : \n"+agentTheory.toString());
		agentThread=null;
	}
	
	////OTHER
	
	public void finalize(){
		super.finalize();
		agentTheory=null;
		//comm.finalize();
	}

	
	////VARIABLES
	
	public static boolean verbose=false;
	protected LocalTheory agentTheory;
//	protected CommLanguage commLanguage;
	protected IndepPField commonLanguage;
	public DiagAgentStats stats;
	
	// Inherited from Basic Agents:
	// 		protected volatile Thread agentThread = null;
	// 		public String name;
	// 		protected CommunicationModule cAg;
	//			public CanalComm commAgent, commSystem;
	//			private Network commBC;
	//			public CommunicationStat stats;
	//			public MainProtocol gbProtocol;
	// 		protected boolean dormant=false;
	

}
