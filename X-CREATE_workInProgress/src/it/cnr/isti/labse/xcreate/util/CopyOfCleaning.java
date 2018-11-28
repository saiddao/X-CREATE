package it.cnr.isti.labse.xcreate.util;

import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.xQuery.ElementsName;
import it.cnr.isti.labse.xcreate.xSDResources.XacmlDataTypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * 
 * @author ARGENIX
 * 
 *	N.B. <ResourceContent> ??? ... non si considera... :)
 *
 *	perogni <AttributeValue> che incontro lo ficco dentro un <Attribute> con AttributeId e DataType
 *	identici al primo.
 *
 *	Per i Resource -- non ci puo' essere + di un resource-id
 *	tengo controllato se e' gia' stato scritto e crivo una nuova Resource ogni volta che lo incontro di nuovo.
 *
 */

public class CopyOfCleaning {

	// solo per tas3
	public static String xmlns = " xmlns:xac=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"";
	//public static String ns = "xac:";
	public static String ns = "";
	//..
	
	private static String resId = "urn:oasis:names:tc:xacml:1.0:resource:resource-id"; 
	private static String subC = "";
	private static String sub = "xacml-context:Subject";
	private static String res = "xacml-context:Resource";
	private static String act = "Action";
	private static String env = "Environment";
	private static String aId = "AttributeId";
	private static String dT = "DataType";
	private static String iss = "Issuer";
	
	private static File [] daPulire; //inizializzato nel metodo
	private File dir; 
	private File dirPulite;
	private String nomeFile;
	private boolean newResource;
	
	
	// modifiche apportate da Said
	private boolean resourceId;
	private Document doc;
	
	/* Il costruttore di questa classe prende la directory dove ci sono le richieste finali
	 * verra' chiamato tramite un metodo che restituira' all'interfaccia la directory coi file puliti. 
	 * 
	 * */
	
	/**
	 * Costruttore
	 * dir e' la directory dove sono le richieste riempite
	 * dirPulite e' la directory dove sono le richieste pulite
	 */
	public CopyOfCleaning(File dir ){
		this.dir = dir;
		//this.dirPulite = new File (dir.getParent()+File.separator+"Cleaned");
		//meglio dargli un nome che si capisce a quali si riferiscono
	}
	
