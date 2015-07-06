package genLib.tools;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class ToolsJDomElement {

	@SuppressWarnings("rawtypes")
	public static List<Element> typeAsElement(List l){
		List<Element> result=new ArrayList<Element>();
		for (Object o:l){
			result.add((Element)o);
		}
		return result;
	}
	public static void fetchAttributes(Element e,Element attHolder){
		for (Object o:attHolder.getAttributes()){
			Attribute a=(Attribute) ((Attribute)o).clone();
			a.detach();
			e.setAttribute(a);
		}
	}
	public static Element fusionContent(Element e1, Element e2,boolean firstHeader){
		Element result;
		if (e1==null && e2==null) return null;
		firstHeader=(firstHeader && (e1!=null)) || (e2==null);
		if (firstHeader){
			result=new Element(e1.getName());
			fetchAttributes(result,e1);
		}
		else {
			result=new Element(e2.getName());
			fetchAttributes(result,e2);
		}
		if (e1!=null) result.addContent(e1.cloneContent());
		if (e2!=null) result.addContent(e2.cloneContent());
		String fusionType="";
		if (e1!=null) 
			fusionType=e1.getAttributeValue("type");
		if (fusionType!=null){
			if (fusionType.equals("fusion"))
				collapseChildren(result,firstHeader);
			if (fusionType.equals("unique"))
				suppressRedundantChildren(result,true);
		}
		return result;
	}

	public static void collapseChildren(Element e, boolean firstHeader){
		if (e==null) return;
		List <String> labels=new ArrayList<String>();
		for (Object o:e.getChildren()){
			String name=((Element)o).getName();
			if (!labels.contains(name)) labels.add(name);
		}
		for (String label:labels){
			if (e.getChildren(label).size()>1){
				Element temp=e.getChild(label);
				e.removeContent(temp);
				for (Element c:typeAsElement(e.getChildren(label))){
					temp=fusionContent(temp,c,firstHeader);
				}
				e.removeChildren(label);
				e.addContent(temp);
			}
		}
	}

	public static void suppressRedundantChildren(Element e, boolean keepLast){
		if (e==null) return;
		List <String> labels=new ArrayList<String>();
		for (Object o:e.getChildren()){
			String name=((Element)o).getName();
			if (!labels.contains(name)) labels.add(name);
		}
		for (String label:labels){
			if (e.getChildren(label).size()>1){
				Element temp=e.getChild(label);
				e.removeContent(temp); //to avoid exploring it again in loop
				if (keepLast)
					for (Element c:typeAsElement(e.getChildren(label))){
						temp=c;
					}
				e.removeChildren(label);
				e.addContent(temp);
			}
		}
	}


}
