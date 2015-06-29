/**

 * 

 */
package agents;

import genLib.tools.Arguments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import logicLanguage.CNF;
import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;

import org.nabelab.solar.parser.ParseException;

import solarInterface.IndepPField;
import stats.ConsFindingAgentStats;
import theory.ConsFindingLocalTheory;
import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.Message;
import agLib.agentCommunicationSystem.Network;
import agLib.agentCommunicationSystem.protocols.MainProtocol;

import communication.CooperativeMessage;
import communication.CooperativeMessageTypes;
import communication.PBMessage;
import communication.PBMessageTypes;
import communication.protocol.LocalCooperativeProtocol;
import communication.protocol.MainCooperativeProtocol;

/**
 * Agent for the cooperative consequence finding algorithm.
 * 
 * @author Viel Charlotte
 * 
 */

public class CooperativeAgent extends ConsFindingAgent implements Runnable,
		Agent, CooperativeMessageTypes, CooperativeDiagnoser<CNF> {

	/**
	 * Constructor for the agent.
	 * 
	 * @param name 		: name of the agent
	 * @param theory 	: initial theory of the agent
	 * @param cSyst 	: CanalComm of the system
	 * @param net 		: Network
	 * @param stats 	: Stats of the agent
	 * @param nbAgents 	: number of agents in the system
	 * @param pField 	: original production field
	 */
	public CooperativeAgent(String name, ConsFindingLocalTheory theory,
			CanalComm cSyst, Network net, ConsFindingAgentStats stats,
			int nbAgents, IndepPField pField, long deadline) {
		super(name, theory, cSyst, net, stats);
		this.deadline=deadline;
		LocalCooperativeProtocol baseLocalProt = new LocalCooperativeProtocol(
				cAg, this);
		MainProtocol gbProtocol = new MainCooperativeProtocol(cAg,
				baseLocalProt);
		cAg.setProtocol(gbProtocol);
		this.pField = pField;
		extResolvableLiterals=extResolvableLiterals.addToLiterals(pField.getLiterals());

		listClausesSent = new ArrayList<ArrayList<IndepClause>>();
		for (int i = 0; i < nbAgents - 1; i++) {
			commLanguage.commLanguage.add(new IndepPField());
			listClausesSent.add(new ArrayList<IndepClause>());
		}

		setDormant(false);

	}

	/**
	 * Returns all the literals that could be resolved with the theory of ag.
	 * that is, the negation of all literals present in the theory
	 * 
	 * @return List<IndepLiteral>
	 * @throws ParseException
	 */
	public List<IndepLiteral> getAllResolvableLiterals() {
		CNF theory = agentTheory.getTheory(true).getAllClauses();
		return theory.getNegatedVocabulary();
	}
	

	public void sendAllResolvableLiterals(CanalComm ag) {
		Arguments arg;
		arg = new Arguments(getAllResolvableLiterals());
		CooperativeMessage mToSend = new CooperativeMessage(
					CM_SEND_ALL_LTERALS, arg);
			cAg.send(mToSend, ag);
	}


	public void askAllResolvableLiterals(CanalComm ag) {
		CooperativeMessage mToSend = new CooperativeMessage(CM_ASK_ALL_LTERALS);
		cAg.send(mToSend, ag);
		//sendAllResolvableLiterals(ag);
	}
	

//	// update comm language as the intersection of the theory literals with the given list of literals
//	// listeLit must contains the negated vocabulary of the sending agent (that is, the list of literals that it can resolve)
//	public void updateCommLanguage(CanalComm ag, List<IndepLiteral> listeLit) {
//		List<IndepLiteral> litTheory=agentTheory.getTheory(true).getVocabulary();
//		int numAg = identifyNeighbor(ag);
//		litTheory.retainAll(listeLit);
//		commLanguage.commLanguage.set(numAg, new IndepPField(litTheory));
//	}
	
	// update comm language as the given list of literals (more memory cost, but this way, we can worry less about synchronization of updates
	// listeLit must contains the negated vocabulary of the sending agent (that is, the list of literals that it can resolve)
	// and update the extCommLanguage
	public void updateCommLanguage(CanalComm ag, List<IndepLiteral> listeLit) {
		int numAg = identifyNeighbor(ag);
		commLanguage.commLanguage.set(numAg, new IndepPField(listeLit));
		extResolvableLiterals=extResolvableLiterals.addToLiterals(listeLit);
	}


	@Override
	public synchronized void consFinding(ArrayList<IndepClause> topClauses, boolean addConsToTheory, boolean originalTopClauses) {
		IndepPField pf = new IndepPField();

		pf = getPField(topClauses);

		CNF consequences;
		boolean noTopClauses=topClauses.isEmpty() && (!originalTopClauses || agentTheory.getTheory(true).getTopClauses(true).isEmpty());
		boolean topClausesInTheory=(!topClauses.isEmpty()) && agentTheory.getTheory(true).getAxioms(true).containsAll(topClauses);
		if ((originalTopClauses && noTopClauses && initNewCarc)
			|| (topClausesInTheory))
			consequences=new CNF();
		else
			consequences= agentTheory.consequenceFinding(pf, topClauses,
					stats.getSolarCtrList(), originalTopClauses,deadline);
		if (verbose) System.out.println(name + " : consequences : "
				+ consequences.toString());
		
		for (IndepClause c : consequences) {
			if (this.pField.belongsTo(c)) {
				if (verbose) System.out.println(name + " sends NEW CONS : " + c.toString());
				sendConseq(c);
			}

			CanalComm neighbor = chooseAgent(c, WOC_ORDERED);
			if (neighbor != null) {
				int n = identifyNeighbor(neighbor);
				if (!isClauseSent(c, n)) {
					String s = c.toString();
					Arguments arg = new Arguments(s.substring(1,
							s.length() - 1));
					Message<?> mToSend = new CooperativeMessage(
							CM_SEND_CLAUSE, arg);
					cAg.send(mToSend, neighbor);
					stats.clausesSent.inc(1);
						listClausesSent.get(n).add(c);
				}
				//TODO : cehek if ok without MAJ (should we uncomment the following line?)
				//askAllResolvableLiterals(neighbor);
			}
		}
		setDormant(cAg.getComm().isEmpty());
	}
		
	/**
	 * Returns the production field constructed from all the literals in
	 * the language of the agent.
	 * 
	 * @return IndepPField
	 * @throws ParseException
	 */
	private IndepPField getPField(List<IndepClause> topClauses) {
		CNF theory = agentTheory.getTheory(true).getAllClauses();
		//getAllClauses is not a ref, so following line should not modify the theory
		theory.addAll(topClauses);
		List<IndepLiteral> literals=theory.getVocabulary();
		List<IndepLiteral> selectedLits=new ArrayList<IndepLiteral>();
		for (IndepLiteral lit:literals)
			if (extResolvableLiterals.getLiterals().contains(lit))
				selectedLits.add(lit);
		IndepPField pf = new IndepPField(selectedLits);
		return pf;
	}

	private void sendConseq(IndepClause clause) {
	String s = clause.toString();
	Arguments arg = new Arguments(s.substring(1, s.length() - 1));
	Message<?> mToSend = new PBMessage(PBMessageTypes.PBM_SEND_CONSEQ,arg);
	cAg.send(mToSend, commSyst);
	stats.getCounter(ConsFindingAgentStats.DCF_NBCONSEQSENT).inc(1);
	}
	
	/**
	 * Verify if the clause has already been sent to its neighbor.
	 * 
	 * @param clause
	 * @param neighbor
	 * @return boolean
	 */
	private boolean isClauseSent(IndepClause clause, int neighbor) {
		if (listClausesSent.isEmpty()) {
			return false;
		}
		for (IndepClause indepClause : listClausesSent.get(neighbor)) {
			if (IndepClause.isEquiv(indepClause, clause)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Choose an agent to which the clause will be sent.
	 * 
	 * @param clause
	 * @param wayOfChoosing
	 * @return CanalComm of the chosen agent
	 */
	public CanalComm chooseAgent(IndepClause clause, int wayOfChoosing) {
		CanalComm ag = null;
		switch (wayOfChoosing) {
		case WOC_COMPLEMENTARY:
			ag = chooseAgentComplementary(clause);
			break;
		case WOC_INTERSECTION:
			ag = chooseAgentIntersection(clause);
		case WOC_ORDERED:
			ag = chooseAgentIntersectionOrdered(clause);
		}
		return ag;
	}

	/**
	 * Choose an agent that has the most complementary literals.
	 * 
	 * @param clause
	 * @return
	 */
	private CanalComm chooseAgentComplementary(IndepClause clause) {
		CanalComm neighbor = null;
		return neighbor;
	}

	/**
	 * Choose an agent where intersection between the literals of the agent theory and 
	 * the commLanguage with other agent (containing resolvable literals) is not null.
	 * 
	 * @param clause
	 * @return CanalComm
	 */
	private CanalComm chooseAgentIntersection(IndepClause clause) {
		CanalComm neighbor = null;
		int nbAgents = commLanguage.agents.size();
		int i = 0;
		boolean canResolve=false;
		do {
			i++;
			int rand = (int) (Math.random() * nbAgents);
			neighbor = commLanguage.agents.get(rand);
			canResolve=commLanguage.getLanguage(neighbor).partlyBelongsTo(clause,extResolvableLiterals);
		} while (!canResolve
				&& i <= 2 * nbAgents);

		if (!canResolve) {
			neighbor = null;
		}

		return neighbor;
	}

	/**
	 * Choose an agent where intersection between the literals of the agent theory and 
	 * the commLanguage with other agent (containing resolvable literals) is not null.
	 * 
	 * @param clause
	 * @return CanalComm
	 */
	private CanalComm chooseAgentIntersectionOrdered(IndepClause clause) {
		boolean canResolve=false;
		for (CanalComm neighbor:cAg.getNeighbours()) {
			canResolve=commLanguage.getLanguage(neighbor).partlyBelongsTo(clause,extResolvableLiterals);
			if (canResolve)
				return neighbor;
		} 
		return null;
	}

	
	
	/**
	 * Identify the number of an agent from its CanalComm.
	 * 
	 * @param ag
	 * @return int
	 */
	public int identify(CanalComm ag) {
		return Integer.valueOf(ag.getName().substring(2));
	}


	/**
	 * Identify the index of the neibhor in the list of the neighboring agents.
	 * 
	 * @param ag
	 * @return int
	 */
	public int identifyNeighbor(CanalComm ag) {
		int ind=commLanguage.identifier(ag);
		if (ind==-1){
			commLanguage.addAgent(ag);
			ind=commLanguage.identifier(ag);
		}
		return ind;
	}
	

	public int getNbNeighbors() {
		return cAg.getNbNeighbours();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * agents.CooperativeDiagnoser#addToListClausesSent(agentCommunicationSystem
	 * .CanalComm, logicLanguage.IndepClause)
	 */

	public boolean addToListClausesSent(CanalComm sender, IndepClause clause) {
		int neighbor = identifyNeighbor(sender);
		listClausesSent.get(neighbor).add(clause);
		if (!isClauseSent(clause, identifyNeighbor(sender))) {
			// listClausesSent.add(clause);
			listClausesSent.get(neighbor).add(clause);
			return true;
		}
		return false;
	}

	/* *************** START/FINISH ****************** */

	@Override
	public void start() {
		super.start();
		
		if (verbose)
			System.out.println("\n" + "Theory of " + this.name + " : \n"
					+ agentTheory.toString());
		for (CanalComm neighbor : cAg.getNeighbours()) {
			askAllResolvableLiterals(neighbor);
		}
	}

	@Override
	public void finish() {
		if (verbose)
			System.out.println("\n" + "Theory of " + this.name + " : \n"
					+ agentTheory.toString());
		agentThread = null;
	}

	/* *************** Variables ****************** */

	public static final int WOC_COMPLEMENTARY = 0;
	public static final int WOC_INTERSECTION = 1;
	public static final int WOC_ORDERED = 2;
	public static boolean newCarcPb = true;
	protected boolean initNewCarc=newCarcPb;
	public static boolean verbose= true;
	private ArrayList<ArrayList<IndepClause>> listClausesSent;
	private IndepPField pField;
	private IndepPField extResolvableLiterals=new IndepPField();
	private long deadline;
}

