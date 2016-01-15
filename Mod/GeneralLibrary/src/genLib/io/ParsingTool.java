package genLib.io;
import genLib.tools.Arguments;

import java.util.ArrayList;
import java.util.List;

public class ParsingTool {
	public static List<String> getCols(String line, String sep){
		List<String> res=new ArrayList<String>();
		String elt;
		int deb=0;
		int fin=0;
		while (fin<line.length()){
			fin=line.indexOf(sep, deb);
			if (fin==-1)
				fin=line.length();
			elt=line.substring(deb, fin);
			res.add(elt);
			deb=fin+sep.length();
		}
		return res;
	}
	
	public static void main(String[] args) {
		String line="agent2->[revise,[no,[t(agent1_16_30,[],[],[],trace(0,0),1,[bl(a6),bl(a3),bl(a7),clear(a2),noir(a4),clear(a6),on(a6,floor),bl(a2),on(a4,floor),bleu(a5),on(a2,a3),bl(a4),noir(a3),bl(a1),on(a5,floor),noir(a1),bleu(a6),clear(a1),noir(a7),clear(a7),on(a1,floor),noir(a2),bl(a5),on(a7,floor),on(a3,a5),clear(a4)],move(a6,a1),post([noir(a6)],[bleu(a6)]),0),t(agent1_29_54,[],[],[],trace(0,0),1,[noir(a6),on(a7,floor),bl(a3),clear(a1),noir(a1),clear(a3),bl(a6),on(a3,a5),bl(a1),bleu(a5),on(a6,a7),noir(a7),bl(a2),on(a1,floor),bl(a7),bl(a5),noir(a4),on(a5,floor),noir(a3),on(a4,a6),noir(a2),bl(a4),clear(a2),on(a2,a4)],move(a3,a1),post([on(a3,a1),clear(a5)],[on(a3,a5),clear(a1)]),0),t(agent1_18_32,[],[],[],trace(0,0),1,[noir(a4),on(a2,a3),clear(a4),on(a7,floor),noir(a6),clear(a1),on(a5,floor),bleu(a5),on(a4,floor),noir(a3),on(a3,a5),bl(a7),clear(a2),bl(a6),noir(a7),bl(a4),clear(a6),on(a1,floor),bl(a5),noir(a1),bl(a1),on(a6,a7),noir(a2),bl(a2),bl(a3)],move(a2,a1),post([on(a2,a1),clear(a3)],[on(a2,a3),clear(a1)]),0)]],agent1]";
		int agent=Integer.parseInt(line.substring("agent".length(), line.indexOf("->")));
		String mess=line.substring(line.indexOf("->")+2,line.length());
		int j=Integer.parseInt(mess.substring(mess.lastIndexOf("agent")+"agent".length(), 
				mess.lastIndexOf("]")));
		Arguments l1=Arguments.parse(mess);
		Arguments l2=Arguments.parse(l1.get(1));
		Arguments l3=Arguments.parse(l2.get(1));
	
		System.out.println(line);
		System.out.println(mess);
		System.out.println(j);
		System.out.println(l1+" \n "+l2+" \n "+l3+"\n"+l3.size());
		
	}
}
