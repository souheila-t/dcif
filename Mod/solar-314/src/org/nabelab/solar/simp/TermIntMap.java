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

package org.nabelab.solar.simp;

import java.util.ArrayList;
import java.util.HashMap;

import org.nabelab.solar.Term;

public class TermIntMap {

  /**
   * Registers a term and return the identifier.
   * @param term  a term to be registered.
   * @return  the identifier.
   */
  public int put(Term term) {
    if (nmap.containsKey(term))
      return nmap.get(term);
    int id = ++num;
    nmap.put(term, id);
    rmap.add(term);
    return id;
  }
  
  /**
   * Returns a term which is associated with the specified identifier.
   * @param id  an identifier.
   * @return a term which is associated with the specified identifier.
   */
  public Term get(int id) {
    assert(0 < id && id <= rmap.size());
    return rmap.get(id - 1);
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (int i=1; i <= num; i++)
      str.append(i + ": " + rmap.get(i) + "\n");
    return str.toString();
  }
  
  /** A mapping from terms to integers. */
  private HashMap<Term, Integer> nmap = new HashMap<Term, Integer>();
  /** A mapping from integers to terms. */
  private ArrayList<Term> rmap = new ArrayList<Term>();
  /** The number of registerd terms. */
  private int num = 0;
}
