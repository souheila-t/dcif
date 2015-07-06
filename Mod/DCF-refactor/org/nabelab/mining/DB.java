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

package org.nabelab.mining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.nabelab.util.IntIterator;

@SuppressWarnings("serial")
public class DB extends ArrayList<ItemSet> {

  /**
   * Returns closed frequent item sets.
   * @param miner   a mining algorithm.
   * @param minsup  minimum support.
   * @param minpat  minimum pattern length.
   * @param maxpat  maximum pattern length.
   * @return closed frequent item sets.
   */
  public ArrayList<FreqItemSet> getClosedFreqItemSets(Miner miner) {
    return getClosedFreqItemSets(miner, 1);
  }

  /**
   * Returns closed frequent item sets.
   * @param miner   a mining algorithm.
   * @param minsup  minimum support.
   * @return closed frequent item sets.
   */
  public ArrayList<FreqItemSet> getClosedFreqItemSets(Miner miner, int minsup) {
    return getClosedFreqItemSets(miner, minsup, 1, Integer.MAX_VALUE);
  }
  
  /**
   * Returns closed frequent item sets.
   * @param miner   a mining algorithm.
   * @param minsup  minimum support.
   * @param minpat  minimum pattern length.
   * @return closed frequent item sets.
   */
  public ArrayList<FreqItemSet> getClosedFreqItemSets(Miner miner, int minsup, int minpat) {
    return getClosedFreqItemSets(miner, minsup, minpat, Integer.MAX_VALUE);
  }
  
  /**
   * Returns closed frequent item sets.
   * @param miner   a mining algorithm.
   * @param minsup  minimum support.
   * @param minpat  minimum pattern length.
   * @param maxpat  maximum pattern length.
   * @return closed frequent item sets.
   */
  public ArrayList<FreqItemSet> getClosedFreqItemSets(Miner miner, int minsup, int minpat, int maxpat) {
    // Applies the mining algorithm to this DB.
    return miner.getClosedFreqItemSets(this, minsup, minpat, maxpat);
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (ItemSet items : this) {
      IntIterator i = items.iterator();
      while (i.hasNext()) 
        str.append(i.next() + " ");
      str.append('\n');
    }
    return str.toString();
  }  
  /**
   * Main method for test.
   * @param args  a list of command line arguments. 
   */
 public static void main(String args[]) {
    
    try {
      int minsup = 0;
      int minpat = 0;
      int maxpat = Integer.MAX_VALUE;
      boolean help = false;
      String file = null;

      for (int i=0; i < args.length; i++) {
        String op    = args[i];
        String oparg = (i+1 < args.length) ? args[i+1] : null; 
        if (op.equals("-m")) {
          minsup = Integer.parseInt(oparg);
          i++;
        }
        else if (op.equals("-M")) {
          minpat = Integer.parseInt(oparg);
          i++;
        }
        else if (op.equals("-L")) {
          maxpat = Integer.parseInt(oparg);
          i++;
        }
        else if (op.startsWith("-h") || op.startsWith("--h"))
          help = true;
        else
          file = op;
      }

      if (help || file == null) {
        System.out.println("Usage: java org.nabelab.mining.DB");
        System.out.println(" [-m minsup] [-M minpat] [-L maxpat] FILE");
        System.exit(-1);      
      }
    
      DB db = new DB();
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = reader.readLine()) != null) {
        ItemSet transaction = new ItemSet();
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          transaction.add(Integer.parseInt(token));
        }
        db.add(transaction);
      }
      
      PrefixSpan prefixSpan = new PrefixSpan();
      ArrayList<FreqItemSet> freqItemSets = db.getClosedFreqItemSets(prefixSpan, minsup, minpat, maxpat);
      for (FreqItemSet freqItemSet : freqItemSets)
        System.out.println(freqItemSet);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
