package genLib.tools;

public class ToolIndex {
	
	
	//return false if both are negative
	public static boolean isBefore(int i, int j){
		return ((i<j)||(j<0))&&(i>=0);
	}
	public static int getFirst(int i, int j, int k){
		int max=Math.abs(i)+Math.abs(j)+Math.abs(k);
		if (i<0) i=max;
		if (j<0) j=max;
		if (k<0) k=max;
		int min=Math.min(i, Math.min(j,k));
		if (min==max) return -1;
		return min;		
	}
	//return true if i is the First element (and is not = -1)
	public static boolean isFirst(int testedInt, int j, int k){
		int min=getFirst(testedInt, j,k);
		return (testedInt>-1) && (testedInt==min);
		
	}

}
