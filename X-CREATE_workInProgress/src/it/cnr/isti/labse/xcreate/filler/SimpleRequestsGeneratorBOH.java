package it.cnr.isti.labse.xcreate.filler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;
import it.cnr.isti.labse.xcreate.sql.SelectTipi;
import it.cnr.isti.labse.xcreate.sql.TableAttributeNamesCons;
import it.cnr.isti.labse.xcreate.util.RequestGenUtil;
import it.cnr.isti.labse.xcreate.util.StringToEnum;
import it.cnr.isti.labse.xcreate.util.Util;

public class SimpleRequestsGeneratorBOH {
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

	public SimpleRequestsGeneratorBOH(Connection mySqlConnection1, int reqQuanti, int pkPolicy, int pkNodo, File simpleCombDir) {
		this.mySqlConnection = mySqlConnection1;
		this.numberOfRequests = reqQuanti;
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
			int fkTipoTupla;	
			int fkNodo;
			String nomeTipoTupla;	
			String nomeTipoCombinazione;	
			int pkCombinazione;
			
// questa interrogazione non e' quella corretta per questo!!!
			String querySQL = SelectSQL.selectTupleFromSimpleCombination(this.pkPolicy, this.pkNode);

			Statement stmt;

			stmt = this.mySqlConnection.createStatement();

			ResultSet result = stmt.executeQuery(querySQL);
			int count = 0;
			while(result.next()){
				count ++;

				nomeTipoCombinazione = result.getString("NomeTipoCombinazione");
				pkCombinazione = result.getInt("PK_Combinazione");		
				idTupla = result.getInt("IdTupla");
				attributeValue = result.getString("AttributeValue");
				attributeId = result.getString("AttributeId");
				dataType = result.getString("DataType");
				issuer = result.getString("Issuer");
				subjectCategory = result.getString("SubjectCategory");
				pkTupla = result.getInt("PK_Tupla");
				fkTipoTupla = result.getInt("FK_TipoTupla");
				fkNodo = result.getInt("FK_Nodo");
				nomeTipoTupla = result.getString("NomeTipoTupla");
				
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
				/*
				System.out.println(	"  "+idTupla+"  "+attributeValue+"  "+attributeId+"  "+dataType+"   "+
						issuer+"  "+subjectCategory+"  "+pkTupla+"  "+fkTipoTupla+"  "+fkNodo+"  "+
						nomeTipoTupla+"  "+nomeTipoCombinazione+"  "+pkCombinazione);
				 */
			}

			//System.out.println("resultSize() : "+count);
			//System.out.println(" numero di richieste da generare : "+this.numberOfRequests);
			
			System.out.println(" dimensione ComBPair  :"+this.combPairWise.size());
			System.out.println(" dimensione ComBThree :"+this.combThreeWise.size());
			System.out.println(" dimensione ComBONE   :"+this.combOneWise.size());
			System.out.println(" dimensione ComBFOUR  :"+this.combFourWise.size());
			//System.out.println("CONTENUTO PAIR  : "+this.combPairWise);
			//System.out.println("CONTENUTO THREE : "+this.combThreeWise);

			System.out.println(" dimensione subHash : "+this.subHashTable.size());
			System.out.println(" dimensione resHash : "+this.resHashTable.size());
			System.out.println(" dimensione actHash : "+this.actHashTable.size());
			System.out.println(" dimensione envHash : "+this.envHashTable.size());
			 

			generates();
			result.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.combDir;
	}
	/*
	 *GENERA LE RICHIESTE XACML
	 * GENERA UNA RICHIESTA PER OGNI COMBINAZIONE  
	 */
	private void generates() {
		// TODO Auto-generated method stub
		try {
			Enumeration<Integer> combKeys;
			Integer keys[];
			
			if(this.simpleCombDir.getName().equals(GuiCons.HIER_SIMPLE_COMB_DIR_NAME)){
				this.combDir = new File(this.simpleCombDir.getCanonicalFile()+GuiCons.DIR_SEPARATOR+"pkSubtreeRootNode_"+String.valueOf(this.pkNode));
				if(!this.combDir.exists())
					this.combDir.mkdirs();
			}
			else{
				this.combDir = this.simpleCombDir;
			}

			BitSet pattern = new BitSet(4);
			pattern.clear();

			if(this.subHashTable.size() == 0 )
				pattern.set(0);
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
				//reqOneWiseDir = new File(policyPath+"reqOneWise");reqOneWiseDir.mkdir();
				// ONEWISE
//				int reqGeneretedCount = 0;
//				combKeys = this.combOneWise.keys();
//				System.out.println(combKeys.toString());
//				keys = new Integer[combOneWise.size()];
//				
//				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					this.combination = this.combOneWise.get(combKey);
//					createsSimpleRequest(reqGeneretedCount,combKey, this.combination);
//					reqGeneretedCount++;
//				}
				int reqGeneretedCount = 0;
				combKeys = this.combOneWise.keys();
				keys = new Integer[combOneWise.size()];
				System.out.println("1-WISE "+keys.length);
				int min , max;
				min = max = 0;
				boolean first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				reqGeneretedCount = 0;
				int i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combOneWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
			}
			/*
			 * abbiamo solo due tipi di tupla 
			 * pairWise
			 */
			if((caso > 4) && (caso <= 10)){
				//reqPairWiseDir = new File(policyPath+"reqPairWise");reqPairWiseDir.mkdir();
				// PAIRWISE
//				int reqGeneretedCount = 0;
//				combKeys = this.combPairWise.keys();
//				System.out.println(this.combPairWise.size());
//				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					this.combination = this.combPairWise.get(combKey);
//					createsSimpleRequest(reqGeneretedCount,combKey,this.combination);
//					reqGeneretedCount++;
//				}
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
		
				keys = new Integer[combPairWise.size()];
				
				
				System.out.println("2-WISE "+keys.length);
				int min , max;
				min = max = 0;
				boolean first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				reqGeneretedCount = 0;
				int i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combPairWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
			}
			/*
			 * abbiamo tre tipi di tupla
			 * threeWise
			 */
			if((caso >= 11) && (caso < 15)){
				//System.out.println("caso 3 abbiamo tre tipi di tupla "+caso);
				//reqThreeWiseDir = new File(policyPath+"reqThreeWise");reqThreeWiseDir.mkdir();
				// PAIRWISE
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				
				keys = new Integer[combPairWise.size()];
				System.out.println("2-WISE "+keys.length);
				int min , max;
				min = max = 0;
				boolean first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				reqGeneretedCount = 0;
				int i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combPairWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}	
				System.out.println("min : "+min+" max : "+max);
				// THREEWISE
				System.out.println("3-WISE");
				combKeys = this.combThreeWise.keys();
				first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combThreeWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
//				while (combKeys.hasMoreElements() ) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					reqGeneretedCount++;
//					System.out.println("reqGeneretedCount : "+reqGeneretedCount+" combKey :"+combKey);
//				}
//				
//				combKeys = this.combThreeWise.keys();
//				
//				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					this.combination = this.combThreeWise.get(combKey);
//					createsSimpleRequest(reqGeneretedCount,combKey,this.combination);
//					reqGeneretedCount++;
//				}
			}
			/*
			 *  abbiamo tutte e quattro le tuple
			 *  pairWise
			 *  threeWise
			 *  fourWise
			 */
			if (caso == 15){
				// PAIRWISE
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				
				keys = new Integer[combPairWise.size()];
				System.out.println("2-WISE "+keys.length);
				int min , max;
				min = max = 0;
				boolean first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				reqGeneretedCount = 0;
				int i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combPairWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
				// THREEWISE
				System.out.println("3-WISE");
				combKeys = this.combThreeWise.keys();
				first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combThreeWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
				// FOURWISE
				System.out.println("4-WISE");
				combKeys = this.combFourWise.keys();
				first = true;
				while (combKeys.hasMoreElements() ) {
					Integer combKey = (Integer) combKeys.nextElement();
					if(first){
						min = combKey.intValue();
						max = combKey.intValue();
						first = false;
					}				
					if(combKey.intValue() < min)
						min = combKey.intValue();
					if(combKey.intValue() > max)
						max = combKey.intValue();
				}
				i = min;
				while(i <= max && reqGeneretedCount < this.numberOfRequests){
					this.combination = this.combFourWise.get(i);
					createsSimpleRequest(reqGeneretedCount,i,this.combination);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
				
				//int reqGeneretedCount = 0;
//				combKeys = this.combPairWise.keys();
//				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					this.combination = this.combPairWise.get(combKey);
//					createsSimpleRequest(reqGeneretedCount,combKey,this.combination);
//					reqGeneretedCount++;
//				}
//				// THREEWISE
//				combKeys = this.combThreeWise.keys();
//				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					this.combination = this.combThreeWise.get(combKey);
//					createsSimpleRequest(reqGeneretedCount,combKey,this.combination);
//					reqGeneretedCount++;
//				}
//				// FOURWISE
//				combKeys = this.combFourWise.keys();
//				while (combKeys.hasMoreElements() && reqGeneretedCount < this.numberOfRequests) {
//					Integer combKey = (Integer) combKeys.nextElement();
//					this.combination = this.combFourWise.get(combKey);
//					createsSimpleRequest(reqGeneretedCount,combKey,this.combination);
//					reqGeneretedCount++;
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  data una combinazione crea la corrispondente richiesta
	 * @param combKey2
	 * @param combKey
	 * @param comb
	 * @return
	 */
	private String createsSimpleRequest(Integer combKey2, Integer combKey, Hashtable<Integer, Tupla> comb) {
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
//		System.out.println(RequestGenUtil.generateSimpleCombReq(subTupla, resTupla, actTupla, envTupla));
		saveRequest(makeRequestName(combKey2, combKey, subTupla, resTupla, actTupla, envTupla), RequestGenUtil.generateSimpleCombReq(subTupla, resTupla, actTupla, envTupla));
		return RequestGenUtil.generateSimpleCombReq(subTupla, resTupla, actTupla, envTupla);
	}

	/*
	private void setPkWises() {
		// TODO Auto-generated method stub
		this.pkOneWise = this.tipiCom.get(MySQLCons.ONE_WISE);
		this.pkPairWise = this.tipiCom.get(MySQLCons.PAIR_WISE);
		this.pkThreWise = this.tipiCom.get(MySQLCons.THREE_WISE);
		this.pkFourWise = this.tipiCom.get(MySQLCons.FOUR_WISE);
	}
	 */

	private String makeRequestName(Integer combKey2, Integer combKey, Tupla subTupla, Tupla resTupla,
			Tupla actTupla, Tupla envTupla) {
		// TODO Auto-generated method stub
		StringBuilder reqName = new StringBuilder();
		reqName.append(String.valueOf(combKey.intValue())+"_");
		reqName.append(String.valueOf(combKey2.intValue())+"_");
		reqName.append((subTupla == null)? "Null" : subTupla.getPkTupla());
		reqName.append("_");
		reqName.append((resTupla == null)? "Null" : resTupla.getPkTupla());
		reqName.append("_");
		reqName.append((actTupla == null)? "Null" : actTupla.getPkTupla());
		reqName.append("_");
		reqName.append((envTupla == null)? "Null" : envTupla.getPkTupla());
		System.out.println(reqName.toString());
		return reqName.toString();
	}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo per sapere quanti e quali tipi di tupla ho per questa politica
	 */
	
	private int getTipiTupla(){
		
		boolean sub = false;
		boolean res = false;
		boolean act = false;
		boolean env = false;
		int caso = 0;
		String nomeTipoTupla;
		BitSet pattern = new BitSet(4);
		pattern.clear();
		
		try {
			String queryTipi = "SELECT XT.FK_TipoTupla, XTT.NomeTipoTupla" +
					"FROM XcreatePolitiche XP, XcreateNodi XN, XcreateTuple XT, XcreateTipiTupla XTT" +
					"WHERE XP.PK_Politica = "+this.pkPolicy+" AND " +
					"XN.FK_Politica = XP.PK_Politica AND" +
					"XN.PK_Nodo = "+this.pkNode+" AND " +
					"XN.PK_Nodo = XT.FK_Nodo AND " +
					"XT.FK_TipoTupla = XTT.PK_TipoTupla" +
					"GROUP BY FK_TipoTupla";
			Statement stmt = this.mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(queryTipi);
			
			while(result.next()){
				nomeTipoTupla = result.getString("NomeTipoTupla");
			//ora le scorro e memorizzo poi col pattern vedo in quale caso sono 
				// bastano dei booleani: se c'e'...non c'e'
				switch (StringToEnum.valueOf(nomeTipoTupla)) {
				case Subject:
					sub = true;
					break;
				case Resource:
					res = true;
					break;
				case Action:
					act = true;
					break;
				case Environment:
					env = true;
					break;
				}
			}
			if(sub == false )
				pattern.set(0);
			if(res == false )
				pattern.set(1);
			if(act == false )
				pattern.set(2);
			if(env == false )
				pattern.set(3);
			
			caso = Util.patternForRequestGenerator(pattern);
			// io finirei qui il metodo con return int!
			result.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return caso;
	}
	
}
