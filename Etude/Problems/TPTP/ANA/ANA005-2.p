%--------------------------------------------------------------------------
% File     : ANA005-2 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : The sum of two continuous functions is continuous
% Version  : [MOW76] axioms : Incomplete.
% English  :

% Refs     : [MOW76] McCharen et al. (1976), Problems and Experiments for a
% Source   : [ANL]
% Names    : prob2.ver1.in [ANL]

% Status   : Unknown
% Rating   : 1.00 v2.0.0
% Syntax   : Number of clauses     :   16 (   0 non-Horn;   4 unit;  14 RR)
%            Number of atoms       :   35 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   15 (   5 constant; 0-2 arity)
%            Number of variables   :   24 (   0 singleton)
%            Maximal term depth    :    6 (   1 average)
% SPC      : CNF_UNK_NUE

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

%----Less than transitivity
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

%----Axiom 7
cnf(axiom_7,axiom,
    ( ~ less_than(n0,Xa)
    | less_than(n0,half(Xa)) )).

%----Clauses from the theorem
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
    | ~ less_than(absolute(add(add(f(fp33(X)),g(fp33(X))),minus(add(l1,l2)))),b) )).

%--------------------------------------------------------------------------
