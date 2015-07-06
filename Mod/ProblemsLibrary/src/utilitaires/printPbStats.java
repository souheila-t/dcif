package utilitaires;



import cnfPb.SolProblemWithHeader;

import solarInterface.SolProblem;


public class printPbStats {
	public static void printHelp(){
		System.out.println("Usage :");
		System.out.println("    printPbStats filename.sol");
		System.out.println("Options");
		System.out.println("-h print this help.");	
	}
	
	
	public static void exec(String problemFilename){
		
		String filename=problemFilename.trim();
		if (filename.endsWith(".sol"))
			filename=filename.substring(0,filename.length()-4);
		
		try {
			SolProblemWithHeader sourcePb=new SolProblemWithHeader(new SolProblem(filename));
			sourcePb.printHeader(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i=0;
		boolean verbose=true;
		while (i<args.length && args[i].startsWith("-")) {
			if (args[i].trim().equals("-verbose")){
				verbose=true;
				i++;
				continue;
			}
			else{
				printHelp();
				return;
			}
		}
		if (args.length<=i){
			printHelp();
			return;			
		}
		String problemFilename=args[i].trim();
		
		exec(problemFilename);
	}

}
