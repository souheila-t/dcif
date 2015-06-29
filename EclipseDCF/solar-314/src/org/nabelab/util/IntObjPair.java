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

package org.nabelab.util;

/**
 * @author nabesima
 *
 */
public final class IntObjPair<T> {

  /**
   * Construct a integer pair.
   * @param x the first value of this pair.
   * @param y the second value of this pair.
   */
  public IntObjPair(int x, T y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the first value of this pair.
   * @return the first value of this pair.
   */
  public int get1st() {
    return x;
  }

  /**
   * Sets the first value of this pair.
   * @param x the first value of this pair.
   */
  public void set1st(int x) {
    this.x = x;
  }

  /**
   * Returns the second value of this pair.
   * @return the second value of this pair.
   */
  public T get2nd() {
    return y;
  }

  /**
   * Sets the second value of this pair.
   * @param y the second value of this pair.
   */
  public void set2nd(T y) {
    this.y = y;
  }

  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y.hashCode();
    return result;
  }

  /**
   * Compares the specified object with this object for equality.
   * @param obj the reference object with which to compare.
   */
  @SuppressWarnings("unchecked")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IntObjPair<T> other = (IntObjPair<T>) obj;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    return true;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return "<" + x + "," + y + ">";
  }

  /** The first value of this pair. */
  private int x;
  /** The second value of this pair. */
  private T y;

}