	/*finche' ci sono file lui li passa li pulisce e li scrive in una sotto directory 
	 * che va restituita 
	 */
	public File cleaner(){		
		/*
		 * creo una cartella di nome Cleaned_dir.getName() dove scrivero' i file
		 * controllo se esiste, poi creo l'array di File, 
		 */		
		if(this.dir.getName().equals(GuiCons.COMB_FROM_INTER_REQ_DIR_NAME)){
			this.dirPulite = new File (dir.getParent()+File.separator+"Cleaned_"+dir.getName());
		}
		else {
			//quelle gerarchiche
			String sub = dir.getName();
			this.dirPulite = new File (dir.getParent()+File.separator+sub+"_Cleaned");
		}
		if (!dirPulite.exists()){
			dirPulite.mkdirs();
		}
		daPulire = dir.listFiles();
		
		int index = 0;
		//finche' ce ne sono li pulisce
		while(index < daPulire.length){
			System.out.println("Numero File " +daPulire.length);
			resourceId = false;			
			nomeFile = daPulire[index].getName();
			System.out.println("File : "+nomeFile+ " " +index);
			//piglia il nodo principale di questo file
			Node requestAsDom = getFileAsDom(daPulire[index], false);
			//metodo che lo scorre
//			String pulito = scorre(requestAsDom, daPulire[index]);
			//le scrive nella cartella
//			saveRequest(nomeFile, pulito);
			
			// aggiunto da Said 07-12-2011
			cleansRequest(requestAsDom);
			
			saveRequest(nomeFile, NodeToString.nodeToString(requestAsDom));
			index++;
			
			
		}
		return dirPulite;	
	}
	
public String getDefaultResource(){
		
		return "\n<Resource>" +
				"\n<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" " +
				"DataType=\"http://www.w3.org/2001/XMLSchema#string\">" +
				"\n<AttributeValue>DEFULT RESOURCE FOR SUN_PDP</AttributeValue>" +
				"\n</Attribute>" +
				"\n</Resource>";
	}
	private void cleansRequest(Node requestAsDom) {
		StringBuilder reqBuilder = new StringBuilder();
		System.out.println(NodeToString.nodeToString(requestAsDom));
		NodeList childrenList = requestAsDom.getChildNodes();
		boolean risorsa = false;
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node child = childrenList.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE){
				if(child.getNodeName().equals(ElementsName.Resource.toString()))
					risorsa = true;
				else
					risorsa = false;
				
				System.out.println(child.getNodeName());
				Vector<Node> daEliminare = cleanNodeAtLevelOne(child);
				for (Node node : daEliminare) {
					child.removeChild(node);
				}
				if(risorsa && !resourceId){
					risorsa = false;
					resourceId = false;
					addDefaultAttribute(child);
					
					System.err.println(NodeToString.nodeToString(child));
					
				}
					
			}
		}
		System.out.println(NodeToString.nodeToString(requestAsDom));
	}
		
	private void addDefaultAttribute(Node child) {
		
		Element attribute = doc.createElement(ElementsName.Attribute.toString());
		attribute.setAttribute(ElementsName.AttributeId.toString(), resId);
		attribute.setAttribute(ElementsName.DataType.toString(), XacmlDataTypes.STRING);
		
		Element attributeValue = doc.createElement(ElementsName.AttributeValue.toString());
		attributeValue.setTextContent("DEFULT RESOURCE FOR SUN_PDP");
		attribute.appendChild(attributeValue);
		
		child.appendChild(attribute);
		
	}

	private Vector<Node> cleanNodeAtLevelOne(Node nodeAtLevelOne) {
		int eliminati = 0, totali = 0;
		Vector<Node> daEliminare = new Vector<Node>();
		NodeList childrenList = nodeAtLevelOne.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node childAttribute = childrenList.item(i);
			if(childAttribute.getNodeType() == Node.ELEMENT_NODE){
				System.out.println(childAttribute.getNodeName());
				
				if(childAttribute.getNodeName().equals("Attribute")){
					totali++;
					NamedNodeMap attributes = childAttribute.getAttributes();
					if(!attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals("")
							&& !attributes.getNamedItem(ElementsName.DataType.toString()).getTextContent().equals("")){
						if(attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals(resId))
							resourceId = true;
						System.out.println(attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent());
						System.out.println(attributes.getNamedItem(ElementsName.DataType.toString()).getTextContent());
						
					}else{
						eliminati++;
						daEliminare.add(childAttribute);
					}
						
				}
//				cleanNodeAtLevelTwo(childAttribute);
			}
		}
		System.out.println("Totali    : "+totali);
		System.out.println("Eliminati : "+eliminati);
		System.out.println("quanti Da eliminare : "+daEliminare.size());
		return daEliminare;
	}

	private void cleanNodeAtLevelTwo(Node nodeAtLevelTwo) {
		int eliminati = 0, totali = 0;
		NodeList childrenList = nodeAtLevelTwo.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node child = childrenList.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE){
				System.out.println(child.getNodeName());
//				cleanNodeAtLevelTwo(child);
			}
		}
	}

