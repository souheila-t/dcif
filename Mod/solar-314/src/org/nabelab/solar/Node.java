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

//MEMO for J2SE1.6
//import java.util.ArrayDeque; 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.nabelab.solar.constraint.Constraint;
import org.nabelab.solar.constraint.Disjunction;
import org.nabelab.solar.constraint.NotEqual;
import org.nabelab.solar.equality.EqType;
import org.nabelab.solar.equality.NEFInfo;
import org.nabelab.solar.indexing.DiscTree;
import org.nabelab.solar.operator.Conqueror;
import org.nabelab.solar.operator.OpChecker;
import org.nabelab.solar.operator.Operator;
import org.nabelab.solar.operator.Operators;
import org.nabelab.solar.operator.RootExtension;
import org.nabelab.solar.proof.Proof;
import org.nabelab.solar.util.ArrayQueue;

/**
 * @author nabesima
 *
 */
public class Node implements ClauseTypes, Tags, OptionTypes, DebugTypes {
  
  /**
   * Constructs a root node.
   * @param env        the environment.
   * @param opt        the options.
   * @param tableau    the tableau to which this node belongs.
   * @param topClauses the set of top clauses. 
   */
  public Node(Env env, Options opt, Tableau tableau, List<Clause> topClauses) {
    this.env     = env;
    this.opt     = opt;
    this.tableau = tableau;
    this.stats   = tableau.stats();
    //for (Clause c : topClauses)
    for (int i=topClauses.size() - 1; i >= 0; i--)
      operators.add(new RootExtension(env, this, topClauses.get(i)));
    first = false;
    if (operators.size() > 1 && tableau.getOpOrder().use())
      operators.sort(tableau.getOpOrder().comparator());
  }
  
  /**
   * Constructs a node which has the specified literal and parent.
   * @param literal the specified literal.
   * @param parent  the parent node.
   */
  public Node(Literal literal, Node parent) {
    this(literal, literal, parent);
  }
  
  /**
   * Constructs a node which has the specified literal and parent.
   * @param literal the specified literal.
   * @param origin  the original literal of the specified literal.
   * @param parent  the parent node.
   */
  public Node(Literal literal, Literal origin, Node parent) {
    this.env      = parent.env;
    this.opt      = parent.opt;
    this.tableau  = parent.tableau;
    this.depth    = parent.depth + 1;
    this.extDepth = parent.extDepth + (literal.isConnPred() ? 0 : 1);
    this.parent   = parent;
    this.stats    = parent.stats;
    if (opt.getEqType() >= CFP.EQ_SNMTN && literal.isEqualPred()) {
      if (literal.isPositive() || (opt.getEqType() == CFP.EQ_SNM && literal.isNegative())) { 
        addTag(EQ_RAW);
        eqType = new EqType(literal.getTerm());
      }      
    }
//    if (opt.divide()) {
//      float depthLimit = opt.getMaxDivDepth();
//      if (depthLimit == 0) { // ALL
//        if (tableau.getSearchParam().getDepthLimit() != 0)
//          depthLimit = tableau.getSearchParam().getDepthLimit() - 1;   // In the leaf level, do not divide.
//        else
//          depthLimit = Integer.MAX_VALUE;
//      }
//      else if (0.0 < depthLimit && depthLimit < 1.0) {  // Ratio
//        if (tableau.getSearchParam().getDepthLimit() != 0)
//          depthLimit = (int)(tableau.getSearchParam().getDepthLimit() * depthLimit);
//        else
//          depthLimit = Integer.MAX_VALUE;
//      }
//      else if (depthLimit < 0.0) {
//        if (tableau.getSearchParam().getDepthLimit() != 0)
//          depthLimit = tableau.getSearchParam().getDepthLimit() - depthLimit;
//        else
//          depthLimit = Integer.MAX_VALUE;
//      }        
//      if (depth == 1 || depth <= depthLimit)
//        addTag(DIVIDED);      
//    }
    if (opt.use(USE_NODE_INSTANTIATION) && literal.hasBindedVars())
      literal = literal.instantiate();
    if (opt.use(USE_NEGATION_AS_FAILURE) && literal.hasNAF())
      addTag(NOT_SKIPPABLE);
    this.literal  = literal;
    this.origin   = origin;
  }
  
  /**
   * Construct an empty node for identification.
   */
  private Node() {   
  }
  
  /**
   * Instantiates all variables in this node are replaced with the values. 
   */
  public void instantiate() {
    if (literal != null)
      literal = literal.instantiate();
    if (extChild != null)
      extChild = extChild.instantiate();
  }
  
