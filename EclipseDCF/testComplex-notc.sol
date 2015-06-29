cnf(f1, axiom,[belongs(a,c1)]).
cnf(f2, axiom,[belongs(b,c1)]).
cnf(rule1, axiom,[-exchange(C1,X,Y,C2),belongs(Y,C2)]).
cnf(rule2, axiom,[-exchange(C1,X,Y,C2),-belongs(Z,C1),eq(Z,X),belongs(Z,C2)]).
cnf(f4, axiom,[eq(X,X)]).


pf([belongs(_,_),eq(_,_)]<=2).