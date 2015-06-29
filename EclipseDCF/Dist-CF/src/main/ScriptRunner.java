/**
 * 
 */
package main;

import genLib.io.LoaderTool;
import genLib.io.Saver;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import agLib.agentCommunicationSystem.CanalComm;

import solarInterface.CFSolver;
import stats.ExpeSummary;
import systemStructure.Tree;

/**
 * @author Gauvain Bourgne
 *
 */
public class ScriptRunner implements Saver{
	//run script & fill result
	public void exec(String scriptFilename, String outputfilename, boolean replace) throws Exception{
		CFScript pb=new CFScript(scriptFilename);
		result=pb.run();
		writeToCsv(scriptFilename,replace);
	}
	
	//write to csv
	public void writeToCsv(String filename, boolean replace) throws Exception{
		LoaderTool.save(filename, ".csv", this, replace);
	}
	
	/* (non-Javadoc)
	 * @see io.Saver#save(java.io.PrintStream)
	 */
	public void save(PrintStream output) {
		output.println(ExpeSummary.labels());
		for (ExpeSummary res:result)
			output.println(res.toLine());
	}
	
	
	public static void printHelp(){
		System.out.println("Runs the problems and experiments in file script.scp and save result as output.csv");
		System.out.println("Usage :");
		System.out.println("    cnfsat2sol [Options] script.scp [output.csv]");
		System.out.println("Options");
		System.out.println("-replace  replace the produced .sol if it already exists.");	
		System.out.println("-t=L  set TimeLimit in milliseconds for each pb.");	
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int i=0;
		boolean replace=false;
		while (args[i].startsWith("-")) {
			if (args[i].trim().equals("-replace")){
				replace=true;
				i++;
				continue;
			}
			if (args[i].trim().startsWith("-t=")){
				String time=args[i].substring(args[i].indexOf('=')+1);
				CFScript.timeLimitMillis=Long.parseLong(time);
				i++;
				continue;
			}
			
			else{
				printHelp();
				return;
			}
		}
		
		CFSolver.verbose=false;
		Tree.verbose=false;
		CanalComm.verbose=false;
		
		String scriptFilename=args[i].trim();
		if (scriptFilename.endsWith(".sol"))
			scriptFilename=scriptFilename.substring(0,scriptFilename.length()-4);
		
		String outputFilename=scriptFilename;
		if (args.length>i+1)
			outputFilename=args[i+1].trim();
		if (outputFilename.endsWith(".csv"))
			outputFilename=outputFilename.substring(0,outputFilename.length()-4);
		ScriptRunner runner=new ScriptRunner();
		runner.exec(scriptFilename, outputFilename, replace);
	}


	
	public List<ExpeSummary> result=new ArrayList<ExpeSummary>();


}
