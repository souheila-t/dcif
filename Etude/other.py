# -*- coding: utf-8 -*-



def is_a_file(f):
    try:
        f.read()
        return True
    except AttributeError:
        return False
        
"""
def fromObjDictToStrDict(origDict):
    '''
    tranasforme tous les objets dans le dictionnaire en string
    et pour les files, transorme le chemin relatif en chemin absolu
    '''
    res = dict()
    for key, value in origDict.iteritems():
        #if string cp normal
        if isinstance(value, str):
            res[key] = value
        if is_a_file(value):
            res[key] = value.name
        if isinstance( value, int ):
            res[key] = str(value)
        else:
            print 'type Error'    
    return res
newDict = fromObjDictToStrDict(argsDict)
print newDict
"""