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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class IntHashMap implements Iterable<IntPair> {

  /**
   * Constructs a empty map.
   */
  public IntHashMap() {    
  }
  
  /**
   * Returns true if the specified number is stored as a key in this map.
   * @param key  the specified number to add.
   * @return true if the specified number is stored as a key.
   */
  public boolean containsKey(int key) {
    if (size == 0) 
      return false;
    int index = hash(key);
    if (table.get(index) != null) 
      for (IntPair pair : table.get(index))
        if (pair.get1st() == key)
          return true;
    return false;
  }

  /**
   * Associates the specified value with the specified key in this map. If the
   * map previously contained a mapping for the key, the old value is replaced
   * by the specified value.
   * @param key  key with which the specified value is to be associated.
   * @param val  value to be associated with the specified key.
   * @return the previous value associated with key, or -1 if there was no
   *          mapping for key.
   */
  public int put(int key, int val) {
    if (size + 1 > capacity / 2)
      rehash();
    int index = hash(key);
    if (table.get(index) != null) {
      for (IntPair pair : table.get(index)) {
        if (pair.get1st() == key) {
          int old = pair.get2nd();
          pair.set2nd(val);
          return old;
        }
      }
    }
    else
      table.set(index, new LinkedList<IntPair>());

    table.get(index).add(new IntPair(key, val));
    size++;
    return -1;
  }

  /**
   * Returns the value to which the specified key is mapped, or null if this map
   * contains no mapping for the key.
   * @param key  the key whose associated value is to be returned.
   * @return the value to which the specified key is mapped, or -1 if this map
   *         contains no mapping for the key.
   */
  public int get(int key) {
    if (size == 0) 
      return -1;
    int index = hash(key);
    if (table.get(index) != null) 
      for (IntPair pair : table.get(index))
        if (pair.get1st() == key)
          return pair.get2nd();
    return -1;
  }

  /**
   * Removes the mapping for a key from this map if it is present.
   * @param key  key whose mapping is to be removed from the map 
   * @return the previous value associated with key, or -1 if there was no mapping for key. 
   */
  public int remove(int key) {
    int index = hash(key);
    if (table.get(index) != null) {
      ListIterator<IntPair> i = table.get(index).listIterator();
      while (i.hasNext()) {
        IntPair pair = i.next();
        if (pair.get1st() == key) {
          size--;
          i.remove();
          return pair.get2nd();
        }
      }
    }
    return -1;
  }
    
  /**
   * Clears all elements in this set.
   */
  public void clear() {
    for (int i=0; i < table.size(); i++)
      if (table.get(i) != null)
        table.get(i).clear();
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
  public Iterator<IntPair> iterator() {
    return new IntHashMapIterator();
  }  
  
  /**
   * Inserts the specified number to this set without occuer-check.
   * @param num  the specified number to insert.
   */
  private void insert(IntPair pair) {
    int index = hash(pair.get1st());
    if (table.get(index) == null)
      table.set(index, new LinkedList<IntPair>());
    table.get(index).add(pair);
  }

  /**
   * Rehash all the elements.
   */
  private void rehash() {
    ArrayList<LinkedList<IntPair>> old = table;
    if (capacity == 0)
      capacity = 7;
    else 
      capacity <<= 1;
        
    table = new ArrayList<LinkedList<IntPair>>(capacity);
    for (int i=0; i < capacity; i++)
      table.add(null);
    if (old != null)
      for (LinkedList<IntPair> pairs : old)
        if (pairs != null) 
          for (IntPair pair : pairs)
            insert(pair);
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
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isEmpty())
      return "[]";
    StringBuilder str = new StringBuilder();
    str.append('[');
    Iterator<IntPair> i = iterator();
    while (true) {
      str.append(i.next());
      if (!i.hasNext())
        return str.append(']').toString();
      str.append(' ');
    }    
  }
    
  private class IntHashMapIterator implements Iterator<IntPair> {

    public IntHashMapIterator() {
      if (table != null) {
        for (index=0; index < table.size(); index++) {
          if (table.get(index) != null && table.get(index).size() > 0) {
            subIterator = table.get(index).iterator();
            break;
          }
        }
      }
    }
    
    public boolean hasNext() {
      if (table == null)
        return false;
      return index != table.size();
    }

    public IntPair next() {
      IntPair pair = subIterator.next();
      
      if (!subIterator.hasNext()) {
        index++;
        subIterator = null;
        for (; index < table.size(); index++) {
          if (table.get(index) != null && table.get(index).size() > 0) {
            subIterator = table.get(index).iterator();
            break;
          }
        }
      }

      return pair;
    }
    
    public void remove() {
      throw new UnsupportedOperationException();      
    }

    private int index = 0;
    private Iterator<IntPair> subIterator = null;
  }
  
  /**
   * Main method for test.
   */
  public static void main(String[] args) {
    IntHashMap map = new IntHashMap();
    map.put(1, 2);
    map.put(2, 3);
    map.put(3, 4);
    System.out.println(map);
    map.remove(1);
    System.out.println(map);
  }
  
  /** The list of buckets. */
  private ArrayList<LinkedList<IntPair>> table = null;
  /** The number of buckets. */
  private int capacity = 0;
  
  /** The number of elements in this set. */
  private int size = 0;
}