  /**
   * Returns the literal which is associated with this node.
   * @return the literal which is associated with this node.
   */
  public Literal getLiteral() {
    return literal;
  }
  
  /**
   * Returns the original literal which is associated with this node.
   * @return the original literal which is associated with this node.
   */
  public Literal getOrigin() {
    return origin;
  }
  
  /**
   * Adds all literals in the specified clause to this node.
   * @param topClause the clause to add.
   */
  public void addTopChildren(Clause topClause) {
    tableau.setTopClause(topClause);
    child = new Node(topClause.get(0), this);
    Node prev = child;
    for (int i=1; i < topClause.size(); i++) {
      Node node = new Node(topClause.get(i), this);
      node.left  = prev;
      prev.right = node;
      prev = node;
    }
    if (opt.use(USE_IDENTICAL_FOLDING_DOWN)) {
      Node node = child;
      while (node.right != null) {
        Node right = node.right;
        while (right != null) {
          node.addFoldingDown(right);
          right = right.right;
        }
        node = node.right;
      }
    }          
    numChildren = topClause.size();
    tableau.incNodes(numChildren);
  }

  /**
   * Adds all literals except for the head in the specified clause to this node.
   * @param clause the clause to add.
   */
  public void addChildren(Clause clause) {
    extChild = clause.get(0);
    if (clause.size() > 1) {
      child = new Node(clause.get(1), this);
      Node prev = child;      
      for (int i=2; i < clause.size(); i++) {
        Node node = new Node(clause.get(i), this);
        node.left  = prev;
        prev.right = node;
        prev = node;
      }
      if (opt.use(USE_IDENTICAL_FOLDING_DOWN)) {
        Node node = child;
        while (node.right != null) {
          Node right = node.right;
          while (right != null) {
            node.addFoldingDown(right);
            right = right.right;
          }
          node = node.right;
        }
      }      
    }
    numChildren = clause.size();
    tableau.incNodes(numChildren);
    tableau.decOpenNodes();        // Closed by the extension target node.
  }
    
  /**
   * Removes all the children.
   */
  public void removeChildren() {
    tableau.decNodes(numChildren);
    if (hasTag(EXTENDED))
      tableau.incOpenNodes();
    while (child != null && child.hasTag(DIVISION_COMPLETED)) {
      tableau.incOpenNodes();
      child = child.right;
    }
    numChildren = 0;
    child    = null;
    extChild = null;
  }
  
  /**
   * Adds two specified literals to this node as children.
   * @param eq1 the first equality literal to add.
   * @param eq2 the second equality literal to add.
   */
  public void addSymSplittedNode(Literal eq) {
    assert(eq.isEqualPred());
    child = new Node(eq, this);
    child.removeTag(EQ_RAW);
    child.addTag(EQ_MATURE);
    child.extDepth = extDepth;
    numChildren = 1;
    tableau.incNodes(numChildren);
  }
    
  /**
   * Adds two specified literals to this node as children.
   * @param eq1 the first equality literal to add.
   * @param eq2 the second equality literal to add.
   */
  public void addSymSplittedNodes(Literal eq1, Literal eq2) {
    assert(eq1.isEqualPred() && eq2.isEqualPred());
    child = new Node(eq1, this);
    Node brother = new Node(eq2, this);
    child.right = brother;
    child.removeTag(EQ_RAW);
    child.addTag(EQ_MATURE);
    child.extDepth = extDepth;
    brother.left = child;
    brother.extDepth = extDepth;
    if (opt.getEqType() == CFP.EQ_SNM) {
      brother.removeTag(EQ_RAW);
      brother.addTag(EQ_MATURE);
    }
    numChildren = 2;
    tableau.incNodes(numChildren);
  }
    
  /**
   * Removes two specified literals to this node as children.
   */
  public void removeSymSplittedNodes() {
    tableau.decNodes(numChildren);
    numChildren = 0;
    child    = null;
  }
    
  /**
   * Adds two specified literals to this node as children.
   * @param subNeq    the negative equality that rewrites a non-variable term in orgNeq to a variable.
   * @param orgNeq    the negative equality that has a variable which is replaced by subNeq.
   * @param nefInfo   the information of the NEF operator that is applied to this node.
   */
  public void addNegEqFlatNodes(Literal subNeq, Literal orgNeq, NEFInfo nefInfo) {
    assert(subNeq.isNegEqualPred() && orgNeq.isNegEqualPred());
    child = new Node(subNeq, this);
    Node brother = new Node(orgNeq, this);
    child.right = brother;
    brother.left = child;
    brother.nefInfo = nefInfo;
    if (opt.use(USE_IDENTICAL_FOLDING_DOWN)) 
      child.addFoldingDown(brother);
    numChildren = 2;
    tableau.incNodes(numChildren);
  }

