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

/**
 * Literal ordering in tableau clauses. 
 * @author nabesima
 */
public class LitOrder implements TermTypes,OptionTypes {

  /**
   * Constructs the literal ordering.
   * @param cfp the consequence finding problem.
   */
  public LitOrder(CFP cfp) {
    this.cfp = cfp;
    this.opt = cfp.getOptions();
    
    int o1 = opt.get1stLitOrder();
    int o2 = opt.get2ndLitOrder();
    
    boolean o1IsFix = (o1 == FIX_FEW_SYMS || o1 == FIX_FEW_EXTS || o1 == FIX_MANY_SYMS || o1 == FIX_MANY_EXTS);
    boolean o2IsFix = (o2 == FIX_FEW_SYMS || o2 == FIX_FEW_EXTS || o2 == FIX_MANY_SYMS || o2 == FIX_MANY_EXTS);
    boolean o1IsDyn = (o1 == DYN_FEW_SYMS || o1 == DYN_FEW_EXTS || o1 == DYN_MANY_SYMS || o1 == DYN_MANY_EXTS || o1 == DYN_SYMS_PER_EXTS || o1 == DYN_SYMS_PER_DYN_EXTS);
    boolean o2IsDyn = (o2 == DYN_FEW_SYMS || o2 == DYN_FEW_EXTS || o2 == DYN_MANY_SYMS || o2 == DYN_MANY_EXTS);

    isOrg = (o1 == ORG_ORDER && o2 == ORG_ORDER);
    isFix = (o1IsFix || o2IsFix || o2 == ORG_ORDER);
    isDyn = (o1IsDyn || o2IsDyn);
    
    useDynSyms = (o1 == DYN_FEW_SYMS || o1 == DYN_MANY_SYMS || o2 == DYN_FEW_SYMS || o2 == DYN_MANY_SYMS || o1 == DYN_SYMS_PER_EXTS || o1 == DYN_SYMS_PER_DYN_EXTS);
    useDynExts = (o1 == DYN_FEW_EXTS || o1 == DYN_MANY_EXTS || o2 == DYN_FEW_EXTS || o2 == DYN_MANY_EXTS || o1 == DYN_SYMS_PER_DYN_EXTS);

    switch (o1) {

    case ORG_ORDER:
      comparator = new Comparator<Literal>() { 
        public int compare(Literal o1, Literal o2) { 
          return 0;
        } 
      };
      break;
      
    case FIX_FEW_SYMS:
    case DYN_FEW_SYMS:
      switch (o2) {
      case ORG_ORDER:
      case FIX_FEW_SYMS:
      case DYN_FEW_SYMS:
      case FIX_MANY_SYMS:
      case DYN_MANY_SYMS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            return o1.getNumSyms() - o2.getNumSyms();
          } 
        };
        break;
        
      case FIX_FEW_EXTS:
      case DYN_FEW_EXTS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o1.getNumSyms() - o2.getNumSyms();
            if (diff != 0) return diff;
            return o1.getNumExts() - o2.getNumExts();
          } 
        };
        break;
              
      case FIX_MANY_EXTS:
      case DYN_MANY_EXTS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o1.getNumSyms() - o2.getNumSyms();
            if (diff != 0) return diff;
            return o2.getNumExts() - o1.getNumExts();
          } 
        };
        break;
      }
      break;
      
    case FIX_FEW_EXTS:
    case DYN_FEW_EXTS:
      switch (o2) {
      case ORG_ORDER:
      case FIX_FEW_EXTS:
      case DYN_FEW_EXTS:
      case FIX_MANY_EXTS:
      case DYN_MANY_EXTS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            return o1.getNumExts() - o2.getNumExts();
          } 
        };
        break;
        
      case FIX_FEW_SYMS:
      case DYN_FEW_SYMS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o1.getNumExts() - o2.getNumExts();
            if (diff != 0) return diff;
            return o1.getNumSyms() - o2.getNumSyms();
          } 
        };
        break;
        
      case FIX_MANY_SYMS:
      case DYN_MANY_SYMS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o1.getNumExts() - o2.getNumExts();
            if (diff != 0) return diff;
            return o2.getNumSyms() - o1.getNumSyms();
          } 
        };
        break;      

      }
      break;
      
    case FIX_MANY_SYMS:
    case DYN_MANY_SYMS:
      switch (o2) {
      case ORG_ORDER:
      case FIX_FEW_SYMS:
      case DYN_FEW_SYMS:
      case FIX_MANY_SYMS:
      case DYN_MANY_SYMS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            return o2.getNumSyms() - o1.getNumSyms();
          } 
        };
        break;
        
      case FIX_FEW_EXTS:
      case DYN_FEW_EXTS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o2.getNumSyms() - o1.getNumSyms();
            if (diff != 0) return diff;
            return o1.getNumExts() - o2.getNumExts();
          } 
        };
        break;
              
      case FIX_MANY_EXTS:
      case DYN_MANY_EXTS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o2.getNumSyms() - o1.getNumSyms();
            if (diff != 0) return diff;
            return o2.getNumExts() - o1.getNumExts();
          } 
        };
        break;
      }
      break;
      
    case FIX_MANY_EXTS:
    case DYN_MANY_EXTS:
      switch (o2) {
      case ORG_ORDER:
      case FIX_FEW_EXTS:
      case DYN_FEW_EXTS:
      case FIX_MANY_EXTS:
      case DYN_MANY_EXTS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            return o2.getNumExts() - o1.getNumExts();
          } 
        };
        break;
        
      case FIX_FEW_SYMS:
      case DYN_FEW_SYMS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o2.getNumExts() - o1.getNumExts();
            if (diff != 0) return diff;
            return o1.getNumSyms() - o2.getNumSyms();
          } 
        };
        break;
        
      case FIX_MANY_SYMS:
      case DYN_MANY_SYMS:
        comparator = new Comparator<Literal>() {
          public int compare(Literal o1, Literal o2) { 
            if (opt.use(USE_NEGATION_AS_FAILURE)) {
              if (o1.hasNAF() && !o2.hasNAF()) return +1;
              if (!o1.hasNAF() && o2.hasNAF()) return -1;
            }
//            if (opt.getCalcType() == CALC_RME) {
//              if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
//                return -1;
//              if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
//                return +1;
//            }          
            int diff = o2.getNumExts() - o1.getNumExts();
            if (diff != 0) return diff;
            return o2.getNumSyms() - o1.getNumSyms();
          } 
        };
        break;      

      }
      break;
      
    case DYN_SYMS_PER_EXTS:
    case DYN_SYMS_PER_DYN_EXTS:
      comparator = new Comparator<Literal>() {
        public int compare(Literal o1, Literal o2) {
          if (opt.use(USE_NEGATION_AS_FAILURE)) {
            if (o1.hasNAF() && !o2.hasNAF()) return +1;
            if (!o1.hasNAF() && o2.hasNAF()) return -1;
          }
          
          if (o1.isConnPred() != o2.isConnPred()) {    // one is a connector predicate and the other is not a connector predicate.
            if (o1.isConnPred())
              return +1;    // o1 is greater than o2.
            if (o2.isConnPred())
              return -1;    // o1 is less than o2.
//            if (o1.isSrcConnPred())
//              return +1;    // o1 is greater than o2.
//            if (o2.isSrcConnPred())
//              return -1;    // o1 is less than o2.
          }
          
          if (opt.getCalcType() == CALC_RME) {
            if (o1.isPosEqualPred() && o2.isPosEqualPred())
              return 0;
            //if (o1.isPositive() && !o1.hasTag(NO_RESTART) && o2.isNegative())
            if (o1.isPosEqualPred())
              return +1;
            //if (o1.isNegative() && o2.isPositive() && !o2.hasTag(NO_RESTART))
            if (o2.isPosEqualPred())             
              return -1;
          }          

          if (o1.getNumExts() == 0) return -1;
          if (o2.getNumExts() == 0) return +1;
          float o1cost = (float)o1.getNumSyms() / (float)(o1.getNumExts() + 1);
          float o2cost = (float)o2.getNumSyms() / (float)(o2.getNumExts() + 1);
          if (o1cost > o2cost)
            return -1;
          else if (o1cost < o2cost)
            return +1;
          return 0;
        } 
      };      
      break;
    }

  }

  /**
   * Returns true if the original literal ordering is used.
   * @return true if the original literal ordering is used.
   */
  public boolean isOrg() {
    return isOrg;
  }

  /**
   * Returns true if the static literal ordering is used.
   * @return true if the static literal ordering is used.
   */
  public boolean isFix() {
    return isFix;
  }

  /**
   * Returns true if the dynamic literal ordering is used.
   * @return true if the dynamic literal ordering is used.
   */
  public boolean isDyn() {
    return isDyn;
  }
  
  /**
   * Returns true if this ordering uses the dynamic symbol ordering.
   * @return true if this ordering uses the dynamic symbol ordering.
   */
  public boolean useDynSyms() {
    return useDynSyms;
  }
  
  /**
   * Returns true if this ordering uses the dynamic extendable ordering.
   * @return true if this ordering uses the dynamic extendable ordering.
   */
  public boolean useDynExts() {
    return useDynExts;
  }

  /**
   * Compares its two arguments for order. Returns a negative integer, zero, or
   * a positive integer as the first argument is less than, equal to, or greater
   * than the second.
   * @param l1 the first literal to be compared.
   * @param l2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first
   *         argument is less than, equal to, or greater than the second.
   */
  public Comparator<Literal> comparator() {
    return comparator;
  }
  
  /** Uses the original literal ordering. */
  public final static int ORG_ORDER = 0;
  /** A literal with few symbols is preferred. */
  public final static int FIX_FEW_SYMS = 1;
  /** A literal with few extendable clauses is preferred. */
  public final static int FIX_FEW_EXTS = 2;
  /** A literal with few symbols is preferred (dynamic checking, costly). */
  public final static int DYN_FEW_SYMS = 3;
  /** A literal with few extendable clauses is preferred (dynamic checking, costly). */
  public final static int DYN_FEW_EXTS = 4;
  /** A literal with many symbols is preferred. */
  public final static int FIX_MANY_SYMS = 5;
  /** A literal with many extendable clauses is preferred. */
  public final static int FIX_MANY_EXTS = 6;
  /** A literal with many symbols is preferred (dynamic checking, costly). */
  public final static int DYN_MANY_SYMS = 7;
  /** A literal with many extendable clauses is preferred (dynamic checking, costly). */
  public final static int DYN_MANY_EXTS = 8;
  /** A literal with many symbols and few extendable clauses is preferred (symbols are checked dynamically (costly)). */
  public final static int DYN_SYMS_PER_EXTS = 9;
  /** A literal with many symbols and few extendable clauses is preferred (symbols and extendables are checked dynamically (costly)). */
  public final static int DYN_SYMS_PER_DYN_EXTS = 10;
  
  /** The consequence finding problem. */
  @SuppressWarnings("unused")
  private CFP cfp = null;  
  /** The options. */
  private Options opt = null;
  /** Whether the original ordering is used or not. */
  private boolean isOrg;
  /** Whether the static ordering is used or not. */
  private boolean isFix;
  /** Whether the dynamic ordering is used or not. */
  private boolean isDyn;

  /** Whether the symbol-ordering is dynamic or not. */
  private boolean useDynSyms;
  /** Whether the extendable-ordering is dynamic or not. */
  private boolean useDynExts;
  
  private Comparator<Literal> comparator = null;
}
