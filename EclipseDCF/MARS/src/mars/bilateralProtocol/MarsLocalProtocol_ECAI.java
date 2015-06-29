package mars.bilateralProtocol;

import logicLanguage.CNF;
import logicLanguage.UnitClauseCNF;
import mars.ConsensusDiagnoser;
import mars.reasoning.FullHypothesis;

import org.nabelab.solar.parser.ParseException;


import agentCommunicationSystem.CanalComm;
import agentCommunicationSystem.CommunicationModule;
import agentCommunicationSystem.CommStatsUpdater;
import agentCommunicationSystem.protocols.LocalProtocol;
import agentCommunicationSystem.protocols.consensus.ValTracker;

public class MarsLocalProtocol_ECAI extends MarsLocalProtIncCtxComp {

	public MarsLocalProtocol_ECAI(CommunicationModule cAg,
			ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag){
		super(cAg,ag);
	}
	public MarsLocalProtocol_ECAI(CommunicationModule cAg,
			ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag,
			CanalComm target){
		this(cAg,ag);
		this.target=target;
	}
	public MarsLocalProtocol_ECAI(ValTracker<FullHypothesis> tracker,CommunicationModule cAg,
			ConsensusDiagnoser<FullHypothesis,UnitClauseCNF,CNF,CNF,UnitClauseCNF> ag,
			CanalComm target){
		this(cAg,ag,target);
		this.tracker=tracker;
		
	}
	public MarsLocalProtocol_ECAI(MarsLocalProtocol_ECAI base, CanalComm target, boolean critic){
		this(base.tracker,base.cAg,base.ag,target);
		this.critic=critic;
	}
	public MarsLocalProtocol_ECAI(MarsLocalProtocol_ECAI base, CanalComm target, boolean critic, 
			boolean initiallyEmpty){
		this(base,target,critic);
		this.willProposeEmpty=initiallyEmpty;
	}
	
	
	//// INITIALISATIONS
	
	public LocalProtocol initProtocol(CanalComm target, CommStatsUpdater cs) {
		if (verbose) {
			System.out.println("\n"+"Local Exchange between "+cAg.getComm().toString()+" and "+target.toString());
			System.out.println("        val of "+ag+": "+ag.getOwnCssValue(target)
								+" / last val conf by "+target+": "+tracker.getLastValConfirmedBy(target)+"\n");
		}	
		cs.initLocalProtocol();
		return new MarsLocalProtocol_ECAI(this,target,false,ag.getHypothesis(target).isEmpty());		
	}
	
	public LocalProtocol acceptProtocol(CanalComm target, CommStatsUpdater cs) {
		cs.acceptLocalProtocol();
		return new MarsLocalProtocol_ECAI(this,target,true,ag.getHypothesis(target).isEmpty());		
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
		if (first && CNF.isEquiv(hyp.getBase(), ag.getHypothesis(target))) {
			modCtx=ctx0.addAll(ag.getContext(sender));
		}
		//compute ctx1 with new context ctx0
		if (modCtx)
			receiveCheckContext(ctx0,sender,hypCopy);
		else
			receiveCheckContext(ctx0,sender,null);
	}	
		//receiveCheckContext and subprocedure
	public void receiveCheckContext(CNF currContext, CanalComm sender,
									CNF initialCtx) throws ParseException{
		CNF currCtx=CNF.copy(currContext);
		UnitClauseCNF hyp=getSubjectHypBase(sender);
		CNF result=new CNF();
			// computeCtx give new csq if sat or used clauses if unsat (stored in result)
		boolean sat=ag.computeContext(hyp,new CNF(), currCtx, result, refute, sender);
		if (!sat)
			foundRefutation(result, sender);
		else 
			processNewCons(result,sender,currCtx,initialCtx);
	}
	
	protected void processNewCons(CNF newCtx,CanalComm sender, CNF currCtx,CNF initialCtx) 
												throws ParseException{
		//ensure that computations are not fouled by eventual Ctx' addition at first critic step
		if (initialCtx!=null) currCtx=initialCtx;
		//comparison with previous steps
		CNF newCons=CNF.copy(newCtx);
		newCons.removeAllSubsumedbyAny(currCtx);
		if (!prevCompCtx.isEmpty()) 
			newCons.removeAllSubsumedbyAny(prevCompCtx);
		if (!newCons.isEmpty()){
			prevCompCtx=newCtx;
			cAg.send(new DiagMessage(DGM_CHCK_CTXT,newCtx), sender);
		}
		else {
			//deduce consequence of other agent theory
			CNF newFact=CNF.copy(newCtx);
			newFact.removeAllSubsumedbyAny(currCtx);
			ag.addToTheory(newFact,sender);
			//send final context
			newCtx.removeAllSubsumedbyAny(newFact);
			goodContext(newCtx,sender);
			cAg.send(new DiagMessage(DGM_CONFIRM_CTXT,newCtx), sender);
		}			
	}


	public void receiveConfirmCtxt(CNF finalCtx, CanalComm sender) throws ParseException{
		//deduce consequence of other agent theory
		CNF newFact=CNF.copy(prevCompCtx);
		newFact.removeAllSubsumedbyAny(finalCtx);
		if (ag.addToTheory(newFact,sender)) {
			cAg.send(new DiagMessage(DGM_WITHDRAW), sender);
			return;
		}
		//adopt final context
		goodContext(finalCtx,sender);
		if (critic)
			checkCompleteness(tracker.getLastValOf(sender),sender);
		else
			cAg.send(new DiagMessage(DGM_ACK_CONFIRM,ag.getOwnCssValue(sender).toArgument()), sender);
	}
	
}
