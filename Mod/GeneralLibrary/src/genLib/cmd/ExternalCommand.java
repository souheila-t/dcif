/**
 * 
 */
package genLib.cmd;

import genLib.io.TransferToOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Gauvain Bourgne
 *
 */
public class ExternalCommand {

	public static BufferedReader execute(String command) {
		 
        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
            //Process pr = rt.exec("c:\\helloworld.exe");
            Process pr = rt.exec(command);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

//            String line=null;
//            while((line=input.readLine()) != null) {
//                System.out.println(line);
//            }
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
	
	public static void main(String[] args){
		
		BufferedReader input =ExternalCommand.execute("/tmp/testCmd.sh");
		TransferToOutput console=new TransferToOutput(System.out);
		try {
			console.parse(input);
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
