# -*- coding: utf-8 -*-
#/home/magma/Documents/dcif/ScriptDCF/tools/buildGraph.jar
#/home/magma/Documents/dcif/ScriptDCF/tools/metis-4.0.3/kmetis
#/home/magma/Documents/dcif/ScriptDCF/tools/graph2dcf.jar


import sys
PROJECT_PATH = "/home/magma/projects/dcif/Etude/"
RSRC_PATH = PROJECT_PATH + 'ressources/'
GEN_PATH = PROJECT_PATH + 'gen/'
TOOLS_PATH = "/home/magma/projects/dcif/ScriptDCF/tools/"

BUILDGRAPH_JAR = TOOLS_PATH + "buildGraph.jar"
KMETIS_EX = TOOLS_PATH + "metis-4.0.3/kmetis"
GRAPH2DCF_JAR = TOOLS_PATH + "graph2dcf.jar"
MAKESOLVARIANT_JAR = TOOLS_PATH + "makeSolVariant.jar"
CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_4.jar'


from subprocess import *
def jarWrapper(*args):
    '''    
        http://stackoverflow.com/questions/7372592/python-how-can-execute-a-jar-file-through-a-python-script
        RQ : *args signifie ici un nombre variable d'arguments
    '''
    print list(args)
    process = Popen(['java', '-jar']+list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith('\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split('\n')
    if stderr != '':
        ret += stderr.split('\n')
    ret.remove('')
    print ret
    return ret
def exWrapper(*args):
    print args
    process = Popen(list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith('\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split('\n')
    if stderr != '':
        ret += stderr.split('\n')
    ret.remove('')
    print ret
    return ret



import argparse
parser = argparse.ArgumentParser(description='Process to test the DCIF.')
parser.add_argument('infile', 
                    help='input file with ext .sol')
                    
parser.add_argument('outfile', 
                    help='output file for the debug (ext .csv will be added)')

parser.add_argument('-m', '--method', default='DICF-PB-Async',
help='method used for DCIF : Async,  token, or star-based \nchoices=[DICF-PB-Async, DICF-PB-Token, DICF-PB-Star\]')

parser.add_argument('-v', '--verbose', action='store_true')

parser.add_argument('-n', '--numagent',type=int , required=True,
                    help = 'an integer for the number of agents on which it is distributed')

parser.add_argument('-t', '--timeout',type=int , 
                    help = 'an integer for the timeout')

parser.add_argument('--var', 
                    help = 'var=varSuffix  use the variant with given suffix (should begin by _).')

parser.add_argument('--par', choices=['naive_eq', 'naive_indent','kmetis'], default = 'kmetis',
                    help = 'type de partitionnement')
    


#class ArgHolder :
#    pass
#argHolder = ArgHolder()                          
#args = parser.parse_args(namespace=argHolder                                   
args = parser.parse_args()
argsDict = vars(args)
print 'les arguments passés sont :', argsDict
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
#PROCESS
#PREREQUIS :ON A UN FICHIER SOL
#ON A LES FICHIERS CLIQUE pour le bon nombre d'agents

##############################################################################
#######SOIT
from agentPartitioning import *
import os
def flowPar():
    temp_graph_filename=GEN_PATH + 'temp_graph'
    if  argsDict['par'] ==  'kmetis':
        ########On utilise un partitionnement un peu opti:
        ########buildGraph    
        try:
            os.remove(temp_graph_filename+'.gra')
            print 'removing old temporary file'
        except OSError:
            pass
        print 'buildGRAPH ################################################'   
        # on supprime le fichier s'il existe.       
        #
        args = [BUILDGRAPH_JAR, argsDict['infile'], temp_graph_filename ]
        result = jarWrapper(*args)
        
        ########kMetis
        print 'kMETIS ################################################'  
        args = [KMETIS_EX, temp_graph_filename+'.gra', str(argsDict['numagent']) ]
        result = exWrapper(*args)
        ########graph2DCF
        print 'graph2DCF ################################################'  
        args = [GRAPH2DCF_JAR, argsDict['infile'], temp_graph_filename+'.gra.part.'+str(argsDict['numagent']), argsDict['infile'][:-4] ]
        result = jarWrapper(*args)
    elif argsDict['par'] == 'naive_eq' :
        print 'naivePARTITIONING ################################################'  
        divEquNaive(argsDict['infile'], temp_graph_filename, argsDict['numagent'])
        ########graph2DCF
        print 'graph2DCF ################################################' 
        args = [GRAPH2DCF_JAR, argsDict['infile'], temp_graph_filename+'.gra.part.'+str(argsDict['numagent']), argsDict['infile'][:-4] ]
        result = jarWrapper(*args)
flowPar()
#IF NONE
#######SOIT
########on fait un partitionnemnt naïf
########SOL -> DCF : using graph2DCF
###############################################################################


###############################################################################
#on envoie le problème a DCIF avec une variante ou pas...
#args = [CFLAUNCHER_JAR, RSRC_PATH+argsDict['infile'].name, 
#RSRC_PATH+argsDict['outfile'].name, '-method=DICF-PB-Async',
# '-verbose','var=_max-4_ld-1--1' ]

args = [CFLAUNCHER_JAR, 
        '-method='+argsDict['method']]

if argsDict['verbose'] == True :
    print 'verbose'
    args.append('-verbose')
if argsDict['timeout'] != None :
    print argsDict['timeout']
    args.append('-t='+str(argsDict['timeout']))
if argsDict['var'] != None :
    print argsDict['var']
    args.append('-var='+argsDict['var'])
#adding arguments at the end because (cf CfLauncher.java ^^).
args = args +[argsDict['infile'], GEN_PATH+argsDict['outfile']]

print 'CFLAUNCHER ################################################' 
result = jarWrapper(*args)  

import os
char_clauses = result
print len(result)
#on écrit les résultats dans un fichier pour un traitement ultérieur
#print os.path.basename(argsDict['infile'])
outfile_csq = os.path.basename(argsDict['infile'])[:-4] + '.csq'
f = open(GEN_PATH+outfile_csq, 'w')

for line in char_clauses :
    f.write(line+'\n')
#f.writelines(char_clauses)

f.close()

#and compare the result with what we should have had
#python test.py ressources/glucolysis.sol debug_test1.csv -n 4 --var _max-4_ld-1--1 --verbose -t 10000
##
#//-var=_max-4_ld-1--1 -method=DICF-PB-Token-FixedOrder-3-2-1-0 -dist=_kmet4 -verbose glucolysis.sol debugGluc.csv
#//-var=_max-4_ld-1--1 -method=DICF-PB-Async -dist=_kmet12 -verbose -t=3000 glucolysis.sol debugGluc.csv
#//-method=DICF-PB-Async -dist=_def -verbose toy-pb1.sol debugi.csv