//	dajhfdjhkahfkjhakhfkjahfkjhakjhfkjahfkjhakjh
	
	
	/**
	 * Scorre tutta la richiesta finale, e la ripulisce da tutti i campi che danno noia al PDP della SUN.
	 * Vengono cosi' eliminati tutti gli attributi che non hanno un valore, viene rimpiazzato l'header,
	 * viene mantenuto un solo <AttributeValue> per ogni <Attribute>, 
	 * vengono chiusi tutti quei tag di elementi che non presentano un AttributeId.
	 * @param reqNode il nodo principale dal quale raggiungere tutti gli altri.
	 * @param daPulire
	 * @return la stringa che sara' il contenuto della richiesta finale da sottoporre al PDP.
	 */
	private String scorre(Node reqNode, File daPulire){
		StringBuilder combReq = new StringBuilder();
		System.out.println("MA QUI... CI ARRIVA? 1");
		NodeList childrenEntityList = reqNode.getChildNodes();

		if (childrenEntityList.getLength() != 0){
			combReq.append(requestHeader());//ci metto lo stesso header come x le ric.semplici
			Node childEntity;
			for (int j = 0; j < childrenEntityList.getLength(); j++) { // ho i nodi principali
				System.out.println(childrenEntityList.item(j)+ " " + j); // cosi' stampa S,R,A,E
				childEntity = childrenEntityList.item(j); // x ese Subject
				if(childEntity.getNodeType() != Node.TEXT_NODE){
					System.out.println("MA QUI... CI ARRIVA? 2");
					System.out.println(childEntity.getNodeName() + " non e' testo");
					if(childEntity.getNodeName().equals(sub)){
						//System.out.println("si! e' uguale!");
						if(controlla(childEntity)){
							combReq.append("<"+ns+"Subject/>");
						}else{
							combReq.append("<"+ns+"Subject");
							combReq.append(cleanCategory(childEntity));
							combReq.append(cleanAttribute(childEntity));
							combReq.append("</"+ns+"Subject>");
							System.out.println(combReq);
						}
					}
					if(childEntity.getNodeName().equals(res)){
						//System.out.println("si! e' uguale!");
						if(controlla(childEntity)){
							//combReq.append("<Resource/>");
							combReq.append(" ");
						}else{
							combReq.append("<"+ns+"Resource>");
							combReq.append(cleanAttribute(childEntity));
							if(!newResource){
								combReq.append("<"+ns+"Attribute AttributeId=\""+resId+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">");
								combReq.append("<"+ns+"AttributeValue> </"+ns+"AttributeValue></"+ns+"Attribute>");
							}
							combReq.append("</"+ns+"Resource>");
//							combReq.append("<"+ns+"Resource>");
//							combReq.append(cleanAttribute(childEntity));
//							combReq.append("</"+ns+"Resource>");
//							if(!newResource){
//								combReq.append("<"+ns+"Resource>");
//								combReq.append("<"+ns+"Attribute AttributeId=\""+resId+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">");
//								combReq.append("<"+ns+"AttributeValue> </"+ns+"AttributeValue></"+ns+"Attribute>");
//								combReq.append("</"+ns+"Resource>");
//							}
							System.out.println(combReq);
						}
					}
					if(childEntity.getNodeName().equals(act)){
						//System.out.println("si! e' uguale!");
						if(controlla(childEntity)){
							combReq.append("<"+ns+"Action/>");
						}else{
							combReq.append("<"+ns+"Action>");
							combReq.append(cleanAttribute(childEntity));
							combReq.append("</"+ns+"Action>");
							System.out.println(combReq);
						}
					}
					if(childEntity.getNodeName().equals(env)){
						//System.out.println("si! e' uguale!");
						if(controlla(childEntity)){
							combReq.append("<"+ns+"Environment/>");
						}else{
							combReq.append("<"+ns+"Environment>");
							combReq.append(cleanAttribute(childEntity));
							combReq.append("</"+ns+"Environment>");
							System.out.println(combReq);
						}
					}
				}
			} //end for
			combReq.append("</"+ns+"Request>");
		}
		return combReq.toString();
	}

	/**
	 * Si occupa di inserire nel tag <Attribute> solo quegli attributi che presentano un valore.
	 * @param childEntity
	 * @return la stringa da inserire nei tag degli elementi principali.
	 */
	private String cleanAttribute(Node childEntity){
		StringBuilder subAttr = new StringBuilder();
		NodeList figliDelPrimo = childEntity.getChildNodes();
		if (figliDelPrimo.getLength() != 0){
			/* se questo figlio ha altri figli, prendo i suoi figli,<Attribute>
			 * faccio mappa di questo nodo
			 * devo appendere <Attribute 
			 * x ese setValue()
			 */
			newResource = false;
			int altraResource = 0;
			for (int k = 0; k < figliDelPrimo.getLength(); k++){
				//System.out.println(figliDelPrimo.item(k));
				String thisAttribute = "";
				Node attribute = figliDelPrimo.item(k);
				if(attribute.getNodeType() != Node.TEXT_NODE){
					//se capito qui => siamo nel secondo Attribute di questo Elemento
					//se questo ha AttributeId="" non devo scrivere niente
					if(controllAttr(attribute)){
						//System.out.println("QUI CE SO STATA!!!");
						break;
					}
					else{
						if(altraResource != 0){
							subAttr.append("</"+ns+"Resource><"+ns+"Resource>");
							altraResource = 0;
						}
						subAttr.append("<"+ns+"Attribute");
						thisAttribute = thisAttribute + "<"+ns+"Attribute";
						//System.out.println("MA QUI... CI ARRIVA? 3");
						/* prendo i suoi attributi e
						 * devo prendere i suoi figli <AttributeValue>
						 * CONVIENE SALVARE IN UNA STRINGA
						 * TUTTO QUELLO CHE VIENE COSTRUITO QUI!
						 */
						
						NamedNodeMap attribut2 = attribute.getAttributes();
						if(attribut2.getLength() != 0){
							for(int i = 0; i < attribut2.getLength(); i++){
								
								System.out.println(attribut2.item(i).getNodeValue());
								Node comp1 = attribut2.item(i);
								/*
								 * faccio 2 metodi da chiamare a seconda del caso??
								 * setto un booleano???
								 */
								
								/* se AttributeId="" o se DataType="" o se Issuer="" 
								 *non lo metto
								 *prendo il valore e scrivo append Attribute="qualcosa" DataType="qualcosa" Issuer="opsionale"*/
								if (comp1.getNodeName().equals(aId) && !comp1.getNodeValue().equals(subC)){
									if(comp1.getNodeValue().equals(resId)){
										newResource = true;
										altraResource = 1;
									}
									else 
										newResource = false;
									subAttr.append(" AttributeId=\"");
									subAttr.append(comp1.getNodeValue() + "\"");
									thisAttribute = thisAttribute + " AttributeId=\""+comp1.getNodeValue() + "\"";
								}
								if (comp1.getNodeName().equals(dT) && !comp1.getNodeValue().equals(subC)){
									subAttr.append(" DataType=\"");
									subAttr.append(comp1.getNodeValue() + "\"");
									thisAttribute = thisAttribute +" DataType=\""+comp1.getNodeValue() + "\"";

								}
								if (comp1.getNodeName().equals(iss) && !comp1.getNodeValue().equals(subC)){
									subAttr.append(" Issuer=\"");
									subAttr.append(comp1.getNodeValue() + "\"");
									thisAttribute = thisAttribute +" Issuer=\""+comp1.getNodeValue() + "\"";
								}
							}

						}// end if degli attributi
						subAttr.append("><"+ns+"AttributeValue>");
						thisAttribute = thisAttribute+"><"+ns+"AttributeValue>";
						/* da qui prendo i figli di <Attribute>
						 * leggo il contenuto del Tag<AttributeValue>
						 * e li appendo dentro degli <AttributeValue> uguali al primo
						 */
						NodeList figliAttribute = attribute.getChildNodes();
						if (figliAttribute.getLength() != 0){
							Node attributeValue = figliAttribute.item(1);
							/*	
							 * appendo la stringa che ho salvato
							 */
							String valore = attributeValue.getTextContent();
							//System.out.println("MA QUI... CI ARRIVA? 4");
							//System.out.println(valore);
							subAttr.append(valore);
							subAttr.append("</"+ns+"AttributeValue></"+ns+"Attribute>");
							
							for(int z = 2; z < figliAttribute.getLength(); z++){
								/*
								 * controllo il valore di newResource
								 * cosi' gli altri AttributeValue diventano Resource distinte
								 */
								Node attributeValue2 = figliAttribute.item(z);
								if(attributeValue2.getNodeType() != Node.TEXT_NODE){
									/*	
									 * appendo la stringa che ho salvato
									 */
									String valore2 = attributeValue2.getTextContent();
									String vuota = " ";
									if(!valore2.equals(vuota)&& !newResource){
										//System.out.println("MA QUI... CI ARRIVA? 5");
										//System.out.println(valore2);
										subAttr.append(thisAttribute);
										subAttr.append(valore2);
										subAttr.append("</"+ns+"AttributeValue></"+ns+"Attribute>");
									}
									else if(!valore2.equals(vuota)&& newResource){
										//System.out.println("MA QUI... CI ARRIVA? 6");
										//System.out.println(valore2);
										subAttr.append("</"+ns+"Resource><"+ns+"Resource>");
										subAttr.append(thisAttribute);
										subAttr.append(valore2);
										subAttr.append("</"+ns+"AttributeValue></"+ns+"Attribute>");
									}
								}
							}
						}
					//	if(newResource){
						//	subAttr.append("</"+ns+"Resource><"+ns+"Resource>");
						//}
					}
				}
//				if(!newResource){
//					subAttr.append("<"+ns+"Attribute AttributeId=\""+resId+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">");
//					subAttr.append("<"+ns+"AttributeValue> </"+ns+"AttributeValue></"+ns+"Attribute>");
//				}
			}
		}
		return subAttr.toString();
	}

	/**
	 * Serve per controllare se il tag Subject ha l'attirbuto SubjectCategory="valore"
	 * se SubjectCategory="" non lo scrive
	 * @param childEntity
	 * @return la stringa con SubjectCategory="valore" oppure ">"(per chiiudere <Subject.
	 */
	private String cleanCategory(Node childEntity){
		StringBuilder subCat = new StringBuilder();
		NamedNodeMap attributi = childEntity.getAttributes();
		if(attributi.getLength() != 0){
			//si tratta di sicuro di un Subject
			for(int i = 0; i < attributi.getLength(); i++){
				System.out.println(attributi.item(i).getNodeValue());//cosi' stampa SubjectCategory
				Node comp = attributi.item(i);
				if(comp.getNodeValue().equals(subC)){
					//System.out.println("E CHE CACCHIO!!!! e' entratoooooooooooooooooooooooooooooo");
					/*non ha un valore quindi non lo scrivo nel nuovo file, solo <Subject>
					 * chiamo il metodo x l'append*/
					subCat.append(">");
				}else{
					/*devo prendere il valore e lo devo inserire nel nuovo file <Subject SubjectCategoty="qualcosa">*/

					subCat.append(" SubjectCategory=\"");
					subCat.append(comp.getNodeValue());
					subCat.append("\">");
				}
			}
			//System.out.println(combReq);	
		}
		return subCat.toString();
	}

	/**
	 * Controlla se l'elemento ha AttributeId="elemento-id", perche' questo attributo e' obbligatorio
	 * se non c'e' La valutazione del PDP portera' cmq a un errore, quindi chiudiamo subito il tag dell'elemento
	 * @param childEntity
	 * @return true se AttributeId="", false altrimenti.
	 */
	private boolean controlla(Node childEntity){
		boolean vuoto = false;

		NodeList presente = childEntity.getChildNodes();//x ese sono i figli di Subject => Attribute
		if(presente.getLength()!=0 ){
			Node daCercare = presente.item(1);
			//		for(int h = 0; h < presente.getLength(); h++){
			//		Node daCercare = presente.item(h);
			if(daCercare.getNodeType() != Node.TEXT_NODE){
				//System.out.println("Questo nodo e' un elemento ATTRIBUTE!!! "+daCercare);
				NamedNodeMap cercoId = daCercare.getAttributes();
				Node attrId = cercoId.item(0);
				if (attrId.getNodeName().equals(aId) && attrId.getNodeValue().equals(subC)){
					//System.out.println("##########################################################");
					vuoto = true;
				}
			}
		}
		return vuoto;
	}

	/**
	 * Controlla se l'Attribute ha AttributeId="elemento-id", perche' questo attributo e' obbligatorio
	 * se non c'e' La valutazione del PDP portera' cmq a un errore, quindi non lo facciamo scrivere.
	 * @param childEntity
	 * @return true se AttributeId="", false altrimenti.
	 */
	private boolean controllAttr(Node childEntity){
		boolean vuoto = false;

		NamedNodeMap presente = childEntity.getAttributes();//gli attributi di Attribute
		Node daCercare = presente.item(0);
		//		for(int h = 0; h < presente.getLength(); h++){
		//		Node daCercare = presente.item(h);
		if(daCercare.getNodeType() != Node.TEXT_NODE && daCercare.getNodeName().equals(aId) && daCercare.getNodeValue().equals(subC)){
			//System.out.println("Questo nodo e' un elemento ATTRIBUTE e controllo il suo ID!!! "+daCercare);
			
			//if (attrId.getNodeName().equals(aId) && attrId.getNodeValue().equals(subC)){
				//System.out.println("##########################################################");
				vuoto = true;
			//}
		}
		//}
		return vuoto;
	}

	/**
	 * Inserisce questo header nella richiesta finale.
	 * @return la stringa con cui iniziera' il file della richiesta finale.
	 */

	private String requestHeader(){
		StringBuilder builder = new StringBuilder();
		// solo per tas3
		boolean tas = false;
		if(tas)
			builder.append("<"+ns+"Request "+xmlns + ">");
		else{			
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		builder.append("<"+ns+"Request xmlns=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" ");
		builder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		builder.append(" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:context:schema:os access_control-xacml-2.0-context-schema-os.xsd\">");
		}
		//...
//		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
//		builder.append("<Request xmlns=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" ");
//		builder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
//		builder.append(" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:context:schema:os access_control-xacml-2.0-context-schema-os.xsd\">");
		return builder.toString();
	}

	/**
	 * Permette di selezionare il nodo radice
	 * si utiizza XPath, sappiamo cosa cercare e dove.
	 * @param filename
	 * @param validating
	 * @return
	 */
	private Node getFileAsDom(File filename, boolean validating) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validating);
			doc = factory.newDocumentBuilder().parse(filename);

			NodeList nodelist = XPathAPI.selectNodeList(doc, "/*");
			return nodelist.item(0);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		} catch (TransformerException e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * Scrive sul file system la richiesta appena ripulita.
	 * @param requestName il nome del file.
	 * @param request il contenuto da scrivere nel file.
	 */
	private void saveRequest(String requestName, String request){
		try {	
			BufferedWriter requestWriter;
			//File req = new File(nomePolicy.getCanonicalFile()+File.separator+requestName);
			File req = new File(dirPulite.getCanonicalFile()+File.separator+requestName);
			//System.out.println(req.getCanonicalPath());
			if(req.exists())
				req.delete();
			req.createNewFile();
			requestWriter = new BufferedWriter(new FileWriter(req.getCanonicalPath()));
			requestWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			requestWriter.write(request);
			requestWriter.flush();
			requestWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
