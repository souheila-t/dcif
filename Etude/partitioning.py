# -*- coding: utf-8 -*-
'''
partitionnement TP/axiom et multi-agents
remarque on fait des deep copy quna methode name = create 
'''
from commons import *
import re
import random
import copy
class IndexedLine():
    '''
    might be a comment as well as a production field
    '''
    def __init__(self, index, data):
        self.data = str(data)
        self.index = index
        
class Clause(IndexedLine):
    def __init__(self, index,data):
        IndexedLine.__init__(self,index,data)
    def isTP (self):
        return 'top_clause' in self.data #top_clauseS ??
    def isAxiom(self):
        return 'axiom' in self.data
    def transformToTP(self):
        if not self.isTP():                
            newTP = re.sub(r'(cnf\(\w*,\s*)axiom(\s*,\s*\[[\w\-,\s]*\]\)\.)',
                '\\1top_clause\\2', self.data)
            self.data = newTP
    def transformToAxiom(self):
        if not self.isAxiom():
            newAxiom = re.sub(r'(cnf\(\w*,\s*)top_clause(\s*,\s*\[[\w\-,\s]*\]\)\.)',
                '\\1axiom\\2', self.data)
            self.data = newAxiom
    def nbCommas(self):
        '''        
        une heuristique peut etre de compter le nombre de virgules...
        a raffiner http://stackoverflow.com/questions/18906514/regex-for-matching-functions-and-capturing-their-arguments
        il faut faire un parser normalement
        '''        
        
        p = re.sub(r'(cnf\(\w*,)\s*\w*\s*,\s*\[([\w\-,\s]*)\]\)\.',
            '\\2', self.data)     
        newString,nbCommas = re.subn(',','', p)
        return nbCommas
        

def comp_index(obj1,obj2):  
    if (not isinstance(obj1,IndexedLine)) or (not isinstance(obj2,IndexedLine)) :
        raise Exception('Comparing objects which are not indexedLines')
    if obj1.index < obj2.index :
        return -1
    elif obj1.index > obj2.index :
        return 1
    else : 
        return 0

def comp_length_clauses(obj1,obj2):    
    if (not isinstance(obj1,Clause)) or (not isinstance(obj2,Clause)) :
        raise Exception('Comparing objects which are not clauses')
    if obj1.nbCommas() < obj2.nbCommas():
        return -1
    elif obj1.nbCommas() > obj2.nbCommas() :
        return 1
    else : 
        return 0

def chunks(l, n):
    """Yield successive n-sized chunks from l."""
    for i in xrange(0, len(l), n):
        yield l[i:i+n]
        
class FileBase(object):
    def __init__(self,path, filename, ext):
        self.filename = filename
        self.ext =ext
        self.path = path
    def load(self,path = None,filename = None,):
        if filename == None:
            filename = self.filename
        if path == None :
            path = self.path
        if not (os.path.isfile(path+filename+self.ext)):
            raise Exception( 'erreur, fichier :', path+filename+self.ext,'non present')
        f = open(path+filename+self.ext, 'r')
        self.lines=[]        
        
        for index, line in enumerate(f):
            if line.startswith('cnf'):
                self.lines.append(Clause(index,line))
            else:                
                self.lines.append(IndexedLine(index,line)) 

    def count_clauses(self):
        count = 0 
        for line in self.lines:
            if isinstance(line, Clause):
                count += 1
        return count
    def reset_all_to_axioms(self):
        for line in self.lines:
            if isinstance(line, Clause):
                line.transformToAxiom()
            
    def save(self, path = None,filename= None):
        if filename == None :
            filename = self.filename
        if path == None :
            path = self.path
        f = open(path+filename+self.ext, 'w')
        #let us sort our lines by index
        self.lines.sort(cmp=comp_index)# trie la liste 
        for index,line in enumerate(self.lines):
       #     if index != line.index :
      #          raise Exception('problem with the indexing of our lines...')#os this the right way to call an exception ?
            f.write(line.data)
        f.close()  
        
