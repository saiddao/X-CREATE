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
 *	N.B. <ResourceContent> ... non si considera... :)
 *
 *	Per i Resource -- non ci puo' essere + di un resource-id
 *	tengo controllato se e' gia' stato scritto e scrivo una nuova Resource 
 *	ogni volta che lo incontro di nuovo.
 *
 */
public class Cleaning {
	private final String DEFULT_RESOURCE_CONTENT = "DEFAULT RESOURCE FOR SUN_PDP";
	private String resId = "urn:oasis:names:tc:xacml:1.0:resource:resource-id"; 
	private File[] daPulire;
	private File dir; 
	private File dirPulite;
	private String nomeFile;
	// FIXME modifiche apportate da Said
	private boolean resourceId;
	private Document doc;
	private int attributesEliminati;
	private int attributesTotali;
	// FIXME modifica 16-04-2012
	private int numeroResourceId;
	private int numeroResourceIdRichiestaAttuale;
	
	/* Il costruttore di questa classe prende la directory dove ci sono le richieste finali
	 * verra' chiamato tramite un metodo che restituira' all'interfaccia 
	 * la directory con i file puliti. 
	 * 
	 * */
	/**
	 * Costruttore
	 * dir e' la directory dove sono le richieste riempite
	 * dirPulite e' la directory dove sono le richieste pulite
	 */
	public Cleaning(File dir ){
		this.dir = dir;
	}
	/*
	 * finche' ci sono file lui li passa li pulisce e li scrive in una sotto directory 
	 * che va restituita 
	 */
	public File cleaner(){		
		/*
		 * creo una cartella di nome Cleaned_dir.getName() dove scrivero' i file
		 * controllo se esiste, poi creo l'array di File, 
		 */		
		if(this.dir.getName().equals(GuiCons.COMB_FROM_INTER_REQ_DIR_NAME)){
			this.dirPulite = new File (dir.getParent()+File.separator+"Cleaned_"+dir.getName());
		}else {
			//quelle gerarchiche
			String sub = this.dir.getName();
			this.dirPulite = new File (this.dir.getParent()+File.separator+sub+"_Cleaned");
		}
		if (!this.dirPulite.exists()){
			this.dirPulite.mkdirs();
		}
		this.daPulire = this.dir.listFiles();
		for (int index = 0; index < this.daPulire.length; index++) {
			System.out.println("Numero File " +daPulire.length);
			this.resourceId = false;			
			this.nomeFile = this.daPulire[index].getName();
			System.out.println("File : "+this.nomeFile+ " " +index);
			Node requestAsDom = getFileAsDom(this.daPulire[index], false);
			// aggiunto da Said 07-12-2011
			cleansRequest(requestAsDom);
			
			// FIXME aggiunto il 16-04-2012
			/*
			 * problema:
			 * quando in una richiesta sono gia' presenti piu' di un 
			 * "ResourceId" viene gienerata una richiesta che li contiene tutti.
			 * Questo fatto crea problemi al pdp della Sun in quanto accetta e elabora solo
			 * richieste con un unico "ResourceId".
			 * Infatti, la valutazione di tali richietste porta sempre al solito risultato
			 * "Indeterminate"
			 * 
			 * Soluzione:
			 * per ogni "ResoureId", creare una richiesta separata.
			 * 
			 */
			if(numeroResourceIdRichiestaAttuale > 1){
				moltiplicaRichietse(requestAsDom);
//				saveRequest(this.nomeFile, NodeToString.nodeToStringNew(requestAsDom));
			}else{
				saveRequest(this.nomeFile, NodeToString.nodeToStringNew(requestAsDom));
			}
		}
		return this.dirPulite;	
	}
	/*
	 * 
	 * TODO TODO TODO 
	 * TODO TODO TODO 
	 * FIXME aggiunto il 16-04-2012
	 * FIXME
	 * FIXME
	 * FIXME
	 * FIXME
	 */
	private void moltiplicaRichietse(Node requestAsDom) {
		NodeList childrenList = requestAsDom.getChildNodes();
		Node resourceElement = null;
		Vector<Node> attributesId = new Vector<Node>();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node child = childrenList.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				switch (StringToEnum.valueOf(child.getNodeName())) {
				case Resource:
					resourceElement = child;
					NodeList resChildrenList = child.getChildNodes();
					for (int j = 0; j < resChildrenList.getLength(); j++) {
						Node attribute = resChildrenList.item(j);
						if(attribute.getNodeType() == Node.ELEMENT_NODE){
							if(attribute.getNodeName().equals(ElementsName.Attribute.toString())){
								NamedNodeMap attributes = attribute.getAttributes();
								if(attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals(resId)){
									attributesId.add(attribute);
								}
							}
						}
					}
					break;
				default:
					break;
				}
			}
		}
		/**
		 * Per ogni risorsa con attributeId = resourceId
		 * si genera una richiesta.
		 */
		for (int i = 0; i < attributesId.size(); i++) {
			resourceElement.removeChild(attributesId.get(i));
			System.out.println(NodeToString.nodeToStringNew(requestAsDom));
			String fileNameNew = this.nomeFile.substring(0, this.nomeFile.length()-4);
			saveRequest(fileNameNew+"_"+i+".xml", NodeToString.nodeToStringNew(requestAsDom));
			resourceElement.appendChild(attributesId.get(i));
		}
	}
	private void cleansRequest(Node requestAsDom) {
		NodeList childrenList = requestAsDom.getChildNodes();
		Vector<Node> subjectsNodes = new Vector<Node>();
		Vector<Node> resourcesNodes = new Vector<Node>();
		Vector<Node> actionsNodes = new Vector<Node>();
		Vector<Node> environmentsNodes = new Vector<Node>();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node child = childrenList.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE){
				switch (StringToEnum.valueOf(child.getNodeName())) {
				case Subject:
					subjectsNodes.add(child);
					requestAsDom.removeChild(child);
					break;
				case Resource:
					resourcesNodes.add(child);
					requestAsDom.removeChild(child);
					break;
				case Action:
					actionsNodes.add(child);
					requestAsDom.removeChild(child);
					break;
				case Environment:
					environmentsNodes.add(child);
					requestAsDom.removeChild(child);
					break;
				default:
					break;
				}
			}
		}
		
		requestAsDom.appendChild(cleanSubjects(subjectsNodes, requestAsDom));
		
		requestAsDom.appendChild(cleanResources(resourcesNodes, requestAsDom));
		
		requestAsDom.appendChild(cleanActions(actionsNodes, requestAsDom));
		
		requestAsDom.appendChild(cleanEnvironments(environmentsNodes, requestAsDom));
	}
	
	private Element cleanSubjects(Vector<Node> subjectsNodes, Node requestAsDom) {
		Vector<Node> subAttrNodes = new Vector<Node>();
		for (Node subNode : subjectsNodes) {
			subAttrNodes.addAll(getAttributeNodes(subNode));
		}
	
		Element newSub = getNewElement(ElementsName.Subject.toString());
		for (Node node : subAttrNodes) {
			newSub.appendChild(node);
		}
		
		return newSub;	
	}
	
	private Element cleanResources(Vector<Node> resourcesNodes, Node requestAsDom) {
		resourceId = false;
		numeroResourceIdRichiestaAttuale = 0;
		Vector<Node> resAttrNodes = new Vector<Node>();
		for (Node envNode : resourcesNodes) {
			resAttrNodes.addAll(getAttributeNodes(envNode));
		}
		if(numeroResourceId > 1)
			numeroResourceIdRichiestaAttuale = numeroResourceId;
		System.err.println("Cleaning.cleanResources() -> Nome File  = "+this.nomeFile+"   numeroResourceId = "+numeroResourceId);
		Element newRes = getNewElement(ElementsName.Resource.toString());
		for (Node node : resAttrNodes) {
			newRes.appendChild(node);
		}
		if(!resourceId)
			addDefaultAttribute(newRes);
		return newRes;	
	}

	private Element cleanActions(Vector<Node> actionsNodes, Node requestAsDom) {
		Vector<Node> actAttrNodes = new Vector<Node>();
		for (Node envNode : actionsNodes) {
			actAttrNodes.addAll(getAttributeNodes(envNode));
		}
		
		Element newAct = getNewElement(ElementsName.Action.toString());
		for (Node node : actAttrNodes) {
			newAct.appendChild(node);
		}
		
		return newAct;
	}

	private Element cleanEnvironments(Vector<Node> environmentsNodes, Node requestAsDom) {
		Vector<Node> envAttrNodes = new Vector<Node>();
		for (Node envNode : environmentsNodes) {
			envAttrNodes.addAll(getAttributeNodes(envNode));
		}
		
		Element newEnv = getNewElement(ElementsName.Environment.toString());
		for (Node node : envAttrNodes) {
			newEnv.appendChild(node);
		}
		
		return newEnv;
	}

	/**
	 * 
	 * @param elementName
	 * @return
	 */
	// modificato Lunedi' 16 Aprile 2012
	private Element getNewElement(String elementName) {	
		Element element = this.doc.createElement(elementName);
		return element;
	}

	private Vector<Node> getAttributeNodes(Node elementNode) {
		numeroResourceId = 0;
		Vector<Node> daNonEliminare = new Vector<Node>();
		NodeList childrenList = elementNode.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node childAttribute = childrenList.item(i);
			if(childAttribute.getNodeType() == Node.ELEMENT_NODE){
				if(childAttribute.getNodeName().equals(ElementsName.Attribute.toString())){
					NamedNodeMap attributes = childAttribute.getAttributes();
					if(!attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals("")
							&& !attributes.getNamedItem(ElementsName.DataType.toString()).getTextContent().equals("")){
						if(attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals(resId)){
							this.resourceId = true;
							numeroResourceId++;
						}
						daNonEliminare.add(childAttribute);
					}
				}
			}
		}
		return daNonEliminare;
	}



	private Vector<Node> cleanNodeAtLevelOne(Node nodeAtLevelOne) {
		this.attributesEliminati = 0;
		this.attributesTotali = 0;
		Vector<Node> daEliminare = new Vector<Node>();
		NodeList childrenList = nodeAtLevelOne.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++) {
			Node childAttribute = childrenList.item(i);
			if(childAttribute.getNodeType() == Node.ELEMENT_NODE){
				System.out.println(childAttribute.getNodeName());
				if(childAttribute.getNodeName().equals(ElementsName.Attribute.toString())){
					attributesTotali++;
					NamedNodeMap attributes = childAttribute.getAttributes();
					if(!attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals("")
							&& !attributes.getNamedItem(ElementsName.DataType.toString()).getTextContent().equals("")){
						if(attributes.getNamedItem(ElementsName.AttributeId.toString()).getTextContent().equals(resId))
							this.resourceId = true;
					}else{
						attributesEliminati++;
						daEliminare.add(childAttribute);
					}
				}
			}
		}
		
		System.out.println("Totali    : "+attributesTotali);
		System.out.println("Eliminati : "+attributesEliminati);
		System.out.println("quanti Da eliminare : "+daEliminare.size());
		return daEliminare;
	}
	private void addDefaultAttribute(Node child) {
			Element attribute = this.doc.createElement(ElementsName.Attribute.toString());
			attribute.setAttribute(ElementsName.AttributeId.toString(), this.resId);
			attribute.setAttribute(ElementsName.DataType.toString(), XacmlDataTypes.STRING);
			Element attributeValue = this.doc.createElement(ElementsName.AttributeValue.toString());
			attributeValue.setTextContent(DEFULT_RESOURCE_CONTENT);
			attribute.appendChild(attributeValue);
			child.appendChild(attribute);
		}


	/**
	 * Permette di selezionare il nodo radice.
	 * Si utiizza XPath.
	 * @param filename
	 * @param validating
	 * @return
	 */
	private Node getFileAsDom(File filename, boolean validating) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validating);
			this.doc = factory.newDocumentBuilder().parse(filename);
			NodeList nodelist = XPathAPI.selectNodeList(this.doc, "/*");
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
			File req = new File(this.dirPulite.getCanonicalFile()+File.separator+requestName);
			if(req.exists())
				req.delete();
			req.createNewFile();
			requestWriter = new BufferedWriter(new FileWriter(req.getCanonicalPath()));
//			requestWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						
			requestWriter.write(request);
			requestWriter.flush();
			requestWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
