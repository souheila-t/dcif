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

import org.nabelab.solar.util.IntHashSet;


public abstract class IntSet {

  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public abstract boolean contains(int num);
  
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean containsAll(IntSet set) {
    if (size() < set.size())
      return false;
    IntIterator i = set.iterator();
    while (i.hasNext())
      if (!contains(i.next()))
        return false;
    return true;
  }

  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public abstract boolean add(int num); 
  
  /**
   * Adds the elements in the specified set to this set.
   * @param set  the specified set.
   * @return true if a number is added to this set.
   */
  public boolean addAll(IntSet set) {
    boolean added = false;
    IntIterator i = set.iterator();
    while (i.hasNext())
      added |= add(i.next());
    return added;
  }
  
  /**
   * Removes the specified number.
   * @param num  the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public abstract boolean remove(int num); 
    
  /**
   * Removes the elements in the specified set from this set.
   * @param set  the specified set.
   * @return true if a number is removed from this set.
   */
  public boolean removeAll(IntSet set) {
    boolean removed = false;
    IntIterator i = set.iterator();
    while (i.hasNext())
      removed |= remove(i.next());
    return removed;
  }
  
  /**
   * Returns true if this set is disjoint with the specified set.
   * @param set  a set to be compared.
   * @return true if this set is disjoint with the specified set.
   */
  public boolean isDisjoint(IntSet set) {
    IntSet small = this;
    IntSet large = set;
    if (size() > set.size()) {
      small = set;
      large = this;
    }
    IntIterator i = small.iterator();
    while (i.hasNext())
      if (large.contains(i.next()))
        return false;
    return true;
  }
  
  /**
   * Returns the intersection of this set and the specified set.
   * @param set  a set to be compared.
   * @return the intersection of this set and the specified set.
   */
  public IntSet intersect(IntSet set) {
    IntSet small = this;
    IntSet large = set;
    if (size() > set.size()) {
      small = set;
      large = this;
    }
    IntSet cap = new IntHashSet();
    IntIterator i = small.iterator();
    while (i.hasNext()) {
      int n = i.next();
      if (large.contains(n))
        cap.add(n);
    }
    
    return cap;        
  }
  
  /**
   * Clears all elements in this set.
   */
  public abstract void clear();
  
  /**
   * Returns the size of this set.
   * @return
   */
  public abstract int size();

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public abstract boolean isEmpty();
  
  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public abstract IntIterator iterator();
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isEmpty())
      return "[]";
    StringBuilder str = new StringBuilder();
    str.append('[');
    IntIterator i = iterator();
    while (true) {
      str.append(i.next());
      if (!i.hasNext())
        return str.append(']').toString();
      str.append(' ');
    }    
  }
  
}
