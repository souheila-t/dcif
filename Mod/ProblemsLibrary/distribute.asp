%#maxint=35.% max all other values
%#const nbAg=3.
%#const minCl=2.
%#const maxCl=5.
%#const maxComm=10.
%#const maxEcart=3. %maxEcart=maxCl-minCl.
%#const maxTotComm=30.%maxTotComm=(nbAg*nbAg-1)*maxcomm/2


agent(1..nbAg).

hasPred(C,P) :- hasLit(C,P,pos,V).
hasPred(C,P) :- hasLit(C,P,neg,V).

equiv(C,F):- clause(C), clause(F), C<F,
  #count{P:predicate(P),hasPred(C,P), not hasPred(F,P)}=0,
  #count{P:predicate(P),hasPred(F,P), not hasPred(C,P)}=0.
  
hasClause(A,C) v -hasClause(A,C) :- clause(C), agent(A).
:- clause(C), not #count{A:agent(A),hasClause(A,C)}=1.

:- agent(A), #count{C:clause(C),hasClause(A,C)}<minCl.
:- agent(A), #count{C:clause(C),hasClause(A,C)}>maxCl.

num(minCl..maxCl).

nbClauses(A,N) :- N=#count{C:clause(C),hasClause(A,C)}, agent(A), num(N).



maxClauses(M) :- M=#max{N:nbClauses(A,N), agent(A)}, num(M).
minClauses(M) :- M=#min{N:nbClauses(A,N), agent(A)}, num(M).

num2(0..maxEcart).
ecartMax(E) :- +(N,E,M), minClauses(N), maxClauses(M), num2(E).

:~ ecartMax(E). [E:1]


common(A,B,P) :- predicate(P), hasPred(C,P), hasPred(F,P), hasClause(A,C), hasClause(B,F), clause(C), clause(F), agent(A), agent(B), A<B.

numComm(1..maxComm).
:- #count{P:predicate(P),common(A,B,P)}>maxComm, agent(A), agent(B).
commLang(A,B,N) :- N=#count{P:predicate(P),common(A,B,P)}, agent(A), agent(B), numComm(N).

bignum(0..maxTotComm).
:- #sum{K,A,B:commLang(A,B,K),agent(A), agent(B)}>maxTotComm.
totalComm(N):- N=#sum{K,A,B:commLang(A,B,K),agent(A), agent(B)}, bignum(N).

:~totalComm(N). [N:2]


% avoiding symmetry on agents with clause and agent order (to modify if redundancy authorized, by adding -hasclause(J,C) (and -hasClause (I,F) in cont?)
:-agent(I), hasClause(I,C), clause(C), agent(J), J<I, #count{F:hasClause(J,F),F<C,clause(F)}=0.  
% avoiding symmetry on equivalent clauses
:-clause(C), clause(F), agent(I), agent(J), hasClause(I,F), hasClause(J,C), equiv(C,F), C<F, I<J.

%allEdge(1,2,N) :- N=#count{P:predicate(P),hasPred(C,P),hasPred(F,P)}, clause(C), clause(F).
%edge(1,2,N):-allEdge(C,F,N), clause(C), clause(F), N>0.

%:- #sum{K,A,B,1,2:edge(C,F,K), hasClause(A,C),hasClause(B,F), agent(A), agent(B), A!=B}>20.
%extEdges(N) :- N=#sum{K,A,B,1,2:edge(C,F,K), hasClause(A,C),hasClause(B,F), clause(C), clause(F), agent(A), agent(B), A!=B}, bignum(N).
%:~extEdges(N). [N:2]

