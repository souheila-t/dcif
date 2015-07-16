package stats;

import genLib.io.LoaderTool;
import genLib.io.Saver;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
<<<<<<< HEAD
import java.util.Map.Entry;
=======
>>>>>>> 68c4466bc683c56cc55590c40150b9574e388b3e

public class OutputCompHolder implements Saver{
	HashMap<String, Integer> mapRes = new HashMap<String, Integer>();
	
	public OutputCompHolder() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void addEntry(String s, int i ){
		mapRes.put(s,i);
	}
	public void save(String filename, boolean replace) throws Exception{
		System.out.println(filename);
		LoaderTool.save(filename, "_csqstats"+".csv", this, replace);
	}

	@Override
	public void save(PrintStream p){
<<<<<<< HEAD
		Iterator<Entry<String, Integer>> it = mapRes.entrySet().iterator();
=======
		Iterator it = mapRes.entrySet().iterator();
>>>>>>> 68c4466bc683c56cc55590c40150b9574e388b3e
		String header ="";
		String values = "";
		String separator = ";";
		
	    while (it.hasNext()) {
<<<<<<< HEAD
	        Map.Entry<String, Integer> pair = (Map.Entry)it.next();
	   //     p.println(pair.getKey() + " = " + pair.getValue());
	        header += pair.getKey()+separator;
	        values += Integer.toString(pair.getValue())+separator;
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    
	    
	    //removing last comma
		header = header.substring(0,header.length()-1);
	    values = values.substring(0,values.length()-1);

		System.out.println(header);
		System.out.println(values);

=======
	        Map.Entry pair = (Map.Entry)it.next();
	   //     p.println(pair.getKey() + " = " + pair.getValue());
	        header += pair.getKey()+separator;
	        values += pair.getValue()+separator;
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    //removing last comma
	    header.substring(0,header.length()-1);
	    values.substring(0,header.length()-1);
	    
>>>>>>> 68c4466bc683c56cc55590c40150b9574e388b3e
	    p.println(header);
		p.println(values);
	}
}