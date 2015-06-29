cnf(c1, axiom, [hasSymptom(fever), -hasDisease(flu)]).
cnf(c2, axiom, [hasSymptom(throatache), -hasDisease(angina)]).
cnf(c3, axiom, [hasSymptom(fever), -hasDisease(angina)]).
cnf(c4, axiom, [hasSymptom(mucus), -hasDisease(flu)]).
cnf(c5, axiom, [hasSymptom(mucus), -hasDisease(rhino)]).

cnf(d1, top_clause, [-hasSymptom(fever)]).
cnf(d2, top_clause, [-hasSymptom(mucus)]).
cnf(d3, top_clause, [-hasSymptom(throatache)]).

pf([hasSymptom(_)] < 2).

