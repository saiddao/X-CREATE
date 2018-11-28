package it.cnr.isti.labse.xcreate.filler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
//import java.util.Vector;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;
import it.cnr.isti.labse.xcreate.sql.SelectTipi;
import it.cnr.isti.labse.xcreate.util.RequestGenUtil;
import it.cnr.isti.labse.xcreate.util.StringToEnum;
import it.cnr.isti.labse.xcreate.util.Util;

public class SimpleRequestsGenerator {
	private Hashtable<Integer, Tupla> subHashTable;
	private Hashtable<Integer, Tupla> resHashTable;
	private Hashtable<Integer, Tupla> actHashTable;
	private Hashtable<Integer, Tupla> envHashTable;

	private Hashtable<Integer, Tupla> combination;

	private Hashtable<Integer , Hashtable<Integer, Tupla>> combOneWise;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combPairWise;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combThreeWise;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combFourWise;


	private int pkPolicy;
	private int pkNode;
	private Connection mySqlConnection;
	private int numberOfRequests;
	private Hashtable<String, Integer> tipiCom;
	private File simpleCombDir;
	private File combDir;
	//private int pkOneWise, pkPairWise, pkThreWise, pkFourWise;
	
	/*
	 * fixed bug :: request name
	 *  19 Aprile 2013
	 */
	private String wise;

