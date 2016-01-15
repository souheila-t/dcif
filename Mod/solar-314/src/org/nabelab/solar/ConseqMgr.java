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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.nabelab.mhs.HitSet;
import org.nabelab.mhs.MHSFinder;
import org.nabelab.solar.indexing.FVecTrie;
import org.nabelab.solar.parser.ParseException;
import org.nabelab.solar.parser.Parser;
import org.nabelab.util.IntArraySet;
import org.nabelab.util.IntIterator;
import org.nabelab.util.IntSet;
import org.nabelab.util.LightArrayList;

/**
 * Consequence Manager
 * @author nabesima
  */
public class ConseqMgr implements DebugTypes,ExitStatus {

  /**
   * Prints the help message.
   */
  public void printHelp() {
    PrintStream out = System.out;
    out.println("Usage: ConseqMgr OPTYPE [OPARGS] FILES...");
    out.println("  -add         add all consequences in the specified files");
    out.println("  -remove      remove from the first file all consequences in the rest files");
    out.println("  -join [LEN]  computes integrated consequences from the specified files");
    out.println("                LEN the maximum length of integrated consequences");
    //out.println("  -njoin [LEN] computes integrated consequences with a naive algorithm");
    //out.println("                LEN the maximum length of integrated consequences");
    out.println("  -num NUM     limit the number of consequences to find in the join operation");
    out.println("  -o FILE      specify the output file");
    out.println("  -z           compress the output with gzip");
    out.println("  -v           verbose mode");
    out.println("  -pp          print all consequences in a compact format");
    //out.println("  -fof       treated as input files as first order formulae");    // for experiments
    out.println("  -Dxxx        print debug information");
    out.println("                A  all information");
    out.println("                p  progress status");
    out.println("  -h           print help");
    out.println();
    System.exit(ExitStatus.OPTION_ERROR);
  }

