# -*- coding: utf-8 -*-
import copy
import os
import csv
import partitioning as par
from commons import *
JAVA_ARGS = ['-d64', '-Xms512m', '-Xmx6g','-jar']
def touch_file(path,filename,ext='.csv'):
    with open(path+filename+ext,'w') as csvfile:
        pass
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
def generate_kmet_distribution(argsDict,gen_path,java_args=[]):
    dist_outfile_path = argsDict['infile_path']
    dist_outfile_name = argsDict['infile']+argsDict['dist']
    touch_file(gen_path, dist_outfile_name)
    '''
    dist_outfile_path = argsDict['infile_path']
    dist_outfile_name = argsDict['infile']+argsDict['dist']
    if os.path.isfile(dist_outfile_path+dist_outfile_name+'.dcf') : 
         print ['dist_file already exist, nothing is done']
         return dist_outfile_path,dist_outfile_name
    temp_graph_filename=gen_path+argsDict['infile']+'temp_graph'+argsDict['dist']
    ########buildGraph    
    try:
        os.remove(temp_graph_filename+'.gra')        
        print ['removing old temporary file .gra']
    except OSError:
        print ['temporary file did not exist']
        pass                
    print ['buildGraph #########################################']
    args = java_args+[BUILDGRAPH_JAR]+[argsDict['infile_path']+argsDict['infile'], temp_graph_filename ]
    log = jarWrapper(*args)
    print log
    ########kMetis
    try:
        os.remove(temp_graph_filename+'.gra.part.'+argsDict['numagent'])
        print  ['removing old temporary file .gra.part.N']
    except OSError:
        print  ['temporary file did not exist']
        pass
    print ['kMETIS #############################################']
    args = [KMETIS_EX, temp_graph_filename+'.gra', argsDict['numagent']]
    log = exWrapper(*args)
    print log
    ########graph2DCF
    print ['graph2DCF ##########################################']
    
    args = java_args+[GRAPH2DCF_JAR]+[argsDict['infile_path']+argsDict['infile'],temp_graph_filename+'.gra.part.'+argsDict['numagent'] , dist_outfile_path+dist_outfile_name+'.dcf']
    log = jarWrapper(*args)
    print log
    #clean un peu...
    if os.path.isfile(temp_graph_filename+'.gra') : 
        os.remove(temp_graph_filename+'.gra')
    if os.path.isfile(temp_graph_filename+'.gra.part.'+argsDict['numagent']) : 
        os.remove(temp_graph_filename+'.gra.part.'+argsDict['numagent'])
    return dist_outfile_path,dist_outfile_name
    '''
def generate_naiveEq_distribution(argsDict, gen_path):
    dist_outfile_path = argsDict['infile_path']
    dist_outfile_name = argsDict['infile']+argsDict['dist']
    touch_file(gen_path, dist_outfile_name)
    '''
    #load the sol file
    nbagent = int(argsDict['numagent'])
    sol_filepath = argsDict['infile_path']
    sol_filename = argsDict['infile']   
    sol_file = par.FileSol(sol_filepath,sol_filename)
    sol_file.load()
    
    dcf_file = sol_file.create_dcf_Agent_distribution(nbagent, method = 'naive_eq')
    dcf_file.save()
    
    return dcf_file.path,dcf_file.filename
    '''
