package it.cnr.isti.labse.xcreate.filler;

import it.cnr.isti.labse.xcreate.policyAnalyzer.LogicaPolicyAnalyzer;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.util.RequestGenUtil;
import it.cnr.isti.labse.xcreate.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 * TODO DA FARE 
 * VEDERE IL MAIN A RIPORTARE TUTTO IN QUESTA CLASSE ..
 * 
 * CREARE RICHIESTA UNO UNO 
 * 	---- PER OGNI COMBINAZIONE CRARE UNA RICHIESTA
 * CREARE RICHIESTE A PARTIRE DA RICHIESTE INTERMEDIE
 *  --- UTILIZZARE L'APPROCCIO DEFINITO NEL DOCUMENTO DI PARTENZA ..
 *  
 *  
 *  da integrare direttamente con le interrogazioni MySQL :::::
 *  
 *  CAPIRE BENE CHE COSA DOVREBBE FARE 
 */
public class RequestGenerator {
	
	private  LogicaPolicyAnalyzer policyAnalyzer;
	private CombinationsGenerator combGenerator;
	private BitSet pattern;
	
	private Hashtable<String, Tupla> subHashTable;
	private Hashtable<String, Tupla> resHashTable;
	private Hashtable<String, Tupla> actHashTable;
	private Hashtable<String, Tupla> envHashTable;
	
	private Vector<Combinazione> combOneWise;
	private Vector<Combinazione> combPairWise;
	private Vector<Combinazione> combThreeWise;
	private Vector<Combinazione> combFourWise;
	
	private String policyPath;
	private File reqPairWiseDir;
	private File reqThreeWiseDir;
	private File reqOneWiseDir;
	private File reqFourWiseDir;
	

	public RequestGenerator() {
		// TODO Auto-generated constructor stub
	}	


