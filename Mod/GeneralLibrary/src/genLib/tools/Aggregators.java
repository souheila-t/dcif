package genLib.tools;

import java.util.ArrayList;
import java.util.List;

public class Aggregators {
	
	public static final int AGG_PRODUCT=0;
	public static final int AGG_MAX=1;
	public static final int AGG_MIN=2;
	public static final int AGG_SUM=3;
	public static final int AGG_AVERAGE=4;
	public static final int AGG_STDDEV=5;
	

	public static boolean isInt(Number n){
		return (n instanceof Long || n instanceof Integer || n instanceof Byte);
	}
	
	private static Number productAggregate(List<? extends Number> values){
		Number res=1;
		for (Number v:values) 
			if (isInt(v))
				res=new Long(res.longValue()*v.longValue());
			else 
				res=new Double(res.doubleValue()*v.doubleValue());
		return res;
	}
	
	private static Number maxAggregate(List<? extends Number> values){
		Number res=0;
		for (Number v:values) 
			if (isInt(v)){
				if (res.longValue()<v.longValue()) res=v;
			}
			else if (res.doubleValue()<v.doubleValue())
				res=v;				
		//if (w==0) return 1;
		return res;
	}
	private static Number minAggregate(List<? extends Number> values){
		Number res=Double.POSITIVE_INFINITY;
		for (Number v:values) 
			if (isInt(v)){
				if (res.equals(Double.POSITIVE_INFINITY) || res.longValue()>v.longValue()) 
					res=v;
			}
			else if (res.doubleValue()>v.doubleValue())
				res=v;				
		//if (w==0) return 1;
		return res;
	}

	private static Number sumAggregate(List<? extends Number> values){
		Number res=0;
		for (Number v:values) 
			if (isInt(v))
				res=new Long(res.longValue()+v.longValue());
			else 
				res=new Double(res.doubleValue()+v.doubleValue());
		return res;
	}
	
	private static Double avgAggregate(List<? extends Number> values){
		Number sum=sumAggregate(values);
		return new Double(sum.doubleValue()/values.size());
	}

	private static Double stddevAggregate(List<? extends Number> values){
		Double res=0.0;
		Double avg=avgAggregate(values);		
		for (Number v:values)
			res=res+(v.doubleValue()-avg)*(v.doubleValue()-avg);
		return Math.sqrt(res.doubleValue()/values.size());
	}

	public static Number aggregate(List<? extends Number> values, int aggregationCode){
		switch(aggregationCode){
		case AGG_PRODUCT: 
			return productAggregate(values);
		case AGG_MAX:
			return maxAggregate(values);
		case AGG_MIN:
			return minAggregate(values);
		case AGG_SUM:
			return sumAggregate(values);
		case AGG_AVERAGE:
			return avgAggregate(values);
		case AGG_STDDEV:
			return stddevAggregate(values);
		default:
			return defaultVal;
		}
	}
	
	public static String label(int aggregationCode){
		switch(aggregationCode){
		case AGG_PRODUCT: 
			return "Product of ";
		case AGG_MAX:
			return "Max of ";
		case AGG_MIN:
			return "Min of ";
		case AGG_SUM:
			return "Sum of ";
		case AGG_AVERAGE:
			return "Average of ";
		case AGG_STDDEV:
			return "Standard deviation of ";
		default:
			return "";
		}
	}
	
	public static List<Integer> getAllCode(){
		ArrayList<Integer> res=new ArrayList<Integer>();
		res.add(AGG_PRODUCT); 
		res.add(AGG_MAX);
		res.add(AGG_MIN);
		res.add(AGG_SUM);
		res.add(AGG_AVERAGE);
		res.add(AGG_STDDEV);
		return res;
	}
	
	public static Number defaultVal=1;

}
