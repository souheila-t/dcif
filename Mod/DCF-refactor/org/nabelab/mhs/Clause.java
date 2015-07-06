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
package org.nabelab.mhs;

import org.nabelab.util.IntArraySet;

public class Clause {

  /**
   * Constructs a clause.
   * @param lits    the set of integers.
   * @param sorted  the list of sorted integers.
   */
  public Clause(IntArraySet lits, IntArraySet sorted) {
    assert(lits.size() > 1);
    this.lits = lits;
    this.sorted = sorted;
  }
  
  /**
   * Returns the value at the specified index.
   * @param index the specified index.
   * @return the value at the specified index.
   */
  public int getAt(int index) {
    return lits.getAt(index);
  }
    
//  /**
//   * Updates the watched literals.
//   * @param flit        the false literal.
//   * @param hitSetCand  a hitting set candidate.
//   * @param watcher     the literal watcher. 
//   * @return true if the watched literals change.
//   */
//  public boolean update(int flit, HitSetCand hitSetCand, LitWatcher watcher) {
//
//    // Make sure the false literal is the second:
//    if (lits.getAt(idx1stWLit) == flit) {
//      int tmp = idx1stWLit;
//      idx1stWLit = idx2ndWLit;
//      idx2ndWLit = tmp;
//    }
//    
//    // If 1st watch is true, then clause is already satisfied.
//    int firstLit = lits.getAt(idx1stWLit);
//    int firstVal = hitSetCand.getValue(firstLit); 
//    if (firstVal == HitSetCand.TRUE)
//      return false;
//    
//    // Look for new watch for the second:
//    boolean found = false;
//    for (int i=0; i < lits.size(); i++) {
//      if (i == idx1stWLit || i == idx2ndWLit)
//        continue;
//      int lit = lits.getAt(i);
//      if (hitSetCand.getValue(lit) != HitSetCand.FALSE) {
//        watcher.attach(-lit, this);
//        idx2ndWLit = i;
//        return true;
//      }
//    }
//    
//    // Did not find watch -- clause is unit under assignment:
//    if (firstVal == HitSetCand.FALSE) {
//      // TODO: conflict!
//    }
//    else {
//      // TODO: unit!
//      
//    }
//    
//    
////      for (int k = 2; k < c.size(); k++)
////        if (value(c[k]) != l_False){
////          c[1] = c[k]; c[k] = false_lit;
////          watches[toInt(~c[1])].push(&c);
////          goto FoundWatch; }
////      
////      // Did not find watch -- clause is unit under assignment:
////      *j++ = &c;
////      if (value(first) == l_False){
////        confl = &c;
////        qhead = trail.size();
////        // Copy the remaining watches:
////        while (i < end)
////          *j++ = *i++;
////      }else
////        uncheckedEnqueue(first, &c);
////    }
////    
//    
//  }

  /**
   * Returns the first watched literal.
   * @return the first watched literal.
   */
  public int get1stWLit() {
    return lits.getAt(idx1stWLit);
  }

  /**
   * Returns the second watched literal.
   * @return the second watched literal.
   */
  public int get2ndWLit() {
    return lits.getAt(idx2ndWLit);
  }
  
  /**
   * Returns the first watched literal.
   * @return the first watched literal.
   */
  public int get1stWLitIdx() {
    return idx1stWLit;
  }

  /**
   * Returns the second watched literal.
   * @return the second watched literal.
   */
  public int get2ndWLitIdx() {
    return idx2ndWLit;
  }

  /**
   * Returns the first watched literal.
   * @return the first watched literal.
   */
  public void setWLitIdx(int idx1stWLit, int idx2ndWLit) {
    this.idx1stWLit = idx1stWLit;
    this.idx2ndWLit = idx2ndWLit;    
  }

  /**
   * Returns true if the specified literal is contained in this clause.
   * @param num  the specified literal.
   * @return true if the specified literal is contained in this clause.
   */
  public boolean contains(int lit) {
    return sorted.contains(lit);
  } 

  /**
   * Returns the size of this set.
   * @return
   */
  public int size() {
    return lits.size();
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return lits.toString();
  }
  
  /** The set of literals. */
  private IntArraySet lits = null;
  /** The set of sorted literals. */
  private IntArraySet sorted = null;
  
  /** The position of the first watched literal. */
  private int idx1stWLit = 0;
  /** The position of the second watched literal. */
  private int idx2ndWLit = 1;
}
