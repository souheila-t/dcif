package stats;

import genLib.io.LoaderTool;
import genLib.io.Saver;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
		Iterator it = mapRes.entrySet().iterator();
		String header ="";
		String values = "";
		String separator = ";";
		
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	   //     p.println(pair.getKey() + " = " + pair.getValue());
	        header += pair.getKey()+separator;
	        values += pair.getValue()+separator;
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    //removing last comma
	    header.substring(0,header.length()-1);
	    values.substring(0,header.length()-1);
	    
	    p.println(header);
		p.println(values);
	}
}