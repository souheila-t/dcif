%GRP123-1.005
%Problem:  (3,2,1) conjugate orthogonality
%Status:  Satisfiable
%Type: CARC

cnf(element_1, axiom, [group_element(e_1)]).
cnf(element_2, axiom, [group_element(e_2)]).
cnf(element_3, axiom, [group_element(e_3)]).
cnf(element_4, axiom, [group_element(e_4)]).
cnf(element_5, axiom, [group_element(e_5)]).
cnf(e_1_is_not_e_2, axiom, [-equalish(e_1,e_2)]).
cnf(e_1_is_not_e_3, axiom, [-equalish(e_1,e_3)]).
cnf(e_1_is_not_e_4, axiom, [-equalish(e_1,e_4)]).
cnf(e_1_is_not_e_5, axiom, [-equalish(e_1,e_5)]).
cnf(e_2_is_not_e_1, axiom, [-equalish(e_2,e_1)]).
cnf(e_2_is_not_e_3, axiom, [-equalish(e_2,e_3)]).
cnf(e_2_is_not_e_4, axiom, [-equalish(e_2,e_4)]).
cnf(e_2_is_not_e_5, axiom, [-equalish(e_2,e_5)]).
cnf(e_3_is_not_e_1, axiom, [-equalish(e_3,e_1)]).
cnf(e_3_is_not_e_2, axiom, [-equalish(e_3,e_2)]).
cnf(e_3_is_not_e_4, axiom, [-equalish(e_3,e_4)]).
cnf(e_3_is_not_e_5, axiom, [-equalish(e_3,e_5)]).
cnf(e_4_is_not_e_1, axiom, [-equalish(e_4,e_1)]).
cnf(e_4_is_not_e_2, axiom, [-equalish(e_4,e_2)]).
cnf(e_4_is_not_e_3, axiom, [-equalish(e_4,e_3)]).
cnf(e_4_is_not_e_5, axiom, [-equalish(e_4,e_5)]).
cnf(e_5_is_not_e_1, axiom, [-equalish(e_5,e_1)]).
cnf(e_5_is_not_e_2, axiom, [-equalish(e_5,e_2)]).
cnf(e_5_is_not_e_3, axiom, [-equalish(e_5,e_3)]).
cnf(e_5_is_not_e_4, axiom, [-equalish(e_5,e_4)]).
cnf(product_total_function1, axiom, [-group_element(X), -group_element(Y), product(X,Y,e_1), product(X,Y,e_2), product(X,Y,e_3), product(X,Y,e_4), product(X,Y,e_5)]).
cnf(product_total_function2, axiom, [-product(X,Y,W), -product(X,Y,Z), equalish(W,Z)]).
cnf(product_right_cancellation, axiom, [-product(X,W,Y), -product(X,Z,Y), equalish(W,Z)]).
cnf(product_left_cancellation, axiom, [-product(W,Y,X), -product(Z,Y,X), equalish(W,Z)]).
cnf(product_idempotence, axiom, [product(X,X,X)]).

pf([-product(_,_,_)<=4, equalish(_,_)<=1]).