class FileSol(FileBase):
    def __init__(self, path, filename,ext ='.sol', lines = None):
        FileBase.__init__(self, path, filename, ext)
        self.lines  = lines
        
    '''
    faire la même fonction qui internalise tout ici pour la  distribution plus subtile...
    prend des repertoires de travail et tout...
    '''
    def create_a_FileSol_wit_a_TP_distribution_naiveShort(self,perc, seuilMin=1) :
        '''
        TP = top_clause : args = percentage, seuil min
        return a new File Sol object with new distribution
        perc =[0, 100]
        
        faire une variante qui garde les TP originales ??
        pas RANDOM !! on prend les n premiers plus courtes dans l'ordre d'apparition !
        '''
        clauses = []
        otherlines =[]
        for line in copy.deepcopy(self.lines) :
            if isinstance(line,Clause):
                clauses.append(line)
            else:
                otherlines.append(line)
        
        clauses.sort(cmp=comp_length_clauses)

       # for clause in clauses :
        #    print clause.index, clause.data
        
        #on prend les perc premier et au moins minsueil, ttes les autres deviennent axiom...
        nbTP = perc/100.*len(clauses)      
       # print 'perc',perc,'len clauses',len(clauses),'nbTP',nbTP
        for index, clause in enumerate(clauses) :
            if index < seuilMin or index < nbTP :
                clause.transformToTP()
              #  print 'aie'
            else:
                clause.transformToAxiom()
               # print 'ouch'
        
        distributed_lines = clauses + otherlines   #on ne sort pas car on s'en fout, çaa sera fait après
        distrib_name =  '_TPnaiveshortdist_per'+str(perc)+'_'+'seuil'+str(seuilMin)
        filename = self.filename + distrib_name
        return FileSol(self.path,filename = filename,lines=distributed_lines), int(nbTP)
    def is_valid(self):
        '''doit contenir au moins une top_clause pour etre valide
        '''
        for line in self.lines:
            if isinstance(line,Clause):
                if line.isTP():
                    return True
        return False
        
    def create_dcf_Agent_distribution(self,nbAgents,method = 'naive_eq',outpath = None):
        '''
        on doit prendre en compte l'index des lignes désormais
        method = ['naive_eq', 'naive_indent'(if possible),'sol2dcf'] en cours de debug
        remarque, au lieu d'ecrie agent(agX) on ecrit agent(X) ne change rien normalement
        '''
        filename = self.filename
        if outpath == None :
            outpath = self.path
            
        if method == 'naive_eq':
            dcf_lines = self.naive_eq(nbAgents)
            filename += '_naiveEq'+str(nbAgents)
            return FileDCF(outpath,filename,lines= dcf_lines )
        elif method == 'naive_indent':
            dcf_lines = self.naive_indent(nbAgents)
            filename += '_naiveEq'+str(nbAgents)
            return FileDCF(outpath,filename,lines= dcf_lines )
        elif method == 'kmetis':
            #execute the kmetis trnasifmantion
            filename += '_kmet'+str(nbAgents)
            outpath, outfilename = generate_kmet_distribution_simple(nbAgents, self.path, self.filename, outfile_path=outpath, outfile_name=filename)
            res = FileDCF(outpath, outfilename)
            res.load()
            return res
            
    def naive_eq(self,nbAgents):
        clauses = []
        pf=[]
        comments=[]
        for line in copy.deepcopy(self.lines) :
            if isinstance(line,Clause):
                clauses.append(line)
            elif 'pf(' in line.data :
                pf.append(line)
            elif line.data.startswith('%'):
                comments.append(line)
                
        clauses.sort(cmp=comp_index)
        sizeAgent = len(clauses)/nbAgents     
        agent_clauses = []
        for indexAgent,c in enumerate(chunks(clauses, sizeAgent)):
            if not indexAgent >= nbAgents :
                #on rajoute un agent que si c'est légal                 
                new_agent_line = IndexedLine(0,'\nagent('+str(indexAgent) +').\n')
                agent_clauses.append(new_agent_line)
            for line in c :
                agent_clauses.append(line)         
        #on doit réindexer tout cela
        dcf_lines=[]
        for index, line in enumerate(comments + agent_clauses + pf) :
            line.index = index
            dcf_lines.append(line)
            
        return dcf_lines
    def naive_indent(self,nbAgents):
        '''
        si pas d'indentation, on fail et renvoie exception qui devrait etre 
        catch.e bien plus haut... (niveau de la generattion de problemes)
        '''
        '''
        on commence par regarder le nombre de partitions qui existent dans l'indentation
        et on fait comme avec chunk la dessus aussi...
        '''
        self.lines.sort(cmp=comp_index)
        partitions = []
        pf=[]
        comments=[]
        currentPartition=None
        
        for line in copy.deepcopy(self.lines) :
            if not line.data or not(line.data.strip()):
                if currentPartition == None:
                    currentPartition = []
                else:
                    partitions.append(currentPartition)
                    currentPartition = []
            if isinstance(line,Clause):
                currentPartition.append(line)
                
            elif 'pf(' in line.data :
                pf.append(line)
            elif line.data.startswith('%'):
                comments.append(line)
        if len(partitions) == 1 or len(partitions) < nbAgents:
            raise Exception('pas de fucking indentation suffisante pour fuckinf agent paritioning')
                #on chnunk sur le nbAgets
          
        agent_clauses = []
      #  print 'nbAg', nbAgents
       # print 'nbPartition', len(partitions)
        for indexAgent,p in enumerate(chunks(partitions, len(partitions)/nbAgents)):
            if not indexAgent >= nbAgents :                    
                new_agent_line = IndexedLine(0,'\nagent('+str(indexAgent) +').\n')
                agent_clauses.append(new_agent_line) 
            for line in merge_inner_lists(p):
                agent_clauses.append(line) 
                
        dcf_lines=[]
        for index, line in enumerate(comments + agent_clauses + pf) :
            line.index = index
            dcf_lines.append(line)
           
        return dcf_lines
    def load_file_sol_from_clauses(self,clauses):
        nbPrecLines = 0
        if self.lines != None:
            nbPrecLines = len(self.lines)
        else:
            self.lines= []
        for index, clause in enumerate(clauses):
            newIndex = index + nbPrecLines
            newClause = Clause(newIndex, clause)
            self.lines.append(newClause)
            
    def add_IndexedLine(self,lineData):
        '''
        pas une clause ici
        '''
        if self.lines != None :
            index = self.lines[-1].index + 1
        else :
            index = 0
        self.lines.append(IndexedLine(index, lineData))
        
