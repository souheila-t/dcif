%--------------------------------------------------------------------------
% File     : ANA004-3 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : Lemma 2 for the sum of two continuous functions is continuous
% Version  : [Ble90] axioms : Incomplete > Augmented > Complete.
% English  : A lemma formed by adding in some resolvants and taking out
%            the corresponding clauses.

% Refs     : [Ble90] Bledsoe (1990), Challenge Problems in Elementary Calcu
%          : [Ble92] Bledsoe (1992), Email to G. Sutcliffe
% Source   : [TPTP]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.90 v6.1.0, 1.00 v5.0.0, 0.93 v4.1.0, 1.00 v4.0.0, 0.91 v3.7.0, 0.90 v3.5.0, 0.91 v3.4.0, 0.92 v3.3.0, 1.00 v2.0.0
% Syntax   : Number of clauses     :   33 (   5 non-Horn;  12 unit;  19 RR)
%            Number of atoms       :   60 (   6 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   13 (   3 constant; 0-2 arity)
%            Number of variables   :   67 (   4 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : Based on the theorem in calculus that the sum of two continuous
%            functions is continuous.
%          : [TUM] provided some input to this problem.
%--------------------------------------------------------------------------
%----Include axioms for limits
include('Axioms/ANA002-0.ax').
%--------------------------------------------------------------------------
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

%----Clause 7_1
cnf(clause_7_1,negated_conjecture,
    ( less_or_equal(Delta,n0)
    | ~ less_or_equal(absolute(add(add(f(xs(Delta)),negate(f(a_real_number))),add(g(xs(Delta)),negate(g(a_real_number))))),epsilon_0) )).

%--------------------------------------------------------------------------
