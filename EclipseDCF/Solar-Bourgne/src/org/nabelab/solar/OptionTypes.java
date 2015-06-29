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

/**
 * @author nabesima
 * 
 */
public interface OptionTypes {

  // Type of tableau calculus.
  public final static int CALC_ME                         = 0;
  public final static int CALC_RME                        = 1;

  // Pruning techniques.
  public final static int USE_IDENTICAL_REDUCTION         = 0;
  public final static int USE_ORDER_PRESERVING_REDUCTION  = 1;
  public final static int USE_UNIT_AXIOM_MATCHING         = 2;
  public final static int USE_UNIT_LEMMA_MATCHING         = 3;
  public final static int USE_UNIT_LEMMA_EXTENSION        = 4;
  public final static int USE_STRONG_CONTRACTION          = 5;
  public final static int USE_IDENTICAL_C_REDUCTION       = 6;
  public final static int USE_IDENTICAL_FOLDING_DOWN      = 7;
  public final static int USE_REGULARITY                  = 8;
  public final static int USE_COMPLEMENT_FREE             = 9;
  public final static int USE_TAUTOLOGY_FREE              = 10;
  public final static int USE_UNIT_SUBSUMPTION            = 11;
  public final static int USE_LOCAL_FAILURE_CACHE         = 12;
  public final static int USE_SKIP_REGULARITY             = 13;
  public final static int USE_SKIP_MINIMALITY             = 14;

  // Built-in techniques.
  public final static int USE_NEGATION_AS_FAILURE         = 15;

  // Preprocessing techniques.
  public final static int USE_CLAUSE_SUBSUMP_MINIZING     = 16;
  public final static int USE_PURE_LITERAL_ELIMINATION    = 17;
  public final static int USE_FREQ_COMMON_LITS_EXTRACTION = 18;

  // Implementation techniques.
  public final static int USE_NODE_INSTANTIATION          = 19;
  public final static int USE_CONSTRAINT_INSTANTIATION    = 20;

  // Techniques for skip operations.
  public final static int USE_BRIDGE_FORMULA_TRANSLATION  = 21;

  // Techniques for characteristic clause computation.
  public final static int USE_INC_CARC_COMP               = 22;

  public final static int USE_TEST1                       = 23;
  public final static int USE_TEST2                       = 24;
  public final static int USE_TEST3                       = 25;
  public final static int USE_TEST4                       = 26;
  public final static int NUM_OPTION_TYPES                = 27;

}
