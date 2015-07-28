%--------------------------------------------------------------------------
% File     : ANA003-4 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : Lemma 1 for the sum of two continuous functions is continuous
% Version  : [Ble90] axioms : Incomplete.
% English  : A lemma formed by adding in some resolvants and taking out
%            the corresponding clauses.

% Refs     : [Ble90] Bledsoe (1990), Challenge Problems in Elementary Calcu
%          : [LM92]  Lusk & McCune (1992), Experiments with ROO, a Parallel
%          : [Ble92] Bledsoe (1992), Email to G. Sutcliffe
% Source   : [Ble92]
% Names    : Problem 1 [Ble90]
%          : Bledsoe-P1 [LM92]
%          : p1.lop [SETHEO]

% Status   : Unsatisfiable
% Rating   : 0.33 v6.1.0, 0.29 v5.5.0, 0.38 v5.4.0, 0.50 v5.2.0, 0.30 v5.1.0, 0.27 v5.0.0, 0.50 v4.1.0, 0.38 v4.0.1, 0.40 v4.0.0, 0.43 v3.4.0, 0.50 v3.3.0, 0.33 v2.7.0, 0.12 v2.6.0, 0.33 v2.5.0, 0.00 v2.4.0, 0.20 v2.3.0, 0.00 v2.2.1, 0.25 v2.1.0, 0.37 v2.0.0
% Syntax   : Number of clauses     :   12 (   4 non-Horn;   1 unit;  11 RR)
%            Number of atoms       :   27 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    1 (   0 propositional; 2-2 arity)
%            Number of functors    :   13 (   3 constant; 0-2 arity)
%            Number of variables   :   20 (   2 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : Based on the theorem in calculus that the sum of two continuous
%            functions is continuous.
%          : [TUM] provided some input to this problem.
%--------------------------------------------------------------------------
%----Clause 9.11
cnf(minimum3,axiom,
    ( ~ less_or_equal(Z,minimum(X,Y))
    | less_or_equal(Z,X) )).

%----Clause 10.11
cnf(minimum7,axiom,
    ( ~ less_or_equal(Z,minimum(X,Y))
    | less_or_equal(Z,Y) )).

%----Clause 10.3
cnf(minimum9,axiom,
    ( less_or_equal(X,n0)
    | less_or_equal(Y,n0)
    | ~ less_or_equal(minimum(X,Y),n0) )).

%----Clause 11.3
cnf(less_or_equal_sum_of_halves,axiom,
    ( ~ less_or_equal(X,half(Z))
    | ~ less_or_equal(Y,half(Z))
    | less_or_equal(add(X,Y),Z) )).

%----Clause 12
cnf(zero_and_half,axiom,
    ( less_or_equal(X,n0)
    | ~ less_or_equal(half(X),n0) )).

%----Clause 1
cnf(clause_1,hypothesis,
    ( less_or_equal(Epsilon,n0)
    | ~ less_or_equal(delta_1(Epsilon),n0) )).

%----Clause 2
cnf(clause_2,hypothesis,
    ( less_or_equal(Epsilon,n0)
    | ~ less_or_equal(delta_2(Epsilon),n0) )).

%----Clause 3
cnf(clause_3,hypothesis,
    ( less_or_equal(Epsilon,n0)
    | ~ less_or_equal(absolute(add(Z,negate(a_real_number))),delta_1(Epsilon))
    | less_or_equal(absolute(add(f(Z),negate(f(a_real_number)))),Epsilon) )).

%----Clause 4
cnf(clause_4,hypothesis,
    ( less_or_equal(Epsilon,n0)
    | ~ less_or_equal(absolute(add(Z,negate(a_real_number))),delta_2(Epsilon))
    | less_or_equal(absolute(add(g(Z),negate(g(a_real_number)))),Epsilon) )).

%----Clause 5
cnf(clause_5,hypothesis,
    ( ~ less_or_equal(epsilon_0,n0) )).

%----Clause 6
cnf(clause_6,hypothesis,
    ( less_or_equal(Delta,n0)
    | less_or_equal(absolute(add(xs(Delta),negate(a_real_number))),Delta) )).

%----Clause 7_2
cnf(clause_7_2,negated_conjecture,
    ( less_or_equal(Delta,n0)
    | ~ less_or_equal(add(absolute(add(f(xs(Delta)),negate(f(a_real_number)))),absolute(add(g(xs(Delta)),negate(g(a_real_number))))),epsilon_0) )).

%--------------------------------------------------------------------------
