# -*- coding: utf-8 -*-
#- filename,cnf filename2.sol

'''
mettre moins de parametres que pour bio pour que ca aille plus vite...
'''
import multiprocessing as mp
#from BIO_GLOBAL_lib import *
import copy
from BIO_lib import *
from commons import *
from cut_problems import *

PROCESSES = 16
JAVA_ARGS = ['-d64', '-Xms512m', '-Xmx8g','-jar']
TIMEOUT_MONO = 2000000#correspond a peu pres a 20 minutes
TIMEOUT_MULTI = 600000 #10 min
LENGTH_LIST = [-1,2,3,4,5]
DEPTH_LIST =[-1]
METH_LIST = [ 'max-','min-','all']
METH_N_LIST = [1,3,5,10] 

NUMAGENT_LIST = [2,4,6,8]
METHODS = ['DICF-PB-Async', 'DICF-PB-Star', 'DICF-PB-Token']

'''
PROCESSES = 4
JAVA_ARGS = ['-d64', '-Xms512m', '-Xmx2g','-jar']
TIMEOUT_MONO = 5000 #il faut mettre le double de ce qu'on veut ici, bug bizarre 
TIMEOUT_MULTI = 5000
#VAR
LENGTH_LIST = [-1]
DEPTH_LIST =[-1]
METH_LIST = [ 'max-']#,'all']
METH_N_LIST = [2] 
#MULTI
NUMAGENT_LIST = [2,4]
METHODS = ['DICF-PB-Async', 'DICF-PB-Star', 'DICF-PB-Token']


'''

FINISHING_MONO_PROBLEMS_FILENAME = 'finishing_MONO_problems'
FINISHING_MONO_PROBLEMS_FIELDNAMES_ORDER = ['infile_path','infile','outfile','method','timeout','var','csq','dist','numagent']

MONO_PROBLEMS_FILENAME = 'generated_MONO_problems'
MONO_PROBLEMS_FIELDNAMES_ORDER = ['infile_path','infile','methodVar','length','depth','dist','numagent'] 

#failed inclus unfinishing
FAILED_MONO_PROBLEMS_FILENAME = 'failed_MONO_problems'
FAILED_MONO_PROBLEMS_FIELDNAMES_ORDER = FINISHING_MONO_PROBLEMS_FIELDNAMES_ORDER
#MULTI_PROBLEMS_FILENAME = 'generated_MULTI_problems'
MULTI_PROBLEMS_FILENAME = 'MULTI_problems'
MULTI_PROBLEMS_FIELDNAMES_ORDER = ['infile_path','infile','outfile','var','numagent','dist','csq_mono']#numagent et dist sont la uniquement si c'est un probleme distribué...

MULTI_PROBLEMS_ALGO_PARAMETERS_FILENAME = 'algo_parameters_MULTI_problems'
MULTI_PROBLEMS_ALGO_PARAMETERS_FIELDNAMES_ORDER = ['infile_path','infile','outfile','var','verbose','timeout','method','numagent','dist','csq','csq_mono']

FAILED_MULTI_PROBLEMS_FILENAME = 'failed_MULTI_problems'
FAILED_MULTI_PROBLEMS_FIELDNAMES_ORDER = MULTI_PROBLEMS_ALGO_PARAMETERS_FIELDNAMES_ORDER

MULTI_PROBLEMS_ALLSTATS_FILENAME = 'running_stats_MULTI_problems'  

GLOBAL_LOG_FILENAME = 'global_log'

#pas testé dans les cas limites : 
#il faut catché l'exception et supprimer la variante distributionnelle dans ce cas...
#ne pas générer de statistiques, peut etre juste un log des problemes qui n'ont pas été lancés (qd on catch une exception)
#ROBUSTE ainsi
METHOD_AGENT_DISTRIBUTION = ['_kmet','_naiveEq']#'naive_indent'
METHOD_TP_DISTRIBUTION_LIST = ['short']#,'random']
PERC_TP_DIST_LIST = [1,5,10]
def generate_algos_parameters(filename, ext,parameters,gen_path,log_file) :
    '''
    ici il faudra faire varier les parametres d'ordre pour TOKEN et STAR
    faire toutes les combinaisons
    '''
    timeout,methods,numagent_list = parameters
    dictParameters=[]
    filename = gen_path+ filename + ext
    if not os.path.isfile(filename) :
        addToLog(log_file,['PAS de PROBLEMS FILE GENERE, on le touch'])
        open(filename, 'a').close()#touch
    with open(filename) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            for method in methods :
                argsDict = dict() 
                argsDict['infile_path'] = rowDict['infile_path']                      
                argsDict['infile'] = rowDict['infile']
                argsDict['var'] = rowDict['var']
                argsDict['verbose'] = True 
                argsDict['timeout'] = timeout
                argsDict['dist'] = rowDict['dist'] 
                argsDict['numagent'] = rowDict['numagent']     
                if method == 'DICF-PB-Token':
                    if int(argsDict['numagent']) <= 2 :#noticed that TOKEN algos fail when numagent =2, whatever the fixed order is...
                        continue
                    #probably unnecessary to do this...
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
def generate_MULTI_config_to_file(problemsDir,log_file_GLOBAL):
    '''
    a partir du fichier contenant les variantes de problemes solvables,
    générer les parametres pour les differents algos multi-agents dans un fichier
    '''
    parameters = TIMEOUT_MULTI,  METHODS, NUMAGENT_LIST
    addToLog(log_file_GLOBAL,['generating parameters MULTI'])
    
    #faire une walk ici sur els filenames qui ont ca...
    
    for root, dirnames, filenames in os.walk(problemsDir):
        root += '/'
        if os.path.isfile(filename+ext) :
            list_parameters_MULTI = generate_algos_parameters(MULTI_PROBLEMS_FILENAME,
                                                              '.csv', parameters, 
                                                              root,log_file_GLOBAL)
            addToLog(log_file_GLOBAL,['generating parameters MULTI :'+len(list_parameters_MULTI)+' problems'])
        
            outputs_config_file_all_rows(list_parameters_MULTI, 
                                        MULTI_PROBLEMS_ALGO_PARAMETERS_FIELDNAMES_ORDER, 
                                        MULTI_PROBLEMS_ALGO_PARAMETERS_FILENAME, 
                                        '.csv',root,lock=None)
        else:
            addToLog(log_file,['pas de fichier '+filename])
          
                                        
