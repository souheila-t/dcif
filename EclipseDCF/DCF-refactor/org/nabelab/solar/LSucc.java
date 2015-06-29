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

package org.nabelab.solar;

/**
 * @author nabesima
 *
 */
public class LSucc extends Subst implements TermTypes {

  /**
   * Constructs a empty success.
   * @param env   the environment.
   * @param conq  a sub-consequence.
   */
  public LSucc(Env env, Clause conq) {
    this.env      = env;
    this.varTable = env.getVarTable();
    this.conseqs  = new ConseqSet(env);
    addConseq(conq);
  }
  
  /**
   * Constructs a empty success.
   * @param env    the environment.
   * @param conqs  a set of sub-consequences.
   */
  public LSucc(Env env, ConseqSet conqs) {
    this.env      = env;
    this.varTable = env.getVarTable();
    this.conseqs  = new ConseqSet(env);
    addConseqs(conqs);
  }
  
  /**
   * Returns true if this success is more general.
   * @param orgNumVars  the original number of variables.
   * @param conq        the skipped nodes.
   * @return true if this success is more general.
   */
  public boolean isMoreGeneral(int orgNumVars, Clause conq) {

//    if (subConqs.size() > conq.size())
//      return false;
    
    // Example 1:
    //  {Z/X}           <-> {Z/Y, X/a, Y/a}
    //  {Z/Y, X/Y}      <-> {Z/Y, X/a, Y/a}
    //  {Z/Y, X/a, Y/a} <-> {Z/Y, X/a, Y/a} ... {Z/X} is more general than {Z/Y, X/a, Y/a}.
    // Example 2:
    //  {Z/X}           <-> {Z/Y, X/a, Y/b}
    //  {Z/Y, X/Y}      <-> {Z/Y, X/a, Y/b}
    //  {Z/Y, X/a, Y/a} <-> {Z/Y, X/a, Y/b} ... {Z/X} and {Z/Y, X/a, Y/b} are not comparable.
    // Example 3:
    //  {Z/X}           <-> {Z/Y, Y/a}
    //  {Z/Y, X/Y}      <-> {Z/Y, Y/a}      ... {Z/X} and {Z/Y, X/a, Y/b} are not comparable.
    
    int  state = varTable.state();
    Subst diff = env.getLSuccSubst();
    
    // At least, the current substitution must have all variable used in the past
    // substitution (this object).
    for (int i=0; i < size; i++) {
      Term past = vals[i];
      Term curr = varTable.getValue(vars[i]);
      if (curr == null) {
        varTable.backtrackTo(state);
        return false;
      }
      //Subst g = past.isSubsuming(curr);
      Subst g = past.subsumes(curr);
      if (g == null) {
        varTable.backtrackTo(state);
        return false;
      }
      for (int j=0; j < g.size(); j++)
        if (g.getVar(j) >= 0)
          diff.add(g.getVar(j), g.getVal(j), 0);
      //diff.add(g);
    }
    
    varTable.backtrackTo(state);

    // Checks that the composed past substitution is more general than the current substitution.
    for (int i=0; i < diff.size; i++) {
      Term curr = varTable.getValue(diff.vars[i]);
      if (curr == null) 
        return false;
    }
    
    // If a set of sub-consequecens is empty, then this success is more general.
    if (conseqs.hasEmptyClause())
      return true;

    // TODO: Currently, the generality between two sets of sub-consequences can not be defined.
    return false;
    
//    varTable.substitute(diff);
//        
//    OuterLoop:
//    for (Literal lit : subConqs) {
//      for (Literal curLit : curSkipped) 
//        if (Literal.equals(lit, curLit))   
//          continue OuterLoop;
//      varTable.backtrackTo(state);
//      return false;
//    }
//
//    varTable.backtrackTo(state);
//    return true;
  }
  
