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
public class LightArrayList<E> {
  
  /** 
   * Constructs a set.
   */
  public LightArrayList() {
    this(INITIAL_SIZE);
  }

  /** 
   * Constructs a set with the specified initial size.
   * @param size the initial size of this set.
   */
  public LightArrayList(int size) {
    elements = new Object[size];
    num = 0;
  }

  /**
   * Adds the specified object to this set.
   * @param obj the object to add.
   */
  public void add(E obj) {
    if (elements.length == num) {
      int size = (num == 0) ? 1 : (num << 2);
      // MEMO for J2SE1.6
      //elements = Arrays.copyOf(elements, size);
      Object[] old = elements;
      elements = new Object[size];
      System.arraycopy(old, 0, elements, 0, old.length);
    }
    elements[num++] = obj;
  }

  /**
   * Returns the value at the specified index.
   * @param index the specified index.
   * @return the value at the specified index.
   */
  @SuppressWarnings("unchecked")
  public E get(int index) {
    assert(index < elements.length);
    return (E)elements[index];
  }
  
  /**
   * Returns the last element.
   * @return the last element.
   */
  @SuppressWarnings("unchecked")
  public E getLast() {
    assert(num > 0);
    return (E)elements[num-1];
  }
  
  /**
   * Removes the object at the specified position.
   * @param idx the specified position to be removed.
   */
  @SuppressWarnings("unchecked")
  public E remove(int idx) {
    E obj = (E) elements[idx];
    elements[idx] = elements[--num];
    return obj;
  }
  
  /**
   * Removes the specified object.
   * @param obj the specified object to be removed.
   * @return true if this set contains the specified object.
   */
  public boolean remove(E obj) {
    for (int i=0; i < num; i++) {
      if (elements[i].equals(obj)) {
        elements[i] = elements[--num];
        return true;
      }
    }
    return false;
  }
  
  /**
   * Removes the last element and returns it.
   * @return the removed last element.
   */
  @SuppressWarnings("unchecked")
  public E removeLast() {
    assert(num > 0);
    return (E)elements[--num];
  }
  
  /**
   * Clears all elements in this set.
   */
  public void clear() {
    num = 0;
  }

  /**
   * Returns the size of this set.
   * @return
   */
  public int size() {
    return num;
  }

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public boolean isEmpty() {
    return num == 0;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append('[');
    for (int i=0; i < num; i++) {
      str.append(elements[i]);
      if (i+1 < num)
        str.append(", ");
    }
    str.append(']');
    return str.toString();
  }

  /** The initial default size of a set. */
  private final static int INITIAL_SIZE = 1;
  /** The list of elements in this set. */
  private Object[] elements = null;
  /** The number of elements in this set. */
  private int num = 0;
  
}
