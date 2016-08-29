/**
 * 
 */
package org.nabelab.solar.operator;

import org.nabelab.solar.Env;
import org.nabelab.solar.Node;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;

/**
 * @author nabesima
 *
 */
public class ConquerorChecker extends OpChecker implements Tags {
  
  /**
   * Constructs a conquest checker.
   * @param env       the environment.
   * @param tableau   the tableau.
   */
  public ConquerorChecker(Env env, Tableau tableau) {
    super(env, tableau);
  }

  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {
    if (node.isDivided())
      ops.add(new Conqueror(env, node));
    return true;
  }

}
