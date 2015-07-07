package old_logicLanguage;

import java.util.ArrayList;
import java.util.List;

import old_solarInterface.IndepPField;
import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;

public class CommLanguage {
	
	public CommLanguage(String name){
		agents=new ArrayList<CanalComm>();
		commLanguage=new ArrayList<IndepPField>();
		this.name=name;
	}

	public CommLanguage(Agent owner){
		agents=new ArrayList<CanalComm>();
		commLanguage=new ArrayList<IndepPField>();
		this.owner=owner;
		this.name=owner.toString();
	}

	
	public int identifier(CanalComm inconnu){
		int indice = agents.indexOf(inconnu);
		//This part assume that two CanalComm cannot have the same name !!!
		// if this is the case, one of them was a temporary fake
		if (indice==-1){
			indice=identifier(inconnu.getName());
			if (indice!=-1 && agents.get(indice).isUninitialized() && !inconnu.isUninitialized())
				agents.set(indice,inconnu);
		}			
		return indice;
	}
	
	public int identifier(String inconnu){
		int indice=-1;
		for(CanalComm c:agents)
			if (c.getName()==inconnu){
				indice = agents.indexOf(inconnu);
				break;
		}
		return indice;
	}	
	
	public void addAgent(CanalComm ag){
		agents.add(ag);
		commLanguage.add(new IndepPField());
	}
	
	public CanalComm addAgent(String agName){
		CanalComm temp=new CanalComm(agName);
		agents.add(temp);
		commLanguage.add(new IndepPField());
		return temp;
	}
	
	public void removeAgent(CanalComm ag){
		int indice=identifier(ag);
		if (indice<0) return;
		agents.remove(indice);
		commLanguage.remove(indice);
	}
	
	
	//add or set data
	public void addData(CanalComm ag,List<IndepLiteral> language){
		IndepPField p=new IndepPField(language);
		if (identifier(ag)==-1){
			agents.add(ag);
			commLanguage.add(p);
		}
		else
			commLanguage.set(identifier(ag), p);
	}
	
	
	public void addData(String agName,List<IndepLiteral> language){
			IndepPField p = new IndepPField(language);
			if (identifier(agName)==-1){
				agents.add(new CanalComm(agName));
				commLanguage.add(p);
				return;
			}
			commLanguage.set(identifier(agName), p);
	}
	
	
	public void addAllData(CanalComm[] agents, List<IndepLiteral>[] language){
		for (int i=0;i<language.length;i++)
			if (!language[i].isEmpty())
				addData(agents[i],language[i]);
	}
	
	public void addAllData(List<IndepLiteral>[] language){
		for (int i=0;i<language.length;i++)
			if (!language[i].isEmpty())
				addData(new CanalComm("ag"+i),language[i]);
	}
	
	public IndepPField getLanguage(CanalComm agent){
		int indice=identifier(agent);
		if (indice==-1)
			return new IndepPField();
		IndepPField pf=commLanguage.get(indice);
		return pf;
	}
	
	public List <IndepPField> getAllLanguages(){
		ArrayList <IndepPField> result=new ArrayList<IndepPField>();
		for (CanalComm ag:agents){
			result.add(getLanguage(ag));
		}
		return result;
	}
	
	public String toString(){
		String res="Communications languages of "+name+":\n";
		for (int i=0;i<agents.size();i++){
			res+="  with "+agents.get(i)+": "+commLanguage.get(i)+"\n";
		}
		
		return res;
	}
	
	public Agent owner;
	public String name; 
	
	public List<CanalComm> agents;
	public List<IndepPField> commLanguage;
	
}
