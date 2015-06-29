package org.nabelab.solar.pfield;

import java.util.ArrayList;
import java.util.List;

import org.nabelab.solar.Env;
import org.nabelab.solar.Literal;
import org.nabelab.solar.PLiteral;
import org.nabelab.solar.SymTable;
import org.nabelab.solar.indexing.DiscTree;

public class PFieldCheckerCardConstraints extends PFieldCheckerWithSubst {

	public PFieldCheckerCardConstraints(Env env, PField pfield) {
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
	    	        	// TODO check if more specific form of this literal are preent in the disc tree ??
	    	        }
	    	    }
	    	    else {
	    	    	// check if this specific literal is already present
	    	    	if (pfieldItems!=null){
	    	    		Literal refLitPos=new Literal(env, true,  lit.getTerm());
	    	    		Literal refLitNeg=new Literal(env, false,  lit.getTerm());
	    	    		boolean pos= (lit.getSign()!=PLiteral.NEG);
	    	    		boolean neg= (lit.getSign()!=PLiteral.POS);
	    	    		if (pos){
	    	    			PFieldItem item=pfieldItems.contains(refLitPos);
	    	    			if (item!=null)
	    	    				item.addToGroup(pfCtr);
	    	    		}
	    	    		if (neg){
	    	    			PFieldItem item=pfieldItems.contains(refLitNeg);
	    	    			if (item!=null)
	    	    				item.addToGroup(pfCtr);
	    	    		}	    	    			    	    		
	    	    	}
	    	    	//TODO Problem : double sign // special literals and cardinality constraints on single sign ?
	    	    	//TODO if not present, add a new node ? not unless there is a subsuming node (or a max general instance)
	    	    }
	    	        
	    	}
	    }
	    
	}
	
	

}
