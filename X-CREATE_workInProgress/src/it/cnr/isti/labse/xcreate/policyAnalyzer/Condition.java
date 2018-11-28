package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.util.Hashtable;
import java.util.Map;

public class Condition {
	private Nodo myParent;
	private String myId;
	private Function child;
	private String nomeNodo;
	
	// FIXME for mySQL
	private int pkNodo;
	private int fkPadre;
	private boolean red;
	
	public Condition(Nodo parent, String id){
		this.myParent = parent;
		this.myId = id;
	}
	
	public Condition(String id){
		this.myId = id;
	}
	
	public Condition(){
	
	}
	
	public Nodo parent(){
		return this.myParent;
	}
	
	public void setChild(Function function){
		this.child = function;
	}
	
	public Function getChild(){
		return this.child;
	}
	public void printTupleSet(){
		this.child.printTupleSet();
	}
	
	public void setCardinalitySet(){
		this.child.setCardinalitySet();
	}
	public int getCardinalitySet(){
		return this.child.getCardinalitySet();
	}

	public void addTupleInHashTable(Hashtable<String, Tupla> tupleHashTable) {
		// TODO Auto-generated method stub
		this.child.addTupleInHashTable(tupleHashTable);
	}
//FIXME da completare .........
	public Map<? extends String, ? extends Tupla> getTupleInHashTable() {
		// TODO Auto-generated method stub
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		Hashtable<String, Tupla> childTupleHashTable = (Hashtable<String, Tupla>) this.child.getTupleInHashTable();
		tupleHashTable.putAll(childTupleHashTable);
		return tupleHashTable;
	}

	public Nodo getMyParent() {
		return this.myParent;
	}

	public void setMyParent(Nodo myParent) {
		this.myParent = myParent;
	}

	public String getMyId() {
		return this.myId;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}

	public int getPkNodo() {
		return this.pkNodo;
	}

	public void setPkNodo(int pkNodo) {
		this.pkNodo = pkNodo;
	}

	public int getFkPadre() {
		return this.fkPadre;
	}

	public void setFkPadre(int fkPadre) {
		this.fkPadre = fkPadre;
	}
	/*
	 * TODO PER IL POPOLAMENTO DELLA TABELLA TUPLE
	 */
	public Map<? extends String, ? extends Integer> getIdPkAsHashTable() {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> idPkAsHashTable = new Hashtable<String, Integer>();
		idPkAsHashTable.put(this.myId, new Integer(this.pkNodo));
		Hashtable<String, Integer> childIdPkHashTable = (Hashtable<String, Integer>) this.child.getIdPkAsHashTable();
		idPkAsHashTable.putAll(childIdPkHashTable);
		return idPkAsHashTable;
	}

	public Map<? extends String, ? extends Object> getIdNodoAsHash() {
		// TODO Auto-generated method stub
		Hashtable<String, Object> idNodoNodoAsHash = new Hashtable<String, Object>();
		idNodoNodoAsHash.put(this.myId,this);
		Hashtable<String, Object> childIdNodoNodoHashTable = (Hashtable<String, Object>) this.child.getIdNodoNodoAsHash();
		idNodoNodoAsHash.putAll(childIdNodoNodoHashTable);
		return idNodoNodoAsHash;
	}
	// MySQL :: per l'associazione AntenatoDiscendente
	public void setRed(boolean red) {
		this.red = red;
	}

	public boolean isRed() {
		return red;
	}

	public void setNomeNodo(String nomeNodo) {
		this.nomeNodo = nomeNodo;
	}

	public String getNomeNodo() {
		return this.nomeNodo;
	}
}
