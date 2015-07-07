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

package org.nabelab.solar.constraint;

import org.nabelab.solar.Env;
import org.nabelab.solar.Node;
import org.nabelab.solar.OptionTypes;
import org.nabelab.solar.Options;
import org.nabelab.solar.Subst;
import org.nabelab.solar.Term;

/**
 * @author nabesima
 *
 */
public class NotEqual extends Constraint implements OptionTypes {

  /**
   * Construct a not-equal constraint.
   * @param env   the environment.
   * @param opt   the options.
   * @param node  the node which contains this constraint.
   * @param type  the type of this constraint.
   */
  public NotEqual(Env env, Options opt, Node node, int type, int var, Term val) {
    super(env, node, type);
    this.var = varTable.getTailVar(var);
    if (opt.use(USE_CONSTRAINT_INSTANTIATION) && val.hasBindedVars())
      val = val.instantiate();
    this.val = val;
  }
  
  /**
   * Returns true if this constraint is satisfiable.
   * @return true if this constraint is satisfiable.
   */
  protected int isSatisfiable() {
    Subst g = val.isUnifiable(var);
    if (g == null)
      return SAT;
    if (g.isEmpty()) 
      return UNSAT;
    return UNKNOWN;
  }

  /**
   * Returns the size of this constraint.
   * @return the size of this constraint.
   */
  public int size() {
    return 1;
  }

  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((val == null) ? 0 : val.hashCode());
    result = prime * result + ((var == null) ? 0 : var.hashCode());
    return result;
  }

  /**
   * Compares the specified object with this object for equality.
   * @param obj the reference object with which to compare.  
   */
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NotEqual other = (NotEqual) obj;
    if (val == null) {
      if (other.val != null)
        return false;
    } else if (!val.equals(other.val))
      return false;
    if (var == null) {
      if (other.var != null)
        return false;
    } else if (!var.equals(other.var))
      return false;
    return true;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return super.toString() + " [" + val + " != " + var + "]"; //  @" + time; 
  }
  
  /** The variable number. */
  private Term var = null;
  /** The variable value. */
  private Term val = null;
}
