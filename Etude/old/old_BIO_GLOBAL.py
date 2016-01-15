# -*- coding: utf-8 -*-
'''
catch all fails and output to logs, and stop process of one  at the moment
'''
import multiprocessing as mp
PROCESSES = 4
JAVA_ARGS = ['-d64', '-Xms512m', '-Xmx2g','-jar']
'''
TIMEOUT_MONO = 1200000 #il faut mettre le double de ce qu'on veut ici, bug bizarre
TIMEOUT_MULTI = 600000
#VAR

LENGTH_LIST = [-1,2,3,4,5]
DEPTH_LIST =[-1]
METH_LIST = [ 'max-','min-', 'all']
METH_N_LIST = [2,4,5,6,8,10,15,20] 
#MULTI
NUMAGENT_LIST = [2,4,6,8]
METHODS = ['DICF-PB-Async', 'DICF-PB-Star', 'DICF-PB-Token']
'''
TIMEOUT_MONO = 25000 #il faut mettre le double de ce qu'on veut ici, bug bizarre
TIMEOUT_MULTI = 25000
#VAR
LENGTH_LIST = [-1]
DEPTH_LIST =[-1]
METH_LIST = [ 'max-']#,'all']
METH_N_LIST = [2] 
#MULTI
NUMAGENT_LIST = [2,4]
METHODS = ['DICF-PB-Async', 'DICF-PB-Star', 'DICF-PB-Token']
#METHODS = ['DICF-PB-Token']

PROBLEM_PATH = 'Problems/Bio/'
GEN_PATH = PROBLEM_PATH
#il faut mettre un lock sur ces deux fichiers quand on écrit

FINISHING_MONO_PROBLEMS_FILENAME = 'finishing_MONO_problems'
FINISHING_MONO_PROBLEMS_FIELDNAMES_ORDER = ['infile','outfile','method','timeout','var','csq']

#MULTI_PROBLEMS_FILENAME = 'generated_MULTI_problems'
MULTI_PROBLEMS_FILENAME = 'MULTI_problems'
MULTI_PROBLEMS_FIELDNAMES_ORDER = ['infile','outfile','var','numagent','dist','csq','csq_mono']

MULTI_PROBLEMS_ALGO_PARAMETERS_FILENAME = 'algo_parameters_MULTI_problems'
MULTI_PROBLEMS_ALGO_PARAMETERS_FIELDNAMES_ORDER = ['infile','outfile','var','verbose','timeout','method','numagent','dist','csq','csq_mono']

MULTI_PROBLEMS_ALLSTATS_FILENAME = 'running_stats_MULTI_problems'  


GLOBAL_LOG_FILENAME = 'global_log'
from BIO_GLOBAL_lib import *

print 'GEN PATH IS',GEN_PATH

    
def launch_problem_generation_and_mono(parameters):
    '''
    generates problem for this variant if it does not exist
    then test the MONO algo
    if there is not time out it saves the problem and generates distribution 
    for MULTI.
    if the generated problem does not have any top_clause, 
    it aborts with an error
    TODO : try except with logging of the error
    TODO : mettre un lock quand on ecrit dans le fichier
    '''
    filename,method,length,depth = parameters
    suffix_var = "_"+method+"_ld"+length+"-"+depth;        
    output_filename = filename + suffix_var
    log_filename = GEN_PATH + output_filename             
                               
    argsDict = dict()    
    argsDict['method'] = 'SOLAR-Inc-Carc'        
    argsDict['verbose'] = True 
    argsDict['dist'] = None             
    argsDict['timeout'] = TIMEOUT_MONO      
    argsDict['var'] = suffix_var     
    argsDict['log_filename'] = log_filename
    argsDict['csq'] = GEN_PATH + output_filename+'_MONO'
    argsDict['infile'] = PROBLEM_PATH + filename
    argsDict['outfile'] = GEN_PATH + output_filename
    print argsDict
    with open(log_filename + '.log', 'w') as log_file :
        addToLog(log_file,['generate VARIANT################################'])
        generate_variant(parameters, argsDict, log_file,java_args=JAVA_ARGS)
        addToLog(log_file,['launch MONO#####################################'])
        log_mono=launch_mono(argsDict,log_file,JAVA_ARGS)  
        
        if not is_timeout(log_mono) :
        #if True:
            addToLog(log_file,['NO TIME OUT#################################'])
            addToLog(log_file,['output to finishing problems################'])
            statsDict = get_stats(argsDict)    
            field_names_order = FINISHING_MONO_PROBLEMS_FIELDNAMES_ORDER + statsDict.keys()
            statsDict.update(argsDict)
            outputs_config_file_one_row(statsDict,field_names_order,FINISHING_MONO_PROBLEMS_FILENAME,'.csv',GEN_PATH, lock=lock_FINISHING_MONO)    
            addToLog(log_file,['GENERATE DISTRIBUTION#######################'])
            parameters_algos_multi = generate_parameters_algos_multi(NUMAGENT_LIST)
            distributions = generate_valid_distributions(argsDict, parameters_algos_multi,log_file,GEN_PATH,java_args=JAVA_ARGS)
            outputs_distributions(argsDict,MULTI_PROBLEMS_FIELDNAMES_ORDER,MULTI_PROBLEMS_FILENAME,'.csv',distributions,log_file,GEN_PATH, lock=lock_MULTI)

        filename_stats_temp = argsDict['outfile']+'.csv'
        if os.path.isfile(filename_stats_temp):
            addToLog(log_file,['removing temp .csv#########################'])
            os.remove(filename_stats_temp)
        
