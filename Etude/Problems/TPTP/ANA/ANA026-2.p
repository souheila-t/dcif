%------------------------------------------------------------------------------
% File     : ANA026-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.50 v6.1.0, 0.71 v6.0.0, 0.70 v5.5.0, 0.85 v5.3.0, 0.83 v5.2.0, 0.81 v5.1.0, 0.76 v5.0.0, 0.71 v4.1.0, 0.69 v4.0.1, 0.82 v3.7.0, 0.80 v3.5.0, 0.82 v3.4.0, 0.83 v3.3.0, 0.71 v3.2.0
% Syntax   : Number of clauses     :   31 (   1 non-Horn;   3 unit;  18 RR)
%            Number of atoms       :   71 (  10 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :   12 (   0 propositional; 1-3 arity)
%            Number of functors    :   11 (   3 constant; 0-3 arity)
%            Number of variables   :  137 (  99 singleton)
%            Maximal term depth    :    4 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(v_g(V_U),v_k(V_U),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_Orderings_Omax(c_minus(v_f(v_x),v_k(v_x),t_b),c_0,t_b),c_HOL_Oabs(c_minus(v_f(v_x),v_g(v_x),t_b),t_b),t_b) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

cnf(cls_OrderedGroup_Oab__group__add__class_Odiff__minus_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,V_b,T_a) = c_plus(V_a,c_uminus(V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Oabs__ge__zero_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_lessequals(c_0,c_HOL_Oabs(V_a,T_a),T_a) )).

cnf(cls_OrderedGroup_Oabs__minus__cancel_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_HOL_Oabs(c_uminus(V_a,T_a),T_a) = c_HOL_Oabs(V_a,T_a) )).

cnf(cls_OrderedGroup_Oabs__of__nonpos_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | ~ c_lessequals(V_a,c_0,T_a)
    | c_HOL_Oabs(V_a,T_a) = c_uminus(V_a,T_a) )).

cnf(cls_OrderedGroup_Oadd__le__cancel__left_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__semigroup__add__imp__le(T_a)
    | ~ c_lessequals(V_a,V_b,T_a)
    | c_lessequals(c_plus(V_c,V_a,T_a),c_plus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Oadd__le__cancel__right_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__semigroup__add__imp__le(T_a)
    | ~ c_lessequals(c_plus(V_a,V_c,T_a),c_plus(V_b,V_c,T_a),T_a)
    | c_lessequals(V_a,V_b,T_a) )).

cnf(cls_OrderedGroup_Ocomm__monoid__add__class_Oaxioms_0,axiom,
    ( ~ class_OrderedGroup_Ocomm__monoid__add(T_a)
    | c_plus(c_0,V_y,T_a) = V_y )).

cnf(cls_OrderedGroup_Ocompare__rls__10_1,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(c_plus(V_c,V_b,T_a),V_b,T_a) = V_c )).

cnf(cls_OrderedGroup_Ocompare__rls__2_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(V_a,c_minus(V_b,V_c,T_a),T_a) = c_minus(c_plus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__3_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(c_minus(V_a,V_b,T_a),V_c,T_a) = c_minus(c_plus(V_a,V_c,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__4_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(c_minus(V_a,V_b,T_a),V_c,T_a) = c_minus(V_a,c_plus(V_b,V_c,T_a),T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__6_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_less(c_minus(V_a,V_b,T_a),V_c,T_a)
    | c_less(V_a,c_plus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__8_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(V_a,c_plus(V_c,V_b,T_a),T_a)
    | c_lessequals(c_minus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Odiff__self_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,V_a,T_a) = c_0 )).

cnf(cls_OrderedGroup_Ominus__diff__eq_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_uminus(c_minus(V_a,V_b,T_a),T_a) = c_minus(V_b,V_a,T_a) )).

cnf(cls_Orderings_Olinorder__not__le_0,axiom,
    ( ~ class_Orderings_Olinorder(T_a)
    | c_less(V_y,V_x,T_a)
    | c_lessequals(V_x,V_y,T_a) )).

cnf(cls_Orderings_Olinorder__not__le_1,axiom,
    ( ~ class_Orderings_Olinorder(T_a)
    | ~ c_less(V_y,V_x,T_a)
    | ~ c_lessequals(V_x,V_y,T_a) )).

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

cnf(clsrel_LOrder_Ojoin__semilorder_1,axiom,
    ( ~ class_LOrder_Ojoin__semilorder(T)
    | class_Orderings_Oorder(T) )).

cnf(clsrel_OrderedGroup_Olordered__ab__group__abs_1,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T)
    | class_OrderedGroup_Opordered__ab__group__add(T) )).

cnf(clsrel_OrderedGroup_Olordered__ab__group__abs_6,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T)
    | class_OrderedGroup_Opordered__ab__semigroup__add__imp__le(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_23,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Ocomm__monoid__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_33,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Orderings_Olinorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_35,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_LOrder_Ojoin__semilorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_4,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Oab__group__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

%------------------------------------------------------------------------------
