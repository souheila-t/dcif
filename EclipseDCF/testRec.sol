cnf(f1, top_clause,[a(4,5)]).
cnf(f2, axiom,[-a(X,Y),b(X,Y)]).
cnf(rule1, axiom,[-b(X,Y),c(Y,Z)]).
cnf(rule2, axiom,[-c(Y,Z),b(Z,X)]).

pf([b(_,_),c(_,_)]<=1).