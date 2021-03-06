package old_utilitaires;

import old_cnfPb.CnfReader;
import old_cnfPb.PFieldGenerator;
import old_cnfPb.VariantProblem;
import old_solarInterface.IndepPField;
import old_solarInterface.SolProblem;

public class makeSolVariant {
	public static void printHelp(){
		System.out.println("This tools make a variant for a sol problem");
		System.out.println("Usage :");
		System.out.println("    makeSolVariant filename.sol [filename2.var]");
		System.out.println("Options");
		System.out.println("-replace  replace the produced .var if it already exists.");
		System.out.println("-method=xxx  use variant xxx.");
		System.out.println("          xxx=min-N / max-N / all");
		System.out.println("-len=N  set length limit.");
		System.out.println("-d=N  set depth limit.");
		
	}
	
	public static void exec(final String pbFilename, final String outputFilename, final String method, final int length, final int depth, final boolean replace){
		// load CNF and save output 
		try {
			SolProblem input=new SolProblem(pbFilename);
			PFieldGenerator pf= new PFieldGenerator(input);
			IndepPField varPf;
			if (method.startsWith("max") || method.startsWith("min")){
				boolean mostRare=method.startsWith("min");
				String prop=method.substring(method.indexOf('-')+1);
				double proportion=Integer.parseInt(prop)/100.;
				varPf=pf.setFreqPField(mostRare, proportion);
			}
			else {
				varPf=pf.setGlobalPField();
			}
			varPf.setMaxLength(length);
			String name=method+"_ld"+length+"-"+depth;
			VariantProblem output = new VariantProblem(pbFilename,varPf, depth, name);
			output.save(outputFilename, replace);
		} catch (Exception e) {
			System.err.println("Error.");
			//e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i=0;
		boolean replace=false;
		String method="all";
		int length=-1;
		int depth=-1;
		while (args[i].startsWith("-")) {
			if (args[i].startsWith("-method=")){
				method=args[i].substring(args[i].indexOf("=")+1).trim();
				i++;
				continue;
			}
			if (args[i].trim().equals("-replace")){
				replace=true;
				i++;
				continue;
			}
			if (args[i].startsWith("-d=")){
				depth=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			if (args[i].startsWith("-len=")){
				length=Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
				i++;
				continue;
			}
			else{
				printHelp();
				return;
			}
		}
		String problemFilename=args[i].trim();
		if (problemFilename.endsWith(".sol"))
			problemFilename=problemFilename.substring(0,problemFilename.length()-4);
		String outputFilename=problemFilename+"_"+method+"_ld"+length+"-"+depth;
		if (args.length>i+1){
			outputFilename=args[i+1].trim();
			if (outputFilename.endsWith(".var"))
				outputFilename=outputFilename.substring(0,outputFilename.length()-4);
			
		}			
		outputFilename=outputFilename.trim();
		exec(problemFilename, outputFilename, method, length, depth, replace);
	}

}
