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

import java.util.Random;

import org.nabelab.util.IntArraySet;
import org.nabelab.util.IntIterator;
import org.nabelab.util.IntSet;

public class IntHashSet extends IntSet {

  /**
   * Constructs a empty set.
   */
  public IntHashSet() {    
  }
  
  /**
   *  Constructs a new set containing the elements in the specified set.
   */
  public IntHashSet(IntSet set) {
    addAll(set);
  }
  
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean contains(int num) {
    if (size == 0) 
      return false;
    int index = hash(num);
    if (table[index] != null)
      return table[index].contains(num);
    return false;
  }

  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public boolean add(int num) {
    if (contains(num))
      return false;
    if (size + 1 > capacity / 2)
      rehash();
    insert(num);
    size++;
    return true;
  }

  /**
   * Removes the specified number.
   * @param num the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public boolean remove(int num) {
    int index = hash(num);
    if (table[index] != null) {
      if (table[index].remove(num)) {
        size--;
        return true;
      }
    }
    return false;
  }
    
  /**
   * Clears all elements in this set.
   */
  public void clear() {
    for (int i=0; i < table.length; i++)
      table[i].clear();
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
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public IntIterator iterator() {
    return new IntHashSetIterator();
  }  
  
  /**
   * Inserts the specified number to this set without occuer-check.
   * @param num  the specified number to insert.
   */
  private void insert(int num) {
    int index = hash(num);
    if (table[index] == null)
      table[index] = new IntArraySet();
    table[index].add(num);
  }

  /**
   * Rehash all the elements.
   */
  private void rehash() {
    IntArraySet[] old = table;
    if (capacity == 0)
      capacity = 7;
    else 
      capacity <<= 1;
    table = new IntArraySet[capacity];
    if (old != null)
      for (int i=0; i < old.length; i++) 
        if (old[i] != null) 
          for (int j=0; j < old[i].size(); j++) 
            insert(old[i].getAt(j));
  }    
  
  /**
   * Return the hash code of the specified number.
   * @param num   the specified number.
   * @return the hash code of the specified number.
   */
  private int hash(int num) {
    if (num > 0)
      return (31 * num) % capacity;
    return (13 * -num) % capacity;
  }
  
  private class IntHashSetIterator implements IntIterator {

    public IntHashSetIterator() {
      if (table != null) {
        for (index=0; index < table.length; index++)
          if (table[index] != null && table[index].size() > 0)
            break;
      }
    }
    
    public boolean hasNext() {
      if (table == null)
        return false;
      return index != table.length;
    }

    public int next() {
      int num = table[index].getAt(subindex++);
      
      if (subindex == table[index].size()) {
        index++;
        subindex = 0;
        for (; index < table.length; index++)
          if (table[index] != null && table[index].size() > 0)
            break;
      }

      return num;
    }
    
    private int index = 0;
    private int subindex = 0;
  }
  
  /**
   * Main method for test.
   */
  public static void main(String[] args) {
    
    int num = 100000;
    if (args.length == 1)
      num = Integer.parseInt(args[0]);
      
    Random rand = new Random();
    IntHashSet set = new IntHashSet();
    
    System.out.println("Adding " + num + " random numbers to the set.");
    long time = System.currentTimeMillis();
    for (int i=0; i < num; i++) 
      set.add(rand.nextInt(num) - (num / 2));
    System.out.println("CPU time: " + ((System.currentTimeMillis() - time) / 1000.0) + "s");
  
//    IntIterator i = set.iterator();
//    while (i.hasNext())
//      System.out.println(i.next());
  }

  /** The list of buckets. */
  private IntArraySet[] table = null;
  /** The number of buckets. */
  private int capacity = 0;
  
  /** The number of elements in this set. */
  private int size = 0;
  
}
