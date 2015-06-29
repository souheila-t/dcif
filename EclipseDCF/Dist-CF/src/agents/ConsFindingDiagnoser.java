/**

 * 

 */

package agents;



import java.util.ArrayList;



import logicLanguage.CNF;

import logicLanguage.IndepClause;

import agLib.agentCommunicationSystem.CanalComm;



/**

 * Diagnoser withe the methods that the agents in the 2 

 * algorithms have in common.

 * 

 * @author Viel Charlotte

 *

 */

public interface ConsFindingDiagnoser<Rules> {

	

	/**

	 * Add a rule to the agent theory.

	 * 

	 * @param ruleSet

	 * @param from

	 * @return boolean

	 */

	public boolean addToTheory(CNF ruleSet, CanalComm from);

	

	/**

	 * Allows to indicate if the agent is working or not.

	 * 

	 * @param dormant

	 */

	public void setDormant(boolean dormant);

	

	/**

	 * Do consequence agent with the agent's theory.

	 * 

	 * @param carcNewCarc

	 *            : allows to choose between Carc and Newcarc

	 * @param topClauses

	 *            : topClauses used in case of Newcarc

	 */

	public void consFinding(ArrayList<IndepClause> topClauses, boolean addConsToTheory, boolean useOriginalTopClauses);

	

}