	public int requestGenerator(){
		StringBuilder request;
		String simpleCombReq;
		int caso = Util.patternForRequestGenerator(pattern);
		if (caso != 0){
			subHashTable = policyAnalyzer.getSubjectSetAsHash();
			resHashTable = policyAnalyzer.getResourceSetAsHash();
			actHashTable = policyAnalyzer.getActionSetAsHash();
			envHashTable = policyAnalyzer.getEnvironmentSetAsHash();
		}
		if( (caso >= 1) && (caso <= 4)){
			reqOneWiseDir = new File(policyPath+"reqOneWise");
			reqOneWiseDir.mkdir();
		}
		if(caso > 4){
			reqPairWiseDir = new File(policyPath+"reqPairWise");
			reqPairWiseDir.mkdir();
		}
		if(caso >= 11){
			reqThreeWiseDir = new File(policyPath+"reqThreeWise");
			reqThreeWiseDir.mkdir();
		}

		switch (Util.patternForRequestGenerator(pattern)) {
		// zero insiemi --- case 0
		case 0:
			System.out.println("case 0");
			break;
			/**
			 * abbiamo un solo insieme
			 */
			// subject --- case 1
		case 1:
			System.out.println("case 1");
			System.out.println("SUBJECT");
			combOneWise = combGenerator.getOneWise();
			for (Combinazione combination : combOneWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						null, null, null);
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request.toString());
				/*
				 * Creare un file per ogni richieste 
				 */
				saveRequest(reqOneWiseDir, combination.toString(), request.toString());
			}
			break;
			// resource --- case 2
		case 2:
			System.out.println("case 2 : RESOURCE");
			combOneWise = combGenerator.getOneWise();
			resHashTable = policyAnalyzer.getResourceSetAsHash();
			for (Combinazione combination : combOneWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null,
						resHashTable.get(combination.getRes()),
						null, null);
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request.toString());
				saveRequest(reqOneWiseDir, combination.toString(), request.toString());
			}
			break;
			// action --- case 3
		case 3:
			System.out.println("case 3 : ACTION");
			combOneWise = combGenerator.getOneWise();
			actHashTable = policyAnalyzer.getActionSetAsHash();
			for (Combinazione combination : combOneWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null, null,
						actHashTable.get(combination.getAct()),
						null);
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request.toString());
				saveRequest(reqOneWiseDir, combination.toString(), request.toString());
			}
			break;
			// environment --- case 4
		case 4:
			System.out.println("case 4 : ENVIRONMENT");
			combOneWise = combGenerator.getOneWise();
			envHashTable = policyAnalyzer.getEnvironmentSetAsHash();
			for (Combinazione combination : combOneWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null, null, null,
						envHashTable.get(combination.getEnv()));
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request.toString());
				saveRequest(reqOneWiseDir, combination.toString(), request.toString());

			}
			break;
			/**
			 * abbiamo due insiemi
			 */
			// sub res --- case 5
		case 5:
			System.out.println("case 5 : sub res");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						null, null);
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			break;
			// sub act --- case 6
		case 6:
			System.out.println("case 6 : sub act");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						null,
						actHashTable.get(combination.getAct()),
						null);
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			break;
			// sub env --- case 7
		case 7:
			System.out.println("case 7 : sub env");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						null, null,
						envHashTable.get(combination.getEnv()));
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			break;
			// res act --- case 8
		case 8:
			System.out.println("case 8 : res act");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null,
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						null);
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			break;
			// res env --- case 9
		case 9:
			System.out.println("case 9 : res env");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null,
						resHashTable.get(combination.getRes()),
						null,
						envHashTable.get(combination.getEnv()));
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			break;
			// act env --- case 10
		case 10:
			System.out.println("case 10 : act env");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null,null,
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			break;
			/**
			 * abbiamo tre insiemi
			 */
			// sub res act --- case 11
		case 11:
			System.out.println("Case 11 : sub res act :: \n PairWise");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						null);
				
				
				//System.out.println(combination.toString());
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);

				saveRequest(reqPairWiseDir, combination.toString(), request.toString());
			}
			System.out.println("Case 11 : sub res act :: \n ThreeWise");
			combThreeWise = combGenerator.getThreeWise();
			for (Combinazione combination : combThreeWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						null);
				
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);

				saveRequest(reqThreeWiseDir, combination.toString(), request.toString());
			}
			break;
			// sub res env --- case 12
		case 12:
			System.out.println("Case 12 : sub res env :: \n PairWise");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						null,
						envHashTable.get(combination.getEnv()));
				
				
				
				
				//System.out.println(combination.toString());
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			System.out.println("Case 12 : sub res env :: \n ThreeWise");
			combThreeWise = combGenerator.getThreeWise();
			for (Combinazione combination : combThreeWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						null,
						envHashTable.get(combination.getEnv()));
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());

				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));

				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqThreeWiseDir, combination.toString(), request.toString());

			}


			break;
			// sub act env --- case 13
		case 13:
			System.out.println("13  sub act env");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						null,
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());

			}
			combThreeWise = combGenerator.getThreeWise();
			for (Combinazione combination : combThreeWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						null,
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqThreeWiseDir, combination.toString(), request.toString());
			}

			break;
			// res act env --- case 14
		case 14:
			System.out.println("case 14 : res act env");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null,
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqPairWiseDir, combination.toString(), request.toString());
			}
			combThreeWise = combGenerator.getThreeWise();
			for (Combinazione combination : combThreeWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						null,
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//	System.out.println(request);
				saveRequest(reqThreeWiseDir, combination.toString(), request.toString());
			}

			break;
			// abbiamo tutti e quattro gli insiemi case default  15;
		default:
			reqFourWiseDir = new File(policyPath+"reqFourWise");
			reqFourWiseDir.mkdir();

			System.out.println("defualt : tutti e quattro gli insiemi sono presenti ");
			combPairWise = combGenerator.getPairWise();
			for (Combinazione combination : combPairWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(
						subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);

				saveRequest(reqPairWiseDir, combination.toString(), request.toString());
			}
			combThreeWise = combGenerator.getThreeWise();
			for (Combinazione combination : combThreeWise) {
				simpleCombReq = RequestGenUtil.
				generateSimpleCombReq(subHashTable.get(combination.getSub()),
						resHashTable.get(combination.getRes()),
						actHashTable.get(combination.getAct()),
						envHashTable.get(combination.getEnv()));
				
				
				
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqThreeWiseDir, combination.toString(), request.toString());
			}
			combFourWise = combGenerator.getFourWise();
			for (Combinazione combination : combFourWise) {
				request = new StringBuilder();
				request.append(RequestGenUtil.requestHeader());
				request.append(RequestGenUtil.getSubjectAttributeAsXMLString(
						subHashTable.get(combination.getSub())));
				request.append(RequestGenUtil.getResourceAttributeAsXMLString(
						resHashTable.get(combination.getRes())));
				request.append(RequestGenUtil.getActionAttributeAsXMLString(
						actHashTable.get(combination.getAct())));
				request.append(RequestGenUtil.getEnvironmentAttributeAsXMLString(
						envHashTable.get(combination.getEnv())));
				request.append(RequestGenUtil.requestFooter());
				//System.out.println(request);
				saveRequest(reqFourWiseDir, combination.toString(), request.toString());
			}
			break;
		}
		return 0;
	}

	public void saveRequest(File reqDir, String requestName, String request){
		try {	
			BufferedWriter requestWriter;
			File req = new File(reqDir.getCanonicalFile()+File.separator+requestName+".xml");
			//System.out.println(req.getCanonicalPath());
			req.createNewFile();
			requestWriter = new BufferedWriter(new FileWriter(req.getCanonicalPath()));
			requestWriter.write(request);
			requestWriter.flush();
			requestWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
