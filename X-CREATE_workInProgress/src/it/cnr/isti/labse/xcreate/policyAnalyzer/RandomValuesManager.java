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
 *
 */
public class RandomValuesManager {
	private static int setSize;
	private static Hashtable<? extends String, ? extends Tupla> tupleAsHashTable;
	private static HashSet<String> attValueSubSet;
	/**
	 * FIXME PROVA
	 */
	// elemento radice : ospita le entita' casuali
	private static TuplaSet rootTuplaSet;
	private static String idRoot;
	// per la gestione dei tipi
	/**
	 * FIXME Tipi gestiti
	 */
	private static boolean stringTypePolicy;
	private static boolean integerTypePolicy;
	private static boolean anyUriPolicy;
	/**
	 * TODO Tipi ancora da gestire
	 */
	/*
	private static boolean booleanTypePolicy;
	private static boolean doubleTypePolicy;
	private static boolean timeTypePolicy;
	private static boolean dateTypePolicy;
	private static boolean dateTimeTypePolicy;
	private static boolean dayTimeDurationTypePolicy;
	private static boolean yearMonthDurationTypePolicy;
	private static boolean hexBinaryTypePolicy;
	private static boolean base64BinaryTypePolicy;
	private static boolean rfc822NameTypePolicy;
	private static boolean x500NameTypePolicy;
	*/
	
