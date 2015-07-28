%--------------------------------------------------------------------------
% File     : ANA005-5 : TPTP v6.1.0. Released v1.0.0.
% Domain   : Analysis
% Problem  : The sum of two continuous functions is continuous
% Version  : [Ble90] axioms : Incomplete.
% English  :

% Refs     : [Ble90] Bledsoe (1990), Challenge Problems in Elementary Calcu
%          : [Ble92] Bledsoe (1992), Email to G. Sutcliffe
% Source   : [Ble92]
% Names    : Problem 5 [Ble90]
%          : LIM+ [Ble90]
%          : p5.lop [SETHEO]

% Status   : Unknown
% Rating   : 1.00 v2.0.0
% Syntax   : Number of clauses     :   24 (   4 non-Horn;   5 unit;  15 RR)
%            Number of atoms       :   48 (   0 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    2 (   0 propositional; 2-2 arity)
%            Number of functors    :   13 (   3 constant; 0-2 arity)
%            Number of variables   :   47 (   0 singleton)
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

%----Properties of minimum.
%----Clause 9
cnf(minimum1,axiom,
    ( ~ less_or_equalish(X,Y)
    | equalish(minimum(X,Y),X) )).

%----Clause 10
cnf(minimum5,axiom,
    ( ~ less_or_equalish(Y,X)
    | equalish(minimum(X,Y),Y) )).

%----Properties of half.
%----Clause 11
cnf(half_plus_half_is_whole,axiom,
    ( equalish(add(half(X),half(X)),X) )).

%----Clause 12
cnf(zero_and_half,axiom,
    ( less_or_equalish(X,n0)
    | ~ less_or_equalish(half(X),n0) )).

%----Properties of add.
%----Clause 13
cnf(add_to_both_sides_of_less_equal1,axiom,
    ( ~ less_or_equalish(X,Y)
    | less_or_equalish(add(X,Z),add(Y,Z)) )).

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

%----Clause 18
cnf(equal_implies_less_or_equal,axiom,
    ( ~ equalish(X,Y)
    | less_or_equalish(X,Y) )).

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
