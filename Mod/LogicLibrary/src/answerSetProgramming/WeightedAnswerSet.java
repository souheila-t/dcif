/**
 * 
 */
package answerSetProgramming;

import java.util.List;

import logicLanguage.IndepLiteral;
import genLib.tools.Arguments;

/**
 * @author Gauvain Bourgne
 *
 */
public class WeightedAnswerSet extends AnswerSet{

	public WeightedAnswerSet(){
		super();
	}
	
	public WeightedAnswerSet(List<IndepLiteral> lits){
		super(lits);
	}
	
	public WeightedAnswerSet(String strRepr,String weightStr){
		String listRepr="";
		if (strRepr.startsWith("Best model: "))
			listRepr=strRepr.substring("Best model: ".length());
		listRepr=listRepr.replace('{', '[')
;		listRepr=listRepr.replace('}', ']');
		Arguments arg=Arguments.parse(listRepr);
		for (String strLit:arg){
			IndepLiteral ilit=IndepLiteral.parse(strLit);
			elements.add(ilit);			
		}
		if (weightStr.startsWith("Cost ([Weight:Level]): "))
			weightRepr=weightStr.substring("Cost ([Weight:Level]): ".length());
		else 
			weightRepr=weightStr;
	}

	String weightRepr="";
}
