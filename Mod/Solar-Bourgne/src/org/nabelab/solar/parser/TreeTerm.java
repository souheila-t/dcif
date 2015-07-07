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

package org.nabelab.solar.parser;

import java.util.Stack;
import java.util.List;
import java.util.ListIterator;

import org.nabelab.solar.*;

/**
 * A term class represented by tree data structure.
 * 
 * @author nabesima
 *
 */
public class TreeTerm implements TermTypes {
  
  /**
   * Constructs a term object.
   * @param env  the environment.
   * @param name the name of the term.
   * @param type the type of the term.
   * @param args the arguments of the term.
   */
  private TreeTerm(Env env, int name, int type, List<TreeTerm> args)
  {
    this.env  = env;
    this.name = name;
    this.type = type;
    this.args = args;
    
    numSymbols++;
    if (args != null)
      for (TreeTerm arg : args)
        numSymbols += arg.numSymbols;
  }
  
  /**
   * Constructs a new predicate with no arguments.
   * @param env  the environment.
   * @param name the name of the predicate.
   */
  public static TreeTerm newPredicate(Env env, int name)
  {
    return new TreeTerm(env, name, PREDICATE, null); 
  }
  
  /**
   * Constructs a new predicate with arguments.
   * @param env  the environment.
   * @param name the name of the predicate.
   * @param args the arguments of the predicate.
   */
  public static TreeTerm newPredicate(Env env, int name, List<TreeTerm> args)
  {
    return new TreeTerm(env, name, PREDICATE, args); 
  }

  /**
   * Constructs a new function.
   * @param env  the environment.
   * @param name the name of the function.
   * @param args the arguments of the function.
   */
  public static TreeTerm newFunction(Env env, int name, List<TreeTerm> args)
  {
    return new TreeTerm(env, name, FUNCTION, args); 
  }
  
  /**
   * Constructs a new constant.
   * @param env  the environment.
   * @param name the name of the constant.
   */
  public static TreeTerm newConstant(Env env, int name)
  {
    return new TreeTerm(env, name, CONSTANT, null); 
  }
   
  /**
   * Constructs a new integer.
   * @param env  the environment.
   * @param name the value of the integer.
   */
  public static TreeTerm newInteger(Env env, int value)
  {
    return new TreeTerm(env, value, INTEGER, null); 
  }
     
  /**
   * Constructs a new variable.
   * @param env  the environment.
   * @param name the name of the variable.
   */
  public static TreeTerm newVariable(Env env, int name)
  {
    return new TreeTerm(env, name, VARIABLE, null); 
  }   

  /**
   * Converts this to the term object.
   * @return the term object.
   */
  public Term toTerm()
  {
    int[] nameArray = new int[numSymbols];
    int[] typeArray = new int[numSymbols];
    int[] nextArray = new int[numSymbols];
    
    Stack<ListIterator<TreeTerm>> rest = new Stack<ListIterator<TreeTerm>>();
    
    TreeTerm term = this;
    int i= 0;
    ListIterator<TreeTerm> j = null;
    while (true) {
      nameArray[i] = term.name;
      typeArray[i] = term.type;
      nextArray[i] = i + term.numSymbols;
      i++;
      if (term.args != null) {
        if (j != null && j.hasNext())
          rest.push(j);
        j = term.args.listIterator();
      }
      else if (j == null)
        break;
      else if (!j.hasNext()) {
        if (rest.isEmpty()) 
          break;
        j = rest.pop();
      }
      term = j.next();
    }
    
    return new Term(env, nameArray, typeArray, nextArray);
  }
  
  /**
   * Returns a string representation of this object.
   * 
   * @return a string representation of this object.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();
    SymTable symTable = env.getSymTable();
    
    str.append(symTable.get(name, type));
    
    if (args != null)
      str.append(args.toString());
    
    return str.toString();
  }

  
  // The environment. */
  private Env env = null;
  // The name of this term.
  private int name = 0;
  // The type of this term.
  private int type = 0;
  // The arguments of this term.
  private List<TreeTerm> args = null;
  // The number of symbols used in the term.
  private int numSymbols = 0;

}
