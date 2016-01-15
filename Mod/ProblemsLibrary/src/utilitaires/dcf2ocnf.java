package utilitaires;

import problemDistribution.DCFProblemOrderedCNFSaver;

public class dcf2ocnf {

	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    dcf2ocnf filename.dcf [output.o.cnf]");
		System.out.println("'filename.dcfl'  is the file of the program whose graph should be generated.");
		System.out.println("'output.o.cnf'  is the output file, by default, same name as filename with .o.cnf extension.");
				
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Interpret command line
		if (args.length<=0){
			printHelp();
			return;
		}
		int i=0;
		boolean replace=true;
			// Options
		while (args[i].startsWith("-")) { i++; }
		if (args.length<=i){
			printHelp();
			return;
		}
		
		String problemFilename=args[i].trim();
		if (problemFilename.endsWith(".dcf"))
			problemFilename=problemFilename.substring(0,problemFilename.length()-4);
			
		String outputFilename=problemFilename;
		if (args.length>i+1)
			outputFilename=args[i+1].trim();
		if (outputFilename.endsWith(".o.cnf"))
			outputFilename=outputFilename.substring(0,outputFilename.length()-6);
		if (outputFilename.endsWith(".cnf"))
			outputFilename=outputFilename.substring(0,outputFilename.length()-4);
		
		// MAIN
		try {
			// load problem.sol and convert it to dcf problem
			DCFProblemOrderedCNFSaver pb=new DCFProblemOrderedCNFSaver(problemFilename);
			System.out.println("File loaded, Now saving...");
			pb.save(outputFilename, replace);
			System.out.println("Output saved as "+outputFilename+".o.cnf");
		} catch (Exception e) {
			System.err.println("Error.");
			//e.printStackTrace();
		}

	}
	
}
