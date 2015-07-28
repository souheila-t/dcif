%------------------------------------------------------------------------------
% File     : ANA015-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.0.0, 0.11 v5.5.0, 0.06 v5.4.0, 0.07 v5.3.0, 0.17 v5.2.0, 0.12 v5.1.0, 0.14 v4.1.0, 0.22 v4.0.1, 0.17 v3.3.0, 0.29 v3.2.0
% Syntax   : Number of clauses     :   12 (   0 non-Horn;   3 unit;   8 RR)
%            Number of atoms       :   23 (   2 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    8 (   0 propositional; 1-3 arity)
%            Number of functors    :   10 (   4 constant; 0-3 arity)
%            Number of variables   :   34 (  22 singleton)
%            Maximal term depth    :    5 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Oabs__ge__zero_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_lessequals(c_0,c_HOL_Oabs(V_a,T_a),T_a) )).

cnf(cls_OrderedGroup_Osemigroup__mult__class_Omult__assoc_0,axiom,
    ( ~ class_OrderedGroup_Osemigroup__mult(T_a)
    | c_times(c_times(V_a,V_b,T_a),V_c,T_a) = c_times(V_a,c_times(V_b,V_c,T_a),T_a) )).

cnf(cls_Ring__and__Field_Oabs__mult_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T_a)
    | c_HOL_Oabs(c_times(V_a,V_b,T_a),T_a) = c_times(c_HOL_Oabs(V_a,T_a),c_HOL_Oabs(V_b,T_a),T_a) )).

cnf(cls_Ring__and__Field_Opordered__semiring__class_Omult__left__mono_0,axiom,
    ( ~ class_Ring__and__Field_Opordered__semiring(T_a)
    | ~ c_lessequals(V_a,V_b,T_a)
    | ~ c_lessequals(c_0,V_c,T_a)
    | c_lessequals(c_times(V_c,V_a,T_a),c_times(V_c,V_b,T_a),T_a) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_g(V_U),t_a),c_times(v_d,c_HOL_Oabs(v_f(V_U),t_a),t_a),t_a) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(c_times(c_HOL_Oinverse(v_c,t_a),v_g(v_x(V_U)),t_a),t_a),c_times(V_U,c_HOL_Oabs(v_f(v_x(V_U)),t_a),t_a),t_a) )).

cnf(clsrel_Ring__and__Field_Ofield_21,axiom,
    ( ~ class_Ring__and__Field_Ofield(T)
    | class_OrderedGroup_Osemigroup__mult(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_0,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Ring__and__Field_Ofield(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_34,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Ring__and__Field_Oordered__idom(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_46,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_Ring__and__Field_Opordered__semiring(T) )).

cnf(clsrel_Ring__and__Field_Oordered__field_47,axiom,
    ( ~ class_Ring__and__Field_Oordered__field(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__field(t_a) )).

%------------------------------------------------------------------------------
