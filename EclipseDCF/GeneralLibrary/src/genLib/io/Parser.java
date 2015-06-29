/**
 * 
 */
package genLib.io;

import java.io.BufferedReader;
import java.io.IOException;



/**
 * @author Gauvain Bourgne
 *
 */
public interface Parser {
	public void parse(BufferedReader input) throws IOException;
}
