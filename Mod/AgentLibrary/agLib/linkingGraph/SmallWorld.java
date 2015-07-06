package agLib.linkingGraph;

import java.util.Random;
import java.util.Vector;


public class SmallWorld extends RegularGraph {
	public int n;
	public int k;
	public double p; //rewiring proba
	public static Random seed=new Random();
	
	public SmallWorld(int n,int k, double p){
		super(n,k);
		rewireAll(p);
	}
	
	public void rewireAll(double p){
		int i,l,j;
		double r;
		Vector <Integer> temp;
		
		
		for (i=0;i<ConnectGraph.size();i++){
			temp=ConnectGraph.get(i);
			for (l=0;l<temp.size();l++){
				j=temp.get(l).intValue();
				r=seed.nextDouble();
				if (r<=p && i<j){ // condition i<j pour ne tester chaque arc qu'une seule fois
					rewire(i,j);
				}				
			}			
		}
	}
	
	public void rewire(int i, int j){
		int n1,n2,n3;
		//determination du noeud fixe
		boolean node=seed.nextBoolean();
		if (node) {
			n1=i;
			n2=j;
		}
		else {
			n1=j;
			n2=i;
		}
		// determination du nouveau noeud d'arrivée
		n3=seed.nextInt(ConnectGraph.size());
		while (existeLien(n1,n3) || n3==n1){
			n3=seed.nextInt(ConnectGraph.size());			
		}
		// rewiring
		moveArc(n1,n2,n3);
	}
	
	public void moveArc(int i,int j1,int j2){
		ConnectGraph.get(i).remove(new Integer(j1));
		ConnectGraph.get(j1).remove(new Integer(i));
		addLien(i,j2);
	}

	public static void main(String[] args) {
		int n=1;
		int k=4;
		double p=0.5;
		//int i;
		SmallWorld g;
		for (n=30;n<=50;n+=10){
			g=new SmallWorld(n,k,p);
			g.saveToXml("SmWk4p05n".concat(Integer.toString(n)));
			System.out.println("SmWk4p05n".concat(Integer.toString(n)));
		}
			
	}	
	
}
