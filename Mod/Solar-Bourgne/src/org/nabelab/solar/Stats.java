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

import java.io.PrintStream;

import org.nabelab.util.Counter;

/**
 * Statistics Information of SOLAR.
 * @author nabesima
 */
public class Stats implements ExitStatus {
  
  public final static int CONSEQUENCES           = 1;
  public final static int CONSEQ_LITS            = 2;
  public final static int CLAUSES                = 3;
  public final static int LITERALS               = 4;
  public final static int ORG_CLAUSES            = 5;
  public final static int ORG_LITERALS           = 6;
  public final static int CLA_SUBSUMP_MIN        = 7;
  public final static int STAGE                  = 8;
  public final static int SKIP                   = 9;
  public final static int FACTORING              = 10;
  public final static int MERGE                  = 11;
  public final static int EXTENSION              = 12;
  public final static int EQ_RESOLUTION          = 13;
  public final static int SYMMETRY_SPLITTING     = 14;
  public final static int EQ_EXTENSION           = 15;
  public final static int NEG_EQ_FLATTENING      = 16;
  public final static int REDUCTION              = 17;
  public final static int IDENTICAL_REDUCTION    = 18;
  public final static int UNIT_AXIOM_MATCHING    = 19;
  public final static int UNIT_LEMMA_MATCHING    = 20;
  public final static int UNIT_LEMMA_EXTENSION   = 21;
  public final static int STRONG_CONTRACTION     = 22;
  public final static int IDENTICAL_C_REDUCTION  = 23;
  public final static int IDENTICAL_FOLDING_DOWN = 24;
  public final static int REGULARITY_GEN         = 25;
  public final static int REGULARITY_CHK         = 26;
  public final static int COMPLEMENT_FREE_GEN    = 27;
  public final static int COMPLEMENT_FREE_CHK    = 28;
  public final static int TAUTOLOGY_FREE_GEN     = 29;
  public final static int TAUTOLOGY_FREE_CHK     = 30;
  public final static int UNIT_SUBSUMPTION_GEN   = 31;
  public final static int UNIT_SUBSUMPTION_CHK   = 32;
  public final static int LOCAL_FAILURE_CACHE    = 33;
  public final static int SKIP_REGULARITY_TRY    = 34;
  public final static int SKIP_REGULARITY_GEN    = 35;
  public final static int SKIP_REGULARITY_CHK    = 36;
  public final static int SKIP_MINIMALITY        = 37;
  public final static int SKIP_MIN_SUBSUMP_CHK   = 38;
  public final static int EQ_CONSTRAINT_GEN      = 39;
  public final static int EQ_CONSTRAINT_CHK      = 40;
  public final static int NEGATION_AS_FAILURE    = 41;
  public final static int FAIL                   = 42;
  public final static int MAX_CONSTRAINTS        = 43;

  public final static int NUM_STATS_TYPES        = 44;
  
  /**
   * Constructs statistics information.
   */
  public Stats() {
    this.inf = new Counter();
    this.prods = new long[NUM_STATS_TYPES];
    this.succs = new long[NUM_STATS_TYPES];
    this.tests = new long[NUM_STATS_TYPES];
  }

  /**
   * Increase the value of the producing counter of the specified type.
   * @param type the specified type.
   * @return the value of the producing counter of the specified type.
   */
  public long incProds(int type) {
    return ++prods[type];
  }
  
  /**
   * Increase the value of the producing counter of the specified type.
   * @param type the specified type.
   * @param val  the value to be added
   * @return the value of the producing counter of the specified type.
   */
  public long incProds(int type, int val) {
    return prods[type] += val;
  }

  /**
   * Increase the value of the succeeding counter of the specified type.
   * @param type the specified type.
   * @return the value of the succeeding counter of the specified type.
   */
  public long incSuccs(int type) {
    return ++succs[type];
  }
  
  /**
   * Increase the value of the testing counter of the specified type.
   * @param type the specified type.
   * @return the value of the testing counter of the specified type.
   */
  public long incTests(int type) {
    return ++tests[type];
  }
  
  /**
   * Returns the value of the producing counter of the specified type.
   * @param type the specified type.
   * @return the value of the producing counter of the specified type.
   */
  public long getProds(int type) {
    return prods[type];
  }
  
  /**
   * Returns the value of the succeeding counter of the specified type.
   * @param type the specified type.
   * @return the value of the succeeding counter of the specified type.
   */
  public long getSuccs(int type) {
    return succs[type];
  }
  
  /**
   * Returns the value of the testing counter of the specified type.
   * @param type the specified type.
   * @return the value of the testing counter of the specified type.
   */
  public long getTests(int type) {
    return tests[type];
  }
  
