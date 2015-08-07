# -*- coding: utf-8 -*-
import sys
from subprocess import *
import os
import re
import csv
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
CFLAUNCHER_JAR =RSRC_PATH + 'CFLauncher_9.jar'
#print('WORK DIR: '+os.getcwd() + "\n")
P2SOL_JAR = TOOLS_PATH + 'p2sol.jar'

def addToLog(log_file, toAdd) :
    for line in toAdd :
        log_file.write(line + '\n')
        print line

def writeToLog(log_filename, toAdd) :
    with open(log_filename + '.log', 'a') as log_file :
        for line in toAdd :
            log_file.write(line + '\n')
            print line
        
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
    return ret

def computeArgs_old(EXE,argsDict,exe_args=[]):
    args =  exe_args + [EXE]
    args = args + ['-method='+argsDict['method']]
    
    if argsDict['verbose'] == True :
        args.append('-verbose')
    if argsDict['timeout'] != None :
        args.append('-t='+str(argsDict['timeout']))
    if argsDict['var'] != None :
        args.append('-var='+argsDict['var'])
    if argsDict['dist'] != None :
        args.append('-dist='+argsDict['dist'])    
    if argsDict['csq'] != None :
        args.append('-csq='+argsDict['csq'])  
        
    #adding arguments at the end because (cf CfLauncher.java ^^).
    args = args + [argsDict['infile'], argsDict['outfile']]
    return args
    
def generate_kmet_distribution(argsDict, log_file,gen_path,java_args=[]):
  #generate DCF
    #if already exist do nothing, keep it all the same
    dist_outfile_path = argsDict['infile_path']
    dist_outfile_name = argsDict['infile']+argsDict['dist']
    if os.path.isfile(dist_outfile_path+dist_outfile_name+'.dcf') : 
         addToLog(log_file,['dist_file already exist, nothing is done'])
         return dist_outfile_path,dist_outfile_name
    temp_graph_filename=gen_path+argsDict['infile']+'temp_graph'+argsDict['dist']
    ########buildGraph    
    try:
        os.remove(temp_graph_filename+'.gra')        
        addToLog(log_file,['removing old temporary file .gra'])
    except OSError:
        addToLog(log_file,['temporary file did not exist'])
        pass                
    addToLog(log_file,['buildGraph #########################################'])
    args = java_args+[BUILDGRAPH_JAR]+[argsDict['infile_path']+argsDict['infile'], temp_graph_filename ]
    log = jarWrapper(*args)
    addToLog(log_file,log)
    ########kMetis
    try:
        os.remove(temp_graph_filename+'.gra.part.'+argsDict['numagent'])
        addToLog(log_file, ['removing old temporary file .gra.part.N'])
    except OSError:
        addToLog(log_file, ['temporary file did not exist'])
        pass
    addToLog(log_file,['kMETIS #############################################'])
    args = [KMETIS_EX, temp_graph_filename+'.gra', argsDict['numagent']]
    log = exWrapper(*args)
    addToLog(log_file,log)
    ########graph2DCF
    addToLog(log_file,['graph2DCF ##########################################']) 
    
    args = java_args+[GRAPH2DCF_JAR]+[argsDict['infile_path']+argsDict['infile'],temp_graph_filename+'.gra.part.'+argsDict['numagent'] , dist_outfile_path+dist_outfile_name+'.dcf']
    log = jarWrapper(*args)
    addToLog(log_file,log)
    #clean un peu...
    if os.path.isfile(temp_graph_filename+'.gra') : 
        os.remove(temp_graph_filename+'.gra')
    if os.path.isfile(temp_graph_filename+'.gra.part.'+argsDict['numagent']) : 
        os.remove(temp_graph_filename+'.gra.part.'+argsDict['numagent'])
    return dist_outfile_path,dist_outfile_name
    
def generate_kmet_distribution_simple(nbAgent,infile_path,infile_name,outfile_path = None,outfile_name=None) :
    java_args=['-jar']
    nbAgent = str(nbAgent)
    if outfile_path == None:
        outfile_path = infile_path
        
    if outfile_name == None:
        dist = '_kmet'+str(nbAgent)
        outfile_name = infile_name + dist
       
    if os.path.isfile(outfile_path+outfile_name+'.dcf') : 
         return outfile_path,outfile_name
         
    temp_graph_file=infile_path+outfile_name+'tempgraph'
    ########buildGraph    
    try:
        os.remove(temp_graph_file+'.gra')        
    except OSError:
        pass                
    args = java_args+[BUILDGRAPH_JAR]+[infile_path+infile_name, temp_graph_file ]
    log = jarWrapper(*args)
    ########kMetis
    try:
        os.remove(temp_graph_file+'.gra.part.'+nbAgent)
    except OSError:
        pass
    args = [KMETIS_EX, temp_graph_file+'.gra', nbAgent]
    log = exWrapper(*args)
    ########graph2DCF
    args = java_args+[GRAPH2DCF_JAR]+[infile_path+infile_name,temp_graph_file+'.gra.part.'+nbAgent, outfile_path+outfile_name+'.dcf']
    log = jarWrapper(*args)
    #clean un peu...
    if os.path.isfile(temp_graph_file+'.gra') : 
        os.remove(temp_graph_file+'.gra')
    if os.path.isfile(temp_graph_file+'.gra.part.'+nbAgent) : 
        os.remove(temp_graph_file+'.gra.part.'+nbAgent)
    return outfile_path,outfile_name
    
    
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

def get_problem_files(problem_path,ext):
    res = []
    for file in os.listdir(problem_path):
        if file.endswith(ext):
            res.append(file)
    return res    

def make_dirs_safe(path):
    try: 
        os.makedirs(path)
    except OSError:
        if not os.path.isdir(path):
            raise

def read_csv_config(filename, ext,log_file):
    res= []
    if not os.path.isfile(filename+ext) :
        addToLog(log_file,['pas de fichier '+filename])
    with open(filename+ext) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            res.append(rowDict)
    return res

def read_config(filename,ext,log_file):
    res= []
    if not os.path.isfile(filename+ext) :
        addToLog(log_file,['pas de fichier '+filename])
    with open(filename+ext,'r') as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            res.append(rowDict)
    return res
def reset_config(filename,ext,log_file):
    with open(filename+ext,'w') as csvfile:
        pass
    

def outputs_config_file_one_row(argsDict,fieldnames, filename, ext,gen_path,lock=None):
    output_file =gen_path + filename + ext
    file_exist = False
    if os.path.isfile(output_file) : 
        file_exist = True
    if lock!= None:
        lock.acquire()
    with open(output_file, 'a') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames,extrasaction='ignore')
        if not file_exist :
            writer.writeheader()
        writer.writerow(argsDict)
        if lock!= None:
            lock.release()
            
def outputs_config_file_all_rows(l_argsDict,fieldnames, filename, ext,gen_path,lock=None):
    output_file =gen_path + filename + ext
    file_exist = False
    if os.path.isfile(output_file) : 
        file_exist = True
    if lock!= None:
        lock.acquire()
    with open(output_file, 'a') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames,extrasaction='ignore')
        if not file_exist :
            writer.writeheader()
        for argsDict in l_argsDict :
            writer.writerow(argsDict)
    if lock!= None:
        lock.release()
def remove_ext(problem_filenames, ext):
    res=[]
    for filename in problem_filenames :
        if filename.endswith(ext):
            filename_noext = filename[:-len(ext)]
            res.append(filename_noext)
    return res