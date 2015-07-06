package agLib.agentCommunicationSystem;

public interface CommStatsUpdater {

	/**
	 * The aim of this interface is to weave specific measurement into the 
	 * communication module. Those methods are invoked each a message is
	 * send receveid, or whenever a local protocol is initiated or acceted.
	 * Classically, implementation of this interface will increase some 
	 * counter(s) depending on the kind of message (its MessageKey : class + type).
	 * 
	 *   * @author gauvain bourgne
	 */

	public void sentMessages(Message<?> m, int nbTarget);
	public void receivedMessages(Message<?> m);
	public void initLocalProtocol();
	public void acceptLocalProtocol();
}
