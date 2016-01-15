package old_cnfPb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import genLib.io.LoaderTool;
import genLib.io.Parser;
import genLib.io.Saver;
import old_solarInterface.IndepPField;
import old_solarInterface.SolProblem;

public class VariantProblem implements Saver, Parser{

	public VariantProblem(String solFilename,IndepPField varPf, int depthLim, String name){
		varName=name;
		this.solFilename=solFilename;
		variantPField=varPf;
		depthLimit=depthLim;
	}
	
	public VariantProblem(String variantFilenameNoExt) throws Exception{
		super();
		load(variantFilenameNoExt);
	}
	
	public void load(String filename) throws Exception{
		LoaderTool.load(filename, ".var", this);
		initialPb=new SolProblem(solFilename);
	 }	
	
	public void parse(BufferedReader bIn) throws IOException {
		String line=LoaderTool.getNextLine(bIn, '%');
		while (line!=null){
			parseFileLine(line);
			line=LoaderTool.getNextLine(bIn, '%');
		}
	}

	public void parseFileLine(String line){
		if (line.startsWith("solSource"))
			parseSolSource(line);
		if (line.startsWith("pf"))
			variantPField=IndepPField.parse(line);
		if (line.startsWith("depthLimit"))
			parseDepth(line);
	}

	public void parseSolSource(String line){
		solFilename=line.substring("solSource(".length(),line.lastIndexOf(")."));
	}
	
	public void parseDepth(String line){
		depthLimit=Integer.parseInt(line.substring("depthLimit(".length(),line.lastIndexOf(").")));
	}
	
	public void save(String filename, boolean replace) throws Exception{
		LoaderTool.save(filename, ".var", this, replace);
	}
	
	public void save(PrintStream p) {
		printHeader(p);
		p.println("solSource("+solFilename+").");
		p.println(variantPField.toSolFileLine());
		p.println("depthLimit("+depthLimit+").");
	}	
	
	public void printHeader(PrintStream p){
		p.println("%Sol source: "+solFilename);
		p.println("%Variant name: "+varName);
		p.println();		

	}
	
	public IndepPField getVariantPField() {
		return variantPField;
	}

	public SolProblem initialPb;
	public String solFilename;
	public String varName;
	public IndepPField variantPField;

	public int depthLimit;
}
