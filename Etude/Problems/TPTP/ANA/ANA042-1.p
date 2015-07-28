%------------------------------------------------------------------------------
% File     : ANA042-1 : TPTP v6.1.0. Released v3.2.0.
% Domain   : Analysis
% Problem  : Problem about Big-O notation
% Version  : [Pau06] axioms : Especial.
% English  :

% Refs     : [Pau06] Paulson (2006), Email to G. Sutcliffe
% Source   : [Pau06]
% Names    : BigO__bigo_setsum1_2 [Pau06]

% Status   : Unsatisfiable
% Rating   : 0.40 v6.1.0, 0.36 v6.0.0, 0.20 v5.5.0, 0.25 v5.3.0, 0.28 v5.2.0, 0.25 v5.1.0, 0.41 v5.0.0, 0.36 v4.1.0, 0.31 v4.0.1, 0.45 v4.0.0, 0.36 v3.7.0, 0.40 v3.5.0, 0.36 v3.4.0, 0.50 v3.2.0
% Syntax   : Number of clauses     : 2787 ( 248 non-Horn; 649 unit;1975 RR)
%            Number of atoms       : 6119 (1279 equality)
%            Maximal clause size   :    7 (   2 average)
%            Number of predicates  :   87 (   0 propositional; 1-3 arity)
%            Number of functors    :  242 (  47 constant; 0-18 arity)
%            Number of variables   : 5804 (1185 singleton)
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
cnf(cls_SetsAndFunctions_Oset__one__times_0,axiom,
    ( ~ class_OrderedGroup_Ocomm__monoid__mult(T_a)
    | c_SetsAndFunctions_Oelt__set__times(c_1,V_y,T_a) = V_y )).

cnf(cls_SetsAndFunctions_Oset__zero__plus_0,axiom,
    ( ~ class_OrderedGroup_Ocomm__monoid__add(T_a)
    | c_SetsAndFunctions_Oelt__set__plus(c_0,V_y,T_a) = V_y )).

cnf(cls_conjecture_0,negated_conjecture,
    ( c_lessequals(c_0,v_h(V_U,V_V),t_c) )).

cnf(cls_conjecture_1,negated_conjecture,
    ( c_lessequals(c_HOL_Oabs(v_f(V_U,V_V),t_c),c_times(v_x,v_h(V_U,V_V),t_c),t_c) )).

cnf(cls_conjecture_2,negated_conjecture,
    ( c_in(v_xb(V_U),v_A(v_xa(V_U)),t_b) )).

cnf(cls_conjecture_3,negated_conjecture,
    ( ~ c_lessequals(c_HOL_Oabs(v_f(v_xa(V_U),v_xb(V_U)),t_c),c_times(V_U,v_h(v_xa(V_U),v_xb(V_U)),t_c),t_c) )).

cnf(tfree_tcs,negated_conjecture,
    ( class_Ring__and__Field_Oordered__idom(t_c) )).

%------------------------------------------------------------------------------
