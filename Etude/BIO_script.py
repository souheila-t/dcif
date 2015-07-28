# -*- coding: utf-8 -*-
#Mettre le time out a une valeur assez élevée... ça dépend des sytemes = car timout en ms et non en nb d'opérations... mettre a 10 mn
timeout_MONO = 10000
timeout_MULTI = 10000

#VAR
length_list = [-1,2,3,4,5]
depth_list =[-1]
meth_list = [ 'max-','min-', 'all']
methN_list = [2,4,5,6,8,10,15,20] #a varier en fonction de la taille de la theorie = si ne genere pas suffisamment de top_clauses alors  
#verifier que kmetis ne laisse aucun agent vide sinon eliminer la variante
#MULTI
numagent_list = [2,4,6,8]



import os
import csv
from commons import *

PROBLEM_PATH = 'Problems/Bio/'
methods = ['DICF-PB-Async', 'DICF-PB-Star', 'DICF-PB-Token']
filename_finishing_problems = PROBLEM_PATH+'config_BIO_finishing_problems' + '.csv'
log_base_filename= 'log_'
#working dir is ETUDE/
#print(os.getcwd() + "\n")

'''
length_list = [2]
depth_list =[-1]
meth_list = [ 'max-']
methN_list = [2]
#MULTI
numagent_list = [2]
'''

def generate_parameters_makesolvar() :
    '''
    generates tuples of arguments exploring parameters of make sol var
    '''
    lArgs =  []
    for length in length_list:
        for d in depth_list  :      
            for meth in meth_list:
                if meth != 'all' :
                    for meth_N in methN_list:
                        args = (meth+str(meth_N), str(length),str(d))
                        lArgs.append(args)
                else :
                    args = (meth,str(length),(d))
                    lArgs.append(args)                    
    return lArgs      

def saveLog(log, filename):
    '''
    on save le log a la fin, il faut ecrire en continue, abandonner cette approche
    '''
    f = open(filename, 'w')
    for line in log :
        f.write(line+'\n')
    f.close()
    
def addToLog(log_file, toAdd) :
    for line in toAdd :
        log_file.write(line + '\n')
        print line
    
def generate_parameters_algos_multi():
    lArgs=[]
    
    for numagent in numagent_list:
        dist = '_kmet'+str(numagent)
        args = (str(numagent), dist)
        lArgs.append(args)        
    return lArgs
    
def generate_distribution(argsDict, numagent, dist, log_file) :
    #generate DCF
    #if already exist do nothing, keep it all the same
    #here only 1 kind of partitioning   
    temp_graph_filename=PROBLEM_PATH + 'temp_graph'
    ########On utilise un partitionnement un peu opti:
    ########buildGraph    
    try:
        os.remove(temp_graph_filename+'.gra')
        log= ['removing old temporary file']
        addToLog(log_file,log)
    except OSError:
        log= ['temporary file did not exist']
        addToLog(log_file,log)
        pass                
    log= ['buildGraph ################################################'  ]
    addToLog(log_file,log)
    args = [BUILDGRAPH_JAR, argsDict['infile'], temp_graph_filename ]
    log = jarWrapper(*args)
    addToLog(log_file,log)
    
    ########kMetis
    try:
        os.remove(temp_graph_filename+'.gra.part.'+numagent)
        log= ['removing old temporary file']
        addToLog(log_file,log)
    except OSError:
        log= ['temporary file did not exist']
        addToLog(log_file,log)
        pass
    log= ['kMETIS ################################################']
    addToLog(log_file,log)
    args = [KMETIS_EX, temp_graph_filename+'.gra', numagent]
    log = exWrapper(*args)
    addToLog(log_file,log)
    ########graph2DCF
    log=[ 'graph2DCF ################################################'    ]        
    addToLog(log_file,log)                  
    args = [GRAPH2DCF_JAR, argsDict['infile'],temp_graph_filename+'.gra.part.'+numagent , argsDict['infile']+dist+'.dcf']
    log = jarWrapper(*args)
    addToLog(log_file,log)
    return log

import re
def get_number_first_agent_having_top_clause(filename):
    '''
    filename of the distribution file (dcf)
    optimized version
    '''    
    if not (os.path.isfile(filename+'.dcf')):
        print 'file non existing'
    print 'dcf existe ici :',filename
    f = open(filename+'.dcf', 'r')
    numAgent = 0
    for line in f:           
        if line.startswith('agent'):
            re.findall(r'\d+', 'hello 42 I\'m a 32 string 30')
            numAgent = re.findall(r'\d+', line)[0]
            print 
        if line.startswith('cnf'):
            if 'top_clause' in line:
                return int(numAgent)
    print 'pas de top_clause dans ce .dcf'
    return -1
    
