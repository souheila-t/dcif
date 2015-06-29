package mars.bilateralProtocol;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.UnitClauseCNF;
import mars.ConsensusDiagnoser;
import mars.reasoning.FullHypothesis;

import org.nabelab.solar.parser.ParseException;

import tools.Arguments;


import agentCommunicationSystem.CanalComm;
import agentCommunicationSystem.CommunicationModule;
import agentCommunicationSystem.CommStatsUpdater;
import agentCommunicationSystem.Message;
import agentCommunicationSystem.protocols.LocalProtocol;
import agentCommunicationSystem.protocols.consensus.GlobalWeightedMessage;
import agentCommunicationSystem.protocols.consensus.GlobalWeightedMessageTypes;
import agentCommunicationSystem.protocols.consensus.ValTracker;

public class MarsLocalProtIncCtxComp implements LocalProtocol, DiagMessageTypes, GlobalWeightedMessageTypes {


	public MarsLocalProtIncCtxComp(ValTracker<FullHypothesis> tracker,CommunicationModule cAg,
			ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag,
			CanalComm target){
		this.tracker=tracker;
		this.cAg=cAg;
		this.target=target;
		this.ag=ag;
	}
	public MarsLocalProtIncCtxComp(CommunicationModule cAg,
			ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag,
			CanalComm target){
		this.cAg=cAg;
		this.target=target;
		this.ag=ag;
	}
	public MarsLocalProtIncCtxComp(CommunicationModule cAg,
			ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag){
		this.cAg=cAg;
		this.ag=ag;
	}
	public MarsLocalProtIncCtxComp(MarsLocalProtIncCtxComp base, CanalComm target, boolean critic){
		this.tracker=base.tracker;
		this.cAg=base.cAg;
		this.target=target;
		this.ag=base.ag;
		this.critic=critic;
	}
	public MarsLocalProtIncCtxComp(MarsLocalProtIncCtxComp base, CanalComm target, boolean critic, 
			boolean initiallyEmpty){
		this.tracker=base.tracker;
		this.cAg=base.cAg;
		this.target=target;
		this.ag=base.ag;
		this.critic=critic;
		this.willProposeEmpty=initiallyEmpty;
	}
	
	public void setTracker(ValTracker<FullHypothesis> track){
		tracker=track;
	}
	
	public void disconnect() {
		// TODO Auto-generated method stub
	}

	public boolean gereParProtocol(Message<?> m) {
		if (m!=null)
			if (m instanceof DiagMessage) {
				switch (m.getCode()){
				case DGM_PROPOSE:
				case DGM_PROPOSEAGAIN:
				case DGM_ACCEPT:
				case DGM_WITHDRAW:
				case DGM_DENY:
				case DGM_COUNTEREXAMPLE_COH:
				case DGM_COUNTEREXAMPLE_COMP:
				case DGM_CHALLENGE:
				case DGM_ARGUE:
				case DGM_HASBETTERHYP:
				case DGM_INFORM:
				case DGM_ACK:
				case DGM_CHCK_CTXT:
			//	case DGM_CHCK_CTXT_FRST:
				case DGM_CONFIRM_CTXT:
				case DGM_WITHDRAW_INCOMPLETE:
				case DGM_ACK_CONFIRM:	
				case DGM_ASK_OTHERHYP:
				case DGM_ACK_INFORM:
					return true;
				}
			}
			if (m instanceof GlobalWeightedMessage) {
				switch (m.getCode()){
				case GWM_ENDLOCALCONV:
					return critic;
				}
			}
		return false;
	}
	
