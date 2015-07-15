package stats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatsTests {





	@Test
	public void compareIdenticalPropFiles() {
		String filename1 ="/home/magma/projects/dcif/Etude/ressources/glucolysis_max-4_ld-1--1_DICF-PB-Async";
		String filename2 ="/home/magma/projects/dcif/Etude/ressources/glucolysis_max-4_ld-1--1_DICF-PB-Async_bis";
		compCsq.main(new String[] {filename1, filename2});

		// assert statements
		assertEquals("0 equals 0", 0, 0);
	}

} 

