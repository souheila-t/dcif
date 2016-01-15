package agLib.agentCommunicationSystem;

public interface ThreadAgent extends Agent, Runnable {
	public void start();
	public void finish();
	public Thread getThread();
	public void stop();
}
