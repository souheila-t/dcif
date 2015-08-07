# -*- coding: utf-8 -*-

'''
takes n = nbCore = nbAgent as argument
and a filenameProblem

do we do the walk ?? or is it one level, flat ?
only when !! RMA is 144 Go !!
'''
from generate_BIO_problems import *
import argparse


GEN_PATH = 'Problems/Bio/'


def writeToLog(log_filename, toAdd) :
    with open(log_filename + '.log', 'a') as log_file :
        for line in toAdd :
            log_file.write(str(line) + '\n')
            print line
        
def define_arguments():    
    parser = argparse.ArgumentParser(description='Process to launch Multi problems with a fixed number of agents')
    parser.add_argument('-n', '--numagent', type= int ,required=True,
                        help="le nombre d\'Agents, doit correspondre idealement au nbre de cores..")
    parser.add_argument('-d', '--div', type= int ,required=True,
                        help="le nombre de divisions ")
    parser.add_argument('-k', '--kpos', type= int ,required=True,
                        help="la kieme partie des D divisions est calculée, 0<=k<D")
    '''                    
    parser.add_argument('problemDir', 
                        help='repertoire ou on va chercher le file avec les multiproblems')
    '''
    return parser
def chunks(l, n):
    """Yield successive n-sized chunks from l."""
    for i in xrange(0, len(l), n):
        yield l[i:i+n]
        
def getArgsForN(filename, ext, numagent):
    res= []
    if not os.path.isfile(filename+ext) :
        print 'pas de fichier ', filename
    with open(filename+ext) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            if rowDict['numagent'] == str(numagent) :
                res.append(rowDict)
    return res
def getKinDivN_minLast(l,div,kpos):
    '''
    avec ca le dernier peut se retrouver avec moins = permet de controler le temps d'exec
    '''
    sizeChunks = len(l) // div
    if len(l) % div != 0:
        sizeChunks +=1
    for index, c in enumerate(chunks(l,sizeChunks)):
        if index == kpos:
            return c
    return []
def comp_dict(obj1, obj2):  
    '''
    MULTI_PROBLEMS_ALGO_PARAMETERS_FIELDNAMES_ORDER = 
    ['infile_path','infile','outfile','var','verbose',
    'timeout','method','numagent','dist',
    'csq','csq_mono']

    sort in place
        clauses.sort(cmp=comp_length_clauses)
    '''
    if obj1['infile_path'] < obj2['infile_path'] :
        return -1
    elif obj1['infile_path'] > obj2['infile_path'] :
        return 1
    else : 
        if obj1['infile'] < obj2['infile'] :
            return -1
        elif obj1['infile'] > obj2['infile'] :
            return 1
        else:
            if obj1['var'] < obj2['var'] :
                return -1
            elif obj1['var'] > obj2['var'] :
                return 1
            else:
                if obj1['method'] < obj2['method'] :
                    return -1
                elif obj1['method'] > obj2['method'] :
                    return 1
                else:
                    if obj1['numagent'] < obj2['numagent'] :
                        return -1
                    elif obj1['numagent'] > obj2['numagent'] :
                        return 1
                    else:
                        if obj1['dist'] < obj2['dist'] :
                            return -1
                        elif obj1['dist'] > obj2['dist'] :
                            return 1
                        else:
                            return 0
                            

def launch_MULTI_problem_seq(paramDict, gen_path, java_args):
    log_filename = paramDict['outfile']+'_'+paramDict['dist']
    with open(log_filename + '.log', 'w') as log_file :
        try:
            addToLog(log_file,['LAUNCH MULTI PROBLEM############################'])
            args = computeArgs(CFLAUNCHER_JAR, paramDict,java_args)
            addToLog(log_file,args)
            #run        
            log=jarWrapper(*args)
            addToLog(log_file,log)
            
            addToLog(log_file,['LAUNCH CMP CSQ##################################'])        
            csq_mono_filename = paramDict['csq_mono']
            csq_multi_filename = paramDict['csq']
            csq_stats_filename = csq_multi_filename + '_CMP'        
            args = java_args+[CMPCSQ_JAR]+[csq_multi_filename,csq_mono_filename,csq_stats_filename]
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
            #using prefix or so there is no collision between all the rest...
            tempDict['or_method'] = paramDict['method']
            tempDict['or_numagent'] = paramDict['numagent']
            tempDict['or_dist'] = paramDict['dist']
            tempDict['or_csq'] = paramDict['csq'] 
            tempDict['or_csq_mono'] = paramDict['csq_mono'] 
            
            tempDict['infile_path'] = paramDict['infile_path']
            tempDict['infile'] = paramDict['infile']
            tempDict['var'] = paramDict['var']
            tempDict['timed_out'] =from_boolean_to_int( is_timeout_from_file(paramDict, log_file) )
            statsDict.update(csqStatsDict)
            statsDict.update(tempDict)
            
            addToLog(log_file,statsDict)
            outputs_config_file_one_row(statsDict, statsDict.keys(),multi_problems_allstats_filename,'.csv',gen_path,lock=None)
        except Exception as e:
            addToLog(log_file, [str(e)])
            outputs_config_file_one_row(paramDict,FAILED_MULTI_PROBLEMS_FIELDNAMES_ORDER,failed_multi_problems_filename,'.csv',gen_path, lock=None)    
      