def generate_valid_distributions(argsDict,log_file,java_args=[]):
    argsList = generate_parameters_algos_dist([argsDict['infile']],NUMAGENT_LIST)   
    valid_distributions = []    
    for argsDistribution in argsList :
        dist_outfile_path,dist_outfile_name = generate_distribution(argsDistribution, log_file,argsDict['infile_path'],java_args)
        dcf_file = par.FileDCF(dist_outfile_path, dist_outfile_name)   
        dcf_file.load()
        if dcf_file.is_valid() :
            d = copy.deepcopy(argsDict)
            d.update(argsDistribution) 
            valid_distributions.append(d)  
    return valid_distributions
    
def generate_variant( argsDict_mono, log_file,java_args=[]):
    args = java_args+[MAKESOLVARIANT_JAR]+['-method='+argsDict_mono['methodVar'],'-len='+argsDict_mono['length'],'-d='+argsDict_mono['depth'],argsDict_mono['infile_path']+argsDict_mono['infile'], argsDict_mono['outfile']]
    log = jarWrapper(*args)  
    addToLog(log_file,log)
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
               
def generate_parameters_sols_var_for_dist(spe_dicts,length_list,depth_list,meth_list,methN_list):
    res = []            
    for spe_d in spe_dicts :
        params = generate_parameters_onesol_var(spe_d['infile_path'], spe_d['infile'],LENGTH_LIST,DEPTH_LIST,
                                                                           METH_LIST,METH_N_LIST)
        for p in params :
            r = copy.deepcopy(p)
            r.update(spe_d) 
            res.append(r)
    return res
def generate_parameters_algos_dist(problems_path,problem_filenames,numagent_list):
    '''
    add here a for methodAgentPAritioning = kmetis or naive_eq
    '''
    print problem_filenames
    resL = []   
    for filename in problem_filenames:
        for method in METHOD_AGENT_DISTRIBUTION:
            for numagent in numagent_list:
                argsDict = dict()
                dist = method+str(numagent)
                argsDict['dist'] = dist
                argsDict['numagent'] = str(numagent)
                argsDict['infile'] = filename
                argsDict['infile_path'] = problems_path
                resL.append(argsDict)        
    return resL

def generate_valid_problem_args_with_TP_distributed(problems_path,problem_filenames,NUMAGENT_LIST,log_file):
    '''
    genPATH NE SERT A RIEN ICI
    '''
    #now for the dist thing    
    parameters_dist_dicts = generate_parameters_algos_dist(problems_path,problem_filenames,NUMAGENT_LIST)
    spe_dicts = []
    for argsDict in parameters_dist_dicts : 
        try:
            dcf_filepath,dcf_filename = generate_distribution(argsDict, log_file,problems_path,java_args=JAVA_ARGS)
            #the generated .dcf has name sol_filename+dist            
            dcf_file = par.FileDCF(dcf_filepath,dcf_filename)
        except Exception as e :
             addToLog(log_file, ['erreur : distribution non générée : arguments invalides surement']+[str(e)])
        try:
            dcf_file.load()
        except Exception as e:
            addToLog(log_file, ['erreur : dcf non chargé']+[str(e)])
            #raise e
            continue
        for method in METHOD_TP_DISTRIBUTION_LIST:
            for percTotal in PERC_TP_DIST_LIST:
                try:
                    new_tp_sol_file = dcf_file.create_a_FileSol_wit_a_TPdistribution_for_each_agent(percTotal,method = method)
                    if not new_tp_sol_file.is_valid():
                        continue
                    new_tp_sol_file.save()
                    subDict=copy.deepcopy(argsDict)
                    subDict['infile'] = new_tp_sol_file.filename
                    subDict['infile_path'] = new_tp_sol_file.path
                    subDict['distfile_name'] = dcf_filename
                    subDict['distfile_path'] = dcf_filepath
                    #ici on doit le recuperere dnas le laucn multi
                    spe_dicts.append(subDict)
                except Exception as e:
                    addToLog(log_file, [str(e)])
                    break
    return spe_dicts
