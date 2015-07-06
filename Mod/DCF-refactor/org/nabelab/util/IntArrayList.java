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

import java.util.Arrays;

public class IntArrayList extends IntSet {

  /** 
   * Constructs a empty list.
   */
  public IntArrayList() {
    this(INITIAL_SIZE);
  }
  
  /** 
   * Constructs a set with the specified initial size.
   * @param size  the initial size of this set.
   */
  public IntArrayList(int size) {
    this.elements = new int[size];
    this.size = 0;
  }
  
  /**
   *  Constructs a new list containing the elements in the specified list.
   *  @param list  the specified list.
   */
  public IntArrayList(IntSet list) {
    this(list.size());
    addAll(list);
  }
    
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean contains(int num) {
    for (int i=0; i < size; i++)
      if (elements[i] == num)
        return true;
    return false;
  }
  
  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public boolean add(int num) {
    if (elements.length == size) {
      int newsz = (size == 0) ? 1 : (size << 2);
      // MEMO for J2SE1.6
      //elements = Arrays.copyOf(elements, newsz);
      int[] old = elements;
      elements = new int[newsz];
      System.arraycopy(old, 0, elements, 0, old.length);
    }
    elements[size++] = num;
    return true;
  }
  
  /**
   * Adds the elements in the specified set to this set.
   * @param list  the specified set.
   * @return true if a number is added to this set.
   */
  public void addAll(IntArrayList list) {
    for (int i=0; i < list.size(); i++)
      add(list.getAt(i));
  }
  
  /**
   * Returns the value at the specified index.
   * @param index the specified index.
   * @return the value at the specified index.
   */
  public int getAt(int index) {
    assert(index < size);
    return elements[index];
  }
    
  /**
   * Returns the value of the last element.
   * @return the value of the last element.
   */
  public int getLast() {
    return elements[size - 1];
  }
    
  /**
   * Sets the value at the specified index.
   * @param index the specified index.
   * @param value the value to set.
   * @return the old value at the specified index.
   */
  public int setAt(int index, int value) {
    assert(index < size);
    int old = elements[index];
    elements[index] = value;
    return old;
  }
    
  /**
   * Removes the object at the specified position.
   * @param index the specified position to be removed.
   */
  public int removeAt(int index) {
    assert(index < size);
    int num = elements[index];
    elements[index] = elements[--size];
    return num;
  }
    
  /**
   * Removes the last elements.
   */
  public int removeLast() {
    assert(size > 0);
    return elements[--size];
  }
  
  /**
   * Removes the specified number.
   * @param num  the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public boolean remove(int num) {
    for (int i=0; i < size; i++) {
      if (elements[i] == num) {
        elements[i] = elements[--size];
        return true;
      }
    }
    return false;
  }
    
  /**
   * Clears all elements in this set.
   */
  public void clear() {
    size = 0;
  }
  
  /**
   * Returns the size of this set.
   * @return
   */
  public int size() {
    return size;
  }

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Sorts the elements.
   */
  public void sort() {
    Arrays.sort(elements, 0, size);
  }

  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public IntIterator iterator() {
    return new IntArrayListIterator();
  }  
  
  private class IntArrayListIterator implements IntIterator {

    public IntArrayListIterator() {
      index = 0;
    }
    
    public boolean hasNext() {
      return index < size();
    }

    public int next() {
      return getAt(index++);
    }
    
    private int index = 0;
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isEmpty())
      return "[]";
    StringBuilder str = new StringBuilder();
    str.append('[');
    int i=0;
    while (true) {
      str.append(elements[i++]);
      if (i == size)
        return str.append(']').toString();
      str.append(' ');
    }    
  }

  /** The initial default size of a set. */
  private final static int INITIAL_SIZE = 1;
  /** The list of elements in this set. */
  private int[] elements = null;
  /** The number of elements in this set. */
  private int size = 0;
  
}


