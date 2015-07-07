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
import org.nabelab.solar.Term;
import org.nabelab.solar.equality.TermWeight;

/**
 * @author nabesima
 *
 */
public class GreaterThanOrEqualTo extends Constraint {

  /**
   * Construct a greater-than-or-equal-to constraint for equality handling.
   * @param env  the environment.
   * @param node    the node which contains this constraint.
   * @param type the type of this constraint.
   * @param x    a term to be compared. 
   * @param y    a term to be compared.
   */
  public GreaterThanOrEqualTo(Env env, Node node, int type, Term x, Term y) {
    super(env, node, type);
    this.x = x;
    this.y = y;
  }
  
  /**
   * Returns true if this constraint is satisfiable.
   * @return true if this constraint is satisfiable.
   */
  protected int isSatisfiable() {
    TermWeight weight1 = env.getTermWeight1();
    TermWeight weight2 = env.getTermWeight2();
    x.calcTermWegiht(weight1);
    y.calcTermWegiht(weight2);
    int ret = weight2.isGreaterThan(weight1); 
    if (ret == TermWeight.TRUE) 
      return UNSAT;
    if (ret == TermWeight.SAME || weight1.isGreaterThan(weight2) == TermWeight.TRUE) 
      return SAT;
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
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "EQCN [" + x + " >= " + y + "] to " + node;
  }
  
  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((x == null) ? 0 : x.hashCode());
    result = prime * result + ((y == null) ? 0 : y.hashCode());
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
    GreaterThanOrEqualTo other = (GreaterThanOrEqualTo)obj;
    if (x == null) {
      if (other.x != null)
        return false;
    } else if (!x.equals(other.x))
      return false;
    if (y == null) {
      if (other.y != null)
        return false;
    } else if (!y.equals(other.y))
      return false;
    return true;
  }

  /** A term to be compared. */
  private Term x = null;
  /** A term to be compared. */
  private Term y = null;

}
