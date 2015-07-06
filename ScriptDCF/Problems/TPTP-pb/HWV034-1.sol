%HWV034-1
%Problem:  Connections, faults, and gates.
%Status:  Satisfiable
%Type: CF

cnf(value_propagation1, axiom, [-connection(P1,P2), -value(P1,V), value(P2,V)]).
cnf(value_propagation2, axiom, [-connection(P1,P2), -value(P2,V), value(P1,V)]).
cnf(unique_value, axiom, [-value(P,V1), -value(P,V2), equal_value(V1,V2)]).
cnf(equal_value1, axiom, [-equal_value(n0,n1)]).
cnf(equal_value2, axiom, [-equal_value(n1,n0)]).
cnf(not_ok_and_abnormal, axiom, [-mode(K,ok), -mode(K,abnormal)]).
cnf(ok_or_abnormal, axiom, [-type(K,Any), mode(K,ok), mode(K,abnormal)]).
cnf(and_0x_0, axiom, [-mode(K,ok), -type(K,and), -value(in(Any,K),n0), value(out(n1,K),n0)]).
cnf(and_11_1, axiom, [-mode(K,ok), -type(K,and), -value(in(n1,K),n1), -value(in(n2,K),n1), value(out(n1,K),n1)]).
cnf(and_0_00, axiom, [-mode(K,ok), -type(K,and), -value(out(n1,K),n0), value(in(n1,K),n0), value(in(n2,K),n0)]).
cnf(and_1_1x, axiom, [-mode(K,ok), -type(K,and), -value(out(n1,K),n1), value(in(n1,K),n1)]).
cnf(and_1_x1, axiom, [-mode(K,ok), -type(K,and), -value(out(n1,K),n1), value(in(n2,K),n1)]).
cnf(or_1x_1, axiom, [-mode(K,ok), -type(K,or), -value(in(Any,K),n1), value(out(n1,K),n1)]).
cnf(or_00_0, axiom, [-mode(K,ok), -type(K,or), -value(in(n1,K),n0), -value(in(n2,K),n0), value(out(n1,K),n0)]).
cnf(or_1_11, axiom, [-mode(K,ok), -type(K,or), -value(out(n1,K),n1), value(in(n1,K),n1), value(in(n2,K),n1)]).
cnf(or_0_0x, axiom, [-mode(K,ok), -type(K,or), -value(out(n1,K),n0), value(in(n1,K),n0)]).
cnf(or_0_01, axiom, [-mode(K,ok), -type(K,or), -value(out(n1,K),n0), value(in(n2,K),n0)]).
cnf(not_0_1_fw, axiom, [-mode(K,ok), -type(K,not), -value(in(n1,K),n0), value(out(n1,K),n1)]).
cnf(not_1_0_fw, axiom, [-mode(K,ok), -type(K,not), -value(in(n1,K),n1), value(out(n1,K),n0)]).
cnf(not_0_1_bw, axiom, [-mode(K,ok), -type(K,not), -value(out(n1,K),n0), value(in(n1,K),n1)]).
cnf(not_1_0_bw, axiom, [-mode(K,ok), -type(K,not), -value(out(n1,K),n1), value(in(n1,K),n0)]).

% pf([-connection(_,_), connection(_,_), -value(_,_), value(_,_), equal_value(_,_), -equal_value(_,_), -mode(_,_), mode(_,_), -type(_,_), type(_,_)]).

pf([-connection(_,_), connection(_,_), -value(_,_), value(_,_)]).
