%SYN084-1
%Problem:  Pelletier Problem 62
%Status:  Satisfiable
%Type: CF

cnf(clause_1, top_clause, [big_p(a)]).
cnf(clause_2, axiom, [-big_p(f(Y)), big_p(f(f(X))), big_p(X), -big_p(a)]).
cnf(clause_3, axiom, [big_p(Y), big_p(f(f(X))), big_p(X), -big_p(a)]).
cnf(clause_4, axiom, [-big_p(c), big_p(f(c)), big_p(f(f(X))), big_p(X), -big_p(a)]).
cnf(clause_5, axiom, [-big_p(f(f(c))), big_p(f(f(X))), big_p(X), -big_p(a)]).
cnf(clause_6, axiom, [-big_p(f(f(c))), big_p(f(f(X))), -big_p(f(X)), -big_p(a)]).
cnf(clause_7, axiom, [-big_p(c), big_p(f(c)), big_p(f(f(X))), -big_p(f(X)), -big_p(a)]).
cnf(clause_8, axiom, [big_p(Y), big_p(f(f(X))), -big_p(f(X)), -big_p(a)]).
cnf(clause_9, axiom, [-big_p(f(Y)), big_p(f(f(X))), -big_p(f(X)), -big_p(a)]).
cnf(clause_10, axiom, [-big_p(f(f(c))), big_p(f(b)), -big_p(b)]).
cnf(clause_11, axiom, [-big_p(c), big_p(f(c)), big_p(f(b)), -big_p(b)]).
cnf(clause_12, axiom, [-big_p(a), big_p(Y), big_p(f(b)), -big_p(b)]).
cnf(clause_13, axiom, [-big_p(a), -big_p(f(Y)), big_p(f(b)), -big_p(b)]).

pf([big_p(_), -big_p(_)]).
