package stats;

import org.nabelab.solar.Clause;


public class compCsq {
	/**
	 * Compare 2 fichiers consequences pour déterminer leurs mesures de similarités
	 * doit être exporté en .jar
	 * @param args
	 */
	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    compCsq filename.csq filename2.csq [output.csqstats]");
		System.out.println("compares the 2 csq files to produces stats regarding the experiences.\n"
				+ ", the files contain csq in the form of a .sol file.\n"
				);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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

		String outputFilename=filename1 + "_" +filename2 + ".csqstats";
		if (args.length>i+1)
			outputFilename=args[i+1].trim();
		if (outputFilename.endsWith(".csqstats"))
			outputFilename=outputFilename.substring(0,outputFilename.length()-9);

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
			int nbCl_c1_non_sub = 0;//

			//nb de clauses de c1 qui sont subsumées par des clauses de c2 (strict)
			int nbCl_c1_sub_min = 0;

			//nb de clauses de c1 qui sont subsumées par des clauses de c2 
			int nbCl_c1_sub_non_min = 0;






			//nb de clauses de c2 qui ne subsument aucune clause de c1
			int nbCl_c2_non_sub =0;

			//nb de clauses de c2 qui subsument au moins une clause de c1
			int nbCl_c2_sub_non_min =0;

			//nb de clauses de c2 qui subsument au moins une clause de c1 (strict)
			int nbCl_c2_sub_min =0;




			//normalement pas de doublons : clauses toutes différentes
			for(Clause c1: csqH1.getClauses()){
				boolean isSub = false;
				boolean isEqual = false;
				for (Clause c2 : csqH2.getClauses()){

					if (c2.subsumes(c1)){
						isSub = true;
						nbTimesCl_subs_non_min++;
						if (c1.subsumes(c2)){
							isEqual = true;
							nbCl_c1_tot_present++;
						}
						else
							nbTimesCl_subs_min++;
					}
				} 
				if (!isSub)
					nbCl_c1_non_sub++;
				else {
					if (isEqual)
						nbCl_c1_sub_non_min++;
					else
						nbCl_c1_sub_min++;
				}
			}

			for(Clause c2: csqH2.getClauses()){
				boolean isSub = false;
				boolean isEqual = false;
				for (Clause c1 : csqH1.getClauses()){
					if (c2.subsumes(c1)){
						isSub = true;
						if (c1.subsumes(c2)){
							isEqual = true;
						}
					}
				} 
				if (!isSub)
					nbCl_c2_non_sub++;
				else {
					if (isEqual)
						nbCl_c2_sub_non_min++;
					else
						nbCl_c2_sub_min++;
				}
			}
			//Rq on peut enlever le calcul de is equal mais permet de verifier l'integrité des operations

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
					nbCl_c1_non_sub+"\n"+

					"//nb de clauses de c1 qui sont subsumées par des clauses de c2 (strict)\n"+
					nbCl_c1_sub_min +"\n"+

					"//nb de clauses de c1 qui sont subsumées par des clauses de c2 \n"+
					nbCl_c1_sub_non_min +"\n"+


					"//nb de clauses de c2 qui ne subsument aucune clause de c1\n"+
					nbCl_c2_non_sub +"\n"+

					"//nb de clauses de c2 qui subsument au moins une clause de c1\n"+
					nbCl_c2_sub_non_min +"\n"+

					"//nb de clauses de c2 qui subsument au moins une clause de c1(strict)\n"+
					nbCl_c2_sub_min +"\n"
					);

			// save output to CSV
			//output.save(outputFilename,replace);
		} catch (Exception e) {
			System.err.println("Error");
			//e.printStackTrace();
		}
	}


}
