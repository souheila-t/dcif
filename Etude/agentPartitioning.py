# -*- coding: utf-8 -*-
"""
NOT TESTED IRS (in real situation)
prend un fichier .sol et un nombre d'agents en entrée et renvoie un fichier 
.gro.part.n qui répartit de manière naïve les clauses entre agents.

dans la premiere partie on ne s'occupe pas des TOP CLAUSES 
"""

def annotateLines(f):
    '''
    annotate lines with information used for partitioning
    '''
    l = []
    nbClauses=0
    nLines=0
    for line in f:
        print line
        if line[:3] == 'cnf':
            if 'top_clause' in line :
                l.append(('cnf',line,nbClauses,'top_clause'))
            else:
                l.append(('cnf',line,nbClauses,'axiom'))
            nbClauses += 1
        elif line[:1] == '%':
            pass
        elif line[:2] == 'pf':
            pass
        elif line.isspace():
            l.append(('blank',''))
        else:
            print 'error : .sol file contains invalid or unknown lines'
        nLines+=1
    print 'nb de clauses : ',nbClauses
    return l, nbClauses
    
def filterClauses(l):
    '''
    ne garde que les clauses
    '''
    res = []    
    for line in l :
        if line[0] == 'cnf' :
            res.append(line)
    return res
    
def chunks(l, n):
    """Yield successive n-sized chunks from l."""
    for i in xrange(0, len(l), n):
        yield l[i:i+n]
        
def divEquNaive(infilename,outfilename, nbAgents):
    '''
    1ere naive : divides the clauses equally between the agents
    and writes the output in a .gro.part.N file
    '''
    f = open(infilename, 'r')
    lines, nbClauses = annotateLines(f)
    f.close()
    lines = filterClauses(lines)    
    
    sizeAgent = nbClauses/nbAgents     
    outfilename = outfilename+'.gra.part.'+str(nbAgents)
    outfile = open(outfilename, 'w')
    i = 0
    for c in chunks(lines, sizeAgent):
        print c
        for line in c :
            print line, i
            outfile.write(str(i)+'\n')
        if i != nbAgents - 1 :
            i += 1
    outfile.close()    
    return 

def divEqu_test(f, nbAgents, filename):
    '''
    1ere naive : divides the clauses equally between the agents
    '''
    lines, nbClauses = annotateLines(f)
    lines = filterClauses(lines)   
    
    sizeAgent = nbClauses/nbAgents    
    outfilename = filename+'.gro.part.'+str(nbAgents)
    outfile = open(outfilename, 'w')
    i = 0
    for c in chunks(lines, sizeAgent):
        print c
        for line in c :
            print line, i
            outfile.write(str(i))
        i += 1
    outfile.close()    
    return 
    
def test_div():
    f = []
    f.append('cnf wdwwdwd')
    f.append('cnf wewrewww')
    f.append('cnf wewrewww3')
    f.append('cnf wewrewww3')
    f.append('cnf wewrewww3')
    f.append('cnf wewrewww3')
    f.append('wdw wd')
    print divEqu_test(f, 2, 'truc')


'''
    2eme naive : on considère que l'indentation a un sens
    trouver les parties puis les assigner en modulo incrémentalement
'''



'''
    3eme moins naive : on crée d'abird le graphe (KMETIS) et on répartit les 
    TOPCLAUSES entre CHAQUE AGENT
'''