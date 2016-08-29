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

package org.nabelab.mhs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.nabelab.debug.Debuggable;
import org.nabelab.debug.Debugger;
import org.nabelab.util.IntArraySet;
import org.nabelab.util.IntIterator;
import org.nabelab.util.IntSet;
import org.nabelab.util.LightArrayList;

public final class MHSFinder implements DebugTypes {

  /**
   * Constructs a minimum hitting set finder.
   * @param family  the specified family of integers.
   */
  public MHSFinder(Debuggable env, List<? extends IntSet> family) {
    this.env = env;
    this.orgFamily = family;
  }

  /**
   * Returns all minimum hitting set of the specified family of integers.
   * @return all minimum hitting set of the specified family of integers.
   */
  public List<HitSet> findAllMHS() {
    return findAllMHS(NOLIMIT, NOLIMIT);    
  }
  
  /**
   * Returns all minimum hitting set of the specified family of integers.
   * @param maxSize  the maximum size of hitting set.
   * @return all minimum hitting set of the specified family of integers.
   */
  public List<HitSet> findAllMHS(int maxSize) {
    return findAllMHS(maxSize, NOLIMIT);    
  }
  
  /**
   * Returns all minimum hitting set of the specified family of integers.
   * @param maxSize  the maximum size of hitting set.
   * @param maxNum   the maximum number of hitting set.
   * @return all minimum hitting set of the specified family of integers.
   */
  public List<HitSet> findAllMHS(int maxSize, int maxNum) {
    this.maxSize = maxSize;
    this.maxNum  = maxNum;    
    mhss = new ArrayList<HitSet>();
    hs = new HitSet();

    if (useRecursive)
      findAllMHSRec();
    else
      findAllMHSNonRec();
    
    if (env.dbg(DBG_PROGRESS) && maxEstimateLv != -1)
      System.out.println();    
    return mhss;
  }
  
  /**
   * Returns all minimum hitting set of the specified family of integers (recursive version). 
   */
  private void findAllMHSRec() {
    sort(orgFamily);
    simpleCriticals = new LightArrayList<LightArrayList<IntSet>>();
    findAllMHSRec(0);
  }
  
  /**
   * Returns all minimum hitting set of the specified family of integers (recursive version). 
   * @param index  the ending index (inclusive) of the family to be considered. 
   */
  private void findAllMHSRec(int index) {
    while (true) {
    
      if (env.dbg(DBG_PROGRESS) && index == maxEstimateLv) {
        System.out.format("Progress: %.1f%% (%d/%d, %d found) \r", 100.0 * curSteps / numSteps, curSteps, numSteps, mhss.size());
        curSteps++;
      }

      if (maxNum != NOLIMIT && mhss.size() >= maxNum)
        return;
      
      if (index == orderedFamily.size()) {
        mhss.add(new HitSet(hs));
        //System.out.println("hs = " + hs);
        return;
      }
    
      IntArraySet orderedNext = orderedFamily.get(index);
      IntArraySet sortedNext = sortedFamily.get(index);
      index++;
      
      int hindex = hs.getHitIndex(sortedNext);
      if (hindex >= 0) {
        simpleCriticals.get(hindex).add(sortedNext);
        findAllMHSRec(index);
        simpleCriticals.get(hindex).remove(sortedNext);
      }
      else if (hindex == HitSet.MULTIPLE) {
        //findAllMHSAux(index);    // To avoid the stack over flow.
        continue;
      }
      else {
        
        if (hs.size() == maxSize) 
          return;
        
        //ArrayList<Pair> removed = new ArrayList<Pair>();
        LightArrayList<LightArrayList<IntSet>> criticalHolders = new LightArrayList<LightArrayList<IntSet>>();
        LightArrayList<IntSet> removedCriticals = new LightArrayList<IntSet>();
        LightArrayList<IntSet> newCritical = new LightArrayList<IntSet>();        
        for (int i=0; i < orderedNext.size(); i++) {
          boolean isMin = true;
          int n = orderedNext.getAt(i);    // n will be added to hs.
          criticalHolders.clear();
          removedCriticals.clear();
          
          // Tautology checking.
          if (hs.contains(-n))    
            continue;
          
        CHECK:
          for (int j=0; j < hs.size(); j++) {
            LightArrayList<IntSet> familyH = simpleCriticals.get(j);

            for (int k=familyH.size() - 1; k >= 0; k--) {
              IntSet setH = familyH.get(k);
              if (setH.contains(n)) {    // Now, setH contains not only h but also n. Then the set becomes no-critical.
                familyH.remove(k);
                criticalHolders.add(familyH);
                removedCriticals.add(setH);
                if (familyH.isEmpty()) {
                  isMin = false;
                  break CHECK;
                }              
              }
            }
          }

          if (isMin) {
            newCritical.clear();
            newCritical.add(sortedNext);
            simpleCriticals.add(newCritical);
            hs.add(n);
            findAllMHSRec(index);
            hs.removeLast();
            simpleCriticals.remove(simpleCriticals.size() - 1);
          }
          else if (index - 2 < maxEstimateLv) {
            int pruned = 1;
            for (int j=index - 2; j < maxEstimateLv; j++)
              pruned *= orderedFamily.get(j).size();
            curSteps += pruned;        
          }

          for (int j=0; j < criticalHolders.size(); j++)
            criticalHolders.get(j).add(removedCriticals.get(j));
        }
      }
      break;
    }
  }

