/* Generated By:JavaCC: Do not edit this line. Parser.java */
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

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.nabelab.solar.*;
import org.nabelab.solar.pfield.*;

/**
 * A consequence finding problem description parser.
 */
@SuppressWarnings("all")
public class Parser implements OptionTypes, ParserConstants {

  /**
   * Constructs a parser object.
   * @param env the environment.
   * @param opt the options.
   */
  public Parser(Env env, Options opt) {
        this(env, opt, null);
  }

  /**
   * Constructs a parser object for a consequence finding problem description.
   * @param env the environment.
   * @param opt the options.   
   * @param cfp the consequence finding problem.
   */
  public Parser(Env env, Options opt, CFP cfp) {
        this(System.in);
        this.env = env;
        this.opt = opt;
        this.cfp = cfp;
        this.symTable = env.getSymTable();
        this.varNameMap = new VarNameMap();
        this.usedClauseDB = new HashMap<Clause,Clause>();
  }

  /**
   * Parse a consequence finding problem with the specified reader.
   * @param reader the reader.
   * @param source the name of source.
   * @param base   the base directory for the "include" directives. 
   */
  public void parse(Reader reader, String source, String base) throws ParseException {
    try {
      ReInit(reader);
      problem(base);
    } catch (ParseException e) {
      if (source != null)
        throw new ParseException(source + ": " + e.getMessage());
      else
        throw e;
    }
  }

  /**
   * Parse a consequence finding problem with the specified reader.
   * @param reader the reader.
   * @param source the name of source.
   */
  public void parse(Reader reader, String source) throws ParseException {
    parse(reader, source, null);
  }

  /**
   * Parse a consequence finding problem with the specified reader.
   * @param reader the reader.
   */
  public void parse(Reader reader) throws ParseException {
    parse(reader, null);
  }

  /**
   * Parse a literal with the specified reader.
   * @param reader the reader.
   * @return the literal.
   */
  public Literal literal(Reader reader) throws ParseException {
    ReInit(reader);
    return literal();
  }

  /**
   * Parse literals with the specified reader.
   * @param reader the reader.
   * @return the literals.
   */
  public List<Literal> literals(Reader reader) throws ParseException {
    ReInit(reader);
    return literals();
  }

  /**
   * Parse a term with the specified reader.
   * @param reader the reader.
   * @return the term.
   */
  public Term term(Reader reader) throws ParseException {
    ReInit(reader);
    return term().toTerm();
  }

  /**
   * Parse a productoin field literal with the specified reader.
   * @param reader the reader.
   * @return the production field literal.
   */
  public PLiteral pliteral(Reader reader) throws ParseException {
    ReInit(reader);
    return pliteral();
  }

  /**
   * Parse a productoin field with the specified reader.
   * @param reader the reader.
   * @return the production field.
   */
  public PField pfield(Reader reader) throws ParseException {
    ReInit(reader);
    return pfield();
  }

  /** Parses a problem description without the base directory specification. */
  public void problem() throws ParseException { problem(null); }

  /**
   * Parse a set of consequences.
   * @param reader the reader.
   * @return a set of consequences.
   */
  public List<Conseq> conseqs(Reader reader) throws ParseException {
    ReInit(reader);
    return conseqs();
  }

  /** The environment. */
  private Env env = null;
  /** The options. */
  private Options opt = null;
  /** The consequence finding problem. */
  private CFP cfp = null;
  /** The symbol table of the consequence finding problem. */
  private SymTable symTable = null;
  /** The mapping from variable names to variable numbers. */
  private VarNameMap varNameMap = null;
  /** The set of used clauses to generate consequences. */
  private HashMap<Clause,Clause> usedClauseDB = null;

