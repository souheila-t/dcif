/**

 * 

 */

package agents;



import java.util.ArrayList;

import java.util.List;



import logicLanguage.CNF;

import logicLanguage.CommLanguage;

import logicLanguage.IndepClause;

import logicLanguage.IndepLiteral;



import org.nabelab.solar.parser.ParseException;



import solarInterface.IndepPField;

import stats.ConsFindingAgentStats;

import theory.ConsFindingLocalTheory;

import agLib.agentCommunicationSystem.BasicAgent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Network;






/**
 * Basic agent for the 2 consequences finding algorithms.
 * 
 * @author Viel Charlotte
 *
 */

public abstract class ConsFindingAgent extends BasicAgent implements ConsFindingDiagnoser<CNF> {	

	

	/**
	 * Constructs an agent.
	 * 
	 * @param name		: name of the agent
	 * @param theory	: initial theory
	 * @param cSyst		: CanalComm of the system
	 * @param net		: network
	 * @param stats		: stats of the agents
	 */
	public ConsFindingAgent(String name, ConsFindingLocalTheory theory, 
			CanalComm cSyst, Network net, ConsFindingAgentStats stats) {

		agentTheory = theory;
		this.name = name;
		this.stats = stats;
		commLanguage = new CommLanguage(this);
		cAg = new CommunicationModule(new CanalComm(this), cSyst, net, stats);
		commSyst=cSyst;
	}

	public ConsFindingLocalTheory getAgentTheory() {
		return agentTheory;
	}

	/**
	 * @return isRoot
	 */
	public boolean isRoot() {
		return isRoot;
	}
	
	/**
	 * @param set
	 *            isRoot
	 */
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	/**
	 * @return parentIndex
	 */
	public int getParentIndex() {
		return parentIndex;
	}

	/**
	 * @param set
	 *            parentIndex
	 */
	public void setParentIndex(int parentIndex) {
		this.parentIndex = parentIndex;
	}

	/**
	 * @return parent
	 */
	public CanalComm getParent() {
		return parent;
	}

	/**
	 * @param set
	 *            parent
	 */
	public void setParent(CanalComm parent) {
		this.parent = parent;
	}

	/**
	 * @param set
	 *            commSyst
	 */
	public void setCommSyst(CanalComm commSyst) {
		this.commSyst = commSyst;
	}

	
	/**
	 * Add a new neighbor to the agent. Returns true if the neighbor wasn't
	 * already in the list. Returns false otherwise.
	 * 
	 * @param neighbor
	 * @return boolean
	 */
	public boolean addNeighbor(CanalComm neighbor) {

		if(!commLanguage.agents.contains(neighbor)) {
			commLanguage.agents.add(neighbor);
			return true;
		}
		return false;
	}

	

	/**
	 * Check if the clause is in the common language between the agent and his 
	 * neighbor.
	 * 
	 * @param clause
	 * @param neighbor
	 * @return boolean
	 * @throws ParseException
	 */
	protected boolean isClauseInCommLanguage(IndepClause clause, CanalComm neighbor) {

		List<IndepLiteral> arg;
		boolean b = false;
		arg = clause.getLiterals();
		List<IndepLiteral> language = commLanguage.getLanguage(neighbor).getLiterals();

		for (IndepLiteral literal : arg) {
			b = false;
			for (IndepLiteral litLang : language) {
				if(litLang.equals(literal)) {
					b = true;
				}
			}
			if(!b) break;
		}
		return b;
	}

	

	/**
	 * Check if the clause is in the common language between the agent and his 
	 * neighbor.
	 * 
	 * @param clause
	 * @param neighbor
	 * @return boolean
	 * @throws ParseException
	 */
	protected boolean isClauseInCommLanguage(IndepClause clause, CanalComm neighbor, 
			IndepPField originalPField) {

		List<IndepLiteral> arg;
		boolean b = false;
		arg = clause.getLiterals();
		List<IndepLiteral> language = commLanguage.getLanguage(neighbor).getLiterals();
		language.addAll(originalPField.getLiterals());

		for (IndepLiteral literal : arg) {
			b = false;
			for (IndepLiteral litLang : language) {
				if(litLang.equals(literal)) {
					b = true;
				}
			}
			if(!b) break;
		}
		return b;
	}



	

	
	/**
	 * Indicates if the literal has its complementary in the language
	 * 
	 * @param literal
	 * @param neighbor
	 * @return
	 */
	protected boolean isComplLitInCommLanguage(IndepLiteral literal, CanalComm neighbor) {

		List<IndepLiteral> language = commLanguage.getLanguage(neighbor).getLiterals();

		return (language.contains(literal));

	}

	
	/**
	 * Indicates if all the literals in the clause have their complementary
	 * in the language
	 * 
	 * @param clause
	 * @param neighbor
	 * @return boolean
	 */
	protected boolean isComplLitInCommLanguage(IndepClause clause, CanalComm neighbor) {

		List<IndepLiteral> arg;
		arg = clause.getLiterals();
		
		for (IndepLiteral literal : arg) {			
			if(!isComplLitInCommLanguage(literal, neighbor))
				return false;
		}
		return true;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see partitionBasedConsFinding.PBDiagnoser#addToTheory(java.lang.Object,
	 * agentCommunicationSystem.CanalComm)
	 */
	public boolean addToTheory(CNF ruleSet, CanalComm from) {
		return agentTheory.addToTheory(ruleSet);
	}



	public abstract void consFinding(ArrayList<IndepClause> topClauses, boolean addConsToTheory, boolean originalTopClauses);

	
	

	protected boolean isRoot;
	protected int parentIndex;
	protected CanalComm parent;
	
	protected ConsFindingLocalTheory agentTheory;

	protected CommLanguage commLanguage;

	public ConsFindingAgentStats stats;

	public CanalComm commSyst;

	public static boolean verbose;

}

