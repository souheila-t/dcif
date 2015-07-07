/**
 * 
 */
package old_answerSetProgramming;

import java.util.ArrayList;
import java.util.List;

import old_logicLanguage.IndepClause;
import old_logicLanguage.IndepLiteral;
import genLib.tools.Arguments;


/**
 * @author Gauvain Bourgne
 *
 */
public class AnswerSet {

	public AnswerSet(){
		super();
	}
	
	public AnswerSet(List<IndepLiteral> lits){
		elements.addAll(lits);
	}
	
	public AnswerSet(String strRepr){
		String listRepr=strRepr.replaceAll("{", "[").replaceAll("}", "]");
		Arguments arg=Arguments.parse(listRepr);
		for (String strLit:arg){
			IndepLiteral ilit=IndepLiteral.parse(strLit);
			elements.add(ilit);			
		}
	}

	public IndepClause toIndepClause(String name){
		return new IndepClause(name,elements);
	}
	
	public List<IndepLiteral> getLiterals(){
		List<IndepLiteral> res=new ArrayList<IndepLiteral>();
		res.addAll(elements);
		return res;
	}
	
	protected List<IndepLiteral> elements=new ArrayList<IndepLiteral>();
}
