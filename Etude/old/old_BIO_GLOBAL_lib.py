# -*- coding: utf-8 -*-
from commons import *
import csv
'''
def generate_parameters_makesolvar(problem_files,length_list,depth_list,meth_list,methN_list) :
    '''
'''
    generates tuples of arguments exploring parameters of make sol var 
    for all problems given
'''    
'''
    lArgs =  []
    for filename in problem_files:
        for length in length_list:
            for d in depth_list  :      
                for meth in meth_list:
                    if meth != 'all' :
                        for meth_N in methN_list:
                            args = (filename,meth+str(meth_N), str(length),str(d))
                            lArgs.append(args)
                    else :
                        args = (filename,meth,str(length),str(d))
                        lArgs.append(args)                    
    return lArgs     

def launch_mono(argsDict,log_file,java_args=[]):                
    args = computeArgs(CFLAUNCHER_JAR, argsDict,exe_args=java_args)
    addToLog(log_file,args)
    log = jarWrapper(*args)
    addToLog(log_file,log)
    return log
    
def generate_variant(parameters, argsDict_mono, log_file,java_args=[]):
    filename,method,length,depth = parameters
    args = java_args+[MAKESOLVARIANT_JAR]+['-method='+method,'-len='+length,'-d='+depth,argsDict_mono['infile'], argsDict_mono['outfile']]
    log = jarWrapper(*args)  
    addToLog(log_file,log)
'''   

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

def remove_ext(problem_files, ext):
    res=[]
    for filename in problem_files :
        if filename.endswith(ext):
            filename_noext = filename[:-len(ext)]
            res.append(filename_noext)
    return res

def generate_parameters_algos_multi(numagent_list):
    lArgs=[]    
    for numagent in numagent_list:
        dist = '_kmet'+str(numagent)
        args = (str(numagent), dist)
        lArgs.append(args)        
    return lArgs
'''
def generate_distribution(argsDict, numagent, dist, log_file,java_args=[]) :
    #generate DCF
    #if already exist do nothing, keep it all the same
    if os.path.isfile(argsDict['infile']+dist+'.dcf') : 
         addToLog(log_file,['dist_file already exist, nothing is done'])
    #here only 1 kind of partitioning   
    temp_graph_filename=argsDict['outfile']+'temp_graph'+dist
    ########buildGraph    
    try:
        os.remove(temp_graph_filename+'.gra')        
        addToLog(log_file,['removing old temporary file .gra'])
    except OSError:
        addToLog(log_file,['temporary file did not exist'])
        pass                
    addToLog(log_file,['buildGraph #########################################'])
    args = java_args+[BUILDGRAPH_JAR]+[argsDict['infile'], temp_graph_filename ]
    log = jarWrapper(*args)
    addToLog(log_file,log)
    ########kMetis
    try:
        os.remove(temp_graph_filename+'.gra.part.'+numagent)
        addToLog(log_file, ['removing old temporary file .gra.part.N'])
    except OSError:
        addToLog(log_file, ['temporary file did not exist'])
        pass
    addToLog(log_file,['kMETIS #############################################'])
    args = [KMETIS_EX, temp_graph_filename+'.gra', numagent]
    log = exWrapper(*args)
    addToLog(log_file,log)
    ########graph2DCF
    addToLog(log_file,['graph2DCF ##########################################']) 
    args = java_args+[GRAPH2DCF_JAR]+[argsDict['infile'],temp_graph_filename+'.gra.part.'+numagent , argsDict['infile']+dist+'.dcf']
    log = jarWrapper(*args)
    addToLog(log_file,log)
    
    #clean un peu...
    if os.path.isfile(temp_graph_filename+'.gra') : 
        os.remove(temp_graph_filename+'.gra')
    if os.path.isfile(temp_graph_filename+'.gra.part.'+numagent) : 
        os.remove(temp_graph_filename+'.gra.part.'+numagent)
    
def contains_top_clause():
    for line in f:
        if line.startswith('cnf'):
            if 'top_clause' in line:
                return True
            elif 'axiom' in line :#check if axiom or top_clause
                l.append((numAgent,'axiom', line))
    pass
'''  
def is_distribution_valid(argsDict, numagent, dist, log_file) :
    '''
    on regarde jsute le .dcf pour coir s'il contient au moins 1 top_clause et autres
    '''
    dist_filename = argsDict['infile']+dist+'.dcf'
    if not os.path.isfile(dist_filename) : 
   # if not(is_a_file(dist_filename)):
        addToLog(log_file,['distribution File '+dist_filename+ ' not generated... PROBLEM#'])
        #should raise exception here...
        return False
    else:
        dist_file = open(dist_filename,'r')
        l, nb_ag, nb_axiom, nb_top_clauses = read_dcf(dist_file)
        if nb_ag != int(numagent):
            addToLog(log_file,['number of agents generated is not good#on###'])
            return False
        if nb_top_clauses < 1: 
            addToLog(log_file,['NO TOP_CLAUSE###############################'])
            return False
        addToLog(log_file,['Dist is quite VALID#############################'])
        return True
        
