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

package org.nabelab.solar.util;

/**
 * @author nabesima
 *
 */
public class ArrayQueue<E> {

  /** 
   * Constructs a queue.
   */
  public ArrayQueue() {
    this(INITIAL_SIZE);
  }

  /** 
   * Constructs a queue with the specified initial size.
   * @param size the initial size of this queue.
   */
  public ArrayQueue(int size) {
    elements = new Object[size];
  }
  
  /**
   * Adds the specified element to this queue.
   * @param e the element to add.
   */
  public void add(E e) {
    elements[end++] = e;
    if (end == elements.length) 
      end = 0;
    // Resize?
    if (first == end) {
      Object[] tmp = new Object[elements.length << 1];
      int i = 0;
      for (int j=first; j < elements.length; j++) tmp[i++] = elements[j];
      for (int j=0    ; j < end            ; j++) tmp[i++] = elements[j];
      first = 0;
      end   = elements.length;
      elements = tmp;
    }
  }
  
  /**
   * Removes and returns the first element in this queue.
   * @return the first element in this queue if it exists.
   */
  @SuppressWarnings("unchecked")
  public E remove() {
    if (first == end) 
      return null;
    E e = (E)elements[first++];
    if (first == elements.length)
      first = 0; 
    return e;
  }
  
  /**
   * Clears this queue.
   */
  public void clear() {
    first = end = 0;
  }

  /**
   * Returns the number of elements in this queue.
   * @return the number of elements in this queue.
   */
  public int size() {
    return (end >= first) ? end - first : end - first + elements.length;
  }

  /**
   * Returns true if this queue is empty.
   * @return true if this queue is empty.
   */
  public boolean isEmpty() {
    return first == end;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (size() == 0)
      return "[]";
    StringBuilder str = new StringBuilder("[");
    if (end >= first) {
      for (int i=first; i < end; i++) {
        str.append(elements[i]);
        if (i < end - 1)
          str.append(", ");
      }
    }
    else {
      for (int i=first; i < elements.length; i++) {
        str.append(elements[i]);
        if (i < elements.length - 1 || end != 0)
          str.append(", ");
      }
      for (int i=0; i < end; i++) {
        str.append(elements[i]);
        if (i < end - 1)
          str.append(", ");
      }
    }
    str.append("]");
    return str.toString();
  }
  
  /**
   * The main method for testing this class.
   */
  public static void main(String[] args) {
    ArrayQueue<Integer> queue = new ArrayQueue<Integer>();
    
    for (int i=0; i < 15; i++) {
      queue.add(i);
      System.out.println("add(" + i + "): " + queue);
    }
    for (int i=0; i < 15; i++) {
      int n = queue.remove();
      System.out.println("remove() = " + n + ": " + queue);
    }
    for (int i=0; i < 15; i++) {
      queue.add(i);
      System.out.println("add(" + i + "): " + queue);
    }
    for (int i=0; i < 5; i++) {
      int n = queue.remove();
      System.out.println("remove() = " + n + ": " + queue);
    }
    for (int i=0; i < 10; i++) {
      queue.add(i);
      System.out.println("add(" + i + "): " + queue);
    }
    while (!queue.isEmpty()) {
      int n = queue.remove();
      System.out.println("remove() = " + n + ": " + queue);
    }
  }

  /** The initial default size of a stack. */
  private final static int INITIAL_SIZE = 1;
  /** The list of elements in this queue. */
  private Object[] elements = null;
  /** The first element index. */
  private int first = 0;
  /** The last element index. */
  private int end = 0;
}