def launch_MULTI(paramDict):
    log_filename = paramDict['outfile']+'_'+paramDict['dist']
    with open(log_filename + '.log', 'w') as log_file :
        addToLog(log_file,['LAUNCH MULTI PROBLEM############################'])
        args = computeArgs(CFLAUNCHER_JAR, paramDict,JAVA_ARGS)
        addToLog(log_file,args)
        #run        
        log=jarWrapper(*args)
        addToLog(log_file,log)
        
        addToLog(log_file,['LAUNCH CMP CSQ##################################'])        
        csq_mono_filename = paramDict['csq_mono']
        csq_multi_filename = paramDict['csq']
        csq_stats_filename = csq_multi_filename + '_CMP'        
        args = JAVA_ARGS+[CMPCSQ_JAR]+[csq_multi_filename,csq_mono_filename,csq_stats_filename]
        log = jarWrapper(*args)
        addToLog(log_file,log)
    
        addToLog(log_file,['MERGE STATS####################################']) 
        #regarde dans le outfile et prend en fonction de la méthode...
        #merge le outfile avec les stats.csq et avec le timeout aussi
        #pour chaque methode faire custom
        csqStatsDict = get_csq_cmp_stats(csq_stats_filename)   
        os.remove(csq_stats_filename+'.csv')
        
        statsDict = get_stats(paramDict)    
        filename_stats_temp = paramDict['outfile']+'.csv'
        os.remove(filename_stats_temp)
        #enlever le .csv local inutile
        #je ne suis psa sur que la methode printée prenne en compte les parametres comme l'ordre des TOKEN ou autre
        #c'est pour ca que je le rajoute ici
        #ajouter timeout
        tempDict = dict()
      #  tempDict['method_precision'] = paramDict['method']      
        tempDict['infile'] = paramDict['infile']
        tempDict['var'] = paramDict['var']
        tempDict['timed_out'] =from_boolean_to_int( is_timeout_from_file(paramDict, log_file) )
        statsDict.update(csqStatsDict)
        statsDict.update(tempDict)
        
        addToLog(log_file,statsDict)
        outputs_config_file_one_row(statsDict, statsDict.keys(),MULTI_PROBLEMS_ALLSTATS_FILENAME,'.csv',GEN_PATH,lock=lock_MULTI_ALLSTATS)
        
        #enlever le .csv local inutile