def generate_valid_distributions(argsDict,parameters,log_file,gen_path,java_args=[]):
    valid_distributions = []    
    for numagent, dist in parameters:
        generate_distribution(argsDict, numagent, dist, log_file,gen_path,java_args )  
        if is_distribution_valid(argsDict, numagent, dist, log_file):
            valid_distributions.append((numagent, dist))            
    return valid_distributions

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
    dist_filename = argsDict['infile']+argsDict['dist']+'.dcf'
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
def outputs_distributions(argsDict_mono,fieldnames, filename, ext,distributions,log_file,gen_path,lock=None):
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
            for numagent,dist in distributions :
                argsDict = dict()                       
                argsDict['infile'] = argsDict_mono['infile']
                argsDict['var'] = argsDict_mono['var']
                argsDict['verbose'] = None
                argsDict['timeout'] = None
                argsDict['dist'] = dist  
                argsDict['csq'] = None
                argsDict['csq_mono'] = argsDict_mono['csq']
                argsDict['numagent'] = numagent
                argsDict['outfile'] = argsDict_mono['outfile']            
                writer.writerow(argsDict)
    if lock!=None:
        lock.release()
##############################################################################
def compute_timedout_problems():
    '''
    prend les problemes restants qui ne sont pas dans finis
    '''        
    pass

##############################################################################


def generate_algos_parameters(filename, ext,parameters,gen_path,log_file) :
    '''
    ici il faudra faire varier les parametres d'ordre pour TOKEN et STAR
    faire toutes les combinaisons
    '''
    timeout,methods,numagent_list = parameters
    dictParameters=[]
    filename = gen_path+ filename + ext
    print filename
    if not os.path.isfile(filename) :
        addToLog(log_file,['PAS de PROBLEMS FILE GENERE, on le touch'])
        open(filename, 'a').close()#touch
    with open(filename) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            print rowDict
            for method in methods :
                argsDict = dict()                       
                argsDict['infile'] = rowDict['infile']
                argsDict['var'] = rowDict['var']
                argsDict['verbose'] = True 
                argsDict['timeout'] = timeout
                argsDict['dist'] = rowDict['dist'] 
                argsDict['numagent'] = rowDict['numagent']     
                if method == 'DICF-PB-Token':
                    firstToken = get_number_first_agent_having_top_clause(argsDict,log_file)
                    method += '-FixedOrder-'+get_string_tokens(firstToken,argsDict['numagent'])                    
               # elif method == 'DICF-PB-Star':
                #    firstToken = get_number_first_agent_having_top_clause(argsDict,log_file)
                 #   method += '-FixedRoot-'+firstToken                
                argsDict['method'] = method           
                
                argsDict['csq'] = rowDict['outfile']+'_MULTI_'+method
                argsDict['csq_mono'] = rowDict['csq_mono']                
                argsDict['outfile'] = rowDict['outfile']+'_MULTI_'+method   
                dictParameters.append(argsDict)
    return dictParameters
    
def get_csq_cmp_stats(filename):
    '''
    one ne prend que la premiere ligne
    '''
    with open(filename+'.csv') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            return row
def get_stats(argsDict):
    '''
    on prend la premiere ligne
    '''
    print argsDict
    filename = argsDict['outfile']
    
    with open(filename+'.csv') as csvfile:
        reader = csv.DictReader(csvfile,delimiter=';')
        for row in reader:
            print row
            if argsDict['method'].startswith(row['method']):
                return row
    
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

def from_boolean_to_int(boolean):
    '''
    True = 1
    False = 0
    '''
    if boolean :
        return 1
    else :
        return 0
