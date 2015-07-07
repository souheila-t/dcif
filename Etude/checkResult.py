# -*- coding: utf-8 -*-
'''
    module permet de vérifier l'efficacité de l'algorithme en comparant les résultats produits
    (basé sur un ordre)
'''

class Clause :
    '''
    a clause is a disjonction of one or more Propositions or Pred (mixed)
    '''
    def __init__(self,count):
        self.clause = clause

    def __cmp__(self,other):
        '''        
        cmp(x, y)

            Compare the two objects x and y and return an integer according
            to the outcome. The return value is negative if x < y,
            zero if x == y and strictly positive if x > y.
        '''
        return cmp(self.count,other.count)
        #creer propre fonction de comparaison
    pass

'''
QUESTION : EST CE QU 'ono peut AVOIR DES PROP ET DES PRED DANS UNE MEME CLAUSE ???
REGARDER DANS LES FICHIERS DE REPONSES 

mETTONS QU ON SUPPOSE QUE OUI :
    UNE CNF AVEC PLUS DE PROP EST < UNE CNF AVEC MOINS.(for en iterant sur les 2 en meme temps)
    ecrire algo sur papier
'''
class Prop():
    def __init__(self,name):
        self.name = name
    def is_prop():
        return True
    def is_pred():
        return False
    pass

class Pred():
    '''
        prend un nombre variable d'arguments
    '''
    def __init__(self,name, args):
        self.name = name
        self.args = args 
    def is_prop():
        return False
    def is_pred():
        return True
    pass
