package it.cnr.isti.labse.xcreate.policyAnalyzer;

import it.cnr.isti.labse.xcreate.xSDResources.XacmlDataTypes;

import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Aggiunge entita' casuali a un insieme.
 * 
 * @author Said Daoudagh
 */
public class NewRandomValuesManager {
	private static int setSize;
	private static Hashtable<? extends String, ? extends Tupla> tupleAsHashTable;
	private static HashSet<String> attValueSubSet;

	private static Hashtable<String, Vector<Tupla>> randomValues;
	private static Hashtable<String, Boolean> guardDataTypes;

	public static void addRamdonValues(Nodo nodoRoot) {
		randomValues = new Hashtable<String, Vector<Tupla>>();
		initGuardDataTypes();
		attValueSubSet = new HashSet<String>();
		setSize = nodoRoot.getCardinalityTuplaSet();
		tupleAsHashTable = (Hashtable<? extends String, ? extends Tupla>) nodoRoot.getTupleHashTable();
		Enumeration<? extends String> keys = tupleAsHashTable.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			attValueSubSet.add(tupleAsHashTable.get(key).getAttributeValue());
		}
		if(nodoRoot.getTuplaSet().getSize() != 0)
			addValues(nodoRoot.getTuplaSet(), nodoRoot);
		
