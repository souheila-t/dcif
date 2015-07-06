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
import org.nabelab.solar.Options;
import org.nabelab.solar.Term;

/**
 * @author nabesima
 *
 */
public class NotSubsuming extends Constraint {

  /**
   * Construct a not-equal constraint.
   * @param env    the environment.
   * @param opt    the options.
   * @param node   the node which contains this constraint.
   * @param type   the type of this constraint.
   * @param xterm  a term which should not subsume the term y.
   * @param yterm  a term which should not be subsumed the term x.
   */
  public NotSubsuming(Env env, Options opt, Node node, int type, Term xterm, Term yterm) {
    super(env, node, type);
    if (opt.use(USE_CONSTRAINT_INSTANTIATION)) {
      xterm = xterm.instantiate();
      yterm = yterm.instantiate();
    }
    this.xterm = xterm;
    this.yterm = yterm;
  }

  /**
   * Returns true if this constraint is satisfiable.
   * @return true if this constraint is satisfiable.
   */
  protected int isSatisfiable() {
    if (xterm.isSubsuming(yterm) != null)
      return UNSAT;
    if (xterm.isUnifiable(yterm) == null)
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
   * Instantiates all variables in this constraint.
   */
	public void instantiate() {
		xterm = xterm.instantiate();
		yterm = yterm.instantiate();
	}

	/**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((xterm == null) ? 0 : xterm.hashCode());
    result = prime * result + ((yterm == null) ? 0 : yterm.hashCode());
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
    NotSubsuming other = (NotSubsuming) obj;
    if (xterm == null) {
      if (other.xterm != null)
        return false;
    } else if (!xterm.equals(other.xterm))
      return false;
    if (yterm == null) {
      if (other.yterm != null)
        return false;
    } else if (!yterm.equals(other.yterm))
      return false;
    return true;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return super.toString() + " [" + xterm + " not subsume " + yterm + "]"; //  @" + time;
  }

  /** A term which should not subsume the term y. */
  private Term xterm = null;
  /** A term which should not be subsumed the term x. */
  private Term yterm = null;

}
