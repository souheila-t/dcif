/**
 * 
 */
package genLib.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Gauvain Bourgne
 *
 */
public class TransferToOutput implements Parser{
	
	public TransferToOutput(PrintStream out){
		output=out;
	}
	
	public void parse(BufferedReader input) throws IOException {
		String line = input.readLine();
		while ( line != null ){
			output.println(line);
			line = input.readLine();
		}
	}

	PrintStream output;
}
