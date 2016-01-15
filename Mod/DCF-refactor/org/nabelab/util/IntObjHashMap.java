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

public class IntObjHashMap<T> implements Iterable<IntObjPair<T>> {

  /**
   * Constructs a empty map.
   */
  public IntObjHashMap() {    
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
      for (IntObjPair<T> pair : table.get(index))
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
   * @return the previous value associated with key, or null if there was no
   *          mapping for key.
   */
  public T put(int key, T val) {
    if (size + 1 > capacity / 2)
      rehash();
    int index = hash(key);
    if (table.get(index) != null) {
      for (IntObjPair<T> pair : table.get(index)) {
        if (pair.get1st() == key) {
          T old = pair.get2nd();
          pair.set2nd(val);
          return old;
        }
      }
    }
    else
      table.set(index, new LinkedList<IntObjPair<T>>());

    table.get(index).add(new IntObjPair<T>(key, val));
    size++;
    return null;
  }

  /**
   * Returns the value to which the specified key is mapped, or null if this map
   * contains no mapping for the key.
   * @param key  the key whose associated value is to be returned.
   * @return the value to which the specified key is mapped, or null if this map
   *         contains no mapping for the key.
   */
  public T get(int key) {
    if (size == 0) 
      return null;
    int index = hash(key);
    if (table.get(index) != null) 
      for (IntObjPair<T> pair : table.get(index))
        if (pair.get1st() == key)
          return pair.get2nd();
    return null;
  }

  /**
   * Removes the mapping for a key from this map if it is present.
   * @param key  key whose mapping is to be removed from the map 
   * @return the previous value associated with key, or null if there was no mapping for key. 
   */
  public T remove(int key) {
    int index = hash(key);
    if (table.get(index) != null) {
      ListIterator<IntObjPair<T>> i = table.get(index).listIterator();
      while (i.hasNext()) {
        IntObjPair<T> pair = i.next();
        if (pair.get1st() == key) {
          size--;
          i.remove();
          return pair.get2nd();
        }
      }
    }
    return null;
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
  public Iterator<IntObjPair<T>> iterator() {
    return new IntObjHashMapIterator();
  }  
  
  /**
   * Inserts the specified number to this set without occuer-check.
   * @param num  the specified number to insert.
   */
  private void insert(IntObjPair<T> pair) {
    int index = hash(pair.get1st());
    if (table.get(index) == null)
      table.set(index, new LinkedList<IntObjPair<T>>());
    table.get(index).add(pair);
  }

  /**
   * Rehash all the elements.
   */
  private void rehash() {
    ArrayList<LinkedList<IntObjPair<T>>> old = table;
    if (capacity == 0)
      capacity = 7;
    else 
      capacity <<= 1;
        
    table = new ArrayList<LinkedList<IntObjPair<T>>>(capacity);
    for (int i=0; i < capacity; i++)
      table.add(null);
    if (old != null)
      for (LinkedList<IntObjPair<T>> pairs : old)
        if (pairs != null) 
          for (IntObjPair<T> pair : pairs)
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
    Iterator<IntObjPair<T>> i = iterator();
    while (true) {
      str.append(i.next());
      if (!i.hasNext())
        return str.append(']').toString();
      str.append(' ');
    }    
  }
    
  private class IntObjHashMapIterator implements Iterator<IntObjPair<T>> {

    public IntObjHashMapIterator() {
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

    public IntObjPair<T> next() {
      IntObjPair<T> pair = subIterator.next();
      
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
    private Iterator<IntObjPair<T>> subIterator = null;
  }
  
  /**
   * Main method for test.
   */
  public static void main(String[] args) {
    IntObjHashMap<String> map = new IntObjHashMap<String>();
    map.put(1, "one");
    map.put(2, "two");
    map.put(3, "three");
    System.out.println(map);
    map.remove(1);
    System.out.println(map);
  }
  
  /** The list of buckets. */
  private ArrayList<LinkedList<IntObjPair<T>>> table = null;
  /** The number of buckets. */
  private int capacity = 0;
  
  /** The number of elements in this set. */
  private int size = 0;
}