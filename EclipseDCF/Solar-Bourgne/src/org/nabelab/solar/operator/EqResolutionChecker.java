/************************************************************************
 Copyright 2003-2009, University of Yamanashi. All rights reserved. 
 By using this software the USER indicates that he or she has read,
 understood and will comply with the following:

 --- University of Yamanashi hereby grants USER non-exclusive permission
 to use, copy and/or modify this software for internal, non-commercial,
 research purposes only. Any distribution, including commercial sale or
 license, of this software, copies of the software, its associated
 documentation and/or modifications of either is strictly prohibited
 without the prior consent of University of Yamanashi. Title to
 copyright to this software and its associated documentation shall at
 all times remain with University of Yamanashi.  Appropriate copyright
 notice shall be placed on all software copies, and a complete copy of
 this notice shall be included in all copies of the associated
 documentation. No right is granted to use in advertising, publicity or
 otherwise any trademark, service mark, or the name of University of
 Yamanashi.

 --- This software and any associated documentation is provided "as is"

 UNIVERSITY OF YAMANASHI MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS
 OR IMPLIED, INCLUDING THOSE OF MERCHANTABILITY OR FITNESS FOR A
 PARTICULAR PURPOSE, OR THAT USE OF THE SOFTWARE, MODIFICATIONS, OR
 ASSOCIATED DOCUMENTATION WILL NOT INFRINGE ANY PATENTS, COPYRIGHTS,
 TRADEMARKS OR OTHER INTELLECTUAL PROPERTY RIGHTS OF A THIRD PARTY.

 University of Yamanashi shall not be liable under any circumstances for
 any direct, indirect, special, incidental, or consequential damages
 with respect to any claim by USER or any third party on account of or
 arising from the use, or inability to use, this software or its
 associated documentation, even if University of Yamanashi has been
 advised of the possibility of those damages.
************************************************************************/

package org.nabelab.solar.operator;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Stats;
import org.nabelab.solar.Subst;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.VarTable;

public class EqResolutionChecker extends OpChecker implements Tags {

  /**
   * Constructs a equality resolution operation checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */
  public EqResolutionChecker(Env env, Tableau tableau) {
    super(env, tableau);
  }

  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {
    Literal lit = node.getLiteral();
    if (lit.isNegEqualPred() && !node.hasTag(EQ_RAW)) {
      
      // Do not apply EQR to nodes that extract a non-variable term from the parent by NEF.
      if (node.getRight() != null && node.getRight().getNEFInfo() != null)
        return true;
      
      stats.incTests(Stats.EQ_RESOLUTION);
      Term term = lit.getTerm();
      int  arg1 = term.getStart() + 1;
      int  arg2 = term.getNext(arg1);
      
      VarTable varTable = env.getVarTable();
      int state = varTable.state();
      Subst g = Term.unify(term, arg1, term, arg2);
      if (g != null) {
        EqResolution eqr = new EqResolution(env, node, g);
        ops.add(eqr);
        varTable.backtrackTo(state);
      }
    }    
    return true;
  }
  
  
}
