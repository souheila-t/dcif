package utilitaires;

import convert.BioNetProblem;

public class rnConvert {

	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    rnConvert [options] filename.rn [filename2.sol]");
		System.out.println("Options");
		System.out.println("-replace  replace the produced .sol if it already exists.");	
	}

	
	
	public static void exec(final String pbFilename, final String outputFilename, final boolean replace){
		String problemFilename=pbFilename.trim();
		if (problemFilename.endsWith(".rn"))
			problemFilename=problemFilename.substring(0,problemFilename.length()-3);
		String output;
		if (outputFilename==null | outputFilename.length()==0)
			output=problemFilename;
		output=outputFilename.trim();
		if (output.endsWith(".sol"))
			output=output.substring(0,output.length()-4);
		// load problem.p and convert it to sol problem
		BioNetProblem pb=new BioNetProblem();
		// save output 
		try {
			System.out.println("Loading and parsing ...");
			pb.loadFromRN(problemFilename);
			System.out.println("Saving ...");
			pb.save(output,replace);
			System.out.println("Finished.");
		} catch (Exception e) {
			System.err.println("Error while saving "+outputFilename+".sol, " +
					"file already exists and option -replace not used.");
			//e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i=0;
		boolean replace=false;
		while (args[i].startsWith("-")) {
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
		
		String problemFilename=args[i].trim();
		String outputFilename=null;
		if (args.length>i+1)
			outputFilename=args[i+1].trim();
		
		exec(problemFilename, outputFilename, replace);

	}

}
