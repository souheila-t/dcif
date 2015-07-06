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
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.VarTable;
import org.nabelab.solar.equality.EqType;

public class SymSplitChecker extends OpChecker implements Tags, TermTypes {

  /**
   * Constructs a symmetrical splitting operation checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */
  public SymSplitChecker(Env env, Tableau tableau) {
    super(env, tableau);
  }


  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {

    if (!node.hasTag(EQ_RAW)) 
      return true;
    
    assert(node.getLiteral().isEqualPred());
    assert(node.getEqType() != null);
    
    if (param.getDepthLimit() != 0 && node.getExtDepth() > param.getDepthLimit()) {
      param.setExhaustiveness(false);
      node.markAsNotExhausted();
      return true;
    }
    
    EqType eqType = node.getEqType();
    Literal lit = node.getLiteral().instantiate();
    VarTable varTable = env.getVarTable();
    tableau.stats().incTests(Stats.SYMMETRY_SPLITTING);

    Term term = lit.getTerm();
    Term arg1 = term.getArg(0);
    Term arg2 = term.getArg(1);

    // The literal is positive.
    if (lit.isPositive()) {
      
      Term newVar = Term.createVar(env, varTable.getNumVars());
      Term eq1 = Term.createPredicate(env, EQUAL_PRED, arg1, newVar);
      Term eq2 = Term.createPredicate(env, EQUAL_PRED, arg2, newVar);
      
      if (eqType.arg2IsVar()) {
        ops.add(new SymSplit(env, node, lit));
      }
      else {
        Literal posEq1 = new Literal(env, true,  eq1);
        Literal negEq2 = new Literal(env, false, eq2);
        ops.add(new SymSplit(env, node, posEq1, negEq2));
      }
      if (eqType.arg1IsVar()) {
        Term rev = Term.createPredicate(env, EQUAL_PRED, arg2, arg1);
        Literal posRev = new Literal(env, true, rev);
        ops.add(new SymSplit(env, node, posRev));
      }
      else {
        Literal posEq1 = new Literal(env, true,  eq2);
        Literal negEq2 = new Literal(env, false, eq1);
        ops.add(new SymSplit(env, node, posEq1, negEq2));
      }
    }
    // The literal is negative.
    else {
      if (eqType.arg2IsVar()) {
        ops.add(new SymSplit(env, node, lit));
      }
      else {
        Term newVar = Term.createVar(env, varTable.getNumVars());
        Term eq1 = Term.createPredicate(env, EQUAL_PRED, arg1, newVar);
        Term eq2 = Term.createPredicate(env, EQUAL_PRED, arg2, newVar);
        Literal negEq1 = new Literal(env, false, eq1);
        Literal negEq2 = new Literal(env, false, eq2);
        ops.add(new SymSplit(env, node, negEq1, negEq2));
      }
    }
    
    return true;
  }    

}
