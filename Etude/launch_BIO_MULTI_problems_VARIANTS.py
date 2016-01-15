# -*- coding: utf-8 -*-

# -*- coding: utf-8 -*-

'''
takes n = nbCore = nbAgent as argument
and a filenameProblem

do we do the walk ?? or is it one level, flat ?
only when !! RMA is 144 Go !!
'''
from generate_BIO_problems import *
import argparse

def writeToLog(log_filename, toAdd) :
    with open(log_filename + '.log', 'a') as log_file :
        for line in toAdd :
            log_file.write(str(line) + '\n')
            print line
        
def define_arguments():    
    parser = argparse.ArgumentParser(description='Process to launch Multi problems with a fixed number of agents')
    parser.add_argument('-n', '--numagent', type= int ,required=True,
                        help="le nombre d\'Agents, doit correspondre idealement au nbre de cores..")
    parser.add_argument('-d', '--div', type= int,
                        help="le nbre de div")
    parser.add_argument('-k','--kpos', type= int,
                        help="la position de la partition")
    return parser
def chunks(l, n):
    """Yield successive n-sized chunks from l."""
    for i in xrange(0, len(l), n):
        yield l[i:i+n]
        
def readPbBase(filename, ext):
    res= []
    if not os.path.isfile(filename+ext) :
        print 'pas de fichier ', filename
    with open(filename+ext) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
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
        ici method inclut l'ordre  don c'est bon
    '''
    params = ['infile_path','infile','var','method','numagent','dist']
    for p in params :
        if obj1[p] < obj2[p] :
            return -1
        elif obj1[p] > obj2[p] :
            return 1
    return 0
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

def launch_MULTI_problem_seq(paramDict, gen_path, java_args):
    log_filename = paramDict['outfile']+'_'+paramDict['dist']+'_'+paramDict['method']
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
def readMulti_allstats_allfiles(path, configfilename):
    print path
    print configfilename
    res=[]
    for filename in os.listdir(path) :
        if configfilename in filename:
            with open(path+filename) as csvfile:
                reader = csv.DictReader(csvfile)
                for rowDict in reader:
                    res.append(rowDict)
    print res
    return res
    

def compareDict(dictStats, dictMulti):
    if (dictStats['or_method'] != dictMulti['method']
        or dictStats['or_numagent'] != dictMulti['numagent']
        or dictStats['or_dist'] != dictMulti['dist']
      #  or dictStats['or_csq'] != dictMulti['csq']#removing this because we do not want to redo the same eproblem 
        or dictStats['or_csq_mono'] != dictMulti['csq_mono']
        or dictStats['infile_path'] != dictMulti['infile_path']
        or dictStats['infile'] != dictMulti['infile']
        or dictStats['var'] != dictMulti['var']):
        return False
    return True         
import copy
['infile_path','infile','outfile','var','verbose','timeout','method','numagent','dist','csq','csq_mono']
def generatePbNagentRoot(lPbBase, numagent):
    '''
    et la distribution ??
    '''
    #rename the csq field    
    distArr = ['_kmet', '_naiveEq']    
    pbGen = []
    
    normalProblems = []    
    predistributedProblems = []
    
    for baseDict in lPbBase :
        for i in range(numagent):
            for i in distArr:
                newDict = copy.deepcopy(baseDict)
                newDict['csq'] = newDict['csq'] + str(method)#permet de les identifier
                newDict['method'] ="DICF-PB-Star-FixedRoot-"+str(i)#DICF-PB-Token-FixedOrder-0-1-2-3-4-5
                newDict['outfile'] = newDict['outfile']+ str(method)
                newDict['timeout'] = 3600000 #set to one hour for better results
                newDict['numagent'] = str(numagent)
                newDict['dist'] = dist+str(numagent)
            pbGen.append(newDict)
    #on ajoute aussi les pb async mais il n'y en a pas beaucoup...
    for baseDict in lPbBase :
        for i in distArr:
            newDict = copy.deepcopy(baseDict)
            newDict['csq'] = newDict['csq'] + 'method'#permet de les identifier
            newDict['method'] ="DICF-PB-Async" #DICF-PB-Token-FixedOrder-0-1-2-3-4-5
            newDict['timeout'] = 3600000 #set to one hour for better results
            newDict['outfile'] = newDict['outfile']+ str(method)
            newDict['numagent'] = str(numagent)
            newDict['dist'] = dist+str(numagent)
        pbGen.append(newDict)
    return pbGen
    
if __name__ == '__main__':
    parser = define_arguments()                                
    args = parser.parse_args()
    argsDict = vars(args)
    
    '''
    lock_allstats = mp.Lock()
    lock_failed_multi = mp.Lock()
    '''
    #problemsDir = 'Problems/Bio_ext/'
    #problemsDir = '/home/magma/BIO_mono_multi_debrief/'
    #problemsDir = '/home/magma/'
    problemsDir = 'Problems/Bio_focus/Bio/'
    supDir = 'Problems/Bio_focus/'
    suffix =  '_'+str(argsDict['numagent'])+'agents_'+str(argsDict['div'])+'div_k'+str(argsDict['kpos']) 
   # with open(problemsDir+'/'+GLOBAL_LOG_FILENAME + suffix + '.log', 'a') as log_file_GLOBAL  :   
    log_file_GLOBAL = supDir+GLOBAL_LOG_FILENAME + suffix
    writeToLog(log_file_GLOBAL, ['NEW EXPERIENCE'])
    print 'les arguments passés sont :', argsDict
    writeToLog(log_file_GLOBAL, argsDict)
    writeToLog(log_file_GLOBAL, argsDict.values())
    
    nbCores = int(argsDict['numagent'])    
    
    global multi_problems_allstats_filename
    global failed_multi_problems_filename 
    multi_problems_allstats_filename = 'FOCUS_'+MULTI_PROBLEMS_ALLSTATS_FILENAME +suffix
    failed_multi_problems_filename ='FOCUS_'+FAILED_MULTI_PROBLEMS_FILENAME + suffix

    problemsource = supDir + 'GEN_algo_parameters_MULTI_problems'
    list_parameters_MULTI  = getArgsForN(problemsource, '.csv', argsDict['numagent'])
    writeToLog(log_file_GLOBAL, ['SSSS'+ str(len(list_parameters_MULTI))])  

    #list_parameters_MULTI = getKinDivN_minLast(list_parameters_MULTI,argsDict['div'],argsDict['kpos'])

    writeToLog(log_file_GLOBAL, ['SSSS'+ str(len(list_parameters_MULTI))])    
    
    
    print 'size all for agents: ', len(list_parameters_MULTI)          
    writeToLog(log_file_GLOBAL, ['size  new: '+ str(len(list_parameters_MULTI))])
    
    
    
    maxHeapSize = 5#(144 / 24) * nbCores - 2 #si on alloue la meme ram a tous les coeurs
    java_args = ['-d64', '-Xms512m', '-Xmx'+str(maxHeapSize)+'g','-jar']
    #faire sequentiel now...
    #calculer le java_args en fonction du nombre d vore !!
    for param in list_parameters_MULTI:
        launch_MULTI_problem_seq(param, problemsDir, java_args=java_args)

