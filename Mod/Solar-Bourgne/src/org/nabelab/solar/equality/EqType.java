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

package org.nabelab.solar.equality;

import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;

/**
 * @author nabesima
 *
 */
public class EqType implements TermTypes {
  
  /**
   * Constructs a equality type information.
   * @param term  the original term in the axiom set.
   */
  public EqType(Term term) {
    int arg1Pos = term.getStart() + 1;
    int arg2Pos = term.getNext(arg1Pos);
    arg1IsVar = term.getType(arg1Pos) == VARIABLE;
    arg2IsVar = term.getType(arg2Pos) == VARIABLE;
  }
  
  /**
   * Returns true if the first argument is variable.
   * @return true if the first argument is variable.
   */
  public boolean arg1IsVar() {
    return arg1IsVar;
  }

  /**
   * Returns true if the second argument is variable.
   * @return true if the second argument is variable.
   */
  public boolean arg2IsVar() {
    return arg2IsVar;
  }

  /** Whether the first argument is variable or not. */
  private boolean arg1IsVar = false;
  /** Whether the second argument is variable or not. */
  private boolean arg2IsVar = false;
  
}
