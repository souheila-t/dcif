cnf(u1, axiom, [pM(X,Y),-pU(X,Y)]).
cnf(u2, axiom, [pT(X,Y),-pUt(X,Y)]).
cnf(u2, axiom, [pT(X,Y,Z),-pUt(X,Y,Z)]).

pf([pU(_,_),pUt(_,_),pUt(_,_,_)]).