class FileDCF(FileBase):
    def __init__(self,path,filename, ext = '.dcf',lines = None):
        FileBase.__init__(self,path,filename,ext)
        self.lines  = lines
    def is_valid(self):
        '''
        est valide si tous les agents ont au moins une clause et si au moins
        un agent a une Top_clause
        
        not checked
        '''
        allAgentsClauses = []
        otherlines =[]
        nbAgents = 0
        numAgents = []
        existTP = False
        currentAgent = None
        for line in self.lines :
            if isinstance(line,Clause):
                if currentAgent == None :
                    return False
                currentAgentClauses.append((currentAgent, line))
                if line.isTP():
                    existTP = True
            else:
                otherlines.append(line)
                if 'agent(' in line.data :
                    if currentAgent != None :
                        allAgentsClauses.append(currentAgentClauses)
                    currentAgent = re.findall(r'\d+', line.data)[0]
                    currentAgentClauses=[]
                    numAgents.append(currentAgent)
                    nbAgents += 1
        #instruction terminale car on ne le fait jamais sinon
        allAgentsClauses.append(currentAgentClauses)
        
        if not existTP:
            return False
        if nbAgents != len(set(numAgents)): #cela veut dire que le fichier est bizarrement écrit...
            return False
        for agentClauses in allAgentsClauses :
            if len(agentClauses) == 0 :
                return False
        return True
    def toSol(self):#on l'a externalisé parce que prend trop de place
        return dcf2sol(self)
        
    def create_a_FileSol_wit_a_TPdistribution_for_each_agent(self,percTotal, seuilMin=1, method = 'random', filename = None):
        '''
        return a new sol with this distribution and a new dcf with a new filename
        percTotal = [0;100]
        
        on considere ici que toutes les clauses sont des axioms        
        method = ['random', 'short']
        '''
        pf=[]
        comments=[]
        #stocker pour chaque agent ses propres clauses 
        allAgentsClauses = []
        currentAgentClauses = []
        currentAgent=None
        
        nbClauses = 0
        nbAgents = 0
        
        for line in copy.deepcopy(self.lines) :
            if isinstance(line, Clause):
                if currentAgent == None:
                    raise Exception ('wtf on a des cnf dans un dcf alors quon a pas declaé d agents !!')
                currentAgentClauses.append(line)
                nbClauses += 1
            else:#il faudrait utiliser starts with...
                if 'agent(' in line.data :
                    nbAgents +=1
                    if currentAgent != None:
                        allAgentsClauses.append(currentAgentClauses)
                    currentAgentClauses = []
                    currentAgent = re.findall(r'\d+', line.data)[0]
                elif 'pf(' in line.data :
                      pf.append(line)
                elif line.data.startswith('%'):
                    comments.append(line)
        #on recupere les clauses du dernier agent
        allAgentsClauses.append(currentAgentClauses)
               
        #le nbre de tp que l on ecrit effectivnet
        nbNewTp = 0
        #le nbre detop clauses que l on veut avoir a la fin
        nbTp = percTotal / 100. * nbClauses
        #nbre de clauses par agent
        nbClAg =  int(nbTp / nbAgents)
        for agentClauses in allAgentsClauses :
         #   print len(agentClauses)
           # faire un random sans remise sur la longueur des agents clauses et les transformer puis incrementer le compteur
            #pas besoin de verifier si c'est deja TOP CLAUSE ou pas
            if len(agentClauses) == 0:
                raise Exception('partition d un agent vide...')
            sizesample = min(nbClAg, len(agentClauses))  
        #faire le while qui fill le compteur
        #puis faire variante avec elecion sur les plus courtes
            if method == 'random':
                indexesTP = random.sample(range(len(agentClauses)), sizesample)
            elif method == 'short':
                agentClauses.sort(cmp = comp_length_clauses)
                indexesTP = range(sizesample)
            #prendre les npremeirs
            #permet de reset et de controler les vertiables parametres
            for c in agentClauses :
                c.transformToAxiom()
            for indexC in indexesTP :
               # print indexC
                agentClauses[indexC].transformToTP()             
                nbNewTp += 1
        #done here on a trnasfomré les CLAUSES !!
        cpt = nbTp - nbNewTp
        agentClausesIndex = 0
        while (cpt > 0 and nbNewTp != nbTp ):
            agentClauses = allAgentsClauses[agentClausesIndex]
            for c in agentClauses :
                if c.isAxiom():
                    c.transformToTP()
                    nbNewTp +=1
                    break #cela veut dire qu'on a que des top_clause ici
            if nbNewTp >= nbTp:
                break
            #pire cas : on veut rajouter n top clauses a nu seul agent...
            cpt -= 1            
            agentClausesIndex = (agentClausesIndex + 1 )% len(agentClauses)
            

        sol_lines = comments + merge_inner_lists(allAgentsClauses) + pf
        if filename == None:
            filename = self.filename+'_TPuniform'+method+'dist_per'+str(percTotal)+'_'+'seuil'+str(seuilMin)
        return FileSol(self.path,filename,ext = '.sol', lines = sol_lines )
        
    def get_pf(self):
        for line in self.lines:
            if line.data.startswith('pf'):
                return line.data
        raise Exception ('Pas de Pf dans le dcf !')

    def get_clauses_of_one_agent(self,numagent):
        print self.filename        
        print len(self.lines)
        
        clauses = []
        isAgent = False
        for line in self.lines:
            if 'agent' in line.data:
                currentAgent = int(re.findall(r'\d+', line.data)[0])
                if numagent != currentAgent :
                    isAgent = False
                else:
                    isAgent = True
            elif isinstance(line, Clause):
                if isAgent :
                    clauses.append(line.data)
        return clauses

    
solFilename = 'ressources/glucolysis.sol'
def dcf2sol(dcfFile): 
    '''
    on se fiche des agents, on répercute uniquement les TP et les Axioms 
    ici on détruit toute indentation parce qu'on s'en fiche on suppose 
    qu'on a déja le partiotinnement entre agents
    a l. origine dans le .dcf
    '''
    clauses = []
    comments =[]
    pf=[]
    sol_lines=[]
    
    for line in dcfFile.lines :
        if isinstance(line,Clause):
            clauses.append(line)
        else:
            if 'agent' in line.data :
                pass
            elif 'pf(' in line.data :
                pf.append(line)
            elif line.data.startswith('%'):
                comments.append(line)
    for index, line in enumerate(comments + clauses + pf) :
        if isinstance(line,Clause):
            new_line = Clause(index, line.data)
        else:
            new_line = IndexedLine(index, line.data)
        sol_lines.append(new_line)
        
    return FileSol(dcfFile.path,dcfFile.filename,ext='.sol',lines = sol_lines)

    