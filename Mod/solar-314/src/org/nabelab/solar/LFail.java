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
public class LFail extends Subst implements TermTypes {

  /**
   * Constructs a empty failure.
   * @param env      the environment.
   * @param skipped  the skipped nodes.
   */
  public LFail(Env env, Clause skipped) {
    this.env      = env;
    this.varTable = env.getVarTable();
    this.skipped  = skipped;
  }
  
  /**
   * Returns true if this failure is more general.
   * @param orgNumVars   the original number of variables.
   * @param curSkipped the skipped nodes.
   * @return true if this failure is more general.
   */
  public boolean isMoreGeneral(int orgNumVars, Clause curSkipped) {

    if (skipped.size() > curSkipped.size())
      return false;
    
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
    Subst diff = env.getLFailSubst();
    
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
    
    varTable.substitute(diff);
    
    OuterLoop:
    for (Literal lit : skipped) {
      for (Literal curLit : curSkipped) 
        if (Literal.equals(lit, curLit))   
          continue OuterLoop;
      varTable.backtrackTo(state);
      return false;
    }

    varTable.backtrackTo(state);
    return true;
  }
  
  /**
   * Returns true if this substitution is empty.
   * @return true if this substitution is empty.
   */
  public boolean isEmpty() {
    return size == 0 && skipped.size() == 0;
  }
  
  /**
   * Returns the set of skipped literals.
   * @return the set of skipped literals.
   */
  public Clause getSkipped() {
    return skipped;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "<" + super.toString() + "," + skipped + ">";
  }
  
  /** The environment. */
  private Env env = null;
  /** The variable table. */
  private VarTable varTable = null;
  /** The skipped nodes. */
  private Clause skipped = null;
  
}
