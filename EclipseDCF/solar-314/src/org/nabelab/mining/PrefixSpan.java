package org.nabelab.mining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.nabelab.util.IntArrayList;
import org.nabelab.util.IntHashMap;
import org.nabelab.util.IntIterator;
import org.nabelab.util.IntObjHashMap;
import org.nabelab.util.IntObjPair;
import org.nabelab.util.IntPair;

public class PrefixSpan implements Miner {

  /**
   * Returns  frequent item sets.
   * @param db      a database.
   * @param minsup  minimum support.
   * @param minpat  minimum pattern length.
   * @param maxpat  maximum pattern length.
   * @return frequent item sets.
   */
  public ArrayList<FreqItemSet> getFreqItemSets(DB db, int minsup, int minpat, int maxpat) {
    // Converts the DB to integer arrays.
    transactions = new int[db.size()][];
    int i=0;
    for (ItemSet items : db)
      transactions[i++] = items.toIntArray();
   
    // Sets parameters.
    this.minsup = minsup;
    this.minpat = minpat;
    this.maxpat = maxpat;
   
    // Initializes a pattern and a set of frequent item sets.
    pattern = new ArrayList<IntPair>();
    freqItemSets = new LinkedList<FreqItemSet>();
    
    // Constructs the first projected database.
    ArrayList<IntPair> root = new ArrayList<IntPair>();
    for (i=0; i < transactions.length; i++) 
      root.add(new IntPair(i, -1));
    
    project(root);      
    
    Collections.sort(freqItemSets, new Comparator<FreqItemSet>() {
      public int compare(FreqItemSet o1, FreqItemSet o2) {
        if (o1.getFreq() != o2.getFreq())
          return o2.getFreq() - o1.getFreq();
        return o2.size() - o1.size();
      }});
    
    return new ArrayList<FreqItemSet>(freqItemSets);
  }

  /**
   * Returns closed frequent item sets.
   * @param db      a database.
   * @param minsup  minimum support.
   * @param minpat  minimum pattern length.
   * @param maxpat  maximum pattern length.
   * @return closed frequent item sets.
   */
  public ArrayList<FreqItemSet> getClosedFreqItemSets(DB db, int minsup, int minpat, int maxpat) {
    
    // Computes the set of frequent item sets.
    System.out.println("getFreqItemSets start");
    getFreqItemSets(db, minsup, minpat, maxpat);
    System.out.println("getFreqItemSets end");
    
    // Removes non closed frequent item sets. 
    // TODO: The following is a naiive implementation.
    ArrayList<FreqItemSet> fps = new ArrayList<FreqItemSet>(freqItemSets);
    if (fps.size() > 0) {
      for (int i=0; i < fps.size(); i++) {
        FreqItemSet fp1 = fps.get(i);
        if (fp1 == null) continue;
        for (int j=i+1; j < fps.size(); j++) {
          FreqItemSet fp2 = fps.get(j);
          if (fp2 == null) continue;
          if (fp1.getFreq() == fp2.getFreq() && fp1.containsAll(fp2))
            fps.set(j, null);
        }
      }
      ArrayList<FreqItemSet> newFps = new ArrayList<FreqItemSet>();
      for (FreqItemSet fp : fps)
        if (fp != null)
          newFps.add(fp);
      fps = newFps;
    }
        
    return fps;
  }

  private void project(ArrayList<IntPair> projected) {
    
    generate(projected);
    
    IntObjHashMap<ArrayList<IntPair>> counter = new IntObjHashMap<ArrayList<IntPair>>();
  
    for (int i=0; i < projected.size(); i++) {
      int id   = projected.get(i).get1st();
      int pos  = projected.get(i).get2nd();
      int size = transactions[id].length;
 
      // Projected transaction: 
      //    2 3 4 5 6
      //   {a a b b c}
      // tmp = {<a,2>, <b,4>, <c,6>}
      IntHashMap tmp = new IntHashMap();
      for (int j=pos + 1; j < size; j++) {
        int item = transactions[id][j];
        if (!tmp.containsKey(item))
          tmp.put(item, j);
      }

      // For each item in the projected transaction, records the transaction id and the occurrence position of the item. 
      for (IntPair e : tmp) { 
        if (!counter.containsKey(e.get1st()))
          counter.put(e.get1st(), new ArrayList<IntPair>());
        counter.get(e.get1st()).add(new IntPair(id, e.get2nd()));
      }
    }

    // Removes item sets whose frequency is less than the minimum support. 
    IntArrayList removedKeys = new IntArrayList();
    for (IntObjPair<ArrayList<IntPair>> e : counter)
      if (e.get2nd().size() < minsup) 
        removedKeys.add(e.get1st());
    IntIterator i = removedKeys.iterator();
    while (i.hasNext())
      counter.remove(i.next());

    if (counter.size () == 0) 
      return;
    
    for (IntObjPair<ArrayList<IntPair>> e : counter) {
      if (pattern.size() < maxpat) {
        pattern.add(new IntPair(e.get1st(), e.get2nd().size()));
        project(e.get2nd());
        pattern.remove(pattern.size() - 1);
      }
    }
  }  
  
  private void generate(ArrayList<IntPair> projected) {
    if (pattern.size() == 0 || minpat > pattern.size()) 
      return;

    // The frequency of the pattern.
    int freq = pattern.get(pattern.size() - 1).get2nd();

    // The set of IDs in which the pattern is contained.
    IntArrayList where = new IntArrayList();
    for (int i=0; i < projected.size(); i++) 
      where.add(projected.get(i).get1st());

    // A set of items.
    IntArrayList items = new IntArrayList();
    for (int i=0; i < pattern.size(); i++) 
      items.add(pattern.get(i).get1st());

    // Stores  a frequent item set.
    freqItemSets.add(new FreqItemSet(items, freq, where));
  }
  
  /** A list of transactions. */
  private int[][] transactions = null;
  /** A pattern. */
  ArrayList<IntPair> pattern = null;
  /** A set of frequent patterns. */
  LinkedList<FreqItemSet> freqItemSets = null;
  
  /** The minimum support */
  private int minsup = 0;
  /** The minimum length of patterns */
  private int minpat = 0;
  /** The maximum length of patterns */
  private int maxpat = Integer.MAX_VALUE;

}
