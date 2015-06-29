package distrib;

import java.util.Comparator;
import java.util.List;

	public class CompareNbFormula implements Comparator<List<Integer>>{
		
		public CompareNbFormula(SolBioNetParsingDistributor dist, boolean ascending){
			s=dist;
			ascend=ascending;
		}
		

		public int compare(List<Integer> g1, List<Integer> g2) {
			if (ascend)
				return s.countFormulas(g1)-s.countFormulas(g2);
			else
				return s.countFormulas(g2)-s.countFormulas(g1);
		}
		
		
		
		private SolBioNetParsingDistributor s;
		private boolean ascend;

		
	}