  /**
   * Sets the value of the producing counter of the specified statistic type.
   * @param type the specified type.
   * @param val  the value of the specified type.
   */
  public void setProds(int type, long val) {
    prods[type] = val;
  }
  
  /**
   * Sets the value of the succeeding counter of the specified statistic type.
   * @param type the specified type.
   * @param val  the value of the specified type.
   */
  public void setSuccs(int type, long val) {
    succs[type] = val;
  }
  
  /**
   * Sets the value of the testing counter of the specified statistic type.
   * @param type the specified type.
   * @param val  the value of the specified type.
   */
  public void setTests(int type, long val) {
    tests[type] = val;
  }
  
  /**
   * Increases the value of the succeeding counter of the specified type (same as incSucc(type)).
   * @param type the specified type.
   */
  public void inc(int type) {
    succs[type]++;
  }
  
  /**
   * Returns the value of the succeeding counter of the specified type (same as getSucc(type)).
   * @param type the specified type.
   * @return the value of the succeeding counter of the specified type.
   */
  public long get(int type) {
    return succs[type];
  }
  
  
  /**
   * Returns the number of inferences.
   * @return the number of inferences.
   */
  public long inf() {
    return inf.value();
  }

  /**
   * Increases the number of inferences.
   */
  public void incInf() {
    inf.inc();
  }

  /**
   * Returns the counter of inferences.
   * @return the counter of inferences.
   */
  public Counter getInfCounter() {
    return inf;
  }
  
