package it.cnr.isti.labse.xcreate.filler;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;
import it.cnr.isti.labse.xcreate.sql.TableAttributeNamesCons;
import it.cnr.isti.labse.xcreate.util.MultiRequestGenUtil;
import it.cnr.isti.labse.xcreate.util.RequestGenUtil;
import it.cnr.isti.labse.xcreate.util.StringToEnum;
import it.cnr.isti.labse.xcreate.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class MultiplesRequestsGenerator {
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
	private File multipleCombDir;
	private File combDir;
	/*
	 * fixed bug :: request name
	 *  19 Aprile 2013
	 */
	private String wise;

	public MultiplesRequestsGenerator(Connection mySqlConnection1, int reqQuanti, int pkPolicy, int pkNodo, File multipleCombDir){
		this.mySqlConnection = mySqlConnection1;
		this.numberOfRequests = reqQuanti;
		this.pkPolicy = pkPolicy;
		this.pkNode = pkNodo;
		this.multipleCombDir = multipleCombDir;	
		
		this.subHashTable = new Hashtable<Integer, Tupla>();
		this.resHashTable = new Hashtable<Integer, Tupla>();
		this.actHashTable = new Hashtable<Integer, Tupla>();
		this.envHashTable = new Hashtable<Integer, Tupla>();
		
		this.combOneWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combPairWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combThreeWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combFourWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
	}
	
	/*
	 *  se il nodo e' il nodo radice allora 
	 * 	recupero le combinazioni
	 * 	recupero le tuple riferite da ogni combinazione 
	 * 	genero, per ogni combinazione una richieta 
	 * 	salvo le richieste nella cartella opportuna
	 * 	
	 * IdTupla AttributeValue AttributeId DataType Issuer SubjectCategory PK_Tupla
	 * FK_TipoTupla FK_Nodo NomeTipoTupla NomeTipoCombinazione PK_Combinazione 	
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
			//int fkTipoTupla;	
			//int fkNodo;
			String nomeTipoTupla;	
			String nomeTipoCombinazione;	
			int pkCombinazione;

			String querySQL = SelectSQL.selectTupleFromCombination(this.pkPolicy, this.pkNode);
			System.out.println(" faccio le cose per "+this.pkPolicy+" --- "+this.pkNode);

			Statement stmt;
			stmt = this.mySqlConnection.createStatement();
			
			ResultSet result = stmt.executeQuery(querySQL);
			int count = 0;
			while(result.next()){
				count ++;

				idTupla = result.getInt("IdTupla");
				attributeValue = result.getString("AttributeValue");
				attributeId = result.getString("AttributeId");
				dataType = result.getString("DataType");
				issuer = result.getString("Issuer");
				subjectCategory = result.getString("SubjectCategory");
				pkTupla = result.getInt("PK_Tupla");
				//fkTipoTupla = result.getInt("FK_TipoTupla");
				//fkNodo = result.getInt("FK_Nodo");
				nomeTipoTupla = result.getString("NomeTipoTupla");
				nomeTipoCombinazione = result.getString("NomeTipoCombinazione");
				pkCombinazione = result.getInt("PK_Combinazione");

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
			/*	System.out.println(	"  "+idTupla+"  "+attributeValue+"  "+attributeId+"  "+dataType+"   "+
						issuer+"  "+subjectCategory+"  "+pkTupla+"  "+fkTipoTupla+"  "+fkNodo+"  "+
						nomeTipoTupla+"  "+nomeTipoCombinazione+"  "+pkCombinazione);		*/	
			}
			System.out.println(count+ " i cicli fatti...");
			System.out.println(" dimensione ComBPair  :"+this.combPairWise.size());
			System.out.println(" dimensione ComBThree :"+this.combThreeWise.size());
			System.out.println(" dimensione ComBONE   :"+this.combOneWise.size());
			System.out.println(" dimensione ComBFOUR  :"+this.combFourWise.size());

			System.out.println(" dimensione subHash : "+this.subHashTable.size());
			System.out.println(" dimensione resHash : "+this.resHashTable.size());
			System.out.println(" dimensione actHash : "+this.actHashTable.size());
			System.out.println(" dimensione envHash : "+this.envHashTable.size());
			
			generates();
			result.close();
			stmt.close();
		}catch (SQLException e) {
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
			Integer keys[];//<---------------------------------------------------
			
			if(this.multipleCombDir.getName().equals(GuiCons.HIER_SIMPLE_COMB_DIR_NAME)){
				this.combDir = new File(this.multipleCombDir.getCanonicalFile()+GuiCons.DIR_SEPARATOR+"pkSubtreeRootNode_"+String.valueOf(this.pkNode));
				if(!this.combDir.exists())
					this.combDir.mkdirs();
			}
			else{
				this.combDir = this.multipleCombDir;
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
				while (i <= max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combOneWise.get(i);
					createsMultipleRequest(this.combination, i);
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
				// PAIRWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.PAIR_WISE;
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
				while (i <= max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combPairWise.get(i);
					createsMultipleRequest(this.combination, i);
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
				// PAIRWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.PAIR_WISE;
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				//....
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
				//....
				while (i <= max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combPairWise.get(i);
					System.out.println("2-wise combinazione "+i);
					createsMultipleRequest(this.combination, i);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
				// THREEWISE
				System.out.println("3-WISE");
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.THREE_WISE;
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
				while (i <= max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combThreeWise.get(i);
					System.out.println("3-wise combinazione "+i);
					createsMultipleRequest(this.combination, i);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
			}
			/*
			 *  abbiamo tutte e quattro le tuple
			 *  pairWise
			 *  threeWise
			 *  fourWise
			 */
			if (caso == 15){
				//subHashTable;resHashTable;actHashTable;envHashTable;
				// PAIRWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.PAIR_WISE;
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
				
				while (i <=max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combPairWise.get(i);
					createsMultipleRequest(this.combination, i);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
				// THREEWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.THREE_WISE;
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
				
				while (i <= max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combThreeWise.get(i);
					createsMultipleRequest(this.combination, i);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
				// FOURWISE
				// FIXME fixed bug :: for request name
				this.wise = MySQLCons.FOUR_WISE;
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
				while (i <= max && reqGeneretedCount < this.numberOfRequests) {
					//Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combFourWise.get(i);
					createsMultipleRequest(this.combination, i);
					reqGeneretedCount++;
					i++;
				}
				System.out.println("min : "+min+" max : "+max);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	
	/**
	 *  data una combinazione crea la corrispondente richiesta
	 * @param comb la combinazione di Tuple che compongono la richiesta
	 * @param comKey l'identificatifo della combinazione
	 * @return la richiesta XACML sotto forma di stringa
	 */
	private String createsMultipleRequest(Hashtable<Integer, Tupla> comb, Integer combKey) {
		List<Tupla> subs = new ArrayList<Tupla>();
		List<Tupla> ress = new ArrayList<Tupla>();
		List<Tupla> acts = new ArrayList<Tupla>();
		List<Tupla> envs = new ArrayList<Tupla>();
		
		Enumeration<Integer> tupleKeys = comb.keys();
		while (tupleKeys.hasMoreElements()) {
			Integer tuplaKey = (Integer) tupleKeys.nextElement();
			Tupla tupla = comb.get(tuplaKey);
			//System.out.println(" "+combKey+" "+tupla.getTipoTupla());
			switch (StringToEnum.valueOf(tupla.getTipoTupla())) {
			case Subject:
				subs.add(tupla);
				break;
			case Resource:
				ress.add(tupla);
				break;
			case Action:
				acts.add(tupla);
				break;
			case Environment:
				envs.add(tupla);
				break;
			default:
				break;
			}
		}
		/*if(subs.size()>0){
		for (int i = 0; i < subs.size(); i++){
			System.out.println("AAAAAAAA Soggetto "+subs.get(i).getAttributeValue()+" size "+subs.size());
		}}
		if(ress.size()>0){
		for (int i = 0; i < ress.size(); i++){
			System.out.println("Risorsa "+ress.get(i).getAttributeValue());
		}}
		if(acts.size()>0){
		for (int i = 0; i < acts.size(); i++){
			System.out.println("Azzione "+acts.get(i).getAttributeValue());
		}}
		if(envs.size()>0){
		for (int i = 0; i < envs.size(); i++){
			System.out.println("Ambiente "+envs.get(i).getAttributeValue());
		}}*/
		
		
		
		
		// OLD :: ARGENE
//		saveRequest(makeRequestName(combKey),MultiRequestGenUtil.generateMultipleCombReq(subs, ress, acts, envs, combKey));
		
		
		/*
		 * FIXME
		 * FIXME 
		 * aggiunto il 27 Aprile 2012
		 */
		Hashtable<String, String> richieste = MultiRequestGenUtil.generateMultipleCombReq(makeRequestName(combKey), subs, ress, acts, envs, combKey);
		Set<String> keys = richieste.keySet();
		for (String key : keys) {
			saveRequest(RequestGenUtil.makeNewRequestName(this.wise, combKey,key), richieste.get(key));
		}
		
		return MultiRequestGenUtil.generateMultipleCombReq(subs, ress, acts, envs, combKey);
	}
	
	/**
	 * Questo metodo serve per dare il nome alla richiesta che verra' scritta su File-System.
	 * Il tipo di nomenclatura scelto e' quello che corrisponde al valore della combinazione
	 * memorizzato nel DB, in quanto unico.
	 * Percio' viene eseguita un'interrogazione per recuperare il valore alla corrispondente
	 * chiave primaria della combinazione che si sta scrivendo. 
	 * @param combKey l'identificativo della combinazione
	 * @return il nome della richiesta sotto forma di stringa
	 */
	private String makeRequestName(Integer combKey){
		String reqName = new String();
		
		Statement stmt;
		ResultSet result;
		try {
			stmt = this.mySqlConnection.createStatement();
			result = stmt.executeQuery("SELECT XC.ValoreCombinazione FROM "+TableAttributeNamesCons.COMBINAZIONI+" XC WHERE XC.PK_Combinazione = "+combKey+";");
		while(result.next()){
			reqName = result.getString("ValoreCombinazione");
		}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(reqName+" sarebbe il nome del file");
		return reqName;
	}
	
	/**
	 * Questo metodo prende il nome del file e la stringa che sara' il contenuto del file
	 * e lo scrive su file system nel percorso stabilito.
	 * @param requestName il nome del file della richiesta
	 * @param request la richiesta XACML come stringa
	 */
	public void saveRequest(String requestName, String request){
		try {	
			BufferedWriter requestWriter;
			File req = new File(this.combDir.getCanonicalFile()+File.separator+requestName+".xml");
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
