# -*- coding: utf-8 -*-
'''
    module permettant de vérifier l'efficacité de l'algorithme en comparant les résultats produits
    (basé sur un ordre)
'''

'''
>>> sorted([5, 2, 4, 1, 3], cmp=numeric_compare)
[1, 2, 3, 4, 5]
cmp(x, y)
    Compare the two objects x and y and return an integer according
    to the outcome. The return value is negative if x < y,
    zero if x == y and strictly positive if x > y.
'''


class Clause :
    '''
    a Clause is a disjonction of one or more Propositions or Pred
    the Clause arg is a list
    on ne vaut pas trier les CNF (ou alors pour optimiser mais seulement pouvoir 
    verifier egalite et subsumption entre elles et pour cela ce sont ses arguments qui sont triés)
    '''
    def __init__(self,args):
        self.args = args 
        
    def subsumes(self,clause):
        for obj1 in self.args :
            is_in = False
            for obj2 in clause.args :
                if obj1.is_eq(obj2):
                    is_in = True
                    break
            if is_in == False:
                return False
        return True
        
    def is_subsumed(self, clause):
        clause.subsumes(self)

class Prop :
    def __init__(self,name):
        self.name = name
    def is_prop(self):
        return True
    def is_pred(self):
        return False
    def is_eq(self, obj):
        if obj.is_prop() != self.is_prop() or obj.is_pred() != self.is_pred() :
            return False
        if self.name() != obj.name:
            return False
        else :
            return True
    def __eq__(self, other):
        if isinstance(other, Prop):
            return self.foo == other.foo
        return NotImplemented
class Pred():
    '''
        prend un nombre variable d'arguments
        definition de PROLOG        
        Two clauses belong to the same predicate if they have the same functor (name) and the same arity (number of arguments).
        !!  : on ne regarde pas le nom des variables ici
    '''
    def __init__(self,name, variables):
        self.name = name
        self.variables 
    def is_prop(self):
        return False
    def is_pred(self):
        return True
    def is_eq(self, obj):
        if obj.is_prop() != self.is_prop() or obj.is_pred() != self.is_pred() :
            return False
        if self.name() != obj.name:
            return False
        if len(self.variables) != len(obj.variables):
            return False
        else :
            return True
        
def comp_All(obj1, obj2):
    '''
    Compare deux objets avec comme ordre :
    prop < pred
    et si type egal, compare les noms 
    '''  
    if obj1.is_prop() and obj2.is_pred() :
        return -1
    if obj1.is_pred() and obj2.is_prop():
        return 1
    if (obj1.is_prop() and obj2.is_prop) or ( obj1.is_pred() and obj2.is_pred()):
        return comp_Name(obj1,obj2)    

def comp_Name(obj1,obj2):    
    if obj1.name < obj2.name :
        return -1
    elif obj1.name > obj2.name :
        return 1
    else : 
        return 0
    
def comp_Pred(pred1, pred2):
    '''
    ici on trie les predicats de même nom par le nombre d'arguments
    ceux qui en ont le moins se retrouvent en premier
    '''
    if pred1.name < pred2.name :
        return -1
    elif pred1.name > pred2.name :
        return 1
    #NAME EGAL
    elif len(pred1.variables) < len(pred2.variables):
        return -1
    elif len(pred1.variables) > len(pred2.variables):
        return 1      
    #NBRE ARGS EGAL
    else:
        return comp_Vars(pred1, pred2)
        
def comp_Vars(pred1,pred2):
    '''
    PRECOND : même taille de liste, même nom
    '''
    l1 = pred1.variables
    l2 = pred2.variables
    for e1,e2 in zip(l1,l2) :
        if e1==e2 :continue
        if e1 < e2 :return -1
        if e1 > e2 :return 1
    return 0 

