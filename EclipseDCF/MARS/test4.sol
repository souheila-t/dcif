cnf(c1, axiom, [hasSymptom(fever), -hasDisease(flu)]).
cnf(c2, axiom, [hasSymptom(throatache), -hasDisease(angina)]).
cnf(c3, axiom, [hasSymptom(fever), -hasDisease(angina)]).
cnf(c4, axiom, [hasSymptom(mucus), -hasDisease(flu)]).
cnf(c5, axiom, [hasSymptom(mucus), -hasDisease(rhino)]).
cnf(c6, axiom, [hasSymptom(fever)]).
cnf(h1, axiom, [hasDisease(angina)]).
cnf(d1, top_clause, [default(mucus)]).
cnf(d2, top_clause, [default(fever)]).
cnf(d3, top_clause, [default(throatache)]).
cnf(dC, top_clause, [contrad(X),-default(X),-hasSymptom(X)]).
pf([contrad(_),default(_)] < 3).
