%------------------------------------------------------------------------------
% File     : ANA030-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.22 v5.5.0, 0.50 v5.4.0, 0.53 v5.3.0, 0.67 v5.2.0, 0.25 v5.1.0, 0.29 v4.1.0, 0.33 v3.7.0, 0.17 v3.3.0, 0.29 v3.2.0
% Syntax   : Number of clauses     :   16 (   0 non-Horn;   3 unit;   9 RR)
%            Number of atoms       :   32 (   6 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    7 (   0 propositional; 1-3 arity)
%            Number of functors    :   13 (   3 constant; 0-3 arity)
%            Number of variables   :   55 (  33 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(c_Orderings_Omax(c_minus(v_f(V_U),v_g(V_U),t_b),c_0,t_b),t_b),c_times(v_c,c_HOL_Oabs(v_h(V_U),t_b),t_b),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(v_f(v_x(V_U)),c_plus(v_g(v_x(V_U)),c_times(V_U,c_HOL_Oabs(v_h(v_x(V_U)),t_b),t_b),t_b),t_b) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

cnf(cls_OrderedGroup_Oabs__of__nonneg_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | ~ c_lessequals(c_0,V_y,T_a)
    | c_HOL_Oabs(V_y,T_a) = V_y )).

cnf(cls_OrderedGroup_Ocompare__rls__1_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(V_a,c_uminus(V_b,T_a),T_a) = c_minus(V_a,V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__8_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(c_minus(V_a,V_b,T_a),V_c,T_a)
    | c_lessequals(V_a,c_plus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Odiff__minus__eq__add_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,c_uminus(V_b,T_a),T_a) = c_plus(V_a,V_b,T_a) )).

cnf(cls_OrderedGroup_Ominus__add__distrib_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_uminus(c_plus(V_a,V_b,T_a),T_a) = c_plus(c_uminus(V_a,T_a),c_uminus(V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Ominus__diff__eq_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_uminus(c_minus(V_a,V_b,T_a),T_a) = c_minus(V_b,V_a,T_a) )).

cnf(cls_OrderedGroup_Ominus__minus_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_uminus(c_uminus(V_y,T_a),T_a) = V_y )).

cnf(cls_Orderings_Ole__maxI2_0,axiom,
    ( ~ class_Orderings_Olinorder(T_b)
    | c_lessequals(V_y,c_Orderings_Omax(V_x,V_y,T_b),T_b) )).

cnf(cls_Orderings_Omin__max_Obelow__sup_Oabove__sup__conv_0,axiom,
    ( ~ class_Orderings_Olinorder(T_b)
    | ~ c_lessequals(c_Orderings_Omax(V_x,V_y,T_b),V_z,T_b)
    | c_lessequals(V_x,V_z,T_b) )).

cnf(clsrel_OrderedGroup_Olordered__ab__group__abs_1,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T)
    | class_OrderedGroup_Opordered__ab__group__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_33,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Orderings_Olinorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_4,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Oab__group__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

%------------------------------------------------------------------------------