  /**
   * Adds two specified literals to this node as children.
   * @param literals  the set of negative equalities.
   */
  public void addNegEqFlatNodes(ArrayList<Literal> literals) {
    assert(literals.size() > 1);
    child = new Node(literals.get(0), this);
    Node prev = child;
    for (int i=1; i < literals.size(); i++) {
      Node node = new Node(literals.get(i), this);
      node.left  = prev;
      prev.right = node;
      prev = node;
    }
    if (opt.use(USE_IDENTICAL_FOLDING_DOWN)) {
      Node node = child;
      while (node.right != null) {
        Node right = node.right;
        while (right != null) {
          node.addFoldingDown(right);
          right = right.right;
        }
        node = node.right;
      }
    }          
    numChildren = literals.size();
    tableau.incNodes(numChildren);
  }

  /**
   * Return the information of the NEF operator that is applied to this node.
   * @return the information of the NEF operator that is applied to this node.
   */
  public NEFInfo getNEFInfo() {
    return nefInfo;
  }
    
  /**
   * Removes two specified literals to this node as children.
   */
  public void removeNegEqFlatNodes() {
    tableau.decNodes(numChildren);
    numChildren = 0;
    child    = null;
    nefInfo  = null;
  }
    
  /**
   * Adds equality extended nodes as children.
   * @param clause  the clause to add.
   * @param gchild  the grand child node to add.
   */
  public void addEqExtendedNodes(Clause clause, Literal gchild) {
    assert(literal.isEqualPred());
    
    child = new Node(clause.get(0), this);
    Node prev = child;
    for (int i=1; i < clause.size(); i++) {
      Node node = new Node(clause.get(i), this);
      node.left  = prev;
      prev.right = node;
      prev = node;
    }
    numChildren = clause.size();
    
    Node childchild = new Node(gchild, child);
    childchild.extDepth = child.extDepth;    
    child.child = childchild;
    if (opt.getEqType() == CFP.EQ_SNM || (opt.getEqType() == CFP.EQ_SNMTN && gchild.isPositive())) {
      childchild.removeTag(EQ_RAW);
      childchild.addTag(EQ_MATURE);
    }
    child.numChildren = 1;
    child.addTag(SYMMETRY_SPLITTED);
    child.orgVarState = env.getVarTable().state();
    child.orgNumVars  = env.getVarTable().getNumVars();
    
    tableau.incNodes(numChildren + 1);
  }
    
  /**
   * Removes equality extended nodes.
   */
  public void removeEqExtendedNodes() {
    tableau.decNodes(numChildren + 1);
    if (!isRoot())
      tableau.incOpenNodes();
    numChildren = 0;
    child.child = null;
    child = null;
  }
  
  /**
   * Returns the equality type information.
   * @return the equality type information.
   */
  public EqType getEqType() {
    return eqType;
  }
  
