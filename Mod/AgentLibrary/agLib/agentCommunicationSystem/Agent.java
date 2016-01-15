package agLib.agentCommunicationSystem;

/**
 * This interface provides some very basic functions of an agent, that should 
 * be accessible for the main system.
 * It is mainly used to store groups of agent in the class representing the MAS
 * (or a subgroup of agents) in a given application.
 * It might also be used to represent an agent from another one point of view, 
 * but we would in general prefer to idientify the agent with its CanalComm in
 * this case.
 *  
 *   * @author gauvain bourgne
 */

public interface Agent {
	/** 
	 * @return true if the agent is ''alive'' (meaning that it is currently 
	 * running and should be able to received or proceeds messages)
	 */
	public boolean isAlive();
	/** 
	 * @return the CanalComm of this agent. This allow communications or
	 * identification for a more external reference.
	 */
	public CanalComm getComm();
	/** 
	 * @return true if the agent is ''dormant'' (meaning that the agent is not
	 * actively sending message unless sollicited)
	 */
	public boolean isDormant();
	/** 
	 * Set the agent in a dormant state or wake it.
	 * Agent in dormant state do not sollicit other agents, but might still 
	 * answer direct queries. 
	 */
	public void setDormant(boolean val);
	
}