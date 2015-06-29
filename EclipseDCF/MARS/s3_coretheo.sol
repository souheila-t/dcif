cnf(c1, axiom, [hasSymptom(fever), -hasDisease(flu)]).
cnf(c2, axiom, [hasSymptom(throatache), -hasDisease(angina)]).
cnf(c3, axiom, [hasSymptom(fever), -hasDisease(angina)]).
cnf(c4, axiom, [hasSymptom(mucus), -hasDisease(flu)]).
cnf(c5, axiom, [hasSymptom(mucus), -hasDisease(rhino)]).
cnf(c6, axiom, [-notSymptom(fever), notDisease(flu)]).
cnf(c7, top_clause, [notSymptom(fever)]).
cnf(t1, top_clause, [-hasSymptom(mucus)]).
pf([-hasDisease(_), notDisease(_)] < 4).

