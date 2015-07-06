package base;

import java.util.ArrayList;
import java.util.Collection;

import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.BasicAgent;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.SystemMessage;

public class ActivityChecker extends BasicAgent implements Agent,Runnable {
	

	public ActivityChecker(CommunicationModule cMod, Collection<? extends Agent> ags){
		name="ActivityChecker";
		cSys=cMod;
		agents.addAll(ags);
	}
	
	public synchronized void run(){
		Thread myThread = Thread.currentThread();
		while (agentThread == myThread && !finished){
			try {
				//wait for other agents to put value
				wait(500);
			} catch (InterruptedException e) { }
			finished=true;
			for (Agent ag:agents) {
				if (!ag.isDormant()) {
					finished=false;
					break;
				}
			}
		}
		cSys.send(new SystemMessage(SYS_FINISH,cSys.getComm()),cSys.commSystem);
		//System.out.println(" Finished !");
		
	}
	
	CommunicationModule cSys;
	boolean finished=false;
	Collection<Agent> agents=new ArrayList<Agent>();
	
	
}
