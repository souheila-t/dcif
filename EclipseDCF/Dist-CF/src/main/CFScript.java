/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import stats.ExpeSummary;

import genLib.io.LoaderTool;
import genLib.io.Parser;
import genLib.io.Saver;
import genLib.tools.Arguments;

/**
 * @author Gauvain Bourgne
 *
 */
public class CFScript implements Saver, Parser{

	
	public CFScript(String filenameNoExt) throws Exception{
		super();
		load(filenameNoExt);
	}

	
	public void load(String filename) throws Exception{
		LoaderTool.load(filename, ".scp", this);
	 }

	public void save(String filename, boolean replace) throws Exception{
		LoaderTool.save(filename, ".scp", this, replace);
	}
	
	
	/* (non-Javadoc)
	 * @see io.Parser#parse(java.io.BufferedReader)
	 */
	public void parse(BufferedReader bIn) throws IOException {
		String line=LoaderTool.getNextLine(bIn, '%');
		while (line!=null){
			if (line.startsWith("groupBy"))
				groupByPb=line.trim().equals("groupBy(Problem).");
			if (line.startsWith("method"))
				parseMethod(line);
			if (line.startsWith("problem"))
				parseProblem(line);
			line=LoaderTool.getNextLine(bIn, '%');
		}
	}

	
	private void parseMethod(String line){
		String meth=line.substring("method(".length(),line.lastIndexOf(")."));
		if (!methods.contains(meth))
			methods.add(meth);
	}
	
	private void parseProblem(String line) throws IOException{
		String pbBase, variantSuffix, distSuffix;
		//extract arguments
		String temp=line.substring(line.indexOf("(")+1, line.lastIndexOf(")."));
		Arguments arg=Arguments.parse("["+temp+"]");
		//attribute arguments to correct string
		pbBase=arg.get(0).trim();
		variantSuffix=arg.get(1).trim();
		distSuffix=arg.get(2).trim();
		
		pbRad.add(pbBase);
		varSuf.add(variantSuffix);
		distSuf.add(distSuffix);
	}

	/* (non-Javadoc)
	 * @see io.Saver#save(java.io.PrintStream)
	 */
	public void save(PrintStream output) {
		if (groupByPb)
			output.println("groupBy(Problem).");
		else
			output.println("groupBy(Method).");
		output.println();
		output.println("%Method list");
		for (String meth:methods)
			output.println("method("+meth+").");
		output.println();
		output.println("%Problem list");
		for (int i=0;i<pbRad.size();i++)
			output.println("problem("+pbRad.get(i)+","+varSuf.get(i)+","+distSuf.get(i)+").");
	}
	

	public List<ExpeSummary> run() throws Exception{
		List<ExpeSummary> result=new ArrayList<ExpeSummary>();
		if (groupByPb)
			for (int p=0; p<pbRad.size();p++)
				for(int m=0; m<methods.size();m++)
					result.add(run(m,p));
		else
			for(int m=0; m<methods.size();m++)
				for (int p=0; p<pbRad.size();p++)
					result.add(run(m,p));
		return result;
	}
	
	private ExpeSummary run(int method, int problem) throws Exception{
		Thread.sleep(500);
		String methCode=methods.get(method);
		String pbBase=pbRad.get(problem);
		String varSuffix=varSuf.get(problem);
		String distSuffix=distSuf.get(problem);
		System.out.println();
		System.out.println("*********************************************************************");
		System.out.println("       RUNNING "+pbBase+varSuffix+distSuffix+" with "+methCode);
		
		System.out.println("*********************************************************************");

		return CFLauncher.runExpe(methCode, pbBase, varSuffix, distSuffix, timeLimitMillis);
	}
	
	public List<String> methods=new ArrayList<String>();
	public List<String> pbRad=new ArrayList<String>();
	public List<String> varSuf=new ArrayList<String>();
	public List<String> distSuf=new ArrayList<String>();
	public boolean groupByPb=true;
	public static long timeLimitMillis=600000;  //900000; // max 15 minutes
	
}