def algo_TRI_Clause(cnfs):
    '''
    Input : file1=result( MonoAgent ou MultiAgent = même ecriture ??)
    
    On parse les fichiers et on les stocke sous forme d'objets
    chaque ligne -> Clause
    chaque Clause -> (Pred|Prop)+
    
    
    On trie chaque fichier 
    (Rq, avec notre implémentation ce n'est pas nécessaire de sorter les lignes
    mais ça permet une meilleure efficacité dans la recherche de certains types
    de Clauses).
    Chaque ligne doit être triée : on fait appel au sort interne de python et il
    faut implémenter une comparaison entre les objets de chaque Clause

        1ere PARTIE du TRI : COMP_NOM
        différents cas pour (obj1, obj2) : (if obj1 < obj2 return -1 else if == return 0 else 1)
            if obj1 isProp and obj2 isPred, return -1
            if obj1 is Pred and obj2 isProp, return 1
            if (obj1 isProp and obj2 isProp) or (if obj1 isPred and obj 2 is pred), return COMPARAISOMLexicographique de (obj1.getname,obj2.getname)
        => Clauses à peu près triées mais pas pour les predicats
         
        
        2eme PARTIE du TRI : 
        pour chaque Clause : on coupe la queue à partir du premier predicat. (trié dans ordre propr < pred)
        et pour chaque queue, on numérote dans l'ordre d'apparition les VAR et on les renomme (type: int)
        on sorte ensuite la liste des VARs (arg) (sort numérique simple)
        on sorte ensuite les pred avec comp_Pred(pred1,pred2) ici (qui utilise comp_Vars)
            if compLexicogrpahique(pred1.name,pred2.name) < or > return res 
            |REDONDANT ICI il suffit de trier les sous-listes de prédicats de même nom (forcéement contigu) mais plus simple de faire ca = si probleme de perf on verra
            else (==)
                return compVARS(pred1.args,pred1.args)
                
        compVARS (l1,l2):
            for e1,e2 in l1,l2 :
                if e1==e2 continue
                if e1 < e2 return -1
                if e1 > e2 return 1
            return 0 
        
        puis on prend la liste des pred triées et on la reappend à la liste des prop pour la Clause
        
        
        
        OPTIMISER en faisant ligne par ligne entraabillant sur le fichier...
    '''
    for c in cnfs:  
        c.sort(cmp=comp_All)# not this => because this makes a copy. sorted(cl, cmp=comp_NOM)
        
        props=[]
        preds=[]
        for obj in c :
            if obj.is_prop():
                props.append(obj)
            if obj.is_pred():
                preds.append(obj)
                
        nVar=1
        dictVar = dict()        
        for p in preds :
            for v in p.variables:
                if dictVar[v] == None:
                    dictVar[v] = nVar
                    nVar+=1
                p.setVar = dictVar[v]   
            print p.variables 
            #on trie les variables
            p.variables.sort()#comparaison numerique standard 
            print p.variables 
            
        preds.sort(cmp=comp_Pred)
    
def algo_CHECK_RESULTS():
#    on a deux listes de CNF chacune aves ses arg triés
    


    pass    

import re
def open_csq(filename):
    '''
    TODO : OPTI :il faut compiler les expressions regulieres avant
    '''
    #cnf
    f = open(filename, 'r')
    cnf = []    
    iter_f = f.__iter__()
    for line in iter_f :
        if 'CHARACTERISTIC CLAUSES' in line :
            nbClauses = int(re.findall('\d+', line)[0])
            print 'il y a : ', nbClauses, 'new clauses'  
            break
    for line in iter_f:
        if nbClauses == 0:
            print 'no more clauses to read'            
            break
        #ici on n'a pas besoin de savoir ce que veut dire + ou -, on verifier
        # juste legalite des string
        clauses = re.findall('[\-|\+]?[\w]+(\(.*\))?',line)
        print clauses
        #ROBUSTE
        args_clause = []
        for p_str in clauses :
            print p_str
            if '(' in p_str:
                print 'predicat'
                name = re.findall('[\-|\+]?[\w]',p_str)
                print 'pred name', name.group()
                args  = re.sub('[\-|\+]?[\w]','',p_str)
                print args
                args = re.findall('\w+',args)
                print args
                p = Pred(name, args)                
                #parser predicat
                #p = new Pred()
            else:
                print 'prop'
                p = Prop(p_str)
            args_clause.append(p)
        clause = Clause(args_clause)
        cnf.append(clause)
        nbClauses -=1
    
    return cnf
filename = '/home/magma/projects/dcif/Etude/gen' + '/glucolysis_mono.csq'
filename = '/home/magma/projects/dcif/Etude/gen' + '/test_pred.csq'
cnf = open_csq (filename)

for c in cnf :
    for p in c.args :
        print p.name
        if p.is_pred():
            for v in p.variables:
                print v