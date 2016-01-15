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

package org.nabelab.solar;

import java.util.Comparator;

import org.nabelab.solar.operator.NegationAsFailure;
import org.nabelab.solar.operator.Operator;

/**
 * @author nabesima
 *
 */
public class OpOrder {

  /**
   * Constructs the clause ordering.
   * @param cfp the consequence finding problem.
   */
  public OpOrder(CFP cfp) {
    this.cfp = cfp;
    this.opt = cfp.getOptions();
    
    int o1 = opt.get1stOpOrder();
    int o2 = opt.get2ndOpOrder();
    
    use = !(o1 == ORG_ORDER && o2 == ORG_ORDER);
    
    switch (o1) {

    case ORG_ORDER:
      comparator = new Comparator<Operator>() { 
        public int compare(Operator o1, Operator o2) { 
          return 0;
        } 
      };
      break;
      
    case FEW_SYMS:
      switch (o2) {
      case ORG_ORDER:
      case FEW_SYMS:
      case MANY_SYMS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            return o1.getNumSyms() - o2.getNumSyms(); 
          } 
        };
        break;
                
      case FEW_EXTS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o1.getNumSyms() - o2.getNumSyms();
            if (diff != 0) return diff;
            return o1.getNumExts() - o2.getNumExts();
          } 
        };
        break;
        
      case MANY_EXTS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o1.getNumSyms() - o2.getNumSyms();
            if (diff != 0) return diff;
            return o2.getNumExts() - o1.getNumExts();
          } 
        };
        break;
      }
      break;
      
    case MANY_SYMS:
      switch (o2) {
      case ORG_ORDER:
      case FEW_SYMS:
      case MANY_SYMS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            return o2.getNumSyms() - o1.getNumSyms(); 
          } 
        };
        break;
                
      case FEW_EXTS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o2.getNumSyms() - o1.getNumSyms();
            if (diff != 0) return diff;
            return o1.getNumExts() - o2.getNumExts();
          } 
        };
        break;
        
      case MANY_EXTS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o2.getNumSyms() - o1.getNumSyms();
            if (diff != 0) return diff;
            return o2.getNumExts() - o1.getNumExts();
          } 
        };
        break;
      }
      break;
      
    case FEW_EXTS:
      switch (o2) {
      case ORG_ORDER:
      case FEW_EXTS:
      case MANY_EXTS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            return o1.getNumExts() - o2.getNumExts(); 
          } 
        };
        break;

      case FEW_SYMS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o1.getNumExts() - o2.getNumExts();
            if (diff != 0) return diff;
            return o1.getNumSyms() - o2.getNumSyms();
          } 
        };
        break;      

      case MANY_SYMS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o1.getNumExts() - o2.getNumExts();
            if (diff != 0) return diff;
            return o2.getNumSyms() - o1.getNumSyms();
          } 
        };
        break;      
      }
      break;

    case MANY_EXTS:
      switch (o2) {
      case ORG_ORDER:
      case FEW_EXTS:
      case MANY_EXTS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            return o2.getNumExts() - o1.getNumExts(); 
          } 
        };
        break;

      case FEW_SYMS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o2.getNumExts() - o1.getNumExts();
            if (diff != 0) return diff;
            return o1.getNumSyms() - o2.getNumSyms();
          } 
        };
        break;      

      case MANY_SYMS:
        comparator = new Comparator<Operator>() {
          public int compare(Operator o1, Operator o2) { 
            if (o1 instanceof NegationAsFailure) return +1;
            if (o2 instanceof NegationAsFailure) return -1;
            int diff = o2.getNumExts() - o1.getNumExts();
            if (diff != 0) return diff;
            return o2.getNumSyms() - o1.getNumSyms();
          } 
        };
        break;      
      }      
      break;
    }

  }

  /**
   * Returns true if the operator ordering is used.
   * @return true if the operator ordering is used.
   */
  public boolean use() {
    return use;
  }

  /**
   * Compares its two arguments for order. Returns a negative integer, zero, or
   * a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   * @param l1 the first clause to be compared.
   * @param l2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   */
  public Comparator<Operator> comparator() {
    return comparator;
  }
  
  /** Uses the original clause ordering. */
  public final static int ORG_ORDER = 0;
  /** A clause with few symbols is preferred. */
  public final static int FEW_SYMS = 1;
  /** A clause with many symbols is preferred. */
  public final static int MANY_SYMS = 2;
  /** A clause with few extendable clauses is preferred. */
  public final static int FEW_EXTS = 3;
  /** A clause with many extendable clauses is preferred. */
  public final static int MANY_EXTS = 4;
  
  /** The consequence finding problem. */
  @SuppressWarnings("unused")
  private CFP cfp = null;  
  /** The options. */
  private Options opt = null;
  /** Whether the operator ordering is used or not. */
  private boolean use;

  private Comparator<Operator> comparator = null;
  
}
