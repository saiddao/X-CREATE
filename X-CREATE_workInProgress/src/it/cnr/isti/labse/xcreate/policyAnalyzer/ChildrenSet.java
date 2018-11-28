package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.util.Enumeration;
import java.util.Hashtable;

public class ChildrenSet {
	private Hashtable<String, Nodo> childHashtable;
	private int cardinalitySet;
	
	public ChildrenSet(){
		this.childHashtable = new Hashtable<String, Nodo>();
		this.cardinalitySet = 0;
	}
	
	public Enumeration<String> getKeys(){
		return this.childHashtable.keys();
	}
	
	public Nodo getChild(String id){
		return this.childHashtable.get(id);
	}
	
	public void addChild(String id, Nodo child){
		this.childHashtable.put(id, child);
	}
	
	public Enumeration<String> allChildrenId(){
		return this.childHashtable.keys();
	}
	
	public void setCardinalityTuplaSet(){
	}
	
	public int getCardinalityTuplaSet(){
		for(Enumeration<String> enumerationId = allChildrenId(); enumerationId.hasMoreElements();){
			this.cardinalitySet += this.childHashtable.get((enumerationId.nextElement())).getCardinalityTuplaSet();
		}
		return this.cardinalitySet;
	}
}
