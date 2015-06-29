package agLib.masStats;

import java.util.ArrayList;
import java.util.List;

import genLib.tools.Aggregators;

public class StatCounter<Key> {
	public static final int KEY_NB=1;
	public static final int KEY_TOTSIZE=2;
	public static final int KEY_MAXSIZE=3;
	public static final int KEY_MINSIZE=4;
	public static final int KEY_VALUE=0;
	
		//identifier
		public Key key;
		//stats
		public long nb=0;
		public long totalSize=0;
		public int maxSize=-1;
		public int minSize=-1;
		public Number value=-1;
		//type
		public boolean hasNb=false;
		public boolean hasSize=false;
		public boolean hasValue=false;
		
		
		public StatCounter(Key k){
			key=k;
		}

		public void updateWithCtr(StatCounter<?> upd){
			nb=upd.nb;
			totalSize=upd.totalSize;
			maxSize=upd.maxSize;
			minSize=upd.minSize;
			value=upd.value;
			hasNb=upd.hasNb;
			hasSize=upd.hasSize;
			hasValue=upd.hasValue;
			
		}

		public void inc(int size){
			nb++;
			hasNb=true;
			if (size!=1) hasSize=true;
			totalSize+=size;
			if (size>maxSize)
				maxSize=size;
			if (size<minSize||minSize<0)
				minSize=size;
		}
		
		public void set(Number val){
			hasValue=true;
			value=val;
		}
		
		public boolean equalKey(Object obj){
			return (key.equals(obj));
		}	
	
		public Number get(int typeMeasure){
			switch(typeMeasure){
			case KEY_NB:
				return nb;
			case KEY_TOTSIZE:
				return totalSize;
			case KEY_MAXSIZE:
				return maxSize;
			case KEY_MINSIZE:
				return minSize;
			case KEY_VALUE:
				return value;	
			}
			return -1;
		}
		
		public Key getKey(){
			return key;
		}
		public void setKey(Key k){
			key=k;
		}
		
		public static String label(int measureType){
			String res="";
			switch(measureType){
			case StatCounter.KEY_NB: res+="Nb of "; break;
			case StatCounter.KEY_TOTSIZE:res+="Total size of "; break;
			case StatCounter.KEY_MAXSIZE:res+="Max size of "; break;
			case StatCounter.KEY_MINSIZE:res+="Min size of "; break;
			}
			return res;
		}
		
		
		public static <K> StatCounter<K> merging(K k,
				List<StatCounter<?>> ctrList, int codeAggValue){
			StatCounter<K> t=new StatCounter<K>(k);
			List<Number> values=new ArrayList<Number>();
			for (StatCounter<?> ctr: ctrList){
				t.nb+=ctr.nb;
				t.totalSize+=ctr.totalSize;
				if (ctr.maxSize>t.maxSize)
					t.maxSize=ctr.maxSize;
				if (ctr.minSize>-1 && (ctr.minSize<t.minSize||t.minSize==-1))
						t.minSize=ctr.minSize;
				if (ctr.value.intValue()!=-1) 
					values.add(ctr.value);	
			}
			if (t.nb>0) t.hasNb=true;
			if (t.totalSize>t.nb) t.hasSize=true;
			if (!values.isEmpty()){
				t.hasValue=true;
				t.value=Aggregators.aggregate(values, codeAggValue);
			}
			return t;
		}
		
		public String toString(){
			String res="";
			if (hasValue)
				res+=value;
			if (hasSize){
				if (hasValue)
					res+=" / ";
				res+="Total="+totalSize;
				if (nb>1)
					res+=" ("+nb+" occ in ["+minSize+","+maxSize+"], avg="
						+(((double)totalSize)/nb)+")";
				else res+=" ("+nb+" occ)";
			}
			else if (hasNb){
				if (hasValue)
					res+=" / "+nb+" occ";
				else
					res+=nb;
			}
			return res;
		}
}