def get_string_tokens(firstToken, numagent):
    res_tokens = ""
    tokens = range (firstToken,firstToken+numagent)
    for token in tokens :
        if token > numagent :
            res_tokens+=str(token%numagent)+'-'
        else:
            res_tokens+=str(token)+'-'
    return res_tokens[:-1]
    
def printConfigFile_algos(method, outfilename, parameters_algos_multi,argsDict_mono):    
    fieldnames_order = ['infile','outfile','method','var','numagent','dist','csq','verbose','timeout']
    
    if not(os.path.isfile(outfilename)) : 
        with open(outfilename, 'a') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames_order)
            writer.writeheader()
             
    with open(outfilename, 'a') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames_order)
        
        lCsq = []
        for numagent, dist in parameters_algos_multi :
            argsDict = dict()                       
            argsDict['infile'] = argsDict_mono['infile']
            argsDict['var'] = argsDict_mono['var']
            argsDict['verbose'] = True 
            argsDict['timeout'] = timeout_MULTI 
            argsDict['dist'] = dist
            
            #in another directory ?
            argsDict['csq'] = argsDict_mono['outfile']+'_MULTI_'+method
            lCsq.append(argsDict['csq'])
            argsDict['numagent'] = numagent
            argsDict['outfile'] = argsDict_mono['outfile']+'.csv'#on les met ensemble            
            if method == 'DICF-PB-Token':
                firstToken = get_number_first_agent_having_top_clause(argsDict['infile']+dist)
                method += '-FixedOrder-'+get_string_tokens(firstToken,int(argsDict['numagent']))            
            argsDict['method'] = method
            
            writer.writerow(argsDict)
        return lCsq
def printConfigFile_comp(lCsq, mono_csq, filename):
    with open(filename, 'w') as csvfile:
        fieldnames_order = ['compRef','comp']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames_order)
        writer.writeheader()
        for csq in lCsq :
            argsDict= dict()
            argsDict['compRef'] = mono_csq
            argsDict['comp'] = csq
            writer.writerow(argsDict) 
            
def print_header_configFile(filename, fieldnames_order) :
    if not(os.path.isfile(filename)) : 
        with open(outfilename, 'a') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames_order)
            writer.writeheader()
def generate_variant(method, length, depth, argsDict_mono, log_file):
    #MAKE VAR
    args = [MAKESOLVARIANT_JAR, '-method='+method,'-len='+str(length),'-d='+str(depth),argsDict_mono['infile'], argsDict_mono['outfile']]
    log = jarWrapper(*args)  
    addToLog(log_file,log)
def launch_mono(argsDict,log_file):                
    args = computeArgs(CFLAUNCHER_JAR, argsDict)
    addToLog(log_file,args)
    log = jarWrapper(*args)
    addToLog(log_file,log)
    return log

#version trop compliquée pour rien
def is_timeout(argsDict_mono,log_file):                        
    filename_csq = argsDict_mono['csq']+'.csq'
    if os.path.isfile(filename_csq):
        log = ["file does exist at this time\n"]
        addToLog(log_file,log)
        log=[filename_csq]
        addToLog(log_file,log)
        f = open(filename_csq, 'r')
        head = f.readline()
        if head.startswith('timeout'):      
            log=[head[8]]
            addToLog(log_file,log)
            ##ATTENTION CHANGER CA QUAND OPERATIONNER::: 
            '''
            PASSEN SIE AUF
            '''
            if int(head[8]) == 1:
           # if False :
                log=["timeout occured"]
                addToLog(log_file,log)
                #do not write in config file
                return True
            else :
                log=['timeout did not occur']
                addToLog(log_file,log)
                
                return False                 
        else:
            log=["timeout not written in .csq file"]
            addToLog(log_file,log)
            return True #problem we do nothing
    else:
        log=[ "no such file"]

        addToLog(log_file,log)
        return True

def generate_config_and_distribution(argsDict,log_file):
    log=["timeout did not occur"] 
    addToLog(log_file,log)
    parameters_algos_multi = generate_parameters_algos_multi()
    for numagent, dist in parameters_algos_multi :
        log= generate_distribution(argsDict, numagent, dist, log_file)    
        addToLog(log_file,log)
    lCsqFilenames=[]                        
    for method in methods:
        filename = PROBLEM_PATH+'config_'+ method+'.csv'
        lCsqFilenames += printConfigFile_algos(method, filename, parameters_algos_multi,argsDict)
    printConfigFile_comp(lCsqFilenames, argsDict['csq'],PROBLEM_PATH+'config_comp.csv') 
    