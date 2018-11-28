package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class Function {

	private String myId;
	private String function;
	private Vector<Object> argomenti;
	private Object myParent;
	private int cardinalitySet;
	// FIXME for mySQL
	private int pkNodo;
	private int fkPadre;
	private boolean red;
	private String nomeNodo;
	
	public Function(Object parent, String id, String function){
		this.function = function;
		this.myParent = parent;
		this.myId = id;
		this.argomenti = new Vector<Object>();
		this.cardinalitySet = 0;
	}

	public void addArgomento(TuplaSet tuplaSet){
		this.argomenti.add(tuplaSet);
	}

	public void addArgomento(Function tuplaSet){
		this.argomenti.add(tuplaSet);
	}

	public Object getParent(){
		return this.myParent;
	}
	public String getFunctionId(){
		return this.myId;
	}
	public String getFunctionType(){
		return this.function;
	}

	public void printTupleSet(){
		Object obj;
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof TuplaSet)
				((TuplaSet)obj).printTuplaSet();
			else
				((Function)obj).printTupleSet();
		}

	}

	public int getCardinalitySet(){

		Object obj;
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof TuplaSet)
				this.cardinalitySet += ((TuplaSet)obj).getSize();

			else
				this.cardinalitySet+=((Function)obj).getCardinalitySet();
		}
		return this.cardinalitySet;
	}

	public void setCardinalitySet() {
		Object obj;
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof TuplaSet)
				this.cardinalitySet += ((TuplaSet)obj).getSize();
			else
				((Function)obj).setCardinalitySet();
		}
	}

	public void addTupleInHashTable(Hashtable<String, Tupla> tupleHashTable) {
		// TODO Auto-generated method stub
		Object obj;
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof TuplaSet)
				for (Iterator<Tupla> iterator = ((TuplaSet)obj).iterator(); iterator.hasNext();) {
					Tupla tupla = (Tupla) iterator.next();
					tupleHashTable.put(tupla.getTuplaId(), tupla);
				}
			else
				((Function)obj).addTupleInHashTable(tupleHashTable);
		}
	}

	public Map<? extends String, ? extends Tupla> getTupleInHashTable() {
		// TODO Auto-generated method stub
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		Object obj;
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof TuplaSet)
				for (Iterator<Tupla> iterator = ((TuplaSet)obj).iterator(); iterator.hasNext();) {
					Tupla tupla = (Tupla) iterator.next();
					tupleHashTable.put(tupla.getTuplaId(), tupla);
				}
			else
				tupleHashTable.putAll(((Function)obj).getTupleInHashTable());
		}
		return tupleHashTable;
	}

	public String getMyId() {
		return myId;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public Vector<Object> getArgomenti() {
		return argomenti;
	}

	public void setArgomenti(Vector<Object> argomenti) {
		this.argomenti = argomenti;
	}

	public Object getMyParent() {
		return myParent;
	}

	public void setMyParent(Object myParent) {
		this.myParent = myParent;
	}

	public int getPkNodo() {
		return pkNodo;
	}

	public void setPkNodo(int pkNodo) {
		this.pkNodo = pkNodo;
	}

	public int getFkPadre() {
		return fkPadre;
	}

	public void setFkPadre(int fkPadre) {
		this.fkPadre = fkPadre;
	}

	public void setCardinalitySet(int cardinalitySet) {
		this.cardinalitySet = cardinalitySet;
	}

	/*
	 * TODO PER IL POPOLAMENTO DELLA TABELLA TUPLE
	 */
	public Hashtable<String, Integer> getIdPkAsHashTable() {
		// TODO Auto-generated method stub
		Hashtable<String, Integer> idPkAsHashTable = new Hashtable<String, Integer>();
		Object obj;
		idPkAsHashTable.put(this.myId, new Integer(this.pkNodo));
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof Function)
				idPkAsHashTable.putAll(((Function)obj).getIdPkAsHashTable());
		}
		return idPkAsHashTable;
	}

	public Hashtable<String, Object> getIdNodoNodoAsHash() {
		// TODO Auto-generated method stub
		
		Hashtable<String, Object> idNodoNodoAsHashTable = new Hashtable<String, Object>();
		Object obj;
		idNodoNodoAsHashTable.put(this.myId, this);
		for(int i = 0; i < this.argomenti.size(); i++){
			obj = this.argomenti.get(i);
			if(obj instanceof Function)
				idNodoNodoAsHashTable.putAll(((Function)obj).getIdNodoNodoAsHash());
		}
		return idNodoNodoAsHashTable;
		
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
		//return (this.nomeNodo.startsWith(String.valueOf("caso"))) ? "Apply" : this.nomeNodo ;
		return this.nomeNodo;
	}

}
