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

import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Skipped;
import org.nabelab.solar.Stats;
import org.nabelab.solar.Subst;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Unifiable;
import org.nabelab.solar.pfield.PFieldChecker;
import org.nabelab.solar.pfield.PFieldItem;

/**
 * @author nabesima
 *
 */
public class SkipChecker extends OpChecker implements Tags {

  /**
   * Constructs a skip operation checker.
   * @param env       the environment.
   * @param tableau   the tableau.
   * @param pfChecker the production field checker.
   */
  public SkipChecker(Env env, Tableau tableau, PFieldChecker pfChecker) {
    super(env, tableau);
    this.pfChecker = pfChecker;
    this.skipped   = tableau.getSkipped();
  }
  
  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {
    Literal lit = node.getLiteral();
    
    if (node.hasTag(NOT_SKIPPABLE))
      return true;
    
    tableau.stats().incTests(Stats.MERGE);
    tableau.stats().incTests(Stats.FACTORING);
    List<Unifiable<Node>> unifiables = skipped.findUnifiable(lit);
    if (unifiables != null) {
      for (int i=0; i < unifiables.size(); i++) {
        Unifiable<Node> unif = unifiables.get(i);
        Subst g      = unif.getSubst();
        Node  target = unif.getObject(); 
        if (g.isEmpty()) {
          ops.add(new Merge(env, node, target));
          return true;
        }
        ops.add(new Factoring(env, node, target, g));
      }    
    }
    
    tableau.stats().incTests(Stats.SKIP);
    List<Unifiable<PFieldItem>> unifs = pfChecker.getUnifiableItems(lit);
    if (unifs != null) 
      for (Unifiable<PFieldItem> unif : unifs) 
        ops.add(new Skip(env, node, unif));
//    if (pfChecker.belongs(lit))
//      ops.add(new Skip(env, node, pfChecker));
    
    return true;
  }  
  
  
  /** The production field checker. */
  private PFieldChecker pfChecker = null;   
  /** The set of skipped literals. */
  private Skipped skipped = null;
  
}
