package it.cnr.isti.labse.xcreate.util;

import it.cnr.isti.labse.xcreate.policyAnalyzer.ChildrenSet;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Nodo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TreeUtil {
	public static void stampaIds(Nodo node){
		ChildrenSet childrenSet;
		childrenSet = node.getChildrenSet();
		System.out.println(node.getMyId());
		System.out.flush();
		for(Enumeration<String> enumerationId = childrenSet.allChildrenId(); enumerationId.hasMoreElements();){
			stampaIds(node.getChild(enumerationId.nextElement()));
		}
		System.out.flush();
	}
	public static void printSet(Nodo node){
		ChildrenSet childrenSet;
		childrenSet = node.getChildrenSet();
		node.printTuplaSet();
		if(node.hasCondition())
			node.getCondition().printTupleSet();
		System.out.flush();
		for(Enumeration<String> enumerationId = childrenSet.allChildrenId(); enumerationId.hasMoreElements();){
			printSet(node.getChild(enumerationId.nextElement()));
		}
		System.out.flush();
	}
	
	public static List<String> getTuplaSetAsList(Nodo node){
		List<String> list = new ArrayList<String>();
		
		for (int i = 0; i < node.getCardinalityTuplaSet(); i++) {
			list.add(String.valueOf(i+1));
		}
		
		return list;
		
	}
}