  /**
   * Returns the next applicable operator.
   * @return the next applicable operator.
   */
  public Operator getNextOperator() {
    // If it is first, finds all applicable operators to this node.
    if (first) {
      first = false;
      tags &= (EQ_RAW | EQ_MATURE | CONTRACTIBLE | DIVIDED);
      VarTable varTable = env.getVarTable();
      orgVarState = varTable.state();
      orgNumVars  = varTable.getNumVars();
      operators.clear();
      if (failCache != null)
        failCache.clear();
      foldingUps = null;

      if (opt.use(USE_NEGATION_AS_FAILURE)) {
        tags |= (parent.tags & NOT_SKIPPABLE);    // Inherits the not-skippable property of the parent.
        if (hasNAF()) addTag(NOT_SKIPPABLE);      // Skip operations are not allowed below NAF nodes. 
      }
            
      // Skip-regularity checking
      if (opt.use(USE_SKIP_REGULARITY)) {
        Skipped skipped = tableau.getSkipped();
        if (skipped != null && !skipped.isEmpty()) {
          // Adaptive use of skip-regularity (test1)
          boolean use = true;
          if (opt.use(USE_TEST1)) {
            stats.incTests(Stats.SKIP_REGULARITY_TRY);
            if (stats.getSuccs(Stats.SKIP_REGULARITY_GEN) != 0) {
              long interval = stats.getTests(Stats.SKIP_REGULARITY_GEN) / stats.getSuccs(Stats.SKIP_REGULARITY_GEN);
              interval >>= 2;
              if (stats.getTests(Stats.SKIP_REGULARITY_TRY) % interval != 0)
                use = false;
            }
          }
          
          if (use) {
            stats.incTests(Stats.SKIP_REGULARITY_GEN);
            List<Unifiable<Node>> unifiables = skipped.findCompUnifiable(literal);
            if (unifiables != null) {
              Operator last = tableau.getLastOperator();
              for (int i = 0; i < unifiables.size(); i++) {
                Unifiable<Node> unif = unifiables.get(i);
                
                Subst g = unif.getSubst();
                if (g.isEmpty()) {
                  stats.incSuccs(Stats.SKIP_REGULARITY_GEN);
                  return null;
                } else if (g.size() == 1) {
                  NotEqual neq = new NotEqual(env, opt, this, Stats.SKIP_REGULARITY_CHK, g.getVar(0), g.getVal(0));
                  if (tableau.addConstraint(neq)) {
                    last.addGenerated(neq);
                    stats.incProds(Stats.SKIP_REGULARITY_GEN);
                  }
                } else {
                  HashSet<Constraint> dis = new HashSet<Constraint>();
                  for (int j = 0; j < g.size(); j++)
                    dis.add(new NotEqual(env, opt, this, Stats.SKIP_REGULARITY_CHK, g.getVar(j), g.getVal(j)));
                  Disjunction disjunct = new Disjunction(env, this, Stats.SKIP_REGULARITY_CHK, dis);
                  if (tableau.addConstraint(disjunct)) {
                    last.addGenerated(disjunct);
                    stats.incProds(Stats.SKIP_REGULARITY_GEN, disjunct.size() + 1);
                  }
                }
              }
            }
          }          
        }
      }
      
      
      //
      // TEST
      // 
      if (opt.divide()) {
        float depthLimit = opt.getMaxDivDepth();
        if (depthLimit == 0) { // ALL
          if (tableau.getSearchParam().getDepthLimit() != 0)
            depthLimit = tableau.getSearchParam().getDepthLimit() - 1;   // In the leaf level, do not divide.
          else
            depthLimit = Integer.MAX_VALUE;
        }
        else if (0.0 < depthLimit && depthLimit < 1.0) {  // Ratio
          if (tableau.getSearchParam().getDepthLimit() != 0)
            depthLimit = (int)(tableau.getSearchParam().getDepthLimit() * depthLimit);
          else
            depthLimit = Integer.MAX_VALUE;
        }
        else if (depthLimit < 0.0) {
          if (tableau.getSearchParam().getDepthLimit() != 0)
            depthLimit = tableau.getSearchParam().getDepthLimit() - depthLimit;
          else
            depthLimit = Integer.MAX_VALUE;
        }        
//        if (depth == 1 || depth <= depthLimit)
//          addTag(DIVIDED);      
        if (depth == 1) 
          addTag(DIVIDED);
        else if (depth <= depthLimit && parent.isDivided() && parent.getNumChildren() >= 4)
          addTag(DIVIDED);
      }

      
      if (isDivided()) {
        assert(succCache == null);
        int maxSuccs = opt.getMaxSuccs();
        // The parent is not root AND there is a limitation of the max successes AND the max successes is distributed to each operator. 
        if (!parent.isRoot() && maxSuccs != 0 && opt.getDivCommonRatio() != 1.0f) { 
          float comRatio = opt.getDivCommonRatio();
          int   capacity = parent.succCache.getMaxSize() - parent.succCache.size();
          if (capacity < 0) capacity = 1;
          float denominator = (float)(2 * (1 - Math.pow(comRatio, parent.operators.size())));
          if (denominator > 0.0)
            capacity = Math.round(capacity / denominator);
          maxSuccs = capacity;
        }
        succCache = new LSuccCache(env, opt, this, maxSuccs);
        succCache.setStartInfStep(env.getTimeStep());
      }
          
      //
      // Computes applicable operators.
      //
      
      ArrayList<Subst> prevSubsts = new ArrayList<Subst>();
      if (isDivided() && left != null) {
        assert(left.succCache.size() > 0);
        if (left.succCache.size() <= 1 /* || literal.isMaxGeneral()*/) {
          VarRenameMap map = env.getVarRenameMap();
          int maxVar = orgNumVars - 1;
          for (int i=0; i < left.succCache.size(); i++) {
            Subst g = new Subst(left.succCache.get(i));
            g.instantiate();
            map.clear();
            map.setOffset(orgNumVars);
            g.subrename(map, Integer.MIN_VALUE, -1);
            if (maxVar < map.getMaxVar())
              maxVar = map.getMaxVar();              
            prevSubsts.add(g);
          }
          varTable.addVars(maxVar - orgNumVars + 1);
        }
      }
      if (prevSubsts.isEmpty())
        prevSubsts.add(new Subst());
      
      ArrayList<OpChecker> opCheckers = tableau.getOpCheckers();
      NextPrevSubst:
      for (int i=0; i < prevSubsts.size(); i++) {
        Subst g = prevSubsts.get(i);
        varTable.substitute(g);
        Operators ops = new Operators();
        for (int j=0; j < opCheckers.size(); j++) {
          OpChecker opChecker = opCheckers.get(j);
          // If a operator checker returns false (it means the tableau is redundant), then backtracks immediately.
          if (!opChecker.check(this, ops)) {
            //first = true;
            //return null;
            varTable.backtrackTo(orgVarState);            
            continue NextPrevSubst;
          }
          // If a mandatory operator is found, returns it only.
          if (!ops.isEmpty() && ops.getLast().isMandatory()) {
            Operator last = ops.getLast();
            ops.clear();
            ops.add(last);
            break;
          }
        }
        varTable.backtrackTo(orgVarState);
        if (!g.isEmpty()) 
          for (int j=0; j < ops.size(); j++)
            ops.get(j).getSubst().add(g);
        operators.addAll(ops);
      }
      if (operators.size() > 1 && tableau.getOpOrder().use())
        operators.sort(tableau.getOpOrder().comparator());
      if (operators.size() > 0 && isDivided())
        operators.add(new Conqueror(env, this));
    }
    
    if (operators.isEmpty()) {
      if (env.dbgNow(DBG_TABLEAUX)) 
        System.out.println("FAILED by no applicable operators.");
      stats.inc(Stats.FAIL);
      first = true;
      return null;
    }
    
    if (env.dbgNow(DBG_TABLEAUX)) {
      System.out.println("Operators:");
      for (int i=0; i < operators.size(); i++)
        System.out.println(" " + operators.get(i));
    }
    
    // Chooses the next operator.
    if (!isDivided())
      return operators.removeFirst();
    
    // For divided subgoals: 
    Operator op = operators.removeFirst();
    if (succCache.hasEmptySucc()) {
      while (!(op instanceof Conqueror))
        op = operators.removeFirst();
    }
    else if (succCache.isFull()) {
      tableau.getSearchParam().setExhaustiveness(false);
      markAsNotExhausted();
      while (!(op instanceof Conqueror))
        op = operators.removeFirst();      
    }
    else if (succCache.isEmpty() && op instanceof Conqueror)
      op = null;
    
    return op;
  }
  
