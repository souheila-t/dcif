# -*- coding: utf-8 -*-
'''
    module permet de vérifier l'efficacité de l'algorithme en comparant les résultats produits
    (basé sur un ordre)
'''

'''
def numeric_compare(x, y):
        return x - y
>>> sorted([5, 2, 4, 1, 3], cmp=numeric_compare)
[1, 2, 3, 4, 5]
'''
'''
   def __cmp__(self,other):
        
cmp(x, y)

    Compare the two objects x and y and return an integer according
    to the outcome. The return value is negative if x < y,
    zero if x == y and strictly positive if x > y.

return cmp(self.count,other.count)
'''


class Cnf :
    '''
    a Cnf is a disjonction of one or more Propositions or Pred (mixed here 
    the Cnf arg is a list
    '''
    def __init__(self,args):
        self.args = args 
        #creer propre fonction de comparaison
    def subsumes(self,cnf):
        for obj1 in self.args :
            for obj2 in cnf.args :
                if 
            
        
    def is_subsumed(self, cnf):
        pass

'''
QUESTION : EST CE QU 'ono peut AVOIR DES PROP ET DES PRED DANS UNE MEME Cnf ???
REGARDER DANS LES FICHIERS DE REPONSES 

mETTONS QU ON SUPPOSE QUE OUI :
    UNE CNF AVEC PLUS DE PROP EST < UNE CNF AVEC MOINS.(for en iterant sur les 2 en meme temps)
    ecrire algo sur papier
'''

class Prop():
    def __init__(self,name):
        self.name = name
    def is_prop(self):
        return True
    def is_pred(self):
        return False

class Pred():
    '''
        prend un nombre variable d'arguments
    '''
    def __init__(self,name, variables):
        self.name = name
        self.variables 
    def is_prop(self):
        return False
    def is_pred(self):
        return True
    pass

def comp_NAME(obj1, obj2):
    if obj1.name < obj2.name :
        return -1
    elif obj1.name > obj2.name :
        return 1
    else : 
        return 0
    
def comp_PRED(pred1, pred2):
    if pred1.name < pred2.name :
        return -1
    elif pred1.name > pred2.name :
        return 1
    else :         
        return comp_VARS(pred1.variables, pred2.variables)
        
def comp_VARS(l1,l2):
    '''
    un prédicat a toujours le mêm nombre d'arguments ?
    On le suppose ici,
    il faudra surement changer
    '''
    for e1,e2 in zip(l1,l2) :
        if e1==e2 :continue
        if e1 < e2 :return -1
        if e1 > e2 :return 1
    return 0 

def algo_TRI_Cnf(cnfs):
    '''
    Input : file1=result( MonoAgent ou MultiAgent = même ecriture ??)
    
    On parse les fichiers et on les stocke sous forme d'objets
    chaque ligne -> Cnf
    chaque Cnf -> (Pred|Prop)+
    
    
    On trie chaque fichier 
    (Rq, avec notre implémentation ce n'est pas nécessaire de sorter les lignes
    mais ça permet une meilleure efficacité dans la recherche de certains types
    de Cnfs).
    Chaque ligne doit être triée : on fait appel au sort interne de python et il
    faut implémenter une comparaison entre les objets de chaque Cnf

        1ere PARTIE du TRI : COMP_NOM
        différents cas pour (obj1, obj2) : (if obj1 < obj2 return -1 else if == return 0 else 1)
            if obj1 isProp and obj2 isPred, return -1
            if obj1 is Pred and obj2 isProp, return 1
            if (obj1 isProp and obj2 isProp) or (if obj1 isPred and obj 2 is pred), return COMPARAISOMLexicographique de (obj1.getname,obj2.getname)
        => Cnfs à peu près triées mais pas pour les predicats
         
        
        2eme PARTIE du TRI : 
        pour chaque Cnf : on coupe la queue à partir du premier predicat. (trié dans ordre propr < pred)
        et pour chaque queue, on numérote dans l'ordre d'apparition les VAR et on les renomme (type: int)
        on sorte ensuite la liste des VARs (arg) (sort numérique simple)
        on sorte ensuite les pred avec COMP_PRED(pred1,pred2) ici (qui utilise COMP_VARS)
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
        
        puis on prend la liste des pred triées et on la reappend à la liste des prop pour la Cnf
        
        
        
        OPTIMISER en faisant ligne par ligne entraabillant sur le fichier...
    '''
    for c in cnfs:  
        c.sort(cmp=comp_NAME)# not this because this makes a copy. sorted(cl, cmp=comp_NOM)
        
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
            
        preds.sort(cmp=comp_PRED)
    
def algo_CHECK_RESULTS():
#    on a deux listes de CNF TTRIEes t onc ompar
    pass    