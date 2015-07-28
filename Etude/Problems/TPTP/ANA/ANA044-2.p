%------------------------------------------------------------------------------
% File     : ANA044-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.08 v5.2.0, 0.00 v5.1.0, 0.14 v5.0.0, 0.29 v4.1.0, 0.22 v4.0.1, 0.33 v3.7.0, 0.17 v3.3.0, 0.29 v3.2.0
% Syntax   : Number of clauses     :    8 (   0 non-Horn;   4 unit;   6 RR)
%            Number of atoms       :   15 (   2 equality)
%            Maximal clause size   :    4 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-3 arity)
%            Number of functors    :    9 (   4 constant; 0-3 arity)
%            Number of variables   :   20 (  18 singleton)
%            Maximal term depth    :    5 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_0,v_l(V_U,V_V),t_b) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(c_0,v_h(V_U),t_b) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( c_times(v_l(v_x,v_xa),v_h(v_k(v_x,v_xa)),t_b) != c_HOL_Oabs(c_times(v_l(v_x,v_xa),v_h(v_k(v_x,v_xa)),t_b),t_b) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

cnf(cls_OrderedGroup_Oabs__of__nonneg_0,axiom,
    ( ~ class_OrderedGroup_Olordered__ab__group__abs(T_a)
    | ~ c_lessequals(c_0,V_y,T_a)
    | c_HOL_Oabs(V_y,T_a) = V_y )).

cnf(cls_Ring__and__Field_Omult__nonneg__nonneg_0,axiom,
    ( ~ class_Ring__and__Field_Opordered__cancel__semiring(T_a)
    | ~ c_lessequals(c_0,V_b,T_a)
    | ~ c_lessequals(c_0,V_a,T_a)
    | c_lessequals(c_0,c_times(V_a,V_b,T_a),T_a) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_40,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_Ring__and__Field_Opordered__cancel__semiring(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_50,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Olordered__ab__group__abs(T) )).

%------------------------------------------------------------------------------
