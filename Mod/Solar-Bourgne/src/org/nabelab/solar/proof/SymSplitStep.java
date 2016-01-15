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

package org.nabelab.solar.proof;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Node;
import org.nabelab.solar.Tableau;
import org.nabelab.solar.Tags;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;
import org.nabelab.solar.VarTable;
import org.nabelab.solar.operator.Operator;
import org.nabelab.solar.operator.SymSplit;

/**
 * @author nabesima
 *
 */
public class SymSplitStep extends ProofStep implements Tags, TermTypes {

  /**
   * Constructs a symmetrical splitting step.
   * @param env    the environment.
   * @param clause the tableau clause.
   */
  public SymSplitStep(Env env, Literal lit1, Literal lit2) {
    super(env);
    this.lit1 = lit1;
    this.lit2 = lit2;
  }

  /**
   * Converts this proof step to the corresponding operator.
   * @param tableau the tableau. 
   * @param node    the node to which this operator is applied.
   * @return the corresponding operator.
   */
  public Operator convert(Tableau tableau, Node node) {
    
    if (!node.hasTag(EQ_RAW)) 
      return null;
    
    Literal lit = node.getLiteral();
    if (!lit.isPosEqualPred())
      return null;
    
    VarTable varTable = env.getVarTable();
    Term term = lit.getTerm();
    boolean arg1IsVar = false;
    boolean arg2IsVar = false;
    int arg1stPos = term.getStart() + 1;
    int arg2ndPos = term.getNext(arg1stPos);
    if (term.getType(arg1stPos) == Term.VARIABLE) 
      arg1IsVar = (varTable.getTailValue(term.getName(arg1stPos) + term.getOffset()) == null); 
    if (term.getType(arg1stPos) == Term.VARIABLE)
      arg2IsVar = (varTable.getTailValue(term.getName(arg2ndPos) + term.getOffset()) == null);
      
    if (arg1IsVar && arg2IsVar) {
      if (lit1 == null && lit2 == null)
        return null; // TODO modify new SymSplit(env, node);
      return null;
    }

    Term arg1 = term.getArg(0);
    Term arg2 = term.getArg(1);
    Term newVar = Term.createVar(env, varTable.getNumVars());
    
    Term lit1arg1 = lit1.getArg(0);
    Term lit1arg2 = lit1.getArg(1);
    Term lit2arg1 = lit2.getArg(0);
    Term lit2arg2 = lit2.getArg(1);
    
    if (!Term.equals(newVar, lit1arg2) || !Term.equals(newVar, lit2arg2))
      return null;
    
    if (Term.equals(arg1, lit1arg1) && Term.equals(arg2, lit2arg1))
      return new SymSplit(env, node, lit1, lit2);
    else if (Term.equals(arg1, lit2arg1) && Term.equals(arg2, lit1arg1))
      return new SymSplit(env, node, lit1, lit2);
    
    return null;
  }
    
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "symmetry splitting";
  }
  
  /** The first literal which is used to extend this node. */
  private Literal lit1 = null;
  /** The second literal which is used to extend this node. */
  private Literal lit2 = null;  

}
