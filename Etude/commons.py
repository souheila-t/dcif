# -*- coding: utf-8 -*-
import sys
from subprocess import *
import os
import re
'''
PROJECT_PATH = "/home/magma/projects/dcif/Etude/"
RSRC_PATH = PROJECT_PATH + 'ressources/'
GEN_PATH = PROJECT_PATH + 'gen/'
TOOLS_PATH = "/home/magma/projects/dcif/ScriptDCF/tools/"
BUILDGRAPH_JAR = TOOLS_PATH + "buildGraph.jar"
KMETIS_EX = TOOLS_PATH + "metis-4.0.3/kmetis"
GRAPH2DCF_JAR = TOOLS_PATH + "graph2dcf.jar"
MAKESOLVARIANT_JAR = TOOLS_PATH + "makeSolVariant.jar"

CNFSAT2SOL_JAR= TOOLS_PATH+"cnfsat2sol.jar"
CMPCSQ_JAR= TOOLS_PATH+"cmpCsq.jar"
#CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_6_csq_filename.jar'
CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_7.jar'
'''

PROJECT_PATH = ""
RSRC_PATH = PROJECT_PATH + 'ressources/'

TOOLS_PATH = "../ScriptDCF/tools/"
BUILDGRAPH_JAR = TOOLS_PATH + "buildGraph.jar"
KMETIS_EX = TOOLS_PATH + "metis-4.0.3/kmetis"
GRAPH2DCF_JAR = TOOLS_PATH + "graph2dcf.jar"
MAKESOLVARIANT_JAR = TOOLS_PATH + "makeSolVariant.jar"
CNFSAT2SOL_JAR= TOOLS_PATH+"cnfsat2sol.jar"
CMPCSQ_JAR= TOOLS_PATH+"cmpCsq.jar"
#CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_6_csq_filename.jar'
CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_7.jar'
#print('WORK DIR: '+os.getcwd() + "\n")

def addToLog(log_file, toAdd) :
    for line in toAdd :
        log_file.write(line + '\n')
        print line
'''
def addToLog(log_filename, toAdd) :
    with open(log_filename + '.log', 'w') as log_file :
        for line in toAdd :
            log_file.write(line + '\n')
            print line
'''                    
def jarWrapper(*args):
    '''    
        http://stackoverflow.com/questions/7372592/python-how-can-execute-a-jar-file-through-a-python-script
        RQ : *args signifie ici un nombre variable d'arguments
    '''
    print list(args)
    process = Popen(['java']+list(args), stdout=PIPE, stderr=PIPE)
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

def computeArgs(EXE,argsDict,exe_args=[]):
    args =  exe_args + [EXE]
    args = args + ['-method='+argsDict['method']]
    
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
    if argsDict['csq'] != None :
        print argsDict['csq']
        args.append('-csq='+argsDict['csq'])  
        
    #adding arguments at the end because (cf CfLauncher.java ^^).
    args = args + [argsDict['infile'], argsDict['outfile']]
    return args
    
def read_dcf(f):
    nb_axioms=0
    nb_top_clauses=0
    nb_ag=0
    isValid = False
    numAgent = 0
    l = []
    for line in f:   
        if line.startswith('agent'):
            isValid = True
            numAgent = re.findall(r'\d+',line)[0]
            nb_ag += 1
        elif isValid:
            if line.startswith('cnf'):
                if 'top_clause' in line:
                    l.append((numAgent,'top_clause', line))
                    nb_top_clauses+=1
                elif 'axiom' in line :#check if axiom or top_clause
                    l.append((numAgent,'axiom', line))
                    nb_axioms+=1
    return l, nb_ag, nb_axioms, nb_top_clauses

def get_problem_files(PROBLEM_PATH,ext):
    res = []
    for file in os.listdir(PROBLEM_PATH):
        if file.endswith(ext):
            print(file)
            res.append(file)
    return res    

def make_dirs_safe(path):
    try: 
        os.makedirs(path)
    except OSError:
        if not os.path.isdir(path):
            raise
