%------------------------------------------------------------------------------
% File     : ANA010-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.08 v5.2.0, 0.00 v5.0.0, 0.14 v4.1.0, 0.11 v4.0.1, 0.17 v3.3.0, 0.29 v3.2.0
% Syntax   : Number of clauses     :    9 (   0 non-Horn;   4 unit;   6 RR)
%            Number of atoms       :   17 (   2 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-3 arity)
%            Number of functors    :    8 (   3 constant; 0-3 arity)
%            Number of variables   :   26 (  20 singleton)
%            Maximal term depth    :    5 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Oabs__of__nonneg_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | ~ c_lessequals(c_0,V_y,T_a)
    | c_HOL_Oabs(V_y,T_a) = V_y )).

cnf(cls_Orderings_Oorder__class_Oorder__trans_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_lessequals(V_y,V_z,T_a)
    | ~ c_lessequals(V_x,V_y,T_a)
    | c_lessequals(V_x,V_z,T_a) )).

cnf(cls_Ring__and__Field_Oabs__mult_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T_a)
    | c_HOL_Oabs(c_times(V_a,V_b,T_a),T_a) = c_times(c_HOL_Oabs(V_a,T_a),c_HOL_Oabs(V_b,T_a),T_a) )).

cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_0,v_f(V_U),t_b) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(v_f(V_U),c_times(v_c,v_g(V_U),t_b),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(v_f(v_x(V_U)),c_times(V_U,c_HOL_Oabs(v_g(v_x(V_U)),t_b),t_b),t_b) )).

cnf(clsrel_OrderedGroup_Olordered__ab__group__abs_17,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T)
    | class_Orderings_Oorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