  /**
   * Returns true if there is a next applicable operator.
   * @return true if there is a next applicable operator.
   */
  public boolean hasNextOperator() {
    // If you want to call this method at the first time, then you have to
    // execute the above "if (first) { ... }" code.
    assert(first == false);   
    return !operators.isEmpty();
  }
  
  /**
   * Clears the operators.
   */
  public void clearOperators() {
    operators.clear();
  }
  
  /**
   * Reset this node.
   */
  public void reset() {
    first = true;
  }
  
  /**
   * Returns the number of variables at the first solving this node.
   * @return the variable state of this node.
   */
  public int getOrgNumVars() {
    return orgNumVars;
  }
  
  /**
   * Returns the variable state at the first solving this node.
   * @return the variable state of this node.
   */
  public int getOrgVarState() {
    return orgVarState;
  }
  
  /**
   * Returns the number of children.
   * @return the number of children.
   */
  public int getNumChildren() {
    return numChildren;
  }
  
  
  /**
   * Returns the parent node.
   * @return the parent node.
   */
  public Node getParent() {
    return parent;
  }

  /**
   * Returns the left node.
   * @return the left node.
   */
  public Node getLeft() {
    return left;
  }

  /**
   * Returns the right node.
   * @return the right node.
   */
  public Node getRight() {
    return right;
  }

  /** 
   * Returns the first child.
   * @return the first child.
   */
  public Node getFirstChild() {
    return child;
  }
  
  /**
   * Returns the literal that is used to extend this node.
   * @return the literal that is used to extend this node.
   */
  public Literal getExtChild() {
    return extChild;
  }
  
  /**
   * Return the next node.
   * @param the next node.
   */
  public Node getNext() {
    // If there is a child, then it is the next node.
    if (child != null)
      return child;
      
    // If there is a right brother, then returns it.
    if (right != null)
      return right;
      
    // Otherwise
    Node p = parent;
    while (p.right == null) 
      if ((p = p.parent) == null)
        return null;
      
    return p.right;
  }

  /**
   * Returns true if this is the root node.
   * @return true if this is the root node.
   */
  public boolean isRoot() {
    return parent == null;
  }
  
