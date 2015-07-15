# -*- coding: utf-8 -*-

import sys
from commons import *
from subprocess import *
PROJECT_PATH = "/home/magma/projects/dcif/Etude/"
RSRC_PATH = PROJECT_PATH + 'ressources/'
GEN_PATH = PROJECT_PATH + 'gen/'
TOOLS_PATH = "/home/magma/projects/dcif/ScriptDCF/tools/"

def jarWrapper(*args):
    '''    
        http://stackoverflow.com/questions/7372592/python-how-can-execute-a-jar-file-through-a-python-script
        RQ : *args signifie ici un nombre variable d'arguments
    '''
    print list(args)
    process = Popen(['java', '-jar']+list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith('\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split('\n')
    if stderr != '':
        ret += stderr.split('\n')
    ret.remove('')
    print ret
    return ret
def exWrapper(*args):
    print args
    process = Popen(list(args), stdout=PIPE, stderr=PIPE)
    ret = []
    while process.poll() is None:
        line = process.stdout.readline()
        if line != '' and line.endswith('\n'):
            ret.append(line[:-1])
    stdout, stderr = process.communicate()
    ret += stdout.split('\n')
    if stderr != '':
        ret += stderr.split('\n')
    ret.remove('')
    print ret
    return ret
