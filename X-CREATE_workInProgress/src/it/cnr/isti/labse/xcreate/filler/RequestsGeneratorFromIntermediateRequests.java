package it.cnr.isti.labse.xcreate.filler;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;
import it.cnr.isti.labse.xcreate.sql.SelectTipi;
import it.cnr.isti.labse.xcreate.util.StringToEnum;
import it.cnr.isti.labse.xcreate.util.Util;
import it.cnr.isti.labse.xcreate.xQuery.ElementsName;
import it.cnr.isti.labse.xcreate.util.Cleaning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
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

public class RequestsGeneratorFromIntermediateRequests {
	private int pkPolitica; 
	private int pkNodo;
	private File[] intermediateRequests;
	private Hashtable<Integer, Tupla> subHashTable;
	private Hashtable<Integer, Tupla> resHashTable;
	private Hashtable<Integer, Tupla> actHashTable;
	private Hashtable<Integer, Tupla> envHashTable;
	//private Hashtable<Integer, Tupla> combination;
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

	public RequestsGeneratorFromIntermediateRequests(int pkPolitica, int pkNodo, Connection mySqlConnection2, File[] interRequests, File combFromInterDir) {
		this.pkNodo = pkNodo;
		this.pkPolitica = pkPolitica;
		this.intermediateRequests = interRequests;
		this.mySqlConnection = mySqlConnection2;
		this.combFromInterDir = combFromInterDir; //dove scrivere le richieste riempite

		this.subHashTable = new Hashtable<Integer, Tupla>();
		this.resHashTable = new Hashtable<Integer, Tupla>();
		this.actHashTable = new Hashtable<Integer, Tupla>();
		this.envHashTable = new Hashtable<Integer, Tupla>();
		//this.combination = new Hashtable<Integer, Tupla>();
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
	
	/**
	 * PERMETTE DI SELEZIONARE IL NODO RADICE
	 * SI UTILIZZA XPATH.
	 * 
	 * PRECONDIZIONE:
	 * 		TUTTE LE RICHIESTE INTERMEDIE TRATTATE SONO VALIDE RISPETTO 
	 * 		ALLO SCHEMA DI RIFERIMENTO CONTEXT-SCHEMA
	 * @param filename
	 * @param validating
	 * @return
	 */
	public Node getFileAsDom(File filename, boolean validating) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validating);
			Document doc = factory.newDocumentBuilder().parse(filename);
			
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

	public File generatesRequests(){
		try {
			int idTupla, pkTupla, pkCombinazione;
			//int fkTipoTupla, fkNodo; // li stampava solo per debug
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
					//System.out.println("combPairWise::: "+this.combPairWise.get(pkCombinazione).get(pkTupla));
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
						nomeTipoTupla+"  "+nomeTipoCombinazione+"  "+pkCombinazione);*/
			}
			
			/**
			System.out.println("resultSize() : "+count);
			System.out.println(" dimensione ComBPair  :"+this.combPairWise.size());
			System.out.println(" dimensione ComBThree :"+this.combThreeWise.size());
			System.out.println(" dimensione ComBONE   :"+this.combOneWise.size());
			System.out.println(" dimensione ComBFOUR  :"+this.combFourWise.size());

			System.out.println(" dimensione subHash : "+this.subHashTable.size());
			System.out.println(" dimensione resHash : "+this.resHashTable.size());
			System.out.println(" dimensione actHash : "+this.actHashTable.size());
			System.out.println(" dimensione envHash : "+this.envHashTable.size());

			System.out.println("dimensione subAttSubSet "+this.subAttValueSubSet.size());				
			System.out.println("dimensione resAttSubSet "+this.resAttValueSubSet.size());
			System.out.println("dimensione actAttSubSet "+this.actAttValueSubSet.size());
			System.out.println("dimensione envAttSubSet "+this.envAttValueSubSet.size());
			*/
			
			generatesFinalRequest();
			result.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * FIXME 
		 * FIXME 
		 * FIXME
		 * FIXME
		 * FIXME
		 * FIXME
		 * FIXME 
		 *  Prima costruisco un nuovo Pulitore, poi su di esso ci chiamo il metodo
		 * che ripulisce i file e restituire all'interfaccia la nuova directory 
		 * 
		 */
		Cleaning clean = new Cleaning(this.intermediateReqDir);
		
		return clean.cleaner(); 
		
