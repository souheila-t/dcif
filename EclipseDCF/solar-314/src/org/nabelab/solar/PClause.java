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
 * A clause with a position for the extension operation.
 * @author nabesima
 *
 */
public class PClause {

  /**
   * Constructs a clause with a position.
   * @param clause the clause.
   * @param pos    the position.
   */
  public PClause(Clause clause, int pos) {
    this.clause = clause;
    this.pos    = pos;
  }
  
  /**
   * Returns the clause.
   * @return the clause.
   */
  public Clause getClause() {
    return clause;
  }
  
  /**
   * Returns the position.
   * @return the position.
   */
  public int getPos() {
    return pos;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PClause other = (PClause) obj;
    if (pos != other.pos)
      return false;
    if (clause == null) {
      if (other.clause != null)
        return false;
    } else if (!clause.equals(other.clause))
      return false;
    return true;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    String str = "[";
    for (int i=0; i < clause.size(); i++) {
      if (i == pos)
        str += "<" + clause.get(i) + ">";
      else  
        str += clause.get(i);
      if (i + 1 < clause.size())
        str += ", ";
    }
    return str + "]";
  }

  /**
   * Returns a string representation of this object.
   * @param offset a variable offset. 
   * @return a string representation of this object.
   */
  public String toSimpString(int offset) {
    String str = "[";
    for (int i=0; i < clause.size(); i++) {
      if (i == pos)
        str += "<" + clause.get(i).toSimpString(offset) + ">";
      else  
        str += clause.get(i).toSimpString(offset);
      if (i + 1 < clause.size())
        str += ", ";
    }
    return str + "]";
  }
   
  /** The clause. */
  private Clause clause = null;
  /** The position. */
  private int pos = 0;
}
