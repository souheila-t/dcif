cnf(c4, axiom, [hasSymptom(headache), -hasDisease(flu)]).
cnf(c4, axiom, [hasSymptom(cough), -hasDisease(flu)]).
cnf(c4, axiom, [hasSymptom(tiredness), -hasDisease(flu)]).
cnf(c4, axiom, [hasSymptom(fever), -hasDisease(flu)]).

cnf(c2, axiom, [hasSymptom(cough), -hasDisease(bronchitis)]).
cnf(c2, axiom, [hasSymptom(mucus), -hasDisease(bronchitis)]).
cnf(c2, axiom, [hasSymptom(throatpain), -hasDisease(bronchitis)]).

cnf(c2, axiom, [hasSymptom(fever), -hasDisease(pneumonia)]).
cnf(c2, axiom, [hasSymptom(cough), -hasDisease(pneumonia)]).
cnf(c2, axiom, [hasSymptom(headache), -hasDisease(pneumonia)]).
cnf(c2, axiom, [hasSymptom(thrills), -hasDisease(pneumonia)]).

cnf(c2, axiom, [hasSymptom(fever), -hasDisease(syphilis)]).
cnf(c2, axiom, [hasSymptom(throatache), -hasDisease(syphilis)]).
cnf(c2, axiom, [hasSymptom(headache), -hasDisease(syphilis)]).
cnf(c2, axiom, [hasSymptom(appetiteloss), -hasDisease(syphilis)]).
cnf(c2, axiom, [hasSymptom(stiffness), -hasDisease(syphilis)]).

cnf(c2, axiom, [hasSymptom(appetiteloss), -hasDisease(hepatitisB)]).
cnf(c2, axiom, [hasSymptom(tiredness), -hasDisease(hepatitisB)]).
cnf(c2, axiom, [hasSymptom(vomiting), -hasDisease(hepatitisB)]).
cnf(c2, axiom, [hasSymptom(itching), -hasDisease(hepatitisB)]).
cnf(c2, axiom, [hasSymptom(headache), -hasDisease(hepatitisB)]).

cnf(c2, axiom, [hasSymptom(appetiteloss), -hasDisease(hepatitisC)]).
cnf(c2, axiom, [hasSymptom(tiredness), -hasDisease(hepatitisC)]).
cnf(c2, axiom, [hasSymptom(vomiting), -hasDisease(hepatitisC)]).
cnf(c2, axiom, [hasSymptom(diarrhea), -hasDisease(hepatitisC)]).
cnf(c2, axiom, [hasSymptom(headache), -hasDisease(hepatitisC)]).

cnf(c2, axiom, [hasSymptom(appetiteloss), -hasDisease(hepatitisA)]).
cnf(c2, axiom, [hasSymptom(tiredness), -hasDisease(hepatitisA)]).
cnf(c2, axiom, [hasSymptom(vomiting), -hasDisease(hepatitisA)]).
cnf(c2, axiom, [hasSymptom(diarrhea), -hasDisease(hepatitisA)]).
cnf(c2, axiom, [hasSymptom(headache), -hasDisease(hepatitisA)]).
cnf(c2, axiom, [hasSymptom(fever), -hasDisease(hepatitisA)]).

cnf(c2, axiom, [hasSymptom(diarrhea), -hasDisease(gastroenteritis)]).
cnf(c2, axiom, [hasSymptom(fever), -hasDisease(gastroenteritis)]).
cnf(c2, axiom, [hasSymptom(vomiting), -hasDisease(gastroenteritis)]).

cnf(c2, axiom, [hasSymptom(mucus), -hasDisease(tuberculosis)]).
cnf(c2, axiom, [hasSymptom(weightloss), -hasDisease(tuberculosis)]).
cnf(c2, axiom, [hasSymptom(appetiteloss), -hasDisease(tuberculosis)]).
cnf(c2, axiom, [hasSymptom(cough), -hasDisease(tuberculosis)]).

cnf(c2, axiom, [hasSymptom(headache), -hasDisease(meningitis)]).
cnf(c2, axiom, [hasSymptom(fever), -hasDisease(meningitis)]).
cnf(c2, axiom, [hasSymptom(vomiting), -hasDisease(meningitis)]).
cnf(c2, axiom, [hasSymptom(meckstiffness), -hasDisease(meningitis)]).

cnf(c2, axiom, [hasSymptom(fever), -hasDisease(tonsillitis)]).
cnf(c2, axiom, [hasSymptom(tiredness), -hasDisease(tonsillitis)]).

cnf(c5, axiom, [hasSymptom(mucus), -hasDisease(cold)]).

cnf(d1, top_clause, [-hasSymptom(fever)]).
cnf(d2, top_clause, [-hasSymptom(mucus)]).
cnf(d3, top_clause, [-hasSymptom(headache)]).
cnf(d3, top_clause, [-hasSymptom(throatache)]).
cnf(d3, top_clause, [-hasSymptom(appetiteloss)]).
cnf(d3, top_clause, [-hasSymptom(vomiting)]).
cnf(d3, top_clause, [-hasSymptom(cough)]).
cnf(d3, top_clause, [-hasSymptom(throatpain)]).
cnf(d3, top_clause, [-hasSymptom(weightloss)]).
cnf(d3, top_clause, [-hasSymptom(tiredness)]).
cnf(d3, top_clause, [-hasSymptom(diarrhea)]).
cnf(d3, top_clause, [-hasSymptom(stiffness)]).
cnf(d3, top_clause, [-hasSymptom(neckstiffness)]).
cnf(d3, top_clause, [-hasSymptom(itching)]).
cnf(d3, top_clause, [-hasSymptom(thrills)]).

pf([hasSymptom(_)] < 2).

