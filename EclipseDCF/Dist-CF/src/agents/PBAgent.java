/**

 * 

 */

package agents;

import genLib.tools.Arguments;

import java.util.ArrayList;

import java.util.List;



import org.nabelab.solar.Stats;



import logicLanguage.CNF;

import logicLanguage.CommLanguage;

import logicLanguage.IndepClause;

import solarInterface.CFSolver;

import solarInterface.IndepPField;

import stats.ConsFindingAgentStats;

import theory.ConsFindingLocalTheory;


import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.Network;






import communication.PBMessage;

import communication.PBMessageTypes;

import communication.protocol.LocalPBProtocol;

import communication.protocol.MainPBProtocol;



/**
 * Agent for the partition-based consequence finding.
 * 
 * @author Viel Charlotte
 */

public class PBAgent extends ConsFindingAgent implements Runnable, Agent,
		PBDiagnoser<CNF> {


	/**
	 * Constructor for the agent.
	 * 
	 * @param name 			  : name of the agent
	 * @param theory 		  : initial theory of the agent
	 * @param cSyst 		  : CanalComm of the system
	 * @param net 			  : Network
	 * @param stats 		  : Stats of the agent
	 * @param typeConsFinding : Type of consequence finding
	 */
	public PBAgent(String name, ConsFindingLocalTheory theory, CanalComm cSyst,
			Network net, ConsFindingAgentStats stats, int typeConsFinding, long deadline) {
		super(name, theory, cSyst, net, stats);
		setDormant(false);
		this.deadline=deadline;
		LocalPBProtocol baseLocalProt = new LocalPBProtocol(cAg, this);
		baseLocalProt.setTypeConsFinding(typeConsFinding);
		MainPBProtocol gbProtocol = new MainPBProtocol(cAg, baseLocalProt);
		cAg.setProtocol(gbProtocol);
		
		carcDone = false;
	//	originalPField = getOriginalPField();
	//	parentCLField = computePField();
	}

	@Override
	// NOTE : useOriginalTopClauses set to false here (not used in PB version)
	public void consFinding(ArrayList<IndepClause> topClauses, boolean addConsToTheory, boolean useOriginalTopClauses) {
		//originalPField = getOriginalPField();
		//pField = computePField(originalPField);
		/* ********************************************************** */
		// pField = originalPField;

		if (verbose) {
		//	System.out.println("\n" + name + " : "
		//			+ getAgentTheory().toString());
			System.out.println(name + " : pfield : " + getPField().toString());
			System.out.println(name + " : original pfield : "
					+ originalPField.toString());
		}

		/* ********************************************************** */

		CNF consequences = agentTheory.consequenceFinding(getPField(),
			topClauses, stats.getSolarCtrList(), false, deadline);

		if (verbose) System.out.println(name + " : consequences : "
				+ consequences.toString());
		CNF clausesToSend=new CNF();
		consequences.removeAll(listClausesSent);
		consequences.removeAll(listConseqSent);
		
		for (IndepClause c : consequences) {
			if (originalPField.belongsTo(c)) {
				/* *********************** */
				//System.out.println(name + " : clause " + c.toString());
				sendConseq(c);
			}
			if (!isRoot){
				if ((!refinedPF)// && isClauseInCommLanguage(c, parent, originalPField)) 
				// condition isClauseInCommLanguage(c, parent)) always true given PF
				|| (refinedPF && parentCLField.partlyBelongsTo(c, originalPField)))
					clausesToSend.add(c);
			}
		}
		
		if (pruneCsq)
			CFSolver.pruneConseqSet(clausesToSend, agentTheory.getTheory(true).getDepthLimit(), deadline,
				stats.getSolarCtrList());
		
		for (IndepClause c : clausesToSend) {
			sendClause(c);
		}
		if (!clausesToSend.isEmpty())
			sendEndBatchToParent();
		
		if (addConsToTheory)
			agentTheory.addToTheory(consequences);
		
		
		if (getSons().size()==0) {
			sendEndToParent();
			setDormant(true);
		}
		
		/*if (nbEndReceived == getSons().size()) {
			sendEndToParent();
			setDormant(true);
		}*/
		carcDone = true;
	}

	/**
	 * Send the clause to the parent of the agent.
	 * 
	 * @param clause
	 */
	private void sendClause(IndepClause clause) {
		if (!listClausesSent.contains(clause)) {
			listClausesSent.add(clause);
			String s = clause.toString();
			Arguments arg = new Arguments(s.substring(1, s.length() - 1));
			Message<?> mToSend = new PBMessage(PBMessageTypes.PBM_SEND_CLAUSE,
					arg);
			cAg.send(mToSend, parent);
			stats.getCounter(ConsFindingAgentStats.DCF_NBCLAUSESSENT).inc(1);
		}
	}
	
	private void sendConseq(IndepClause clause) {
		if (!listConseqSent.contains(clause)) {
			listConseqSent.add(clause);
			String s = clause.toString();
			Arguments arg = new Arguments(s.substring(1, s.length() - 1));
			Message<?> mToSend = new PBMessage(PBMessageTypes.PBM_SEND_CONSEQ,
					arg);
			cAg.send(mToSend, commSyst);
			stats.getCounter(ConsFindingAgentStats.DCF_NBCONSEQSENT).inc(1);
		}
	}
	

	public synchronized void sendEndToParent() {
		Message<?> m = new PBMessage(PBMessageTypes.PBM_END);

		if (!isRoot) {
			cAg.send(m, parent);
		} else {
			notifyAll();
			cAg.send(m, commSyst);
		}
		setDormant(true);
	}

	public synchronized void sendEndBatchToParent() {
		Message<?> m = new PBMessage(PBMessageTypes.PBM_ENDBATCH);

		if (!isRoot) {
			cAg.send(m, parent);
		} else {
			notifyAll();
			cAg.send(m, commSyst);
		}
	}

	
	/**
	 * Find the production field for the agent. If the agent is the root,
	 * returns the original production field. Otherwise, returns the literals in
	 * the language between the agent and its parent.
	 * 
	 * @param originalPField
	 *            : IndepPField
	 * @return IndepPField
	 */
	public void computePField() {
		if (computedPFields) return;
		parentCLField = new IndepPField();
		if (isRoot) {
			computationPField=originalPField;
		} else {
			for (int i = 0; i < commLanguage.agents.size(); i++) {
				if (commLanguage.agents.get(i).toString().equals(
						"ag" + parentIndex)) {
					parentCLField = commLanguage.commLanguage.get(i);
				}
			}
			computationPField=parentCLField.mergeWith(originalPField, IndepPField.MRG_UNION_INITFIT);
		}
		computedPFields=true;
	}

	/*  *** Getters/Setters *** */

	/**
	 * @return nbEndReceived
	 */
	public int getNbEndReceived() {
		return nbEndReceived;
	}
	

	public void incNbEndReceived() {
		nbEndReceived++;
	}

	/**
	 * @return pField
	 */
	public IndepPField getPField() {
		if (!computedPFields)
			computePField();
		return computationPField;
	}

	/**
	 * @param set
	 *            originalPField
	 */
	public void setOriginalPField(IndepPField originalPField) {
		this.originalPField = originalPField;
	}

	/**
	 * @return originalPField
	 */
	public IndepPField getOriginalPField() {
		return originalPField;
	}

	/**
	 * @return agentTheory
	 */
	public ConsFindingLocalTheory getAgentTheory() {
		return agentTheory;
	}

	/**
	 * @return commLanguage
	 */
	public CommLanguage getCommLanguage() {
		return commLanguage;
	}

	/**
	 * @param set
	 *            commLanguage
	 */
	public void setCommLanguage(CommLanguage commLanguage) {
		this.commLanguage = commLanguage;
	}

	/**
	 * @return carcDone
	 */
	public boolean isCarcDone() {
		return carcDone;
	}

	/**
	 * @param set
	 *            isRoot
	 */
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}



	/**
	 * Returns the String representation of the object.
	 */
	public String toString() {
		return name;
		// + "\ncommLanguage : " + cAg.getNeighbours().toString();
	}


	public ArrayList<CanalComm> getSons() {
		ArrayList<CanalComm> fils = new ArrayList<CanalComm>();
		fils.addAll(commLanguage.agents);
		if (!isRoot) {
			fils.remove(parent);
		}
		return fils;
	}

	/* *************** START/FINISH ****************** */

	@Override
	public void start() {
		super.start();
		if (verbose)
			System.out.println("\n" + "Theory of " + this.name + " : \n"
					+ agentTheory.toString());
	}

	@Override
	public void finish() {
		if (verbose)
			System.out.println("\n" + "Theory of " + this.name + " : \n"
					+ agentTheory.toString());
		agentThread = null;
	}

	/* *************** Variables ****************** */
	
	public static boolean refinedPF=true;
	public static boolean pruneCsq=true;
	public static boolean incremental=false;
	
	
	private boolean carcDone;
	private IndepPField parentCLField;
	private IndepPField computationPField;
	private boolean computedPFields=false;
	private IndepPField originalPField;
	private int nbEndReceived;
	private List<IndepClause> listClausesSent= new ArrayList<IndepClause>();;
	private List<IndepClause> listConseqSent= new ArrayList<IndepClause>();;
	private long deadline=-1;

}

