# -*- coding: utf-8 -*-
import copy
import commons as com
def addArgToDict(name, data,lDicts):
    '''
    return the list of new dictionaries, does not change the structure given
    '''
    lNewDicts = copy.deepcopy(lDicts)
    for d in lNewDicts:
        d[name] = data
        
    return lNewDicts
def testArgToDict():
    d1 = dict()
    d2 = dict()
    
    d1['qq'] = 11
    d1['ee'] = 22
    
    d2['q1'] = 1111
    d2['e2'] = 2222
    
    l =[d1,d2]
    
    newL = copy.deepcopy(l)
    
    newL[0]['qq'] = 1111111
    
    print d1
    print newL[0]
    
#testArgToDict()
def generate(params,lDicts,cond):
    res = lDicts
    for key,value in params.iteritems():
        res = sub_generate(key,value,res,cond,prefix = '') #on reaffecte res pour propager les nouveaux paramètres
    return res

def sub_generate(name, params, lDicts, cond, prefix = '') :
    if isinstance(params, dict):
        res =[sub_generate(name, value, lDicts,cond,prefix = prefix + key) for key,value in params.iteritems()]  
        return com.merge_inner_lists(res)
    elif isinstance(params, list):
        res =[sub_generate(name, value, lDicts,cond,prefix = prefix) for value in params]  
        return com.merge_inner_lists(res)
    else:
        try:
            if not testCond(cond[name],name, params, prefix):
                return []
        except:
            pass
        return addArgToDict(name, prefix+str(params), lDicts)
    
def testCond(condString , name, params, prefix) :
    '''
    la condition doit etre passée 
    '''
    condString = "cond = "+condString
    exec(condString)    
    if cond:
        return False
    return True
    
def test(name, params, prefix):
    if name == 'var' and  'min' in prefix and params == 2:
        return False
    return True
def test_generateArgs():
    m1 = dict()
    m1['e']=0
    m2 = dict()
    m2['f'] = 1
    
    dall = dict()
    
    d1=dict()
    d2=dict()
    d1['min'] = [1,2]
    d2['max'] =[5,6]
    
    
    d3=dict()
    d3['min'] = [1,2]
    d3['max'] =[5,6]
    
    dall['var'] = ['all',d3]
   #dall['other'] = [11,99]
    cond = dict()
    cond['var']  = "name == 'var' and  'min' in prefix and params == 2"
                    
    res = generate(dall,[m1,m2],cond)
    print res
    print len(res)
    '''
    l1 = [d for d in res if d['other'] == str(11)]
    l2 =  [d for d in res if d['other'] == str(99)]
    
    print len(l1)
    print len(l2)
    '''
test_generateArgs()