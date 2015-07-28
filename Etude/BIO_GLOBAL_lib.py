# -*- coding: utf-8 -*-
import os
import csv
from commons import *
def remove_ext(problem_filenames, ext):
    res=[]
    for filename in problem_filenames :
        if filename.endswith(ext):
            filename_noext = filename[:-len(ext)]
            res.append(filename_noext)
    return res

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
    args = args + [argsDict['infile_path']+argsDict['infile'], argsDict['outfile']]
    return args

def generate_distribution(argsDict, log_file,java_args=[]) :
  #generate DCF
    #if already exist do nothing, keep it all the same
    dist_outfile_path = argsDict['infile_path']
    dist_outfile_name = argsDict['infile']+argsDict['dist']
    if os.path.isfile(dist_outfile_path+dist_outfile_name+'.dcf') : 
         addToLog(log_file,['dist_file already exist, nothing is done'])
         return dist_outfile_path,dist_outfile_name
    temp_graph_filename=GEN_PATH+argsDict['infile']+'temp_graph'+argsDict['dist']
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
    

def launch_mono(argsDict,log_file,java_args=[]):                
    args = computeArgs(CFLAUNCHER_JAR, argsDict,exe_args=java_args)
    addToLog(log_file,args)
    log = jarWrapper(*args)
    addToLog(log_file,log)
    return log
def get_stats(argsDict):
    '''
    on prend la premiere ligne
    '''
    filename = argsDict['outfile']
    
    with open(filename+'.csv') as csvfile:
        reader = csv.DictReader(csvfile,delimiter=';')
        for row in reader:
            if argsDict['method'].startswith(row['method']):
                return row


def is_timeout(log):
    for line in log :
        print line
        if line.startswith("---System Timeout---"):
            return True
    return False
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

def get_number_first_agent_having_top_clause(argsDict,log_file):
    '''
    filename of the distribution file (dcf)
    optimized version
    '''
    ''' 
    #deja fait dans valid dis
    if not (os.path.isfile(filename+dist+'.dcf')):
        addToLog(log_file,['file '+filename+ '.dcf non existing#############'])
    '''
    print argsDict
    dist_filename = argsDict['infile_path']+argsDict['infile']+argsDict['dist']+'.dcf'
    f = open(dist_filename, 'r')
    numAgent = 0
    for line in f:           
        if line.startswith('agent'):
            numAgent = re.findall(r'\d+', line)[0]
        if line.startswith('cnf'):
            if 'top_clause' in line:
                return numAgent
    addToLog(log_file,['erreur : pas de top clause ici ou pas d Agent######'])
    return 0
    
def get_string_tokens(firstToken, numagent):
    firstToken = int(firstToken)
    numagent = int(numagent)
    
    res_tokens = ""
    tokens = range (firstToken,firstToken+numagent)
    for token in tokens :
        if token >= numagent :
            res_tokens+=str(token%numagent)+'-'
        else:
            res_tokens+=str(token)+'-'
    return res_tokens[:-1]