  /**
   * Sets the search depth of tableaux.
   * @param depth the search depth.
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }
  
  /**
   * Prints statistics information to the specified stream.
   * @param out    the specified stream.
   * @param cfp    the consequence finding problem.
   * @param millis the elapsed time in milliseconds.
   * @param csv    if true, then outputs the information in the csv format.
   */
  public void print(PrintStream out, CFP cfp, long millis, boolean csv) {
    double sec = millis / 1000.0;
    out.printf("inferences   : %-8d (%.0f/s, dep %d)\n", inf.value(), inf.value() / sec, depth);
    out.printf("consequences : %-8d (%.2fl/c)\n", prods[CONSEQUENCES], (double)prods[CONSEQ_LITS] / prods[CONSEQUENCES]);
    out.printf("clauses      : %-8d (%.2fl/c; org %d clauses, %.2fl/c)\n", prods[CLAUSES], (double)prods[LITERALS] / prods[CLAUSES], prods[ORG_CLAUSES], (double)prods[ORG_LITERALS] / prods[ORG_CLAUSES]);
    out.printf("cla sub min  : %-7.2f%% (%d/%dchk)\n", 100.0 * succs[CLA_SUBSUMP_MIN] / tests[CLA_SUBSUMP_MIN], succs[CLA_SUBSUMP_MIN], tests[CLA_SUBSUMP_MIN]);
    out.printf("skips        : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[SKIP                 ], prods[SKIP                 ], 100.0 * succs[SKIP                 ] / prods[SKIP                 ], 100.0 * prods[SKIP                 ] / tests[SKIP                 ], tests[SKIP                 ]);
    out.printf("factorings   : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[FACTORING            ], prods[FACTORING            ], 100.0 * succs[FACTORING            ] / prods[FACTORING            ], 100.0 * prods[FACTORING            ] / tests[FACTORING            ], tests[FACTORING            ]);
    out.printf("merge        : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[MERGE                ], prods[MERGE                ], 100.0 * succs[MERGE                ] / prods[MERGE                ], 100.0 * prods[MERGE                ] / tests[MERGE                ], tests[MERGE                ]);
    out.printf("extensions   : %-8d (%dops, %.2f%%usd, %dscn)\n",            succs[EXTENSION            ], prods[EXTENSION            ], 100.0 * succs[EXTENSION            ] / prods[EXTENSION            ], tests[EXTENSION            ]);
    if (cfp.getOptions().getEqType() != CFP.EQ_AXIOMS_REQUIRED) {
    out.printf("eq resolves  : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[EQ_RESOLUTION        ], prods[EQ_RESOLUTION        ], 100.0 * succs[EQ_RESOLUTION        ] / prods[EQ_RESOLUTION        ], 100.0 * prods[EQ_RESOLUTION        ] / tests[EQ_RESOLUTION        ], tests[EQ_RESOLUTION        ]);                     
    out.printf("sym splits   : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[SYMMETRY_SPLITTING   ], prods[SYMMETRY_SPLITTING   ], 100.0 * succs[SYMMETRY_SPLITTING   ] / prods[SYMMETRY_SPLITTING   ], 100.0 * prods[SYMMETRY_SPLITTING   ] / tests[SYMMETRY_SPLITTING   ], tests[SYMMETRY_SPLITTING   ]);                     
    out.printf("eq exts      : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[EQ_EXTENSION         ], prods[EQ_EXTENSION         ], 100.0 * succs[EQ_EXTENSION         ] / prods[EQ_EXTENSION         ], 100.0 * prods[EQ_EXTENSION         ] / tests[EQ_EXTENSION         ], tests[EQ_EXTENSION         ]);                     
    out.printf("neg eq flats : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[NEG_EQ_FLATTENING    ], prods[NEG_EQ_FLATTENING    ], 100.0 * succs[NEG_EQ_FLATTENING    ] / prods[NEG_EQ_FLATTENING    ], 100.0 * prods[NEG_EQ_FLATTENING    ] / tests[NEG_EQ_FLATTENING    ], tests[NEG_EQ_FLATTENING    ]);
    }
    out.printf("reductions   : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[REDUCTION            ], prods[REDUCTION            ], 100.0 * succs[REDUCTION            ] / prods[REDUCTION            ], 100.0 * prods[REDUCTION            ] / tests[REDUCTION            ], tests[REDUCTION            ]);
    out.printf("id reducts   : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[IDENTICAL_REDUCTION  ], prods[IDENTICAL_REDUCTION  ], 100.0 * succs[IDENTICAL_REDUCTION  ] / prods[IDENTICAL_REDUCTION  ], 100.0 * prods[IDENTICAL_REDUCTION  ] / tests[IDENTICAL_REDUCTION  ], tests[IDENTICAL_REDUCTION  ]);
    out.printf("uaxiom match : %-8d (%daxm, %.2f%%suc, %dtst)\n", succs[UNIT_AXIOM_MATCHING  ], prods[UNIT_AXIOM_MATCHING  ], 100.0 * succs[UNIT_AXIOM_MATCHING  ] / tests[UNIT_AXIOM_MATCHING  ], tests[UNIT_AXIOM_MATCHING  ]); 
    out.printf("ulemma match : %-8d (%dLmm, %.2f%%suc, %dtst)\n", succs[UNIT_LEMMA_MATCHING  ], prods[UNIT_LEMMA_MATCHING  ], 100.0 * succs[UNIT_LEMMA_MATCHING  ] / tests[UNIT_LEMMA_MATCHING  ], tests[UNIT_LEMMA_MATCHING  ]);
    out.printf("strong cont  : %-8d (%dops, %.2f%%suc, %dtst)\n", succs[STRONG_CONTRACTION   ], prods[STRONG_CONTRACTION   ], 100.0 * succs[STRONG_CONTRACTION   ] / tests[STRONG_CONTRACTION   ], tests[STRONG_CONTRACTION   ]);
    out.printf("id c-reducts : %-8d (%dLmm, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[IDENTICAL_C_REDUCTION], prods[IDENTICAL_C_REDUCTION], 100.0 * succs[IDENTICAL_C_REDUCTION] / prods[IDENTICAL_C_REDUCTION], 100.0 * prods[IDENTICAL_C_REDUCTION] / tests[IDENTICAL_C_REDUCTION], tests[IDENTICAL_C_REDUCTION]);
    out.printf("id flding-dwn: %-8d (%dLmm, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[IDENTICAL_FOLDING_DOWN], prods[IDENTICAL_FOLDING_DOWN], 100.0 * succs[IDENTICAL_FOLDING_DOWN] / prods[IDENTICAL_FOLDING_DOWN], 100.0 * prods[IDENTICAL_FOLDING_DOWN] / tests[IDENTICAL_FOLDING_DOWN], tests[IDENTICAL_FOLDING_DOWN]);
    out.printf("regularities : %-8d (%dimm, %dcns, %.2f%%gen, %dtst; %.2f%%suc, %dchk)\n", succs[REGULARITY_GEN       ] + succs[REGULARITY_CHK       ], succs[REGULARITY_GEN       ], prods[REGULARITY_GEN       ], 100.0 * prods[REGULARITY_GEN       ] / tests[REGULARITY_GEN       ], tests[REGULARITY_GEN       ], 100.0 * succs[REGULARITY_CHK      ] / tests[REGULARITY_CHK      ], tests[REGULARITY_CHK      ]);    
    out.printf("comp frees   : %-8d (%dimm, %dcns, %.2f%%gen, %dtst; %.2f%%suc, %dchk)\n", succs[COMPLEMENT_FREE_GEN  ] + succs[COMPLEMENT_FREE_CHK  ], succs[COMPLEMENT_FREE_GEN  ], prods[COMPLEMENT_FREE_GEN  ], 100.0 * prods[COMPLEMENT_FREE_GEN  ] / tests[COMPLEMENT_FREE_GEN  ], tests[COMPLEMENT_FREE_GEN  ], 100.0 * succs[COMPLEMENT_FREE_CHK ] / tests[COMPLEMENT_FREE_CHK ], tests[COMPLEMENT_FREE_CHK ]);    
    out.printf("taut frees   : %-8d (%dimm, %dcns, %.2f%%gen, %dtst; %.2f%%suc, %dchk)\n", succs[TAUTOLOGY_FREE_GEN   ] + succs[TAUTOLOGY_FREE_CHK   ], succs[TAUTOLOGY_FREE_GEN   ], prods[TAUTOLOGY_FREE_GEN   ], 100.0 * prods[TAUTOLOGY_FREE_GEN   ] / tests[TAUTOLOGY_FREE_GEN   ], tests[TAUTOLOGY_FREE_GEN   ], 100.0 * succs[TAUTOLOGY_FREE_CHK  ] / tests[TAUTOLOGY_FREE_CHK  ], tests[TAUTOLOGY_FREE_CHK  ]);
    out.printf("unit subsump : %-8d (%dimm, %dcns, %.2f%%gen, %dtst; %.2f%%suc, %dchk)\n", succs[UNIT_SUBSUMPTION_GEN ] + succs[UNIT_SUBSUMPTION_CHK ], succs[UNIT_SUBSUMPTION_GEN ], prods[UNIT_SUBSUMPTION_GEN ], 100.0 * prods[UNIT_SUBSUMPTION_GEN ] / tests[UNIT_SUBSUMPTION_GEN ], tests[UNIT_SUBSUMPTION_GEN ], 100.0 * succs[UNIT_SUBSUMPTION_CHK] / tests[UNIT_SUBSUMPTION_CHK], tests[UNIT_SUBSUMPTION_CHK]);
    if (cfp.getOptions().getEqType() != CFP.EQ_AXIOMS_REQUIRED) 
    out.printf("eq exts      : %-8d (%dops, %.2f%%usd, %.2f%%gen, %dtst)\n", succs[EQ_EXTENSION         ], prods[EQ_EXTENSION         ], 100.0 * succs[EQ_EXTENSION         ] / prods[EQ_EXTENSION         ], 100.0 * prods[EQ_EXTENSION         ] / tests[EQ_EXTENSION         ], tests[EQ_EXTENSION         ]);                     
    out.printf("skip-reg     : %-8d (%dimm, %dcns, %.2f%%gen, %dtst; %.2f%%suc, %dchk)\n", succs[SKIP_REGULARITY_GEN  ] + succs[SKIP_REGULARITY_CHK  ], succs[SKIP_REGULARITY_GEN  ], prods[SKIP_REGULARITY_GEN  ], 100.0 * prods[SKIP_REGULARITY_GEN  ] / tests[SKIP_REGULARITY_GEN  ], tests[SKIP_REGULARITY_GEN  ], 100.0 * succs[SKIP_REGULARITY_CHK ] / tests[SKIP_REGULARITY_CHK ], tests[SKIP_REGULARITY_CHK ]);
    out.printf("skip-min     : %-8d (%.2f%%suc, %dtst; %.2f%%, %d/%dchk)\n", succs[SKIP_MINIMALITY      ], 100.0 * succs[SKIP_MINIMALITY      ] / tests[SKIP_MINIMALITY      ], tests[SKIP_MINIMALITY      ], 100.0 * succs[SKIP_MIN_SUBSUMP_CHK ] / tests[SKIP_MIN_SUBSUMP_CHK ], succs[SKIP_MIN_SUBSUMP_CHK ], tests[SKIP_MIN_SUBSUMP_CHK]);
    out.printf("lfc hits     : %-8d (%dfails, %.2f%%succ, %dtst)\n", succs[LOCAL_FAILURE_CACHE  ], prods[LOCAL_FAILURE_CACHE  ], 100.0 * succs[LOCAL_FAILURE_CACHE  ] / tests[LOCAL_FAILURE_CACHE  ], tests[LOCAL_FAILURE_CACHE  ]);
    if (cfp.getOptions().getEqType() != CFP.EQ_AXIOMS_REQUIRED)
    out.printf("eq consts    : %-8d (%dimm, %dcns, %.2f%%gen, %dtst; %.2f%%suc, %dchk)\n", succs[EQ_CONSTRAINT_GEN    ] + succs[EQ_CONSTRAINT_CHK    ], succs[EQ_CONSTRAINT_GEN    ], prods[EQ_CONSTRAINT_GEN    ], 100.0 * prods[EQ_CONSTRAINT_GEN    ] / tests[EQ_CONSTRAINT_GEN    ], tests[EQ_CONSTRAINT_GEN    ], 100.0 * succs[EQ_CONSTRAINT_CHK   ] / tests[EQ_CONSTRAINT_CHK   ], tests[EQ_CONSTRAINT_CHK   ]);
    out.printf("fails        : %-8d\n", succs[FAIL]);
    out.printf("max consts   : %-8d\n", prods[MAX_CONSTRAINTS]);
    out.printf("CPU time     : %.2fs\n", sec);
    
    if (!csv) return;
    
    String hdr = "hdr,";
    String val = "csv,";
    
    hdr += "problem,";
    val += cfp.getName() + ",";

    hdr += "status,";
    switch (cfp.getStatus()) {
    case UNSATISFIABLE:          val += "unsat,";        break;
    case SATISFIABLE:            val += "sat,";          break;
    case TRIVIALLY_SATISFIABLE:  val += "trivial sat,";  break;
    case UNKNOWN:                val += "unknown,";      break;
    }
    
    hdr += "inferences,";
    val += inf.value() + ",";
    hdr += "inference speed,";
    val += (inf.value() / sec) + ",";
    
    hdr += "depth,";
    val += depth + ",";
    
    hdr += "consequences,";
    val += prods[CONSEQUENCES] + ",";
    hdr += "literals/consequences,";
    val += (double)prods[CONSEQ_LITS] / prods[CONSEQUENCES] + ",";
    
    hdr += "clauses,";
    val += prods[CLAUSES] + ",";
    hdr += "literals,";
    val += prods[LITERALS] + ",";
    hdr += "literals/clause,";
    val += (double)prods[LITERALS] / prods[CLAUSES] + ",";
    hdr += "org clauses,";
    val += prods[ORG_CLAUSES] + ",";
    hdr += "org literals,";
    val += prods[ORG_LITERALS] + ",";
    hdr += "org literals/clause,";
    val += (double)prods[ORG_LITERALS] / prods[ORG_CLAUSES] + ",";
    
    hdr += "clause subsump min succ rate,";
    val += 100.0 * succs[CLA_SUBSUMP_MIN] / tests[CLA_SUBSUMP_MIN] + ",";
    hdr += "clause subsump-min checking number,"; 
    val += succs[CLA_SUBSUMP_MIN] + ",";
    hdr += "clause subsump-min checking number without filtering,"; 
    val += tests[CLA_SUBSUMP_MIN] + ",";
    
    hdr += "skips,";
    val += succs[SKIP] + ",";
    hdr += "skip op objects,";
    val += prods[SKIP] + ",";
    hdr += "skip op used rate,";
    val += 100.0 * succs[SKIP] / prods[SKIP] + ",";
    hdr += "skip op generation rate,";
    val += 100.0 * prods[SKIP] / tests[SKIP] + ",";
    hdr += "skip tests,";
    val += tests[SKIP] + ",";
    
    hdr += "factorings,";
    val += succs[FACTORING] + ",";
    hdr += "factoring op objects,";
    val += prods[FACTORING] + ",";
    hdr += "factoring op used rate,";
    val += 100.0 * succs[FACTORING] / prods[FACTORING] + ",";
    hdr += "factoring op generation rate,";
    val += 100.0 * prods[FACTORING] / tests[FACTORING] + ",";
    hdr += "factoring tests,";
    val += tests[FACTORING] + ",";
    
    hdr += "merge,";
    val += succs[MERGE] + ",";
    hdr += "merge op objects,";
    val += prods[MERGE] + ",";
    hdr += "merge op used rate,";
    val += 100.0 * succs[MERGE] / prods[MERGE] + ",";
    hdr += "merge op generation rate,";
    val += 100.0 * prods[MERGE] / tests[MERGE] + ",";
    hdr += "merge tests,";
    val += tests[MERGE] + ",";
    
    hdr += "extensions,";
    val += succs[EXTENSION] + ",";
    hdr += "extension op objects,";
    val += prods[EXTENSION] + ",";
    hdr += "extension op used rate,";
    val += 100.0 * succs[EXTENSION] / prods[EXTENSION] + ",";
    hdr += "extension scans,";
    val += tests[EXTENSION] + ",";

    hdr += "equality resolutions,";
    val += succs[EQ_RESOLUTION] + ",";
    hdr += "equality resolution op objects,";
    val += prods[EQ_RESOLUTION] + ",";
    hdr += "equality resolution op used rate,";
    val += 100.0 * succs[EQ_RESOLUTION] / prods[EQ_RESOLUTION] + ",";
    hdr += "equality resolution op generation rate,";
    val += 100.0 * prods[EQ_RESOLUTION] / tests[EQ_RESOLUTION] + ",";
    hdr += "equality resolution tests,";
    val += tests[EQ_RESOLUTION] + ",";   
    
    hdr += "symmetry splittings,";
    val += succs[SYMMETRY_SPLITTING] + ",";
    hdr += "symmetry splitting op objects,";
    val += prods[SYMMETRY_SPLITTING] + ",";
    hdr += "symmetry splitting op used rate,";
    val += 100.0 * succs[SYMMETRY_SPLITTING] / prods[SYMMETRY_SPLITTING] + ",";
    hdr += "symmetry splitting op generation rate,";
    val += 100.0 * prods[SYMMETRY_SPLITTING] / tests[SYMMETRY_SPLITTING] + ",";
    hdr += "symmetry splitting tests,";
    val += tests[SYMMETRY_SPLITTING] + ",";   
    
    hdr += "equality extensions,";
    val += succs[EQ_EXTENSION] + ",";
    hdr += "equality extention op objects,";
    val += prods[EQ_EXTENSION] + ",";
    hdr += "equality extention op used rate,";
    val += 100.0 * succs[EQ_EXTENSION] / prods[EQ_EXTENSION] + ",";
    hdr += "equality extention op generation rate,";
    val += 100.0 * prods[EQ_EXTENSION] / tests[EQ_EXTENSION] + ",";
    hdr += "equality extention tests,";
    val += tests[EQ_EXTENSION] + ",";   
    
    hdr += "negative equality flattening,";
    val += succs[NEG_EQ_FLATTENING] + ",";
    hdr += "negative equality flattening op objects,";
    val += prods[NEG_EQ_FLATTENING] + ",";
    hdr += "negative equality flattening op used rate,";
    val += 100.0 * succs[NEG_EQ_FLATTENING] / prods[NEG_EQ_FLATTENING] + ",";
    hdr += "negative equality flattening op generation rate,";
    val += 100.0 * prods[NEG_EQ_FLATTENING] / tests[NEG_EQ_FLATTENING] + ",";
    hdr += "negative equality flattening tests,";
    val += tests[NEG_EQ_FLATTENING] + ",";   
    
    hdr += "reduction,";
    val += succs[REDUCTION] + ",";
    hdr += "reduction op objects,";
    val += prods[REDUCTION] + ",";
    hdr += "reduction op used rate,";
    val += 100.0 * succs[REDUCTION] / prods[REDUCTION] + ",";
    hdr += "reduction op generation rate,";
    val += 100.0 * prods[REDUCTION] / tests[REDUCTION] + ",";
    hdr += "reduction tests,";
    val += tests[REDUCTION] + ",";

    hdr += "identical reduction,";
    val += succs[IDENTICAL_REDUCTION] + ",";
    hdr += "identical reduction op objects,";
    val += prods[IDENTICAL_REDUCTION] + ",";
    hdr += "identical reduction op used rate,";
    val += 100.0 * succs[IDENTICAL_REDUCTION] / prods[IDENTICAL_REDUCTION] + ",";
    hdr += "identical reduction op generation rate,";
    val += 100.0 * prods[IDENTICAL_REDUCTION] / tests[IDENTICAL_REDUCTION] + ",";
    hdr += "identical reduction tests,";
    val += tests[IDENTICAL_REDUCTION] + ",";
    
    hdr += "unit axiom matchings,";
    val += succs[UNIT_AXIOM_MATCHING] + ",";
    hdr += "unit axioms,";
    val += prods[UNIT_AXIOM_MATCHING] + ",";
    hdr += "unit axiom matching succ rate,";
    val += 100.0 * succs[UNIT_AXIOM_MATCHING] / tests[UNIT_AXIOM_MATCHING] + ",";
    hdr += "unit axiom matching tests,";
    val += tests[UNIT_AXIOM_MATCHING] + ",";

    hdr += "unit lemma matchings,";
    val += succs[UNIT_LEMMA_MATCHING] + ",";
    hdr += "unit lemmas,";
    val += prods[UNIT_LEMMA_MATCHING] + ",";
    hdr += "unit lemma matching succ rate,";
    val += 100.0 * succs[UNIT_LEMMA_MATCHING] / tests[UNIT_LEMMA_MATCHING] + ",";
    hdr += "unit lemma matching tests,";
    val += tests[UNIT_LEMMA_MATCHING] + ",";

    hdr += "strong contractions,";
    val += succs[STRONG_CONTRACTION] + ",";
    hdr += "strong contraction op objects,";
    val += prods[STRONG_CONTRACTION] + ",";
    hdr += "strong contraction succ rate,";
    val += 100.0 * succs[STRONG_CONTRACTION] / tests[STRONG_CONTRACTION] + ",";
    hdr += "strong contraction tests,";
    val += tests[STRONG_CONTRACTION] + ",";

    hdr += "identical c-reduction,";
    val += succs[IDENTICAL_C_REDUCTION] + ",";
    hdr += "identical c-reduction lemmas,";
    val += prods[IDENTICAL_C_REDUCTION] + ",";
    hdr += "identical c-reduction op used rate,";
    val += 100.0 * succs[IDENTICAL_C_REDUCTION] / prods[IDENTICAL_C_REDUCTION] + ",";
    hdr += "identical c-reduction op generation rate,";
    val += 100.0 * prods[IDENTICAL_C_REDUCTION] / tests[IDENTICAL_C_REDUCTION] + ",";
    hdr += "identical c-reduction tests,";
    val += tests[IDENTICAL_C_REDUCTION] + ",";
    
    hdr += "identical folding-down,";
    val += succs[IDENTICAL_FOLDING_DOWN] + ",";
    hdr += "identical folding-down lemmas,";
    val += prods[IDENTICAL_FOLDING_DOWN] + ",";
    hdr += "identical folding-down op used rate,";
    val += 100.0 * succs[IDENTICAL_FOLDING_DOWN] / prods[IDENTICAL_FOLDING_DOWN] + ",";
    hdr += "identical folding-down op generation rate,";
    val += 100.0 * prods[IDENTICAL_FOLDING_DOWN] / tests[IDENTICAL_FOLDING_DOWN] + ",";
    hdr += "identical folding-down tests,";
    val += tests[IDENTICAL_FOLDING_DOWN] + ",";
    
    hdr += "regularities,";
    val += succs[REGULARITY_CHK] + ",";
    hdr += "regularity constraints,";
    val += prods[REGULARITY_GEN] + ",";
    hdr += "regularity constraint generation rate,";
    val += 100.0 * prods[REGULARITY_GEN] / tests[REGULARITY_GEN] + ",";
    hdr += "regularity constraint generation tests,";
    val += tests[REGULARITY_GEN] + ",";
    hdr += "regularity constraint succ rate,";
    val += 100.0 * succs[REGULARITY_CHK] / tests[REGULARITY_CHK] + ",";
    hdr += "regularity constraint checks,";
    val += tests[REGULARITY_CHK] + ",";
    
    hdr += "complement frees,";
    val += (succs[COMPLEMENT_FREE_GEN] + succs[COMPLEMENT_FREE_CHK]) + ",";
    hdr += "complement free immediate succs,";
    val += succs[COMPLEMENT_FREE_GEN] + ",";
    hdr += "complement free constraints,";
    val += prods[COMPLEMENT_FREE_GEN] + ",";
    hdr += "complement free constraint generation rate,";
    val += 100.0 * prods[COMPLEMENT_FREE_GEN] / tests[COMPLEMENT_FREE_GEN] + ",";
    hdr += "complement free constraint generation tests,";
    val += tests[COMPLEMENT_FREE_GEN] + ",";
    hdr += "complement free constraint succ rate,";
    val += 100.0 * succs[COMPLEMENT_FREE_CHK] / tests[COMPLEMENT_FREE_CHK] + ",";
    hdr += "complement free constraint checks,";
    val += tests[COMPLEMENT_FREE_CHK] + ",";

    hdr += "tautology frees,";
    val += (succs[TAUTOLOGY_FREE_GEN] + succs[TAUTOLOGY_FREE_CHK]) + ",";
    hdr += "tautology free immediate succs,";
    val += succs[TAUTOLOGY_FREE_GEN] + ",";
    hdr += "tautology free constraints,";
    val += prods[TAUTOLOGY_FREE_GEN] + ",";
    hdr += "tautology free constraint generation rate,";
    val += 100.0 * prods[TAUTOLOGY_FREE_GEN] / tests[TAUTOLOGY_FREE_GEN] + ",";
    hdr += "tautology free constraint generation tests,";
    val += tests[TAUTOLOGY_FREE_GEN] + ",";
    hdr += "tautology free constraint succ rate,";
    val += 100.0 * succs[TAUTOLOGY_FREE_CHK] / tests[TAUTOLOGY_FREE_CHK] + ",";
    hdr += "tautology free constraint checks,";
    val += tests[TAUTOLOGY_FREE_CHK] + ",";

    hdr += "unit subsumptions,";
    val += (succs[UNIT_SUBSUMPTION_GEN] + succs[UNIT_SUBSUMPTION_CHK]) + ",";
    hdr += "unit subsumption immediate succs,";
    val += succs[UNIT_SUBSUMPTION_GEN] + ",";
    hdr += "unit subsumption constraints,";
    val += prods[UNIT_SUBSUMPTION_GEN] + ",";
    hdr += "unit subsumption constraint generation rate,";
    val += 100.0 * prods[UNIT_SUBSUMPTION_GEN] / tests[UNIT_SUBSUMPTION_GEN] + ",";
    hdr += "unit subsumption constraint generation tests,";
    val += tests[UNIT_SUBSUMPTION_GEN] + ",";
    hdr += "unit subsumption constraint succ rate,";
    val += 100.0 * succs[UNIT_SUBSUMPTION_CHK] / tests[UNIT_SUBSUMPTION_CHK] + ",";
    hdr += "unit subsumption constraint checks,";
    val += tests[UNIT_SUBSUMPTION_CHK] + ",";
    
    hdr += "skip regularities,";
    val += (succs[SKIP_REGULARITY_GEN] + succs[SKIP_REGULARITY_CHK]) + ",";
    hdr += "skip regularity immediate succs,";
    val += succs[SKIP_REGULARITY_GEN] + ",";
    hdr += "skip regularity constraints,";
    val += prods[SKIP_REGULARITY_GEN] + ",";
    hdr += "skip regularity constraint generation rate,";
    val += 100.0 * prods[SKIP_REGULARITY_GEN] / tests[SKIP_REGULARITY_GEN] + ",";
    hdr += "skip regularity constraint generation tests,";
    val += tests[SKIP_REGULARITY_GEN] + ",";
    hdr += "skip regularity constraint succ rate,";
    val += 100.0 * succs[SKIP_REGULARITY_CHK] / tests[SKIP_REGULARITY_CHK] + ",";
    hdr += "skip regularity constraint checks,";
    val += tests[SKIP_REGULARITY_CHK] + ",";
    
    hdr += "skip minimalities,";
    val += succs[SKIP_MINIMALITY] + ",";
    hdr += "skip minimality succ rate,";
    val += 100.0 * succs[SKIP_MINIMALITY] / tests[SKIP_MINIMALITY] + ",";
    hdr += "skip minimality tests,";
    val += tests[SKIP_MINIMALITY] + ",";
    hdr += "skip minimality subsumption check succ rate,";
    val += 100.0 * succs[SKIP_MIN_SUBSUMP_CHK] / tests[SKIP_MIN_SUBSUMP_CHK] + ",";
    hdr += "skip minimality subsumption check number,"; 
    val += succs[SKIP_MIN_SUBSUMP_CHK] + ",";
    hdr += "skip minimality subsumption check number without filtering,"; 
    val += tests[SKIP_MIN_SUBSUMP_CHK] + ",";
    
    hdr += "local failure cache hits,";
    val += succs[LOCAL_FAILURE_CACHE] + ",";
    hdr += "local failures,";
    val += prods[LOCAL_FAILURE_CACHE] + ",";
    hdr += "local failure cache hit rate,";
    val += 100.0 * succs[LOCAL_FAILURE_CACHE] / tests[LOCAL_FAILURE_CACHE] + ",";
    hdr += "local failure cache hit tests,";
    val += tests[LOCAL_FAILURE_CACHE] + ",";
    
    hdr += "equality constraints succs,";
    val += (succs[EQ_CONSTRAINT_GEN] + succs[EQ_CONSTRAINT_CHK]) + ",";
    hdr += "equality constraints immediate succs,";
    val += succs[EQ_CONSTRAINT_GEN] + ",";
    hdr += "equality constraints,";
    val += prods[EQ_CONSTRAINT_GEN] + ",";
    hdr += "equality constraints generation rate,";
    val += 100.0 * prods[EQ_CONSTRAINT_GEN] / tests[EQ_CONSTRAINT_GEN] + ",";
    hdr += "equality constraints generation tests,";
    val += tests[EQ_CONSTRAINT_GEN] + ",";
    hdr += "equality constraints succ rate,";
    val += 100.0 * succs[EQ_CONSTRAINT_CHK] / tests[EQ_CONSTRAINT_CHK] + ",";
    hdr += "equality constraints checks,";
    val += tests[EQ_CONSTRAINT_CHK] + ",";
    
    hdr += "failures,";
    val += succs[FAIL] + ",";
    
    hdr += "CPU time,";
    val += sec + ",";

    out.println();
    out.println(hdr);
    out.println(val);
  }
  
  /** The number of inferences. */
  private Counter inf = null;
  /** The frequency array of products. */
  private long[] prods = null;
  /** The frequency array of tests. */
  private long[] tests = null;
  /** The frequency array of success. */
  private long[] succs = null;
  /** The search depth of tableaux. */
  private int depth = 0;

}
