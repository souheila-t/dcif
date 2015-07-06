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
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;

import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.parser.Parser;
import org.nabelab.solar.proof.Proof;

/**
 * @author nabesima
 *
 */
public class Conseq extends Clause {

  /**
   * Constructs a empty clause.
   * @param env  the environment.
   * @param name the name of the clause.
   * @param type the type of the clause.
   */
  protected Conseq(Env env, String name, int type) {
    super(env, name, type);
  }

  /**
   * Constructs a consequence.
   * @param env  the environment.
   * @param literals the set of literals contained in the clause.
   */
  public Conseq(Env env, List<Literal> literals) {
    super(env, "a", CONSEQUENCE, literals);
  }

  /**
   * Constructs a consequence.
   * @param env  the environment.
   * @param name the name of the clause.
   * @param type the type of the clause.
   * @param literals the set of literals contained in the clause.
   */
  public Conseq(Env env, String name, int type, List<Literal> literals) {
    super(env, name, type, literals);
  }
  
  /**
   * Constructs a consequence.
   * @param env  the environment.
   * @param name the name of the clause.
   * @param type the type of the clause.
   * @param literals the set of literals contained in the clause.
   */
  public Conseq(Env env, String name, int type, Literal[] literals) {
    super(env, name, type, literals);
  }

  /**
   * Constructs a consequence from the specified consequence.
   * @param clause the clause.
   */
  public Conseq(Conseq conseq) {
    super(conseq);
    if (conseq.proof != null) 
      this.proof = new Proof(conseq.proof, this);
    this.usedClauses = conseq.usedClauses;
  }

  /**
   * Constructs a unit consequence.
   * @param env  the environment.
   * @param name the name of the clause.
   * @param type the type of the clause.
   * @param literals the set of literals contained in the consequence.
   */
  public Conseq(Env env, String name, int axiom, Literal lit) {
    super(env, name, axiom, lit);
  }

  /**
   * Constructs an axiom clause.
   * @param env      the environment.
   * @param opt    the options.
   * @param name     the name of the clause.
   * @param type     the type of the clause.
   * @param literals the string representation of literals (ex. "[p(a), -q(b)]")
   */
  public static Conseq parse(Env env, Options opt, String literals) throws ParseException {
    return parse(env, opt, "an", AXIOM, literals);
  }
  
  /**
   * Constructs an axiom clause.
   * @param env    the environment.
   * @param opt    the options.
   * @param name   the name of the clause.
   * @param type   the type of the clause.
   * @param clause the string representation of literals (ex. "[p(a), -q(b)]")
   */
  public static Conseq parse(Env env, Options opt, String name, int type, String clause) throws ParseException {
    List<Literal> literals = new Parser(env, opt).literals(new BufferedReader(new StringReader(clause)));
    return new Conseq(env, literals);
  }

  /**
   * Returns a new consequence in which all variables are replaced with the values.
   * @return a new consequence in which all variables are replaced with the values.
   */
  public Conseq instantiate() {
    Conseq newClause = new Conseq(env, name, type);
    newClause.origin = this.origin;
    newClause.literals = new Literal[literals.length];
    for (int i = 0; i < literals.length; i++)
      newClause.literals[i] = literals[i].instantiate();

    copyProperties(newClause);

    return newClause;
  
  }
  
  /**
   * Sets the proof of this clause.
   * @param proof the proof of this clause.
   */
  public void setProof(Proof proof) {
    this.proof = proof;
  }
  
  /**
   * Returns the proof of this clause. 
   * @return the proof of this clause.
   */
  public Proof getProof() {
    return proof;
  }
  
  /**
   * Sets the set of used clauses for proving this consequence.
   * @param used the set of used clauses for proving this consequence. 
   */
  public void setUsedClauses(List<Clause> used) {
    this.usedClauses = used;
  }

  /**
   * Returns the set of used clauses for proving this consequence.
   * @return the set of used clauses for proving this consequence.
   */
  public List<Clause> getUsedClauses() {
    return usedClauses;
  }
  
  /**
   * Outputs the consequences to the specified writer.
   * @param out the writer to output.
   */
  public void output(PrintStream out)  {
    StringBuilder str = new StringBuilder();
    
    str.append("conseq(");
    str.append(toString());
    
    if (usedClauses != null) {
      str.append(", used(");
      str.append(usedClauses.toString());
      str.append(")");
    }
    
    str.append(").");
    
    out.println(str);
  }
  
  /** The proof of this clause. */
  private Proof proof = null;
  /** The set of used clauses for proving this consequence. */
  private List<Clause> usedClauses = null;

}
