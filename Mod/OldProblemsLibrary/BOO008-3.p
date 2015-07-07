%--------------------------------------------------------------------------
% File     : BOO008-3 : TPTP v5.0.0. Released v1.0.0.
% Domain   : Boolean Algebra
% Problem  : Sum is associative ( (X + Y) + Z = X + (Y + Z) )
% Version  : [MOW76] axioms : Reduced > Incomplete.
% English  :

% Refs     :
% Source   : [OTTER]
% Names    : bool_ass.in [OTTER]
%          : bool.in [OTTER]

% Status   : Satisfiable
% Rating   : 0.00 v3.2.0, 0.40 v3.1.0, 0.00 v2.6.0, 0.43 v2.5.0, 0.33 v2.4.0, 0.00 v2.2.1, 0.50 v2.2.0, 0.33 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   21 (   0 non-Horn;  15 unit;  11 RR)
%            Number of atoms       :   39 (   1 equality)
%            Maximal clause size   :    5 (   2 average)
%            Number of predicates  :    3 (   0 propositional; 2-3 arity)
%            Number of functors    :   12 (   9 constant; 0-2 arity)
%            Number of variables   :   46 (   0 singleton)
%            Maximal term depth    :    2 (   1 average)
% SPC      : CNF_SAT_RFO_EQU_NUE

% Comments :
%--------------------------------------------------------------------------
%----Omit the Boolean algebra axioms, add the used ones manually
% include('axioms/BOO002-0.ax').
%--------------------------------------------------------------------------
cnf(closure_of_addition,axiom,
    ( sum(X,Y,add(X,Y)) )).

cnf(closure_of_multiplication,axiom,
    ( product(X,Y,multiply(X,Y)) )).

cnf(commutativity_of_addition,axiom,
    ( ~ sum(X,Y,Z)
    | sum(Y,X,Z) )).

cnf(commutativity_of_multiplication,axiom,
    ( ~ product(X,Y,Z)
    | product(Y,X,Z) )).

cnf(additive_identity1,axiom,
    ( sum(additive_identity,X,X) )).

cnf(additive_identity2,axiom,
    ( sum(X,additive_identity,X) )).

cnf(multiplicative_identity1,axiom,
    ( sum(multiplicative_identity,X,X) )).

cnf(multiplicative_identity2,axiom,
    ( sum(X,multiplicative_identity,X) )).

cnf(distributivity1,axiom,
    ( ~ product(X,Y,V1)
    | ~ product(X,Z,V2)
    | ~ sum(Y,Z,V3)
    | ~ product(X,V3,V4)
    | sum(V1,V2,V4) )).

cnf(distributivity2,axiom,
    ( ~ product(X,Y,V1)
    | ~ product(X,Z,V2)
    | ~ sum(Y,Z,V3)
    | ~ sum(V1,V2,V4)
    | product(X,V3,V4) )).

% input_clause(distributivity3,axiom,
%     [--product(Y,X,V1),
%      --product(Z,X,V2),
%      --sum(Y,Z,V3),
%      --product(V3,X,V4),
%      ++sum(V1,V2,V4)]).

% input_clause(distributivity4,axiom,
%     [--product(Y,X,V1),
%      --product(Z,X,V2),
%      --sum(Y,Z,V3),
%      --sum(V1,V2,V4),
%      ++product(V3,X,V4)]).

cnf(distributivity5,axiom,
    ( ~ sum(X,Y,V1)
    | ~ sum(X,Z,V2)
    | ~ product(Y,Z,V3)
    | ~ sum(X,V3,V4)
    | product(V1,V2,V4) )).

cnf(distributivity6,axiom,
    ( ~ sum(X,Y,V1)
    | ~ sum(X,Z,V2)
    | ~ product(Y,Z,V3)
    | ~ product(V1,V2,V4)
    | sum(X,V3,V4) )).

% input_clause(distributivity7,axiom,
%     [--sum(Y,X,V1),
%      --sum(Z,X,V2),
%      --product(Y,Z,V3),
%      --sum(V3,X,V4),
%      ++product(V1,V2,V4)]).

% input_clause(distributivity8,axiom,
%     [--sum(Y,X,V1),
%      --sum(Z,X,V2),
%      --product(Y,Z,V3),
%      --product(V1,V2,V4),
%      ++sum(V3,X,V4)]).

cnf(additive_inverse1,axiom,
    ( sum(inverse(X),X,multiplicative_identity) )).

cnf(additive_inverse2,axiom,
    ( sum(X,inverse(X),multiplicative_identity) )).

cnf(multiplicative_inverse1,axiom,
    ( product(inverse(X),X,additive_identity) )).

cnf(multiplicative_inverse2,axiom,
    ( product(X,inverse(X),additive_identity) )).

cnf(y_plus_z,hypothesis,
    ( sum(y,z,y_plus_z) )).

cnf(x_plus__y_plus_z,hypothesis,
    ( sum(x,y_plus_z,x__plus_y_plus_z) )).

cnf(x_plus_y,hypothesis,
    ( sum(x,y,x_plus_y) )).

cnf(x_plus_y__plus_z,hypothesis,
    ( sum(x_plus_y,z,x_plus_y__plus_z) )).

cnf(prove_equality,negated_conjecture,
    (  x__plus_y_plus_z != x_plus_y__plus_z )).

%--------------------------------------------------------------------------
