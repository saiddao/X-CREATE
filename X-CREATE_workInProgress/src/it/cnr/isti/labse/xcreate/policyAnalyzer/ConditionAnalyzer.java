package it.cnr.isti.labse.xcreate.policyAnalyzer;

/**
 * Questa classe deve servire per analizzare la struttura delle Condition per
 * vedere se qualche Funzione impone dei vincoli di cardinalita'
 * Quindi potra' restituire dei valori da confrontare con i Massimi gia'
 * ottenuti in LogicaPolicyAnalyzer.
 * @author ARGENIX
 *
 */

public class ConditionAnalyzer {
	
	private int NumSub;
	private int NumRes;
	private int NumAct;
	private int NumEnv;
	
	/**
	 * Costruttore
	 */
	public ConditionAnalyzer(){
		NumSub = 0;
		NumRes = 0;
		NumAct = 0;
		NumEnv = 0;
	}
	/*
	 * qui ci sarà tutto quello ce serve per analizzare le funzioni
	 * e settare i valori di S, R, A, E riscontrati
	 * I Massimi...
	 * E i minimi?????
	 */
	
	
	public int getNumSubject(){
		return this.NumSub;
	}
	public int getNumResource(){
		return this.NumRes;
	}
	public int getNumAction(){
		return this.NumAct;
	}
	public int getNumEnvironment(){
		return this.NumEnv;
	}
	
}
