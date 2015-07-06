/**
 * 
 */
package answerSetProgramming;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Options;
import org.nabelab.solar.parser.ParseException;


/**
 * @author Gauvain Bourgne
 *
 */
public class ASPCaller {

	public static final String DLV_PATH="/Users/ki/bin/";
	public static final String CLASP_PATH="/usr/local/bin/";
	public static final int ASP_DLV=0;
	public static final int ASP_CLASP=1;
	
	public static BufferedReader callToDLV(File program, String pfilter, String filter, int nbAS) {
		 
        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
            //Process pr = rt.exec("c:\\helloworld.exe");
            String options=" -silent";
            if (pfilter!=null && !pfilter.equals(""))
            	options+=" -pfilter="+pfilter;
            if (filter!=null && !filter.equals(""))
            	options+=" -filter="+filter;
            if (nbAS>0)
            	options+=" -n="+nbAS;
            	
            String command=DLV_PATH+"dlv"+options+" "+program.getAbsolutePath();
            System.out.println("Executing "+command+" ...");
            Process pr = rt.exec(command);         
            
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            int exitCode=pr.waitFor();
            if (exitCode!=0)
            	throw new Exception("Error "+exitCode+" in execution of "+command);
            return input;

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return null;
    }
	
	public static List<AnswerSet> readASP_DLV(BufferedReader data, boolean bestModels) throws IOException{
		return readASP_DLV(new Env(), data, bestModels);
	}
	
	public static List<AnswerSet> readASP_DLV(Env env, BufferedReader data, boolean bestModels) throws IOException{
		return readASP_DLV( env, new Options(env),  data,  bestModels);
	}
	
	public static List<AnswerSet> readASP_DLV(Env env, Options opt, BufferedReader data, boolean bestModels) throws IOException{
		List<AnswerSet> stableModels=new ArrayList<AnswerSet>();
		String line = data.readLine();
	    String lineWeight;
		while ( line != null ){  // CR ||commentaire
			if (bestModels && line.startsWith("Best model: ")){
				lineWeight = data.readLine();
				stableModels.add(new WeightedAnswerSet(line,lineWeight));
			}
			else{
				try {
					stableModels.add(new AnswerSet(env, opt, line));
				} catch (ParseException e) {
					throw new IOException(e);
				}
			}
			line = data.readLine();
		}
		return stableModels;
	}
	

	public static BufferedReader callToCLASP(File program, int nbAS, boolean bestModels) {
		 
        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
            //Process pr = rt.exec("c:\\helloworld.exe");
            String options=" --verbose=0";
            if (bestModels)
            	options+=" --quiet=1,2";
            if (nbAS>0)
            	options+=" -n "+nbAS;
            	
            String command=CLASP_PATH+"gringo "+program.getAbsolutePath()+" | "+CLASP_PATH+"clasp "+options;
            System.out.println("Executing "+command+" ...");
            Process pr = rt.exec(command);         
            
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            int exitCode=pr.waitFor();
            if (exitCode!=0)
            	throw new Exception("Error "+exitCode+" in execution of "+command);
            return input;

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return null;
    }
	
	public static List<AnswerSet> readCLASP(BufferedReader data, boolean bestModels) throws IOException{
		return readCLASP(new Env(),  data,  bestModels);
	}
	
	public static List<AnswerSet> readCLASP(Env env, BufferedReader data, boolean bestModels) throws IOException{
		return readCLASP( env, new Options(env),  data,  bestModels);
	}
	
	public static List<AnswerSet> readCLASP(Env env, Options opt, BufferedReader data, boolean bestModels) throws IOException{
		List<AnswerSet> stableModels=new ArrayList<AnswerSet>();
		String line = data.readLine();
	    String lineWeight;
		while ( line != null && !claspResultLine(line)){  // CR ||commentaire
			if (bestModels && line.startsWith("Best model: ")){
				lineWeight = data.readLine();
				stableModels.add(new WeightedAnswerSet(line,lineWeight));
			} else{
				try {
					stableModels.add(new AnswerSet(env, opt, line));
				} catch (ParseException e) {
					throw new IOException(e);
				}
			}
			line = data.readLine();
		}
		return stableModels;
	}
	
	private static boolean claspResultLine(String s){
		if (s.startsWith("SATISFIABLE") || s.startsWith("OPTIMUM FOUND") || s.startsWith("UNSATISFIABLE"))
			return true;
		return false;
	}
	
	
	
	public static List<AnswerSet> solve(File program, int solver, String pfilter, String filter, int nbAS, 
			boolean bestModels) {
		BufferedReader data;
		switch(solver){
		case ASP_DLV:
			data=callToDLV(program,pfilter, filter, nbAS);
			try {
				return readASP_DLV(data,bestModels);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		case ASP_CLASP:
			data=callToCLASP(program, nbAS, bestModels);
			try {
				return readCLASP(data,bestModels);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
		

}
