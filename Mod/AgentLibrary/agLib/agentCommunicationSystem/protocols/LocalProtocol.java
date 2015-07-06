package agLib.agentCommunicationSystem.protocols;

import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommStatsUpdater;
import agLib.agentCommunicationSystem.Message;

public interface LocalProtocol {

	public boolean gereParProtocol(Message<?> m);
	public void receiveMessage(Message<?> m);
	public void disconnect();
	public LocalProtocol initProtocol(CanalComm target, CommStatsUpdater ctr);
	public LocalProtocol acceptProtocol(CanalComm target, CommStatsUpdater ctr); 
	public void start();	
}
