# -*- coding: utf-8 -*-
from BIO_script import *
def launch_bio_gene_and_mono():
    nb_not_timeout = 0
    lArgs = generate_parameters_makesolvar()       
    fieldnames_order_configFile_finishingProblems = ['infile','outfile','method','timeout','var','csq','dist']
    
    with open(filename_finishing_problems, 'a') as csvfile:
        writer_configFile_finishingProblems = csv.DictWriter(csvfile, fieldnames=fieldnames_order_configFile_finishingProblems,extrasaction='ignore')
        writer_configFile_finishingProblems.writeheader()
        for filename in get_problem_files(PROBLEM_PATH,'.sol'):
            #make variant of problem
            filename_noext = filename[:-4]        
            for method,length,depth in lArgs :
                #try:
                suffix_var = "_"+method+"_ld"+str(length)+"-"+str(depth);        
                output_filename = filename_noext + suffix_var
                log_filename = PROBLEM_PATH + output_filename + '.log'                
                
                argsDict_mono = dict()                                   
                #LAUNCH MONO ALGO
                argsDict_mono = dict()
                argsDict_mono['infile_noext'] = filename_noext
                argsDict_mono['method'] = 'SOLAR-Inc-Carc'        
                argsDict_mono['verbose'] = True 
                argsDict_mono['timeout'] = timeout_MONO      
                argsDict_mono['var'] = suffix_var
                argsDict_mono['dist'] = None                
                #in another directory ?
                argsDict_mono['csq'] = PROBLEM_PATH+output_filename+'_MONO'
                argsDict_mono['infile'] = PROBLEM_PATH+filename_noext#ici de toutes facons les CFLAUNCHER rajoutera l'Extension
                #on print les logs pour tous les mêmes problèmes dans le même fichier
                argsDict_mono['outfile'] = PROBLEM_PATH+output_filename#ici rajoutera un .csv aussi
                
                with open(log_filename, 'w') as log_file :
                    #overwrites the pre-existing file
                    log=['generate VARIANT']
                    addToLog(log_file,log)
                    generate_variant(method, length, depth, argsDict_mono, log_file)
                    log=['launche MONO']
                    addToLog(log_file,log)
                    launch_mono(argsDict_mono,log_file)    
                    
                    #CHEK IF TIMEOUT            
                    if not is_timeout(argsDict_mono,log_file) :
                        log=['WrITE CONFIG FILE FINISIHNG PROBLENS']
                        addToLog(log_file,log)
                        writer_configFile_finishingProblems.writerow(argsDict_mono)
                        nb_not_timeout+=1
                        log=['generate CONFIG AND DISTRIBUTION']
                        addToLog(log_file,log)
                        generate_config_and_distribution(argsDict_mono,log_file)   
                  
            '''
           except Exception :
                log+=[ "erreur quelque part, everythin abort"]
                log_filename = PROBLEM_PATH +output_filename+'.log'
                saveLog(log,log_filename)
                
                log=['erreur TOTALE']
                saveLog(log, PROBLEM_PATH+'erreur_totale.log')
            '''
    print 'something'
    print 'nb nion timeout: ',nb_not_timeout
def launch_bio_multi_agents():
    '''
    run the multiagents and computes consequences
    '''
    log=[]
    #get CSV and execute args here
    for method in methods:
        filename = PROBLEM_PATH+'config_'+ method+'.csv'
        with open(filename) as csvfile:
             reader = csv.DictReader(csvfile)
             for row in reader:
                 args = computeArgs(CFLAUNCHER_JAR, row)
                 print args
                 print(row['infile'], row['outfile'])
                 log+=jarWrapper(*args)
    #log_filename = PROBLEM_PATH +output_filename+'.log'
    saveLog(log, PROBLEM_PATH+'test.log')
def launch_bio_compare_script():
    '''
    export comCsq and define outfilename
    can be made at last
    '''
    #CMPCSQ_JAR
    pass
     #try except here et savelog           
#launch_bio_first_script()
launch_bio_gene_and_mono()
#launch_bio_multi_agents()