	public static void addRamdonValues(Nodo nodoRoot) {
		attValueSubSet = new HashSet<String>();
		setSize = nodoRoot.getCardinalityTuplaSet();
		stringTypePolicy = false;
		integerTypePolicy = false;
		anyUriPolicy = false;
		/*
		booleanTypePolicy = false;
		doubleTypePolicy = false;
		timeTypePolicy = false;
		dateTypePolicy = false;
		dateTimeTypePolicy = false;
		dayTimeDurationTypePolicy = false;
		yearMonthDurationTypePolicy = false;
		hexBinaryTypePolicy = false;
		base64BinaryTypePolicy = false;
		rfc822NameTypePolicy = false;
		x500NameTypePolicy = false;
		*/
		//System.out.println("SetSize : "+setSize);
		tupleAsHashTable = (Hashtable<? extends String, ? extends Tupla>) nodoRoot.getTupleHashTable();
		Enumeration<? extends String> keys = tupleAsHashTable.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			attValueSubSet.add(tupleAsHashTable.get(key).getAttributeValue());
		}
		/*
		 * FIXME PROVA
		 */
		rootTuplaSet = nodoRoot.getTuplaSet();
		idRoot = nodoRoot.getMyId();
		//System.out.println("Random : "+tupleAsHashTable.size());
		//System.out.println("Random :: "+attValueSubSet.size());
		addValues(nodoRoot);
		/*
		 * FIXME PROVA
		 */
		rootTuplaSet.printTuplaSet();
	}

	private static void addValues(Nodo nodo) {
		TuplaSet tuplaSet = nodo.getTuplaSet();
		//System.out.println("TupleSetSize : "+tuplaSet.getSize());
		if(tuplaSet.getSize() != 0)
			addValues(tuplaSet, nodo);
		if(nodo.hasCondition()){
			//System.out.println("Gestire la condition");
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
		boolean stringType = false;
		boolean integerType = false;
		boolean anyUriType = false;
		/*
		boolean booleanType = false;
		boolean doubleType = false;
		boolean timeType = false;
		boolean dateType = false;
		boolean dateTimeType = false;
		boolean dayTimeDurationType = false;
		boolean yearMonthDurationType = false;
		boolean hexBinaryType = false;
		boolean base64BinaryType = false;
		boolean rfc822NameType = false;
		boolean x500NameType = false;
		*/
		Iterator<Tupla> tuplaIterator = tuplaSet.iterator();
		Tupla randomTuplaMeno = null;
		Tupla randomTuplaPiu = null;
		Tupla randomTupla = null;
		while (tuplaIterator.hasNext() && (!stringType || !integerType || !anyUriType)) {
			Tupla tupla = (Tupla) tuplaIterator.next();
			/* 
			 * GESTIONE TIPO STRING
			 */
			if(tupla.getDataType().equals(XacmlDataTypes.STRING) && !stringType){
				stringType = true;
				randomTupla = getClone(tupla);
				randomTupla.setAttributeValue("RandomValue:"+getRandomString()+":"+String.valueOf(++setSize));
				randomTupla.setTuplaId(String.valueOf(setSize));
				//randomTupla.printTupla();
				//tupla.printTupla();
			}
			/*
			 * GESTIONE TIPO INTEGER
			 */
			if(tupla.getDataType().equals(XacmlDataTypes.INTEGER) && !integerType){
				integerType = true;
				randomTuplaMeno = getClone(tupla);
				randomTuplaPiu = getClone(tupla);
				int attrValueContent = Integer.valueOf(tupla.getAttributeValue());
				setSize++;
				randomTuplaMeno.setAttributeValue(String.valueOf(attrValueContent-1));
				randomTuplaMeno.setTuplaId(String.valueOf(setSize));
				setSize++;
				randomTuplaPiu.setAttributeValue(String.valueOf(attrValueContent+1));
				randomTuplaPiu.setTuplaId(String.valueOf(setSize));
				//randomTuplaMeno.printTupla();
				//tupla.printTupla();
				//randomTuplaPiu.printTupla();
			}
			/*
			 * GESTIONE TIPO ANY_URI
			 */
			if(tupla.getDataType().equals(XacmlDataTypes.ANY_URI) && !anyUriType){
				anyUriType = true;
				randomTupla = getClone(tupla);
				randomTupla.setAttributeValue(tupla.getAttributeValue()+"_RandomValue_"+getRandomString()+"_"+String.valueOf(++setSize));
				randomTupla.setTuplaId(String.valueOf(setSize));
			}
		}
		/*
		 * GESTIONE STRING 
		 */
		if(stringType && !stringTypePolicy){
			//tuplaSet.addTupla(randomTupla);
			/*
			 * FIXME PROVA
			 */
			//rootTuplaSet.addTupla(randomTupla);
			stringTypePolicy = true;
			System.out.println("Nodo: "+idRoot+" tupla: "+randomTupla.getTuplaId());
			System.out.println("Nodo: "+n.getMyId()+" tupla: "+randomTupla.getTuplaId());
			tuplaSet.addTupla(randomTupla);
		}
		/*
		 * GESTIONE INTEGER
		 */
		if(integerType && !integerTypePolicy){
			//tuplaSet.addTupla(randomTuplaMeno);
			//tuplaSet.addTupla(randomTuplaPiu);
			/*
			 * FIXME PROVA
			 */
			//rootTuplaSet.addTupla(randomTuplaMeno);
			//rootTuplaSet.addTupla(randomTuplaPiu);
			integerTypePolicy = true;
			System.out.println("Nodo: "+idRoot+" tupla: "+randomTuplaMeno.getTuplaId());
			System.out.println("Nodo: "+idRoot+" tupla: "+randomTuplaPiu.getTuplaId());
			System.out.println("Nodo: "+n.getMyId()+" tupla: "+randomTuplaMeno.getTuplaId()+","+randomTuplaPiu.getTuplaId());
			tuplaSet.addTupla(randomTuplaMeno);
			tuplaSet.addTupla(randomTuplaPiu);
		}
		/*
		 * GESTIONE ANY_URI
		 */
		if(anyUriType && !anyUriPolicy){
			//rootTuplaSet.addTupla(randomTupla);
			anyUriPolicy = true;
			System.out.println("Nodo: "+idRoot+" tupla: "+randomTupla.getTuplaId());
			System.out.println("Nodo: "+n.getMyId()+" tupla: "+randomTupla.getTuplaId());
			tuplaSet.addTupla(randomTupla);
		}
		
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
