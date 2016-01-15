# -*- coding: utf-8 -*-



def is_a_file(f):
    try:
        f.read()
        return True
    except AttributeError:
        return False
        
"""
def fromObjDictToStrDict(origDict):
    '''
    tranasforme tous les objets dans le dictionnaire en string
    et pour les files, transorme le chemin relatif en chemin absolu
    '''
    res = dict()
    for key, value in origDict.iteritems():
        #if string cp normal
        if isinstance(value, str):
            res[key] = value
        if is_a_file(value):
            res[key] = value.name
        if isinstance( value, int ):
            res[key] = str(value)
        else:
            print 'type Error'    
    return res
newDict = fromObjDictToStrDict(argsDict)
print newDict
"""
'''
def isArgsDistributed(argsDict):
    isDistributed = True
    try :
        if argsDict['dist'] == None or argsDict['dist'] == '':
            isDistributed = False
    except KeyError :
        isDistributed = False
    return isDistributed
def generate_problems_MULTI_from_MONO(mono_problems_filename, ext, log_file):   
    mono_parameters = readCsv(GEN_PATH+FINISHING_MONO_PROBLEMS_FILENAME,log_file)
    for argsDict in mono_parameters :
        if isArgsDistributed(argsDict) :
            pass
        else:
            pass
        
        
    pass
'''
def readCsv(filename, log_file):
    res= []
    if not os.path.isfile(filename) :
        addToLog(log_file,['pas de fichier de p!'+filename])
    with open(filename) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            res.append(rowDict)
    return res
#generate PARAMETERS FOR MONO

def sequential_laucher():
     with open(GEN_PATH+GLOBAL_LOG_FILENAME + '.log', 'w') as log_file_GLOBAL  :      
        problem_files = get_problem_files(PROBLEM_PATH,'.sol')
        problem_files = remove_ext(problem_files,'.sol')
        list_parameters_MONO = generate_parameters_makesolvar(problem_files,LENGTH_LIST,DEPTH_LIST,METH_LIST,METH_N_LIST)
        print list_parameters_MONO
     
        for parameters in list_parameters_MONO :
            addToLog(log_file_GLOBAL,parameters)
            launch_problem_generation_and_mono(parameters)
        
    #generate PARAMETERS FOR MULTI
        parameters = TIMEOUT_MULTI,  METHODS, NUMAGENT_LIST
        list_parameters_MULTI = generate_algos_parameters(MULTI_PROBLEMS_FILENAME, '.csv', parameters, GEN_PATH,log_file_GLOBAL)
        #RUN ALL MONO PROBLEMS AND CREATE DISTRIBUTIONS
        for parametersDict in list_parameters_MULTI :
            addToLog(log_file_GLOBAL,parametersDict)
            launch_MULTI(parametersDict)
    