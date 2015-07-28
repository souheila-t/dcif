%------------------------------------------------------------------------------
% File     : ANA039-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.1.0, 0.07 v6.0.0, 0.00 v5.5.0, 0.06 v5.4.0, 0.11 v5.3.0, 0.15 v5.2.0, 0.00 v5.1.0, 0.06 v5.0.0, 0.07 v4.0.1, 0.00 v3.2.0
% Syntax   : Number of clauses     :   10 (   0 non-Horn;   3 unit;   7 RR)
%            Number of atoms       :   21 (   0 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-3 arity)
%            Number of functors    :    8 (   4 constant; 0-3 arity)
%            Number of variables   :   33 (  27 singleton)
%            Maximal term depth    :    4 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_h(V_U),t_a),c_times(v_c,c_HOL_Oabs(v_f(V_U),t_a),t_a),t_a) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(v_h(v_x),t_a),c_times(c_HOL_Oabs(v_c,t_a),c_HOL_Oabs(v_f(v_x),t_a),t_a),t_a) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_a) )).

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

cnf(clsrel_Ring__and__Field_Oordered__idom_42,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Ring__and__Field_Opordered__semiring(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_44,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Orderings_Oorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

%------------------------------------------------------------------------------
