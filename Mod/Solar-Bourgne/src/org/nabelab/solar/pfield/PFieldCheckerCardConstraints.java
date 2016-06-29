package org.nabelab.solar.pfield;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.Options;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.parser.ParseException;

public class PFieldCheckerCardConstraints extends PFieldCheckerWithSubst {

	public PFieldCheckerCardConstraints(Env env, PField pfield) throws ParseException {
	    super(env, pfield);
	
	    for (PFCardConstraint constraint : pfield.getAddConstraints()){
		    // create constraint counters 
	    	PFieldCounter pfCtr=new PFieldCounter(constraint.getMaxLengthGroup());
	    	// search literals in the group
	    	for (PLiteral lit:constraint.getGroup()){
	    		if (lit.isSpecial()){ // special literal : the constraint is only applied to all allowed literals (of correct sign) defined by language
	    			SymTable symTable = env.getSymTable();
	    			for (int name=0; name < symTable.getNumSyms(PREDICATE); name++) {
	    			      
	    				switch (lit.getSign()) {
	    				case PLiteral.POS:
	    					if (positives[name]!=null) positives[name].addToGroup(pfCtr); 
	    				break;
	    				case PLiteral.NEG:
	    					if (negatives[name]!=null) negatives[name].addToGroup(pfCtr); 
	    	            break;
	    				case PLiteral.BOTH:      
	    					if (positives[name]!=null) positives[name].addToGroup(pfCtr); 
	    					if (negatives[name]!=null) negatives[name].addToGroup(pfCtr); 
	    	            break;
	    	            default:
	    	            	assert(false);
	    				}
	    			}
	    		}
	    	    else if (lit.isMaxGeneral()){ // //max general : if in the language, suffices to add it to the group, otherwise add to subsumed literals
	    	        int name = lit.getName();
	    	        int sign = lit.getSign();
	    	        boolean checkTree=false;
	    	        switch (sign) {
	    	        case PLiteral.POS:
	    	        	if (positives[name]!=null) positives[name].addToGroup(pfCtr);
	    	        	else checkTree=true;
	    	        break;
	    	        case PLiteral.NEG:
	    	        	if (negatives[name]!=null) negatives[name].addToGroup(pfCtr);
	    	        	else checkTree=true;
	    	        break;
	    	        case PLiteral.BOTH:        
	    	        	if (positives[name]!=null) positives[name].addToGroup(pfCtr);
	    	        	else checkTree=true;
	    	        	if (negatives[name]!=null) negatives[name].addToGroup(pfCtr);
	    	        	else checkTree=true;
	    	        break;
	    	        default:
	    	          assert(false);
	    	        }	    	    	  
	    	        if (checkTree) {
	    	        	 //TODO check if more specific form of this literal are preent in the disc tree ??
	    	        	if (pfieldItems != null){
	    	        		PFieldItem item = pfieldItems.containsOtherForm(Literal.parse(env, new Options(env), lit.toString()));
	    	        		if (item!=null)
	    	        			item.addToGroup(pfCtr);
	    	        	}
	    	        }
	    	    }
	    	    else {
	    	    	// check if this specific literal is already present
	    	    	boolean addNode = true;
	    	    	if (pfieldItems!=null){
	    	    		Literal refLitPos=new Literal(env, true,  lit.getTerm());
	    	    		Literal refLitNeg=new Literal(env, false,  lit.getTerm());
	    	    		boolean pos= (lit.getSign()!=PLiteral.NEG);
	    	    		boolean neg= (lit.getSign()!=PLiteral.POS);
	    	    		if (pos){
	    	    			PFieldItem item=pfieldItems.contains(refLitPos);
	    	    			if (item!=null){
	    	    				item.addToGroup(pfCtr);
	    	    				addNode = false;
	    	    			}
	    	    		}
	    	    		if (neg){
	    	    			PFieldItem item=pfieldItems.contains(refLitNeg);
	    	    			if (item!=null){
	    	    				item.addToGroup(pfCtr);
	    	    				addNode = false;
	    	    			}	    	    	
	    	    		}
	    	    		//TODO Problem : double sign // special literals and cardinality constraints on single sign ?
	    	    	}
	    	    	
	    	    	
	    	    	//TODO if not present, add a new node ? not unless there is a subsuming node (or a max general instance)
	    	    	if (addNode){
	    	    		
	    	    			switch (lit.getSign()) {
	    	    			case PLiteral.POS:      
	    	    			{
	    	    				Literal l = new Literal(env, true, lit.getTerm());
	    	    				if (pfieldItems.findSubsumed(l) == null){
	    	    					PFieldItem item = new PFieldItem(lit, maxLenCounter);
	    	    					pfieldItems.add(l, item);
	    	    					break;
	    	    				}
	    	    			}
	    	    			case PLiteral.NEG:
	    	    			{
	    	    				Literal l = new Literal(env, false, lit.getTerm());
	    	    				if (pfieldItems.findSubsumed(l) == null){
	    	    					
	    	    					PFieldItem item = new PFieldItem(lit, maxLenCounter);
	    	    					pfieldItems.add(l, item);
	    	    					break;
	    	    				}
	    	    			}
	    	    			case PLiteral.BOTH:
	    	    			{
	    	    				Literal l1 = new Literal(env, true,  lit.getTerm());
	    	    				Literal l2 = new Literal(env, false, lit.getTerm());
	    	    				PFieldItem item = new PFieldItem(lit, maxLenCounter);
	    	    				pfieldItems.add(l1, item);
	    	    				pfieldItems.add(l2, item);
	    	    				break;
	    	    			}
	    	    			}
	    	    		}

	    	    	}
	    	    }
	    	        
	    	    
	    	}
	    }
	    
	    
	}
	
	public static PFieldCheckerCardConstraints createChecker(Env env, PField pfield) throws ParseException {
		return new PFieldCheckerCardConstraints(env, pfield);
		
	}
	
	

}
