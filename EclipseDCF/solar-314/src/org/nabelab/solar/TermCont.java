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
 * Term continuance information based on the liked list data structure.
 * @author nabesima
 */
public final class TermCont {

  /**
   * Constructs a term continuance information.
   * @param term the term that will be followed.
   * @param pos  the restart position.  
   * @param end  the end position.
   * @param prev the previous term continuance information.
   */
  public TermCont(Term term, int pos, int end, TermCont prev) {
    this.term = term;
    this.cur  = pos;
    this.end  = end;    
    this.prev = prev;
  }

  /** 
   * Returns the term that will be followed.
   * @return the term that will be followed.
   */
  public Term getTerm() {
    return term;
  }

  /**
   * Returns the restart position.
   * @return the restart position.
   */
  public int getCur() {
    return cur;
  }

  /**
   * Returns the end position.
   * @return the end position.
   */
  public int getEnd() {
    return end;
  }

  /**
   * Returns the previous term continuance information.
   * @return the previous term continuance information.
   */
  public TermCont getPrev() {
    return prev;
  }

  /** The term that will be followed. */
  private Term term = null;
  /** The restart position. */
  private int cur = 0;
  /** The end position. */
  private int end = 0;
  /** The previous term continuance information. */
  private TermCont prev = null;
}
  
  
