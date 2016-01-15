package stats;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import logicLanguage.IndepClause;
import genLib.io.LoaderTool;
import genLib.io.Saver;

import org.nabelab.solar.Clause;

import solarInterface.SolProblem;


public class compCsq {
	/**
	 * Compare 2 fichiers consequences pour déterminer leurs mesures de similarités
	 * doit être exporté en .jar
	 * @param args
	 */
	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    compCsq filename.csq filename2.csq [output .csv]");
		System.out.println("compares the 2 csq files to produces stats regarding the experiences.\n"
				+ ", the files contain csq in the form of a .sol file, the ref is the 2 nd file.\n"
				);
	}

	private static String getShortFilename(String filename){
		File accesFichier = new File(filename) ;
		return accesFichier.getName();
	}
	private static String getParentFolder(String filename){

		File accesFichier = new File(filename) ;		
		Path p = Paths.get(accesFichier.getAbsolutePath());
		Path folder = p.getParent();
		
		return folder.toString()+"/";
	}
	public static void main(String[] args) {
		// Interpret command line
		if (args.length<=0){
			printHelp();
			return;
		}
		int i=0;
		//filenames
		String filename1=args[i].trim();
		if (filename1.endsWith(".csq"))
			filename1=filename1.substring(0,filename1.length()-4);
		i+=1;
		String filename2=args[i].trim();
		if (filename2.endsWith(".csq"))
			filename2=filename2.substring(0,filename2.length()-4);
		
		

		String outputFilename=getParentFolder(filename1)+compCsq.getShortFilename(filename1) + "_" +compCsq.getShortFilename(filename2) + ".csv";
		if (args.length>i+1)
			outputFilename=args[i+1].trim();
		if (outputFilename.endsWith(".csv"))

			outputFilename=outputFilename.substring(0,outputFilename.length()-4);

		// MAIN
		try {
			//C2 subsumes potentially c1 (if double reciproque = equality)

			System.out.println("here");
			//load consequences from files
			CsqHolder csqH1 = new CsqHolder(filename1);
			CsqHolder csqH2 = new CsqHolder(filename2);

			System.out.println("there");

			//nb de clauses de c1
			int nbCl_c1 = csqH1.getClauses().size();//
			//nb de clauses de c2
			int nbCl_c2 = csqH2.getClauses().size();//

			//c2 : solutions finales du MONO AGENT
			//nb de clauses de c1 totalement(exactement) présentes dans c2 (et inversement)
			int nbCl_c1_tot_present = 0;//
			//permet de mieux voir les redondances
			//nb de fois que des clauses de c2 subsument des clauses de c1
			int nbTimesCl_subs_non_min = 0;//
			//nb de fois que des clauses de c2 subsument des clauses de c1 (strict)
			int nbTimesCl_subs_min = 0;//


			//nb de clauses de c1 qui ne sont subsumées par aucune clause de c2
			int nbCl_c1_non_subsumed = 0;//
			//nb de clauses de c1 qui sont subsumées par des clauses de c2 (strict)
			int nbCl_c1_subsumed_min = 0;
			//nb de clauses de c1 qui sont subsumées par des clauses de c2 
			int nbCl_c1_subsumed_non_min = 0;


			//nb de clauses de c2 qui ne subsument aucune clause de c1
			int nbCl_c2_non_sub =0;
			//nb de clauses de c2 qui subsument au moins une clause de c1
			int nbCl_c2_sub_non_min =0;
			//nb de clauses de c2 qui subsument au moins une clause de c1 (strict)
			int nbCl_c2_sub_min =0;


			//normalement pas de doublons : clauses toutes différentes
			for(Clause c1: csqH1.getClauses()){
				boolean isSubsumed = false;
				boolean isEqual = false;
				for (Clause c2 : csqH2.getClauses()){

					if (c2.subsumes(c1)){
						isSubsumed = true;
						nbTimesCl_subs_non_min++;
						if (c1.subsumes(c2)){
							isEqual = true;
							nbCl_c1_tot_present++;
						}
						else
							nbTimesCl_subs_min++;
					}
				} 
				if (!isSubsumed)
					nbCl_c1_non_subsumed++;
				else {
					if (isEqual)
						nbCl_c1_subsumed_non_min++;
					else
						nbCl_c1_subsumed_min++;
				}
			}

			for(Clause c2: csqH2.getClauses()){
				boolean doSub = false;
				boolean isEqual = false;
				for (Clause c1 : csqH1.getClauses()){
					if (c2.subsumes(c1)){
						doSub = true;
						if (c1.subsumes(c2)){
							isEqual = true;
						}
					}
				} 
				if (!doSub)
					nbCl_c2_non_sub++;
				else {
					if (isEqual)
						nbCl_c2_sub_non_min++;
					else
						nbCl_c2_sub_min++;
				}
			}
			//Rq on peut enlever le calcul de is equal mais permet de verifier l'integrité des operations
			//remplacer ca par autre chose de moins laid
			System.out.println(
					"//nb de clauses de c1\n"+
							nbCl_c1 +"\n"+

					"//nb de clauses de c2\n"+
					nbCl_c2 +"\n"+

					"//c2 : solutions finales du MONO AGENT\n"+
					"//nb de clauses de c1 totalement(exactement) présentes dans c2 (et inversement)\n"+
					nbCl_c1_tot_present +"\n"+


					"//permet de mieux voir les redondances\n"+
					"//nb de fois que des clauses de c2 subsument des clauses de c1\n"+
					nbTimesCl_subs_non_min +"\n"+

					"//nb de fois que des clauses de c2 subsument des clauses de c1 (strict)\n"+
					nbTimesCl_subs_min +"\n"+



					"//nb de clauses de c1 qui ne sont subsumées par aucune clause de c2\n"+
					nbCl_c1_non_subsumed+"\n"+

					"//nb de clauses de c1 qui sont subsumées par des clauses de c2 (strict)\n"+
					nbCl_c1_subsumed_min +"\n"+

					"//nb de clauses de c1 qui sont subsumées par des clauses de c2 \n"+
					nbCl_c1_subsumed_non_min +"\n"+


					"//nb de clauses de c2 qui ne subsument aucune clause de c1\n"+
					nbCl_c2_non_sub +"\n"+

					"//nb de clauses de c2 qui subsument au moins une clause de c1\n"+
					nbCl_c2_sub_non_min +"\n"+

					"//nb de clauses de c2 qui subsument au moins une clause de c1(strict)\n"+
					nbCl_c2_sub_min +"\n");
			
			OutputCompHolder outputHolder= new OutputCompHolder();
			outputHolder.addEntry(
					"nbCl_c1",
					nbCl_c1 );
			outputHolder.addEntry(
					"nbCl_c2",
					nbCl_c2 );
			outputHolder.addEntry(
					"nbCl_c1_tot_present",
					nbCl_c1_tot_present);
			outputHolder.addEntry(
					"nbTimesCl_subs_non_min",
					nbTimesCl_subs_non_min);
			outputHolder.addEntry(
					"nbTimesCl_subs_min",
					nbTimesCl_subs_min);
			outputHolder.addEntry(
					"nbCl_c1_non_subsumed",
					nbCl_c1_non_subsumed);
			outputHolder.addEntry(
					"nbCl_c1_subsumed_min",
					nbCl_c1_subsumed_min);
			outputHolder.addEntry(
					"nbCl_c1_subsumed_non_min",
					nbCl_c1_subsumed_non_min);
			outputHolder.addEntry(
					"nbCl_c2_non_sub",
					nbCl_c2_non_sub);
			outputHolder.addEntry(
					"nbCl_c2_sub_non_min",
					nbCl_c2_sub_non_min);
			outputHolder.addEntry(
					"nbCl_c2_sub_min",
					nbCl_c2_sub_min);		
			
			outputHolder.save(outputFilename,true);
			// save output to CSV
		} catch (Exception e) {
			System.err.println("Error while saving");
			e.printStackTrace();
		}
	}


}



