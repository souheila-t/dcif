# -*- coding: utf-8 -*-
import pandas as pd
import numpy as np
import csv as csv
import matplotlib.pylab as plt
import os
path = '/home/magma/BIO_mono_multi_debrief/stats/'

generatedProblems = 'generated_MONO_problems.csv'
finishingProblems = 'finishing_MONO_problems.csv'
failedProblems = 'failed_MONO_problems.csv'
multiProblems = 'running_stats_MULTI_problems' 



#do the walk then...
df = pd.read_csv(path+finishingProblems, header=0)       

#print df.describe()

dfGen =pd.read_csv(path+generatedProblems, header=0)       
dfFin =pd.read_csv(path+finishingProblems, header=0)       
dfFail=pd.read_csv(path+failedProblems, header=0)  

dfFin_part = pd.read_csv('/home/magma/BIO_mono_multi_debrief/'+finishingProblems)

def removeOrderForTokenMeth(df):
    df.loc[df['method'].str.contains("Token"),'method']= 'DICF-PB-Token'
def replace_meth(x):
    method=x['method']
    if 'DICF-PB-Async' in method or 'DICF-PB-Star' in method :
        return method
    elif 'Token' in method :
        return 'DICF-PB-Token'
    else :
        return method
def do_rename_nethod(df):    
    print '###############################################'
    #df['new_method']=df['method'].map(lambda x: substrings_in_string(x, title_list))
    print df['method'].unique()
    df['method']=df.apply(replace_meth, axis=1)
    print df['method'].unique()
    
    
def renameParamsOr(df):
    df['method'] = df['or_method']
    df['numagent'] = df['or_numagent']
    df['csq'] = df['or_csq']
    df['dist'] = df['or_dist']
    print df.columns
    
def readMulti():
    frames=[]
    for filename in os.listdir(path) :
        if not multiProblems in filename:
            continue
        dfMult=pd.read_csv(path+filename, header=0)  
        frames.append(dfMult)
    return pd.concat(frames)    

dfMulti = readMulti()
renameParamsOr(dfMulti)
print dfMulti['method'].unique()
removeOrderForTokenMeth(dfMulti)

print dfMulti['method'].unique()
#do_rename_nethod(dfMulti)
#print dfMulti['outfile'].unique()
    
    
def printInfoOnDf(df):
    print df.describe()
    print 'number of rows',len(df.index)
    print 'number of column',len(df.columns.values)
    print df.columns.values
        
#printInfoOnDf(dfMulti)
#print dfMulti['timed_out']


def proportionTimeOut(df,discreteVar):
    by_var = df.groupby([discreteVar,'timed_out']) #groups the data act on groups
       #seperately
    table = by_var.size() #gets group size counts, hashed by the two variables
    table = table.unstack() #splits the data into 2 columns, 0, 1, each indexed by the
    #other variable
    normedtable = table.div(table.sum(1), axis=0) #divides the counts by the totals
    return normedtable
def computeProp(df):
    discreteVarList = ['or_numagent','method']#for now no refinement
    fig1, axes1 = plt.subplots(3,1) #creates a 3x1 blank plot
    for i in range(len(discreteVarList)): #now we fill in the subplots
        var = discreteVarList[i]
        table = proportionTimeOut(df,var)
        table.plot(kind='barh', stacked=True, ax=axes1[i])
    fig1.show() #displays the plot, might not need this if running in 'interactive' mode
def getProblemsUnderTime(df, time):
    '''
    get problems having finished under xxxxxx ms
    '''
    return df[df['total time'] < time]
valuesToDrop = ['max', 'sent','root', 'CPU', 'outfile', 'timeout' , 'csq', 'path']
valuesToDrop = ['max', 'sent','root', 'CPU', 'csq']

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
dfMulti = drop_values(valuesToDrop, dfMulti)

print 'number of rows',len(dfMulti.index)

352392

res = getProblemsUnderTime(dfMulti, 500000)
print 'number of rows',len(res.index)
computeProp(res)

'''
def showtime_oftimeout(df):
    l = df[df['timed_out'] == 1]['total time']
    print l.unique()
    print len(l)
def showtime_of(df):
    l = df['total time'] 
    print l.unique()
    print len(l)
    print max (l)
showtime_oftimeout(dfMulti)
showtime_of(dfFin)
'''
def printRepartiondesfiniishsiprone():
    pass
print 'number of rows',len(res.index)
res = getProblemsUnderTime(dfFin, 600000)
print 'number of rows',len(res.index)


import numpy as np
import matplotlib.pyplot as plt
import numpy as np

def makeHistoFinishingMono(df):
    '''
    2000000
    use somethiong else thant total time plz == exectution time would be better
    '''
    df = df['total time']
    s= pd.Series(df, name ='total time')
    a = pd.np.array(s)
    print a
    a = a/1000/60   #convert into minutes
   
    
    
    hist, bins = np.histogram(a, bins=50)
    width = 0.7 * (bins[1] - bins[0])
    center = (bins[:-1] + bins[1:]) / 2
    plt.bar(center, hist, align='center', width=width)
    plt.show()    
#makeHistoFinishingMono(dfFin)
#makeHistoFinishingMono(dfMulti)
#makeHistoFinishingMono(dfFin_part)

def filterOnlyMatchingProblemsOfFinishing(dfOrig, df):
    print '################',len(df.columns)
    print df.columns
    print '################',len(dfOrig.columns)
    print dfOrig.columns
    print len(dfOrig.index), len(dfOrig.columns)
    res = getProblemsUnderTime(dfOrig, 600000)
    print len(res.index), len(res.columns)
    res = res.loc[:,[u'infile_path', u'infile', u'outfile', u'method', u'var', u'csq', u'dist', u'numagent']]
    print len(res.index), len(res.columns)

    print df.columns
    r = df[df[[u'infile_path', u'infile', u'method', u'var', u'dist', u'numagent']].isin(res)]
    print '111111111111111',len(r.index)    
    print r.head(20)
    print r.index   
    
    df['endless10'] = 0
    
    print df['method'].str.contains("Token")
    print df[[u'infile_path', u'infile', u'method', u'var', u'dist', u'numagent']].isin(res)
  #  df.loc[df['method'].str.contains("Token"),'method']= 'DICF-PB-Token'
  #  df.loc[df[[u'infile_path', u'infile', u'method', u'var', u'dist', u'numagent']].isin(res),'endless10']= 1
    
    print len(df[df['endless10']==1])
    
    print len(df[df['endless10']==0])
    
def aggregate(df):
    df['agg'] = df['infile_path']+ df['infile'] + df['var'] +df['dist']
def otherFilter(dfOrig, df) :
    aggregate(df)
    aggregate(dfOrig)
    print df['method'].str.contains("Token")
    df['endless'] = 0
    df.loc[df['agg'].isin(dfOrig['agg']), 'endless'] = 1
  
    print len(df.index)
    print len(df[df['endless']==1])
    print len(df[df['endless']==0])
       
   
#filterOnlyMatchingProblemsOfFinishing(dfFin, dfMulti)
otherFilter(dfFin, dfMulti)

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
'''
print df.describe()
print 'number of rows',len(df.index)
print 'number of column',len(df.columns.values)
print df.columns.values
'''

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