	public void receiveMessage(Message<?> m) {
		if (m!=null && m instanceof DiagMessage) try {
			DiagMessage dm=(DiagMessage)m;
			Arguments arg=dm.getArgument();
			CanalComm sender=dm.getSender();
			switch(dm.getCode()){
			case DGM_PROPOSEAGAIN:
				receiveProposeAgain(new FullHypothesis(arg),sender);
			break; 
			case DGM_PROPOSE:
				//done
				receivePropose(new FullHypothesis(arg),sender);
			break;
			case DGM_ACCEPT:
				//done
				receiveAccept(new FullHypothesis(arg),sender);
			break;
			case DGM_WITHDRAW:
				receiveWithdraw(sender);
			break;
			case DGM_WITHDRAW_INCOMPLETE:	
				receiveWithdrawComp(sender);
			break;
			case DGM_HASBETTERHYP:
				receiveDeny(sender,false);
			break;
			case DGM_DENY:
				receiveDeny(sender,true);
			break;
			case DGM_COUNTEREXAMPLE_COH:
				//done
				receiveCounterExampleCoh(new CNF(arg),sender);
			break;
			case DGM_COUNTEREXAMPLE_COMP:
				//done
				receiveCounterExampleComp(new UnitClauseCNF(arg),sender);
			break;
			case DGM_CHALLENGE:
				receiveChallenge(sender);
			break;
			case DGM_ARGUE:
				receiveArgue(new CNF(arg),sender);
			break;
			case DGM_ACK_INFORM:
				receiveAckInform(sender);
			break;	
			case DGM_ACK:
				receiveAck(sender);
			break;
			case DGM_CHCK_CTXT:
		//	case DGM_CHCK_CTXT_FRST
				receiveCheckContext(new CNF(arg),sender,null);
			break;
			case DGM_CONFIRM_CTXT:
				receiveConfirmCtxt(new CNF(arg),sender);
			break;
			case DGM_ACK_CONFIRM:	
				receiveAckConfirm(new FullHypothesis(arg),sender);
			break;
			case DGM_INFORM:
				receiveInform(new CNF(arg),sender);
			break;
			case DGM_ASK_OTHERHYP:
				receiveAskOtherHyp(new UnitClauseCNF(arg),sender);
			break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (m!=null && m instanceof GlobalWeightedMessage){
			switch(m.getCode()){
			case GWM_ENDLOCALCONV:
				cAg.send(new GlobalWeightedMessage(GWM_ENDLOCALCONV,null), m.getSender());
			break;
			}
		}
	}

	protected UnitClauseCNF getSubjectHypBase(CanalComm sender){
		UnitClauseCNF hyp;
		if (critic) 
			hyp=tracker.getLastValOf(sender).getBase();
		else 
			hyp=ag.getHypothesis(sender);
		return hyp;
	}
	
	protected CNF pickOneArgument(CNF proof, int ind){
		arguments.clear();
		arguments.addAll(proof);
		return pickOneArgument(ind);
	}
	protected CNF pickOneArgument(int ind){
		CNF res=new CNF();
		res.add(arguments.get(ind));
		arguments.remove(ind);
		return res;
	}
	

	//// INITIALISATIONS
	
	public LocalProtocol initProtocol(CanalComm target, CommStatsUpdater cs) {
		if (verbose) {
			System.out.println("\n"+"Local Exchange between "+cAg.getComm().toString()+" and "+target.toString());
			System.out.println("        val of "+ag+": "+ag.getOwnCssValue(target)
								+" / last val conf by "+target+": "+tracker.getLastValConfirmedBy(target)+"\n");
		}
		cs.initLocalProtocol();
		return new MarsLocalProtIncCtxComp(this,target,false,ag.getHypothesis(target).isEmpty());		
	}
	
	public LocalProtocol acceptProtocol(CanalComm target, CommStatsUpdater cs) {
		cs.acceptLocalProtocol();
		return new MarsLocalProtIncCtxComp(this,target,true,ag.getHypothesis(target).isEmpty());		
	}
	
	//also called whenever new hypothesis should be propose
	public void start() {
		// State 1 : Check context and send ASK or PROPOSE
		proposeOwnHypothesis(false);
	}
	
	private void proposeOwnHypothesis(boolean again){
		critic=false;
		prevCompCtx.clear();
		FullHypothesis hyp=ag.getOwnCssValue(target);
		if (hyp.getBase().isEmpty()){
			if (!willProposeEmpty){
				cAg.send(new DiagMessage(DGM_ASK_OTHERHYP,hyp.getBase()), target);
				return;
			}
			else 
				willProposeEmpty=false;
		}
		prevCompCtx.addAll(hyp.getAll());
		if (again)
			cAg.send(new DiagMessage(DGM_PROPOSEAGAIN,hyp.toArgument()),target);
		else
			cAg.send(new DiagMessage(DGM_PROPOSE,hyp.toArgument()),target);
	}
	
	//////// CRITIQUE
	public void receivePropose(FullHypothesis hypo, CanalComm sender) throws ParseException{
		FullHypothesis hyp=new FullHypothesis(hypo);
		tracker.newValReceived(new FullHypothesis(hyp.getBase(),hyp.getContext()), sender);
		critic=true;
		// State 3 : Check hypothesis and send 
		// CE_COHERENCE, CE_COMPLETE, ASK, ACCEPT, DENY
		checkCoherence(hyp,sender,true);		
	}

	///// COHERENCE CHECK
	
	public void checkCoherence(FullHypothesis hyp, CanalComm sender, boolean first) throws ParseException{
		//FIRST Context 
		// reset prevCtx
		prevCompCtx.clear();
		//: include memory
		// unify context if First and same hypothesis : subsumption test when adding
		CNF ctx0=CNF.copy(hyp.getContext()); 
		ctx0.addAll(hyp.getBase());
		CNF hypCopy=CNF.copy(ctx0);
		boolean modCtx=false;
		if (first && CNF.isEquiv(hyp.getBase(), ag.getHypothesis(sender))) {
			modCtx=ctx0.addAll(ag.getContext(sender));
		}
		//compute ctx1 with new context ctx0
		if (modCtx)
			receiveCheckContext(ctx0,sender,hypCopy);
		else
			receiveCheckContext(ctx0,sender,null);
	}	
		//receiveCheckContext and subprocedure
	public void receiveCheckContext(CNF contextStep, CanalComm sender,
									CNF initialCtxStep) throws ParseException{
		CNF ctxStep=CNF.copy(contextStep);
		UnitClauseCNF hyp=getSubjectHypBase(sender);
		CNF result=new CNF();
			// computeCtx give new csq if sat or used clauses if unsat (stored in result)
		boolean sat=ag.computeContext(hyp,prevCompCtx, ctxStep, result, refute, sender);
		if (!sat)
			foundRefutation(result, sender);
		else 
			processNewCons(result,sender,ctxStep,initialCtxStep);
	}
	
	protected void processNewCons(CNF newCons,CanalComm sender, CNF ctxStep,CNF initialCtxStep) 
												throws ParseException{
		//ensure that computations are not fouled by eventual Ctx' addition at first critic step
		if (initialCtxStep!=null) ctxStep=initialCtxStep;
		//newStep
		System.out.println("prevCompCtx : "+prevCompCtx);		
		CNF newCtxStep=CNF.copy(newCons);
		newCtxStep.removeAllSubsumedbyAny(ctxStep);
		newCtxStep.removeAllSubsumedbyAny(prevCompCtx);
		//prevComptCtx
		prevCompCtx.addAll(newCons); 	
		//fill confirmedCtx and inform confirmations 
		CNF confirmedCtx=CNF.copy(ctxStep);
		confirmedCtx.removeAllSubsumedbyAny(newCons);
		if (!confirmedCtx.isEmpty()){
			arguments.clear();
			arguments.addAll(newCtxStep);
			System.out.println("ContextStep : "+ctxStep);
			System.out.println("NewCons : "+newCons);
			System.out.println("newContextStep (newcons \\ ctxstp U prevCtx) : "+newCtxStep);
			System.out.println("confirmedCtx (ctxStep \\ newCons) : "+confirmedCtx);
			cAg.send(new DiagMessage(DGM_INFORM,confirmedCtx), sender); 
			return;
		}
		endCheckContext(newCtxStep,sender);		
	}

		//// Refutations
	protected void foundRefutation(CNF refut,CanalComm sender){
		// if unsat, choose and send counter-example (if critic) or recompute hyp
		if (critic){
			if (refute){
				if (refutOneByOne) 
					refut=pickOneArgument(refut,refut.size()-1);
				cAg.send(new DiagMessage(DGM_COUNTEREXAMPLE_COH,refut), sender);
			}
			else{
				cAg.send(new DiagMessage(DGM_COUNTEREXAMPLE_COH, refut), sender);
			}
		}				 
		else
			receiveCounterExampleCoh(refut, sender);
	}

	public void receiveCounterExampleCoh(CNF cel, CanalComm sender){
			CNF ce=CNF.copy(cel);
			//(i)[state6a]  CE-COHERENCE : Check CE and send CHALLENGE or WITHDRAW
			if (refute){
				if (ag.addToTheory(ce,sender)) 
					cAg.send(new DiagMessage(DGM_WITHDRAW), sender);
				else {
					ag.blockHyp(ag.getHypothesis(sender),false);
					cAg.send(new DiagMessage(DGM_WITHDRAW), sender);
				}
			}
			else { 
				if (ce.isEmpty()) ce=ag.getHypothesis(sender);
				UnitClauseCNF elem=new UnitClauseCNF();
				elem.addAll(ce);
				ag.blockHyp(elem,false);
				cAg.send(new DiagMessage(DGM_WITHDRAW), sender);
			 }
	}
	
		// receive and ack Inform
	public void receiveInform(CNF info, CanalComm sender) throws ParseException{
		boolean modifiedHyp=ag.addToTheory(CNF.copy(info),sender);
		//need to modify prevcomptCtx
		prevCompCtx.removeAllSubsumedbyAny(info);
		if (!critic && modifiedHyp)
			//TODO This should not happen !!
			cAg.send(new DiagMessage(DGM_WITHDRAW), sender);
		else
			cAg.send(new DiagMessage(DGM_ACK_INFORM), sender);
	}

	public void receiveAckInform(CanalComm sender){
		endCheckContext(arguments, sender);
	}	
		
	//this function is used to factorize direct end of check context and interruption by an inform
	protected void endCheckContext(CNF newCtxStep,CanalComm sender){
		if (!newCtxStep.isEmpty()) 
			cAg.send(new DiagMessage(DGM_CHCK_CTXT,newCtxStep), sender);
		else {
			goodContext(prevCompCtx,sender);
			//TODO replace arg by null once debugging finished.
			cAg.send(new DiagMessage(DGM_CONFIRM_CTXT,prevCompCtx), sender);		
		}
	}
		// Confirmations of context
	
	public void goodContext(CNF context, CanalComm sender){
		UnitClauseCNF hyp=getSubjectHypBase(sender);
		if (critic)
			tracker.newValReceived(new FullHypothesis(hyp,context), sender);
		else
			ag.adoptCtx(hyp,context,sender);
		// Beware, here fro=sender just means that adopted ctx was computed with sender
	}

	public void receiveConfirmCtxt(CNF context, CanalComm sender) throws ParseException{
		// TODO check that ctx and prevCompCtx corresponds
		if (!CNF.isEquiv(context,prevCompCtx))
			System.out.println("!!!!!!!!!!!!!!!!  Contextes différents a la terminaison !!!!!!!!!!!!!!!");
		goodContext(context,sender);
		if (critic)
			checkCompleteness(tracker.getLastValOf(sender),sender);
		else
			cAg.send(new DiagMessage(DGM_ACK_CONFIRM,ag.getOwnCssValue(sender).toArgument()), sender);
	}
	
	public void receiveAckConfirm(FullHypothesis hypo, CanalComm sender){
		checkCompleteness(hypo,sender);
	}
	
	//// COMPLETENESS CHECK
	
	public void checkCompleteness(FullHypothesis hyp, CanalComm sender) {
		//check
		UnitClauseCNF unexplManif=new UnitClauseCNF();
		if (ag.getUnexplainedManif(hyp.getBase(), unexplManif)){
			if (uncoveredManifOneByOne){
				IndepClause manif=unexplManif.get(0);
				unexplManif.clear();
				unexplManif.add(manif);
			}
			cAg.send(new DiagMessage(DGM_COUNTEREXAMPLE_COMP,unexplManif), sender);
			return;
		}
		//if no message: proceed to Admissibility Check
		try {
			checkAdmissibility(hyp, sender);
		} catch (ParseException e) {e.printStackTrace();}
	}

	public void receiveCounterExampleComp(UnitClauseCNF cel, CanalComm sender){
		UnitClauseCNF ce=new UnitClauseCNF();ce.addAll(cel);
				if (ag.addManifestations(ce)) 
					cAg.send(new DiagMessage(DGM_WITHDRAW_INCOMPLETE), sender);
				else //false counterExample
					cAg.send(new DiagMessage(DGM_ARGUE,ag.getProof(ce,proveCovering)), sender);
	}

	
	//// ADMISSIBILITY CHECK
	public void checkAdmissibility(FullHypothesis hyp, CanalComm sender) throws ParseException{
		boolean sufficient=ag.sufficientHyp(hyp.getBase());
		if ((sufficient ||fullyExplored) && ag.betterThanOwnHyp(hyp.getBase())){//ACCEPT
			//remove temporary blockers and adopt hypothesis
			ag.unBlockAllHyp();
			ag.adoptHyp(hyp.getBase(),hyp.getContext(),sender);
			tracker.newConfirmationReceived(hyp, sender);
			cAg.send(new DiagMessage(DGM_ACCEPT,hyp.toArgument()), sender);		
		}
		else {//DENY
			//block temporarily this hyp
			if (!sufficient && !hyp.getBase().isEmpty()) 
				ag.blockHyp(hyp.getBase(), true);
			// check if other options from itself
			if (willProposeEmpty || !ag.getHypothesis(sender).isEmpty()){
				if (!sufficient)
					cAg.send(new DiagMessage(DGM_DENY,hyp.toArgument()), sender);
				else
					cAg.send(new DiagMessage(DGM_HASBETTERHYP,hyp.toArgument()), sender);
			}	
			else
				cAg.send(new DiagMessage(DGM_ASK_OTHERHYP,hyp.getBase()), sender);
		}
	}
	
	public void receiveAskOtherHyp(UnitClauseCNF hyp, CanalComm sender) throws ParseException{
		if (!hyp.isEmpty()) 
			ag.blockHyp(hyp, true);
		if (ag.getHypothesis(sender).isEmpty()){
			ag.unBlockAllHyp();
			fullyExplored=true;
			proposeOwnHypothesis(true);
		}
		else {
			target=sender;
			proposeOwnHypothesis(false);
		}			
	}
	
	public void receiveProposeAgain(FullHypothesis hyp, CanalComm sender) throws ParseException {
		ag.unBlockAllHyp();
		fullyExplored=true;		
		receivePropose(hyp,sender);
	}
	
		//// FINAL OUTCOME FOR PROPOSED HYP: Deny & Accept
	
	public void receiveWithdraw(CanalComm sender) {
		cAg.send(new DiagMessage(DGM_ACK), sender);
	}

	public void receiveWithdrawComp(CanalComm sender) {
		ag.unBlockAllHyp();
		cAg.send(new DiagMessage(DGM_ACK), sender);
	}

	public void receiveDeny(CanalComm sender, boolean block) {
		//blockHyp
		if (block && !ag.getHypothesis(sender).isEmpty()) 
			ag.blockHyp(ag.getHypothesis(sender), true);
		//send ACK
		critic=true;
		cAg.send(new DiagMessage(DGM_ACK), sender);
	}
	
	public void receiveAck(CanalComm sender){
		proposeOwnHypothesis(false);
	}

	public void receiveAccept(FullHypothesis hyp, CanalComm sender){
		tracker.newConfirmationReceived(hyp, sender);
		// end protocol properly or send PROPOSE if hyp changed
		//FullHypothesis ownHyp=ag.getOwnCssValue(sender);
		if (ag.equalOwnCssValue(hyp, sender)) {
			ag.unBlockAllHyp();
			if (verbose) System.out.println("END of Local Exchange between "+cAg.getComm().toString()+" and "+target.toString()+"\n");
			cAg.send(new GlobalWeightedMessage(GWM_ENDLOCALCONV,null),sender);
		}
		else {
			proposeOwnHypothesis(false);
		}
	}

	
	
	//// ARGUMENTATION
	public void receiveArgue(CNF arg, CanalComm sender){
		if (critic) { //CounterExample completeness
			// recheck completeness (CE_CMPL, ASK, ACCEPT, DENY)
			ag.addToTheory(arg,sender);
			FullHypothesis hyp=tracker.getLastValOf(sender);
			// since we do not want to settle on an insufficient hypothesis without exploring all 
			// options, we must reset earlier insufficient confirmations when 'argue' give 
			// opportunities for discovering other partial hyp (and eventually a sufficient hyp).
			if (!ag.sufficientHyp(hyp.getBase()) && !ag.sufficientHyp(ag.getHypothesis(sender)))
				tracker.resetAllConfirmations();
			checkCompleteness(hyp,sender);
		}
		//TODO : following (including receiveChallenge) is currently overriden by empirical rules
		else { // unconvincing Refutation
			if (ag.addToTheory(arg,sender)) 
				cAg.send(new DiagMessage(DGM_WITHDRAW), sender);
			else 
				cAg.send(new DiagMessage(DGM_CHALLENGE), sender);
		}
	}
	
	// Only send when a refutation is unconvincing
		// currently overridden by empirical rules
	public void receiveChallenge(CanalComm sender){
		//Send relevant ARGUE(obs) 
		cAg.send(new DiagMessage(DGM_ARGUE,arguments), sender);
	}
		
	
	
	
	public static boolean verbose=false;
	
	protected ValTracker<FullHypothesis> tracker=null;

	protected CommunicationModule cAg;
	
	protected CanalComm target=null;
	
	protected ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag;
	
	protected CNF prevCompCtx=new CNF();
	protected boolean critic=false;
	protected boolean fullyExplored=false;
	protected boolean willProposeEmpty=false;
	
	
	protected CNF arguments=new CNF();
	//protected List <Clause> criticizedCtxt=new ArrayList<Clause>();
	
	//Parameters
		//refutations
	protected boolean refute=false;
	protected boolean refutOneByOne=false;
		//manifestations
	protected boolean proveCovering=false;
	protected boolean uncoveredManifOneByOne=false;
	
	
	//protected FullHypothesis acceptableHyp=null;

}
