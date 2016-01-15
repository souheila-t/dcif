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
public final class IntStack {

  /** 
   * Constructs a integer stack.
   */
  public IntStack() {
    this(INITIAL_SIZE);
  }

  /** 
   * Constructs a integer stack with the specified initial size.
   * @param size the initial size of this stack.
   */
  public IntStack(int size) {
    elements = new int[size];
    num = 0;
  }

  /**
   * Pushes the specified value to this stack.
   * @param value the value to push.
   */
  public void push(int value) {
    if (elements.length == num) {
      int size = (num == 0) ? 1 : (num << 2);
      // MEMO for J2SE1.6
      //elements = Arrays.copyOf(elements, size);
      int[] old = elements;
      elements = new int[size];
      System.arraycopy(old, 0, elements, 0, old.length);
    }
    elements[num++] = value;
  }

  /**
   * Pops the top value of this stack and returns it.
   * @return the top value of this stack.
   */
  public int pop() {
    assert(num > 0);
    return elements[--num];
  }

  /**
   * Returns the value at the specified index.
   * @param index the specified index.
   * @return the value at the specified index.
   */
  public int get(int index) {
    assert(index < elements.length);
    return elements[index];
  }
  
  /**
   * Clears all elements in this stack.
   */
  public void clear() {
    num = 0;
  }

  /**
   * Returns the size of this stack.
   * @return
   */
  public int size() {
    return num;
  }

  /**
   * Returns true if this stack is empty.
   * @return true if this stack is empty.
   */
  public boolean isEmpty() {
    return num == 0;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    String str = "";
    for (int i = num - 1; i >= 0; i--)
      str += i + " : " + elements[i] + "\n";
    return str;
  }

  /** The initial default size of a stack. */
  private final static int INITIAL_SIZE = 8;
  /** The list of elements in this stack. */
  private int[] elements = null;
  /** The number of elements in this stack. */
  private int num = 0;
}

