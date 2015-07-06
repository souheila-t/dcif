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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class IntArraySet extends IntSet {

  /** 
   * Constructs a empty set.
   */
  public IntArraySet() {
    this(INITIAL_SIZE);
  }
  
  /** 
   * Constructs a set with the specified initial size.
   * @param size  the initial size of this set.
   */
  public IntArraySet(int size) {
    this.elements = new int[size];
    this.size = 0;
  }
  
  /**
   *  Constructs a new set containing the elements in the specified set.
   *  @param set  the specified set.
   */
  public IntArraySet(IntSet set) {
    this(set.size());
    addAll(set);
  }
    
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean contains(int num) {
    if (!sorted) {
      for (int i=0; i < size; i++)
        if (elements[i] == num)
          return true;
      return false;
    }
    
    if (num < minVal || maxVal < num)
      return false;
    
    // Binary search.
    int low  = 0;
    int high = size - 1;
    while (low <= high) {
      int mid    = (low + high) >>> 1;
      int midVal = elements[mid];
      if (midVal < num)
        low = mid + 1;
      else if (midVal > num)
        high = mid - 1;
      else
        return true;
    }
    return false;
  }
  
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean containsAll(IntArraySet set) {
    if (!sorted || !set.sorted)
      return super.containsAll(set);
    
    int xpos = 0;
    int ypos = 0;
    while (true) {
      if (xpos == set.size()) 
        return true;
      if (ypos == size())
        return false;
      int xval = set.elements[xpos];
      int yval = this.elements[ypos];
      if (xval < yval)
        return false;
      else if (xval > yval) 
        ypos++;          
      else {
        xpos++;
        ypos++;
      }
    }
  }

  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public boolean add(int num) {
    if (contains(num))
      return false;
    if (elements.length == size) {
      int newsz = (size == 0) ? 1 : (size << 2);
      // MEMO for J2SE1.6
      //elements = Arrays.copyOf(elements, newsz);
      int[] old = elements;
      elements = new int[newsz];
      System.arraycopy(old, 0, elements, 0, old.length);
    }
    elements[size++] = num;
    sorted = false;
    return true;
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
    sorted = false;    
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
   * Retains only the elements in this set that are contained in the specified set.
   * @param set  the set containing elements to be retained in this set.
   * @return true if this set changed as a result of the call 
   */
  public boolean retainAll(IntSet set) {
    boolean removed = false;
    for (int i=size-1; i >= 0; i--) { 
      if (!set.contains(elements[i])) {        
        removeAt(i);
        removed = true;
      }
    }
    return removed;
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
    if (size == 0) return;
    Arrays.sort(elements, 0, size);
    minVal = elements[0];
    maxVal = elements[size - 1];
    sorted = true;    
  }

  /**
   * Sorts the elements.
   * @param comparator  the comparator between integers. 
   */
  public void sort(Comparator<Integer> comparator) {
    if (size == 0) return;
    List<Integer> list = new ArrayList<Integer>();
    for (int i=0; i < size; i++)
      list.add(elements[i]);
    
    Collections.sort(list, comparator);
        
    for (int i=0; i < size; i++)
      elements[i] = list.get(i);
    
    sorted = false;
  }
  
  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public IntIterator iterator() {
    return new IntArraySetIterator();
  }  
  
  private class IntArraySetIterator implements IntIterator {

    public IntArraySetIterator() {
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
   * Main method for test.
   */
  public static void main(String[] args) {
    
    int num = 100000;
    if (args.length == 1)
      num = Integer.parseInt(args[0]);
      
    Random rand = new Random();
    IntSet set = new IntArraySet();
    
    System.out.println("Adding " + num + " random numbers to the set.");
    long time = System.currentTimeMillis();
    for (int i=0; i < num; i++) 
      set.add(rand.nextInt(num) - (num / 2));
    System.out.println("CPU time: " + ((System.currentTimeMillis() - time) / 1000.0) + "s");

  }

  /** The initial default size of a set. */
  private final static int INITIAL_SIZE = 1;
  /** The list of elements in this set. */
  private int[] elements = null;
  /** The number of elements in this set. */
  private int size = 0;
  /** If true, then this set is sorted. */
  private boolean sorted = false;
  /** The minimum value in this set. */
  private int minVal = 0;
  /** The maximum value in this set. */
  private int maxVal = 0;
  
}

