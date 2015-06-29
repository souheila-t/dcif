/**
 * 
 */
package org.nabelab.solar.util;

import java.util.BitSet;
import java.util.Random;

import org.nabelab.util.IntArraySet;
import org.nabelab.util.IntIterator;
import org.nabelab.util.IntSet;

/**
 * @author nabesima
 *
 */
public final class IntBitSet extends IntSet {

  /** 
   * Constructs a empty set.
   */
  public IntBitSet() {
    this(INITIAL_SIZE);
  }
  
  /** 
   * Constructs a set with the specified initial size.
   * @param size  the initial size of this set.
   */
  public IntBitSet(int size) {
    this.elements = new BitSet(size);
  }
  
  /**
   *  Constructs a new set containing the elements in the specified set.
   *  @param set  the specified set.
   */
  public IntBitSet(IntSet set) {
    this();
    addAll(set);
  }
    
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean contains(int num) {
    return elements.get(index(num));
  }
  
  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public boolean add(int num) {
    int index = index(num);
    boolean added = !elements.get(index);
    elements.set(index);
    return added;
  }

  /**
   * Removes the specified number.
   * @param num  the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public boolean remove(int num) {
    int index = index(num);
    boolean removed = elements.get(index);
    elements.clear(index);
    return removed;
  }

  /**
   * Clears all elements in this set.
   */
  public void clear() {
    elements.clear();
  }
  
  /**
   * Returns the size of this set.
   * @return
   */
  public int size() {
    return elements.cardinality();
  }

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public boolean isEmpty() {
    return elements.isEmpty();
  }
  
  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public IntIterator iterator() {
    return new IntBitSetIterator();
  }  

  private int index(int num) {
    if (num > 0)
      return +num << 1;
    else
      return -num << 1 + 1;
  }
  
  private class IntBitSetIterator implements IntIterator {

    public IntBitSetIterator() {
      index = 0;
    }
    
    public boolean hasNext() {
      return index < elements.cardinality();
    }

    public int next() {
      index = elements.nextSetBit(index);
      if ((index & 0x1) == 0)
        return +(index >> 1);
      else
        return -(index >> 1);
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
  private final static int INITIAL_SIZE = 256;
  
  /** The set of elements. */
  private BitSet elements = null;
}
