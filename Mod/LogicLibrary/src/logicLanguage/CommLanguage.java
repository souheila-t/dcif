package logicLanguage;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.pfield.PField;

import solarInterface.IndepPField;
import agLib.agentCommunicationSystem.Agent;
import agLib.agentCommunicationSystem.CanalComm;

public class CommLanguage {
	
	public CommLanguage(String name){
		agents=new ArrayList<CanalComm>();
		commLanguage=new ArrayList<PField>();
		this.name=name;
	}

	public CommLanguage(Agent owner){
		agents=new ArrayList<CanalComm>();
		commLanguage=new ArrayList<PField>();
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
	
	public void addAgent(Env env, Options opt, CanalComm ag){
		agents.add(ag);
		commLanguage.add(new PField(env, opt));
	}
	
	public CanalComm addAgent(Env env, Options opt, String agName){
		CanalComm temp=new CanalComm(agName);
		agents.add(temp);
		commLanguage.add(new PField(env, opt));
		return temp;
	}
	
	public void removeAgent(CanalComm ag){
		int indice=identifier(ag);
		if (indice<0) return;
		agents.remove(indice);
		commLanguage.remove(indice);
	}
	
	
	//add or set data
	public void addData(Env env, Options opt, CanalComm ag, List<PLiteral> language){
		PField p = IndepPField.createPField(env, opt, language);
		if (identifier(ag)==-1){
			agents.add(ag);
			commLanguage.add(p);
		}
		else
			commLanguage.set(identifier(ag), p);
	}
	
	
	public void addData(Env env, Options opt, String agName, List<PLiteral> language){
			PField p = IndepPField.createPField(env, opt, language);
			if (identifier(agName)==-1){
				agents.add(new CanalComm(agName));
				commLanguage.add(p);
				return;
			}
			commLanguage.set(identifier(agName), p);
	}
	
	
	public void addAllData(Env[] envs, Options[] opts, CanalComm[] agents, List<PLiteral>[] language){
		for (int i=0;i<language.length;i++)
			if (!language[i].isEmpty())
				addData(envs[i], opts[i], agents[i],language[i]);
	}
	
	public void addAllData(Env[] envs, Options[] opts, List<PLiteral>[] language){
		for (int i=0;i<language.length;i++)
			if (!language[i].isEmpty())
				addData(envs[i], opts[i], new CanalComm("ag"+i),language[i]);
	}
	
	public PField getLanguage(Env env, Options opt, CanalComm agent){
		int indice=identifier(agent);
		if (indice==-1)
			return new PField(env, opt);
		PField pf=commLanguage.get(indice);
		return pf;
	}
	
	public List <PField> getAllLanguages(){
		ArrayList <PField> result=new ArrayList<PField>();
		for (CanalComm ag:agents){
			result.add(getLanguage(null, null, ag));
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
	
	//private Env env;
	//private Options opt;
	public Agent owner;
	public String name; 
	
	public List<CanalComm> agents;
	public List<PField> commLanguage;
	
}
