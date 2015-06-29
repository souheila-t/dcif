package base;

import java.util.Collection;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;

import agLib.agentCommunicationSystem.Agent;
import logicLanguage.IndepLiteral;

public interface TheoryAgent extends Agent{
	public Collection<Literal> getVocabulary(Env env) throws ParseException;
	public Collection<Literal> getNegatedVocabulary(Env env) throws ParseException;
	public Collection<Literal> getFullVocabulary(Env env) throws ParseException;
	public Env getEnv();
	public Options getOptions();
}
