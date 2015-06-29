cnf(r1, axiom, [pT(h,c1)]).
cnf(r2, axiom, [pM(b,X),-pA(g,X)]).
cnf(r3, axiom, [pT(d,X,Y),-pT(f,X,Y)]).
cnf(r4, axiom, [-pT(c,X),-pT(e,X)]).
cnf(r5, axiom, [pT(k,X),-pA(i,X)]).
cnf(r6, axiom, [pT(e,X),-pT(h,X)]).
cnf(r7, axiom, [pM(a,Y), pT(c,Y), -pT(d,X,Y)]).
cnf(r8, axiom, [pT(f,X,Y),-pT(h,Y),-pA(g,X)]).
cnf(r9, axiom, [pM(b,X),-pT(k,X)]).
cnf(d1, top_clause, [-pM(a,c1)]).
cnf(d2, top_clause, [-pM(b,c2)]).

pf([pM(_,_)]).

