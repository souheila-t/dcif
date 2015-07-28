%------------------------------------------------------------------------------
% File     : ANA031-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v6.1.0, 0.20 v6.0.0, 0.22 v5.5.0, 0.44 v5.4.0, 0.47 v5.3.0, 0.58 v5.2.0, 0.25 v5.1.0, 0.29 v4.1.0, 0.33 v3.3.0, 0.43 v3.2.0
% Syntax   : Number of clauses     :   11 (   0 non-Horn;   3 unit;   7 RR)
%            Number of atoms       :   21 (   2 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    7 (   0 propositional; 1-3 arity)
%            Number of functors    :    9 (   3 constant; 0-3 arity)
%            Number of variables   :   32 (  20 singleton)
%            Maximal term depth    :    6 (   2 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_b(V_U),t_b),c_times(v_c,c_HOL_Oabs(v_g(V_U),t_b),t_b),t_b) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( ~ c_lessequals(c_times(c_HOL_Oabs(v_b(v_x(V_U)),t_b),c_HOL_Oabs(v_f(v_x(V_U)),t_b),t_b),c_times(V_U,c_times(c_HOL_Oabs(v_f(v_x(V_U)),t_b),c_HOL_Oabs(v_g(v_x(V_U)),t_b),t_b),t_b),t_b) )).

cnf(cls_OrderedGroup_Oabs__ge__zero_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | c_lessequals(c_0,c_HOL_Oabs(V_a,T_a),T_a) )).

cnf(cls_OrderedGroup_Omult__ac__1_0,axiom,
    ( ~ class_OrderedGroup_Osemigroup__mult(T_a)
    | c_times(c_times(V_a,V_b,T_a),V_c,T_a) = c_times(V_a,c_times(V_b,V_c,T_a),T_a) )).

cnf(cls_OrderedGroup_Omult__ac__2_0,axiom,
    ( ~ class_OrderedGroup_Oab__semigroup__mult(T_a)
    | c_times(V_a,V_b,T_a) = c_times(V_b,V_a,T_a) )).

cnf(cls_Ring__and__Field_Opordered__semiring__class_Omult__left__mono_0,axiom,
    ( ~ class_Ring__and__Field_Opordered__semiring(T_a)
    | ~ c_lessequals(V_a,V_b,T_a)
    | ~ c_lessequals(c_0,V_c,T_a)
    | c_lessequals(c_times(V_c,V_a,T_a),c_times(V_c,V_b,T_a),T_a) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_17,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Oab__semigroup__mult(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_21,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Osemigroup__mult(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_42,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Ring__and__Field_Opordered__semiring(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
