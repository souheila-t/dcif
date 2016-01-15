%------------------------------------------------------------------------------
% File     : ANA021-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.10 v6.1.0, 0.14 v6.0.0, 0.10 v5.5.0, 0.20 v5.3.0, 0.28 v5.2.0, 0.12 v5.1.0, 0.18 v5.0.0, 0.14 v4.1.0, 0.23 v4.0.1, 0.27 v3.7.0, 0.00 v3.5.0, 0.18 v3.4.0, 0.17 v3.3.0, 0.21 v3.2.0
% Syntax   : Number of clauses     :    5 (   1 non-Horn;   3 unit;   3 RR)
%            Number of atoms       :    7 (   3 equality)
%            Maximal clause size   :    2 (   1 average)
%            Number of predicates  :    3 (   0 propositional; 2-3 arity)
%            Number of functors    :   12 (   6 constant; 0-3 arity)
%            Number of variables   :    5 (   3 singleton)
%            Maximal term depth    :    5 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_NatBin_OSuc__pred_H_0,axiom,
    ( ~ c_less(c_0,V_x,tc_nat)
    | V_x = c_Suc(c_minus(V_x,c_1,tc_nat)) )).

cnf(cls_Nat_Onot__gr0_0,axiom,
    ( c_less(c_0,V_n,tc_nat)
    | V_n = c_0 )).

cnf(cls_conjecture_2,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_f(c_Suc(V_U)),t_a),c_times(v_c,c_HOL_Oabs(v_h(c_Suc(V_U)),t_a),t_a),t_a) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( v_x != c_0 )).

cnf(cls_conjecture_4,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(v_f(v_x),t_a),c_times(v_c,c_HOL_Oabs(v_h(v_x),t_a),t_a),t_a) )).

%------------------------------------------------------------------------------
