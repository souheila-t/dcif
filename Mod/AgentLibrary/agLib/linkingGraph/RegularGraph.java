package agLib.linkingGraph;


public class RegularGraph extends LinkingGraph {
	public int k;
	public int n;
	
	// Note : 	k=n-1 -> Clique
	//			k=1 -> Circuit
	public RegularGraph(int n, int k){
		super();
		this.n=n;
		this.k=k;
		addAgents(n);
		int i,j;
		for (i=0;i<n;i++){
			// agent i, à lier avec les k/2 agents suivants (en cyclant) (k pair)
			for (j=1;j<=k/2;j++){
				addLien(i,(i+j)%n);
			}			
			// k impair, ajouter l'axe si n pair (sinon impossible)
			if (k%2==1 && n%2==0 &&i<n/2) {
				addLien(i,i+n/2);
			}
		}
	}
	
	public static void main(String[] args) {
	int n=1;
	int k=2;
	//int i;
	RegularGraph g;
	for (n=512;n<=512;n+=2){
		g=new RegularGraph(n,n-1);
		g.saveToXml("Clique_".concat(Integer.toString(n)));
		System.out.println("Clique_".concat(Integer.toString(n)));
	}
		
	}
}

