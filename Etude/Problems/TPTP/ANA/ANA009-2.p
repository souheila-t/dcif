%------------------------------------------------------------------------------
% File     : ANA009-2 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Reduced > Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    :

% Status   : Unsatisfiable
% Rating   : 0.00 v5.3.0, 0.08 v5.2.0, 0.00 v5.0.0, 0.14 v4.1.0, 0.11 v4.0.1, 0.17 v3.3.0, 0.14 v3.2.0
% Syntax   : Number of clauses     :    7 (   0 non-Horn;   3 unit;   4 RR)
%            Number of atoms       :   12 (   1 equality)
%            Maximal clause size   :    3 (   2 average)
%            Number of predicates  :    5 (   0 propositional; 1-3 arity)
%            Number of functors    :    7 (   3 constant; 0-3 arity)
%            Number of variables   :   16 (  11 singleton)
%            Maximal term depth    :    4 (   1 average)
% SPC      : CNF_UNS_RFO_SEQ_HRN

% Comments : The problems in the [Pau06] collection each have very many axioms,
%            of which only a small selection are required for the refutation.
%            The mission is to find those few axioms, after which a refutation
%            can be quite easily found. This version has only the necessary
%            axioms.
%------------------------------------------------------------------------------
cnf(cls_OrderedGroup_Oadd__le__cancel__right_1,axiom,
    ( ~ class_OrderedGroup_Opordered__ab__semigroup__add__imp__le(T_a)
    | ~ c_lessequals(V_a,V_b,T_a)
    | c_lessequals(c_plus(V_a,V_c,T_a),c_plus(V_b,V_c,T_a),T_a) )).

cnf(cls_OrderedGroup_Oright__minus_0,axiom,
    ( ~ class_OrderedGroup_Oab__group__add(T_a)
    | c_plus(V_a,c_uminus(V_a,T_a),T_a) = c_0 )).

cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(v_lb(V_U),v_f(V_U),t_b) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( ~ c_lessequals(c_0,c_plus(v_f(v_x),c_uminus(v_lb(v_x),t_b),t_b),t_b) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_37,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Opordered__ab__semigroup__add__imp__le(T) )).

cnf(clsrel_Ring__and__Field_Oordered__idom_4,axiom,
    ( ~ class_Ring__and__Field_Oordered__idom(T)
    | class_OrderedGroup_Oab__group__add(T) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_b) )).

%------------------------------------------------------------------------------