  /**
   * Returns all minimum hitting set of the specified family of integers (Non-recursive version). 
   */
  private void findAllMHSNonRec() {
  
    // Pre-processing
    simplify();
    
    backtrackPoints = new LightArrayList<BacktrackPoint>();
    
//    System.out.println("----------------------------");
//    for (int i=0; i < orderedFamily.size(); i++)
//      System.out.println(orderedFamily.get(i));
//    System.out.println("----------------------------");

    //long counter = 0;
    
    while (true) {
    
//      counter++;
//      System.out.println(counter + " @ " + hitSetCand);
//      if (counter == 6193)
//        System.out.println("6193!");
      
      if (env.dbg(DBG_PROGRESS) && claIndex == maxEstimateLv && litIndex == 0) {
        System.out.format("Progress: %.1f%% (%d/%d, %d found) \r", 100.0 * curSteps / numSteps, curSteps, numSteps, mhss.size());
        curSteps++;
      }

      //System.out.println(claIndex + "/" + clauses.size());
      
      if (maxNum != NOLIMIT && mhss.size() >= maxNum)
        return;
      
      if (claIndex == orderedFamily.size()) {
        HitSet mhs = hitSetCand.getHitSet(); 
//        System.out.println(mhss.size() + ": hs = " + mhs);
//        if (mhss.size() == 0)
//          System.out.println("hello");
        mhss.add(mhs);
        if (mhss.size() == 1)
          System.out.println("Found 1st solution.");
        if (backtrack())
          continue;
        else
          return;
      }
    
      Clause clause = clauses.get(claIndex);
      if (litIndex == 0) {  // First time for checking the sortedNext.
        if (hitSetCand.isHitting(clause, claIndex)) {
          claIndex++;
          continue;
        }
      }
      
      if (hitSetCand.size() == maxSize) { 
        if (backtrack())
          continue;
        else
          return;
      }
      
      int newHLitIndex = hitSetCand.hit(clause, claIndex, litIndex);
      
      if (newHLitIndex == HitSetCand.NON_MINIMAL) {
        if (backtrack())
          continue;
        else
          return;
      }
//      else if (newHLitIndex == HitSetCand.SKIP) {
//        claIndex++;
//        continue;
//      }
      
      int newHLit = clause.getAt(newHLitIndex); 
      hitSetCand.add(newHLit, clause, claIndex);
  
      // Save the next backtrack point
      if (newHLitIndex+1 < clause.size()) {
        backtrackPoints.add(new BacktrackPoint(claIndex, newHLitIndex+1));
      }

      // Unit propagation
      if (!litWatcher.update(newHLit, claIndex)) {
        // hit で n 番目のリテラル選択後，UP で矛盾が発生したときに，
        // n+1 番目のリテラルをテストする必要があるのにバックトラックしてしまう
        if (backtrack()) // TODO: buggy in P0.3_E20_F30.txt
          continue;
        else
          return;
      }
      
      claIndex++;
      litIndex = 0;
    }
  }      
      
  /**
   * Backtracks to the immediate previous level.
   */
  private boolean backtrack() {
    if (backtrackPoints.isEmpty())
      return false;
    BacktrackPoint point = backtrackPoints.removeLast();
    claIndex = point.getClaIndex();
    litIndex = point.getLitIndex();
//    if (claIndex == 2 && litIndex == 2)
//      System.out.println("World!");
    hitSetCand.backtrackTo(claIndex);
   
    return true;
  }

