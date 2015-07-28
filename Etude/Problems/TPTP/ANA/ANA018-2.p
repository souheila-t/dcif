%------------------------------------------------------------------------------
% File     : ANA018-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.08 v5.2.0, 0.00 v5.0.0, 0.14 v4.1.0, 0.11 v4.0.1, 0.17 v3.3.0, 0.14 v3.2.0
% Syntax   : Number of clauses     :   13 (   0 non-Horn;   5 unit;  10 RR)
%            Number of atoms       :   26 (   1 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    8 (   0 propositional; 1-3 arity)
%            Number of functors    :   10 (   5 constant; 0-3 arity)
%            Number of variables   :   42 (  34 singleton)
%            Maximal term depth    :    4 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_f(V_U),t_b),c_times(v_c,c_HOL_Oabs(v_g(V_U),t_b),t_b),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( c_less(c_0,v_ca,t_b) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_x(V_U),t_b),c_times(v_ca,c_HOL_Oabs(v_f(V_U),t_b),t_b),t_b) )).

cnf(cls_conjecture_4,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(v_x(v_xa),t_b),c_times(c_times(v_ca,v_c,t_b),c_HOL_Oabs(v_g(v_xa),t_b),t_b),t_b) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

cnf(cls_OrderedGroup_Omult__ac__1_0,axiom,
    ( ~ class_OrderedGroup_Osemigroup__mult(T_a)
    | c_times(c_times(V_a,V_b,T_a),V_c,T_a) = c_times(V_a,c_times(V_b,V_c,T_a),T_a) )).

cnf(cls_Orderings_Oorder__class_Oorder__trans_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_lessequals(V_y,V_z,T_a)
    | ~ c_lessequals(V_x,V_y,T_a)
    | c_lessequals(V_x,V_z,T_a) )).

cnf(cls_Orderings_Oorder__less__imp__le_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_less(V_x,V_y,T_a)
    | c_lessequals(V_x,V_y,T_a) )).

cnf(cls_Ring__and__Field_Opordered__semiring__class_Omult__left__mono_0,axiom,
    ( ~ class_Ring__and__Field_Opordered__semiring(T_a)
    | ~ c_lessequals(V_a,V_b,T_a)
    | ~ c_lessequals(c_0,V_c,T_a)
    | c_lessequals(c_times(V_c,V_a,T_a),c_times(V_c,V_b,T_a),T_a) )).

cnf(clsrel_LOrder_Ojoin__semilorder_1,axiom,
    ( ~ class_LOrder_Ojoin__semilorder(T)
    | class_Orderings_Oorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_21,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Osemigroup__mult(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_35,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_LOrder_Ojoin__semilorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_42,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Ring__and__Field_Opordered__semiring(T) )).

%------------------------------------------------------------------------------
