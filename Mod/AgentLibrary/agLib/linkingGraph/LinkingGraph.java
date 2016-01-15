package agLib.linkingGraph;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import agLib.agentCommunicationSystem.CanalComm;

import javax.xml.parsers.*;
import java.util.*;

public class LinkingGraph extends DefaultHandler{


	public Vector <CanalComm> Agents=new Vector <CanalComm>();
	
	public Vector <Vector <Integer>> ConnectGraph= new Vector <Vector <Integer>>(); 
		//pour chaque agent d'indice i, ConnecGraph(i) donne la liste des indices des voisins
	
	public int identifier(CanalComm inconnu){
		int indice = Agents.indexOf(inconnu);
//		if ((indice==-1) && (inconnu.equals(commSystem)) ) {
//				indice = AgentLearner.INDICE_SYSTEM;
//		}
		return indice;
	}
	
	public LinkingGraph(){
		Agents=new Vector <CanalComm>();
		ConnectGraph= new Vector <Vector <Integer>>();
	}
	
	public LinkingGraph(String fileXMLSansExt){
		this.loadFromXml(fileXMLSansExt);
	}
	
	public List <CanalComm> getVoisins(CanalComm agent){
		Vector <CanalComm> res=new Vector <CanalComm>();
		for (Integer indNeigh : ConnectGraph.get(identifier(agent))) {
			res.add(Agents.get(indNeigh.intValue()));
		}		
		return res;
	}

	public List <Integer> getVoisins(int indice){
		Vector <Integer> res=new Vector <Integer>();
		for (Integer indNeigh:ConnectGraph.get(indice)) {
			res.add(indNeigh);
		}		
		return res;
	}
	
	public int nbAgents(){
		return ConnectGraph.size();
	}
	
	public Vector <CanalComm> getAgents(){
		return Agents;
	}
	
	public void addAgent(CanalComm agent){
		Agents.add(agent);
		ConnectGraph.add(new Vector <Integer>());
	}
	
	public void addAgent(){
		Agents.add(null);
		ConnectGraph.add(new Vector <Integer>());
	}
	
	public void addAgents(int n){
		for (int i=0;i<n;i++){
			addAgent();
		}
	}
	
	public void setAgent(int i, CanalComm ag){
		Agents.set(i,ag);
	}
	
	public void addLien(CanalComm ag1, CanalComm ag2){
		addLien(identifier(ag1), identifier(ag2));
	}
	
	public void addLien(int a1, int a2){ // agents numérotés de 0 a n-1
		ConnectGraph.get(a1).add(new Integer(a2));
		ConnectGraph.get(a2).add(new Integer(a1));	
	}
	
	public void removeLien(int a1, int a2){ // agents numérotés de 0 a n-1
		ConnectGraph.get(a1).remove(new Integer(a2));
		ConnectGraph.get(a2).remove(new Integer(a1));	
	}
	
	public boolean existeLien(int n1, int n2){
		return ConnectGraph.get(n1).contains(n2);
	}
	
	public LinkingGraph copy(){
		LinkingGraph result=new LinkingGraph();
		int i,j,k;
		for (i=0;i<Agents.size();i++){
			result.addAgent(Agents.get(i));
			for (j=0;j<ConnectGraph.get(i).size();j++){
				k=ConnectGraph.get(i).get(j);
				if (k<=i) result.addLien(i,ConnectGraph.get(i).get(j));
			}
		}
		return result;
	}
	