'''        
['infile_path','infile','outfile','var','verbose','timeout','method','numagent','dist','csq','csq_mono']
#ici comparer avec ce qui est déjà dans le fichier
'''        
def readMulti_allstats(filename, ext):
    res= []
    if not os.path.isfile(filename+ext) :
        print 'pas de fichier ', filename
        return res
    with open(filename+ext) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            res.append(rowDict)
    return res       
def compareDict(dictStats, dictMulti):
    if (dictStats['or_method'] != dictMulti['method']
        or dictStats['or_numagent'] != dictMulti['numagent']
        or dictStats['or_dist'] != dictMulti['dist']
        or dictStats['or_csq'] != dictMulti['csq']
        or dictStats['or_csq_mono'] != dictMulti['csq_mono']
        or dictStats['infile_path'] != dictMulti['infile_path']
        or dictStats['infile'] != dictMulti['infile']
        or dictStats['var'] != dictMulti['var']):
        return False
    return True         
'''
def init(l_allstats, l_failed_multi,multi_pb_allstats_filename,failed_multi_pb_filename):    
    global lock_MULTI_ALLSTATS
    global lock_FAILED_MULTI
    global multi_problems_allstats_filename
    global failed_multi_problems_filename 

    lock_MULTI_ALLSTATS = l_allstats
    lock_FAILED_MULTI = l_failed_multi
    multi_problems_allstats_filename = multi_pb_allstats_filename
    failed_multi_problems_filename =failed_multi_pb_filename
''' 

if __name__ == '__main__':
    parser = define_arguments()                                
    args = parser.parse_args()
    argsDict = vars(args)
    
    '''
    lock_allstats = mp.Lock()
    lock_failed_multi = mp.Lock()
    '''
    problemsDir = 'Problems/Bio/'
    #problemsDir = '/home/magma/'
    
    suffix =  '_'+str(argsDict['numagent'])+'agents_'+str(argsDict['div'])+'div_k'+str(argsDict['kpos']) 
   # with open(problemsDir+'/'+GLOBAL_LOG_FILENAME + suffix + '.log', 'a') as log_file_GLOBAL  :   
    log_file_GLOBAL = problemsDir+'/'+GLOBAL_LOG_FILENAME + suffix
    
    print 'les arguments passés sont :', argsDict
    writeToLog(log_file_GLOBAL, argsDict)
    writeToLog(log_file_GLOBAL, argsDict.values())
    
    nbCores = int(argsDict['numagent'])    
    
    global multi_problems_allstats_filename
    global failed_multi_problems_filename 
    multi_problems_allstats_filename = MULTI_PROBLEMS_ALLSTATS_FILENAME +suffix
    failed_multi_problems_filename =FAILED_MULTI_PROBLEMS_FILENAME + suffix
    
    '''
    pool = mp.Pool(nbProcesses,initializer=init, initargs=(lock_allstats,lock_failed_multi, multi_pb_allstats_filename, failed_multi_pb_filename))
    print 'pool = %s' % pool        
    writeToLog(log_file_GLOBAL,['Creating pool with %d processes\n' % nbProcesses])
    '''     
    #ici il faut sorter ces parametres !! tres important !!
    
    list_parameters_MULTI = getArgsForN(problemsDir + MULTI_PROBLEMS_ALGO_PARAMETERS_FILENAME,'.csv',argsDict['numagent'])
    print 'size  old: ', len(list_parameters_MULTI)      
    writeToLog(log_file_GLOBAL, ['size  old: '+ str(len(list_parameters_MULTI) )])
    list_parameters_MULTI.sort(cmp = comp_dict)
    list_parameters_MULTI = getKinDivN_minLast(list_parameters_MULTI,int(argsDict['div']),int(argsDict['kpos']))
    print 'size  new: ', len(list_parameters_MULTI)   
    
    writeToLog(log_file_GLOBAL, ['size  new: '+ str(len(list_parameters_MULTI))])
    
    problemsToSolve =[]
    problemsFromRun = readMulti_allstats(problemsDir +multi_problems_allstats_filename, '.csv')
    for dictMulti in list_parameters_MULTI :
        hasBeenRun = False
        for dictStats in problemsFromRun :
            if compareDict(dictStats, dictMulti):
                hasBeenRun = True
        if not hasBeenRun :
            problemsToSolve.append(dictMulti)
        
    print 'size  new after removing already run experiences: ', len(problemsToSolve)   
    writeToLog(log_file_GLOBAL, ['size  new after removing already run experiences: '+ str(len(problemsToSolve))])
    
    maxHeapSize = 10#(144 / 24) * nbCores - 2 #si on alloue la meme ram a tous les coeurs
    java_args = ['-d64', '-Xms512m', '-Xmx'+str(maxHeapSize)+'g','-jar']
    #faire sequentiel now...
    #calculer le java_args en fonction du nombre d vore !!
    for param in problemsToSolve :
        launch_MULTI_problem_seq(param, problemsDir, java_args=java_args)
    #pool.map(launch_MULTI_problem,list_parameters_MULTI)
    
    