		//quindi questo return verra' commentato 
		//return this.intermediateReqDir; 
	}
	
	/*
	 * GENERA LE RICHIESTE XACML
	 * RIEMPIE UNA RICHIESTA INTERMEDIA PER OGNI COMBINAZIONE  
	 */
	private void generatesFinalRequest() { 
		try {
		//	Enumeration<Integer> combKeys;
			BitSet pattern = new BitSet(4);
			pattern.clear();
			if(this.combFromInterDir.getName().equals(GuiCons.HIER_COMB_FROM_INTER_REQ_DIR_NAME)){
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
				while(this.indexRequest < this.intermediateRequests.length){
					System.out.println("caso 1 abbiamo un tipo di tupla "+caso);
					// ONEWISE
					System.out.println("ONEWISE");
					Enumeration<Integer> comb1Wise = this.combOneWise.keys();
					
					int [] keysOne = new int [combOneWise.size()];
					int f =0;
					while (comb1Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb1Wise.nextElement();
						keysOne[f]= combKeyappoggio;
						f++;
					}
					Arrays.sort(keysOne);
					for (int j =0; j<keysOne.length; j++){
						if(this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysOne[j], requestAsDom, this.combOneWise ); 
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}					
				}
			}
			/*
			 * abbiamo solo due tipi di tupla 
			 * pairWise
			 * genera le richieste a partire dalle richieste intermedie
			 */
			if((caso > 4) && (caso <= 10)){
				while(this.indexRequest < this.intermediateRequests.length){
					System.out.println("caso 2 abbiamo due tipi di tupla "+caso);
					// PAIRWISE
					System.out.println("PAIRWISE");
					Enumeration<Integer> comb2Wise = this.combPairWise.keys();
					
					int [] keysPair = new int [combPairWise.size()];
					int f =0;
					while (comb2Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb2Wise.nextElement();
						keysPair[f]= combKeyappoggio;
						f++;
					}
					Arrays.sort(keysPair);
					for (int j =0; j<keysPair.length; j++){
						if(this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysPair[j], requestAsDom, this.combPairWise ); 
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}
				}
			}
			/*
			 * abbiamo tre tipi di tupla
			 * threeWise
			 * genera le richieste a partire dalle richieste intermedie 
			 */
			if(caso >= 11){
				while(this.indexRequest < this.intermediateRequests.length){
					System.out.println("caso 3 abbiamo tre tipi di tupla "+caso);
					// PAIRWISE
					System.out.println("PAIRWISE");
					Enumeration<Integer> comb2Wise = this.combPairWise.keys();
					
					int [] keysPair = new int [combPairWise.size()];
					int f =0;
					while (comb2Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb2Wise.nextElement();
						keysPair[f]= combKeyappoggio;
						f++;
					}
					for(int i = 0; i < keysPair.length; i++)
						System.out.println(keysPair[i]+ " -------------------------------------");
					Arrays.sort(keysPair);
					for (int j =0; j<keysPair.length; j++){
						System.out.println(keysPair[j]+ " �������������������������������������");
						if(this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysPair[j], requestAsDom, this.combPairWise ); 
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}
					//THREEWISE
					System.out.println("THREEWISE");
					Enumeration<Integer> comb3Wise = this.combThreeWise.keys();
					
					int [] keysThree = new int [combThreeWise.size()];
					int g =0;
					while (comb3Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb3Wise.nextElement();
						keysThree[g]= combKeyappoggio;
						g++;
					}
					for(int i = 0; i < keysThree.length; i++)//elimina
						System.out.println(keysThree[i]+ " -------------------------------------");//elimina
					Arrays.sort(keysThree);
					
					for (int j =0; j<keysThree.length; j++){
						System.out.println(keysThree[j]+ " �������������������������������������");//elimina
						if(this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysThree[j], requestAsDom, this.combThreeWise ); 
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}
				}// end primo while
			} // end caso >= 11
			/*
			 *  abbiamo tutte e quattro le tuple
			 *  pairWise, threeWise, fourWise
			 *  genera le richieste a partire dalle richieste intermedie 
			 *  subHashTable;resHashTable;actHashTable;envHashTable;
			 */
			if (caso == 15){
				while(this.indexRequest < this.intermediateRequests.length){
					System.out.println("caso 4 abbiamo quattro tipi di tupla "+caso);
					// PAIRWISE
					System.out.println("PAIRWISE");
					Enumeration<Integer> comb2Wise = this.combPairWise.keys();
					
					int [] keysPair = new int [combPairWise.size()];
					int f =0;
					while (comb2Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb2Wise.nextElement();
						keysPair[f]= combKeyappoggio;
						f++;
					}
					for(int i = 0; i < keysPair.length; i++)
						System.out.println(keysPair[i]+ " -------------------------------------");
					Arrays.sort(keysPair);
					for (int j =0; j<keysPair.length; j++){
						System.out.println(keysPair[j]+ " �������������������������������������");
						if(this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysPair[j], requestAsDom, this.combPairWise );
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}
					//THREEWISE
					System.out.println("THREEWISE");
					Enumeration<Integer> comb3Wise = this.combThreeWise.keys();
					
					int [] keysThree = new int [combThreeWise.size()];
					int g =0;
					while (comb3Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb3Wise.nextElement();
						keysThree[g]= combKeyappoggio;
						g++;
					}
					for(int i = 0; i < keysThree.length; i++)//elimina
						System.out.println(keysThree[i]+ " -------------------------------------");//elimina
					Arrays.sort(keysThree);
					
					for (int j =0; j<keysThree.length; j++){
						System.out.println(keysThree[j]+ " �������������������������������������");//elimina
						if(this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysThree[j], requestAsDom, this.combThreeWise ); 
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}
					// FOURWISE
					System.out.println("FOURWISE");
					Enumeration<Integer> comb4Wise = this.combFourWise.keys();
					
					int [] keysFour = new int [combFourWise.size()];
					int h =0;
					while (comb4Wise.hasMoreElements()) {
						Integer combKeyappoggio = (Integer) comb4Wise.nextElement();
						keysFour[h]= combKeyappoggio;
						h++;
					}
					Arrays.sort(keysFour);
					for (int j =0; j<keysFour.length; j++){
						if (this.indexRequest < this.intermediateRequests.length){
							Node requestAsDom = getFileAsDom(this.intermediateRequests[this.indexRequest], false);
							fillIntermediateRequestFromThreeWise(keysFour[j], requestAsDom, this.combFourWise ); 
							printNode(requestAsDom,this.intermediateRequests[this.indexRequest]);
							this.indexRequest++;
							this.guardia.clear();
							System.out.println("File : " +indexRequest);
						}else
							break;
					}
				} // end primo while
			}// end if caso 15
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// end generatesEIlMetodoGiusto


	/**
	 * Si occupa di riempire le richieste intermedie a partire da una combinazione di elementi.
	 * Se sono presenti ulteriori elementi, li riempie prendendo dei valori scelti a caso tra quelli 
	 * ancora disponibili e non gia' usati per riempire la richiesta.
	 * @param combIndex
	 * @param reqNode
	 * @param combNWise
	 */
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
						 * quindi mi riferisco ad una entita' SUBJECT diversa da 
						 * quelle utilizzate per popolare questa richiesta
						 * avanti cosi' finche' ci sono Subject nell'insieme o nella richiesta
						 */
						System.out.println("Ho gia' incontrato un nodo Subject");
						System.out.println("quanti sub non ancora utlizzati  :::: "+subAsArrayLength);
						if(subValueLength > 0){//if(subAsArrayLength > 0){ 
							fillNewSubject(childEntity);
						}
					}
				}else{
					/* subject e' null .. significa che la richiesta non comprendera' alcun Subject..
					 * cambio la guardia 
					 */
					this.guardia.set(1); 
				}
			}// fine elemento subject
			if(childEntity.getNodeName().contains(String.valueOf(ElementsName.Resource))){
				if(res != null){
					System.out.println("Siamo nel nodo Resource "+childEntity.getNodeName());
					/*
					 *  e' la prima volta che incontro il nodo Resource
					 *  quindi mi riferisco alla combinazione identifica dal parametro combIndex
					 */
					//if(!this.guardia.get(0) && !this.guardia.get(1))//nella richiesta non c'e' il nodo Subject
						//this.guardia.set(1);
					if(!this.guardia.get(0) && this.guardia.get(1))
						System.out.println("Allora qui incontro il primo resource!!");
						fillFirstResource(res, childEntity);

				}else {
					/*
					 * HO INCONTRATO UN NUOVO RESOURCE
					 * quindi mi riferisco ad una entita' Resource diversa da 
					 * quelle utilizzate per popolare questa richiesta
					 * avanti cosi' finche' ci sono Resource nell'insieme o nella richiesta
					 */
					if(resValueLength > 0){//if(resAsArrayLength > 0){ 
						System.out.println("TANTO PER SAPERE.. MA QUI CI VAI O NO???");
						fillNewResource(childEntity);
					}
				}
			}else{	}// fine elemento resource
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
			}// fine elemento action
			if(childEntity.getNodeName().contains(String.valueOf(ElementsName.Environment))){
				if(env != null ){
					System.out.println("Siamo nel nodo Environment "+childEntity.getNodeName());
					/*
					 *  e' la prima volta che incontro il nodo Environment
					 *  quindi mi riferisco alla combinazione identifica dal parametro combIndex
					 *  Notare che possiamo inontrare il nodo Environment SOLO UNA VOLTA
					 */
					System.out.println(this.guardia.get(0) +" "+this.guardia.get(1));
					/*if(this.guardia.get(0) && this.guardia.get(1))
						this.guardia.clear(1); */
					if(this.guardia.get(0) && !this.guardia.get(1))
						fillEnvironment(env, childEntity);
				}else{}
			}// fine elemento environment
			
		}// end for -- ha finito di visitare tutti i nodi
	}// end fillIntermediateRequest

	/**
	 * Si occupa di riempire gli Attribute e gli AttributeValue del primo nodo Environment
	 * con le informazioni reperite tramite la Tupla presa dalla Combinazione corrente per questa richiesta.
	 * @param env
	 * @param childEntity
	 */
	private void fillEnvironment(Tupla env, Node childEntity) {
		for (int j = 0; j < envAsArrayLength; j++) {
			if(envKeysAsArray[j] == env.getPkTupla()){
				envKeysAsArray[j] = envKeysAsArray[--envAsArrayLength];
				System.out.println("envKeysAsArray.length "+envKeysAsArray.length);
				break;
			}
		}
		NodeList childrenAttributeList = childEntity.getChildNodes();
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo Attribute figlio del nodo Environment
				 * allora mi riferisco alle informazioni nell'Environment della combinazione
				 * identificata dal parametro combIndex
				 */
				if(this.guardia.get(0) && !this.guardia.get(1)){
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, env);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * Attribute, allora mi riferisco alle informazioni nell'Environment della combinazione
							 * identificata dal parametro combIndex
							 */
							if(this.guardia.get(0) && !this.guardia.get(1)){
								childAttributeValue.setTextContent(env.getAttributeValue());
								for (int l = 0; l < envValueLength; l++) {
									if(envAttValueSubSetAsArray[l].equals(env.getAttributeValue())) {
										envAttValueSubSetAsArray[l] = envAttValueSubSetAsArray[--envValueLength];
										System.out.println("envAttValueSubSetAsArray.length "+envAttValueSubSetAsArray.length);
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								this.guardia.clear(0);
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di Environment non utilizzato per popolare la presente richiesta
								 */
								if(envValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(envValueLength);
									childAttributeValue.setTextContent(envAttValueSubSetAsArray[indexAttValue]);
									envAttValueSubSetAsArray[indexAttValue] = envAttValueSubSetAsArray[--envValueLength];
									System.out.println("envAttValueSubSetAsArray.length "+envAttValueSubSetAsArray.length);
								}
							}
					}// end for degli AttributeValue
				}else if(envAsArrayLength > 0){
					/*
					 * altrimenti mi riferisco alle informazioni contenute in altro Environment
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
							System.out.println("envKeysAsArray.length "+envKeysAsArray.length);
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
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * questo Attribute, allora mi riferisco all'Action corrente
							 */
							if(newGuardia){
								childAttributeValue.setTextContent(newEnv.getAttributeValue());
								for (int l = 0; l < envValueLength; l++) {
									if(envAttValueSubSetAsArray[l].equals(newEnv.getAttributeValue())) {
										envAttValueSubSetAsArray[l] = envAttValueSubSetAsArray[--envValueLength];
										System.out.println("envAttValueSubSetAsArray.length "+envAttValueSubSetAsArray.length);
										break;
									}
								}
								// CAMBIARE IL VALORE DELLA GUARDIA
								newGuardia = false;
							}else{
								/*
								 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
								 * di Environment non utilizzato per popolare la presente richiesta
								 */
								if(envValueLength > 0){
									/*
									 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
									 * gli AttributeValue Non ancora utilizzati
									 */
									int indexAttValue = getRandomInt(envValueLength);
									childAttributeValue.setTextContent(envAttValueSubSetAsArray[indexAttValue]);
									envAttValueSubSetAsArray[indexAttValue] = envAttValueSubSetAsArray[--envValueLength];
									System.out.println("envAttValueSubSetAsArray.length "+envAttValueSubSetAsArray.length);
								}// end if
							}//end else
					}//end for degli AttributeValue dei successivi Attribute
				}//end else per riempire i successivi Attribute
			}//end if ho finito tutti gli Attribute
		}//end for -- non ci sono altri figli per questo Environment
	}//end fillEnvironment

	/**
	 * Si occupa di riempire gli Attribute e gli AttributeValue del primo nodo Action
	 * con le informazioni reperite tramite la Tupla presa dalla Combinazione corrente per questa richiesta.
	 * @param act
	 * @param childEntity
	 */
	private void fillAction(Tupla act, Node childEntity) {

		for (int j = 0; j < actAsArrayLength; j++) {
			if(actKeysAsArray[j] == act.getPkTupla()){
				actKeysAsArray[j] = actKeysAsArray[--actAsArrayLength];
				System.out.println("actKeysAsArray.length "+actKeysAsArray.length);
				break;
			}

		}
		NodeList childrenAttributeList = childEntity.getChildNodes();
		if(childrenAttributeList.getLength() != 0){//il nodo Action potrebbe non avere figli
			for (int j = 0; j < childrenAttributeList.getLength(); j++) {
				Node childAttribute = childrenAttributeList.item(j);
				if(childAttribute.getNodeName().contains("Attribute")){
					/* e' la prima volta che incontro il nodo Attribute figlio del nodo Action
					 * allora mi riferisco alle informazioni nell'Action della combinazione
					 * identificata dal parametro combIndex
					 */
					if(this.guardia.get(0) && this.guardia.get(1)){
						NamedNodeMap attributi = childAttribute.getAttributes();
						fillAttributes(attributi, act);
						NodeList childrenAttributeValueList = childAttribute.getChildNodes();
						for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
							Node childAttributeValue = childrenAttributeValueList.item(k);
							if(childAttributeValue.getNodeName().contains("AttributeValue"))
								/*
								 * e' la prima volta che incontro un AttributeValue figlio di
								 * Attribute, allora mi riferisco all'Action della combinazione 
								 */
								if(this.guardia.get(0) && this.guardia.get(1)){
									childAttributeValue.setTextContent(act.getAttributeValue());
									for (int l = 0; l < actValueLength; l++) {
										if(actAttValueSubSetAsArray[l].equals(act.getAttributeValue())) {
											actAttValueSubSetAsArray[l] = actAttValueSubSetAsArray[--actValueLength];
											System.out.println("actAttValueSubSetAsArray.length "+actAttValueSubSetAsArray.length);
											break;
										}
									}
									// CAMBIARE IL VALORE DELLA GUARDIA
									this.guardia.clear(1);
								}else{
									/*
									 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
									 * di Action non utilizzato per popolare la presente richiesta
									 */
									if(actValueLength > 0){
										/*
										 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
										 * gli AttributeValue Non ancora utilizzati
										 */
										int indexAttValue = getRandomInt(actValueLength);
										childAttributeValue.setTextContent(actAttValueSubSetAsArray[indexAttValue]);
										actAttValueSubSetAsArray[indexAttValue] = actAttValueSubSetAsArray[--actValueLength];
										System.out.println("actAttValueSubSetAsArray.length "+actAttValueSubSetAsArray.length);
									}
								}
						}//end for degli AttributeValue del primo Attribute
					}else if(actValueLength > 0){
						/*
						 * altrimenti mi riferisco alle informazioni contenute in altro Action
						 * diverso da quelli utilizzati per popolare la presente richiesta
						 */
						System.out.println("Ho gia' incontrato un nodo Attribute in Action");
						System.out.println("Prendo un Act diverso "+actKeysAsArray.toString());
						int indexAct = getRandomInt(actAsArrayLength);
						System.out.println(indexAct);
						Tupla newAct = this.actHashTable.get(actKeysAsArray[indexAct]);
						newAct.printTupla();
						for (int k = 0; k < actAsArrayLength; k++) {
							if(actKeysAsArray[k] == newAct.getPkTupla()){
								System.out.println(actKeysAsArray[k]+" == "+newAct.getPkTupla());
								actKeysAsArray[k] = actKeysAsArray[--actAsArrayLength];
								System.out.println("actKeysAsArray.length "+actKeysAsArray.length);
								actAsArrayLength--;
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
								 * e' la prima volta che incontro un AttributeValue figlio di
								 * questo Attribute, allora mi riferisco all'Action corrente
								 */
								if(newGuardia){
									childAttributeValue.setTextContent(newAct.getAttributeValue());
									for (int l = 0; l < actValueLength; l++) {
										if(actAttValueSubSetAsArray[l].equals(newAct.getAttributeValue())) {
											actAttValueSubSetAsArray[l] = actAttValueSubSetAsArray[--actValueLength];
											System.out.println("actAttValueSubSetAsArray.length "+actAttValueSubSetAsArray.length);
											break;
										}
									}
									// CAMBIARE IL VALORE DELLA GUARDIA
									newGuardia = false;
								}else{
									/*
									 * altrimenti mi riferisco ad un AttributeValue del sottoinsieme
									 * di Action non utilizzato per popolare la presente richiesta
									 */
									if(actValueLength > 0){
										/*
										 * generazione di un numero casuale tra 0 e la dimensione dell'array che ospita 
										 * gli AttributeValue Non ancora utilizzati
										 */
										int indexAttValue = getRandomInt(actValueLength);
										childAttributeValue.setTextContent(actAttValueSubSetAsArray[indexAttValue]);
										actAttValueSubSetAsArray[indexAttValue] = actAttValueSubSetAsArray[--actValueLength];
										System.out.println("actAttValueSubSetAsArray.length "+actAttValueSubSetAsArray.length);
									}//end if
								}//end else
						}//end for degli AttributeValue per questo Attribute
					}//end else dei successivi Attribute 
				}//end if sono finiti i nodi Attribute
			}//end for -- non ci sono altri figli per questo Action
		}else { this.guardia.clear(1); }
	}//end fillAction

	/**
	 * Si occupa di riempire gli Attribute e gli AttributeValue di un secondo nodo Resource
	 * con le informazioni reperite tramite un resource presente nell'insieme e non ancora utilizzato
	 * per riempiere la richiesta. La selezione � casuale.
	 * @param childEntity
	 */
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
				/* e' la prima volta che incontro il nodo Attribute figlio 
				 * di questo nodo Resource
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
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * Attribute, allora mi riferisco al Resource corrente 
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
					}//end for degli AttributeValue del primo Attribute
				}else{
					if(resValueLength > 0){
						/* 
						 * Nuovo ATTRIBUTE
						 * altrimenti mi riferisco alle informazioni contenute in altro Resource
						 * diverso da quelli utilizzati per popolare la presente richiesta
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
								 * e' la prima volta che incontro un AttributeValue figlio di
								 * questo Attribute
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
									}//end if
								}//end else
						}//end for degli AttributeValue
					}//end if del nouvo Attribute
				}//end else del nouvo Attribute
			}//end if del primo Attribute per questo nuovo Resource
		}//end for degli Attribute per questo Resource
	}//end fillNewResource

	
	/**
	 * Si occupa di riempire gli Attribute e gli AttributeValue del primo nodo Resource
	 * con le informazioni reperite tramite la Tupla presa dalla Combinazione corrente per questa richiesta.
	 * @param res
	 * @param childEntity
	 */
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
		/*se non ha figli, la richiesta non e' valida per il PDP, puo' anche non continuare a riempirla*/
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo Attribute figlio del nodo Resource
				 * allora mi riferisco alle informazioni presenti nel Resource della combinazione
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
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * Attribute, allora mi riferisco alla combinazione identificata 
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
					}//end for degli AttributeValue per il primo Attribute
				}else if(resValueLength > 0){
					/*
					 * altrimenti ho incontrato un nuovo Attribute allora 
					 * mi riferisco alle informazioni contenute in altro Resource
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
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * questo Attribute, allora mi riferisco alla Tupla appena estratta dall'insieme Resource
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
								}//end if
							}//end else
					}//end for degli AttributeValue di questo Attribute
				}//end else del nuovo Attribute di questa Resource
			}//end if Attribute
		}//end for -- non ci sono piu' figli per questa Resource
	}//end fillFirstResource
	

	/**
	 * Si occupa di inizializzare delle strutture d'appoggio per reperire gli Attribute e
	 * gli AttributeValue, e tenere traccia di quelli gia' usati in questa richiesta.
	 */
	private void initializeVariables() {
		/*
		 * per SUBJECT
		 * prendo la HashSet degli AttributeValue dei Subject e copia i valori in un array 
		 * di stringhe, la dimensione serve per sapere entro che valore effettuare la scelta casuale.
		 */
		subAttValueSubSetAsArray = new String[this.subAttValueSubSet.size()];
		subAttValueSubSetAsArray = (String[])this.subAttValueSubSet.toArray(subAttValueSubSetAsArray);
		subValueLength = subAttValueSubSetAsArray.length;
		System.out.println("subAttValueSubSetAsArray.length "+subAttValueSubSetAsArray.length);
		/*
		 * Nell'array di Integer copia le kiavi dell'HashTable dei Subject
		 * la dimensione serve per sapere entro che valore effettuare la scelta casuale.
		 */ 
		subKeysAsArray = new Integer[this.subHashTable.size()];
		subKeysAsArray = this.subHashTable.keySet().toArray(subKeysAsArray);
		subAsArrayLength = subKeysAsArray.length;
		System.out.println("subKeysAsArray.length "+subKeysAsArray.length);
		// per RESOURCE
		resAttValueSubSetAsArray = new String[this.resAttValueSubSet.size()];
		resAttValueSubSetAsArray = (String[])this.resAttValueSubSet.toArray(resAttValueSubSetAsArray);
		resValueLength = resAttValueSubSetAsArray.length;
		System.out.println("resAttValueSubSetAsArray.length "+resAttValueSubSetAsArray.length);
		resKeysAsArray = new Integer[this.resHashTable.size()];
		resKeysAsArray = this.resHashTable.keySet().toArray(resKeysAsArray);
		resAsArrayLength = resKeysAsArray.length;
		System.out.println("resKeysAsArray.length "+resKeysAsArray.length);
		// per ACTION
		actAttValueSubSetAsArray = new String[this.actAttValueSubSet.size()];
		actAttValueSubSetAsArray = (String[])this.actAttValueSubSet.toArray(actAttValueSubSetAsArray);
		actValueLength = actAttValueSubSetAsArray.length;
		System.out.println("actAttValueSubSetAsArray.length "+actAttValueSubSetAsArray.length);
		actKeysAsArray = new Integer[this.actHashTable.size()];
		actKeysAsArray = this.actHashTable.keySet().toArray(actKeysAsArray);
		actAsArrayLength = actKeysAsArray.length;
		System.out.println("actKeysAsArray.length "+actKeysAsArray.length);
		// per ENVIRONMENT
		envAttValueSubSetAsArray = new String[this.envAttValueSubSet.size()];
		envAttValueSubSetAsArray = (String[])this.envAttValueSubSet.toArray(envAttValueSubSetAsArray);
		envValueLength = envAttValueSubSetAsArray.length;
		System.out.println("envAttValueSubSetAsArray.length");
		envKeysAsArray = new Integer[this.envHashTable.size()];
		envKeysAsArray = this.envHashTable.keySet().toArray(envKeysAsArray);
		envAsArrayLength = envKeysAsArray.length;
		System.out.println("envKeysAsArray.length "+envKeysAsArray.length);
	}


	/**
	 * Si occupa di riempire gli Attribute e gli AttributeValue del primo nodo Subject
	 * con le informazioni reperite tramite la Tupla presa dalla Combinazione corrente per questa richiesta.
	 * @param sub
	 * @param childEntity
	 */
	private void fillFirstSubject(Tupla sub, Node childEntity) {

		sub.printTupla();
		for (int j = 0; j < subAsArrayLength; j++) {
			if(subKeysAsArray[j] == sub.getPkTupla()){
				System.out.println(subKeysAsArray[j] +" == "+ sub.getPkTupla());
				subKeysAsArray[j] = subKeysAsArray[--subAsArrayLength];
				System.out.println("Prima tupla, tolgo pkTupla dal subKeysAsArray");
				break;
			}
		}
		childEntity.getAttributes().getNamedItem(String.valueOf(ElementsName.SubjectCategory)).setNodeValue(sub.getSubjectCategory());
		NodeList childrenAttributeList = childEntity.getChildNodes();
		if(childrenAttributeList.getLength() != 0){//il nodo Subject potrebbe non avere figli
		for (int j = 0; j < childrenAttributeList.getLength(); j++) {
			System.out.println(j+" j-esimo ciclo");
			Node childAttribute = childrenAttributeList.item(j);
			if(childAttribute.getNodeName().contains("Attribute")){
				/* e' la prima volta che incontro il nodo Attribute figlio del nodo Subject
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
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * Attribute, allora mi riferisco alla combinazione identificata 
							 * dal parametro combIndex
							 */
							if(!this.guardia.get(0) && !this.guardia.get(1)){
								childAttributeValue.setTextContent(sub.getAttributeValue());
								for (int l = 0; l < subValueLength; l++) {
									if(subAttValueSubSetAsArray[l].equals(sub.getAttributeValue())) {
										System.out.println("Mette il primo AttibuteValue, che � della prima tupla "+subAttValueSubSetAsArray[l]);
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
									System.out.println("Ma qui ci entra? sarebbe il secondo AttributeValue, e gli altri");
								}
							}
					}
				}else if(subValueLength > 0){
					/*
					 * altrimenti mi riferisco alle informazioni contenute in altro Subject
					 * diverso da quelli utilizzati per popolare la presente richiesta
					 */
					System.out.println("Ho gia' incontrato un nodo Attribute, sono nel secondo...");
					System.out.println("Allora prendo un sub diverso "+subKeysAsArray.toString());
					int indexSub = getRandomInt(subAsArrayLength);
					System.out.println("l'index random "+indexSub);
					Tupla newSub = this.subHashTable.get(subKeysAsArray[indexSub]);
					newSub.printTupla();
					for (int k = 0; k < subAsArrayLength; k++) {
						if(subKeysAsArray[k] == newSub.getPkTupla()){
							System.out.println(subKeysAsArray[k] +" == "+ newSub.getPkTupla());
							subKeysAsArray[k] = subKeysAsArray[--subAsArrayLength];
							System.out.println("E' un altro Attribute, e' come se prendessi un altra tupla "+subAsArrayLength);
							break;
						}
					}
					boolean newGuardia = true;
					NamedNodeMap attributi = childAttribute.getAttributes();
					fillAttributes(attributi, newSub);
					NodeList childrenAttributeValueList = childAttribute.getChildNodes();
					for (int k = 0; k < childrenAttributeValueList.getLength(); k++) {
						System.out.println(k+" k-esimo ciclo");
						Node childAttributeValue = childrenAttributeValueList.item(k);
						if(childAttributeValue.getNodeName().contains("AttributeValue"))
							/*
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * questo Attribute, allora mi riferisco alla Tupla appena estratta dall'insieme Subject
							 */
							if(newGuardia){
								childAttributeValue.setTextContent(newSub.getAttributeValue());
								for (int l = 0; l < subValueLength; l++) {
									if(subAttValueSubSetAsArray[l].equals(newSub.getAttributeValue())) {
										subAttValueSubSetAsArray[l] = subAttValueSubSetAsArray[--subValueLength];
										System.out.println("Sono nel secondo Attribute di Subject, che succede? "+subValueLength);
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
									System.out.println("Sono in un altro AttributeValue di questo Attribute");
								}//end if
							}//end else
					}//end for AttributeValue
				}//end else Attribute preso da un Subject a caso
			}//end if Attribute del primo Subject
		}//end for -- non ci sono piu' figli per il primo Subject
		}
		else { this.guardia.set(1); }
	}//end fillFirstSubject

	/**
	 * Si occupa di riempire gli Attribute e gli AttributeValue di un secondo nodo Subject
	 * con le informazioni reperite tramite un subject presente nell'insieme e non ancora utilizzato
	 * per riempiere la richiesta. La selezione � casuale.
	 * @param childEntity
	 */
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
				/* e' la prima volta che incontro il nodo attribute figlio di questo nodo Subject
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
							 * e' la prima volta che incontro un AttributeValue figlio di
							 * Attribute, allora mi riferisco al Subject corrente 
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
					}//end for AttributeValue
				}else{
					if(subAsArrayLength > 0){
						/*
						 * Nuovo ATTRIBUTE
						 * altrimenti mi riferisco alle informazioni contenute in altro Subject
						 * diverso da quelli utilizzati per popolare la presente richiesta
						 */
						System.out.println("Ho gia' incontrato un nodo Attribute -- 2");
						System.out.println("Allora prendo un sub diverso "+subKeysAsArray.toString());
						indexSub = getRandomInt(subAsArrayLength);
						System.out.println("Index random del fillNewSubject "+indexSub);
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
								 * e' la prima volta che incontro un AttributeValue figlio di
								 * questo Attribute, allora mi riferisco alla Tupla appena estratta 
								 * dall'insieme dei Subject non ancora utilizzati
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
									}//end if
								}//end else
						}//end for AttributeValue
					}
				}//end else nuovo Attribute
			}//end if primo Attribute
		}//end for -- non ci sono piu' figli per questo Subject
	}//end fillNewSubject
	


	/**
	 * Si occupa di reperire e settare i valori agli attributi di un nodo Attribute.
	 * @param attributi
	 * @param sub
	 */
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

	/**
	 * Genera un valore casuale che verra' usato per selezionare una Tupla o un AttributeValue
	 * tra quelli disponibili negli insiemi.
	 * @param number
	 * @return
	 */
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