		addValues(nodoRoot);
	}

	private static void addValues(Nodo nodo) {
		TuplaSet tuplaSet = nodo.getTuplaSet();
		if(tuplaSet.getSize() != 0)
			addValues(tuplaSet, nodo);
		if(nodo.hasCondition()){
			addValuesToCondition(nodo.getCondition().getChild(), nodo);
		}
		Enumeration<String> children = nodo.getChildrenSet().allChildrenId();
		while (children.hasMoreElements()) {
			addValues(nodo.getChild(children.nextElement()));
		}
	}

	private static void addValuesToCondition(Function fun, Nodo n) {
		Vector<Object> args = fun.getArgomenti();
		Object obj;
		for (int i = 0; i < args.size(); i++) {
			obj = args.get(i);
			if(obj instanceof Function)
				addValuesToCondition((Function)obj, n);
			else
				addValues((TuplaSet)obj, n);
		}
	}

	
	private static void addValues(TuplaSet tuplaSet, Nodo n) {
		
		Iterator<Tupla> tuplaIterator = tuplaSet.iterator();
		Vector<Tupla> tupleVector = new Vector<Tupla>();
		//int count = 0;
		while (tuplaIterator.hasNext()) {
			//System.out.println(++count+" - "+tuplaIterator.toString());
			Tupla tupla = (Tupla) tuplaIterator.next();
			//se non ho ancora generato valori random per il tipo associato alla tupla attuale
			if(!guardDataTypes.get(tupla.getDataType()).booleanValue()){
				// * creo valori random per il tipo e lo inserisco tra i valori random  
				randomValues.put(tupla.getDataType(), getNewRandomValue(tupla));
				//* aggiungo i valori appena creati anche nel vettore dei valori random riservato al nodo attuale
				tupleVector.addAll(randomValues.get(tupla.getDataType()));
				guardDataTypes.put(tupla.getDataType(), new Boolean(true));
				// * aggiorno la guardia del tipo attuale
	           guardDataTypes.put(tupla.getDataType(), new Boolean(true));
	    	}
			if(randomValues.containsKey(tupla.getDataType()))//{
				tupleVector.addAll(randomValues.get(tupla.getDataType()));
			else{
				/*
				 * ho gia' creato valori random per il tipo attuale
				 * se non ho ancora aggiunto valori random del tipo attuale al nodo attuale 
				 */
				if(!tupleVector.containsAll(randomValues.get(tupla.getDataType())))
					// * aggiungo tali valori al vettore dei valori random riservato nodo attuale
					tupleVector.addAll(randomValues.get(tupla.getDataType()));
			}
		}
		
		/**
		 * aggiungo i valori random al nodo attuale per tutti i tipi che detiene
		 */
		for(Tupla t : tupleVector)
			tuplaSet.addTupla(t);
	}

	private static Vector<Tupla> getNewRandomValue(Tupla tupla) {
		Vector<Tupla> tuplaVector = new Vector<Tupla>();	
		/*
		 * 1) GESTIONE TIPO ANY_URI
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.ANY_URI)){
			Tupla randomTupla;
			randomTupla = getClone(tupla);
			randomTupla.setAttributeValue(tupla.getAttributeValue()+"_RandomValue_"+getRandomString()+"_"+String.valueOf(++setSize));
			randomTupla.setTuplaId(String.valueOf(setSize));
			
			tuplaVector.add(randomTupla);
		}
		/*
		 * 2) 
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.BASE_64_BINARY)){

		}
		/*
		 * 3)
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.BOOLEAN)){
			Tupla randomTupla;
			randomTupla = getClone(tupla);
			boolean attrValueContent = Boolean.valueOf(tupla.getAttributeValue());
			setSize++;
			randomTupla.setAttributeValue(String.valueOf(!attrValueContent));
			randomTupla.setTuplaId(String.valueOf(setSize));
			tuplaVector.add(randomTupla);
		}
		/*
		 *4) 
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.DATE)){

		}
		/*
		 * 5)
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.DATE_TIME)){

		}
		/*
		 * 6)
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.DAY_TIME_DURATION)){

		}
		/*
		 * 7)
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.DOUBLE)){
			Tupla randomTuplaMeno = getClone(tupla);
			Tupla randomTuplaPiu = getClone(tupla);
			double attrValueContent = Double.valueOf(tupla.getAttributeValue());
			
			setSize++;
			randomTuplaMeno.setAttributeValue(String.valueOf(attrValueContent-1));
			randomTuplaMeno.setTuplaId(String.valueOf(setSize));
			setSize++;
			randomTuplaPiu.setAttributeValue(String.valueOf(attrValueContent+1));
			randomTuplaPiu.setTuplaId(String.valueOf(setSize));
			
			tuplaVector.add(randomTuplaMeno);
			tuplaVector.add(randomTuplaPiu);
		}
		/*
		 * 8)
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.HEX_BINARY)){

		}
		/*
		 * 9) GESTIONE TIPO INTEGER
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.INTEGER)){
			System.out.println("NewRandomValuesManager.getNewRandomValue()");
			Tupla randomTuplaMeno = getClone(tupla);
			Tupla randomTuplaPiu = getClone(tupla);
			int attrValueContent;
			if(notInteger(tupla.getAttributeValue()))
				attrValueContent = mappingCasoToInteger(tupla.getAttributeValue());
			else 
				attrValueContent = Integer.valueOf(tupla.getAttributeValue());
			setSize++;
			randomTuplaMeno.setAttributeValue(String.valueOf(attrValueContent-1));
			randomTuplaMeno.setTuplaId(String.valueOf(setSize));
			setSize++;
			randomTuplaPiu.setAttributeValue(String.valueOf(attrValueContent+1));
			randomTuplaPiu.setTuplaId(String.valueOf(setSize));
			
			tuplaVector.add(randomTuplaMeno);
			tuplaVector.add(randomTuplaPiu);
			
			System.out.println(randomTuplaMeno.toString());
			System.out.println(tupla.toString());
			System.out.println(randomTuplaPiu.toString());
		}
		/*
		 * 10)
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.RFC_822_NAME)){

		}
		/* 
		 * 11) GESTIONE TIPO STRING
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.STRING)){
			Tupla randomTupla;	
			randomTupla = getClone(tupla);
			randomTupla.setAttributeValue("RandomValue:"+getRandomString()+":"+String.valueOf(++setSize));
			randomTupla.setTuplaId(String.valueOf(setSize));
			tuplaVector.add(randomTupla);
		}
		/*
		 * 12) 
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.TIME)){

		}
		/*
		 * 13) 
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.X_500_NAME)){

		}
		/*
		 * 14) 
		 */
		if(tupla.getDataType().equals(XacmlDataTypes.YEAR_MONTH_DURATION)){

		}
		return tuplaVector;
	}

	
	
	private static int mappingCasoToInteger(String attributeValue) {
		// TODO Auto-generated method stub
		return attributeValue.hashCode();
	}

	private static boolean notInteger(String attributeValue) {
		// TODO Auto-generated method stub
		return ((attributeValue.contains("caso")) ? true : false);
	}

	private static void initGuardDataTypes() {
		guardDataTypes = new Hashtable<String, Boolean>();
		guardDataTypes.put(XacmlDataTypes.ANY_URI, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.BASE_64_BINARY, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.BOOLEAN, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.DATE, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.DATE_TIME, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.DAY_TIME_DURATION, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.DOUBLE, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.HEX_BINARY, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.INTEGER, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.RFC_822_NAME, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.STRING, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.TIME, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.X_500_NAME, new Boolean(false));
		guardDataTypes.put(XacmlDataTypes.YEAR_MONTH_DURATION, new Boolean(false));
	}

	private static Tupla getClone(Tupla tupla) {
		Tupla t = new Tupla();
		t.setAttributeId(tupla.getAttributeId());
		t.setDataType(tupla.getDataType());
		t.setIssuer(tupla.getIssuer());
		t.setSubjectCategory(tupla.getSubjectCategory());
		return t;
	}
	/**
	 * Crea un guid di lunghezza prefissata (16 Byte)
	 * utilizzando {@link SecureRandom}
	 * @return
	 */
	public static byte[] getGuid(){
		SecureRandom sr = new SecureRandom();
		return sr.generateSeed(30);
	}
	private static String getRandomString (){
		SecureRandom sr = new SecureRandom();
		return sr.generateSeed(32).toString();
	}
}
