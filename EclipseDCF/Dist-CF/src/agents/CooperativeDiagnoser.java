/**

 * 

 */

package agents;

import java.util.List;

import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;
import agLib.agentCommunicationSystem.CanalComm;

/**
 * Methods useful for an agent of the cooperative consequence 
 * finding algorithm.
 * 
 * @author Viel Charlotte
 * 
 */
public interface CooperativeDiagnoser<Rules> extends
		ConsFindingDiagnoser<Rules> {

	/**
	 * Ask the agent ag for the literals contained in ag's theory, 
	 * so that we can deduce the common language between the 2 agents.
	 * 
	 * @param ag
	 */
	public void askAllResolvableLiterals(CanalComm ag);

	/**
	 * Send all the literals contained in the theory of the agent to
	 * the other agent ag.
	 * 
	 * @param ag
	 */
	public void sendAllResolvableLiterals(CanalComm ag);

	/**
	 * Update the common language between this agent and its neighbor 
	 * ag. The set listeLit is the list of all the literals contained
	 * in the theory of ag.
	 * 
	 * @param ag
	 * @param listeLit
	 */
	public void updateCommLanguage(CanalComm ag, List<IndepLiteral> listeLit);

	/**
	 * Returns the number of the neighbors for the agent.
	 * 
	 * @return int
	 */
	public int getNbNeighbors();
	
	/**
	 * Add the clause to the list of the clauses sent to the agent "sender".
	 * 
	 * @param sender
	 * @param clause
	 * @return boolean
	 */
	public boolean addToListClausesSent(CanalComm sender, IndepClause clause);

}

