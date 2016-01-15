package utilitaires;

import answerSetProgramming.ASPCaller;
import problemDistribution.DCFProblem;
import solarInterface.SolProblem;

public class sol2dcf {
	
	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    sol2dcf -nbAg=N [Options] filename.sol [filename2.dcf]");
		System.out.println("-nbAg=N  indicate the number of agents of output distributed CFP.");
		System.out.println("'filename.sol'  is the file of the program that should be distributed.");
		System.out.println("Options");
		System.out.println("-replace  replace the produced .sol if it already exists.");	
		System.out.println("-dist=filename3  use the ASP base distribution program 'filename3.asp' " +
						"instead of the default on 'cut-distribute.asp'.");
		System.out.println("-asp=name  choose the ASP solver (CLASP or DLV). " +
		"default is CLASP");
		System.out.println("-minProp=R indicates the minimal proportion of the clauses " +
				"given to each agents (between 0 and 1/nbAg). default is 2/3nbAg");
		System.out.println("-maxProp=R indicates the maximal proportion of the clauses " +
		"given to each agents (between 1/nbAg and 1). default is 3/2nbAg (max 1)");

				
		
	}

	public static void main(String[] args) {
		
		// Interpret command line
		if (args.length<=0){
			printHelp();
			return;
		}
		int i=0;
		String progASP="cut-distribute";
		int solvASP=ASPCaller.ASP_CLASP;
		boolean replace=false;
		int nbAg=-1;
		double propMin=-1.0;
		double propMax=-1.0;
			// Options
		while (args[i].startsWith("-")) {
			if (args[i].startsWith("-dist=")){
				progASP=args[i].substring(args[i].indexOf("=")+1).trim();
				if (progASP.endsWith(".asp"))
					progASP=progASP.substring(0,progASP.length()-4);
				
				i++;
				continue;
			}
			if (args[i].startsWith("-asp=")){
				String temp=args[i].substring(args[i].indexOf("=")+1).trim();
				if (temp.equalsIgnoreCase("CLASP"))
					solvASP=ASPCaller.ASP_CLASP;
				else if (temp.equalsIgnoreCase("DLV"))
					solvASP=ASPCaller.ASP_DLV;
				i++;
				continue;
			}
			if (args[i].startsWith("-nbAg=")){
				nbAg=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].startsWith("-minProp=")){
				propMin=Double.parseDouble(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].startsWith("-maxProp=")){
				propMax=Double.parseDouble(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].trim().equals("-replace")){
				replace=true;
				i++;
				continue;
			}
			else{
				printHelp();
				return;
			}
		}
			//checks and adjustments
		if (nbAg<0) {
			printHelp();
			return;
		}
		if (propMax<0) propMax=Math.min(3.0/(2.0*nbAg), 1.0);
		if (propMin<0) propMin=(2.0/(3.0*nbAg));
			//filenames
		String problemFilename=args[i].trim();
		if (problemFilename.endsWith(".sol"))
			problemFilename=problemFilename.substring(0,problemFilename.length()-4);
		String outputFilename=problemFilename;
		if (args.length>i+1)
			outputFilename=args[i+1].trim();
		if (outputFilename.endsWith(".dcf"))
			outputFilename=outputFilename.substring(0,outputFilename.length()-4);
		
		// MAIN
		try {
			// load problem.sol and convert it to dcf problem
			SolProblem input=new SolProblem(problemFilename);
			System.out.println("File loaded, Now distributing...");
			DCFProblem output=new DCFProblem();
			output.generateFrom(input, progASP, solvASP,nbAg, propMin, propMax);
			// save output			
			output.save(outputFilename,replace);
		} catch (Exception e) {
			System.err.println("Error while saving "+outputFilename+".dcf, " +
					"file already exists and option -replace not used.");
			//e.printStackTrace();
		}
	}
}


