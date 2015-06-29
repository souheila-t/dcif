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
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.nabelab.util.IntPair;
import org.nabelab.util.ObjIntPair;

public class PrefixSpanOrg<T> {
  
  public PrefixSpanOrg (int minsup, int minpat, int maxpat, boolean all, boolean where, boolean verbose) {
    this.minsup  = minsup;
    this.minpat  = minpat;
    this.maxpat  = maxpat;
    this.all     = all;
    this.where   = where;
    this.verbose = verbose;
  }
  
  public ArrayList<ArrayList<T>> mine() {
    if (verbose) 
      for (ArrayList<T> t : transactions)
        out.println(t);
    
    ArrayList<IntPair> root = new ArrayList<IntPair>();
    for (int i=0; i < transactions.size(); i++) 
      root.add(new IntPair(i, -1));
     project(root);      
     return null;
  }

  private void report(ArrayList<IntPair> projected) {
    
    if (minpat > pattern.size()) return;
    if (pattern.size() == 0) return;

    // print where & pattern
    if (where) { 
      out.println("<pattern>");

      // what:
      if (all) {
        out.println("<freq>" + pattern.get(pattern.size()-1).get2nd() + "</freq>");
        out.print("<what>");
        for (int i=0; i < pattern.size(); i++) 
          out.print((i!=0 ? " " : "") + pattern.get(i).get1st());
      } else {
        out.print("<what>");
        for (int i=0; i < pattern.size(); i++)
          out.print((i!=0 ? " " : "") + pattern.get(i).get1st() + delimiter + "(" + pattern.get(i).get2nd() + ")");
      }
      out.println("</what>");
      
      // where
      out.print("<where>");
      for (int i=0; i < projected.size(); i++) 
        out.print((i!=0 ? " " : "") + projected.get(i).get1st());
      out.println("</where>");

      out.println("</pattern>");

    } else {

      // print found pattern only
      if (all) {
        out.print("(" + pattern.get(pattern.size()-1).get2nd() + ")");
        for (int i=0; i < pattern.size(); i++)
          out.print(" " + pattern.get(i).get1st());
      } else {
        for (int i=0; i < pattern.size(); i++)
          out.print((i!=0 ? " " : "") + pattern.get(i).get1st() + delimiter + "(" + pattern.get(i).get2nd() + ")");
      }
      out.println();
    }
  }
  
  private void project(ArrayList<IntPair> projected) {
    
    if (all) 
      report(projected);
    
    HashMap<T, ArrayList<IntPair>> counter = new HashMap<T, ArrayList<IntPair>>();
  
    for (int i=0; i < projected.size(); i++) {
      int id   = projected.get(i).get1st();
      int pos  = projected.get(i).get2nd();
      int size = transactions.get(id).size();
 
      // Projected transaction: 
      //    2 3 4 5 6
      //   {a a b b c}
      // tmp = {<a,2>, <b,4>, <c,6>}
      HashMap<T, Integer> tmp = new HashMap<T, Integer>();
      for (int j=pos + 1; j < size; j++) {
        T item = transactions.get(id).get(j);
        if (!tmp.containsKey(item))
          tmp.put(item, j);
      }

      // For each item in the projected transaction, records the transaction id and the occurrence position of the item. 
      for (Map.Entry<T, Integer> e : tmp.entrySet()) { 
        if (!counter.containsKey(e.getKey()))
          counter.put(e.getKey(), new ArrayList<IntPair>());
        counter.get(e.getKey()).add(new IntPair(id, e.getValue()));
      }
    }

    ArrayList<T> removedKeys = new ArrayList<T>();
    for (Map.Entry<T, ArrayList<IntPair>> e : counter.entrySet()) 
      if (e.getValue().size() < minsup) 
        removedKeys.add(e.getKey());
    for (T key : removedKeys)
      counter.remove(key);

    if (!all && counter.size () == 0) {
      report (projected);
      return;
    }
    
    for (Map.Entry<T, ArrayList<IntPair>> e : counter.entrySet()) {
      if (pattern.size() < maxpat) {
        pattern.add(new ObjIntPair<T>(e.getKey(), e.getValue().size()));
        project(e.getValue());
        pattern.remove(pattern.size() - 1);
      }
    }
  }  
  
  public void clear () {
    transactions.clear ();
    pattern.clear ();
  }

  public static void main(String args[]) {
    
    try {
      int minsup = 0;
      int minpat = 0;
      int maxpat = Integer.MAX_VALUE;
      boolean all = false;
      boolean where = false;
      String delimiter = "/";
      boolean verbose = false;
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
        else if (op.equals("-a"))
          all = true;
        else if (op.equals("-w"))
          where = true;
        else if (op.equals("-v"))
          verbose = true;
        else if (op.equals("-d")) {
          delimiter = oparg;
          i++;
        }
        else if (op.startsWith("-h") || op.startsWith("--h"))
          help = true;
        else
          file = op;
      }

      if (help || file == null) {
        System.out.println("Usage: java org.nabelab.mining.PrefixSpan");
        System.out.println(" [-m minsup] [-M minpat] [-L maxpat] [-a] [-w] [-v] [-d delimiter] FILE");
        System.exit(-1);      
      }
    
      PrefixSpanOrg<Integer> prefixSpan = new PrefixSpanOrg<Integer>(minsup, minpat, maxpat, all, where, verbose);
      prefixSpan.load(file);
      prefixSpan.mine();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("unchecked")
  private void load(String file) throws IOException {
   
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = null;
    while ((line = reader.readLine()) != null) {
      ArrayList<T> transaction = new ArrayList<T>();
      StringTokenizer tokenizer = new StringTokenizer(line, delimiter);
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        T item = (T)new Integer(token);
        transaction.add(item);
      }
      transactions.add(transaction);
    }
    
  }

  /** The list of transactions. */
  private ArrayList<ArrayList<T>> transactions = new ArrayList<ArrayList<T>>();
  /** A pattern. */
  ArrayList<ObjIntPair<T>> pattern = new ArrayList<ObjIntPair<T>>();
  
  /** The minimum support */
  private int minsup = 0;
  /** The minimum length of patterns */
  private int minpat = 0;
  /** The maximum length of patterns */
  private int maxpat = Integer.MAX_VALUE;
  
  private boolean all = false;
  private boolean where = false;
  private String delimiter = " ";
  private boolean verbose = false;

  /** An output stream. */
  PrintStream out = System.out;
    
}