  /**
   * Returns true if this node has children.
   * @return true if this node has children.
   */
  public boolean hasChildren() {
    return child != null;
  }
  
  /**
   * Returns the tableau to which this node belongs.
   * @return the tableau to which this node belongs.
   */
  public Tableau getTableau() {
    return tableau;
  }

  /**
   * Returns the depth of this node.
   * @return the depth of this node.
   */
  public int getDepth() {
    return depth;
  }
  
  /**
   * Returns the extension depth of this node.
   * @return the extension depth of this node.
   */
  public int getExtDepth() {
    return extDepth;
  }
  
  /**
   * Sets the reduction target node.
   * @param target the target node.
   */
  public void setReductionTarget(Node target) {
    assert(this.reductionTarget == null);
    this.reductionTarget = target;
  }
  
  /**
   * Clear the reduction target node.
   */
  public void clearReductionTarget() {
    reductionTarget = null;
  }
  
  /**
   * Returns the depth of the most shallow reduction target.
   * @return the depth of the most shallow reduction target.
   */
  // MEMO the following is a simple implementation.
  public int getShallowestTargetDepth() {
    if (!isClosed())
      return -1;
    if (hasTag(STRONG_CONTRACTION))
      return -1;
    if (hasTag(IDENTICAL_FOLDING_DOWN))
      return reductionTarget.getShallowestTargetDepth();
    if (reductionTarget != null)    
      return reductionTarget.depth;    // There is no children.
    if (child == null)
      return depth;
    
    int min = depth;
    ArrayQueue<Node> queue = new ArrayQueue<Node>();
    queue.add(child);
    while (!queue.isEmpty()) {
      Node n = queue.remove();
      if (!n.isClosed())
        return -1;
      if (n.hasTag(STRONG_CONTRACTION))
        return -1;
      if (n.hasTag(IDENTICAL_FOLDING_DOWN)) {
        int d = n.reductionTarget.getShallowestTargetDepth();
        if (d == -1)
          return -1;
        if (d < min)
          min = d;
      }
      else if (n.reductionTarget != null) {
        if (n.reductionTarget.depth < min)
          min = n.reductionTarget.depth;
      }
      if (n.right != null) 
        queue.add(n.right);
      if (n.child != null) 
        queue.add(n.child);
    }

    return min;      
  }
  
  /**
   * Returns the deepest reduction target.
   * @return the deepest reduction target.
   */
  // MEMO the following is a simple implementation.
  public Node getDeepestTarget() {
    if (!isClosed())
      return null;
    if (hasTag(STRONG_CONTRACTION))
      return null;
    if (hasTag(IDENTICAL_FOLDING_DOWN))
      return reductionTarget.getDeepestTarget();
    if (reductionTarget != null)    
      return reductionTarget;    // There is no children.
    if (child == null)
      return tableau.getRoot();
    
    int  max = 0;
    Node maxNode = null;
    ArrayQueue<Node> queue = new ArrayQueue<Node>();
    queue.add(child);
    while (!queue.isEmpty()) {
      Node n = queue.remove();
      if (!n.isClosed())
        return null;
      if (n.hasTag(STRONG_CONTRACTION))
        return null;
      if (n.hasTag(IDENTICAL_FOLDING_DOWN)) {
        Node m = n.reductionTarget.getDeepestTarget();
        if (m == null)
          return null;
        if (max < m.depth && m.depth < depth) {
          max = m.depth;
          maxNode = m;
        }
      }
      else if (n.reductionTarget != null) {
        if (max < n.reductionTarget.depth && n.reductionTarget.depth < depth) {
          max = n.reductionTarget.depth;
          maxNode = n.reductionTarget;
        }
      }
      if (n.right != null)
        queue.add(n.right);
      if (n.child != null) 
        queue.add(n.child);
    }

    return maxNode;
  }

  /**
   * Adds the specified node as a folding-up lemma.
   * @param node  the specified node.
   * @return the literal if it is added to folding-up lemmas.
   */
  public Literal addFoldingUp(Node node) {
    if (foldingUps == null)
      foldingUps = new DiscTree<Node>(env, false);
  
    Literal lit = node.getLiteral();
    if (foldingUps.contains(lit) != null)
      return null;

    foldingUps.add(lit, node);
    
    if (opt.hasVerifyOp()) {
      Proof proof = tableau.getProof(null, node);
      node.setProof(proof);
    }
    
    return lit;
  }
  
  /**
   * Removes the specified node from folding-up lemmas.
   * @param lit the specified node.
   * @return true if the node is removed.
   */
  public boolean removeFoldingUp(Node node) {
    assert(foldingUps != null);
    return foldingUps.remove(node.literal, node);
  }
  
