%------------------------------------------------------------------------------
% File     : ANA028-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.22 v5.5.0, 0.38 v5.4.0, 0.33 v5.3.0, 0.58 v5.2.0, 0.25 v5.1.0, 0.29 v4.1.0, 0.33 v3.7.0, 0.17 v3.3.0, 0.29 v3.2.0
% Syntax   : Number of clauses     :   13 (   0 non-Horn;   3 unit;   7 RR)
%            Number of atoms       :   25 (   5 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    6 (   0 propositional; 1-3 arity)
%            Number of functors    :    8 (   3 constant; 0-3 arity)
%            Number of variables   :   46 (  27 singleton)
%            Maximal term depth    :    3 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Ocomm__monoid__add__class_Oaxioms_0,axiom,
    ( ~ class_OrderedGroup_Ocomm__monoid__add(T_a)
    | c_plus(c_0,V_y,T_a) = V_y )).

cnf(cls_OrderedGroup_Ocompare__rls__2_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(V_a,c_minus(V_b,V_c,T_a),T_a) = c_minus(c_plus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__3_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(c_minus(V_a,V_b,T_a),V_c,T_a) = c_minus(c_plus(V_a,V_c,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__5_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,c_minus(V_b,V_c,T_a),T_a) = c_minus(c_plus(V_a,V_c,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__8_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(V_a,c_plus(V_c,V_b,T_a),T_a)
    | c_lessequals(c_minus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__9_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(c_plus(V_a,V_b,T_a),V_c,T_a)
    | c_lessequals(V_a,c_minus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Odiff__self_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,V_a,T_a) = c_0 )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(v_g(V_U),v_k(V_U),t_b) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( ~ c_lessequals(c_minus(v_f(v_x),v_k(v_x),t_b),c_minus(v_f(v_x),v_g(v_x),t_b),t_b) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_23,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Ocomm__monoid__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_4,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Oab__group__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_54,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Opordered__ab__group__add(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
