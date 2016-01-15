# -*- coding: utf-8 -*-
'''
module handling the cutting of problems
'''
from commons import *
import partitioning as par
import os
import fnmatch

def translate_cnf_to_sol_tree ():
    matches = []
    print('WORK DIR: '+os.getcwd() + "\n")
    oldWrkDir = os.getcwd()
    newWrkDir = 'Problems/SAT/'
    gen_path = 'SATgen/'
    
    os.chdir(newWrkDir)
    java_args = ['-jar']
    CNFSAT2SOL_JAR = '/home/magma/projects/dcif/ScriptDCF/tools/cnfsat2sol.jar'
    for root, dirnames, filenames in os.walk('.'):
        print root
        newDir = '../'+gen_path+root
        make_dirs_safe(newDir)
        print newDir
        for base_filename in remove_ext(fnmatch.filter(filenames, '*.cnf'),'.cnf'):
            print base_filename        
            infile = root+'/'+base_filename
            outfile = '../'+gen_path+root+'/'+base_filename
            args = java_args + [CNFSAT2SOL_JAR, infile, outfile ]
            print args
            res = jarWrapper(*args)
            print res
        
    print('WORK DIR: '+os.getcwd() + "\n")
    print matches
    
    os.chdir(oldWrkDir)
    print('WORK DIR: '+os.getcwd() + "\n")


lDiv = [2,4,8,16]
nbMinClauses = 80
nbSolParDiv = 1
#WRK DIR = ETUDE
lCutMethods = ['kmetis', 'naive_eq']
def cut_Problems(problemsDir):
    '''
    on va dans les repertoires avec les .sol générées et our chqaue .sol 
    on crée un repertoire ou on met les versions du probleme decoupées
    attention, quand on passe un path, toujorus mettre un slash a la fin...
    '''
    for root, dirnames, filenames in os.walk(problemsDir):
        print root
        
        for base_filename in remove_ext(fnmatch.filter(filenames, '*.sol'),'.sol'):
            #create a dir for each problem and cut it inside
            solDir = root + '/' + base_filename
            make_dirs_safe(solDir)
            origin_fileSol = par.FileSol(root+'/', base_filename)
            origin_fileSol.load()
            
            #copy the whole .sol file in the full folder            
            make_dirs_safe(solDir+'/full')
            origin_fileSol.save(path = solDir+'/full/')
            
            for div in lDiv :
                subDir = solDir+'/div' + str(div)+'/'
                make_dirs_safe(subDir)
                print 'SUBDIR'+subDir
                for meth in lCutMethods :
                    dcfFile = origin_fileSol.create_dcf_Agent_distribution(div, method = meth, outpath= subDir)
                    dcfFile.save(path = subDir)
                    pf = dcfFile.get_pf()
                    
                    for numagent in range(div):
                        if numagent >= nbSolParDiv : 
                            break
                        prefix = 'sub-'
                        suffix = '_'+meth+'-div'+str(div)+'k'+str(numagent)
                        
                        newFileSol = par.FileSol(subDir,prefix+base_filename+suffix)
                        clauses = dcfFile.get_clauses_of_one_agent(numagent)
                        print 'METHOD', meth
                        if  len(clauses) < nbMinClauses :
                            print 'COT IUTNTINTNTIN'
                            continue#robustesse
                        newFileSol.load_file_sol_from_clauses(clauses)
                        newFileSol.add_IndexedLine(pf)
                        print ''
                        newFileSol.save()
                    

dirProb = 'Problems/sourceCnf'
cut_Problems(dirProb)