	public SimpleRequestsGenerator(Connection mySqlConnection1, int reqQuanti, int pkPolicy, int pkNodo, File simpleCombDir) {
		this.mySqlConnection = mySqlConnection1;
		this.numberOfRequests = reqQuanti; //arriva da interfaccia
		this.pkPolicy = pkPolicy;
		this.pkNode = pkNodo;
		this.simpleCombDir = simpleCombDir;

		this.subHashTable = new Hashtable<Integer, Tupla>();
		this.resHashTable = new Hashtable<Integer, Tupla>();
		this.actHashTable = new Hashtable<Integer, Tupla>();
		this.envHashTable = new Hashtable<Integer, Tupla>();

		this.combination = new Hashtable<Integer, Tupla>();

		this.combOneWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combPairWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combThreeWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combFourWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();

		this.tipiCom = SelectTipi.getTipiCombinazione();
		//setPkWises();

	}
	/*
	 * se il nodo e' il nodo radice
	 * allora 
	 * 	recupero le combinazioni
	 * 	recupero le tuple riferite da ogni combinazione 
	 * 	genero, per ogni combinazione una richieta 
	 * 	salvo le richieste nella cartella opportuna
	 * 	
		IdTupla AttributeValue AttributeId DataType Issuer SubjectCategory PK_Tupla
		FK_TipoTupla FK_Nodo NomeTipoTupla NomeTipoCombinazione PK_Combinazione 	
	 * 
	 */
	public File generatesRequests(){
		try {
			int idTupla; 
			String attributeValue; 
			String attributeId;
			String dataType;
			String issuer;	
			String subjectCategory;	
			int pkTupla;
			int fkTipoTupla;	// li stampava solo per debug
			int fkNodo;
			String nomeTipoTupla;	
			String nomeTipoCombinazione;	
			int pkCombinazione;

			String querySQL = SelectSQL.selectTupleFromSimpleCombination(this.pkPolicy, this.pkNode);

			Statement stmt;

			stmt = this.mySqlConnection.createStatement();

			ResultSet result = stmt.executeQuery(querySQL);
			int count = 0;
			while(result.next()){// || count<this.numberOfRequests){
				count ++;

				nomeTipoCombinazione = result.getString("NomeTipoCombinazione");
				pkCombinazione = result.getInt("PK_Combinazione");
				idTupla = result.getInt("IdTupla");
				attributeValue = result.getString("AttributeValue");
				attributeId = result.getString("AttributeId");
				dataType = result.getString("DataType");
				issuer = result.getString("Issuer");
				subjectCategory = result.getString("SubjectCategory"); //cosa intende?
				pkTupla = result.getInt("PK_Tupla");
				fkTipoTupla = result.getInt("FK_TipoTupla");
				fkNodo = result.getInt("FK_Nodo");
				nomeTipoTupla = result.getString("NomeTipoTupla");
				
//				System.out.println("Combinazione: "+pkCombinazione);
				
				Tupla tupla = new Tupla();
				tupla.setAttributeId(attributeId);
				tupla.setAttributeValue(attributeValue);
				tupla.setDataType(dataType);
				tupla.setIssuer(issuer);
				tupla.setSubjectCategory(subjectCategory);
				tupla.setPkTupla(pkTupla);
				tupla.setTuplaId(String.valueOf(idTupla));
				tupla.setTipoTupla(nomeTipoTupla);

				switch (StringToEnum.valueOf(nomeTipoTupla)) {
				case Subject:
					if (!this.subHashTable.containsKey(tupla.getPkTupla()))
						this.subHashTable.put(tupla.getPkTupla(), tupla);
					break;
				case Resource:
					if (!this.resHashTable.containsKey(tupla.getPkTupla()))
						this.resHashTable.put(tupla.getPkTupla(), tupla);
					break;
				case Action:
					if (!this.actHashTable.containsKey(tupla.getPkTupla()))
						this.actHashTable.put(tupla.getPkTupla(), tupla);
					break;
				case Environment:
					if (!this.envHashTable.containsKey(tupla.getPkTupla()))
						this.envHashTable.put(tupla.getPkTupla(), tupla);
					break;
				default:
					break;
				}

				if(nomeTipoCombinazione.equals(MySQLCons.ONE_WISE)){
					if(!this.combOneWise.containsKey(pkCombinazione))
						this.combOneWise.put(pkCombinazione, new Hashtable<Integer, Tupla>());
					this.combOneWise.get(pkCombinazione).put(tupla.getPkTupla(), tupla);
				}
				if(nomeTipoCombinazione.equals(MySQLCons.PAIR_WISE)){
					if(!this.combPairWise.containsKey(pkCombinazione))
						this.combPairWise.put(pkCombinazione, new Hashtable<Integer, Tupla>());
					this.combPairWise.get(pkCombinazione).put(tupla.getPkTupla(), tupla);
				}
				if(nomeTipoCombinazione.equals(MySQLCons.THREE_WISE)){
					if(!this.combThreeWise.containsKey(pkCombinazione))
						this.combThreeWise.put(pkCombinazione, new Hashtable<Integer, Tupla>());
					this.combThreeWise.get(pkCombinazione).put(tupla.getPkTupla(), tupla);
				}
				if(nomeTipoCombinazione.equals(MySQLCons.FOUR_WISE)){
					if(!this.combFourWise.containsKey(pkCombinazione))
						this.combFourWise.put(pkCombinazione, new Hashtable<Integer, Tupla>());
					this.combFourWise.get(pkCombinazione).put(tupla.getPkTupla(), tupla);
				}
				
//				System.out.println(	"  "+idTupla+"  "+attributeValue+"  "+attributeId+"  "+dataType+"   "+
//						issuer+"  "+subjectCategory+"  "+pkTupla+"  "+fkTipoTupla+"  "+fkNodo+"  "+
//						nomeTipoTupla+"  "+nomeTipoCombinazione+"  "+pkCombinazione);
				
			}

//			System.out.println("resultSize() : "+count);
//			System.out.println(" numero di richieste da generare : "+this.numberOfRequests);
//			
//			System.out.println(" dimensione ComBPair  :"+this.combPairWise.size());
//			System.out.println(" dimensione ComBThree :"+this.combThreeWise.size());
//			System.out.println(" dimensione ComBONE   :"+this.combOneWise.size());
//			System.out.println(" dimensione ComBFOUR  :"+this.combFourWise.size());
			//System.out.println("CONTENUTO PAIR  : "+this.combPairWise);
			//System.out.println("CONTENUTO THREE : "+this.combThreeWise);

//			System.out.println(" dimensione subHash : "+this.subHashTable.size());
//			System.out.println(" dimensione resHash : "+this.resHashTable.size());
//			System.out.println(" dimensione actHash : "+this.actHashTable.size());
//			System.out.println(" dimensione envHash : "+this.envHashTable.size());
				

			generates();
			result.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.combDir;
	}
	/*
	 *GENERA LE RICHIESTE XACML
	 * GENERA UNA RICHIESTA PER OGNI COMBINAZIONE  
	 */
	private void generates() {
		try {
			Enumeration<Integer> combKeys;
			if(this.simpleCombDir.getName().equals(GuiCons.HIER_SIMPLE_COMB_DIR_NAME)){
				this.combDir = new File(this.simpleCombDir.getCanonicalFile()+GuiCons.DIR_SEPARATOR+"pkSubtreeRootNode_"+String.valueOf(this.pkNode));
				if(!this.combDir.exists())
					this.combDir.mkdirs();
			}
			else{
				this.combDir = this.simpleCombDir;
			}

			BitSet pattern = new BitSet(4); //inizializzati tutti e 4 a FALSE
			pattern.clear();

			if(this.subHashTable.size() == 0 )
				pattern.set(0); //setta questo bit a TRUE
			if(this.resHashTable.size() == 0 )
				pattern.set(1);
			if(this.actHashTable.size() == 0 )
				pattern.set(2);
			if(this.envHashTable.size() == 0 )
				pattern.set(3);

			int caso = Util.patternForRequestGenerator(pattern);
			/*
			 * abbiamo un solo tipo di tupla
			 */
			if( (caso >= 1) && (caso <= 4)){
				// ONEWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.ONE_WISE;
				int reqGeneretedCount = 0;
				combKeys = this.combOneWise.keys();
				int [] keysOne = new int [combOneWise.size()];
				int f =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysOne[f]= combKeyappoggio;
					f++;
				}
				Arrays.sort(keysOne);
				for (int j =0; j<keysOne.length; j++){
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combOneWise.get(keysOne[j]);
						createsSimpleRequest(this.combination, keysOne[j]);
						reqGeneretedCount++;
					}else 
						break;
				}/*
				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combOneWise.get(combKey);
					createsSimpleRequest(this.combination, combKey);
					reqGeneretedCount++;
				}*/
			}
			/*
			 * abbiamo solo due tipi di tupla 
			 * pairWise
			 */
			if((caso > 4) && (caso <= 10)){
				// PAIRWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.PAIR_WISE;
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				int [] keysPair = new int [combPairWise.size()];
				int f =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysPair[f]= combKeyappoggio;
					f++;
				}
				Arrays.sort(keysPair);
				for (int j =0; j<keysPair.length; j++){
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combPairWise.get(keysPair[j]);
						createsSimpleRequest(this.combination, keysPair[j]);
						reqGeneretedCount++;
					}else 
						break;
				}
			}
			/*
			 * abbiamo tre tipi di tupla
			 * threeWise
			 */
			if(caso >= 11){
				// PAIRWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.PAIR_WISE;
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				int [] keysPair = new int [combPairWise.size()];
				int f =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysPair[f]= combKeyappoggio;
					f++;
				}
