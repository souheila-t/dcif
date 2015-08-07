# -*- coding: utf-8 -*-

import pandas as pd
import numpy as np
import csv as csv
import matplotlib.pylab as plt

path = '/home/magma/exp_BIO/'
generatedProblems = 'generated_MONO_problems.csv'

finishingProblems = 'finishing_MONO_problems.csv'
failedProblems = 'failed_MONO_problems.csv'


#do the walk then...
df = pd.read_csv(path+finishingProblems, header=0)       

#print df.describe()




valuesToDrop = ['max', 'sent','root', 'CPU', 'outfile', 'timeout' , 'csq', 'path']
def drop_values (valuesToDrop, df):
    '''
    if contains (not strict equality here...)
    '''
    new_df = df
    for columnName in list(df.columns.values) :
        for v in valuesToDrop :
            if v in columnName :
                print 'dropping :',columnName,'::',v 
                new_df = new_df.drop([columnName],axis = 1)
                break
    return new_df
#df = drop_values(valuesToDrop, df)


def show_duplicates(df):
    '''
    subset are the colums identifying a problem, (all but measures)
    
    see if indexing plays a part in it...
    '''
    idset = ['infile_path', 'infile', 'outfile', 'method','timeout', 'var', 'csq', 'dist', 'numagent','problem']
    df_deduplicated = df.drop_duplicates(subset = idset ,take_last = True)
    
    df_cat = pd.concat([df, df_deduplicated])
    df_cat = df_cat.reset_index(drop=True)

    df_gpby = df_cat.groupby(list(df_cat.columns))
    
    idx = [x[0] for x in df_gpby.groups.values() if len(x) == 1]
    print df_cat.reindex(idx)
    
def remove_duplicates(df):
    idset = ['infile_path', 'infile', 'outfile', 'method','timeout', 'var', 'csq', 'dist', 'numagent','problem']
    df = df.drop_duplicates(subset = idset ,take_last = True)
    return df
#identify a problem not with its measures : all not numerical ?

print df.describe()
print 'number of rows',len(df.index)
print 'number of column',len(df.columns.values)
print df.columns.values


def getAllVarOfProblem(df):
    grouped = df.groupby('infile')
    nbGrouped = len(grouped)
    print 'len :', nbGrouped
    
    for index, g in enumerate(grouped.groups):
        if index > 30 :
            print 'newGROUP',index
            print g

#getAllVarOfProblem(df)
#print list(df.columns.values)
def calc1(df):
    '''
    moyenne d'Exectuion par fichier var
    '''
    grouped = df.groupby('var')
    print grouped.mean()['total time']
    
def expandVar(df):
    '''
    
    '''
    pass

#calc1(df)