%------------------------------------------------------------------------------
% File     : ANA025-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.14 v5.5.0, 0.12 v5.4.0, 0.10 v5.1.0, 0.09 v5.0.0, 0.07 v4.1.0, 0.12 v4.0.1, 0.00 v4.0.0, 0.14 v3.4.0, 0.00 v3.2.0
% Syntax   : Number of clauses     :   12 (   1 non-Horn;   3 unit;  10 RR)
%            Number of atoms       :   27 (   0 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    7 (   0 propositional; 1-3 arity)
%            Number of functors    :    9 (   3 constant; 0-3 arity)
%            Number of variables   :   46 (  44 singleton)
%            Maximal term depth    :    4 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Oabs__ge__zero_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_lessequals(c_0,c_HOL_Oabs(V_a,T_a),T_a) )).

cnf(cls_Orderings_Olinorder__not__le_0,axiom,
    ( ~ class_Orderings_Olinorder(T_a)
    | c_less(V_y,V_x,T_a)
    | c_lessequals(V_x,V_y,T_a) )).

cnf(cls_Orderings_Omin__max_Obelow__sup_Oabove__sup__conv_2,axiom,
    ( ~ class_Orderings_Olinorder(T_b)
    | ~ c_lessequals(V_y,V_z,T_b)
    | ~ c_lessequals(V_x,V_z,T_b)
    | c_lessequals(c_Orderings_Omax(V_x,V_y,T_b),V_z,T_b) )).

cnf(cls_Orderings_Oorder__class_Oorder__trans_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_lessequals(V_y,V_z,T_a)
    | ~ c_lessequals(V_x,V_y,T_a)
    | c_lessequals(V_x,V_z,T_a) )).

cnf(cls_Orderings_Oorder__less__imp__le_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_less(V_x,V_y,T_a)
    | c_lessequals(V_x,V_y,T_a) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_0,c_minus(v_k(v_x),v_g(v_x),t_b),t_b) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( ~ c_lessequals(c_Orderings_Omax(c_minus(v_k(v_x),v_g(v_x),t_b),c_0,t_b),c_HOL_Oabs(c_minus(v_f(v_x),v_g(v_x),t_b),t_b),t_b) )).

cnf(clsrel_LOrder_Ojoin__semilorder_1,axiom,
    ( ~ class_LOrder_Ojoin__semilorder(T)
    | class_Orderings_Oorder(T) )).

cnf(clsrel_OrderedGroup_Olordered__ab__group__abs_15,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T)
    | class_LOrder_Ojoin__semilorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_33,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Orderings_Olinorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
