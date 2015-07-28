%------------------------------------------------------------------------------
% File     : ANA023-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.08 v5.2.0, 0.00 v5.0.0, 0.14 v4.1.0, 0.11 v4.0.1, 0.17 v3.3.0, 0.14 v3.2.0
% Syntax   : Number of clauses     :   12 (   0 non-Horn;   4 unit;  11 RR)
%            Number of atoms       :   24 (   1 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    7 (   0 propositional; 1-3 arity)
%            Number of functors    :    8 (   3 constant; 0-3 arity)
%            Number of variables   :   39 (  34 singleton)
%            Maximal term depth    :    3 (   1 average)
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

cnf(cls_OrderedGroup_Ocompare__rls__9_0,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(V_a,c_minus(V_c,V_b,T_a),T_a)
    | c_lessequals(c_plus(V_a,V_b,T_a),V_c,T_a) )).

cnf(cls_OrderedGroup_Ocompare__rls__9_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__group__add(T_a)
    | ~ c_lessequals(c_plus(V_a,V_b,T_a),V_c,T_a)
    | c_lessequals(V_a,c_minus(V_c,V_b,T_a),T_a) )).

cnf(cls_Orderings_Oorder__class_Oorder__trans_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | ~ c_lessequals(V_y,V_z,T_a)
    | ~ c_lessequals(V_x,V_y,T_a)
    | c_lessequals(V_x,V_z,T_a) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(c_0,c_minus(v_k(v_x),v_g(v_x),t_b),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( c_lessequals(v_k(v_x),v_f(v_x),t_b) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( ~ c_lessequals(c_0,c_minus(v_f(v_x),v_g(v_x),t_b),t_b) )).

cnf(clsrel_Orderings_Olinorder_4,axiom,
    ( ~ class_Orderings_Olinorder(T)
    | class_Orderings_Oorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_23,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Ocomm__monoid__add(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_33,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Orderings_Olinorder(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_54,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Opordered__ab__group__add(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