  /**
   * Constructs a consequence manager.
   * @param args command line arguments
   */
  public ConseqMgr(String[] args) {
    env = new Env();
    opt = new Options(env);
    parser = new Parser(env, opt);
    inputFiles = new ArrayList<File>();

    if (args.length == 0)
      printHelp();

    for (int i = 0; i < args.length; i++) {
      String op    = args[i];
      String oparg = (i + 1 < args.length) ? args[i + 1] : null;

      if (op.equals("-add"))
        opType = OP_ADD;
      else if (op.equals("-remove"))
        opType = OP_REMOVE;
      else if (op.equals("-join")) {
        opType = OP_JOIN;
        if (oparg != null && oparg.matches("[0-9]+")) {
          maxLength = Integer.parseInt(oparg);
          i++;
        }
      }
      else if (op.equals("-njoin")) {
        opType = OP_NAIVE_JOIN;
        if (oparg != null && oparg.matches("[0-9]+")) {
          maxLength = Integer.parseInt(oparg);
          i++;
        }
      }
      else if (op.equals("-num")) {
        if (oparg == null || !oparg.matches("[0-9]+"))
          throw new IllegalArgumentException(
          "-num requires the maximum number specification.");
        maxNum = Integer.parseInt(oparg);
        i++;
      }
      else if (op.equals("-o")) {
        if (oparg == null)
          throw new IllegalArgumentException(
          "-o requires to specify a file name.");
        outputFile = oparg;
        i++;
      }
      else if (op.equals("-z"))
        cmpType = CMP_GZIP;
      else if (op.equals("-v"))
        env.setDebug(DBG_PROGRESS, true);
      else if (op.equals("-pp"))
        fmtType = FMT_PPRINT;
      else if (op.equals("-fof"))
        useFOFver = true;
      else if (!op.startsWith("-"))
        inputFiles.add(new File(op));
      else if (op.startsWith("-D")) {
        for (int j=2; j < op.length(); j++)
          env.setDebug(op.charAt(j), true);
      }
      else if (op.equals("-h"))
        printHelp();
      else
        throw new IllegalArgumentException(
            "Unknown command line argument '" + op + "'");
    }

    // Opens the output file stream.
    if (outputFile != null) {
      try {
        if (cmpType == CMP_GZIP || outputFile.endsWith(".gz"))
          output = new PrintStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(outputFile))));
        else
          output = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
      } catch (FileNotFoundException e) {
        System.err.println("Error: " + e.getMessage());
        System.exit(FILE_NOT_FOUND);
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace(System.err);
        System.exit(UNKNOWN_ERROR);
      }
    }

  }

  /**
   * Returns the type of the operation.
   * @return the type of the operation.
   */
  public int getOpType() {
    return opType;
  }

  /**
   * Returns the list of input files.
   * @return the list of input files.
   */
  public List<File> getInputFiles() {
    return inputFiles;
  }

  /**
   * Loads the list of consequences from the specified file.
   * @param file the specified file.
   * @return the list of consequences from the specified file.
   */
  public List<Conseq> load(File file) throws ParseException, IOException {
    Reader reader = null;
    if (file.getName().endsWith(".gz"))
      reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
    else
      reader = new BufferedReader(new FileReader(file));
    return parser.conseqs(reader);
  }

  /**
   * Executes this manager.
   * @throws ParseException
   */
  public void run() throws ParseException, IOException {

    long start = 0;

    if (env.dbg(DBG_PROGRESS))
      System.out.println("[loading " + getInputFiles().size() + " files]");
    List<List<Conseq>> conseqFamily = new ArrayList<List<Conseq>>();
    List<Clause> samples = new ArrayList<Clause>();
    int first = -1;
    int total = 0;
    for (File file : getInputFiles()) {
      if (env.dbg(DBG_PROGRESS))
        System.out.print("\"" + file);
      start = getCPUTime();
      List<Conseq> conseqList = load(file);
      double elapsed = (getCPUTime() - start) / 1000.0;
      if (env.dbg(DBG_PROGRESS))
        System.out.println("\" (" + conseqList.size() + " conseqs, " + elapsed + "s)");
      conseqFamily.add(conseqList);
      samples.addAll(conseqList);
      if (first == -1)
        first = conseqList.size();
      total += conseqList.size();
    }
    if (env.dbg(DBG_PROGRESS)) {
      System.out.println("[loading time: " + (getCPUTime() / 1000.0) + "s]");
      System.out.println();
    }

    start = getCPUTime();

    env.initFVecMap(samples, null);
    List<Conseq> out = null;

    switch (getOpType()) {
    case OP_ADD:
    {
      ConseqSet conseqSet = new ConseqSet(env);
      if (env.dbg(DBG_PROGRESS))
        System.out.println("[adding & minimizing " + total + " conseqs]");
      Collections.sort(conseqFamily, new ConseqListOrder());    // small set is first
      int num = 0;
      for (List<Conseq> conseqList : conseqFamily) {
        for (Conseq conseq : conseqList) {
          conseqSet.add(conseq);
          if (env.dbg(DBG_PROGRESS))
            System.out.print("Progress: " + (++num) + "/" + total + " (" + conseqSet.size() + ") \r");
        }
      }
      out = conseqSet.get();
      if (env.dbg(DBG_PROGRESS)) {
        System.out.println();
        System.out.println(out.size() + " conseqs are left.");
        System.out.println("[adding & minimizing time: " + ((getCPUTime() - start) / 1000.0) + "s]");
      }
    }
    break;

    case OP_REMOVE:
    {
      ConseqSet conseqSet = new ConseqSet(env);
      int rest = total - first;
      if (env.dbg(DBG_PROGRESS))
        System.out.println("[removing " + rest + " conseqs from " + first + " conseqs]");

      List<Conseq> firstSet = conseqFamily.get(0);
      List<List<Conseq>> restSets = new ArrayList<List<Conseq>>();

      for (int i=1; i < conseqFamily.size(); i++)
        restSets.add(conseqFamily.get(i));
      Collections.sort(restSets, new ConseqListOrder());    // small set is first

      int num = 0;
      if (first <= rest) {
        for (Conseq conseq : firstSet) {
          conseqSet.add(conseq);
          if (env.dbg(DBG_PROGRESS))
            System.out.print("Adding: " + (++num) + "/" + first + " (" + conseqSet.size() + ") \r");
        }
        if (env.dbg(DBG_PROGRESS))
          System.out.println();
        num = 0;
        for (List<Conseq> conseqList : restSets) {
          for (Conseq conseq : conseqList) {
            conseqSet.remove(conseq);
            if (env.dbg(DBG_PROGRESS))
              System.out.print("Removing: " + (++num) + "/" + rest + " (" + conseqSet.size() + ") \r");
          }
        }
      }
      else {
        for (List<Conseq> conseqList : restSets) {
          for (Conseq conseq : conseqList) {
            conseqSet.add(conseq);
            if (env.dbg(DBG_PROGRESS))
              System.out.print("Adding: " + (++num) + "/" + rest + " (" + conseqSet.size() + ") \r");
          }
        }
        if (env.dbg(DBG_PROGRESS))
          System.out.println();
        num = 0;
        for (Conseq conseq : firstSet) {
          conseqSet.add(conseq);
          if (env.dbg(DBG_PROGRESS))
            System.out.print("Adding: " + (++num) + "/" + first + " (" + conseqSet.size() + ") \r");
        }
        if (env.dbg(DBG_PROGRESS))
          System.out.println();
        num = 0;
        for (List<Conseq> conseqList : restSets) {
          for (Conseq conseq : conseqList) {
            conseqSet.remove(conseq);
            if (env.dbg(DBG_PROGRESS))
              System.out.print("Removing: " + (++num) + "/" + rest + " (" + conseqSet.size() + ") \r");
          }
        }
      }

      out = conseqSet.get();
      if (env.dbg(DBG_PROGRESS)) {
        System.out.println();
        System.out.println(out.size() + " conseqs are left.");
        System.out.println("[removing time: " + ((getCPUTime() - start) / 1000.0) + "s]");
      }
    }
    break;

    case OP_JOIN:
    case OP_NAIVE_JOIN:
    {
      if (env.dbg(DBG_PROGRESS))
        System.out.println("[joining " + conseqFamily.size() + " sets (" + total + " conseqs)]");
      out = join(conseqFamily);
      if (env.dbg(DBG_PROGRESS)) {
        System.out.println(out.size() + " consequences were found.");
        System.out.println("[joining time: " + ((getCPUTime() - start) / 1000.0) + "s]");
      }
    }
    break;
    }

    start = getCPUTime();
    if (env.dbg(DBG_PROGRESS)) {
      System.out.println();
      System.out.println("[writing " + out.size() + " conseqs]");
    }
    int num = 0;
    for (Conseq conq : out) {
      if (fmtType != FMT_PPRINT)
        conq.output(output);
      else
        output.println(conq);
      if (env.dbg(DBG_PROGRESS))
        System.out.print("Progress: " + (++num) + "/" + out.size() + " \r");
    }
    output.close();
    if (env.dbg(DBG_PROGRESS)) {
      if (out.size() > 0)
        System.out.println();
      System.out.println("[writing time: " + ((getCPUTime() - start) / 1000.0) + "s]");
      System.out.println();
      System.out.println("CPU time: " + (getCPUTime() / 1000.0) + "s");
      System.out.println();
    }
  }

  /**
   * Computes integrated consequences from the specified consequence family.
   * @param conseqFamily  the family of list of consequences.
   * @return the set of joined consequences.
   */
  private List<Conseq> join(List<List<Conseq>> conseqFamily) {

    // Sorts the family.
    Collections.sort(conseqFamily, new ConseqListOrder());
    // Sorts the list of consequences in the family.
    for (List<Conseq> conseqList : conseqFamily)
      Collections.sort(conseqList, new ConseqOrder());

    if (!useFOFver && isGround(conseqFamily)) {
      if (getOpType() == OP_JOIN)
        return joinPRP(conseqFamily);
      else
        return joinNaivePRP(conseqFamily);
    }
    else {
      return joinNaiveFOF(conseqFamily);
    }
  }

  /**
   * Returns true if the specified family of consequence is ground.
   * @return true if the specified family of consequence is ground.
   */
  private boolean isGround(List<List<Conseq>> conseqFamily) {
    Iterator<List<Conseq>> i = conseqFamily.iterator();
    while (i.hasNext()) {
      Iterator<Conseq> j = i.next().iterator();
      while (j.hasNext())
        if (!j.next().isGround())
          return false;
    }
    return true;
  }

  /**
   * Computes integrated consequences from the specified consequence family.
   * This method is a propositional version.
   * @param conseqFamily  the family of list of consequences.
   */
  private List<Conseq> joinPRP(List<List<Conseq>> conseqFamily) {

    // Translates each term in consequences into an integer.
    HashMap<Term, Integer> intMap = new HashMap<Term, Integer>();
    List<List<IntConseq>> iconseqFamily = term2int(intMap, conseqFamily);

    if (env.dbg(DBG_PROGRESS))
      System.out.print("converting " + conseqFamily.size() + " sets into cnf");

    // Computes the minimum hitting sets of each set of consequences.
    List<IntSet> allcnf = new ArrayList<IntSet>();
    Iterator<List<IntConseq>> i = iconseqFamily.iterator();
    while (i.hasNext()) {
      List<IntConseq> dnf = i.next();
      MHSFinder finder = new MHSFinder(env, dnf);
      List<HitSet> cnf = finder.findAllMHS();
      Iterator<HitSet> j = cnf.iterator();

      while (j.hasNext())
        allcnf.add(j.next());
    }

    if (env.dbg(DBG_PROGRESS)) {
      System.out.println(" (" + allcnf.size() + " clauses)");
      System.out.print("minimizing " + allcnf.size() + " clauses");
    }

    // Minimizes the set of minimum hitting sets with respect to subsumption.
    IntSetDB db = new IntSetDB();
    for (int j=0; j < allcnf.size(); j++)
      db.add(allcnf.get(j));

    if (env.dbg(DBG_PROGRESS)) {
      System.out.println(" (" + db.size() + " clauses are left)");
      System.out.println("generating all minimum models from " + db.size() + " clauses..");
    }

    // Computes the minimum hitting sets of the above sets.
    MHSFinder finder = new MHSFinder(env, db.asList());
    List<HitSet> iconseqSet = finder.findAllMHS(maxLength, maxNum);

    // Translates each integer into a term.
    return int2term(intMap, iconseqSet, iconseqFamily);
  }

  /**
   * Converts each term in the family of consequences into an integer.
   * @param intMap        the mapping from terms to integers.
   * @param conseqFamily  the family of consequences.
   * @return the family of consequences in which each term is represented by an integer.
   */
  private List<List<IntConseq>> term2int(HashMap<Term, Integer> intMap, List<List<Conseq>> conseqFamily) {

    int maxID = 1;
    List<List<IntConseq>> iconseqFamily = new ArrayList<List<IntConseq>>();
    for (int i=0; i < conseqFamily.size(); i++) {
      List<IntConseq> iconseqList = new ArrayList<IntConseq>();
      List<Conseq> conseqList = conseqFamily.get(i);
      for (int j=0; j < conseqList.size(); j++) {
        Conseq conseq  = conseqList.get(j);
        IntConseq iconseq = new IntConseq(conseq);
        for (int k=0; k < conseq.size(); k++) {
          Literal lit = conseq.get(k);
          Term term = lit.getTerm();
          int id = 0;
          if (intMap.containsKey(term))
            id = intMap.get(term);
          else {
            intMap.put(term, maxID);
            id = maxID++;
          }
          if (lit.isPositive())
            iconseq.add(+id);
          else
            iconseq.add(-id);
        }
        iconseqList.add(iconseq);
      }
      iconseqFamily.add(iconseqList);
    }

    return iconseqFamily;
  }

  /**
   * Converts the family of integers into the set of consequence,
   * @param intMap        the mapping from terms to integers.
   * @param iconseqSet    the set of consequences.
   * @param conseqFamily  the family of consequences (for extracting used clauses).
   * @return the set of joined consequence.
   */
  private List<Conseq> int2term(HashMap<Term, Integer> intMap, List<HitSet> iconseqSet, List<List<IntConseq>> iconseqFamily) {

    List<Conseq> out = new ArrayList<Conseq>();

    // Constructs the mapping from integers to terms.
    HashMap<Integer,Term> termMap = new HashMap<Integer,Term>();
    Iterator<Term> i = intMap.keySet().iterator();
    while (i.hasNext()) {
      Term term = i.next();
      int  num  = intMap.get(term);
      termMap.put(num, term);
    }

    // Sorts each sub-consequence in the family of consequence.
    for (List<IntConseq> isubConseqSet : iconseqFamily)
      for (IntConseq isubConseq : isubConseqSet)
        isubConseq.sort();

    // Converts each integer consequence into a term-based consequence.
    for (int j=0; j < iconseqSet.size(); j++) {
      HitSet iconseq = iconseqSet.get(j);
      iconseq.sort();
      IntIterator k = iconseq.iterator();
      ArrayList<Literal> literals = new ArrayList<Literal>();
      while (k.hasNext()) {
        int num = k.next();
        Literal literal = null;
        if (num >= 0)
          literal = new Literal(env, true , termMap.get(+num));
        else
          literal = new Literal(env, false, termMap.get(-num));
        literals.add(literal);
      }

      Conseq conseq = new Conseq(env, literals);

      ArrayList<Clause> used = new ArrayList<Clause>();
      ArrayList<Literal> hit = new ArrayList<Literal>();
      for (int l=0; l < iconseqFamily.size(); l++) {
        List<IntConseq> isubConseqSet = iconseqFamily.get(l);

        // Finds the hit sub-consequence from the set of sub-consequences.
        boolean found = false;
        for (int m=isubConseqSet.size() - 1; m >= 0; m--) {    // First, larger consequence.
          IntConseq isubConseq = isubConseqSet.get(m);
          if (iconseq.containsAll(isubConseq)) {
            Conseq subConseq = isubConseq.getConseq();
            hit.addAll(subConseq.getLiterals());
            if (subConseq.getUsedClauses() != null)
              used.addAll(subConseq.getUsedClauses());
            found = true;
            break;
          }
        }
        assert(found);
      }

      // Validation
      assert(literals.containsAll(hit) && hit.containsAll(literals));

      if (!used.isEmpty())
        conseq.setUsedClauses(used);

      out.add(conseq);
    }

    return out;
  }

  /**
   * Computes integrated consequences from the specified consequence family.
   * This method is a propositional logic & naive approach version.
   * @param conseqFamily  the family of list of consequences.
   * @return the set of joined consequences.
   */
  private List<Conseq> joinNaivePRP(List<List<Conseq>> conseqFamily) {

    maxEstimateLv = Math.min(maxEstimateLv, conseqFamily.size() - 1);
    for (int i=0; i <= maxEstimateLv; i++)
      numSteps *= conseqFamily.get(i).size();

    failedConseqDBs = new FailedConseqDB[conseqFamily.size()];
    for (int i=0; i < conseqFamily.size(); i++)
      failedConseqDBs[i] = new FailedConseqDB(env);

    ConseqSet out = new ConseqSet(env);
    List<Conseq> conseqList = conseqFamily.get(0);
    for (int i=0; i < conseqList.size(); i++) {
      ConseqCandPRP cand = new ConseqCandPRP(env, conseqList.get(i));
      joinNaivePRP(cand, conseqFamily, 1, out);
    }

    if (env.dbg(DBG_PROGRESS))
      System.out.println();

    return out.get();
  }

  private boolean joinNaivePRP(ConseqCandPRP cand, List<List<Conseq>> conseqFamily, int index, ConseqSet out) {

    if (maxNum != NOLIMIT && out.size() >= maxNum)
      return true;

    if (index == conseqFamily.size()) {
      Conseq newConseq = new Conseq(env, cand.getLiterals());
      ArrayList<Clause> used = new ArrayList<Clause>();
      for (Conseq c : cand.getConseqList())
        if (c.getUsedClauses() != null)
          used.addAll(c.getUsedClauses());
      if (!used.isEmpty())
        newConseq.setUsedClauses(used);
      out.add(newConseq);
      return true;
    }

    Clause clause = null;
    if (index < 0) {  // if (index < 3) {
      clause = new Clause(env, cand.getLiterals());
      if (failedConseqDBs[index].hasSubsuming(clause)) {
        if (env.dbg(DBG_PROGRESS) && index == maxEstimateLv) {
          curSteps += conseqFamily.get(index).size();
          System.out.format("Progress: %.1f%% (%d/%d %d found) \r", 100.0 * curSteps / numSteps, curSteps, numSteps, out.size());
        }
        return false;
      }
    }

    boolean success = false;
    List<Conseq> conseqList = conseqFamily.get(index);
    for (int i=0; i < conseqList.size(); i++) {
      if (env.dbg(DBG_PROGRESS) && index == maxEstimateLv) {
        System.out.format("Progress: %.1f%% (%d/%d, %d found) \r", 100.0 * curSteps / numSteps, curSteps, numSteps, out.size());
        curSteps++;
      }
      cand.addAll(conseqList.get(i));
      if (maxLength == NOLIMIT || cand.size() <= maxLength)
        success |= joinNaivePRP(cand, conseqFamily, index+1, out);
      cand.removeLast();
    }

    if (!success && clause != null)
      failedConseqDBs[index].add(clause);

    return success;
  }

  /**
   * Computes integrated consequences from the specified consequence family.
   * This method is a first order & naive approach version.
   * @param conseqFamily  the family of list of consequences.
   * @return the set of joined consequences.
   */
  private List<Conseq> joinNaiveFOF(List<List<Conseq>> conseqFamily) {

    // Renaming
    int numVars = 0;
    for (List<Conseq> conseqSet : conseqFamily) {
      for (Conseq conseq : conseqSet) {
        int num = conseq.getNumVars();
        conseq.setOffset(numVars);
        numVars += num;
      }
    }
    env.getVarTable().addVars(numVars);

    maxEstimateLv = Math.min(maxEstimateLv, conseqFamily.size() - 1);
    for (int i=0; i <= maxEstimateLv; i++)
      numSteps *= conseqFamily.get(i).size();

    // Computes candidates of integrated consequences.
    ConseqSet minCandSet = null;
    List<ConseqCand> candSet = new ArrayList<ConseqCand>();
    candSet.add(new ConseqCand(env));    // adds an empty candidate.
    for (int i=0; i < conseqFamily.size(); i++) {
      List<Conseq> conseqSet = conseqFamily.get(i);
      List<ConseqCand> newCandSet = new ArrayList<ConseqCand>();
      for (ConseqCand cand : candSet) {
        if (env.dbg(DBG_PROGRESS) && i == maxEstimateLv) {
          System.out.format("Progress: %.1f%% (%d/%d, %d found) \r", 100.0 * curSteps / numSteps, curSteps, numSteps, candSet.size());
          curSteps++;
        }
        newCandSet.addAll(joinNaiveFOF(cand, conseqSet));
      }

      //candSet = newCandSet;

      int num = 0;
      minCandSet = new ConseqSet(env);
      for (ConseqCand cand : newCandSet) {
        Conseq conseq = new Conseq(env, "consequence", ClauseTypes.CONSEQUENCE, cand.getLiterals());
        conseq.rename();
        ArrayList<Clause> used = new ArrayList<Clause>();
        for (Conseq c : cand.getConseqList())
          if (c.getUsedClauses() != null)
            used.addAll(c.getUsedClauses());
        if (!used.isEmpty())
          conseq.setUsedClauses(used);
        minCandSet.add(conseq);
        if (env.dbg(DBG_PROGRESS))
          System.out.print("(" + (i+1) + "/" + conseqFamily.size() + ") Adding: " + (++num) + "/" + newCandSet.size() + " (" + minCandSet.size() + ") \r");

      }
      if (env.dbg(DBG_PROGRESS))
        System.out.println();

      candSet = new ArrayList<ConseqCand>();
      for (Conseq conseq : minCandSet) {
        ConseqCand cand = new ConseqCand(env);
        cand.add(conseq.getLiterals(), conseq);
        candSet.add(cand);
      }

    }

    if (env.dbg(DBG_PROGRESS))
      System.out.println();

    // Makes the integrated consequences from candidates.
//    ConseqSet out = new ConseqSet(env);
//    int num = 0;
//    for (ConseqCand cand : candSet) {
//      Literal[] lits = new Literal[cand.size()];
//      int i = 0;
//      for (Literal lit : cand.getLiterals())
//        lits[i++] = lit.instantiate();
//      Conseq conseq = new Conseq(env, "consequence", ClauseTypes.CONSEQUENCE, lits);
//      conseq.rename();
//
//      ArrayList<Clause> used = new ArrayList<Clause>();
//      for (Conseq c : cand.getConseqList())
//        if (c.getUsedClauses() != null)
//          used.addAll(c.getUsedClauses());
//      if (!used.isEmpty())
//        conseq.setUsedClauses(used);
//      out.add(conseq);
//      if (env.dbg(DBG_PROGRESS))
//        System.out.print("Adding: " + (++num) + "/" + candSet.size() + " (" + out.size() + ") \r");
//    }

    if (env.dbg(DBG_PROGRESS))
      System.out.println();

    return minCandSet.get();
    //return out.get();
  }

  private List<ConseqCand> joinNaiveFOF(ConseqCand cand, List<Conseq> conseqSet) {
    List<ConseqCand> candSet = new ArrayList<ConseqCand>();
    for (Conseq conseq : conseqSet)
      candSet.addAll(joinNaiveFOF(cand, new ArrayList<Literal>(), conseq, 0));
    return candSet;
  }

  private List<ConseqCand> joinNaiveFOF(ConseqCand cand, List<Literal> skipped, Conseq conseq, int index) {
    List<ConseqCand> candSet = new ArrayList<ConseqCand>();

    if (maxLength != NOLIMIT && cand.size() + skipped.size() > maxLength)
      return candSet;

    // Terminal condition
    if (index == conseq.size()) {
      ConseqCand newCand = new ConseqCand(cand);
      newCand.add(skipped, conseq);
      candSet.add(newCand);
      return candSet;
    }

    // MERGE operation checking
    Literal lit = conseq.get(index);
    if (cand.contains(lit))
      return joinNaiveFOF(cand, skipped, conseq, index + 1);

    // SKIP operation
    skipped.add(lit);
    candSet.addAll(joinNaiveFOF(cand, skipped, conseq, index + 1));
    skipped.remove(skipped.size() - 1);

    // FACTORING operation
    VarTable varTable = env.getVarTable();
    for (Literal l : cand.getLiterals()) {
      int state = varTable.state();
      Subst g = lit.unify(l);
      if (g != null) {
        candSet.addAll(joinNaiveFOF(cand, skipped, conseq, index + 1));
        varTable.backtrackTo(state);
      }
    }

    return candSet;
  }

  /**
   * Returns the CPU time in milliseconds.
   * @return the CPU time in milliseconds.
   */
  private long getCPUTime() {
    return threadMxBean.getCurrentThreadCpuTime() / 1000000;
  }

  /**
   * The main method of Consequence Manager
   * @param args command line arguments
   */
  public static void main(String[] args) {

    try {
      ConseqMgr manager = new ConseqMgr(args);

      manager.run();

      System.exit(SUCCESS);

    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(OPTION_ERROR);
    } catch (FileNotFoundException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(FILE_NOT_FOUND);
    } catch (ParseException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(PARSE_ERROR);
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace(System.err);
      System.exit(UNKNOWN_ERROR);
    }

  }

  /** Add operation. */
  public final static int OP_ADD = 0;
  /** Sub operation. */
  public final static int OP_REMOVE = 1;
  /** Join operation. */
  public final static int OP_JOIN = 2;
  /** Join operation. */
  public final static int OP_NAIVE_JOIN = 3;

  /** Normal print format. */
  public final static int FMT_PRINT = 0;
  /** Pretty print format. */
  public final static int FMT_PPRINT = 1;

  /** The output is not compressed. */
  public final static int CMP_NONE = 0;
  /** The output is compressed by gzip. */
  public final static int CMP_GZIP = 1;

  /** The constant means no limitation. */
  public final static int NOLIMIT = -1;

  /** The environment. */
  private Env env = null;
  /** The options. */
  private Options opt = null;
  /** The parser. */
  private Parser parser = null;
  /** The set of input files. */
  private ArrayList<File> inputFiles = null;
  /** The output file stream. */
  private String outputFile = null;
  /** The output file stream. */
  private PrintStream output = System.out;
  /** The operation type. */
  private int opType = OP_ADD;
  /** The output format type. */
  private int fmtType = FMT_PRINT;
  /** The compression type. */
  private int cmpType = CMP_NONE;
  /** Use the first order version of the synthesis algorithm. */
  private boolean useFOFver = false;

  /** The maximum length condition of consequences. */
  private int maxLength = NOLIMIT;
  /** The maximum number of consequences to find. */
  private int maxNum = NOLIMIT;

  /** The maximum level for progress estimation. */
  private int maxEstimateLv = 4;
  /** The number of steps for join operation. */
  private int numSteps = 1;
  /** The current number of steps for join operation. */
  private int curSteps = 1;
  /** The list of failed consequence candidate databases. */
  private FailedConseqDB[] failedConseqDBs = null;

  /** For getting CPU time. */
  private ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

  private final static class ConseqCandPRP {

    /**
     * Constructs a empty candidate.
     * @param env  the environment.
     */
    @SuppressWarnings("unused")
		public ConseqCandPRP(Env env) {
      this.env = env;
    }

    /**
     * Constructs a candidate by the specified consequence.
     * @param env    the environment.
     * @param conseq the consequence.
     */
    public ConseqCandPRP(Env env, Conseq conseq) {
      this.env = env;
      addAll(conseq);
    }

    /**
     * Adds the specified consequence to this candidate.
     * @param conseq  the consequence to add.
     */
    public void addAll(Conseq conseq) {
      ArrayList<Literal> added = new ArrayList<Literal>();
      for (Literal lit : conseq)
        if (literals.add(lit))
          added.add(lit);
      addedList.add(added);
      conseqList.add(conseq);
    }

    /**
     * Removes the last added element.
     */
    public void removeLast() {
      conseqList.remove(conseqList.size() - 1);
      List<Literal> lastAdded = addedList.remove(addedList.size() - 1);
      for (Literal lit : lastAdded)
        literals.remove(lit);
    }

    /**
     * Returns the list of literals.
     * @return the list of literals.
     */
    public ArrayList<Literal> getLiterals() {
      return new ArrayList<Literal>(literals);
    }

    /**
     * Returns the list of consequences.
     * @return the list of consequences.
     */
    public ArrayList<Conseq> getConseqList() {
      return conseqList;
    }

    /**
     * Returns the number of literals of this candidate.
     * @return the number of literals of this candidate.
     */
    public int size() {
      return literals.size();
    }

    /**
     * Returns a string representation of this object.
     * @return a string representation of this object.
     */
    public String toString() {
      return literals.toString();
    }

    /** The environment. */
    @SuppressWarnings("unused")
    private Env env = null;
    /** The set of literals. */
    private HashSet<Literal> literals = new HashSet<Literal>();
    /** The list of consequences which are used to generate this candidate. */
    private ArrayList<Conseq> conseqList = new ArrayList<Conseq>();
    /** The list of added literals at each step. */
    private ArrayList<List<Literal>> addedList = new ArrayList<List<Literal>>();
  }

 private final static class FailedConseqDB {

    /**
     * Constructs a set of failed consequence candidates.
     * @param env  the environment.
     */
    public FailedConseqDB(Env env) {
      fvecTrie = new FVecTrie(env, true);
    }

    /**
     * Adds a failed consequence candidate.
     * @param c  the failed consequence candidate to add.
     */
    public void add(Clause c) {
      fvecTrie.add(c.getFVec(false), c);
    }

    /**
     * Returns true if this database has a failed consequence candidate which subsumes the specified candidate.
     * @param c the candidate to be checked.
     * @return true if this database has a failed consequence candidate which subsumes the specified candidate.
     */
    public boolean hasSubsuming(Clause c) {
      return fvecTrie.findSubsuming(c.getFVec(false), c) != null;
    }

    private FVecTrie fvecTrie = null;
  }

 private final static class ConseqCand {

   /**
    * Constructs a empty candidate.
    * @param env  the environment.
    */
   public ConseqCand(Env env) {
     this.env = env;
   }

  /**
    * Constructs a copy of the specified candidate.
    * @param cand  the candidate to be copied.
    */
   public ConseqCand(ConseqCand cand) {
     this.env = cand.env;
     this.literals.addAll(cand.literals);
     this.conseqList.addAll(cand.conseqList);
   }

   /**
    * Adds the skipped literals to this candidate.
    * @param skipped  the set of skipped literals.
    * @param conseq   the consequence which contains the skipped literals.
    */
   public void add(List<Literal> skipped, Conseq conseq) {
     ArrayList<Literal> newLiterals = new ArrayList<Literal>();
     for (Literal lit : literals)
       newLiterals.add(lit.instantiate());
     for (Literal lit : skipped)
       newLiterals.add(lit.instantiate());
     literals = newLiterals;
     conseqList.add(conseq);
   }

   /**
    * Returns true if this candidate has the specified literal.
    * @param literal  the specified literal.
    * @return true if this candidate has the specified literal.
    */
   public boolean contains(Literal literal) {
     for (Literal l : literals)
       if (Literal.equals(l, literal))
         return true;
     return false;
   }

   /**
    * Returns the list of literals.
    * @return the list of literals.
    */
   public ArrayList<Literal> getLiterals() {
     return literals;
   }

   /**
    * Returns the list of consequences.
    * @return the list of consequences.
    */
   public ArrayList<Conseq> getConseqList() {
     return conseqList;
   }

   /**
    * Returns the number of literals in this candidate.
    * @return the number of literals in this candidate.
    */
   public int size() {
     return literals.size();
   }

   /**
    * Returns a string representation of this object.
    * @return a string representation of this object.
    */
   public String toString() {
     return literals.toString();
   }

   /** The environment. */
   private Env env = null;
   /** The set of literals. */
   private ArrayList<Literal> literals = new ArrayList<Literal>();
   /** The list of consequences which are used to generate this candidate. */
   private ArrayList<Conseq> conseqList = new ArrayList<Conseq>();
 }

  private final static class ConseqOrder implements Comparator<Conseq> {
    /**
     * Compares its two arguments for order.
     * @param conseq1 the first object to be compared.
     * @param conseq2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(Conseq conseq1, Conseq conseq2) {
      return conseq1.size() - conseq2.size();
    }
  }

  private final static class ConseqListOrder implements Comparator<List<Conseq>> {
    /**
     * Compares its two arguments for order.
     * @param list1 the first object to be compared.
     * @param list2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(List<Conseq> list1, List<Conseq> list2) {
      return list1.size() - list2.size();
    }
  }

  private final static class IntConseq extends IntArraySet {
    /**
     * Constructs a consequence which is represented by a set of integers.
     * @param conseq  the original consequence.
     */
    public IntConseq(Conseq conseq) {
      super();
      this.conseq = conseq;
    }
    /**
     * Returns the original consequence.
     * @return the original consequence.
     */
    public Conseq getConseq() {
      return conseq;
    }
    /** The original consequence. */
    private Conseq conseq = null;
  }

  private final static class IntSetDB extends LightArrayList<IntArraySet> {

    /**
     * Adds the specified set to this set.
     * @param obj the object to add.
     */
    public void add(IntSet set) {
      IntArraySet aset = new IntArraySet(set);

      // Sorts the set.
      aset.sort();

      // Forward subsumption checking.
      if (hasSubsuming(aset))
        return;

      // Backward subsumption checking.
      removeSubsumed(aset);

      super.add(aset);
    }

    /**
     * Converts this database into a list.
     * @return a list representation of this database.
     */
    public List<IntSet> asList() {
      List<IntSet> out = new ArrayList<IntSet>();
      for (int i=0; i < size(); i++)
        out.add(new IntArraySet(get(i)));
      return out;
    }

    /**
     * Returns true if this database has a set which subsumes the specified set.
     * @param y   the specified set.
     * @return true if this database has a set which subsumes the specified set.
     */
    public boolean hasSubsuming(IntArraySet y) {
      for (int i=0; i < size(); i++) {
        IntArraySet x = get(i);
        if (x.size() > y.size())
          continue;
        // Two sets are sorted!
        int xpos = 0;
        int ypos = 0;
        while (true) {
          if (xpos == x.size())
            return true;
          if (ypos == y.size())
            break;
          int xval = x.getAt(xpos);
          int yval = y.getAt(ypos);
          if (xval < yval)
            break;
          else if (xval > yval)
            ypos++;
          else {
            xpos++;
            ypos++;
          }
        }
      }
      return false;
    }

    /**
     * Removes the sets which are subsumed by the specified set.
     * @param x   the specified set.
     */
    public void removeSubsumed(IntArraySet x) {
      for (int i=size() - 1; i >= 0; i--) {
        IntArraySet y = get(i);
        if (x.size() > y.size())
          continue;
        // Two sets are sorted!
        int xpos = 0;
        int ypos = 0;
        while (true) {
          if (xpos == x.size()) {
            remove(i);
            break;
          }
          if (ypos == y.size())
            break;
          int xval = x.getAt(xpos);
          int yval = y.getAt(ypos);
          if (xval < yval)
            break;
          else if (xval > yval)
            ypos++;
          else {
            xpos++;
            ypos++;
          }
        }
      }
    }
  }
}
