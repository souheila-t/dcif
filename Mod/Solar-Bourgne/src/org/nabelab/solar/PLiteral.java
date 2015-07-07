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

import java.io.BufferedReader;
import java.io.StringReader;

import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.parser.Parser;

public class PLiteral implements TermTypes {

  /**
   * Constructs a literal belonging to a production field.
   * @param sign the sign of this literal.
   * @param term the predicate.
   */
  public PLiteral(int sign, Term term) {
    this.sign = sign;
    this.term = term;
  }

  /**
   * Constructs a special literal that represents all literals.
   * @param sign the sign of this literal.
   */
  public PLiteral(int sign) {
    this(sign, null);
  }
  
  /**
   * Constructs a copy of the literal.
   * @param plit the literal.
   */
  public PLiteral(PLiteral plit) 
  {
    this.sign         = plit.sign;
    this.term         = plit.term;
    this.maxTermDepth = plit.maxTermDepth;
    this.maxLength    = plit.maxLength;
  }

  /**
   * Constructs a production field literal from the string.
   * @param env     the environment.
   * @param opt     the options.
   * @param literal the string representation of a production field literal (ex. "-p(a)")
   */
  public static PLiteral parse(Env env, Options opt, String literal) throws ParseException {
    return new Parser(env, opt).pliteral(new BufferedReader(new StringReader(literal)));
  }
  
  /**
   * Returns true if this literal is maximally general, i.e., all arguments of this literal are variables.
   * @return true if this literal is maximally general.
   */
  public boolean isMaxGeneral() {
    if (term == null)
      return true;
    
    int cur = term.getStart();
    int end = term.getNext(cur);
    for (cur++; cur != end; cur++) 
      if (term.getType(cur) != VARIABLE)
        return false;
        
    return true;
  }
 
  /**
   * Returns true if this literal is a special literal.
   * @return true if this literal is a special literal.
   */
  public boolean isSpecial() {
    return term == null;
  }
  
  /**
   * Sets the sign of this literal.
   * @param sign the sign to set
   */
  public void setSign(int sign) {
    this.sign = sign;
  }

  /**
   * Returns the sign of this literal.     
   * @return the sign
   */
  public int getSign() {
    return sign;
  }
  
  /**
   * Toggles the sign of this literal.
   */
  public void negate() {
    switch (sign) {
    case POS: sign = NEG; break;
    case NEG: sign = POS; break;
    }
  }
  
  /**
   * Sets the term of this literal.
   * @param term the term to set
   */
  public void setTerm(Term term) {
    this.term = term;
  }

  /**
   * Returns the predicate of this literal.
   * @return the predicate. If null, then it means this literal represents all literals.
   */
  public Term getTerm() {
    return term;
  }

  /**
   * Returns the name of this literal.
   * @return the name of this literal.
   */
  public int getName() {
    return term.getStartName();
  }
  
  /**
   * Sets the max term depth limitation.
   * @param depth the max term depth.
   */
  public void setMaxTermDepth(int depth) {
    maxTermDepth = depth;
  }

  /**
   * Returns the max term depth limitation.
   * @return the max term depth.
   */
  public int getMaxTermDepth() {
    return maxTermDepth;
  }
  
  /**
   * Sets the consequence length limitation.
   * @param length the max length of consequences.
   */
  public void setMaxLength(int length) {
    maxLength = length;
  }
  
  /**
   * Returns the consequence length limitation.
   * @return the consequence length limitation.
   */
  public int getMaxLength() {
    return maxLength;
  }

  /**
   * Returns the number of kinds of variables in this term.
   * @param deep if true, then look up values of variables in this term.  
   * @return the number of kinds of variables in this term. If there is no variable, then returns -1.
   */
  public int getNumVars() {
    return term.getNumVars();
  }
  
  /**
   * Returns the hash code value of this object.
   * @return the hash code value of this object.
   */
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + maxLength;
    result = prime * result + maxTermDepth;
    result = prime * result + sign;
    result = prime * result + ((term == null) ? 0 : term.hashCode());
    return result;
  }

  /**
   * Compares the specified object with this object for equality.
   * @param obj the reference object with which to compare.  
   */
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PLiteral other = (PLiteral) obj;
    if (maxLength != other.maxLength)
      return false;
    if (maxTermDepth != other.maxTermDepth)
      return false;
    if (sign != other.sign)
      return false;
    if (term == null) {
      if (other.term != null)
        return false;
    } else if (!term.equals(other.term))
      return false;
    return true;
  }

  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    
    if (term != null) {
      switch (sign) {
      case POS:  str.append("+"  + term); break;
      case NEG:  str.append("-"  + term); break;
      case BOTH: str.append("+-" + term); break;
      default:   assert(false);
      }
    }
    else {
      switch (sign) {
      case POS:  str.append("POS"); break;
      case NEG:  str.append("NEG"); break;
      case BOTH: str.append("ALL"); break;
      default:   assert(false);
      }
    }
    
    if (maxTermDepth != NOT_DEFINED)
      str.append(":" + maxTermDepth);
    if (maxLength != NOT_DEFINED)
      str.append(" <= " + maxLength);
    
    return str.toString();
  }
  
  /** The sign of this allowed predicate. */
  public final static int POS  = 0;
  public final static int NEG  = 1;
  public final static int BOTH = 2;  
    
  /** Depth / length limitation. */
  public final static int NOT_DEFINED = -1;

  /** The sign of this literal. */
  private int sign = BOTH;
  /** The predicate. */
  private Term term = null;
  
  /** The maximum term depth limitation */
  private int maxTermDepth = NOT_DEFINED;
  /** The maximum length limitation */
  private int maxLength = NOT_DEFINED;


}
