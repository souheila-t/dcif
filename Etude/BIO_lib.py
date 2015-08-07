# -*- coding: utf-8 -*-
import os
import csv
from commons import *
import partitioning as par


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

def generate_distribution(argsDict, log_file,gen_path,java_args=[]) :
    if '_kmet' in argsDict['dist'] :
        p,n=generate_kmet_distribution(argsDict, log_file,gen_path,java_args)
    elif '_naiveEq' in argsDict['dist']:
        p,n=generate_naiveEq_distribution(argsDict, log_file,gen_path)
    if n != argsDict['infile']+argsDict['dist']:
        raise Exception('PROBLEME GENERATION DISTRIBUTION :'+ n+'not eq to : '+argsDict['infile']+argsDict['dist'])
    return p,n
def generate_naiveEq_distribution(argsDict, log_file,gen_path):
    #load the sol file
    nbagent = int(argsDict['numagent'])
    sol_filepath = argsDict['infile_path']
    sol_filename = argsDict['infile']   
    sol_file = par.FileSol(sol_filepath,sol_filename)
    sol_file.load()
    
    dcf_file = sol_file.create_dcf_Agent_distribution(nbagent, method = 'naive_eq')
    dcf_file.save()
    
    return dcf_file.path,dcf_file.filename


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

def get_csq_cmp_stats(filename):
    '''
    one ne prend que la premiere ligne
    '''
    with open(filename+'.csv') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            return row

def from_boolean_to_int(boolean):
    '''
    True = 1
    False = 0
    '''
    if boolean :
        return 1
    else :
        return 0
def is_timeout_from_file(argsDict,log_file):                        
    filename_csq = argsDict['csq']+'.csq'
    if not os.path.isfile(filename_csq):
        addToLog(log_file,[ "no such file"])
        return True
    else:
        addToLog(log_file,["file does exist at this time\n"])
        addToLog(log_file,[filename_csq])
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
                addToLog(log_file,["timeout occured"])
                #do not write in config file
                return True
            else :
                addToLog(log_file,['timeout did not occur'])                
                return False                 
        else:
            addToLog(log_file,["timeout not written in .csq file"])
            return True #problem we do nothing
def outputs_distributions(fieldnames, filename, ext,distributions,log_file,gen_path,lock=None):
    '''
    normalement ici on a déjà vérifié que les distributions sont valides...
    '''
    output_file =gen_path + filename + ext
    file_exist = False
    if os.path.isfile(output_file) : 
        file_exist = True
    if lock!=None:
        lock.acquire()
        with open(output_file, 'a') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames,extrasaction='ignore')
            if not file_exist :
                writer.writeheader()
            for argsDict in distributions :          
                writer.writerow(argsDict)
    if lock!=None:
        lock.release()
        
