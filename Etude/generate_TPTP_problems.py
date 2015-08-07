# -*- coding: utf-8 -*-
from commons import *
import os
from generate_BIO_problems import *
import fnmatch
import csv
PROCESSES = 16
JAVA_ARGS = ['-d64', '-Xms512m', '-Xmx8g','-jar']
TIMEOUT_MONO = 2000000#correspond a peu pres a 20 minutes
TIMEOUT_MULTI = 600000 #10 min
LENGTH_LIST = [-1,2,4,5]
DEPTH_LIST =[-1]
METH_LIST = [ 'max-','min-','all']
METH_N_LIST = [1,3,5,10] 

NUMAGENT_LIST = [2,4,6,8]
METHODS = ['DICF-PB-Async', 'DICF-PB-Star', 'DICF-PB-Token']
'''
generate only the tptp wih top clauses
'''
'''
TCONTCONJ_FULLNEWC # considers axioms and hypotheses as axioms, negated_conjecture as top-clause, and use the whole vocabulary as pf
TCONJ_REFUT #: considers axioms and hypotheses as axioms, negated_conjecture as top-clause, and tries to find contradiction in the Newcarc
THYP_FULLNEWC# : considers axioms and negated_conjecture as axioms, hypothesis as top-clauses, and use the whole vocabulary as pf
THYP_REFUT #: considers axioms and negated_conjecture as axioms, hypothesis as top-clauses, and tries to find contradiction in the Newcarc
TCH_FULLNEWC #: considers only axioms as axioms, hypothesis and negated_conjecture as top-clause, and use the whole vocabulary as pf
TCH_REFUT #: considers only axioms as axioms, hypothesis and negated_conjecture as top-clause and tries to find contradiction in the Newcarc
THYP_NCONJ_NEWC #: considers axioms as such and hypotheses as top-clauses, and use the vocabulary of conjecture to make the production field
ABDUCTION #consider axioms as such and negated_conjecture as top-clauses and use the vocabulary of negated hypothesis to make the production field
'''
LP2SOL_PARAMS = ['TCONTCONJ_FULLNEWC','TCONJ_REFUT','THYP_FULLNEWC','THYP_REFUT','TCH_FULLNEWC','TCH_REFUT','THYP_NCONJ_NEWC','ABDUCTION']

MAX_NB_LINES = 900#reject p problems wit more thant 500 lines... ? or better for distribution
MIN_NB_LINES = 100
java_args = ['-jar']

def translate_TPTP_problems(problemsDir):
    '''
    do no cut for now... maybe later
    '''
    for root, dirnames, filenames in os.walk(problemsDir):
        for base_filename in remove_ext(fnmatch.filter(filenames, '*.p'),'.p'):
            #on ne prend pas les problèmes avec plus de 800 lignes...
            solDir = root + '/' + base_filename
            make_dirs_safe(solDir)
            infile=root+'/'+base_filename+'.p'
            with open(infile, 'r') as pfile:
                size = len(pfile.readlines())
                print size
            if size >= MAX_NB_LINES or size < MIN_NB_LINES:
                continue
            if os.listdir(solDir) != [] :
                continue
            
            
            for meth in LP2SOL_PARAMS :
                outfile_name = base_filename+'_'+meth
                outfile_path=root+'/'+base_filename+'/'
                args = JAVA_ARGS + [P2SOL_JAR, infile, outfile_path+outfile_name ]
                print args
                res = jarWrapper(*args)
            
                
problemsDir= 'Problems/TPTP_gen'                
#translate_TPTP_problems(problemsDir)

def get_previous_problems(problemsDir,filename,log_file,ext = '.csv'):
    '''
    recupere les acnoen problems
    '''
   # (res,MONO_PROBLEMS_FIELDNAMES_ORDER,MONO_PROBLEMS_FILENAME,'.csv',root,lock=None)
    res = []
    for root, dirnames, filenames in os.walk(problemsDir):
        root+='/'
        for base_filename in fnmatch.filter(filenames, filename+'*'):#tester ca
            print root+base_filename
            #addToLog(log_file,[root+base_filename])
            with open(root+filename+ext,'r') as csvfile:
                reader = csv.DictReader(csvfile)
                for rowDict in reader:
                    res.append(rowDict)
    return res