#['infile_path','infile','outfile','var','verbose','timeout','method','numagent','dist','csq','csq_mono']
import re
def generatePbNagentRoot(lPbBase, agentArr, gen_path):
    '''
    et la distribution ??
    '''
    
    #rename the csq field    
    distArr = ['_kmet', '_naiveEq']    
    pbGen = []    
    normalProblems = []    
    predistributedProblems = []
    for baseDict in lPbBase :
        if 'kmet' in baseDict['infile'] or 'naiveEq' in baseDict['infile']:
            predistributedProblems.append(baseDict)
        else:
            normalProblems.append(baseDict)
    
    for baseDict in normalProblems :
        for numagent in agentArr:
            for dist in distArr:
                for i in range(numagent):
                    newDict = copy.deepcopy(baseDict)
                    newDict['method'] ="DICF-PB-Star-FixedRoot-"+str(i)#DICF-PB-Token-FixedOrder-0-1-2-3-4-5
                    newDict['csq'] = newDict['csq'] + newDict['method']#permet de les identifier
                    newDict['outfile'] = re.sub(r'(.*)MULTI_.*', r'\1', newDict['csq'])+ newDict['method']
                    newDict['timeout'] = 3600000 #set to one hour for better results
                    newDict['numagent'] = str(numagent)
                    newDict['dist'] = dist+str(numagent)
                    newDict['verbose']=True
                    pbGen.append(newDict)
                newDict = copy.deepcopy(baseDict)
                newDict['method'] ="DICF-PB-Async" #DICF-PB-Token-FixedOrder-0-1-2-3-4-5
                newDict['csq'] = newDict['csq'] + newDict['method']#permet de les identifier
                newDict['timeout'] = 3600000 #set to one hour for better results
                newDict['outfile'] = re.sub(r'(.*)MULTI_.*', r'\1', newDict['csq'])+ newDict['method']
                newDict['numagent'] = str(numagent)
                newDict['dist'] = dist+str(numagent)
                newDict['verbose']=True
                pbGen.append(newDict)
                
    for baseDict in predistributedProblems :
        for dist in distArr:
            newDict = copy.deepcopy(baseDict)
            newDict['method'] ="DICF-PB-Async" #DICF-PB-Token-FixedOrder-0-1-2-3-4-5
            newDict['csq'] = newDict['csq'] + newDict['method']#permet de les identifier
            newDict['timeout'] = 3600000 #set to one hour for better results
            newDict['outfile'] =re.sub(r'(.*)MULTI_.*', r'\1', newDict['csq'])+ newDict['method']
            newDict['numagent'] = str(baseDict['numagent'])
            newDict['dist'] = dist+str(baseDict['numagent'])
            newDict['verbose']=True
            pbGen.append(newDict)
            '''
            #if 'naiveEq' in dist and 'kmet' in baseDict['infile'] :
                #generate_naiveEq_distribution(newDict,gen_path)
            #    pass
         #   elif 'kmet' in dist and 'naiveEq' in baseDict['infile'] :
          #      #generate_kmet_distribution(newDict, gen_path,java_args=JAVA_ARGS)     
           #     pass
            '''

            for i in range(int(float(baseDict['numagent']))):
                    newDict = copy.deepcopy(baseDict)
                    newDict['method'] ="DICF-PB-Star-FixedRoot-"+str(i)#DICF-PB-Token-FixedOrder-0-1-2-3-4-5
                    newDict['csq'] = newDict['csq'] + newDict['method']#permet de les identifier
                    newDict['outfile'] = newDict['csq']+ newDict['method']
                    newDict['timeout'] = 3600000 #set to one hour for better results
                    newDict['numagent'] = str(baseDict['numagent'])
                    newDict['dist'] = dist+str(baseDict['numagent'])
                    newDict['verbose']=True
                    pbGen.append(newDict)
            
           
    return pbGen
    
def readPbBase(filename, ext):
    res= []
    if not os.path.isfile(filename+ext) :
        print 'pas de fichier ', filename
    with open(filename+ext) as csvfile:
        reader = csv.DictReader(csvfile)
        for rowDict in reader:
            res.append(rowDict)
    return res
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
        
def main():
    problemsDir = '/home/magma/projects/dcif/Etude/Problems/Bio_focus/Bio/'
    supDir= '/home/magma/projects/dcif/Etude/Problems/Bio_focus/'
    basePbFilename = 'base_problems_MULTI'
    genPbFilename = 'GEN_algo_parameters_MULTI_problems'
    pbBase = readPbBase(supDir + basePbFilename,'.csv') 
    agentArr= [2,4,6,8]
    print agentArr
    pbGen = generatePbNagentRoot(pbBase,agentArr,problemsDir)
    print pbGen
    fieldnames =['infile_path','infile','outfile','var','verbose','timeout','method','numagent','dist','csq','csq_mono']
    outputs_config_file_all_rows(pbGen, fieldnames, genPbFilename,'.csv', supDir)
    
main()
    