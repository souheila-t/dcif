%------------------------------------------------------------------------------
% File     : ANA014-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.30 v6.1.0, 0.43 v6.0.0, 0.40 v5.5.0, 0.60 v5.3.0, 0.61 v5.2.0, 0.50 v5.1.0, 0.53 v5.0.0, 0.50 v4.1.0, 0.54 v4.0.1, 0.55 v4.0.0, 0.64 v3.7.0, 0.40 v3.5.0, 0.45 v3.4.0, 0.42 v3.3.0, 0.36 v3.2.0
% Syntax   : Number of clauses     :   13 (   1 non-Horn;   3 unit;   8 RR)
%            Number of atoms       :   24 (   6 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    8 (   0 propositional; 1-3 arity)
%            Number of functors    :    9 (   4 constant; 0-3 arity)
%            Number of variables   :   30 (  18 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_0,negated_conjecture,
    ( v_c != c_0 )).

cnf(cls_conjecture_1,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(v_f(v_x(V_U)),t_a),c_times(V_U,c_times(c_HOL_Oabs(v_c,t_a),c_HOL_Oabs(v_f(v_x(V_U)),t_a),t_a),t_a),t_a) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__field(t_a) )).

cnf(cls_OrderedGroup_Omonoid__mult__class_Oaxioms__1_0,axiom,
    ( ~ class_OrderedGroup_Omonoid__mult(T_a)
    | c_times(c_1,V_y,T_a) = V_y )).

cnf(cls_OrderedGroup_Osemigroup__mult__class_Omult__assoc_0,axiom,
    ( ~ class_OrderedGroup_Osemigroup__mult(T_a)
    | c_times(c_times(V_a,V_b,T_a),V_c,T_a) = c_times(V_a,c_times(V_b,V_c,T_a),T_a) )).

cnf(cls_Orderings_Oorder__class_Oaxioms__1_0,axiom,
    ( ~ class_Orderings_Oorder(T_a)
    | c_lessequals(V_x,V_x,T_a) )).

cnf(cls_Ring__and__Field_Oabs__mult_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T_a)
    | c_HOL_Oabs(c_times(V_a,V_b,T_a),T_a) = c_times(c_HOL_Oabs(V_a,T_a),c_HOL_Oabs(V_b,T_a),T_a) )).

cnf(cls_Ring__and__Field_Ofield__class_Oaxioms__1_0,axiom,
    ( ~ class_Ring__and__Field_Ofield(T_a)
    | V_a = c_0
    | c_times(c_HOL_Oinverse(V_a,T_a),V_a,T_a) = c_1 )).

cnf(clsrel_Ring__and__Field_Ofield_12,axiom,
    ( ~ class_Ring__and__Field_Ofield(T)
    | class_OrderedGroup_Omonoid__mult(T) )).

cnf(clsrel_Ring__and__Field_Ofield_21,axiom,
    ( ~ class_Ring__and__Field_Ofield(T)
    | class_OrderedGroup_Osemigroup__mult(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Ring__and__Field_Ofield(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_34,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Ring__and__Field_Oordered__idom(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_58,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Orderings_Oorder(T) )).

%------------------------------------------------------------------------------
