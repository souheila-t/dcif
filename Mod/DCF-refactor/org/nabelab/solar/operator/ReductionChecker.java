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

import org.nabelab.solar.CFP;
import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Stats;
import org.nabelab.solar.Subst;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.VarTable;

/**
 * @author nabesima
 *
 */
public class ReductionChecker extends OpChecker implements Tags {

  /**
   * Constructs a reduction operation checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */
  public ReductionChecker(Env env, Tableau tableau) {
    super(env, tableau);
  }

  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {
    Literal lit   = node.getLiteral();
    
    // Do not allow to reduce the negative equal node whose first argument is variable.
    boolean useR1 = true;
    if (opt.getEqType() >= CFP.EQ_SMT) {
      if (lit.isNegEqualPred()) {
        Term term = lit.getTerm();
        int arg1stPos = term.getStart() + 1;
        if (term.getType(arg1stPos) == Term.VARIABLE) {
          VarTable varTable = env.getVarTable();
          if (varTable.getTailValue(term.getName(arg1stPos) + term.getOffset()) == null) {
            useR1 = false;
          }
        }
      }
      // Do not allow to reduce the raw positive equal predicate.
      if (useR1 && node.hasTag(EQ_RAW))
        useR1 = false;
    }
    
    // Use order preserving reduction?
    boolean useOPR = opt.use(USE_ORDER_PRESERVING_REDUCTION);
    if (useOPR && opt.getEqConstraintType() != CFP.EQ_CONSTRAINTS_NONE && lit.isEqualPred())
      useOPR = false;
    
    Node ancestor = node.getParent();
    while (!ancestor.isRoot()) {
      Literal alit = ancestor.getLiteral();
      if (lit.getName() == alit.getName()) {
        if (lit.isPositive() == alit.isNegative()) {
          // REDUCTION CHECK
          if (useR1) {
            // Do not allow to reduce this positive equal node with a negative equal node whose first argument is variable.
            boolean useR2 = true;
            if (opt.getEqType() >= CFP.EQ_SMT) {
              if (alit.isNegEqualPred()) {
                Term term = alit.getTerm();
                int arg1stPos = term.getStart() + 1;
                if (term.getType(arg1stPos) == Term.VARIABLE) {
                  VarTable varTable = env.getVarTable();
                  if (varTable.getTailValue(term.getName(arg1stPos) + term.getOffset()) == null) {
                    useR2 = false;
                  }
                }
              }
              // Do not allow to reduce this node with the raw positive equal predicate.
              if (useR2 && ancestor.hasTag(EQ_RAW))
                useR2 = false;
            }
            if (useR2) {
              Subst g = lit.isCompUnifiable(alit);
              stats.incTests(Stats.REDUCTION);
              stats.incTests(Stats.IDENTICAL_REDUCTION);
              if (g != null) {
                if (opt.use(USE_IDENTICAL_REDUCTION) && g.isEmpty()) {
                  IdenticalReduction red = new IdenticalReduction(env, node, ancestor);
                  ops.add(red);
                  break;              
                }
                else if (!useOPR || lit.getReductionOrder() < alit.getReductionOrder()) {
                  Reduction red = new Reduction(env, node, ancestor, g);
                  ops.add(red);
                }
              }
            }
          }
        }
      }
      
      if (opt.use(USE_IDENTICAL_C_REDUCTION)) {
        tableau.stats().incTests(Stats.IDENTICAL_C_REDUCTION);
        Node solved = ancestor.containsFoldingUp(node); 
        if (solved != null) {
          IdenticalCReduction fup = new IdenticalCReduction(env, node, ancestor, solved);
          ops.add(fup);
          break;
        }
      }
      
      if (opt.use(USE_IDENTICAL_FOLDING_DOWN)) {
        tableau.stats().incTests(Stats.IDENTICAL_FOLDING_DOWN);
        Node solving = ancestor.containsFoldingDown(node);
        if (solving != null) {
          IdenticalFoldingDown fdown = new IdenticalFoldingDown(env, node, ancestor, solving);
          ops.add(fdown);
          break;
        } 
//        if (ancestor.compContainsFoldingDown(node) != null) {
//          return false;
//          //System.out.print("@");
//        }
      }
      
      ancestor = ancestor.getParent();
    }
    return true;
  }
  
}
