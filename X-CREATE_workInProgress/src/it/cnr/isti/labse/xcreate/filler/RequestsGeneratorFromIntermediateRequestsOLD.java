package it.cnr.isti.labse.xcreate.filler;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;
import it.cnr.isti.labse.xcreate.sql.SelectTipi;
import it.cnr.isti.labse.xcreate.util.StringToEnum;
import it.cnr.isti.labse.xcreate.util.Util;
import it.cnr.isti.labse.xcreate.xQuery.ElementsName;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RequestsGeneratorFromIntermediateRequestsOLD {
	private int pkPolitica; 
	private int pkNodo;
	private File[] intermediateRequests;
	private Hashtable<Integer, Tupla> subHashTable;
	private Hashtable<Integer, Tupla> resHashTable;
	private Hashtable<Integer, Tupla> actHashTable;
	private Hashtable<Integer, Tupla> envHashTable;
	private Hashtable<Integer, Tupla> combination;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combOneWise;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combPairWise;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combThreeWise;
	private Hashtable<Integer , Hashtable<Integer, Tupla>> combFourWise;
	private Connection mySqlConnection;
	private Hashtable<String, Integer> tipiCom;
	private BitSet guardia;
	private HashSet<String> subAttValueSubSet;
	private HashSet<String> resAttValueSubSet;
	private HashSet<String> actAttValueSubSet;
	private HashSet<String> envAttValueSubSet;
	private int indexRequest;
	private File combFromInterDir;
	private File intermediateReqDir;
	private String[] subAttValueSubSetAsArray;
	private int subValueLength;
	private Integer[] subKeysAsArray;
	private int subAsArrayLength;
	private String[] resAttValueSubSetAsArray;
	private int resValueLength;
	private Integer[] resKeysAsArray;
	private int resAsArrayLength;
	private String[] actAttValueSubSetAsArray;
	private int actValueLength;
	private Integer[] actKeysAsArray;
	private int actAsArrayLength;
	private String[] envAttValueSubSetAsArray;
	private int envValueLength;
	private Integer[] envKeysAsArray;
	private int envAsArrayLength;

	public RequestsGeneratorFromIntermediateRequestsOLD(int pkPolitica, int pkNodo, Connection mySqlConnection2, File[] interRequests, File combFromInterDir) {
		// TODO Auto-generated constructor stub
		this.pkNodo = pkNodo;
		this.pkPolitica = pkPolitica;
		this.intermediateRequests = interRequests;
		this.mySqlConnection = mySqlConnection2;
		this.combFromInterDir = combFromInterDir;

		this.subHashTable = new Hashtable<Integer, Tupla>();
		this.resHashTable = new Hashtable<Integer, Tupla>();
		this.actHashTable = new Hashtable<Integer, Tupla>();
		this.envHashTable = new Hashtable<Integer, Tupla>();
		this.combination = new Hashtable<Integer, Tupla>();
		this.combOneWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combPairWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combThreeWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.combFourWise = new Hashtable<Integer, Hashtable<Integer,Tupla>>();
		this.subAttValueSubSet = new HashSet<String>();
		this.resAttValueSubSet = new HashSet<String>();
		this.actAttValueSubSet = new HashSet<String>();
		this.envAttValueSubSet = new HashSet<String>();
		this.guardia = new BitSet(2);
		this.guardia.clear();
		this.indexRequest = 0;		
		this.tipiCom = SelectTipi.getTipiCombinazione();
	}
	public Node getFileAsDom(File filename, boolean validating) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validating);
			Document doc = factory.newDocumentBuilder().parse(filename);
			/*
			 * PERMETTE DI SELEZIONARE IL NODO RADICE
			 * SI UTILIZZA XPATH..SAPPIAMO COSA CERCARE 
			 * DOVE CERCARE E SOPRATTUTTO LA CARDINALITA' DEL RISULTATO
			 * 
			 * PRECONDIZIONE.
			 * TUTTE LE RICHIESTE INTERMEDIE TRATTATE SONO VALIDE RISPETTO ALLO SCHEMA DI RIFERIMENTO 
			 * CONTEXT-SCHEMA
			 */
			NodeList nodelist = XPathAPI.selectNodeList(doc, "/*");
			return nodelist.item(0);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	public void generatesRequests(){
		try {
			int idTupla, pkTupla, pkCombinazione, fkTipoTupla, fkNodo;
			String attributeValue, attributeId, dataType, issuer, subjectCategory, nomeTipoTupla, nomeTipoCombinazione;	
			String querySQL = SelectSQL.selectTupleFromCombination(this.pkPolitica, this.pkNodo);
			Statement stmt = this.mySqlConnection.createStatement();
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
				fkTipoTupla = result.getInt("FK_TipoTupla");
				fkNodo = result.getInt("FK_Nodo");
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
					// costruire il sottoinsieme AttributeValue dell'insieme Subject 
					this.subAttValueSubSet.add(tupla.getAttributeValue());
					break;
				case Resource:
					if (!this.resHashTable.containsKey(tupla.getPkTupla()))
						this.resHashTable.put(tupla.getPkTupla(), tupla);
					this.resAttValueSubSet.add(tupla.getAttributeValue());
					break;
				case Action:
					if (!this.actHashTable.containsKey(tupla.getPkTupla()))
						this.actHashTable.put(tupla.getPkTupla(), tupla);
					this.actAttValueSubSet.add(tupla.getAttributeValue());
					break;
				case Environment:
					if (!this.envHashTable.containsKey(tupla.getPkTupla()))
						this.envHashTable.put(tupla.getPkTupla(), tupla);
					this.envAttValueSubSet.add(tupla.getAttributeValue());
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
			/*
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

				System.out.println("dimensione subAttSubSet "+this.subAttValueSubSet.size());				
				System.out.println("dimensione resAttSubSet "+this.resAttValueSubSet.size());
				System.out.println("dimensione actAttSubSet "+this.actAttValueSubSet.size());
				System.out.println("dimensione envAttSubSet "+this.envAttValueSubSet.size());
			 */
			generatesEIlMetodoGiusto();
			result.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 *GENERA LE RICHIESTE XACML
	 * GENERA UNA RICHIESTA PER OGNI COMBINAZIONE  
	 */
	private void generatesEIlMetodoGiusto() {
		try {
			Enumeration<Integer> combKeys;
			BitSet pattern = new BitSet(4);
			pattern.clear();
			if(this.combFromInterDir.getName().equals(GuiCons.HIER_SIMPLE_COMB_DIR_NAME)){
				this.intermediateReqDir = new File(this.combFromInterDir.getCanonicalFile()+GuiCons.DIR_SEPARATOR+"pkSubtreeRootNode_"+String.valueOf(this.pkNodo));
				if(!this.intermediateReqDir.exists())
					this.intermediateReqDir.mkdirs();
			}else{
				this.intermediateReqDir = this.combFromInterDir;
			}
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
				int reqGeneretedCount = 0;
				combKeys = this.combOneWise.keys();
				while (combKeys.hasMoreElements()) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combOneWise.get(combKey);
					reqGeneretedCount++;
				}
				fillIntermediateRequestFromOneWise();// FIXME RICORDARSI CHE SONO TRATTATI NELLO STESSO MODO 
			}
			/*
			 * abbiamo solo due tipi di tupla 
			 * pairWise
			 */
			if((caso > 4) && (caso <= 10)){
				//reqPairWiseDir = new File(policyPath+"reqPairWise");reqPairWiseDir.mkdir();
				// PAIRWISE
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				System.out.println(this.combPairWise.size());
				while (combKeys.hasMoreElements()) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combPairWise.get(combKey);
					//createsSimpleRequest(this.combination);
					reqGeneretedCount++;
				}
				fillIntermediateRequestFromPairWise();
			}
			/*
			 * abbiamo tre tipi di tupla
			 * threeWise
			 */
			if(caso >= 11){
				System.out.println("caso 3 abbiamo tre tipi di tupla "+caso);
				//reqThreeWiseDir = new File(policyPath+"reqThreeWise");reqThreeWiseDir.mkdir();
				// PAIRWISE
				// genera le richieste a partire dalle richieste intermedie 
				Enumeration<Integer> comb2Wise = this.combPairWise.keys();
				while (comb2Wise.hasMoreElements() && this.indexRequest < this.intermediateRequests.length) {
					int combKey = (Integer) comb2Wise.nextElement().intValue();
					Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
					fillIntermediateRequestFromThreeWise(combKey, requestAsDom, this.combPairWise );
					printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
					this.indexRequest++;
				}
				Enumeration<Integer> comb3Wise = this.combThreeWise.keys();
				while (comb2Wise.hasMoreElements() && this.indexRequest < this.intermediateRequests.length) {
					Integer combKey = (Integer) comb3Wise.nextElement();
					Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
					fillIntermediateRequestFromThreeWise(combKey, requestAsDom, this.combPairWise );
					printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
					this.indexRequest++;
				}
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
				int reqGeneretedCount = 0;
				combKeys = this.combPairWise.keys();
				while (combKeys.hasMoreElements()) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combPairWise.get(combKey);
					reqGeneretedCount++;
				}
				// THREEWISE
				combKeys = this.combThreeWise.keys();
				while (combKeys.hasMoreElements()) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combThreeWise.get(combKey);
					reqGeneretedCount++;
				}
				// FOURWISE
				combKeys = this.combFourWise.keys();
				while (combKeys.hasMoreElements()) {
					Integer combKey = (Integer) combKeys.nextElement();
					this.combination = this.combFourWise.get(combKey);
					reqGeneretedCount++;
				}
				fillIntermediateRequestFromFourWise();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void fillIntermediateRequestFromFourWise() {
		// TODO Auto-generated method stub
	}
	private void fillIntermediateRequestFromPairWise() {
		// TODO Auto-generated method stub
	}
	private void fillIntermediateRequestFromOneWise() {
		// TODO Auto-generated method stub
	}
	private void fillIntermediateRequestFromThreeWise(int combIndex, Node reqNode, Hashtable<Integer, Hashtable<Integer, Tupla>> combNWise) {
		initializeVariables();
		NodeList childrenEntityList = reqNode.getChildNodes();
		Hashtable<Integer, Tupla> combinazione = combNWise.get(combIndex);
		//System.out.println("Combinazione : "+combinazione.toString());
		Tupla sub = null;
		Tupla res = null;
		Tupla act = null;
		Tupla env = null;
		Enumeration<Integer> tupleKeys = combinazione.keys();
		while (tupleKeys.hasMoreElements()) {
			Integer tuplaKey = (Integer) tupleKeys.nextElement();
			Tupla tupla = combinazione.get(tuplaKey);
			//System.out.println(" "+combKey+" "+tupla.getTipoTupla());
			switch (StringToEnum.valueOf(tupla.getTipoTupla())) {
			case Subject:
				sub = tupla;
				break;
			case Resource:
				res = tupla;
				break;
			case Action:
				act = tupla;
				break;
			case Environment:
				env = tupla;
				break;
			default:
				break;
			}
		}
		Node childEntity;
		for (int i = 0; i < childrenEntityList.getLength(); i++) {
			childEntity = childrenEntityList.item(i);
			if(childEntity.getNodeName().contains(String.valueOf(ElementsName.Subject))){
				if(sub != null){
					System.out.println("Siamo nel nodo Subject "+childEntity.getNodeName());
					/*
					 *  e' la prima volta che incontro il nodo Subject
					 *  quindi mi riferisco alla combinazione identifica dal parametro combIndex
					 */
					if(!this.guardia.get(0) && !this.guardia.get(1)){
						fillFirstSubject(sub, childEntity);
					}else{
						/* HO INCONTRATO UN NUOVO SUBJECT
						 * altrimenti mi riferisco ad una entita' SUBJECT diversa da 
						 * quelle utilizzate per popolare questa richiesta 
						 */
						System.out.println("Ho gia' incontrato un nodo Subject");
						System.out.println("quanti sub non ancora utlizzati  :::: "+subAsArrayLength);
						if(subAsArrayLength > 0){ 
							fillNewSubject(childEntity);
						}
					}
				}else{
					/* subject e' null .. significa che la richiesta non comprendera' alcun subject..
					 * cambio la guardia 
					 */
					this.guardia.set(1);
				}
			}// fine elemento subject
			if(childEntity.getNodeName().contains(String.valueOf(ElementsName.Resource))){
				if(res != null){
					System.out.println("Siamo nel nodo Resource"+childEntity.getNodeName());
					/*
					 *  e' la prima volta che incontro il nodo Resource
					 *  quindi mi riferisco alla combinazione identifica dal parametro combIndex
					 */
					if(!this.guardia.get(0) && this.guardia.get(1))
						fillFirstResource(res, childEntity);

				}else {
					/*
					 * altrimenti mi riferisco ad una entita' resource diversa da 
					 * quelle utilizzate per popolare questa richiesta 
					 */
					if(resAsArrayLength > 0){ 
						fillNewResource(childEntity);
					}
				}
			}else{				}
			if(childEntity.getNodeName().contains(String.valueOf(ElementsName.Action))){
				if(act != null){
					System.out.println("Siamo nel nodo Action "+childEntity.getNodeName());
					/*
					 *  e' la prima volta che incontro il nodo Action
					 *  quindi mi riferisco alla combinazione identifica dal parametro combIndex
					 *  NOTARE CHE CI PUO' ESSERE UN SOLO NODO ACTION IN UNA RICHIESTA
					 */
					if(this.guardia.get(0) && this.guardia.get(1))
						fillAction(act, childEntity);
				}else{
					/*
					 *  action e' null .. devo consentire la visita di environment
					 *  cambio la guardia
					 */
					this.guardia.clear(1);
				}
			}

			if(childEntity.getNodeName().contains(String.valueOf(ElementsName.Environment))){
				if(env != null ){
					System.out.println("Siamo nel nodo Environment "+childEntity.getNodeName());
					/*
					 *  e' la prima volta che incontro il nodo Environment
					 *  quindi mi riferisco alla combinazione identifica dal parametro combIndex
					 *  Notare che possiamo inontrare il nodo Environment solo una volta
					 */
					System.out.println(this.guardia.get(0) +" "+this.guardia.get(1));
					if(this.guardia.get(0) && !this.guardia.get(1))
						fillEnvironment(env, childEntity);
				}else{}
			}
		}
	}


	private void fillEnvironment(Tupla env, Node childEntity) {
		for (int j = 0; j < envAsArrayLength; j++) {
			if(envKeysAsArray[j] == env.getPkTupla()){
				envKeysAsArray[j] = envKeysAsArray[--envAsArrayLength];
				System.out.println();
				break;
			}
		}
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo attribute figlio del nodo environment
				 * allora mi riferisco alle informazioni nell'environment della combinazione
				 */
				if(this.guardia.get(0) && !this.guardia.get(1)){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, env);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco ad env
							 */
							if(this.guardia.get(0) && !this.guardia.get(1)){
								childAttributeValue.setTextContent(env.getAttributeValue());
								for (int l = 0; l < envValueLength; l++) {
									if(envAttValueSubSetAsArray[l].equals(env.getAttributeValue())) {
										envAttValueSubSetAsArray[l] = envAttValueSubSetAsArray[--envValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								this.guardia.clear(0);
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di env non utilizzato per popolare la presente richiesta
								 */
								if(envValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(envValueLength);
									childAttributeValue.setTextContent(envAttValueSubSetAsArray[indexAttValue]);
									envAttValueSubSetAsArray[indexAttValue] = envAttValueSubSetAsArray[--envValueLength];
								}
							}
					}
				}else{
					/*
					 * altrimenti mi riferisco alle informazioni contenute in altro env
					 * diverso da quelli utilizzati per popolare la presente richiesta
					 */
					System.out.println("Ho gia' incontrato un nodo Attribute");
					int indexEnv = getRandomInt(envAsArrayLength);
					System.out.println(indexEnv);
					Tupla newEnv = this.envHashTable.get(envKeysAsArray[indexEnv]);
					newEnv.printTupla();
					for (int k = 0; k < envAsArrayLength; k++) {
						if(envKeysAsArray[k] == newEnv.getPkTupla()){
							envKeysAsArray[k] = envKeysAsArray[--envAsArrayLength];
							System.out.println();
							break;
						}
					}
					boolean newGuardia = true;
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newEnv);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco ad env
							 */
							if(newGuardia){
								childAttributeValue.setTextContent(newEnv.getAttributeValue());
								for (int l = 0; l < envValueLength; l++) {
									if(envAttValueSubSetAsArray[l].equals(newEnv.getAttributeValue())) {
										envAttValueSubSetAsArray[l] = envAttValueSubSetAsArray[--envValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di env non utilizzato per popolare la presente richiesta
								 */
								if(envValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(envValueLength);
									childAttributeValue.setTextContent(envAttValueSubSetAsArray[indexAttValue]);
									envAttValueSubSetAsArray[indexAttValue] = envAttValueSubSetAsArray[--envValueLength];
								}
							}
					}
				}
			}
		}
	}
	private void fillAction(Tupla act, Node childEntity) {
		for (int j = 0; j < actAsArrayLength; j++) {
			if(actKeysAsArray[j] == act.getPkTupla()){
				actKeysAsArray[j] = actKeysAsArray[--actAsArrayLength];
				System.out.println();
				break;
			}
		}
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo attribute figlio del nodo Action
				 * allora mi riferisco alle informazioni nell'Action della combinazione
				 */
				if(this.guardia.get(0) && this.guardia.get(1)){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, act);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco ad act 
							 */
							if(this.guardia.get(0) && this.guardia.get(1)){
								childAttributeValue.setTextContent(act.getAttributeValue());
								for (int l = 0; l < actValueLength; l++) {
									if(actAttValueSubSetAsArray[l].equals(act.getAttributeValue())) {
										actAttValueSubSetAsArray[l] = actAttValueSubSetAsArray[--actValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								this.guardia.clear(1);
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di action non utilizzato per popolare la presente richiesta
								 */
								if(actValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(actValueLength);
									childAttributeValue.setTextContent(actAttValueSubSetAsArray[indexAttValue]);
									actAttValueSubSetAsArray[indexAttValue] = actAttValueSubSetAsArray[--actValueLength];
								}
							}
					}
				}else{
					/*
					 * altrimenti mi riferisco alle informazioni contenute in altro resource
					 * diverso da quelli utilizzati per popolare la presente richiesta
					 */
					int indexAct = getRandomInt(actAsArrayLength);
					System.out.println(indexAct);
					Tupla newAct = this.actHashTable.get(actKeysAsArray[indexAct]);
					newAct.printTupla();
					for (int k = 0; k < actAsArrayLength; k++) {
						if(actKeysAsArray[k] == newAct.getPkTupla()){
							actKeysAsArray[k] = actKeysAsArray[--actAsArrayLength];
							System.out.println();
							break;
						}
					}
					boolean newGuardia = true;
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newAct);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco ad act
							 */
							if(newGuardia){
								childAttributeValue.setTextContent(newAct.getAttributeValue());
								for (int l = 0; l < actValueLength; l++) {
									if(actAttValueSubSetAsArray[l].equals(newAct.getAttributeValue())) {
										actAttValueSubSetAsArray[l] = actAttValueSubSetAsArray[--actValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di action non utilizzato per popolare la presente richiesta
								 */
								if(actValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(actValueLength);
									childAttributeValue.setTextContent(actAttValueSubSetAsArray[indexAttValue]);
									actAttValueSubSetAsArray[indexAttValue] = actAttValueSubSetAsArray[--actValueLength];
								}
							}
					}
				}
			}
		}


	}
	private void fillNewResource(Node childEntity) {
		int indexRes = getRandomInt(resAsArrayLength);
		Tupla newRes = this.resHashTable.get(resKeysAsArray[indexRes]);
		newRes.printTupla();
		for (int k = 0; k < resAsArrayLength; k++) {
			if(resKeysAsArray[k] == newRes.getPkTupla()){
				resKeysAsArray[k] = resKeysAsArray[--resAsArrayLength];
				System.out.println();
				break;
			}
		}
		boolean newAttributeGuardia = true;
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo attribute figlio del nodo Resoure
				 * allora mi riferisco alle informazioni nel Resoure corrente
				 */
				if(newAttributeGuardia){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newRes);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco al Resource corrente 
							 * 
							 */
							if(newAttributeGuardia){
								childAttributeValue.setTextContent(newRes.getAttributeValue());
								for (int l = 0; l < resValueLength; l++) {
									if(resAttValueSubSetAsArray[l].equals(newRes.getAttributeValue())) {
										resAttValueSubSetAsArray[l] = resAttValueSubSetAsArray[--resValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newAttributeGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di Resource non utilizzato per popolare la presente richiesta
								 */
								if(resValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(resValueLength);
									childAttributeValue.setTextContent(resAttValueSubSetAsArray[indexAttValue]);
									resAttValueSubSetAsArray[indexAttValue] = resAttValueSubSetAsArray[--resValueLength];
								}
							}
					}
				}else{
					if(resAsArrayLength > 0){
						/*
						 * altrimenti mi riferisco alle informazioni contenute in altro Resource
						 * diverso da quelli utilizzati per popolare la presente richiesta
						 * Nuovo ATTRIBUTE
						 */
						indexRes = getRandomInt(resAsArrayLength);
						System.out.println(indexRes);
						newRes = this.resHashTable.get(resKeysAsArray[indexRes]);
						newRes.printTupla();
						for (int k = 0; k < resAsArrayLength; k++) {
							if(resKeysAsArray[k] == newRes.getPkTupla()){
								System.out.println(resKeysAsArray[k] +" == "+ newRes.getPkTupla());
								resKeysAsArray[k] = resKeysAsArray[--resAsArrayLength];
								System.out.println();
								break;
							}
						}
						boolean newGuardia = true;
						NamedNodeMap attributi = childAttribute.getAttributes();
						fillAttributes(attributi, newRes);
						NodeList childrenAttributeValueList = childAttribute.getChildNodes();
						for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
							Node childAttributeValue = childrenAttributeValueList.item(k);
							if(childAttributeValue.getNodeName().contains("AttributeValue"))
								/*
								 * e' la prima volta che incontro un attributeValue figlio di
								 * attribute
								 */
								if(newGuardia){
									childAttributeValue.setTextContent(newRes.getAttributeValue());
									for (int l = 0; l < resValueLength; l++) {
										if(resAttValueSubSetAsArray[l].equals(newRes.getAttributeValue())) {
											resAttValueSubSetAsArray[l] = resAttValueSubSetAsArray[--resValueLength];
										}
									}
									// CAMBIARE IL VALORE DELLA GUARDIA
									newGuardia = false;
								}else{
									if(resValueLength > 0){
										int indexAttValue = getRandomInt(resValueLength);
										childAttributeValue.setTextContent(resAttValueSubSetAsArray[indexAttValue]);
										resAttValueSubSetAsArray[indexAttValue] = resAttValueSubSetAsArray[--resValueLength];
									}
								}
						}
					}
				}
			}
		}
	
	}
	private void fillFirstResource(Tupla res, Node childEntity) {
		for (int j = 0; j < resAsArrayLength; j++) {
			if(resKeysAsArray[j] == res.getPkTupla()){
				System.out.println(resKeysAsArray[j] +" == "+ res.getPkTupla());
				resKeysAsArray[j] = resKeysAsArray[--resAsArrayLength];
				System.out.println();
				break;
			}
		}
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo attribute figlio del nodo resjet
				 * allora mi riferisco alle informazioni nel resject della combinazione
				 * identificata dal parametro combIndex
				 */
				if(!this.guardia.get(0) && this.guardia.get(1)){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, res);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco alla combinazione identificata 
							 * dal parametro combIndex
							 */
							if(!this.guardia.get(0) && this.guardia.get(1)){
								childAttributeValue.setTextContent(res.getAttributeValue());
								for (int l = 0; l < resValueLength; l++) {
									if(resAttValueSubSetAsArray[l].equals(res.getAttributeValue())) {
										resAttValueSubSetAsArray[l] = resAttValueSubSetAsArray[--resValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								this.guardia.set(0);
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di resject non utilizzato per popolare la presente richiesta
								 */
								if(resValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(resValueLength);
									childAttributeValue.setTextContent(resAttValueSubSetAsArray[indexAttValue]);
									resAttValueSubSetAsArray[indexAttValue] = resAttValueSubSetAsArray[--resValueLength];
								}
							}
					}
				}else{
					/*
					 * altrimenti mi riferisco alle informazioni contenute in altro resource
					 * diverso da quelli utilizzati per popolare la presente richiesta
					 */
					System.out.println("Ho gia' incontrato un nodo Attribute");
					int indexRes = getRandomInt(resAsArrayLength);
					System.out.println(indexRes);
					Tupla newRes = this.resHashTable.get(resKeysAsArray[indexRes]);
					newRes.printTupla();
					for (int k = 0; k < resAsArrayLength; k++) {
						if(resKeysAsArray[k] == newRes.getPkTupla()){
							System.out.println(resKeysAsArray[k] +" == "+ newRes.getPkTupla());
							resKeysAsArray[k] = resKeysAsArray[--resAsArrayLength];
							System.out.println();
							break;
						}
					}
					boolean newGuardia = true;
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newRes);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco alla combinazione identificata 
							 * dal parametro combIndex
							 */
							if(newGuardia){
								childAttributeValue.setTextContent(newRes.getAttributeValue());
								for (int l = 0; l < resValueLength; l++) {
									if(resAttValueSubSetAsArray[l].equals(newRes.getAttributeValue())) {
										resAttValueSubSetAsArray[l] = resAttValueSubSetAsArray[--resValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di resject non utilizzato per popolare la presente richiesta
								 */
								if(resValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(resValueLength);
									childAttributeValue.setTextContent(resAttValueSubSetAsArray[indexAttValue]);
									resAttValueSubSetAsArray[indexAttValue] = resAttValueSubSetAsArray[--resValueLength];
								}
							}
					}
				}
			}
		}


	}
	private void initializeVariables() {
		// per subject
		subAttValueSubSetAsArray = new String[this.subAttValueSubSet.size()];
		subAttValueSubSetAsArray = (String[])this.subAttValueSubSet.toArray(subAttValueSubSetAsArray);
		subValueLength = subAttValueSubSetAsArray.length;
		subKeysAsArray = new Integer[this.subHashTable.size()];
		subKeysAsArray = this.subHashTable.keySet().toArray(subKeysAsArray);
		subAsArrayLength = subKeysAsArray.length;
		// per resource
		resAttValueSubSetAsArray = new String[this.resAttValueSubSet.size()];
		resAttValueSubSetAsArray = (String[])this.resAttValueSubSet.toArray(resAttValueSubSetAsArray);
		resValueLength = resAttValueSubSetAsArray.length;
		resKeysAsArray = new Integer[this.resHashTable.size()];
		resKeysAsArray = this.resHashTable.keySet().toArray(resKeysAsArray);
		resAsArrayLength = resKeysAsArray.length;
		// per action
		actAttValueSubSetAsArray = new String[this.actAttValueSubSet.size()];
		actAttValueSubSetAsArray = (String[])this.actAttValueSubSet.toArray(actAttValueSubSetAsArray);
		actValueLength = actAttValueSubSetAsArray.length;
		actKeysAsArray = new Integer[this.actHashTable.size()];
		actKeysAsArray = this.actHashTable.keySet().toArray(actKeysAsArray);
		actAsArrayLength = actKeysAsArray.length;
		// per environment
		envAttValueSubSetAsArray = new String[this.envAttValueSubSet.size()];
		envAttValueSubSetAsArray = (String[])this.envAttValueSubSet.toArray(envAttValueSubSetAsArray);
		envValueLength = envAttValueSubSetAsArray.length;
		envKeysAsArray = new Integer[this.envHashTable.size()];
		envKeysAsArray = this.envHashTable.keySet().toArray(envKeysAsArray);
		envAsArrayLength = envKeysAsArray.length;
	}
	private void fillFirstSubject(Tupla sub, Node childEntity) {

		for (int j = 0; j < subAsArrayLength; j++) {
			if(subKeysAsArray[j] == sub.getPkTupla()){
				System.out.println(subKeysAsArray[j] +" == "+ sub.getPkTupla());
				subKeysAsArray[j] = subKeysAsArray[--subAsArrayLength];
				System.out.println();
				break;
			}
		}
		childEntity.getAttributes().getNamedItem(String.valueOf(ElementsName.SubjectCategory)).setNodeValue(sub.getSubjectCategory());
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo attribute figlio del nodo Subjet
				 * allora mi riferisco alle informazioni nel subject della combinazione
				 * identificata dal parametro combIndex
				 */
				if(!this.guardia.get(0) && !this.guardia.get(1)){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, sub);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco alla combinazione identificata 
							 * dal parametro combIndex
							 */
							if(!this.guardia.get(0) && !this.guardia.get(1)){
								childAttributeValue.setTextContent(sub.getAttributeValue());
								for (int l = 0; l < subValueLength; l++) {
									if(subAttValueSubSetAsArray[l].equals(sub.getAttributeValue())) {
										subAttValueSubSetAsArray[l] = subAttValueSubSetAsArray[--subValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								this.guardia.set(1);
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di Subject non utilizzato per popolare la presente richiesta
								 */
								if(subValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(subValueLength);
									childAttributeValue.setTextContent(subAttValueSubSetAsArray[indexAttValue]);
									subAttValueSubSetAsArray[indexAttValue] = subAttValueSubSetAsArray[--subValueLength];
								}
							}
					}
				}else{
					/*
					 * altrimenti mi riferisco alle informazioni contenute in altro subject
					 * diverso da quelli utilizzati per popolare la presente richiesta
					 */
					System.out.println("Ho gia' incontrato un nodo Attribute");
					System.out.println("Allora prendo un sub diverso "+subKeysAsArray.toString());
					int indexSub = getRandomInt(subAsArrayLength);
					System.out.println(indexSub);
					Tupla newSub = this.subHashTable.get(subKeysAsArray[indexSub]);
					newSub.printTupla();
					for (int k = 0; k < subAsArrayLength; k++) {
						if(subKeysAsArray[k] == newSub.getPkTupla()){
							System.out.println(subKeysAsArray[k] +" == "+ newSub.getPkTupla());
							subKeysAsArray[k] = subKeysAsArray[--subAsArrayLength];
							System.out.println();
							break;
						}
					}
					boolean newGuardia = true;
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newSub);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco alla combinazione identificata 
							 * dal parametro combIndex
							 */
							if(newGuardia){
								childAttributeValue.setTextContent(newSub.getAttributeValue());
								for (int l = 0; l < subValueLength; l++) {
									if(subAttValueSubSetAsArray[l].equals(newSub.getAttributeValue())) {
										subAttValueSubSetAsArray[l] = subAttValueSubSetAsArray[--subValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di Subject non utilizzato per popolare la presente richiesta
								 */
								if(subValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(subValueLength);
									childAttributeValue.setTextContent(subAttValueSubSetAsArray[indexAttValue]);
									subAttValueSubSetAsArray[indexAttValue] = subAttValueSubSetAsArray[--subValueLength];
								}
							}
					}
				}
			}
		}
	}
	private void fillNewSubject(Node childEntity) {
		int indexSub = getRandomInt(subAsArrayLength);
		Tupla newSub = this.subHashTable.get(subKeysAsArray[indexSub]);
		newSub.printTupla();
		for (int k = 0; k < subAsArrayLength; k++) {
			if(subKeysAsArray[k] == newSub.getPkTupla()){
				System.out.println(subKeysAsArray[k] +" == "+ newSub.getPkTupla());
				subKeysAsArray[k] = subKeysAsArray[--subAsArrayLength];
				System.out.println();
				break;
			}
		}
		boolean newAttributeGuardia = true;
		childEntity.getAttributes().getNamedItem(String.valueOf(ElementsName.SubjectCategory)).setNodeValue(newSub.getSubjectCategory());
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo attribute figlio del nodo Subjet
				 * allora mi riferisco alle informazioni nel subject corrente
				 */
				if(newAttributeGuardia){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newSub);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un attributeValue figlio di
							 * attribute, allora mi riferisco al subject corrente 
							 * 
							 */
							if(newAttributeGuardia){
								childAttributeValue.setTextContent(newSub.getAttributeValue());
								for (int l = 0; l < subValueLength; l++) {
									if(subAttValueSubSetAsArray[l].equals(newSub.getAttributeValue())) {
										subAttValueSubSetAsArray[l] = subAttValueSubSetAsArray[--subValueLength];
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newAttributeGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di Subject non utilizzato per popolare la presente richiesta
								 */
								if(subValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(subValueLength);
									childAttributeValue.setTextContent(subAttValueSubSetAsArray[indexAttValue]);
									subAttValueSubSetAsArray[indexAttValue] = subAttValueSubSetAsArray[--subValueLength];
								}
							}
					}
				}else{
					if(subAsArrayLength > 0){
						/*
						 * altrimenti mi riferisco alle informazioni contenute in altro subject
						 * diverso da quelli utilizzati per popolare la presente richiesta
						 * Nuovo ATTRIBUTE
						 */
						System.out.println("Ho gia' incontrato un nodo Attribute");
						System.out.println("Allora prendo un sub diverso "+subKeysAsArray.toString());
						indexSub = getRandomInt(subAsArrayLength);
						System.out.println(indexSub);
						newSub = this.subHashTable.get(subKeysAsArray[indexSub]);
						newSub.printTupla();
						for (int k = 0; k < subAsArrayLength; k++) {
							if(subKeysAsArray[k] == newSub.getPkTupla()){
								System.out.println(subKeysAsArray[k] +" == "+ newSub.getPkTupla());
								subKeysAsArray[k] = subKeysAsArray[--subAsArrayLength];
								System.out.println();
								break;
							}
						}
						boolean newGuardia = true;
						NamedNodeMap attributi = childAttribute.getAttributes();
						fillAttributes(attributi, newSub);
						NodeList childrenAttributeValueList = childAttribute.getChildNodes();
						for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
							Node childAttributeValue = childrenAttributeValueList.item(k);
							if(childAttributeValue.getNodeName().contains("AttributeValue"))
								/*
								 * e' la prima volta che incontro un attributeValue figlio di
								 * attribute, allora mi riferisco alla combinazione identificata 
								 * dal parametro combIndex
								 */
								if(newGuardia){
									childAttributeValue.setTextContent(newSub.getAttributeValue());
									for (int l = 0; l < subValueLength; l++) {
										if(subAttValueSubSetAsArray[l].equals(newSub.getAttributeValue())) {
											subAttValueSubSetAsArray[l] = subAttValueSubSetAsArray[--subValueLength];
										}
									}
									// CAMBIARE IL VALORE DELLA GUARDIA
									newGuardia = false;
								}else{
									/*
									 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
									 * di Subject non utilizzato per popolare la presente richiesta
									 */
									if(subValueLength > 0){
										/*
										 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
										 * gli AttributeValue Non ancora utilizzati
										 */
										int indexAttValue = getRandomInt(subValueLength);
										childAttributeValue.setTextContent(subAttValueSubSetAsArray[indexAttValue]);
										subAttValueSubSetAsArray[indexAttValue] = subAttValueSubSetAsArray[--subValueLength];
									}
								}
						}
					}
				}
			}
		}
	}
	private void fillAttributes(NamedNodeMap attributi, Tupla sub) {
		// TODO Auto-generated method stub
		for (int k = 0; k < attributi.getLength(); k++) {
			Node attributo = attributi.item(k);
			if(attributo.getNodeName().equals(String.valueOf(ElementsName.AttributeId)))
				attributo.setNodeValue(sub.getAttributeId());
			if(attributo.getNodeName().equals(String.valueOf(ElementsName.DataType)))
				attributo.setNodeValue(sub.getDataType());
			if(attributo.getNodeName().equals(String.valueOf(ElementsName.Issuer)))
				attributo.setNodeValue(sub.getIssuer());
		}
	}
	private int getRandomInt(int number){
		return (int)(Math.random() * number);
	}
	private void printNode(Node node, File intermediateRequests2){
		try {
			String reqName = intermediateRequests2.getName();
			System.out.println("Nome del file della richiesta : "+reqName);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			// salvataggio del file nel fileSystem
			BufferedWriter requestWriter;
			File req = new File(this.intermediateReqDir.getCanonicalFile()+GuiCons.DIR_SEPARATOR+reqName);
			if(req.exists())
				req.delete();
			req.createNewFile();
			requestWriter = new BufferedWriter(new FileWriter(req.getCanonicalPath()));
			requestWriter.write(xmlString);
			requestWriter.flush();
			requestWriter.close();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
