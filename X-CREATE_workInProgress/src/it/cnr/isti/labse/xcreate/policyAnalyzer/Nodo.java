package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Nodo {

	private boolean root;
	private boolean leaf;
	private String nodoNome;
	private TuplaSet tuplaSet;
	private ChildrenSet childrenSet;
	private Nodo myParent;
	private String myId;


	private int cardinalitySet;


	/*per condition*/

	private Condition condition;
	private boolean conditionB;

	/*
	 * per MySQL	
	 */
	private int pkNodo;
	private int fkPadre;
	private boolean red;
	/*
	 * per la gui .. in modo che si possa identificare facilmente 
	 * un nodo dell'albero all'interno della politica XACML
	 */
	private String idXACML;
	
	public Nodo(Nodo parent, String Id){
		this.myParent = parent;
		this.myId = Id;
		this.tuplaSet = new TuplaSet();
		this.childrenSet = new ChildrenSet();
		this.conditionB = false;
		this.cardinalitySet = this.tuplaSet.getSize();
		this.fkPadre = -1;
		this.pkNodo = -1;
		this.idXACML = new String();
	}

	public int getCardinalityTuplaSet(){
		return this.cardinalitySet;
	}

	public void setCardinalityTuplaSet(int i){
		this.cardinalitySet = i;
	}
	public void setRoot(boolean root){
		this.root = root;
	}
	public void setLeaf(boolean leaf){
		this.leaf = leaf;
	}
	public boolean isRoot(){
		return this.root;
	}
	public boolean isLeaf(){
		return this.leaf;
	}
	public void setNodoName(String name){
		this.nodoNome = name;
	}
	// FIXME 
	public String getNodoName(){
		return (this.nodoNome == null)? "NULL":this.nodoNome;
	}
	public Nodo getMyParent(){
		return this.myParent;
	}

	public String getMyId(){
		return this.myId;
	}

	public void addChild(Nodo newChild){
		this.childrenSet.addChild(newChild.getMyId(), newChild);
	}

	public Nodo getChild(String idChild){
		return this.childrenSet.getChild(idChild);
	}


	public TuplaSet getTuplaSet(){
		/**
		 * FIXME
		 * Bisogna definire bene cosa si vuole ottenere ...
		 * 
		 */
		return this.tuplaSet;
	}

	public ChildrenSet getChildrenSet(){
		return this.childrenSet;
	}

	public void printTuplaSet(){
		this.tuplaSet.printTuplaSet();
	}
	public void printSet(){
		this.tuplaSet.printTuplaSet();

	}

	/*per condition*/
	public void setCondition(Condition cond){
		this.condition = cond;
		this.conditionB = true;
	}
	public boolean hasCondition(){
		return this.conditionB;
	}
	public Condition getCondition(){
		return this.condition;
	}

	public void addTupleInHashTable(Hashtable<String, Tupla> tupleHashTable) {

		for (Iterator<Tupla> iterator = this.tuplaSet.iterator(); iterator.hasNext();) {
			Tupla tupla = (Tupla) iterator.next();
			tupleHashTable.put(tupla.getTuplaId(), tupla);
		}

		if(hasCondition())
			this.condition.addTupleInHashTable(tupleHashTable);
		Enumeration<String> chidrenKeys = this.childrenSet.getKeys();

		for (String idString = chidrenKeys.nextElement(); chidrenKeys.hasMoreElements();) {
			this.childrenSet.getChild(idString).addTupleInHashTable(tupleHashTable);
		}

	}

	public Map<? extends String, ? extends Tupla> getTupleHashTable() {
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		for (Iterator<Tupla> iterator = this.tuplaSet.iterator(); iterator.hasNext();) {
			Tupla tupla = (Tupla) iterator.next();
			tupleHashTable.put(tupla.getTuplaId(), tupla);
		}
		if(hasCondition())
			tupleHashTable.putAll(this.condition.getTupleInHashTable());

		Enumeration<String> chidrenKeys = this.childrenSet.getKeys();
		while (chidrenKeys.hasMoreElements()) {
			String idString = (String) chidrenKeys.nextElement();
			tupleHashTable.putAll(this.childrenSet.getChild(idString).getTupleHashTable());
		}

		return tupleHashTable;
	}

	/*
	 * per MySQL
	 */
	public int getPkNodo() {
		return this.pkNodo;
	}

	public void setPkNodo(int pkNodo) {
		this.pkNodo = pkNodo;
	}

	/**
	 * @param fkPadre the fkPadre to set
	 */
	public void setFkPadre(int fkPadre) {
		this.fkPadre = fkPadre;
	}

	/**
	 * @return the fkPadre
	 */
	public int getFkPadre() {
		return this.fkPadre;
	}

	/*
	 * TODO PER IL POPOLAMENTO DELLA TABELLA TUPLE
	 */
	public Hashtable<String, Integer> getIdPkAsHashTable() {
		Hashtable<String, Integer> idPkAsHashTable = new Hashtable<String, Integer>();
		idPkAsHashTable.put(this.myId, new Integer(this.pkNodo));
		if(hasCondition())
			idPkAsHashTable.putAll(this.condition.getIdPkAsHashTable());
		Enumeration<String> chidrenKeys = this.childrenSet.getKeys();
		while (chidrenKeys.hasMoreElements()) {
			String idString = (String) chidrenKeys.nextElement();
			idPkAsHashTable.putAll(this.childrenSet.getChild(idString).getIdPkAsHashTable());
		}
		return idPkAsHashTable;
	}

	public Hashtable<String, Object> getIdNodoAsHash() {
		Hashtable<String, Object> idNodoAsHash = new Hashtable<String, Object>();
		idNodoAsHash.put(this.myId, this);
		if(hasCondition())
			idNodoAsHash.putAll(this.condition.getIdNodoAsHash());
		Enumeration<String> chidrenKeys = this.childrenSet.getKeys();
		while (chidrenKeys.hasMoreElements()) {
			String idString = (String) chidrenKeys.nextElement();
			idNodoAsHash.putAll(this.childrenSet.getChild(idString).getIdNodoAsHash());
		}

		return idNodoAsHash;
	}

	// per l'associazione discendente antenato.. calcolo della chiusura 
	public void setRed(boolean red) {
		this.red = red;
	}

	public boolean isRed() {
		return red;
	}
	/*
	 * per la gui .. in modo che si possa identificare facilmente 
	 * un nodo dell'albero all'interno della politica XACML
	 */
	public String getIdXACML() {
		return this.idXACML;
	}

	public void setIdXACML(String idXACML) {
		this.idXACML = idXACML;
	}

}
