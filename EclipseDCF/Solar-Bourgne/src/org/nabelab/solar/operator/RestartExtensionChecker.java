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

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Conseq;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Stats;
import org.nabelab.solar.Tableau;

/**
 * @author nabesima
 *
 */
public class RestartExtensionChecker extends ExtensionChecker {

  /**
   * Constructs a restart extension operation checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */  
  public RestartExtensionChecker(Env env, Tableau tableau) {
    super(env, tableau);
    
    List<Clause> clauses = tableau.getCFP().getClauses();
    for (Clause clause : clauses) 
      if (clause.isNegative())
        negClauses.add(clause);
  }

  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {
    
    Literal lit = node.getLiteral();
    
    //if (lit.isNegative() || lit.hasTag(NO_RESTART)) // lit.isSymConnPred())
    if (!lit.isPosEqualPred())
      return super.check(node, ops);
    
    if (lit.isMaxGeneral())
      return super.check(node, ops);

    if (opt.use(USE_UNIT_AXIOM_MATCHING)) {
      tableau.stats().incTests(Stats.UNIT_AXIOM_MATCHING);
      Clause uaxiom = tableau.getClauseDB().hasCompSubsumingUnitAxiom(lit);
      if (uaxiom != null) {  
        ops.add(new UnitAxiomMatching(env, node, uaxiom));
        return true;
      }
    }
    
    if (opt.use(USE_UNIT_LEMMA_MATCHING)) {
      tableau.stats().incTests(Stats.UNIT_LEMMA_MATCHING);
      Conseq ulemma = tableau.getClauseDB().hasCompSubsumingUnitLemma(lit);
      if (ulemma != null) {
        ops.add(new UnitLemmaMatching(env, node, ulemma));
        return true;
      }
    }
    
    int offset = env.getVarTable().getNumVars();
    for (Clause clause : negClauses)
      ops.add(new RootExtension(env, node, clause, offset));
    
    return true;
  }
  
  /** The set of negative clauses. */
  private List<Clause> negClauses = new ArrayList<Clause>();
}
