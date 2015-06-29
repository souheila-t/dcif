package utilitaires;

import convert.BioNetProblem;
import distrib.BioDistributedProblem;

public class solBNdist {

	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    solNBdist [options] filename.sol [filename2.dcf]");
		System.out.println("Options");
		System.out.println("-replace  replace the produced .dcf if it already exists.");	
	}

	
	
	public static void exec(final String pbFilename, final String outputFilename, final boolean replace){
		String problemFilename=pbFilename.trim();
		if (problemFilename.endsWith(".sol"))
			problemFilename=problemFilename.substring(0,problemFilename.length()-4);
		String output;
		if (outputFilename==null | outputFilename.length()==0)
			output=problemFilename;
		output=outputFilename.trim();
		if (output.endsWith(".dcf"))
			output=output.substring(0,output.length()-4);
		// load problem.p and convert it to sol problem
		BioDistributedProblem pb=new BioDistributedProblem();
		// save output 
		try {
			System.out.println("Loading and parsing ...");
			pb.loadFromDistributor(problemFilename);
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
