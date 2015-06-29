/**

 * 

 */

package obsolete.tools;



import genLib.tools.Arguments;

import java.util.ArrayList;

import java.util.List;



import logicLanguage.CNF;

import logicLanguage.IndepClause;

import logicLanguage.IndepLiteral;



import org.nabelab.solar.parser.ParseException;






/**

 * @author Viel Charlotte

 *

 */

public class Tools {

	

	/**

	 * Transforms a CNF into an array of literals.

	 * 

	 * @param cnf

	 * @return ArrayList<IndepPLiteral>

	 * @throws ParseException

	 */

	public static List<IndepLiteral> CNFToLiterals(CNF cnf) throws ParseException {

		List<IndepLiteral> listeLit = new ArrayList<IndepLiteral>();

		for (IndepClause clause : cnf) {

			listeLit.addAll(clause.getLiterals());

		}

		return listeLit;

	}

	

	/**

	 * Transforms an argument into an array of literals.

	 * 

	 * @param arg

	 * @return ArrayList<IndepPLiteral>

	 */

	public static List<IndepLiteral> ArgumentsToLiterals(Arguments arg) {

		List<IndepLiteral> listeLit = new ArrayList<IndepLiteral>();

		IndepLiteral literal;

		for (String s : arg) {

			literal = IndepLiteral.parse(s);

			listeLit.add(literal);

		}

		return listeLit;

	}

	

}

