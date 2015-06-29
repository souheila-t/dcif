
cnf(r1,axiom,[p(X),-q(X)]).
cnf(r2,axiom,[q(X),-h(X,Y)]).
cnf(r3,axiom,[r(X),-k(X)]).
cnf(r4,axiom,[k(Y),-h(X,Y)]).
cnf(r5,axiom,[-s(X),-h(X,X)]).
cnf(r6,axiom,[-r(X),-p(X)]).

cnf(r7,axiom,[r(b),s(a)]).
cnf(r8,axiom,[-p(a)]).

pf([r(_)]).

