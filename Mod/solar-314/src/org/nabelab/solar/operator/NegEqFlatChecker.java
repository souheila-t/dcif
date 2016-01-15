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
import org.nabelab.solar.SymTable;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.VarTable;
import org.nabelab.solar.equality.NEFInfo;
import org.nabelab.solar.equality.WeightMap;

public class NegEqFlatChecker  extends OpChecker implements Tags, TermTypes {

  /**
   * Constructs a negative equality fattening checker.
   * @param env     the environment.
   * @param tableau the tableau.
   */
  public NegEqFlatChecker(Env env, Tableau tableau) {
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
    Term left = term.getArg(0);
    Term right = term.getArg(1);

    VarTable  varTable  = env.getVarTable();
    SymTable  symTable  = env.getSymTable();
    WeightMap weightMap = env.getWeightMap();

    Term newVar = Term.createVar(env, varTable.getNumVars());
    
    NEFInfo info = node.getNEFInfo();
    int orgArgIdx = 0;
    int subArgIdx = -1;
    if (info != null) {
      orgArgIdx = info.getOrgArgIdx();
      subArgIdx = info.getSubArgIdx();
    }

    tableau.stats().incTests(Stats.NEG_EQ_FLATTENING);

    // NEF1:
    //       f(..., nv, ...) != t  
    // -----------------------------
    // nv != z   f(..., z, ...) != t
    if (left.getStartType() == FUNCTION && orgArgIdx == 0) {
    
      // this node = -equal(f(...), right)
      Term func  = left;
    
      // For each argument of f(...), if the argument is non-variable, 
      // then applies negative equality flattening.
      int from = func.getStart();
      int to   = func.getNext(from);
      int arg  = 0;
      for (int i=from+1; i < to; i=func.getNext(i), arg++) {
        if (func.getType(i) == VARIABLE || arg <= subArgIdx)
          continue;

        if (weightMap != null && weightMap.isMin(func.getName(i), func.getType(i)))
          continue;
        
        if (param.getDepthLimit() != 0 && node.getExtDepth() >= param.getDepthLimit()) {
          param.setExhaustiveness(false);
          node.markAsNotExhausted();
          return true;
        }

        Term nvarg = Term.newSubTerm(func, i);      
        
        Term eq1 = Term.createPredicate(env, symTable.getEqualPredName(), nvarg, newVar);
        Term eq2 = term.replaceWithVar(i, newVar.getStartName());

//        Term newFunc = left.replaceWithVar(i, newVar.getStartName());
//        Term eq22 = Term.createPredicate(env, symTable.getEqualPredName(), newFunc, right);
//        if (!eq2.toString().equals(eq22.toString())) {
//          System.out.println("eq2  = " + eq2);
//          System.out.println("eq22 = " + eq22);
//          System.out.println("eq2  = " + eq2.toSimpString());
//          System.out.println("eq22 = " + eq22.toSimpString());
//          Term eq222 = Term.createPredicate(env, symTable.getEqualPredName(), newFunc, right);
//        }

        Literal negEq1 = new Literal(env, false, eq1);
        Literal negEq2 = new Literal(env, false, eq2);
        negEq1.setReductionOrder(lit.getReductionOrder());
        negEq2.setReductionOrder(lit.getReductionOrder());
        // TODO: performance test for b219
        //negEq1.getNumSyms(false);
        //negEq2.getNumSyms(false);
        //negEq1.setNumExts(lit.getNumExts());    // estimated value
        //negEq2.setNumExts(lit.getNumExts());    // estimated value
        negEq1.getNumExts(true, tableau.getClauseDB());
        negEq2.getNumExts(true, tableau.getClauseDB());
        ops.add(new NegEqFlat(env, node, negEq1, negEq2, new NEFInfo(0, arg)));
        
        //return true; // break;
      }

    }
    
    // NEF2:
    //       t != f(..., nv, ...)  
    // -----------------------------
    // nv != z   t != f(..., z, ...)
    if (right.getStartType() == FUNCTION) {
     
      // this node = -equal(left, f(...))
      Term func  = right;
    
      // For each argument of f(...), if the argument is non-variable, 
      // then applies negative equality flattening.
      int from = func.getStart();
      int to   = func.getNext(from);
      int arg  = 1;
      for (int i=from+1; i < to; i=func.getNext(i), arg++) {
        if (func.getType(i) == VARIABLE || (orgArgIdx == 1 && arg <= subArgIdx))
          continue;

        if (weightMap.isMin(func.getName(i), func.getType(i)))
          continue;
        
        if (param.getDepthLimit() != 0 && node.getExtDepth() >= param.getDepthLimit()) {
          param.setExhaustiveness(false);
          node.markAsNotExhausted();
          return true;
        }

        Term nvarg = Term.newSubTerm(func, i);      
        //Term newFunc = Term.createFunctionWithVar(env, right, i, newVar.getStartName());
        
        Term eq1 = Term.createPredicate(env, symTable.getEqualPredName(), nvarg, newVar);
        Term eq2 = term.replaceWithVar(i, newVar.getStartName());
        Literal negEq1 = new Literal(env, false, eq1);
        Literal negEq2 = new Literal(env, false, eq2);
        negEq1.setReductionOrder(lit.getReductionOrder());
        negEq2.setReductionOrder(lit.getReductionOrder());
        // TODO: performance test for b219
//        negEq1.getNumSyms(false);
//        negEq2.getNumSyms(false);
        //negEq1.setNumExts(lit.getNumExts());    // estimated value
        //negEq2.setNumExts(lit.getNumExts());    // estimated value
        negEq1.getNumExts(true, tableau.getClauseDB());
        negEq2.getNumExts(true, tableau.getClauseDB());        
        ops.add(new NegEqFlat(env, node, negEq1, negEq2, new NEFInfo(1, arg)));
        
        //return true; // break;
      }
      
    }

    return true;
  }    

}
