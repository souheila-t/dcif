/**
 * 
 */
package answerSetProgramming;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.ClauseTypes;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;

import logicLanguage.IndepClause;
import logicLanguage.IndepLiteral;
import genLib.tools.Arguments;


/**
 * @author Gauvain Bourgne
 *
 */
public class AnswerSet {

	public AnswerSet(){
		super();
	}
	
	public AnswerSet(List<Literal> lits){
		elements.addAll(lits);
	}
	
	public AnswerSet(Env env, Options opt, String strRepr) throws ParseException{
		String listRepr=strRepr.replaceAll("{", "[").replaceAll("}", "]");
		Arguments arg=Arguments.parse(listRepr);
		for (String strLit:arg){
			Literal ilit=Literal.parse(env, opt, strLit);
			elements.add(ilit);			
		}
	}

	public Clause toClause(Env env, String name){
		return new Clause(env, name, ClauseTypes.AXIOM, elements);
	}
	
	public List<Literal> getLiterals(){
		List<Literal> res=new ArrayList<Literal>();
		res.addAll(elements);
		return res;
	}
	
	protected List<Literal> elements=new ArrayList<Literal>();
}
