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
public interface Tags {

  public final static int NONE                   = 0x00000000;
  public final static int SKIPPED                = 0x00000001;
  public final static int FACTORED               = 0x00000002;
  public final static int MERGED                 = 0x00000004;
  public final static int EXTENDED               = 0x00000008;
  public final static int RESTARTTED             = 0x00000010;
  public final static int EQ_RESOLVED            = 0x00000020;
  public final static int SYMMETRY_SPLITTED      = 0x00000040;
  public final static int NEG_EQ_FLATTENED       = 0x00000080;
  public final static int EQ_EXTENDED            = 0x00000100;
  public final static int REDUCED                = 0x00000200;
  public final static int IDENTICAL_REDUCED      = 0x00000400;
  public final static int UNIT_AXIOM_MATCHED     = 0x00000800;
  public final static int UNIT_LEMMA_MATCHED     = 0x00001000;
  public final static int UNIT_LEMMA_EXTENSION   = 0x00002000;
  public final static int STRONG_CONTRACTION     = 0x00004000;
  public final static int IDENTICAL_C_REDUCED    = 0x00008000;
  public final static int IDENTICAL_FOLDING_DOWN = 0x00010000;
  public final static int CLOSED_BY_NAF          = 0x00020000;
  public final static int DIVISION_COMPLETED     = 0x00040000;

  public final static int CLOSED              = EXTENDED | RESTARTTED | REDUCED
                                                  | EQ_RESOLVED
                                                  | SYMMETRY_SPLITTED
                                                  | NEG_EQ_FLATTENED
                                                  | EQ_EXTENDED
                                                  | IDENTICAL_REDUCED
                                                  | UNIT_AXIOM_MATCHED
                                                  | UNIT_LEMMA_MATCHED
                                                  | UNIT_LEMMA_EXTENSION
                                                  | STRONG_CONTRACTION
                                                  | IDENTICAL_C_REDUCED
                                                  | IDENTICAL_FOLDING_DOWN
                                                  | CLOSED_BY_NAF;

  public final static int SOLVED              = SKIPPED | FACTORED | MERGED | CLOSED | DIVISION_COMPLETED;
  
  public final static int DIVIDED               = 0x02000000;
  public final static int NOT_EXHAUSTED         = 0x04000000;
  public final static int NOT_SKIPPABLE         = 0x08000000;
  public final static int EQ_RAW                = 0x10000000;
  public final static int EQ_MATURE             = 0x20000000;
  public final static int CONTRACTIBLE          = 0x40000000;
  public final static int SOLVABLE              = 0x80000000;

}
