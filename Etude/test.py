# -*- coding: utf-8 -*-
#/home/magma/Documents/dcif/ScriptDCF/tools/buildGraph.jar
#/home/magma/Documents/dcif/ScriptDCF/tools/metis-4.0.3/kmetis
#/home/magma/Documents/dcif/ScriptDCF/tools/graph2dcf.jar


import sys
from commons import *
from subprocess import *

PROJECT_PATH = "/home/magma/projects/dcif/Etude/"
RSRC_PATH = PROJECT_PATH + 'ressources/'
GEN_PATH = PROJECT_PATH + 'gen/'
TOOLS_PATH = "/home/magma/projects/dcif/ScriptDCF/tools/"

BUILDGRAPH_JAR = TOOLS_PATH + "buildGraph.jar"
KMETIS_EX = TOOLS_PATH + "metis-4.0.3/kmetis"
GRAPH2DCF_JAR = TOOLS_PATH + "graph2dcf.jar"
MAKESOLVARIANT_JAR = TOOLS_PATH + "makeSolVariant.jar"
CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_5_csq.jar'



import argparse
def define_arguments():    
    parser = argparse.ArgumentParser(description='Process to test the DCIF.')
    parser.add_argument('infile', 
                        help='input file with ext .sol \n For better project \
                        organization, it is recommended to put the .sol file in \
                        the ressources folder and to add the raltive path as an \
                        argumeent such as ressrouces/mock.sol')
                        
    parser.add_argument('outfile', 
                        help='output file for the debug (ext .csv will be added)')
    
    parser.add_argument('-m', '--method', default='DICF-PB-Async',
                        help='method used for DCIF : Async,  token, or star-based\
                        \nchoices=[DICF-PB-Async, DICF-PB-Token, DICF-PB-Star\]')
    
    parser.add_argument('-v', '--verbose', action='store_true')
    
    parser.add_argument('-n', '--numagent',type=int ,
                        help = 'an integer for the number of agents on which it is\
                        distributed')
    
    parser.add_argument('-t', '--timeout',type=int , 
                        help = 'an integer for the timeout')
    
    parser.add_argument('--var', 
                        help = 'var=varSuffix  use the variant with given suffix \
                        (should begin by _).')
    
    parser.add_argument('--par', choices=['naive_eq', 'naive_indent','kmetis_hybrid','kmetis'], default = 'kmetis',
                        help = 'type de partitionnement \n\
                        naive_eq= divise les clauses equitablement (ne regarde pas si top_clauses ou axiom) \n\
                        naive_indent= divise les clauses selon l indentation du fichier sol d origine (pareil que dessus)\n\
                        eq_par= répartit équitablement ET les axiomes ET les Top_clauses\
                        kmetis_hybrid\
                        ,kmetis')
    parser.add_argument('--dist',
                        help = 'distSuffix  use the distribution with given suffix (should begin by \"_\").");')
        
    return parser
parser = define_arguments()
#class ArgHolder :
#    pass
#argHolder = ArgHolder()                          
#args = parser.parse_args(namespace=argHolder                                  
args = parser.parse_args()
argsDict = vars(args)
print 'les arguments passés sont :', argsDict

#PROCESS
#PREREQUIS :ON A UN FICHIER SOL
#ON A LES FICHIERS CLIQUE pour le bon nombre d'agents

##############################################################################
#######SOIT
from agentPartitioning import *
import os

def flow():
    if argsDict['dist'] != None :
        return
    if (argsDict['numagent'] == None) or (argsDict['numagent'] == 1 ) :
        print 'MONO AGENT, by default'
        #check if method basée est bien celle la : facultatif
        if  argsDict['method'] != 'SOLAR-Inc-Carc':
            print 'Methode Mono Agent non passée, mis par défaut en mode Mono'
            argsDict['method'] = 'SOLAR-Inc-Carc'
                
        
    else:
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
flow()
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
if argsDict['dist'] != None :
    print argsDict['dist']
    args.append('-dist='+argsDict['dist'])    
    
#adding arguments at the end because (cf CfLauncher.java ^^).
args = args + [argsDict['infile'], GEN_PATH+argsDict['outfile']]

print 'CFLAUNCHER MULTI ################################################' 
result = jarWrapper(*args)  
#problem : ici ON GARDE TOUTES LES TRACES D'EXÉCUTION EN MÉMOIRE, IL FAUDRAIT LES ECRIRE AU FUR ET A MESURE DANS UN FICHIER
#ET ON RECUPEREAIT ENSUITE SEULEMENT CE QUI NOUS INTERESSERAIT
#TODO REECRIRE JARwRAPPER, AVEC EN ARG UN OUTFILEnAME
import os
char_clauses = result
print len(result)
#on écrit les résultats dans un fichier pour un traitement ultérieur
#print os.path.basename(argsDict['infile'])
'''
outfile_csq = os.path.basename(argsDict['infile'])[:-4] + '.multicsq.log'
f = open(GEN_PATH+outfile_csq, 'w')

for line in char_clauses :
    f.write(line+'\n')
f.close()
'''
print 'CFLAUNCHER MONO################################################'

argsDict_MONO = argsDict.copy()
argsDict_MONO['numagent'] == 1
result = jarWrapper(*args)

'''
char_clauses = result
outfile_csq = os.path.basename(argsDict['infile'])[:-4] + '.monocsq.log'
f = open(GEN_PATH+outfile_csq, 'w')

for line in char_clauses :
    f.write(line+'\n')
f.close()
'''




##and now checking the results between mono agent and Multi
#and compare the result with what we should have had


#python test.py ressources/glucolysis.sol debug_test1.csv -n 4 --var _max-4_ld-1--1 --verbose -t 10000
##
#//-var=_max-4_ld-1--1 -method=DICF-PB-Token-FixedOrder-3-2-1-0 -dist=_kmet4 -verbose glucolysis.sol debugGluc.csv
#//-var=_max-4_ld-1--1 -method=DICF-PB-Async -dist=_kmet12 -verbose -t=3000 glucolysis.sol debugGluc.csv
#//-method=DICF-PB-Async -dist=_def -verbose toy-pb1.sol debugi.csv
