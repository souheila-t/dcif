%--------------------------------------------------------------------------
% File     : ANA005-4 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : The sum of two continuous functions is continuous
% Version  : [Ble90] axioms : Incomplete.
% English  : A lemma formed by adding in some resolvants and taking out
%            the corresponding clauses.

% Refs     : [Ble90] Bledsoe (1990), Challenge Problems in Elementary Calcu
%          : [Ble92] Bledsoe (1992), Email to G. Sutcliffe
% Source   : [Ble92]
% Names    : Problem 4 [Ble90]
%          : p4.lop [SETHEO]

% Status   : Unknown
% Rating   : 1.00 v2.0.0
% Syntax   : Number of clauses     :   24 (   4 non-Horn;   6 unit;  15 RR)
%            Number of atoms       :   48 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   13 (   3 constant; 0-2 arity)
%            Number of variables   :   48 (   2 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNK_NUE

% Comments : Based on the theorem in calculus that the sum of two continuous
%            functions is continuous.
%          : [TUM] provided some input to this problem.
%--------------------------------------------------------------------------
%----|X + Y| <= |X| + |Y|.
%----Clause 8
cnf(absolute_sum_less_or_equal_sum_of_absolutes1,axiom,
    ( less_or_equalish(absolute(add(X,Y)),add(absolute(X),absolute(Y))) )).

%----Clause 9.1
cnf(minimum2,axiom,
    ( less_or_equalish(minimum(X,Y),X) )).

%----Clause 9.2
cnf(minimum4,axiom,
    ( ~ less_or_equalish(X,Y)
    | less_or_equalish(X,minimum(X,Y)) )).

%----Clause 10.1
cnf(minimum6,axiom,
    ( less_or_equalish(minimum(X,Y),Y) )).

%----Clause 10.2
cnf(minimum8,axiom,
    ( ~ less_or_equalish(Y,X)
    | less_or_equalish(Y,minimum(X,Y)) )).

%----Clause 11.3
cnf(less_or_equal_sum_of_halves,axiom,
    ( ~ less_or_equalish(X,half(Z))
    | ~ less_or_equalish(Y,half(Z))
    | less_or_equalish(add(X,Y),Z) )).

%----Clause 12
cnf(zero_and_half,axiom,
    ( less_or_equalish(X,n0)
    | ~ less_or_equalish(half(X),n0) )).

%----Clause 14
cnf(commutativity_of_less_or_equal,axiom,
    ( less_or_equalish(X,Y)
    | less_or_equalish(Y,X) )).

%----Clause 15
cnf(transitivity_of_less_or_equal,axiom,
    ( ~ less_or_equalish(X,Y)
    | ~ less_or_equalish(Y,Z)
    | less_or_equalish(X,Z) )).

%----Clause 15.1 omitted - it's the same as Clause 15

%----Clause 16
cnf(commutativity_of_add,axiom,
    ( equalish(add(X,Y),add(Y,X)) )).

%----Clause 17
cnf(associativity_of_add,axiom,
    ( equalish(add(add(X,Y),Z),add(X,add(Y,Z))) )).

%----Clause 20 = symmetry
cnf(symmetry,axiom,
    ( ~ equalish(X,Y)
    | equalish(Y,X) )).

%----Clause 21 = transitivity
cnf(transitivity,axiom,
    ( ~ equalish(X,Y)
    | ~ equalish(Y,Z)
    | equalish(X,Z) )).

%----Clause 22
cnf(less_or_equal_substitution1,axiom,
    ( ~ equalish(X,Z)
    | ~ less_or_equalish(X,Y)
    | less_or_equalish(Z,Y) )).

%----Clause 24
cnf(absolute_substitution,axiom,
    ( ~ equalish(X,Z)
    | equalish(absolute(X),absolute(Z)) )).

%----Clause 25
cnf(add_substitution1,axiom,
    ( ~ equalish(X,Z)
    | equalish(add(X,Y),add(Z,Y)) )).

%----Clause 26
cnf(add_substitution2,axiom,
    ( ~ equalish(Y,Z)
    | equalish(add(X,Y),add(X,Z)) )).

%----Clause 1
cnf(clause_1,hypothesis,
    ( less_or_equalish(Epsilon,n0)
    | ~ less_or_equalish(delta_1(Epsilon),n0) )).

%----Clause 2
cnf(clause_2,hypothesis,
    ( less_or_equalish(Epsilon,n0)
    | ~ less_or_equalish(delta_2(Epsilon),n0) )).

%----Clause 3
cnf(clause_3,hypothesis,
    ( less_or_equalish(Epsilon,n0)
    | ~ less_or_equalish(absolute(add(Z,negate(a_real_number))),delta_1(Epsilon))
    | less_or_equalish(absolute(add(f(Z),negate(f(a_real_number)))),Epsilon) )).

%----Clause 4
cnf(clause_4,hypothesis,
    ( less_or_equalish(Epsilon,n0)
    | ~ less_or_equalish(absolute(add(Z,negate(a_real_number))),delta_2(Epsilon))
    | less_or_equalish(absolute(add(g(Z),negate(g(a_real_number)))),Epsilon) )).

%----Clause 5
cnf(clause_5,hypothesis,
    ( ~ less_or_equalish(epsilon_0,n0) )).

%----Clause 6
cnf(clause_6,hypothesis,
    ( less_or_equalish(Delta,n0)
    | less_or_equalish(absolute(add(xs(Delta),negate(a_real_number))),Delta) )).

%----Clause 7
cnf(clause_7,negated_conjecture,
    ( less_or_equalish(Delta,n0)
    | ~ less_or_equalish(absolute(add(add(f(xs(Delta)),g(xs(Delta))),add(negate(f(a_real_number)),negate(g(a_real_number))))),epsilon_0) )).

%--------------------------------------------------------------------------
