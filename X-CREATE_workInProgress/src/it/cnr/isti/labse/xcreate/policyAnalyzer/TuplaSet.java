package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.util.Iterator;
import java.util.Vector;


public class TuplaSet {
	private Vector<Tupla> tuplaSetVector;
	
	public TuplaSet(){
		this.tuplaSetVector = new Vector<Tupla> ();
	}
	
	public boolean isEmpty(){
		return this.tuplaSetVector.isEmpty();
	}
	
	public Iterator<Tupla> iterator (){
		return this.tuplaSetVector.iterator();
	}
	
	public void addTupla(Tupla tupla){
		this.tuplaSetVector.add(tupla);
	}
	
	public void printTuplaSet(){
		for (int i = 0; i < this.tuplaSetVector.size(); i++)
			this.tuplaSetVector.get(i).printTupla();
	}
	public int getSize(){
		return this.tuplaSetVector.size();
	}
}