//				for(int i = 0; i < keysPair.length; i++)
//					System.out.println(keysPair[i]+ " -------------------------------------");
				Arrays.sort(keysPair);
				for (int j =0; j<keysPair.length; j++){
//					System.out.println(keysPair[j]+ " �������������������������������������");
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combPairWise.get(keysPair[j]);
						createsSimpleRequest(this.combination, keysPair[j]);
						reqGeneretedCount++;
					}else 
						break;
				}
				// THREEWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.THREE_WISE;
				combKeys = this.combThreeWise.keys();
				int [] keysThree = new int [combThreeWise.size()];
				int g =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysThree[g]= combKeyappoggio;
					g++;
				}
//				for(int i = 0; i < keysThree.length; i++)//elimina
//					System.out.println(keysThree[i]+ " -------------------------------------");//elimina
				Arrays.sort(keysThree);
				for (int j =0; j<keysThree.length; j++){
//					System.out.println(keysThree[j]+ " �������������������������������������");//elimina
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combThreeWise.get(keysThree[j]);
						createsSimpleRequest(this.combination, keysThree[j]);
						reqGeneretedCount++;
					}
					else 
						break;
/*				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combThreeWise.get(combKey);
					createsSimpleRequest(this.combination, combKey);
					reqGeneretedCount++;
				}*/
				}
			}
			/*
			 *  abbiamo tutte e quattro le tuple
			 *  pairWise
			 *  threeWise
			 *  fourWise
			 */
			if (caso == 15){
				// PAIRWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.PAIR_WISE;
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				int [] keysPair = new int [combPairWise.size()];
				int f =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysPair[f]= combKeyappoggio;
					f++;
				}
				Arrays.sort(keysPair);
				for (int j =0; j<keysPair.length; j++){
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combPairWise.get(keysPair[j]);
						createsSimpleRequest(this.combination, keysPair[j]);
						reqGeneretedCount++;
					}else 
						break;
				}
				// THREEWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.THREE_WISE;
				combKeys = this.combThreeWise.keys();
				int [] keysThree = new int [combThreeWise.size()];
				int g =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysThree[g]= combKeyappoggio;
					g++;
				}
				Arrays.sort(keysThree);
				for (int j =0; j<keysThree.length; j++){
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combThreeWise.get(keysThree[j]);
						createsSimpleRequest(this.combination, keysThree[j]);
						reqGeneretedCount++;
					}
					else 
						break;
				}
				// FOURWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.FOUR_WISE;
				combKeys = this.combFourWise.keys();	
				int [] keysFour = new int [combFourWise.size()];
				int h =0;
				while (combKeys.hasMoreElements()) {
					Integer combKeyappoggio = (Integer) combKeys.nextElement();
					keysFour[h]= combKeyappoggio;
					h++;
				}
				Arrays.sort(keysFour);
				for (int j =0; j<keysFour.length; j++){
					if(reqGeneretedCount < this.numberOfRequests){
						this.combination = this.combFourWise.get(keysFour[j]);
						createsSimpleRequest(this.combination, keysFour[j]);
						reqGeneretedCount++;
					}
					else 
						break;
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// data una combinazione crea la corrispondente richiesta
	private String createsSimpleRequest(Hashtable<Integer, Tupla> comb, Integer combKey) {

		Tupla subTupla = null;
		Tupla resTupla = null;
		Tupla actTupla = null;
		Tupla envTupla = null;
		Enumeration<Integer> tupleKeys = comb.keys();
		while (tupleKeys.hasMoreElements()) {
			Integer tuplaKey = (Integer) tupleKeys.nextElement();
			Tupla tupla = comb.get(tuplaKey);
			//System.out.println(" "+combKey+" "+tupla.getTipoTupla());
			switch (StringToEnum.valueOf(tupla.getTipoTupla())) {
			case Subject:
				subTupla = tupla;
				break;
			case Resource:
				resTupla = tupla;
				break;
			case Action:
				actTupla = tupla;
				break;
			case Environment:
				envTupla = tupla;
				break;
			default:
				break;
			}
		}
		//System.out.println(RequestGenUtil.generateSimpleCombReq(subTupla, resTupla, actTupla, envTupla));
		saveRequest(RequestGenUtil.makeNewSimpleRequestName(this.wise, combKey, subTupla, resTupla, actTupla, envTupla), RequestGenUtil.generateSimpleCombReq(subTupla, resTupla, actTupla, envTupla));
		return RequestGenUtil.generateSimpleCombReq(subTupla, resTupla, actTupla, envTupla);
	}

	/*
	private void setPkWises() {
		this.pkOneWise = this.tipiCom.get(MySQLCons.ONE_WISE);
		this.pkPairWise = this.tipiCom.get(MySQLCons.PAIR_WISE);
		this.pkThreWise = this.tipiCom.get(MySQLCons.THREE_WISE);
		this.pkFourWise = this.tipiCom.get(MySQLCons.FOUR_WISE);
	}
	 */

//	private String makeRequestName(Integer combKey, Tupla subTupla, Tupla resTupla,
//			Tupla actTupla, Tupla envTupla) {
//		StringBuilder reqName = new StringBuilder();
//		reqName.append(combKey);
//		reqName.append("_");
//		reqName.append((subTupla == null)? "Null" : subTupla.getPkTupla());
//		reqName.append("_");
//		reqName.append((resTupla == null)? "Null" : resTupla.getPkTupla());
//		reqName.append("_");
//		reqName.append((actTupla == null)? "Null" : actTupla.getPkTupla());
//		reqName.append("_");
//		reqName.append((envTupla == null)? "Null" : envTupla.getPkTupla());
//		return reqName.toString();
//	}
	public void saveRequest(String requestName, String request){
		try {	
			BufferedWriter requestWriter;
			File req = new File(this.combDir.getCanonicalFile()+File.separator+requestName+".xml");
			//System.out.println(req.getCanonicalPath());
			if(req.exists())
				req.delete();
			req.createNewFile();

			requestWriter = new BufferedWriter(new FileWriter(req.getCanonicalPath()));
			requestWriter.write(request);
			requestWriter.flush();
			requestWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
