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
public interface DebugTypes {
  // SOLAR
  public final static int DBG_APPLIED_OPS        = 'a';
  public final static int DBG_ALL_INFO           = 'A';
  public final static int DBG_BRIDGE             = 'b';
  public final static int DBG_CONSTRAINT         = 'c';
  public final static int DBG_DIV_CONQ           = 'd';
  public final static int DBG_DIV_CONQ_DETAIL    = 'D';
  public final static int DBG_LOCAL_FAIL         = 'e';
  public final static int DBG_LOCAL_FAIL_DETAIL  = 'E';
  public final static int DBG_FOLDING_UP         = 'f';
  public final static int DBG_FOLDING_UP_DETAIL  = 'F';
  public final static int DBG_CONSQ              = 'g';
  public final static int DBG_CONSQ_DETAIL       = 'G';
  public final static int DBG_INFERENCE_INFO     = 'i';
  public final static int DBG_UNIT_LEMMA         = 'l';
  public final static int DBG_UNIT_LEMMA_DETAIL  = 'L';
  public final static int DBG_MODIFICATION       = 'm';
  public final static int DBG_SKIP_MINIMALITY    = 'M';
  public final static int DBG_LIT_ORDER          = 'O';
  public final static int DBG_PROBLEM            = 'p';
  public final static int DBG_STRONG_CONTRACTION = 'r';
  public final static int DBG_REDUCTION_ORDER    = 'R';
  public final static int DBG_STEPS              = 's';
  public final static int DBG_SYMBOL_TABLE       = 'S';
  public final static int DBG_TABLEAUX           = 't';
  public final static int DBG_SOLVED_TABLEAUX    = 'T';
  public final static int DBG_UNIT_AXIOM         = 'u';
  public final static int DBG_TAUTOLOGY_FREE     = 'U';
  public final static int DBG_VERBOSE            = 'v';
  public final static int DBG_FEATURE_VECTOR     = 'V';
  
  // ConseqMgr
  public final static int DBG_PROGRESS           = 'p';
}