def launch_MONO_problem(argsDict):
    suffix_var = "_"+argsDict['methodVar']+"_ld"+argsDict['length']+"-"+argsDict['depth']        
    output_filename = argsDict['infile'] + suffix_var
    log_filename = argsDict['infile_path'] + output_filename   
    isDistributed = True        
                               
    argsDict['method'] = 'SOLAR-Inc-Carc'        
    argsDict['verbose'] = True 
    argsDict['timeout'] = TIMEOUT_MONO      
    argsDict['var'] = suffix_var     
    argsDict['log_filename'] = log_filename
    argsDict['outfile'] = argsDict['infile_path'] + output_filename
    argsDict['csq'] = argsDict['infile_path']+ output_filename+'_MONO'
    argsDict['csq_mono'] =argsDict['infile_path']+ output_filename+'_MONO'
    try:
        argsDict['dist']       
    except KeyError :
        isDistributed = False
        argsDict['dist'] = None
        argsDict['numagent'] = None
        
    with open(log_filename + '.log', 'w') as log_file :
        try:        
            addToLog(log_file,['generate VARIANT################################'])
            generate_variant(argsDict, log_file,java_args=JAVA_ARGS)
            addToLog(log_file,['launch MONO#####################################'])
            log_mono=launch_mono(argsDict,log_file,JAVA_ARGS)  
        #    addToLog(log_file, log_mono)
            if not is_timeout(log_mono) :
            #if True:
                addToLog(log_file,['NO TIME OUT#################################'])
                addToLog(log_file,['output to finishing problems################'])
                statsDict = get_stats(argsDict)    
                field_names_order = FINISHING_MONO_PROBLEMS_FIELDNAMES_ORDER + statsDict.keys()
                statsDict.update(argsDict)
                outputs_config_file_one_row(statsDict,field_names_order,FINISHING_MONO_PROBLEMS_FILENAME,'.csv',argsDict['infile_path'], lock=lock_FINISHING_MONO)    
                
                addToLog(log_file,['GENERATE DISTRIBUTION#######################'])
                if isDistributed :
                    #copy_distribution
                    origin_dist_filename = argsDict['distfile_name']
                    origin_dist_filepath = argsDict['distfile_path']
                    dist_file = par.FileDCF(origin_dist_filepath, origin_dist_filename)
                    dist_file.load()
                    dist_outfilename = argsDict['infile'] + argsDict['dist'] 
                    dist_outfilepath = argsDict['infile_path']
                    dist_file.save(dist_outfilepath,dist_outfilename)
                    #generate_distribution(argsDict, log_file,GEN_PATH,java_args=JAVA_ARGS) 
                    outputs_config_file_one_row(argsDict,MULTI_PROBLEMS_FIELDNAMES_ORDER,MULTI_PROBLEMS_FILENAME,'.csv',argsDict['infile_path'], lock=lock_MULTI)    
                else:
                    addToLog(log_file,['ICICICIICICICICCICICCICIICN#######################'])
                    print 'efefefefef'
                    distributions = generate_valid_distributions(argsDict,log_file,java_args=JAVA_ARGS)
                    outputs_distributions(MULTI_PROBLEMS_FIELDNAMES_ORDER,MULTI_PROBLEMS_FILENAME,'.csv',distributions,log_file,argsDict['infile_path'], lock=lock_MULTI)
    
                filename_stats_temp = argsDict['outfile']+'.csv'
                if os.path.isfile(filename_stats_temp):
                    addToLog(log_file,['removing temp .csv#########################'])
                    os.remove(filename_stats_temp) 
            else:
                outputs_config_file_one_row(argsDict,FAILED_MONO_PROBLEMS_FIELDNAMES_ORDER,FAILED_MONO_PROBLEMS_FILENAME,'.csv',argsDict['infile_path'], lock=lock_FAILED_MONO)    
        except Exception as e:
            addToLog(log_file, [str(e)])
            outputs_config_file_one_row(argsDict,FAILED_MONO_PROBLEMS_FIELDNAMES_ORDER,FAILED_MONO_PROBLEMS_FILENAME,'.csv',argsDict['infile_path'], lock=lock_FAILED_MONO)    
               
def init(l_mono,l_multi,l_allstats, l_multi_algo, l_failed_mono, l_failed_multi):    
    global lock_MULTI_algos_p    
    global lock_FINISHING_MONO
    global lock_MULTI 
    global lock_MULTI_ALLSTATS
    global lock_FAILED_MONO
    global lock_FAILED_MULTI
    
    lock_MULTI_algos_p = l_multi_algo
    lock_MULTI = l_multi
    lock_MULTI_ALLSTATS = l_allstats
    lock_FINISHING_MONO = l_mono    
    lock_FAILED_MONO = l_failed_mono
    lock_FAILED_MULTI = l_failed_multi
if __name__ == '__main__':
    lock_mono=mp.Lock()
    lock_multi = mp.Lock()
    lock_allstats = mp.Lock()
    lock_multi_algo = mp.Lock()
    lock_failed_mono = mp.Lock()
    lock_failed_multi = mp.Lock()
    
    
    problemsDir= 'Problems/TPTP_gen'    
    pool = mp.Pool(PROCESSES,initializer=init, initargs=(lock_mono,lock_multi,lock_allstats,lock_multi_algo,lock_failed_mono,lock_failed_multi ))
    with open('Problems/TPTP_gen/'+ GLOBAL_LOG_FILENAME + '.log', 'a') as log_file_GLOBAL  :        
        print 'pool = %s' % pool        
        addToLog(log_file_GLOBAL,['Creating pool with %d processes\n' % PROCESSES])
        
        addToLog(log_file_GLOBAL,['generating Parameters for MONO problems'])
       # list_parameters_MONO = generate_problems_MONO(problemsDir,log_file_GLOBAL)
        #on peut ecrire ca dans un fichier...
      #  print list_parameters_MONO
        addToLog(log_file_GLOBAL,['launching MONO problems'])                                                                                                                                                               
        
        addToLog(log_file_GLOBAL,['generating Dist parameters and dependencies for MULTI problems'])
        list_parameters_MONO = get_previous_problems(problemsDir,MONO_PROBLEMS_FILENAME,log_file_GLOBAL)
        print list_parameters_MONO
        #on prend les problems avec pas du all d'Abord
        methAllProblems = []
        otherProblems = []
        for argsDict in list_parameters_MONO :
            if 'all' in argsDict['methodVar']:
                methAllProblems.append(argsDict)
            else:
                otherProblems.append(argsDict)
        #et ensuite on prend les autres...
        
        addToLog(log_file_GLOBAL,['LAUNCHING MONO PROBLEMS, befor ALL'])
        pool.map(launch_MONO_problem, otherProblems)
        addToLog(log_file_GLOBAL,['LAUNCHING MONO PROBLEMS WITH VAR= ALL'])
        
        pool.map(launch_MONO_problem, methAllProblems)
        
        addToLog(log_file_GLOBAL,['generating Method Parameters for MULTI problems'])
        generate_MULTI_config_to_file(problemsDir,log_file_GLOBAL)       
        addToLog(log_file_GLOBAL,['now everything is generated I hope...'])
        
        
        addToLog(log_file_GLOBAL,['ZIIS ZE END'])