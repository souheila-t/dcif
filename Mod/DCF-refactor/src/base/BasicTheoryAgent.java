package base;

import java.util.Collection;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;

import solarInterface.SolProblem;
import stats.ConsFindingAgentStats;
import logicLanguage.CNF;
import logicLanguage.IndepLiteral;
import agLib.agentCommunicationSystem.BasicAgent;
import agLib.agentCommunicationSystem.CanalComm;
import agLib.agentCommunicationSystem.CommunicationModule;
import agLib.agentCommunicationSystem.Network;

public class BasicTheoryAgent extends BasicAgent implements TheoryAgent {

	public BasicTheoryAgent(int id, SolProblem pb,
			CanalComm systComm, Network net, ConsFindingAgentStats das){
		name="Ag"+id;
		CanalComm comm=new CanalComm(this);
		theory = new ConsFindingLocalTheory(pb,id);
		stats=das;
		cAg = new CommunicationModule(comm,systComm, net, das);
	}
	
	public Collection<Literal> getVocabulary(Env env) throws ParseException {		
		return CNF.getVocabulary(env, theory.getClauses());
	}

	public Collection<Literal> getNegatedVocabulary(Env env) throws ParseException {
		return CNF.getNegatedVocabulary(env, theory.getClauses());
	}

	public Collection<Literal> getFullVocabulary(Env env) throws ParseException {
		return CNF.getFullVocabulary(env, theory.getClauses());
	}

	public Env getEnv() {
		return theory.getEnv();
	}
	
	public Options getOptions(){
		return theory.getOptions();
	}
	
	protected ConsFindingLocalTheory theory;
	public ConsFindingAgentStats stats;
	
}
