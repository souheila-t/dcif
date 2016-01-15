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

package org.nabelab.mining;

import org.nabelab.util.IntArrayList;

public class FreqItemSet extends ItemSet {

  /**
   * Constructs a frequent item set.
   * @param items  a list of items.
   * @param freq   a frequency of the items.
   * @param where  a transaction IDs in which the items are contained.
   */
  public FreqItemSet(IntArrayList items, int freq, IntArrayList where) {
    super(items);
    this.freq = freq;
    this.where = new IntArrayList(where);
  }
  
  /**
   * Returns the frequency of this item set.
   * @return the frequency of this item set.
   */
  public int getFreq() {
    return freq;
  }
  
  /**
   * Returns the index of clauses which contains this item set.
   * @return the index of clauses which contains this item set.
   */
  public IntArrayList getOccurrences() {
    return where;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    return super.toString() + " (" + freq + " occs at " + where + ")";  
  }
  
  /** A frequency. */
  private int freq = 0;
  /** A transaction IDs in which this items are contained. */
  IntArrayList where = null;
}
