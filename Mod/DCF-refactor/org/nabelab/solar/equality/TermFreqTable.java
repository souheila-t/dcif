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

import java.util.List;

import org.nabelab.solar.Clause;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Signature;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.Term;
import org.nabelab.solar.TermTypes;

public class TermFreqTable implements TermTypes {

  /**
   * Constructs a term frequency table.
   * @param symTable  the symbol table.
   */
  public TermFreqTable(SymTable symTable) {
    this.symTable = symTable;
  }
  
  /**
   * Counts up the frequency of every term for the specified clauses.
   * @param clauses  the set of clauses.
   */
  public void count(List<Clause> clauses) {

    posConstants  = new int[symTable.getNumSyms(CONSTANT)];
    negConstants  = new int[symTable.getNumSyms(CONSTANT)];
    posFunctions  = new int[symTable.getNumSyms(FUNCTION)];
    negFunctions  = new int[symTable.getNumSyms(FUNCTION)];
    posPredicates = new int[symTable.getNumSyms(PREDICATE)];
    negPredicates = new int[symTable.getNumSyms(PREDICATE)];
    
    for (Clause clause : clauses) {
      for (int i=0; i < clause.size(); i++) {
        Literal lit  = clause.get(i);
        Term    term = lit.getTerm();
        int     cur  = term.getStart();
        int     end  = term.getNext(cur);
        while (cur != end) {
          int name = term.getName(cur);
          int type = term.getType(cur);
          if (lit.isPositive()) {
            switch (type) {
            case CONSTANT:  posConstants[name]++;  break;                                          
            case FUNCTION:  posFunctions[name]++;  break;
            case PREDICATE: posPredicates[name]++; break;
            default:        break;
            }
          }
          else {
            switch (type) {
            case CONSTANT:  negConstants[name]++;  break;                                          
            case FUNCTION:  negFunctions[name]++;  break;
            case PREDICATE: negPredicates[name]++; break;
            default:        break;
            }            
          }
          cur++;
        }
      }
    }    
  }
  
  /**
   * Return the number of occurrences of the specified term.
   * @param type  the type of the term.
   * @param name  the name of the term. 
   * @return  the number of occurrences of the specified term.
   */
  public int get(int type, int name) {
    switch (type) {
    case CONSTANT:  return posConstants[name]  + negConstants[name];
    case FUNCTION:  return posFunctions[name]  + negFunctions[name];
    case PREDICATE: return posPredicates[name] + negPredicates[name];
    default:        assert(false); return 0;
    }            
  }

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    if (posConstants != null) {
      str.append("[Constants]\n");
      for (Signature sig : symTable.getConstants())
        str.append(sig + " : " + get(CONSTANT, sig.getID()) + "\n"); 
      str.append("[Functions]\n");
      for (Signature sig : symTable.getFunctions())
        str.append(sig + " : " + get(FUNCTION, sig.getID()) + "\n"); 
      str.append("[Predicates]\n");
      for (Signature sig : symTable.getPredicates())       
        str.append(sig + " : " + get(PREDICATE, sig.getID()) + "\n"); 
    }
    else
      str.append("NOT COUNTED");
   
    return str.toString();    
  }
  
  /** The symbol table */
  private SymTable symTable = null;
  
  /** The frequency of constants in positive literals. */
  private int[] posConstants = null;
  /** The frequency of constants in negative literals. */
  private int[] negConstants = null;
  /** The frequency of functions in positive literals. */
  private int[] posFunctions = null;
  /** The frequency of functions in negative literals. */
  private int[] negFunctions = null;
  /** The frequency of predicates in positive literals. */
  private int[] posPredicates = null;
  /** The frequency of predicates in negative literals. */
  private int[] negPredicates = null;
}
