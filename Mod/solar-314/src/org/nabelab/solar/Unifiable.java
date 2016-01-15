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
public class Unifiable<E> {

  /**
   * Constructs a unifiable object.
   * @param subst  the substitution for the unification.
   * @param object the unifiable object with some object.
   * @param offset the variable offset of the object.
   */
  public Unifiable(Subst subst, E object, int offset) {
    this.subst  = subst;
    this.object = object;
    this.offset = offset;
  }
  
  
  /**
   * Returns the substitution for the unification.
   * @return  the substitution for the unification.
   */
  public Subst getSubst() {
    return subst;
  }

  /**
   * Sets the substitution for the unification.
   * @param subst  the substitution to set.
   */
  public void setSubst(Subst subst) {
    this.subst = subst;
  }

  /**
   * Returns the unifiable object.
   * @return the unifiable object.
   */
  public E getObject() {
    return object;
  }

  /**
   * Returns the variable offset of the unifiable object.
   * @return the variable offset of the unifiable object.
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (object instanceof PClause) 
      return ((PClause)object).toSimpString(offset) + "/" + subst;
    return object + "/" + subst;
  }
  
  /** The substitution for the unification. */
  private Subst subst = null;
  /** The unifiable object with some object. */
  private E object = null;
  /** The variable offset of the object. */
  private int offset = 0;
}
