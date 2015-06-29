/**

 * 

 */

package stats;



import genLib.tools.Aggregators;

import java.util.ArrayList;
import java.util.List;

import agLib.masStats.BasicSystemStats;
import agLib.masStats.StatCounter;








/**

 * @author Viel Charlotte

 *

 */

public class ConsFindingSystemStats extends BasicSystemStats {

	public static final int DCF_INFERENCESTEPS = 1;

	public static final int DCF_NB_SENT_MESSAGES = 2;

	public static final int DCF_NB_RECEIVED_MESSAGES = 3;

	

	public ConsFindingSystemStats() {

		addNewCounter(DCF_INFERENCESTEPS);

		addNewCounter(DCF_NB_SENT_MESSAGES);

		addNewCounter(DCF_NB_RECEIVED_MESSAGES);

	}

	

	public Number get(int summaryMeasureCode){

		switch(summaryMeasureCode){

		case DCF_INFERENCESTEPS:return getSystMeasure(DCF_INFERENCESTEPS, 

								StatCounter.KEY_VALUE);

		case DCF_NB_SENT_MESSAGES:return getSystMeasure(DCF_NB_SENT_MESSAGES, 

				StatCounter.KEY_VALUE);

		case DCF_NB_RECEIVED_MESSAGES:return getSystMeasure(DCF_NB_RECEIVED_MESSAGES, 

				StatCounter.KEY_NB);	

		}

		return -1;

	}

	

	public String getSummaryLabel(int summaryMeasureCode){

		String res="";

		switch(summaryMeasureCode){

		case DCF_INFERENCESTEPS:return "Inference steps";

		case DCF_NB_SENT_MESSAGES:return "Messages sent";

		case DCF_NB_RECEIVED_MESSAGES:return "Messages received";

		}

		return res;			

	}

	

	public List<Integer> getSummaryCodes(){

		ArrayList<Integer> res=new ArrayList<Integer>();

		res.add(DCF_INFERENCESTEPS);

		res.add(DCF_NB_SENT_MESSAGES);

		res.add(DCF_NB_RECEIVED_MESSAGES);

		return res;		

	}



	

	//need to define new types of counter and constructor to add them

	public String getFullLabel(int measureCode, int measureType, int aggCode){

		if (measureType==KEY_SUMMARY)

			return getSummaryLabel(measureCode);

		String res="";

		if (aggCode==KEY_SYSTMEASURE){

			res+=StatCounter.label(measureType);

			switch(measureCode){

			case DCF_INFERENCESTEPS : res += "Inference steps"; break;

			case DCF_NB_SENT_MESSAGES : res += "Messages sent"; break;

			case DCF_NB_RECEIVED_MESSAGES : res += "Messages received"; break;

			}

			return res;

		}

		else if (!agData.isEmpty()){

			res=Aggregators.label(aggCode);

			res+=agData.get(0).getFullLabel(measureCode, measureType).toLowerCase();

		}

		return res;

	}



}

