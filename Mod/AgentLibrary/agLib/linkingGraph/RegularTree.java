package agLib.linkingGraph;



public class RegularTree extends LinkingGraph {
	public int n;
	public int k;
	// k = 1 : ligne
	// k = 2 : arbre binaire
	// k = n : étoile centré sur 0
	public RegularTree(int n, int k){
		super();
		this.n=n;
		this.k=k;
		addAgents(n);
		int i;
		// racine = agent 0. On lie du fils vers le père
		for (i=1;i<n;i++){
			// agent i, à lier à un père (indice père = (i-1) div k)
			addLien(i,(i-1)/k);					
		}
	}
	
	public static void main(String[] args) {
		int n=1;
		//int depth;
		int k=1;
		//int i;
		RegularTree g;
		for (n=100;n<=100;n+=10){
			//System.out.println("test".concat(Integer.toString(2^2)));
//			n=((int)(Math.pow(k,depth))-1)/(k-1);
			g=new RegularTree(n,k);
			g.saveToXml("Ligne_".concat(Integer.toString(n)));
			System.out.println("Ligne_".concat(Integer.toString(n)));
		}
			
		}
}
