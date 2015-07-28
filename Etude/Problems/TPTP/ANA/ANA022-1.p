%------------------------------------------------------------------------------
% File     : ANA022-1 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    : BigO__bigo_lesso2 [Pau06]

% Status   : Unsatisfiable
% Rating   : 0.70 v6.1.0, 0.86 v6.0.0, 0.90 v5.3.0, 0.94 v5.2.0, 0.88 v5.0.0, 0.86 v4.1.0, 0.92 v4.0.1, 0.82 v3.7.0, 0.90 v3.5.0, 0.91 v3.4.0, 0.83 v3.3.0, 0.93 v3.2.0
% Syntax   : Number of clauses     : 2808 ( 249 non-Horn; 648 unit;1986 RR)
%            Number of atoms       : 6175 (1289 equality)
%            Maximal clause size   :    7 (   2 average)
%            Number of predicates  :   87 (   0 propositional; 1-3 arity)
%            Number of functors    :  239 (  46 constant; 0-18 arity)
%            Number of variables   : 5879 (1184 singleton)
%            Maximal term depth    :    8 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found.
%------------------------------------------------------------------------------
include('Axioms/ANA003-0.ax').
include('Axioms/MSC001-1.ax').
include('Axioms/MSC001-0.ax').
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Ocompare__rls__10_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | V_a = c_plus(c_minus(V_a,V_b,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__10_1,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(c_plus(V_c,V_b,T_a),V_b,T_a) = V_c )).

cnf(cls_OrderedGroup_Ocompare__rls__11_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(c_minus(V_c,V_b,T_a),V_b,T_a) = V_c )).

cnf(cls_OrderedGroup_Ocompare__rls__11_1,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | V_a = c_minus(c_plus(V_a,V_b,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__1_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(V_a,c_uminus(V_b,T_a),T_a) = c_minus(V_a,V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__2_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(V_a,c_minus(V_b,V_c,T_a),T_a) = c_minus(c_plus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__3_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(c_minus(V_a,V_b,T_a),V_c,T_a) = c_minus(c_plus(V_a,V_c,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__4_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(c_minus(V_a,V_b,T_a),V_c,T_a) = c_minus(V_a,c_plus(V_b,V_c,T_a),T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__5_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,c_minus(V_b,V_c,T_a),T_a) = c_minus(c_plus(V_a,V_c,T_a),V_b,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__6_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_less(c_minus(V_a,V_b,T_a),V_c,T_a)
    | c_less(V_a,c_plus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__6_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_less(V_a,c_plus(V_c,V_b,T_a),T_a)
    | c_less(c_minus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__7_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_less(V_a,c_minus(V_c,V_b,T_a),T_a)
    | c_less(c_plus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__7_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_less(c_plus(V_a,V_b,T_a),V_c,T_a)
    | c_less(V_a,c_minus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__8_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(c_minus(V_a,V_b,T_a),V_c,T_a)
    | c_lessequals(V_a,c_plus(V_c,V_b,T_a),T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__8_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(V_a,c_plus(V_c,V_b,T_a),T_a)
    | c_lessequals(c_minus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__9_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(V_a,c_minus(V_c,V_b,T_a),T_a)
    | c_lessequals(c_plus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__9_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(c_plus(V_a,V_b,T_a),V_c,T_a)
    | c_lessequals(V_a,c_minus(V_c,V_b,T_a),T_a) )).

cnf(cls_SetsAndFunctions_Oset__one__times_0,axiom,
    ( ~ class_OrderedGroup_Ocomm__monoid__mult(T_a)
    | c_SetsAndFunctions_Oelt__set__times(c_1,V_y,T_a) = V_y )).

cnf(cls_SetsAndFunctions_Oset__zero__plus_0,axiom,
    ( ~ class_OrderedGroup_Ocomm__monoid__add(T_a)
    | c_SetsAndFunctions_Oelt__set__plus(c_0,V_y,T_a) = V_y )).

cnf(cls_OrderedGroup_Oab__group__add__class_Odiff__minus_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_minus(V_a,V_b,T_a) = c_plus(V_a,c_uminus(V_b,T_a),T_a) )).

cnf(cls_Orderings_Olinorder__not__le_0,axiom,
    ( ~ class_Orderings_Olinorder(T_a)
    | c_less(V_y,V_x,T_a)
    | c_lessequals(V_x,V_y,T_a) )).

cnf(cls_Orderings_Olinorder__not__le_1,axiom,
    ( ~ class_Orderings_Olinorder(T_a)
    | ~ c_less(V_y,V_x,T_a)
    | ~ c_lessequals(V_x,V_y,T_a) )).

cnf(cls_Orderings_Oorder__class_Oorder__trans_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_lessequals(V_y,V_z,T_a)
    | ~ c_lessequals(V_x,V_y,T_a)
    | c_lessequals(V_x,V_z,T_a) )).

cnf(cls_Orderings_Oorder__less__imp__le_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_less(V_x,V_y,T_a)
    | c_lessequals(V_x,V_y,T_a) )).

cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_0,v_k(V_U),t_b) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(v_k(V_U),v_f(V_U),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_Orderings_Omax(c_minus(v_k(v_x),v_g(v_x),t_b),c_0,t_b),c_HOL_Oabs(c_minus(v_f(v_x),v_g(v_x),t_b),t_b),t_b) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