  /**
   * Returns true if this node has a folding-up lemma that is equal to the literal of the specified node.
   * @param node the specified node.
   * @return true if this node has a folding-up lemma that is equal to the literal of the specified node.
   */
  public Node containsFoldingUp(Node node) {
    if (foldingUps == null)
      return null;
    return foldingUps.contains(node.literal);
  }
  
  /**
   * Returns true if this node has a folding-up lemma that is complementary equal to the literal of the specified node.
   * @param node the specified node.
   * @return true if this node has a folding-up lemma that is complementary equal to the literal of the specified node.
   */  
  public Node compContainsFoldingUp(Node node) {
    if (foldingUps == null)
      return null;
    return foldingUps.compContains(node.literal);
  }
  
  /**
   * Adds the specified node as a folding-down lemma.
   * @param node  the specified node.
   * @return the literal if it is added to folding-up lemmas.
   */
  public Literal addFoldingDown(Node node) {
    if (foldingDowns == null)
      foldingDowns = new DiscTree<Node>(env, false);
  
    Literal lit = node.getLiteral();
    if (foldingDowns.contains(lit) != null)
      return null;

    foldingDowns.add(lit, node);
    stats.incProds(Stats.IDENTICAL_FOLDING_DOWN);
    
    return lit;
  }

  /**
   * Returns true if this node has a folding-down lemma that is equal to the literal of the specified node.
   * @param node the specified node.
   * @return true if this node has a folding-down lemma that is equal to the literal of the specified node.
   */
  public Node containsFoldingDown(Node node) {
    if (foldingDowns == null)
      return null;
    return foldingDowns.contains(node.literal);
  }
  
  /**
   * Returns true if this node has a folding-down lemma that is complementary equal to the literal of the specified node.
   * @param node the specified node.
   * @return true if this node has a folding-down lemma that is complementary equal to the literal of the specified node.
   */  
  public Node compContainsFoldingDown(Node node) {
    if (foldingDowns == null)
      return null;
    return foldingDowns.compContains(node.literal);
  }
  
  /**
   * Adds the local success to this node.
   * @return an added local success if added.
   */
  public LSucc addLocalSuccess() {
    //stats.incProds(Stats.LOCAL_FAILURE_CACHE);
    // TODO tableau.getSkipped() is not correct! It has to return the skipped node below this node.    
    return succCache.add(tableau.getSkipped());
  }
  
  /**
   * Adds a local success cache of a child node.
   * @param child  a local success cache of a child node.
   */
  public void addLocalSuccess(LSuccCache child) {
    succCache.add(child);
  }
  
  /**
   * Returns the local success cache.
   * @return the local success cache.
   */
  public LSuccCache getLSuccCache() {
    return succCache;
  }
  
  /**
   * Sets a new local success cache to this node.
   * @param cache  a local success cache.
   */
  public void setLSuccCache(LSuccCache cache) {
    succCache = cache;
  }
  
  /**
   * Clears the local success cache.
   */
  public void clearLSuccCache() {
    succCache = null;
  }
  
  /**
   * Adds the local failure to this node.
   * @return a local failure to be added.
   */
  public LFail addLocalFailure() {
    if (failCache == null)
      failCache = new LFailCache(env);
    stats.incProds(Stats.LOCAL_FAILURE_CACHE);
    
    return failCache.add(orgVarState, orgNumVars, tableau.getSkipped());
  }
  
  /**
   * Returns the local failure cache.
   * @return the local failure cache.
   */
  public LFailCache getLFailCache() {
    return failCache;
  }
  
  /**
   * Returns true if there is a more general local failure.
   * @return a general local failure if exists.
   */
  public LFail hasMoreGeneralFailure(Clause curSkipped) {
    if (failCache == null)
      return null;
    return failCache.hasMoreGeneralFailure(orgNumVars, curSkipped);//tableau.getSkipped());
  }
  
  /**
   * Returns the mgu if this node is unifiable with the specified node.
   * @param other the specified node.
   * @return the mgu if this node is unifiable with the specified node.
   */
  public Subst isUnifiable(Node other) {
    return literal.isUnifiable(other.literal);
  }
  
  /**
   * Returns the mgu if this node is unifiable with the complement of the specified node.
   * @param other the specified node.
   * @return the mgu if this node is unifiable with the complement of the specified node.
   */
  public Subst isCompUnifiable(Node other) {
    return literal.isCompUnifiable(other.literal);
  }
  
  /**
   * Returns true if this node has NAF.
   * @return true if this node has NAF.
   */
  public boolean hasNAF() {
    return literal.hasNAF();
  }
  
