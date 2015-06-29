/**

 * 

 */

package agents;



import java.util.ArrayList;



import agLib.agentCommunicationSystem.CanalComm;



/**

 * Methods useful for an agent of the partition-based consequence 

 * finding algorithm.

 * 

 * @author Viel Charlotte

 * 

 */

public interface PBDiagnoser<Rules> extends ConsFindingDiagnoser<Rules> {



	/**

	 * Send a message to the parent of the agent meaning the agent is finished.

	 */

	public void sendEndToParent();



	public int getNbEndReceived();



	/**

	 * Increment nbEndReceived

	 */

	public void incNbEndReceived();



	/**

	 * Returns the list of all the sons the agent has.

	 * 

	 * @see agents.PBDiagnoser#getFils()

	 */

	public ArrayList<CanalComm> getSons();



	public boolean isCarcDone();



}