  /**
   * Sorts the family of integers.
   * @param family  the family of integers.
   */
  public void sort(List<? extends IntSet> family) {
    // Sorts the sets according to ascending order of the size of the sets.
    Collections.sort(family, new IntSetSizeOrder());
    
    // Counts the occurrences of each integer.
    HashMap<Integer, Integer> freqMap = new HashMap<Integer, Integer>();
    Iterator<? extends IntSet> i = family.iterator();
    while (i.hasNext()) {
      IntSet set = i.next();
      IntIterator j = set.iterator();
      while (j.hasNext()) {
        int n = j.next();
        if (!freqMap.containsKey(n))
          freqMap.put(n, 1);
        else
          freqMap.put(n, freqMap.get(n) + 1);
      }
    }
    
    orderedFamily = new LightArrayList<IntArraySet>();
    sortedFamily = new LightArrayList<IntArraySet>();
    
    i = family.iterator();
    while (i.hasNext()) {
      IntArraySet set = new IntArraySet(i.next());
      set.sort(new FreqOrder(freqMap));
      orderedFamily.add(set);
      set = new IntArraySet(set);
      set.sort();
      sortedFamily.add(set);
    }
    
    maxEstimateLv = 0;
    numSteps = 1;
    if (orderedFamily.size() > 50)  
      while (numSteps < 1000 && maxEstimateLv < orderedFamily.size()) 
        numSteps *= orderedFamily.get(maxEstimateLv++).size();
    else
      maxEstimateLv = -1;    
  }
  
  /**
   * Simplifies the problem.
   */
  private void simplify() {

    LightArrayList<IntArraySet> family = new LightArrayList<IntArraySet>();
    for (IntSet set : orgFamily) 
      family.add(new IntArraySet(set));

    // Counts the number of variables.
    int maxVar = 0;
    for (int i=0; i < family.size(); i++) {
      IntArraySet set = family.get(i);
      for (int j=0; j < set.size(); j++) 
        maxVar = Math.max(maxVar, Math.abs(set.getAt(j)));
    }
    
    // Creates a candidate of hitting set.
    hitSetCand = new HitSetCand(maxVar);
    // Creates a literal watcher.
    litWatcher = new LitWatcher(hitSetCand);

    boolean changed = false;
    do {    
      LightArrayList<IntArraySet> newFamily = new LightArrayList<IntArraySet>();
      
      // Finds the unit clauses.
      LightArrayList<IntArraySet> units = new LightArrayList<IntArraySet>();
      for (int i=0; i < family.size(); i++) {
        IntArraySet set = family.get(i);
        if (set.size() == 1)
          units.add(set);
        else
          newFamily.add(set);
      }
      changed = !units.isEmpty();
                
      // Removes the satisfied clauses and unsatisfied literals.
      for (int i=newFamily.size() - 1; i >= 0; i--) {
        IntArraySet set = newFamily.get(i);
        for (int j=0; j < units.size(); j++) {          
          int n = units.get(j).getAt(0);
          if (set.contains(n)) {
            newFamily.remove(i);
            break;
          }
          for (int k=set.size() - 1; k >= 0; k--) 
            if (set.getAt(k) == -n)
              set.removeAt(k);          
        }
      }

      // Records the truth value of unit literals.
      for (int i=0; i < units.size(); i++)
        hitSetCand.addTrueLit(units.get(i).getAt(0));
      
      family  = newFamily;
      
      //System.out.println("family = " + family.size());
      
    } while (changed);
   
    ArrayList<IntSet> simplified = new ArrayList<IntSet>();
    for (int i=0; i < family.size(); i++)
      simplified.add(family.get(i));
    
    // Sorts the clauses.
    sort(simplified);
    
    // Converts it to the set of clauses.
    clauses = new LightArrayList<Clause>();
    for (int i=0; i < orderedFamily.size(); i++) {
      IntArraySet lits = orderedFamily.get(i);
      IntArraySet sorted = sortedFamily.get(i);
      Clause clause = new Clause(lits, sorted);
      clauses.add(clause);
      litWatcher.attach(clause);
    }
  }
  
  /**
   * Returns the CPU time in milliseconds.
   * @return the CPU time in milliseconds.
   */
  public static long getCPUTime() {
    return (threadMxBean.getCurrentThreadCpuTime() / 1000000);
  }