  /**
   * Returns true if this success is more general.
   * @param orgNumVars  the original number of variables.
   * @param conqs       a set of consequences.
   * @return true if this success is more general.
   */
  public boolean isMoreGeneral(int orgNumVars, ConseqSet conqs) {

//    if (subConqs.size() > conqs.size())
//      return false;

    // Example 1:
    //  {Z/X}           <-> {Z/Y, X/a, Y/a}
    //  {Z/Y, X/Y}      <-> {Z/Y, X/a, Y/a}
    //  {Z/Y, X/a, Y/a} <-> {Z/Y, X/a, Y/a} ... {Z/X} is more general than {Z/Y, X/a, Y/a}.
    // Example 2:
    //  {Z/X}           <-> {Z/Y, X/a, Y/b}
    //  {Z/Y, X/Y}      <-> {Z/Y, X/a, Y/b}
    //  {Z/Y, X/a, Y/a} <-> {Z/Y, X/a, Y/b} ... {Z/X} and {Z/Y, X/a, Y/b} are not comparable.
    // Example 3:
    //  {Z/X}           <-> {Z/Y, Y/a}
    //  {Z/Y, X/Y}      <-> {Z/Y, Y/a}      ... {Z/X} and {Z/Y, X/a, Y/b} are not comparable.
    
    int  state = varTable.state();
    Subst diff = env.getLSuccSubst();
    
    // At least, the current substitution must have all variable used in the past
    // substitution (this object).
    for (int i=0; i < size; i++) {
      Term past = vals[i];
      Term curr = varTable.getValue(vars[i]);
      if (curr == null) {
        varTable.backtrackTo(state);
        return false;
      }
      //Subst g = past.isSubsuming(curr);
      Subst g = past.subsumes(curr);
      if (g == null) {
        varTable.backtrackTo(state);
        return false;
      }
      for (int j=0; j < g.size(); j++)
        if (g.getVar(j) >= 0)
          diff.add(g.getVar(j), g.getVal(j), 0);
      //diff.add(g);
    }
    
    varTable.backtrackTo(state);

    // Checks that the composed past substitution is more general than the current substitution.
    for (int i=0; i < diff.size; i++) {
      Term curr = varTable.getValue(diff.vars[i]);
      if (curr == null) 
        return false;
    }
    
    // If a set of sub-consequecens is empty, then this success is more general.
    if (conseqs.hasEmptyClause())
      return true;

    // TODO: Currently, the generality between two sets of sub-consequences can not be defined.
    return false;
    
//    varTable.substitute(diff);
//        
//    OuterLoop:
//    for (Literal lit : subConqs) {
//      for (Literal curLit : curSkipped) 
//        if (Literal.equals(lit, curLit))   
//          continue OuterLoop;
//      varTable.backtrackTo(state);
//      return false;
//    }
//
//    varTable.backtrackTo(state);
//    return true;
  }
  
  /**
   * Instantiates values in this substitution. 
   */
  public void instantiate() {
    super.instantiate();
    ConseqSet newConseqs = new ConseqSet(env);
    for (Clause conseq : conseqs)
      newConseqs.add(conseq.instantiate());
    conseqs = newConseqs;
  }
  
  /**
   * Renames the variables using the specified rename-mapping. No variable in this success must have a value.
   * @param renameMap  the rename-mapping.
   * @param minVarName the smallest variable name to be renamed.
   * @param minVarName the largest variable name to be renamed.
   */
  public void subrename(VarRenameMap renameMap, int minVarName, int maxVarName) {
    // Updates the specified variables in consequences.
    ConseqSet newConqs = new ConseqSet(env);
    for (Clause conq : conseqs) {
      conq.subrename(renameMap, Integer.MIN_VALUE, -1);
      newConqs.add(conq);
    }
    conseqs = newConqs;

    // Updates the specified variables in values.
    super.subrename(renameMap, minVarName, maxVarName);
  }
  
  /**
   * Returns true if this substitution is empty.
   * @return true if this substitution is empty.
   */
  public boolean isEmpty() {
    return size == 0 && conseqs.hasEmptyClause();
  }
  
  /**
   * Returns the shallowest reduction target depth.
   * @return the shallowest reduction target depth.
   */
  public int getShallowestTargetDepth() {
    return shallowestTargetDepth;
  }

  /**
   * Sets the shallowest reduction target depth.
   * @param depth  the shallowest reduction target depth.
   */
  public void setShallowestTargetDepth(int depth) {
    shallowestTargetDepth = depth;
  }

  /**
   * Returns the set of skipped literals.
   * @return the set of skipped literals.
   */
  public ConseqSet getConseqs() {
    return conseqs;
  }
  
  /**
   * Adds the specified sub-consequence to this set.
   * @param conq  a sub-consequence to be add.
   */
  public void addConseq(Clause conq) {
    conseqs.add(conq);
  }

  /**
   * Adds the specified sub-consequence to this set.
   * @param conqs  a set of sub-consequences to be add.
   */
  public void addConseqs(ConseqSet conqs) {
    conseqs.addAll(conqs);
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "<" + super.toString() + "," + conseqs + "," + shallowestTargetDepth + "> (" + getNumSyms() + "s)";
  }
  
  /** The environment. */
  private Env env = null;
  /** The variable table. */
  private VarTable varTable = null;
  /** The skipped nodes. */
  private ConseqSet conseqs = null;
  /** The shallowest reduction target depth of this success. */
  private int shallowestTargetDepth = -1;
}