  final public void problem(String base) throws ParseException {
  Clause clause = null;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INCLUDE:
      case CNF:
      case PRODUCTION_FIELD:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CNF:
        clause = cnf();
                     cfp.addClause(clause);
        break;
      case PRODUCTION_FIELD:
        pfield();
        break;
      case INCLUDE:
        include(base);
        break;
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    jj_consume_token(0);
  }

////////////////////////////////////////////////////////////////////////////////
// include directive
////////////////////////////////////////////////////////////////////////////////
  final public void include(String base) throws ParseException {
  Token  nameToken = null;
  String name      = null;
  String full      = null;
    jj_consume_token(INCLUDE);
    jj_consume_token(32);
    nameToken = jj_consume_token(FILENAME);
    jj_consume_token(33);
    jj_consume_token(PERIOD);
    try {
      // Removes "'" which surround the name.
      name = nameToken.image.substring(1, nameToken.image.length() - 1);

      // If the base directory is specified, then uses it.
      if (base == null)
        full = name;
      else
        full = base + File.separator + name;

      new Parser(env, opt, cfp).parse(new BufferedReader(new FileReader(full)), name);

    } catch (FileNotFoundException e) {
      {if (true) throw new ParseException("'" + full + "' is not found at line " +
                                token.beginLine + ", column " +  token.beginColumn + ".");}
    }
  }

////////////////////////////////////////////////////////////////////////////////
// production field directive
////////////////////////////////////////////////////////////////////////////////
  final public PField pfield() throws ParseException {
  PField pfield = (cfp != null) ? cfp.getPField() : new PField(env, opt);
  int    depth  = 0;
  int    length = 0;
    jj_consume_token(PRODUCTION_FIELD);
    jj_consume_token(32);
    
    boolean f = false;
    if(((jj_ntk==-1)?jj_ntk():jj_ntk) == 32){
    	f = true;
    	jj_consume_token(32);
    }
    while (f){
    	PField pf = new PField(env, opt);
    	jj_consume_token(34);
    	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    	case ALL:
    	case ALL_POS:
    	case ALL_NEG:
    	case CONSTANT:
    	case POS:
    	case NEG:
    	case POS_NEG:
    		pfieldConds(pf);
    		break;
    	default:
    		jj_la1[2] = jj_gen;
    		;
    	}
    	jj_consume_token(35);
    	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    	case 37:
    		depth = termDepth();
    		pfield.setMaxTermDepth(depth);
    		break;
    	default:
    		jj_la1[3] = jj_gen;
    		;
    	}
    	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    	
    	case 38:
    	case 39:
    		length = length();
    		pf.setMaxLength(length);
    		break;
    	default:
    		jj_la1[4] = jj_gen;
    		;
    	}
  
    	PFCardConstraint constraint = new PFCardConstraint(pf.getPLiterals(), pf.getMaxLength());
    	pfield.addConstraint(constraint);
    	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    	case 33: f = false;
    			 jj_consume_token(33);
    			 jj_consume_token(36);
    			 break;
    		
		default:
			jj_consume_token(36);
			;
		}
    	
    }
    
    
    
    jj_consume_token(34);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	case ALL:
	case ALL_POS:
	case ALL_NEG:
	case CONSTANT:
	case POS:
	case NEG:
	case POS_NEG:
		pfieldConds(pfield);
		break;
	default:
		jj_la1[2] = jj_gen;
		;
	}
    jj_consume_token(35);
	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
	case 37:
		depth = termDepth();
		pfield.setMaxTermDepth(depth);
		break;
	default:
		jj_la1[3] = jj_gen;
		;
	}
	switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 38:
	case 39:
		length = length();
		pfield.setMaxLength(length);
		break;
	default:
		jj_la1[4] = jj_gen;
		;
    }
	jj_consume_token(33);
    jj_consume_token(PERIOD);
                 {if (true) return pfield;}
    throw new Error("Missing return statement in function");
  }

  final public void pfieldConds(PField pfield) throws ParseException {
    pfieldCond(pfield);
    label_2:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_2;
      }
      jj_consume_token(36);
      pfieldCond(pfield);
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 36:
      jj_consume_token(36);
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
  }

  final public void pfieldCond(PField pfield) throws ParseException {
  PLiteral plit   = null;
  int      depth  = 0;
  int      length = 0;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ALL:
      jj_consume_token(ALL);
                plit = new PLiteral(PLiteral.BOTH);
      break;
    case ALL_POS:
      jj_consume_token(ALL_POS);
                plit = new PLiteral(PLiteral.POS);
      break;
    case ALL_NEG:
      jj_consume_token(ALL_NEG);
                plit = new PLiteral(PLiteral.NEG);
      break;
    case CONSTANT:
    case POS:
    case NEG:
    case POS_NEG:
      plit = pliteral();
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 37:
      depth = termDepth();
                           plit.setMaxTermDepth(depth);
      break;
    default:
      jj_la1[7] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 38:
    case 39:
      length = length();
                           plit.setMaxLength(length);
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    pfield.add(plit);
  }

  final public PLiteral pliteral() throws ParseException {
  TreeTerm pred = null;
  varNameMap.clear();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CONSTANT:
    case POS:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case POS:
        jj_consume_token(POS);
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
      }
      pred = predicate();
                                    {if (true) return new PLiteral(PLiteral.POS,  pred.toTerm());}
      break;
    case NEG:
      jj_consume_token(NEG);
      pred = predicate();
                                    {if (true) return new PLiteral(PLiteral.NEG,  pred.toTerm());}
      break;
    case POS_NEG:
      jj_consume_token(POS_NEG);
      pred = predicate();
                                    {if (true) return new PLiteral(PLiteral.BOTH, pred.toTerm());}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public int termDepth() throws ParseException {
  int depth = 0;
    jj_consume_token(37);
    depth = integer();
    if (depth <= 0)
      {if (true) throw new ParseException("Invalid value (" + depth + ") for term depth at line " +
                                token.beginLine + ", column " +  token.beginColumn + ".");}
    {if (true) return depth;}
    throw new Error("Missing return statement in function");
  }

  final public int length() throws ParseException {
  int length = 0;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 38:
      jj_consume_token(38);
      length = integer();
    if (length < 1)
      {if (true) throw new ParseException("Invalid value (" + length + ") for length at line " +
                                token.beginLine + ", column " +  token.beginColumn + ".");}
    {if (true) return length - 1;}
      break;
    case 39:
      jj_consume_token(39);
      length = integer();
    if (length < 0)
      {if (true) throw new ParseException("Invalid value (" + length + ") for length at line " +
                                token.beginLine + ", column " +  token.beginColumn + ".");}
    {if (true) return length;}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public int integer() throws ParseException {
    jj_consume_token(INTEGER);
    // Removes the plus sign.
    String num = token.image;
    if (num.charAt(0) == '+')
      num = num.substring(1);
    {if (true) return Integer.parseInt(num);}
    throw new Error("Missing return statement in function");
  }

////////////////////////////////////////////////////////////////////////////////
// cnf directive
////////////////////////////////////////////////////////////////////////////////
  final public Clause cnf() throws ParseException {
  String name = null;
  int    type = 0;
  Clause clause = null;
    jj_consume_token(CNF);
    jj_consume_token(32);
    name = clauseName();
    jj_consume_token(36);
    type = clauseType();
    jj_consume_token(36);
    clause = clause(name, type);
    jj_consume_token(33);
    jj_consume_token(PERIOD);
    {if (true) return clause;}
    throw new Error("Missing return statement in function");
  }

  final public String clauseName() throws ParseException {
    jj_consume_token(CONSTANT);
    {if (true) return token.image;}
    throw new Error("Missing return statement in function");
  }

  final public int clauseType() throws ParseException {
  String type = null;
    jj_consume_token(CONSTANT);
    type = token.image;
    if (type.equals("axiom"))
      {if (true) return ClauseTypes.AXIOM;}
    if (type.equals("hypothesis"))
      {if (true) return ClauseTypes.HYPOTHESIS;}
    if (type.equals("conjecture"))
      {if (true) return ClauseTypes.CONJECTURE;}
    if (type.equals("top_clause"))
      {if (true) return ClauseTypes.TOP_CLAUSE;}

    {if (true) throw new ParseException("Invalid type (" + type + ") of a clause at line " +
                              token.beginLine + ", column " +  token.beginColumn + ".");}
    throw new Error("Missing return statement in function");
  }

  final public Clause clause(String name, int type) throws ParseException {
  ArrayList<Literal> literals = null;
    literals = literals();
    {if (true) return new Clause(env, name, type, literals);}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<Literal> literals() throws ParseException {
  ArrayList<Literal> literals = new ArrayList<Literal>();
  Literal            literal  = null;
  varNameMap.clear();
    jj_consume_token(34);
    literal = literal();
                          literals.add(literal);
    label_3:
    while (true) {
      if (jj_2_2(2)) {
        ;
      } else {
        break label_3;
      }
      jj_consume_token(36);
      literal = literal();
                                             literals.add(literal);
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 36:
      jj_consume_token(36);
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
    jj_consume_token(35);
        {if (true) return literals;}
    throw new Error("Missing return statement in function");
  }

  final public Literal literal() throws ParseException {
  TreeTerm pred = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CONSTANT:
    case POS:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case POS:
        jj_consume_token(POS);
        break;
      default:
        jj_la1[13] = jj_gen;
        ;
      }
      pred = predicate();
                                  {if (true) return new Literal(env, true , pred.toTerm());}
      break;
    case NEG:
      jj_consume_token(NEG);
      pred = predicate();
                                  {if (true) return new Literal(env, false, pred.toTerm());}
      break;
    default:
      jj_la1[15] = jj_gen;
      if (jj_2_3(3)) {
        not();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case POS:
          jj_consume_token(POS);
          break;
        default:
          jj_la1[14] = jj_gen;
          ;
        }
        pred = predicate();
                                                        {if (true) return new Literal(env, true, true, pred.toTerm());}
      } else if (jj_2_4(3)) {
        not();
        jj_consume_token(NEG);
        pred = predicate();
                                                        {if (true) return new Literal(env, true, false, pred.toTerm());}
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public void not() throws ParseException {
    jj_consume_token(NOT);
    if (!opt.use(USE_NEGATION_AS_FAILURE))
      {if (true) throw new ParseException("\u005c"\u005c\u005c+\u005c" (negation as failure) is not supported at line " +
                                token.beginLine + ", column " +  token.beginColumn + ".");}
  }

  final public TreeTerm predicate() throws ParseException {
  Token          name = null;
  List<TreeTerm> args = null;
    name = jj_consume_token(CONSTANT);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 32:
      jj_consume_token(32);
      args = termArgs();
      jj_consume_token(33);
      break;
    default:
      jj_la1[16] = jj_gen;
      ;
    }
    if (args != null)
      {if (true) return TreeTerm.newPredicate(env, symTable.putPredicate(name.image, args.size()), args);}
    else
      {if (true) return TreeTerm.newPredicate(env, symTable.putPredicate(name.image, 0));}
    throw new Error("Missing return statement in function");
  }

  final public List<TreeTerm> termArgs() throws ParseException {
  TreeTerm       t    = null;
  List<TreeTerm> args = new ArrayList<TreeTerm>();
    t = term();
    args.add(t);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 36:
        ;
        break;
      default:
        jj_la1[17] = jj_gen;
        break label_4;
      }
      jj_consume_token(36);
      t = term();
                     args.add(t);
    }
    {if (true) return args;}
    throw new Error("Missing return statement in function");
  }

  final public TreeTerm term() throws ParseException {
  Token          name = null;
  List<TreeTerm> args = null;
  int            num = 0;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CONSTANT:
      name = jj_consume_token(CONSTANT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 32:
        jj_consume_token(32);
        args = termArgs();
        jj_consume_token(33);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
    if (args != null)
      {if (true) return TreeTerm.newFunction(env, symTable.putFunction(name.image, args.size()), args);}
    else
      {if (true) return TreeTerm.newConstant(env, symTable.putConstant(token.image));}
      break;
    case INTEGER:
      num = integer();
    {if (true) return TreeTerm.newInteger(env, num);}
      break;
    case VARIABLE:
      jj_consume_token(VARIABLE);
    {if (true) return TreeTerm.newVariable(env, varNameMap.put(token.image));}
      break;
    case ANONYMOUS:
      jj_consume_token(ANONYMOUS);
    {if (true) return TreeTerm.newVariable(env, varNameMap.put());}
      break;
    default:
      jj_la1[19] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

////////////////////////////////////////////////////////////////////////////////
// conseq directive
////////////////////////////////////////////////////////////////////////////////
  final public ArrayList<Conseq> conseqs() throws ParseException {
  ArrayList<Conseq> conseqs = new ArrayList<Conseq>();
  Conseq            conseq  = null;
    conseq = conseq();
                        conseqs.add(conseq);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CONSEQ:
        ;
        break;
      default:
        jj_la1[20] = jj_gen;
        break label_5;
      }
      conseq = conseq();
                        conseqs.add(conseq);
    }
    {if (true) return conseqs;}
    throw new Error("Missing return statement in function");
  }

  final public Conseq conseq() throws ParseException {
  ArrayList<Literal> literals    = null;
    jj_consume_token(CONSEQ);
    jj_consume_token(32);
    literals = literals();
    Conseq conseq = new Conseq(env, literals);
    conseqOp(conseq);
    jj_consume_token(33);
    jj_consume_token(PERIOD);
    {if (true) return conseq;}
    throw new Error("Missing return statement in function");
  }

  final public void conseqOp(Conseq conseq) throws ParseException {
  ArrayList<Clause> clauses = null;
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 36:
        ;
        break;
      default:
        jj_la1[21] = jj_gen;
        break label_6;
      }
      jj_consume_token(36);
      jj_consume_token(USED);
      jj_consume_token(32);
      clauses = clauses();
      jj_consume_token(33);
          ArrayList<Clause> usedClauses = new ArrayList<Clause>();
          for (Clause c : clauses) {
                Clause same = usedClauseDB.get(c);
                if (same != null) {
                  usedClauses.add(same);
                }
                else {
                  usedClauses.add(c);
          usedClauseDB.put(c, c);
                }
          }
          conseq.setUsedClauses(usedClauses);
    }
  }

  final public ArrayList<Clause> clauses() throws ParseException {
  ArrayList<Clause>  clauses  = new ArrayList<Clause>();
  ArrayList<Literal> literals = null;
    jj_consume_token(34);
    literals = literals();
                            clauses.add(new Clause(env, literals));
    label_7:
    while (true) {
      if (jj_2_5(2)) {
        ;
      } else {
        break label_7;
      }
      jj_consume_token(36);
      literals = literals();
                                               clauses.add(new Clause(env, literals));
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 36:
      jj_consume_token(36);
      break;
    default:
      jj_la1[22] = jj_gen;
      ;
    }
    jj_consume_token(35);
    {if (true) return clauses;}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_3R_8() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) {
    jj_scanpos = xsp;
    if (jj_3R_15()) {
    jj_scanpos = xsp;
    if (jj_3R_16()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(36)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(34)) return true;
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(CONSTANT)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_19()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_23() {
    if (jj_scan_token(POS_NEG)) return true;
    return false;
  }

  private boolean jj_3R_22() {
    if (jj_scan_token(NEG)) return true;
    return false;
  }

  private boolean jj_3R_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(27)) jj_scanpos = xsp;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_20() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(NOT)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(36)) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_3R_10()) return true;
    if (jj_scan_token(NEG)) return true;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(36)) return true;
    if (jj_3R_12()) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_3R_10()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(27)) jj_scanpos = xsp;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_18() {
    if (jj_scan_token(NEG)) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_3R_20()) return true;
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_scan_token(ALL_NEG)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) {
    jj_scanpos = xsp;
    if (jj_3_3()) {
    jj_scanpos = xsp;
    if (jj_3_4()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3R_17() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(27)) jj_scanpos = xsp;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(ALL_POS)) return true;
    return false;
  }

  private boolean jj_3R_19() {
    if (jj_scan_token(32)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(ALL)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[23];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xe00,0xe00,0x38027000,0x0,0x0,0x0,0x38027000,0x0,0x0,0x8000000,0x38020000,0x0,0x0,0x8000000,0x8000000,0x18020000,0x0,0x0,0x0,0x1e0000,0x8000,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x20,0xc0,0x10,0x0,0x20,0xc0,0x0,0x0,0xc0,0x10,0x0,0x0,0x0,0x1,0x10,0x1,0x0,0x0,0x10,0x10,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[5];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 23; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[40];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 23; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 40; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 5; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
