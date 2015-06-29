package convert;
import io.LoaderTool;
import solarInterface.SolProblem;


public class BioNetProblem extends SolProblem{
	
	public BioNetProblem(){
		super();
	}
	
	public void loadFromNet(String filename) throws Exception{
		BioNetFileParser bnp=new BioNetFileParser();
		LoaderTool.load(filename, ".net", bnp);
		System.out.println("rule generation finished, now converting...");
		int i=0;
		for (String line:bnp.getSolFileLines()){
			parseSolFileLine(line);
			i++;
			if (i%10==0)
				System.out.print(".");
			if (i%100==0)
				System.out.println();
		}
	 }
	
	public void loadFromRN(String filename) throws Exception{
		BioNetReactionNodeParser bnp=new BioNetReactionNodeParser();
		LoaderTool.load(filename, ".rn", bnp);
		System.out.println("rule generation finished, now converting...");
		int i=0;
		for (String line:bnp.getSolFileLines()){
			parseSolFileLine(line);
			i++;
			if (i%10==0)
				System.out.print(".");
			if (i%100==0)
				System.out.println();
		}
	 }
	
	
	public static void main(String[] args) {
		System.out.println("test");
		BioNetProblem pb= new BioNetProblem();
	}

}
