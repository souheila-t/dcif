%------------------------------------------------------------------------------
% File     : ANA016-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.10 v6.1.0, 0.29 v6.0.0, 0.10 v5.5.0, 0.25 v5.3.0, 0.33 v5.2.0, 0.19 v5.1.0, 0.18 v5.0.0, 0.14 v4.1.0, 0.23 v4.0.1, 0.27 v3.7.0, 0.10 v3.5.0, 0.18 v3.4.0, 0.25 v3.3.0, 0.29 v3.2.0
% Syntax   : Number of clauses     :    9 (   1 non-Horn;   3 unit;   6 RR)
%            Number of atoms       :   16 (   6 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-2 arity)
%            Number of functors    :    8 (   5 constant; 0-3 arity)
%            Number of variables   :   18 (  11 singleton)
%            Maximal term depth    :    4 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_NHN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_0,negated_conjecture,
    ( v_c != c_0 )).

cnf(cls_conjecture_2,negated_conjecture,
    ( v_g(v_x) != c_times(v_c,c_times(c_HOL_Oinverse(v_c,t_a),v_g(v_x),t_a),t_a) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__field(t_a) )).

cnf(cls_OrderedGroup_Omonoid__mult__class_Oaxioms__1_0,axiom,
    ( ~ class_OrderedGroup_Omonoid__mult(T_a)
    | c_times(c_1,V_y,T_a) = V_y )).

cnf(cls_OrderedGroup_Osemigroup__mult__class_Omult__assoc_0,axiom,
    ( ~ class_OrderedGroup_Osemigroup__mult(T_a)
    | c_times(c_times(V_a,V_b,T_a),V_c,T_a) = c_times(V_a,c_times(V_b,V_c,T_a),T_a) )).

cnf(cls_Ring__and__Field_Oright__inverse_0,axiom,
    ( ~ class_Ring__and__Field_Ofield(T_a)
    | V_a = c_0
    | c_times(V_a,c_HOL_Oinverse(V_a,T_a),T_a) = c_1 )).

cnf(clsrel_Ring__and__Field_Ofield_12,axiom,
    ( ~ class_Ring__and__Field_Ofield(T)
    | class_OrderedGroup_Omonoid__mult(T) )).

cnf(clsrel_Ring__and__Field_Ofield_21,axiom,
    ( ~ class_Ring__and__Field_Ofield(T)
    | class_OrderedGroup_Osemigroup__mult(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Ring__and__Field_Ofield(T) )).

%------------------------------------------------------------------------------
