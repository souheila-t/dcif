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

package org.nabelab.solar.operator;

import java.util.Comparator;

/**
 * @author nabesima
 *
 */
public class Operators {

  /** 
   * Constructs a set of operators.
   */
  public Operators() {
    this(INITIAL_SIZE);
  }

  /** 
   * Constructs a set of operators with the specified initial size.
   * @param size the initial size of this set.
   */
  public Operators(int size) {
    elements = new Operator[size];
  }

  /**
   * Adds the specified operator to this set.
   * @param value the operator to add.
   */
  public void add(Operator value) {
    assert(next == 0);
    if (elements.length == num) {
      int size = (num == 0) ? 1 : (num << 2);
      // MEMO for J2SE1.6
      //elements = Arrays.copyOf(elements, size);
      Object[] old = elements;
      elements = new Operator[size];
      System.arraycopy(old, 0, elements, 0, old.length);
    }
    elements[num++] = value;
  }
  
  /**
   * Adds all the operators in the specified set.
   * @param ops  a set of operators.
   */
  public void addAll(Operators ops) {
    for (int i=0; i < ops.size(); i++)
      add(ops.get(i));
  }
  
  /**
   * Returns the last operator.
   * @return the last operator.
   */
  public Operator getLast() {
    return elements[num  - 1];
  }
  
  /**
   * Removes and returns the last operator. 
   * @return the last operator.
   */
  public Operator removeFirst() {
    assert(next < num);
    return elements[next++];
  }
  
  /**
   * Returns the operator at the specified index.
   * @param index the specified index.
   * @return the operator at the specified index.
   */
  public Operator get(int index) {
    assert(next + index < elements.length);
    return elements[next + index];
  }
  
  /**
   * Clears all operators in this set.
   */
  public void clear() {
    next = num = 0;
  }

  /**
   * Returns the size of this set.
   * @return
   */
  public int size() {
    return num - next;
  }

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public boolean isEmpty() {
    return num - next == 0;
  }

  /**
   * Sorts the operators according to the specified order.
   * @param comparator the specified order.
   */
  public void sort(Comparator<Operator> comparator) {
    assert(next == 0);
    if (num < INSERTIONSORT_THRESHOLD) {
      for (int i=0; i < num; i++) {
        for (int j=i; j > 0 && comparator.compare(elements[j-1], elements[j]) > 0; j--) {
          Operator tmp  = elements[j];
          elements[j  ] = elements[j-1];
          elements[j-1] = tmp;
        }
      }          
    }
    else {
      //Arrays.sort(elements, 0, num, comparator);
      Operator[] tmp= new Operator[num];
      System.arraycopy(elements, 0, tmp, 0, num);
      mergeSort(elements, tmp, 0, num, comparator);
      elements = tmp;
    }
  }

  private static void mergeSort(Operator[] src, Operator[] dest, int low, int high, Comparator<Operator> c) {
    
    int length = high - low;
    // Insertion sort on smallest arrays
    if (length < INSERTIONSORT_THRESHOLD) {
      for (int i=low; i<high; i++) {
        for (int j=i; j>low && c.compare(dest[j-1], dest[j]) > 0; j--) {
          Operator tmp  = dest[j];
          dest[j  ] = dest[j-1];
          dest[j-1] = tmp;
        }
      }
      return;
    }
    
    // Recursively sort halves of dest into src
    int destLow = low;
    int destHigh = high;
    int mid = (low + high) >> 1;
    mergeSort(dest, src, low, mid, c);
    mergeSort(dest, src, mid, high, c);

    // If list is already sorted, just copy from src to dest. This is an
    // optimization that results in faster sorts for nearly ordered lists.
    if (c.compare(src[mid - 1], src[mid]) <= 0) {
      System.arraycopy(src, low, dest, destLow, length);
      return;
    }

    // Merge sorted halves (now in src) into dest
    for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
      if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
        dest[i] = src[p++];
      else
        dest[i] = src[q++];
    }
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (int i=next; i < num; i++) {
      str.append(elements[i]);
      str.append("\n");
    }
    return str.toString();
  }
  
  /** The threshold value for sorting. */
  private static final int INSERTIONSORT_THRESHOLD = 7;
  /** The initial default size of a set. */
  private final static int INITIAL_SIZE = 4;
  
  /** The list of elements in this set. */
  private Operator[] elements = null;
  /** The number of elements in this set. */
  private int num = 0;
  /** The index of the next operator to be applied. */
  private int next = 0;
  
}
