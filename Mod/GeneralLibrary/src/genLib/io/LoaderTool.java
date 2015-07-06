/**
 * 
 */
package genLib.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

/**
 * @author Bourgne Gauvain
 *
 */
public class LoaderTool{
	
	public static void load(String filename,String extension, Parser parser) throws Exception{
		
		File accesFichier = new File(filename+extension) ;  	
	 	if (!accesFichier.exists())
	 		throw new Exception("File " + accesFichier.getAbsolutePath() + 
	 				          " does not exist !") ;
		Reader fIn         ; // flux de sortie
		BufferedReader bIn ; // buffer associe
		try {	// ouverture des flux	
				fIn   = new FileReader(accesFichier)  ;
				bIn   = new BufferedReader(fIn)       ;
				//parse
//				name=filename;
				parser.parse(bIn);
				// ferme les flux
				bIn.close() ;
				fIn.close() ;				
			} // fin try
			catch(IOException e){
				System.err.println("Input/Output problem:\n"+e) ; throw new Exception (e) ;}  
			catch(Exception e){
				System.err.println("Problem in file " + filename+":\n"+e);throw new Exception(e);} 
			finally{
				bIn = null ;
				fIn = null ;
			} // end try-catch-finally
	 }


	public static void loadInOutput(String filename,String extension, PrintStream output) throws Exception{
		load(filename, extension, new TransferToOutput(output));
	}

 	/**
	 *  return first line which is neither a comment nor a empty line
 	 * @throws IOException 
	 */
	public static String getNextLine(BufferedReader bIn, char commentChar) throws IOException
	{
	    String line = bIn.readLine();
		while ( line != null &&	  		    // pas fin fichier
			    ( line.length()  == 0 || line.charAt(0) == commentChar )){  // CR ||commentaire
			line = bIn.readLine();
		}
		return line;
	}
	
	public static void save(String filename, String extension, Saver s, boolean replace) throws Exception{
		File accesFichier = new File(filename+extension) ;  	
	 	if (accesFichier.exists() && !replace)
	 		throw new Exception("File " + accesFichier.getAbsolutePath() + 
	 				          " already exists !") ;
	 	accesFichier.createNewFile();
	 	PrintStream fileOut = new PrintStream(new FileOutputStream(accesFichier));
		s.save(fileOut);
		fileOut.close();
	}
	
	public static void addToFile(String filename, String extension, Saver s) throws IOException {
		File accesFichier = new File(filename+extension) ;  	
	 	if (!accesFichier.exists())
	 		accesFichier.createNewFile();
	 	PrintStream fileOut = new PrintStream(new FileOutputStream(accesFichier));
		s.save(fileOut);
		fileOut.close();
	}

	
}
