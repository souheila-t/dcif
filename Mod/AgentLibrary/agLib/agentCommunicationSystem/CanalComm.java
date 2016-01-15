package agLib.agentCommunicationSystem;

import java.util.Vector;



public class CanalComm {
	/**
	 * This class is a simple FIFO queue holding the messages received 
	 * by an agent. If an agent want to get the new message while the queue is empty
	 * it will wait until a message is received.
	 * Any message fetched by the agent is removed from the queue.
	 * Proper use should ensure that no other agent than the owner ever invoke
	 * the 'get' method.
	 * 'enqueue' should only be invoked by other agents than the owner through 
	 * the CommunicationModule class ('send...' methods).
	 * 
	 *   Static boolean verbose can be switched on and offto indicate whether an
	 *   output should be produced.
	 *   
	 *   * @author gauvain bourgne
	 */

	
	protected Agent owner;
	protected String name;
	protected Vector<Message<?>> contentQueue;
	protected boolean empty;
		
	public static boolean verbose=true;
	
	public Agent getOwner() {
		return owner;
	}
	
	
	public Thread getAgentThread(){
		if(owner instanceof ThreadAgent){
			return ((ThreadAgent)owner).getThread();
		}
		return null;
	}
	
	public boolean isEmpty() {
		return empty;
	}

	public CanalComm(Agent owner) {
		this.owner=owner;
		name=owner.toString();
		contentQueue = new Vector<Message<?>>();
		empty=true;
	}
	
	//build a fake CanalComm
	public CanalComm(String name) {
		this.owner=null;
		contentQueue = null;
		empty=true;
		this.name=name;
	}	
	
	public boolean isUninitialized(){
		return (contentQueue==null);
	}
	
	public String getName(){
		return name;
	}
	

	
	public synchronized void die(){
		notifyAll();
	}
	
	public void finalize(){
		owner=null;
		contentQueue=null;
	}
	
	public synchronized Message<?> get(){
		if (owner==null) return null;
		Message<?> o;
		while (empty == true && owner != null && owner.isAlive()) {
			try {
				//wait for other agents to put value
				wait(5000);
				if(Thread.currentThread().isInterrupted())
					return null;
			} catch (InterruptedException e) { }
		}
		if (owner == null || !owner.isAlive()) {
			return null;
		}
		if(Thread.currentThread().isInterrupted())
			return null;
		o=contentQueue.remove(0);
		if (verbose) {
			String receive = new String();
			receive = receive.concat(owner.toString())
				.concat(" <------  ")
				.concat(o.toString());
			System.out.println(receive);
		}
		empty = contentQueue.isEmpty();
		notifyAll();
		
		return o;
	}
	
	public synchronized void enqueue(Object sender, Message<?> arg){
		if (owner==null) return;
		contentQueue.add(arg);
		if (verbose) {
			String envoi = new String();
			envoi = envoi.concat(sender.toString())
				//.concat("(").concat(arg.getSender().toString()).concat(")")
			  	.concat(" -> ")
				.concat(owner.toString())
				.concat(" : ")
				.concat(arg.toString());
			System.out.println(envoi);
		}
		empty = false;
		notifyAll();		
	}
	
	public String toString(){
		return this.name;
	}
}
