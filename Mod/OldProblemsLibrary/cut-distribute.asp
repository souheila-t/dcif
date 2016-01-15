edge(C,D,L) :- hasAtom(C,L,S1), hasAtom(D,L,S2), S1!=S2, C<D, clause(C), clause(D), languageAtom(L), sign(S1), sign(S2).

0{remove(C,D):edge(C,D,L)}maxEdges.

commLanguage(L):-remove(C,D),edge(C,D,L).

linked(C,D) :- edge(C,D,L), not remove(C,D).
linked(C,D) :- linked(C,E), linked(E,D), clause(C), clause(D), clause(E), C<E, E<D.
linked(C,D) :- linked(C,E), linked(D,E), clause(C), clause(D), clause(E), C<E, D<E, C<D.

cardGroup(C,J+1) :- J{linked(C,D):clause(D)}J, 0{linked(E,C):clause(E)}0, clause(C), clauseNumber(J).

ecart(C,D,I-J) :- cardGroup(C,I), cardGroup(D,J), clause(C), clause(D), C<D, I>=J, clauseNumber(I), clauseNumber(J).
ecart(C,D,J-I) :- cardGroup(C,I), cardGroup(D,J), clause(C), clause(D), C<D, I<J, clauseNumber(I), clauseNumber(J).

hasGroup(C) :- cardGroup(C,I), clauseNumber(I).


:- ecart(C,D,Ecart), Ecart>maxEcart, clause(C), clause(D), C<D.
:- cardGroup(C,I), clause(C), I<minCard, clauseNumber(I).
:- cardGroup(C,I), clause(C), I>maxCard, clauseNumber(I).
:- not minAgent {hasGroup(C) : clause(C)} maxAgent.

hasClause(C,C) :- hasGroup(C), clause(C).
hasClause(C,D) :- linked(C,D), hasGroup(C), clause(C), clause(D), C<D. 

#hide.
#show hasClause(C,D).

#minimize{commLanguage(L):languageAtom(L)}. 