  /**
   * Adds the specified tag to this node.
   * @param tag the tag to add.
   */
  public void addTag(int tag) {
    int prev = tags;
    tags |= tag;
    if ((tags & SOLVED) != 0 && (prev & SOLVED) == 0)
      tableau.decOpenNodes();
    if ((tags & CONTRACTIBLE) != 0)
      operators.clear();
  }
  
  /**
   * Removes the specified tag to this node.
   * @param tag the tag to be remove.
   */
  public void removeTag(int tag) {
    int prev = tags;
    tags &= ~tag;
    if ((tags & SOLVED) == 0 && (prev & SOLVED) != 0)    
      tableau.incOpenNodes();
  }

  /**
   * Add the NOT_EXHAUSTED tag to this node and its ancestors.
   */
  public void markAsNotExhausted() {
    Node node = this;
    while (node != null && !node.hasTag(NOT_EXHAUSTED)) {
      node.addTag(NOT_EXHAUSTED);
      node = node.parent;
    }
  }
  
  /**
   * Returns true if this node has the specified tag.
   * @param tag  the specified tag.
   * @return true if this node has the specified tag.
   */
  public boolean hasTag(int tag) {
    return (tags & tag) != 0;
  }
  
  /**
   * Returns true if this node is solved.
   * @return true if this node is solved.
   */
  public boolean isSolved() {
    return (tags & SOLVED) != 0;
  }
  
  /**
   * Returns true if this node is closed.
   * @return true if this node is closed.
   */
  public boolean isClosed() {
    return (tags & CLOSED) != 0;
  }
  
  /**
   * Returns true if this node is divided.
   * @return true if this node is divided.
   */
  public boolean isDivided() {
    return (tags & DIVIDED) != 0;
  }

  /**
   * Returns true if this node is divided but not completed.
   * @return true if this node is divided but not completed.
   */
  public boolean isDividedAndNotCompleted() {
    return (tags & DIVIDED) != 0 && (tags & DIVISION_COMPLETED) == 0;
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
   * Sets the inference step at which an operation is applied to this node.
   * @param step the inference step.
   */
  public void setInfStep(long step) {
    infStep = step;
  }

  /**
   * Returns the inference step at which an operator is applied to this node.
   * @return the inference step.
   */
  public long getInfStep() {
    return infStep;
  }
  
  /**
   * Clears the inference step at which an operation is applied to this node.
   */
  public void clearInfStep() {
    infStep = 0;
  }
  
  /**
   * Returns true if this node is solvable.
   * @return true if this node is solvable.
   */
  public boolean isSolvable() {
    return (tags & SOLVABLE) != 0;
  }
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (literal == null)
      return "root";
    else
      return literal.toString() + (first ? " 1st" : "");
  }
  
  /** The environment. */
  private Env env = null;
  /** The options. */
  private Options opt = null;
  /** The tableau to which this node belongs. */ 
  private Tableau tableau = null;
  /** The literal of this node. */
  private Literal literal = null;
  /** The original literal of this node. */
  private Literal origin = null;
  /** The parent node. */
  private Node parent = null;
  /** The left node. */
  private Node left = null;
  /** The right node. */
  private Node right = null;
  /** The most left child node. */
  private Node child = null;
  /** The literal that is used to extend this node. */
  private Literal extChild = null; 
  /** The reduction target. */
  private Node reductionTarget = null;
  /** The number of children. */
  private int numChildren = 0;
  /** The depth of this node. */
  private int depth = 0;
  /** The depth for extension operations. */
  private int extDepth = 0;
  /** The tags of this node. */
  private int tags = 0;
  /** The set of applicable operations. */
  private Operators operators = new Operators();
  /** Whether the first visit or not. */
  private boolean first = true;
  /** The maximum variable name at the first solving. */
  private int orgNumVars = 0;
  /** The variable table state at the first solving. */
  private int orgVarState = 0;
  /** The statistics information. */
  private Stats stats = null;
  /** The folding up lemmas. */
  private DiscTree<Node> foldingUps = null;
  /** The folding down lemmas. */
  private DiscTree<Node> foldingDowns = null;
  /** The local failure cache. */
  private LSuccCache succCache = null;
  /** The local failure cache. */
  private LFailCache failCache = null;
  /** The equality type information. */
  private EqType eqType = null;
  /** The information of NEF operator. */
  private NEFInfo nefInfo = null;
  /** The proof of this node. */
  private Proof proof = null;
  /** The inference step at which an operation is applied to this node. */
  private long infStep = 0;

  /** The special node that represents restarting the computation is required. */
  public final static Node restart = new Node();
}
