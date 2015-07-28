%--------------------------------------------------------------------------
% File     : ANA003-1 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : Lemma 1 for the sum of two continuous functions is continuous
% Version  : [MOW76] axioms : Incomplete > Augmented > Complete.
% English  : A lemma formed by adding in some resolvants and taking out
%            the corresponding clauses.

% Refs     : [MOW76] McCharen et al. (1976), Problems and Experiments for a
% Source   : [ANL]
% Names    : prob1.ver2.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.33 v6.1.0, 0.60 v6.0.0, 0.78 v5.5.0, 0.94 v5.4.0, 0.87 v5.3.0, 0.92 v5.2.0, 0.88 v5.1.0, 0.86 v5.0.0, 0.71 v4.1.0, 0.89 v4.0.1, 0.83 v3.3.0, 0.71 v3.2.0, 0.86 v3.1.0, 0.67 v2.6.0, 0.71 v2.5.0, 0.60 v2.4.0, 0.83 v2.3.0, 1.00 v2.0.0
% Syntax   : Number of clauses     :   21 (   0 non-Horn;   7 unit;  16 RR)
%            Number of atoms       :   42 (   5 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   15 (   5 constant; 0-2 arity)
%            Number of variables   :   35 (   0 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : No natural language descriptions are available.
%          : Contributed to the ANL library by Woody Bledsoe.
%--------------------------------------------------------------------------
%----Include limits axioms
include('Axioms/ANA001-0.ax').
%--------------------------------------------------------------------------
%----Theorem clauses
cnf(c_10,negated_conjecture,
    ( ~ less_than(n0,X)
    | less_than(n0,fp31(X)) )).

cnf(c_11,negated_conjecture,
    ( ~ less_than(n0,X)
    | ~ less_than(absolute(add(Y,minus(a))),fp31(X))
    | less_than(absolute(add(f(Y),minus(l1))),X) )).

cnf(c_12,negated_conjecture,
    ( ~ less_than(n0,X)
    | less_than(n0,fp32(X)) )).

cnf(c_13,negated_conjecture,
    ( ~ less_than(n0,X)
    | ~ less_than(absolute(add(Y,minus(a))),fp32(X))
    | less_than(absolute(add(g(Y),minus(l2))),X) )).

cnf(c_14,negated_conjecture,
    ( less_than(n0,b) )).

cnf(c_15,negated_conjecture,
    ( ~ less_than(n0,X)
    | less_than(absolute(add(fp33(X),minus(a))),X) )).

cnf(c_16,negated_conjecture,
    ( ~ less_than(n0,X)
    | ~ less_than(add(absolute(add(f(fp33(X)),minus(l1))),absolute(add(g(fp33(X)),minus(l2)))),b) )).

%--------------------------------------------------------------------------