def generate_parameters_onesol_var(problem_path,problem_filename,length_list,depth_list,meth_list,methN_list) :
    '''
    generates dictionaries containing arguments for generating variants of sol problems
    '''
    resL = []
    for length in length_list:
        for d in depth_list  :      
            for meth in meth_list:
                argsDict= dict()
                argsDict['infile'] = problem_filename
                argsDict['infile_path'] = problem_path
                argsDict['length'] = str(length)
                argsDict['depth'] = str(d)
                if meth != 'all' :
                    for meth_N in methN_list:
                        subDict = copy.deepcopy(argsDict)
                        subDict['methodVar'] = meth+str(meth_N)
                        resL.append(subDict)
                else :
                    argsDict['methodVar'] = meth
                    resL.append(argsDict)
    return resL
def generate_parameters_sols_var(problems_path, problem_filenames,length_list,depth_list,meth_list,methN_list):
    res =[]
    for filename in problem_filenames :
        dictL = generate_parameters_onesol_var(problems_path, filename,length_list,depth_list,meth_list,methN_list)
        res += dictL
    return res
def generate_valid_problem_filenames_with_TP(problem_path,problem_filenames):
    valid_problem_filenames = []
    for sol_filename in problem_filenames :
        sol_file = par.FileSol(problem_path,sol_filename)
        sol_file.load()
        if sol_file.is_valid():
            valid_problem_filenames.append(sol_filename)        
        nbTP = 0
        for perc in PERC_TP_DIST_LIST :            
            new_sol_file, new_nbTP = sol_file.create_a_FileSol_wit_a_TP_distribution_naiveShort(perc)
            if  nbTP == new_nbTP  or not new_sol_file.is_valid():#(même nombre de TP que le dernier alors on ajoute pas):
                continue
            nbTP = new_nbTP
            new_sol_file.save()#resemble a  probfilename + '_TPnaiveshortdist_per'+str(perc)+'_'+'seuil'+str(seuilMin)
            valid_problem_filenames.append(new_sol_file.filename)
    return valid_problem_filenames

def generate_problems_MONO(problemsDir,log_file):
    res = []
    print problemsDir
    for root, dirnames, filenames in os.walk(problemsDir):
        root += '/'
        addToLog(log_file, root)
        print root
        print 'rrrrrr'
        problem_filenames = get_problem_files(root,'.sol')
        if len(problem_filenames) == 0 :
            continue
        problem_filenames = remove_ext(problem_filenames,'.sol')
       
        valid_problem_filenames = generate_valid_problem_filenames_with_TP(root,problem_filenames)
        
        list_parameters_MONO = generate_parameters_sols_var(root,valid_problem_filenames,
                                                            LENGTH_LIST,DEPTH_LIST,
                                                            METH_LIST,METH_N_LIST) 
                                                            
        valid_distributed_problem_args = generate_valid_problem_args_with_TP_distributed(root,
                                                            problem_filenames, NUMAGENT_LIST,log_file)
        list_parameters_distributed_MONO = generate_parameters_sols_var_for_dist(
                                                valid_distributed_problem_args,
                                                 LENGTH_LIST,DEPTH_LIST,
                                                 METH_LIST,METH_N_LIST) 
        res += list_parameters_MONO + list_parameters_distributed_MONO
        addToLog(log_file,['generating problems MONO :'+str(len(res))+' problems'])
        outputs_config_file_all_rows(res,MONO_PROBLEMS_FIELDNAMES_ORDER,MONO_PROBLEMS_FILENAME,'.csv',root,lock=None)
        #output generated problems... for later use
    return res

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
    
    problemsDir = 'Problems/sourceCnf'
    
    pool = mp.Pool(PROCESSES,initializer=init, initargs=(lock_mono,lock_multi,lock_allstats,lock_multi_algo,lock_failed_mono,lock_failed_multi ))
    with open('Problems/sourceCnf/'+GLOBAL_LOG_FILENAME + '.log', 'a') as log_file_GLOBAL  :        
        print 'pool = %s' % pool        
        addToLog(log_file_GLOBAL,['Creating pool with %d processes\n' % PROCESSES])
        
        addToLog(log_file_GLOBAL,['generating Parameters for MONO problems'])
        list_parameters_MONO = generate_problems_MONO(problemsDir,log_file_GLOBAL)
        #on peut ecrire ca dans un fichier...
        print list_parameters_MONO
        addToLog(log_file_GLOBAL,['launching MONO problems'])                                                                                                                                                               
        
        addToLog(log_file_GLOBAL,['generating Dist parameters and dependencies for MULTI problems'])
        
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