def generate_problems_and_vars(pool, log_file_GLOBAL):
    problem_files = get_problem_files(PROBLEM_PATH,'.sol')
    problem_files = remove_ext(problem_files,'.sol')
    '''
    #generate new .sol files from KMETIS ROUNDTRIP
    #generate special parameters to that (+ dist arg)
    #append to lis_parameters MONO
    in launch problem : only launch
    in generate MULTI problems : get results from mono if finishing and
    if arg dist exist, only generate problems with this special distribution 
    easy way is to regenerate distributions KMEITs (or copy file and rename it) and no care because determinist (and it is not costy)
    '''
    
    
    
    addToLog(log_file_GLOBAL,['generating parameters MONO'])
    list_parameters_MONO = generate_parameters_makesolvar(problem_files,LENGTH_LIST,DEPTH_LIST,METH_LIST,METH_N_LIST)
    addToLog(log_file_GLOBAL,[])
    for parameters in list_parameters_MONO:
        addToLog(log_file_GLOBAL,parameters)
    
    #ici il faut faire du apply pour gérer le parallelisne interne et controler tTOUS lesparametres (parce qu'on calcule le TEMPS !)!!!
    addToLog(log_file_GLOBAL,['launching MONO'])
    pool.map(launch_problem_generation_and_mono,list_parameters_MONO)
 
def generate_MULTI_from_file_and_launch(pool,log_file_GLOBAL):
    parameters = TIMEOUT_MULTI,  METHODS, NUMAGENT_LIST
    
    addToLog(log_file_GLOBAL,['generating parameters MULTI'])
    list_parameters_MULTI = generate_algos_parameters(MULTI_PROBLEMS_FILENAME, '.csv', parameters, GEN_PATH,log_file_GLOBAL)
    for paramDict in list_parameters_MULTI :
        addToLog(log_file_GLOBAL,paramDict)
    #RUN ALL MONO PROBLEMS AND CREATE DISTRIBUTIONS        
    addToLog(log_file_GLOBAL,['launching MULTI'])
    pool.map(launch_MULTI,list_parameters_MULTI)  
def generate_MULTI_config_to_file(log_file_GLOBAL):
    '''
    a partir du fichier contenant les variantes de problemes solvables,
    générer les parametres pour les differents algos multi-agents dans un fichier
    '''
    parameters = TIMEOUT_MULTI,  METHODS, NUMAGENT_LIST
    addToLog(log_file_GLOBAL,['generating parameters MULTI'])
    list_parameters_MULTI = generate_algos_parameters(MULTI_PROBLEMS_FILENAME,
                                                      '.csv', parameters, 
                                                      GEN_PATH,log_file_GLOBAL)
    
    outputs_config_file_all_rows(list_parameters_MULTI, 
                                MULTI_PROBLEMS_ALGO_PARAMETERS_FIELDNAMES_ORDER, 
                                MULTI_PROBLEMS_ALGO_PARAMETERS_FILENAME, 
                                '.csv',GEN_PATH,lock=None)
#je n'utilise pas le lock ic car c'est sequenetiel et je nepeux pas car cEst dans le glonbal namespace que du POOL
                                
    for paramDict in list_parameters_MULTI :
        addToLog(log_file_GLOBAL,paramDict)
        #RUN ALL MONO PROBLEMS AND CREATE DISTRIBUTIONS        
    addToLog(log_file_GLOBAL,['launching MULTI'])



def init(l_mono,l_multi,l_allstats, l_multi_algo):    
    global lock_MULTI_algos_p    
    global lock_FINISHING_MONO
    global lock_MULTI 
    global lock_MULTI_ALLSTATS
    lock_MULTI_algos_p = l_multi_algo
    lock_MULTI = l_multi
    lock_MULTI_ALLSTATS = l_allstats
    lock_FINISHING_MONO = l_mono
    
if __name__ == '__main__':
    lock_mono=mp.Lock()
    lock_multi = mp.Lock()
    lock_allstats = mp.Lock()
    lock_multi_algo = mp.Lock()
    
    pool = mp.Pool(PROCESSES,initializer=init, initargs=(lock_mono,lock_multi,lock_allstats,lock_multi_algo))
    with open(GEN_PATH+GLOBAL_LOG_FILENAME + '.log', 'a') as log_file_GLOBAL  :        
        print 'pool = %s' % pool        
        addToLog(log_file_GLOBAL,['Creating pool with %d processes\n' % PROCESSES])
        #deja fait ici        
        #generate_problems_and_vars(pool, log_file_GLOBAL)
        
         generate_MULTI_from_file_and_launch(pool,log_file_GLOBAL)
 #       generate_MULTI_config_to_file(log_file_GLOBAL)       
        addToLog(log_file_GLOBAL,['ZIIS ZE END'])