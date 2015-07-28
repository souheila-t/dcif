%--------------------------------------------------------------------------
% File     : ANA003-2 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : Lemma 1 for the sum of two continuous functions is continuous
% Version  : [MOW76] axioms : Incomplete.
% English  : A lemma formed by adding in some resolvants and taking out
%            the corresponding clauses.

% Refs     : [MOW76] McCharen et al. (1976), Problems and Experiments for a
% Source   : [ANL]
% Names    : BL1 [MOW76]
%          : prob1.ver1.in [ANL]

% Status   : Unsatisfiable
% Rating   : 0.67 v6.1.0, 0.86 v6.0.0, 0.78 v5.5.0, 0.88 v5.4.0, 0.94 v5.3.0, 0.90 v5.2.0, 0.69 v5.1.0, 0.75 v5.0.0, 0.73 v4.0.1, 0.71 v3.4.0, 0.60 v3.3.0, 0.33 v3.1.0, 0.67 v2.7.0, 0.50 v2.6.0, 0.43 v2.5.0, 0.57 v2.4.0, 0.86 v2.3.0, 1.00 v2.0.0
% Syntax   : Number of clauses     :   17 (   0 non-Horn;   4 unit;  15 RR)
%            Number of atoms       :   37 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   15 (   5 constant; 0-2 arity)
%            Number of variables   :   27 (   0 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_HRN

% Comments : No natural language descriptions are available.
%          : Contributed to the ANL library by Woody Bledsoe.
%          : equalish/2 is pure, but that's how it is in the source.
%--------------------------------------------------------------------------
%----Axiom 1
cnf(right_identity,axiom,
    ( equalish(add(X,n0),X) )).

cnf(left_identity,axiom,
    ( equalish(add(n0,X),X) )).

cnf(reflexivity_of_less_than,axiom,
    ( ~ less_than(X,X) )).

cnf(transitivity_of_less_than,axiom,
    ( ~ less_than(X,Y)
    | ~ less_than(Y,Z)
    | less_than(X,Z) )).

%----Axiom 2
cnf(axiom_2_1,axiom,
    ( ~ less_than(n0,X)
    | ~ less_than(n0,Y)
    | less_than(n0,minimum(X,Y)) )).

cnf(axiom_2_2,axiom,
    ( ~ less_than(n0,X)
    | ~ less_than(n0,Y)
    | less_than(minimum(X,Y),X) )).

cnf(axiom_2_3,axiom,
    ( ~ less_than(n0,X)
    | ~ less_than(n0,Y)
    | less_than(minimum(X,Y),Y) )).

%----Axiom 3
cnf(axiom_3,axiom,
    ( ~ less_than(X,half(Xa))
    | ~ less_than(Y,half(Xa))
    | less_than(add(X,Y),Xa) )).

%----Axiom 4
cnf(c_17,axiom,
    ( ~ less_than(add(absolute(X),absolute(Y)),Xa)
    | less_than(absolute(add(X,Y)),Xa) )).

%----Axiom 7
cnf(axiom_7,axiom,
    ( ~ less_than(n0,Xa)
    | less_than(n0,half(Xa)) )).

%----Clauses from the problem.
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
