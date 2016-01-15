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

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Stats;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.VarTable;
import org.nabelab.solar.equality.WeightMap;

public class NegEqFlatAllChecker  extends OpChecker implements Tags, TermTypes {

  /**
   * Constructs a negative equality fattening checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */
  public NegEqFlatAllChecker(Env env, Tableau tableau) {
    super(env, tableau);
  }

  /**
   * Returns the applicable operators to the specified node.
   * @param node the node to check.
   * @param ops  the applicable operators.
   * @return true if the tableau is not redundant.
   */
  public boolean check(Node node, Operators ops) {

    Literal lit = node.getOrigin();
    if (!lit.isNegEqualPred())
      return true;
    
    Term term = lit.getTerm();
    if (term.getArg(0).getStartType() != FUNCTION) 
      return true;

    // NEFA:
    //       f(..., nv1, ..., nvn, ...) != t  
    // ----------------------------------------------
    // nv1 != z1  ...  nvn != zn  f(..., z, ...) != t
    
    VarTable  varTable  = env.getVarTable();
    SymTable  symTable  = env.getSymTable();
    WeightMap weightMap = env.getWeightMap();
    int       numVars   = varTable.getNumVars();
      
    tableau.stats().incTests(Stats.NEG_EQ_FLATTENING);

    ArrayList<Literal> lits = new ArrayList<Literal>();
    boolean modified = true;
    while (modified) {
      modified = false;

      // For each argument of f(...), if the argument is non-variable, 
      // then applies negative equality flattening.
      Term left = term.getArg(0);
      int  from = left.getStart();
      int  to   = left.getNext(from);
      for (int i=from+1; i < to; i=left.getNext(i)) {
        if (left.getType(i) == VARIABLE)
          continue;

        if (weightMap != null && weightMap.isMin(left.getName(i), left.getType(i)))
          continue;

        if (param.getDepthLimit() != 0 && node.getExtDepth() >= param.getDepthLimit()) {
          param.setExhaustiveness(false);
          node.markAsNotExhausted();
          return true;
        }

        Term nvarg = Term.newSubTerm(left, i);      
        Term newVar = Term.createVar(env, numVars++);

        Term eq = Term.createPredicate(env, symTable.getEqualPredName(), nvarg, newVar);
        term = term.replaceWithVar(i, newVar.getStartName());

        Literal negEq = new Literal(env, false, eq);
        negEq.setReductionOrder(lit.getReductionOrder());
        negEq.getNumExts(true, tableau.getClauseDB());          
        lits.add(negEq);

        modified = true;
        break;
      }
    }

    if (lits.size() > 0) {
      Literal negEq = new Literal(env, false, term);
      negEq.setReductionOrder(lit.getReductionOrder());
      negEq.getNumExts(true, tableau.getClauseDB());
      lits.add(negEq);

      ops.add(new NegEqFlatAll(env, node, lits));
    }
    
    return true;
  }    

}