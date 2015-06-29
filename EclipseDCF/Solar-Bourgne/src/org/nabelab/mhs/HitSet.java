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

import java.util.List;

import org.nabelab.util.IntArrayList;
import org.nabelab.util.IntSet;


/**
 * @author nabesima
 *
 */
public final class HitSet extends IntArrayList { 

  /**
   * Constructs an empty hitting set.
   */
  public HitSet() {
    super();
  }
  
  /**
   *  Constructs a new set containing the elements in the specified set.
   *  @param set  the specified set.
   */
  public HitSet(IntArrayList set) {
    super(set);
  }

  /**
   * Returns true if this hitting set hits the specified set.
   * @param set  the specified set.
   * @return true if this hitting set hits the specified set.
   */
  public boolean isHitting(IntSet set) {
    return isHitting(set, -1);
  }
  
  /**
   * Returns true if this hitting set hits the specified set.
   * @param set      the specified set.
   * @param exIndex  the index of the ignored element in this set.
   * @return true if this hitting set hits the specified set.
   */
  public boolean isHitting(IntSet set, int exIndex) {
    for (int i=0; i < size(); i++) {
      if (i == exIndex)
        continue;
      if (set.contains(getAt(i)))
        return true;
    }
    return false;      
  }
  
  /**
   * Returns the index of the element that hits the specified set.
   * @param set  the specified set.
   * @return the index of the element that hits the specified set. If there are multiple hitting elements, then returns MULTIPLE.
   */
  public int getHitIndex(IntSet set) {
    int index = NONE;
    boolean first = true;
    for (int i=0; i < size(); i++) {
      if (set.contains(getAt(i))) {
        if (!first) 
          return MULTIPLE;
        index = i;
        first = false;
      }
    }
    return index;
  }
  
  /**
   * Returns true if this set is a hitting set of the specified family of integers.
   * @param family  the specified family of integers.
   * @return true if this set is a hitting set of the specified family of integers.
   */
  public boolean isHS(List<IntSet> family) {
    return isHS(family, family.size());
  }
  
  /**
   * Returns true if this set is a hitting set of the specified family of integers.
   * @param family    the specified family of integers.
   * @param endIndex  the ending index (exclusive) of the family to be considered.
   * @return true if this set is a hitting set of the specified family of integers.
   */
  public boolean isHS(List<IntSet> family, int endIndex) {
    return isHS(family, endIndex, -1);
  }    
  
  /**
   * Returns true if this set is a hitting set of the specified family of integers.
   * @param family    the specified family of integers.
   * @param endIndex  the ending index (exclusive) of the family to be considered.
   * @param exIndex   the index of the ignored element in this set.
   * @return true if this set is a hitting set of the specified family of integers.
   */
  public boolean isHS(List<IntSet> family, int endIndex, int exIndex) {
    if (isEmpty())
      return false;

    boolean hit = false;
    for (int i=0; i < endIndex; i++) {
      IntSet set = family.get(i);
      if (!isHitting(set, exIndex))
        return false;
      hit = true;
    }
    
    return hit;
  }
  
  /**
   * Returns true if this set is a minimum hitting set of the specified family of integers.
   * @param family  the specified family of integers.
   * @return true if this set is a minimum hitting set of the specified family of integers.
   */
  public boolean isMHS(List<IntSet> family) {
    return isMHS(family, family.size());
  }
  
  /**
   * Returns true if this set is a minimum hitting set of the specified family of integers.
   * @param family    the specified family of integers.
   * @param endIndex  the ending index (exclusive) of the family to be considered.
   * @return true if this set is a minimum hitting set of the specified family of integers.
   */
  public boolean isMHS(List<IntSet> family, int endIndex) {
      
    if (isEmpty())
      return false;
    
    for (int exIndex=0; exIndex < size(); exIndex++) 
      if (isHS(family, endIndex, exIndex))
        return false;
    
    return true;
  }

  /** The constant that means there is no hitting element. */
  public final static int NONE = -1;
  
  /** The constant that means there are multiple hitting elements. */  
  public final static int MULTIPLE = -2;
}