	public LinkingGraph blankCopy(){
		LinkingGraph result=new LinkingGraph();
		int i;
		for (i=0;i<Agents.size();i++){
			result.addAgent(Agents.get(i));
		}
		return result;
	}
	public void saveToGastex(String filenameSansExt){
		FileOutputStream fOut;
		BufferedOutputStream fBufOut;
		DataOutputStream dOut;
		int i,j,indj;
		Vector <Integer> tempAgents;
		
		try{
			//ouverture fichier
			String filename=filenameSansExt.concat("-gastex.txt");
			fOut = new FileOutputStream(filename);
			fBufOut = new BufferedOutputStream(fOut);
			dOut= new DataOutputStream(fBufOut);
			//entête
			dOut.writeBytes(filename);
			dOut.writeBytes("\n Modifier les coordonnées des noeuds et la taille du graphe");
			dOut.writeBytes("<!--  -->\n");
			dOut.writeBytes("\\begin{figure}\n");
			dOut.writeBytes("  \\begin{center}\n");
			dOut.writeBytes("    \\unitlength=3pt\n");
			dOut.writeBytes("    \\begin{picture}(0,0)(50,50)\n");
			dOut.writeBytes("    \\gasset{Nw=5,Nh=5,Nmr=2.5,curvedepth=0}\n");
			dOut.writeBytes("    \\thinlines\n");
			//données fixes
			for (i=0;i<ConnectGraph.size();i++){
				dOut.writeBytes("    \\node(A"+Integer.toString(i)+
						")(0,0){$"+Integer.toString(i)+"$}\n");
			}
			//dOut.writeBytes("<NbAgent n=\""+Integer.toString(Agents.size())+"\" />\n");
			//données
			for (i=0;i<ConnectGraph.size();i++){
				tempAgents=ConnectGraph.get(i);
				for (j=0;j<tempAgents.size();j++){
					indj=tempAgents.get(j).intValue();
					if (i<indj) {
						dOut.writeBytes(
								"    \\drawedge(A"+Integer.toString(i)+
								",A"+Integer.toString(indj)+"){}\n");
					}
				}
			}
			dOut.writeBytes("    \\end{picture}\n");
			dOut.writeBytes("  \\end{center}\n");
			dOut.writeBytes("  \\caption{"+filenameSansExt+".}\n");
			dOut.writeBytes("  \\label{fig:"+filenameSansExt+"}\n");
			dOut.writeBytes("\\end{figure}\n");
			dOut.close();
		} catch (IOException e){
			e.printStackTrace();
			fOut=null;
			dOut=null;
		}
	}
	
	
	public void saveToXml(String filenameSansExt){
		FileOutputStream fOut;
		BufferedOutputStream fBufOut;
		DataOutputStream dOut;
		int i,j,indj;
		Vector <Integer> tempAgents;
		
		try{
			//ouverture fichier
			String filename=filenameSansExt.concat(".xml");
			fOut = new FileOutputStream(filename);
			fBufOut = new BufferedOutputStream(fOut);
			dOut= new DataOutputStream(fBufOut);
			//entête
			dOut.writeBytes("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			dOut.writeBytes("<GrapheLiaison>\n");
			dOut.writeBytes("<!--  -->\n");
			//données fixes
			dOut.writeBytes("<NbAgent n=\""+Integer.toString(Agents.size())+"\" />\n");
			//données
			for (i=0;i<ConnectGraph.size();i++){
				tempAgents=ConnectGraph.get(i);
				for (j=0;j<tempAgents.size();j++){
					indj=tempAgents.get(j).intValue();
					if (i<indj) {
						dOut.writeBytes("<Arc i=\""+Integer.toString(i)+
								"\" j=\""+Integer.toString(indj)+"\" />\n");
					}
				}
			}
			dOut.writeBytes("</GrapheLiaison>\n");
			dOut.close();
		} catch (IOException e){
			e.printStackTrace();
			fOut=null;
			dOut=null;
		}
	}
	
	public void loadFromXml(String filenameXMLSansExt){
		String graphXML=filenameXMLSansExt.concat(".xml");
		//handler = new XMLHandlet(); = this
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(graphXML,this);
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}
	}
	
	//XMLHandler
	public void startElement(String uri, String name, String qname, Attributes attr){
		int n,i,j;
		if (name.equals("NbAgent")) {
			n=Integer.parseInt(attr.getValue("n"));
			addAgents(n);			
		}
		if (name.equals("Arc")) {
			i=Integer.parseInt(attr.getValue("i"));
			j=Integer.parseInt(attr.getValue("j"));
			addLien(i,j);			
		}
	}
	
	
	public int[] getShortestPath(int source, int target){
		int d,i;
		if (source>nbAgents()) return null;
		if (target>nbAgents()) return null;
		List<Vector<Integer>> dist; //dist.get(i) give the list of nodes that are at a distance of i steps from j.
		dist=new Vector<Vector<Integer>>();
		boolean[] marked=new boolean[this.nbAgents()];
		boolean finish=false;
		for (i=0;i<marked.length;i++) marked[i]=false;
		dist.add(new Vector<Integer>());
		dist.get(0).add(new Integer(source));
		marked[source]=true;		
		d=0;
		while (!marked[target] && !finish){
			finish=true;
			dist.add(new Vector<Integer>());
			for (Integer prec:dist.get(d)){
				for (Integer next:getVoisins(prec.intValue())){
					if (!marked[next.intValue()]){
						marked[next.intValue()]=true;
						dist.get(d+1).add(next);
						finish=false;
					}
				}
			}
			d++;
		}
		if (finish) return null;
		int[] path=new int[d+1];
		path[d]=target;
		for(int r=d-1;r>=0;r--){
			for (Integer c:dist.get(r))
				if (this.existeLien(c.intValue(), path[r+1])){
					path[r]=c.intValue();
					break;
				}		
		}
		return path;
	}
	
	
	
	
	
	public static void main(String[] args) {
		int n=1;
		//int k=4;
		String name="Arbk2d";
		//int i;
		LinkingGraph g;
		for (n=2;n<=5;n+=1){
			g=new LinkingGraph();
			g.loadFromXml(name.concat(Integer.toString(n)));
			g.saveToGastex(name.concat(Integer.toString(n)));
			System.out.println(name.concat(Integer.toString(n)));
		}
			
		}

}