  /**
   * The main method of MHSFinder
   * @param args command line arguments
   */
  public static void main(String[] args) {

    try {

      if (args.length == 0) {
        System.out.println("Usage: java org.nabelab.mhs.MHSFinder FILE");
        System.exit(0);
      }

      // Outputs the command line arguments.
      System.out.print("Command: " + MHSFinder.class.getName());
      for (String arg : args) 
        System.out.print(" " + arg);
      System.out.println();

      String file = null;
      for (String arg : args) {
        if (arg.equals("-r"))
          useRecursive  = true;
        else if (arg.equals("-nr"))
          useRecursive = false;
        else
          file = arg;
      }
            
      System.out.println("useRecursive = " + useRecursive);
      
      // Open the input file.
      BufferedReader reader = new BufferedReader(new FileReader(file));

      // Read the input file. The format is as follows:
      //   1 2 3   <= { 1, 2, 3 }
      //   1 5     <= { 1, 5 }
      //   3 4 5   <= { 3, 4, 5 }
      List<IntSet> family = new ArrayList<IntSet>();
      
      String line = null;
      while ((line = reader.readLine()) != null) { 
        if (line.startsWith("c") || line.startsWith("p") || line.isEmpty())
          continue;
        IntSet set = new IntArraySet();
        StringTokenizer st = new StringTokenizer(line);
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          int num = Integer.parseInt(token);
          if (num == 0)
            break;
          set.add(num);          
        }
        family.add(set);
      }
      
      System.out.println("[Input]");
      System.out.println(" num of sets = " + family.size());
      if (family.size() <= 10) {
        for (int i=0; i < family.size(); i++)
          System.out.println(" " + family.get(i));
      }
      
      Debugger env = new Debugger();
      //env.setDebug(DBG_PROGRESS, true);
      MHSFinder finder = new MHSFinder(env, family);

      // Main
      long start = getCPUTime();
      List<HitSet> mhss = finder.findAllMHS();
      long time = getCPUTime() - start;
      
      System.out.println("[Output]");
      System.out.println(" num of mhss = " + mhss.size());
      if (mhss.size() <= 10) {
        for (int i=0; i < mhss.size(); i++)
          System.out.println(" " + mhss.get(i));
      }

      System.out.println();
      System.out.println("CPU time: " + (time / 1000.0) + "sec");
      
      // Validation
      start = getCPUTime();
      int errs = 0;
      System.out.println();
      for (int i=0; i < mhss.size(); i++) {
        HitSet mhs = mhss.get(i);
        if (!mhs.isMHS(family)) {
          System.out.println("Error: " + i + ": " + mhs + " is not mhs!");
          errs++;
        }
//        else
//          System.out.println(i + ": " + mhs);
      }
      time = getCPUTime() - start;
      System.out.println("Validation finished (" + errs + "errors, " + (time / 1000.0) + "sec).");
      
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
 
  
  /** The constant means no limitation. */
  private final static int NOLIMIT = -1;

  /** The debug environment. */
  private Debuggable env = null;
  /** The original family of integers. */    
  private List<? extends IntSet> orgFamily = null;
  /** The family of integers. */
  private LightArrayList<IntArraySet> orderedFamily = null;
  /** The family of sorted integers. */
  private LightArrayList<IntArraySet> sortedFamily = null;
  /** The set of minimum hitting sets. */
  private List<HitSet> mhss = null;
  /** The hitting sets. */
  private HitSet hs = null;
  /** The mapping from integers to the set of critical set. */
  private LightArrayList<LightArrayList<IntSet>> simpleCriticals = null;
  /** The maximum size of hitting set. */
  private int maxSize = NOLIMIT;
  /** The maximum number of hitting set. */
  private int maxNum = NOLIMIT;
  
  /** If true, then use recursive mhs algorithm. */
  private static boolean useRecursive = false;
  
//  /** The truth value table. */
//  private TruthTable truthTable = null;
  /** The set of clauses. */
  private LightArrayList<Clause> clauses = null;
  /** The literal watcher. */
  private LitWatcher litWatcher = null;
  
  /** The index of the target clause to hit. */
  private int claIndex = 0;
  /** The index of the literal in the target clause. */
  private int litIndex = 0;
  
  ////private IntArrayList hsIndex = null;
  /** A candidate of hitting sets. */
  private HitSetCand hitSetCand = null;
  
  /** The mapping from integers to the set of critical set. */
  //////private LightArrayList<CriticalSets> criticals = null;
  /** The list of backtrack points. */
  private LightArrayList<BacktrackPoint> backtrackPoints = null;

  /** The maximum level for progress estimation. */
  private int maxEstimateLv = 4;
  /** The number of steps for join operation. */
  private int numSteps = 1;
  /** The current number of steps for join operation. */
  private int curSteps = 1;

  /** For getting CPU time. */
  private static ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

  private final static class IntSetSizeOrder implements Comparator<IntSet> {
    /**
     * Compares its two arguments for order.
     * @param set1 the first object to be compared.
     * @param set2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(IntSet set1, IntSet set2) {
      return set1.size() - set2.size();
    }    
  }  
  
  private final static class FreqOrder implements Comparator<Integer> {
    
    /**
     * Constructs a frequency ordering.
     */
    public FreqOrder(HashMap<Integer,Integer> freqMap) {
      this.freqMap = freqMap;
    }
    
    /**
     * Compares its two arguments for order.
     * @param val1 the first object to be compared.
     * @param val2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(Integer val1, Integer val2) {
      return freqMap.get(val2) - freqMap.get(val1);
    }    
    
    private HashMap<Integer,Integer> freqMap = null;
  }  
  
}

