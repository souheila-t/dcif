%------------------------------------------------------------------------------
% File     : ANA039-1 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    : BigO__bigo_pos_const [Pau06]

% Status   : Unsatisfiable
% Rating   : 0.50 v6.0.0, 0.60 v5.5.0, 0.90 v5.4.0, 0.85 v5.3.0, 0.89 v5.2.0, 0.81 v5.1.0, 0.82 v5.0.0, 0.79 v4.1.0, 0.77 v4.0.1, 0.73 v3.7.0, 0.60 v3.5.0, 0.64 v3.4.0, 0.67 v3.3.0, 0.79 v3.2.0
% Syntax   : Number of clauses     : 1418 (  28 non-Horn; 220 unit;1291 RR)
%            Number of atoms       : 2698 ( 194 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :   82 (   0 propositional; 1-3 arity)
%            Number of functors    :  128 (  20 constant; 0-6 arity)
%            Number of variables   : 2044 ( 241 singleton)
%            Maximal term depth    :    4 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found.
%------------------------------------------------------------------------------
include('Axioms/ANA003-0.ax').
include('Axioms/MSC001-2.ax').
include('Axioms/MSC001-0.ax').
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Oabs__ge__self_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_lessequals(V_a,c_HOL_Oabs(V_a,T_a),T_a) )).

cnf(cls_OrderedGroup_Oabs__ge__zero_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_lessequals(c_0,c_HOL_Oabs(V_a,T_a),T_a) )).

cnf(cls_Orderings_Oorder__class_Oorder__trans_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_lessequals(V_y,V_z,T_a)
    | ~ c_lessequals(V_x,V_y,T_a)
    | c_lessequals(V_x,V_z,T_a) )).

cnf(cls_Ring__and__Field_Opordered__semiring__class_Omult__right__mono_0,axiom,
    ( ~ class_Ring__and__Field_Opordered__semiring(T_a)
    | ~ c_lessequals(V_a,V_b,T_a)
    | ~ c_lessequals(c_0,V_c,T_a)
    | c_lessequals(c_times(V_a,V_c,T_a),c_times(V_b,V_c,T_a),T_a) )).

cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_h(V_U),t_a),c_times(v_c,c_HOL_Oabs(v_f(V_U),t_a),t_a),t_a) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( v_c != c_0 )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(v_h(v_x),t_a),c_times(c_HOL_Oabs(v_c,t_a),c_HOL_Oabs(v_f(v_x),t_a),t_a),t_a) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_a) )).

%------------------------------------------------------------------------------
