# -*- coding: utf-8 -*-
#https://docs.python.org/3/library/unittest.html
#https://web.archive.org/web/20150315073817/http://www.xprogramming.com/testfram.htm
import unittest
from partitioning import *

TEST_PATH ='tests/partitioning/'
class TestStringMethods(unittest.TestCase):
    def setUp(self):
        self.sol_filename = 'glucolysis' 
        self.dcf_filename = 'glucolysis_kmet4'
        self.sol_unvalid_filename ='glucolysis_withoutTP'
        self.f1 = FileSol(TEST_PATH+fSol)
        self.f1.load()
        
    def test_load_save(self):
        f1 = FileSol(TEST_PATH+fSol)
        f1.load()
        
        temp_outfilename = 'tempSol'
        f1.save(filename = TEST_PATH+temp_outfilename)
        f2 = FileSol(TEST_PATH+temp_outfilename)
        f2.load()
        for line1,line2 in zip(f1.lines, f2.lines):
            self.assertEqual(line1.data,line2.data)
        
    def test_validity_sol(self):
        self.assertTrue(self.f1.is_valid())
        self.f1.reset_all_to_axioms()
        self.assertFalse(self.f1.is_valid())
       # self.f1.save(TEST_PATH+'TESTS')
    def test_TPdistribution1(self):
        res=self.f1.create_a_FileSol_wit_a_TP_distribution_naiveShort(10)
        res.save()
    def test_Agdistribution1(self):

        resNaive = self.f1.create_dcf_Agent_distribution(2,method = 'naive_eq')
        resNaive.save()
        resIndent = self.f1.create_dcf_Agent_distribution(2,method = 'naive_indent')
        resIndent.save()
    '''
    def test_split(self):      
        self.assertEqual(s.split(), ['hello', 'world'])
        # check that s.split fails when the separator is not a string
        with self.assertRaises(TypeError):
    '''
    def test_dcf_valid(self):
        file_sol_unvalid = FileSol(TEST_PATH+self.sol_unvalid_filename)
        file_sol_unvalid.load()
        file_dcf_unvalid = file_sol_unvalid.create_dcf_Agent_distribution(2)     
        file_dcf_unvalid.load()
        
        self.assertFalse( file_dcf_unvalid.isValid())
        file_dcf = FileDCF(TEST_PATH+self.dcf_filename)
        file_dcf.load()
        
        self.assertTrue(file_dcf.isValid())
    def test_dcf_to_sol(self):
        file_sol_unvalid = FileSol(TEST_PATH+self.sol_unvalid_filename)
        file_sol_unvalid.load()
        file_dcf_unvalid = file_sol_unvalid.create_dcf_Agent_distribution(2)     
        file_dcf_unvalid.save()
        
        file_dcf = FileDCF(TEST_PATH+file_dcf)
        file_dcf.load()
        
        solres=file_dcf.toSol()
        solres.save(TEST_PATH+'test1tosol')
        solres = file_dcf_unvalid.toSol() 
        solres.save(TEST_PATH+'test2tosol')
        
    def test_TPdistributionEachAgent(self):
        
        file_dcf = FileDCF(TEST_PATH+file_dcf)
        file_dcf.load()        
        solres = file_dcf.create_a_FileSol_wit_a_TPdistribution_for_each_agent(20)
        solres.save()        
        
if __name__ == '__main__':
    unittest.main()
    