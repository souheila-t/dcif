# -*- coding: utf-8 -*-
#/home/magma/Documents/dcif/ScriptDCF/tools/buildGraph.jar
#/home/magma/Documents/dcif/ScriptDCF/tools/metis-4.0.3/kmetis
#/home/magma/Documents/dcif/ScriptDCF/tools/graph2dcf.jar


import sys
from subprocess import *
from commons import *
import argparse
TEMP_PATH = 'temp/'
CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_9.jar'
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
    parser.add_argument('--csq',
                        help = 'outputfilename for csq')
            
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

def old_flow(argsDict):
    if argsDict['dist'] != None :
        return
    if (argsDict['numagent'] == None) or (argsDict['numagent'] == 1 ) :
        print 'MONO AGENT, by default'
        #check if method basée est bien celle la : facultatif
        if  argsDict['method'] != 'SOLAR-Inc-Carc':
            print 'Methode Mono Agent non passée, mis par défaut en mode Mono'
            argsDict['method'] = 'SOLAR-Inc-Carc'
                
        
    else:
        temp_graph_filename=TEMP_PATH + 'temp_graph'
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
            args = ['-jar', BUILDGRAPH_JAR, argsDict['infile'], temp_graph_filename ]
            result = jarWrapper(*args)
            
            ########kMetis
            print 'kMETIS ################################################'  
            args = [KMETIS_EX, temp_graph_filename+'.gra', str(argsDict['numagent']) ]
            result = exWrapper(*args)
            ########graph2DCF
            print 'graph2DCF ################################################'  
            args = ['-jar',GRAPH2DCF_JAR, argsDict['infile'], temp_graph_filename+'.gra.part.'+str(argsDict['numagent']), argsDict['infile'][:-4] ]
            result = jarWrapper(*args)
        elif argsDict['par'] == 'naive_eq' :
            print 'naivePARTITIONING ################################################'  
            divEquNaive(argsDict['infile'], temp_graph_filename, argsDict['numagent'])
            ########graph2DCF
            print 'graph2DCF ################################################' 
            args = ['-jar',GRAPH2DCF_JAR, argsDict['infile'], temp_graph_filename+'.gra.part.'+str(argsDict['numagent']), argsDict['infile'][:-4] ]
            result = jarWrapper(*args)

def flow(argsDict):
    if (argsDict['numagent'] == None) or (argsDict['numagent'] == 1 ) :
        print 'MONO AGENT, by default'
        #check if method basée est bien celle la : facultatif
        if  argsDict['method'] != 'SOLAR-Inc-Carc':
            print 'Methode Mono Agent non passée, mis par défaut en mode Mono'
            argsDict['method'] = 'SOLAR-Inc-Carc'
    else:
        temp_graph_filename=TEMP_PATH + 'temp_graph'
        dist_suffix = '_kmet'+str(argsDict['numagent'])
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
        args = ['-jar', BUILDGRAPH_JAR, argsDict['infile'], temp_graph_filename ]
        result = jarWrapper(*args)
        
        ########kMetis
        print 'kMETIS ################################################'  
        args = [KMETIS_EX, temp_graph_filename+'.gra', str(argsDict['numagent']) ]
        result = exWrapper(*args)
        ########graph2DCF
        print 'graph2DCF ################################################'  
        args = ['-jar',GRAPH2DCF_JAR, argsDict['infile'], temp_graph_filename+'.gra.part.'+str(argsDict['numagent']), argsDict['infile'][:-4]+dist_suffix ]
        result = jarWrapper(*args)
        argsDict['dist'] = dist_suffix
        print argsDict['infile'][:-4]+dist_suffix
    return argsDict#pas besoin normalement...


###############################################################################


print 'CFLAUNCHER MULTI ################################################' 
#argsDict = flow(argsDict)
argsDict['dist'] = '_kmet4'
JAVA_ARGS = ['-jar']
print CFLAUNCHER_JAR
args = computeArgs_old (CFLAUNCHER_JAR,argsDict,exe_args=JAVA_ARGS)
print 'ZISI ARGS',args
result = jarWrapper(*args)  
print result

log_result = os.path.basename(argsDict['infile'])[:-4] +argsDict['var']+argsDict['method'] +'.log'
f = open('ressources/log.log', 'w')
for line in result :
    f.write(line+'\n')
f.close()


#python test.py ressources/glucolysis.sol gen/debug_test1.csv -n 2 --var _max-2_ld-1--1 --verbose -t 10000 --method DICF-PB-Token



#python test.py ressources/glucolysis.sol debug_test1.csv -n 4 --var _max-4_ld-1--1 --verbose -t 10000
##
#//-var=_max-4_ld-1--1 -method=DICF-PB-Token-FixedOrder-3-2-1-0 -dist=_kmet4 -verbose glucolysis.sol debugGluc.csv
#//-var=_max-4_ld-1--1 -method=DICF-PB-Async -dist=_kmet12 -verbose -t=3000 glucolysis.sol debugGluc.csv
#//-method=DICF-PB-Async -dist=_def -verbose toy-pb1.sol debugi.csv
