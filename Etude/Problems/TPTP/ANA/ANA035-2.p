%------------------------------------------------------------------------------
% File     : ANA035-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.11 v5.5.0, 0.19 v5.4.0, 0.27 v5.3.0, 0.42 v5.2.0, 0.12 v5.1.0, 0.00 v4.1.0, 0.22 v4.0.1, 0.17 v3.3.0, 0.14 v3.2.0
% Syntax   : Number of clauses     :    6 (   0 non-Horn;   2 unit;   3 RR)
%            Number of atoms       :   10 (   4 equality)
%            Maximal clause size   :    2 (   2 average)
%            Number of predicates  :    3 (   0 propositional; 1-2 arity)
%            Number of functors    :    8 (   4 constant; 0-3 arity)
%            Number of variables   :   15 (   5 singleton)
%            Maximal term depth    :    5 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_4,negated_conjecture,
    ( c_times(c_times(v_c,v_ca,t_b),c_HOL_Oabs(c_times(v_f(v_x),v_g(v_x),t_b),t_b),t_b) != c_times(c_times(v_c,c_HOL_Oabs(v_f(v_x),t_b),t_b),c_times(v_ca,c_HOL_Oabs(v_g(v_x),t_b),t_b),t_b) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

cnf(cls_OrderedGroup_Omult__ac__2_0,axiom,
    ( ~ class_OrderedGroup_Oab__semigroup__mult(T_a)
    | c_times(V_a,V_b,T_a) = c_times(V_b,V_a,T_a) )).

cnf(cls_OrderedGroup_Omult__ac__3_0,axiom,
    ( ~ class_OrderedGroup_Oab__semigroup__mult(T_a)
    | c_times(V_a,c_times(V_b,V_c,T_a),T_a) = c_times(V_b,c_times(V_a,V_c,T_a),T_a) )).

cnf(cls_Ring__and__Field_Oabs__mult_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T_a)
    | c_HOL_Oabs(c_times(V_a,V_b,T_a),T_a) = c_times(c_HOL_Oabs(V_a,T_a),c_HOL_Oabs(V_b,T_a),T_a) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_17,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Oab__semigroup__mult(T) )).

%------------------------------------------------------------------------------
