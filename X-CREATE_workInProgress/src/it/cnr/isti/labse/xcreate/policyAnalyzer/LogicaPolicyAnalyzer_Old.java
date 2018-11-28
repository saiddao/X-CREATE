package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import it.cnr.isti.labse.xcreate.dbDrivers.EXistDBDriver;
import it.cnr.isti.labse.xcreate.guiXCREATE.GuiCons;
import it.cnr.isti.labse.xcreate.util.TreeUtil;
import it.cnr.isti.labse.xcreate.xQuery.ElementsName;
import it.cnr.isti.labse.xcreate.xQuery.FragmentedXQueryLoader;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * E' la classe starter del componente PolicyAnalyzer. 
 * 
 * Analizza la politica sotto test.
 * data una politica XACML in formato xml
 * data la collezione che contiene la politica XACML
 * 
 *  analizza politica
 *  memorizza risultato analisi politica 
 * 	mostra risultato analisi politica 
 */
public class LogicaPolicyAnalyzer_Old {
	private boolean condition = false;
	
	private static String MXsub = "MaxSubject";
	private static String MXres = "MaxResource";
	private static String MXact = "MaxAction";
	private static String MXenv = "MaxEnvironment";

	private int MaxNumSub;
	private int MaxNumRes;
	private int MaxNumAct;
	private int MaxNumEnv;
	
	private EXistDBDriver driverEXistDB;
	private String policyName;
	private ResourceSet risultatoXQuery;
	private XMLResource xmlResource;
	private Hashtable<String, Nodo> sets;
	//----------------------------------------------
	private ResourceSet risultatoQueryStatistica;
	private XMLResource xmlStatistical;
	//----------------------------------------------
	
	public LogicaPolicyAnalyzer_Old(EXistDBDriver eXistDBDriver){
		this.driverEXistDB = eXistDBDriver;
		this.policyName = "";
		this.MaxNumSub = 0;
		this.MaxNumRes = 0;
		this.MaxNumAct = 0;
		this.MaxNumEnv = 0;
		//this.driverEXistDB.connect();
	}

	public void analizzaPolitica(String policyName, File policyDir){
		this.policyName = policyName;

		/*
		XQueryLoader queryLoader = new XQueryLoader(this.policyName);
		this.risultatoXQuery = this.driverEXistDB.execute(queryLoader.getXQuery());
		 */

		FragmentedXQueryLoader xQueryLoader = new FragmentedXQueryLoader(this.policyName);
		//System.out.println(xQueryLoader.getXQuery());
		this.risultatoXQuery = this.driverEXistDB.execute(xQueryLoader.getXQuery());
		ResourceIterator resourceIterator;
		try {
			//System.out.println("Quanto risultato Xquery ? --> "+this.risultatoXQuery.getSize());
			//System.out.println(this.risultatoXQuery.toString());
			resourceIterator = this.risultatoXQuery.getIterator();
			while(resourceIterator.hasMoreResources()) {
				Resource r = resourceIterator.nextResource();
				this.xmlResource = (XMLResource) r;
				//System.out.println(this.xmlResource.getContent());

				Node xmlNode = this.xmlResource.getContentAsDOM();
				ParserDOM parserDOM = new ParserDOM();
				this.sets = parserDOM.getSets(xmlNode);
				addRandomValues();
				//ParserDOMForMySQL domForMySQL = new ParserDOMForMySQL(this.policyName);
				//domForMySQL.getSets(xmlNode);
			}
			saveResult(policyDir, this.xmlResource.getContent().toString());

		} catch (XMLDBException e) {
			e.printStackTrace();
		}

		//-------------------------------------------
		this.risultatoQueryStatistica = this.driverEXistDB.execute(getStatisticalXQuery());// il rif. al file è nella query
		ResourceIterator statisticalIterator;

		try {
			statisticalIterator = this.risultatoQueryStatistica.getIterator();//NullPointerException
			while(statisticalIterator.hasMoreResources()){
				Resource res = statisticalIterator.nextResource();
				this.xmlStatistical = (XMLResource) res;

				Node xmlNode = this.xmlStatistical.getContentAsDOM();
				parseStatistical(xmlNode);
				if(condition){
					setMaxFromCondition();
					System.out.println("Vado a prendere i valori in ConditionAnalyzer");
				}

			}

		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		//-------------------------------------------
	}
	private void addRandomValues() {
		System.out.println("Aggiungi RandomValues a SubjectSet");
		NewRandomValuesManager.addRamdonValues(this.sets.get(String.valueOf(ElementsName.SubjectSet)));
		System.out.println("Aggiungi RandomValues a ResourceSet");
		NewRandomValuesManager.addRamdonValues(this.sets.get(String.valueOf(ElementsName.ResourceSet)));
		System.out.println("Aggiungi RandomValues a ActionSet");
		NewRandomValuesManager.addRamdonValues(this.sets.get(String.valueOf(ElementsName.ActionSet)));
		System.out.println("Aggiungi RandomValues a EnvironmentSet");
		NewRandomValuesManager.addRamdonValues(this.sets.get(String.valueOf(ElementsName.EnvironmentSet)));
	}


	public Nodo getSubjectSet(){
		//System.out.println("CARDINALITA' :::  "+this.sets.get(String.valueOf(ElementsName.SubjectSet)).getCardinalityTuplaSet());
		return this.sets.get(String.valueOf(ElementsName.SubjectSet));
	}
	public List<String> getSubjectSetAsList(){
		return TreeUtil.getTuplaSetAsList(getSubjectSet());
	}
	public Nodo getResourceSet(){
		//	System.out.println("CARDINALITA' :::  "+this.sets.get(String.valueOf(ElementsName.ResourceSet)).getCardinalityTuplaSet());
		return this.sets.get(String.valueOf(ElementsName.ResourceSet));		
	}

	public List<String> getResourceSetAsList(){
		return TreeUtil.getTuplaSetAsList(getResourceSet());
	}

	public Nodo getActionSet(){
		//	System.out.println("CARDINALITA' :::  "+this.sets.get(String.valueOf(ElementsName.ActionSet)).getCardinalityTuplaSet());
		return this.sets.get(String.valueOf(ElementsName.ActionSet));
	}
	public List<String> getActionSetAsList(){
		return TreeUtil.getTuplaSetAsList(getActionSet());
	}
	public Nodo getEnvironmentSet(){
		//	System.out.println("CARDINALITA' :::  "+this.sets.get(String.valueOf(ElementsName.EnvironmentSet)).getCardinalityTuplaSet());
		return this.sets.get(String.valueOf(ElementsName.EnvironmentSet));
	}
	public List<String> getEnvironmentSetAsList(){
		return TreeUtil.getTuplaSetAsList(getEnvironmentSet());
	}

	// le entita' come HashTable
	public Hashtable<String, Tupla> getSubjectSetAsHash(){
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		tupleHashTable.putAll(this.sets.get(String.valueOf(ElementsName.SubjectSet)).getTupleHashTable());

		return tupleHashTable;
	}
	public Hashtable<String, Tupla> getResourceSetAsHash(){
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		tupleHashTable.putAll(this.sets.get(String.valueOf(ElementsName.ResourceSet)).getTupleHashTable());
		return tupleHashTable;
	}
	public Hashtable<String, Tupla> getActionSetAsHash(){
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		tupleHashTable.putAll(this.sets.get(String.valueOf(ElementsName.ActionSet)).getTupleHashTable());

		return tupleHashTable;
	}
	public Hashtable<String, Tupla> getEnvironmentSetAsHash(){
		Hashtable<String, Tupla> tupleHashTable = new Hashtable<String, Tupla>();
		tupleHashTable.putAll(this.sets.get(String.valueOf(ElementsName.EnvironmentSet)).getTupleHashTable());

		return tupleHashTable;
	}



	public void saveResult(File tempDir, String content){
		try {	
			BufferedWriter requestWriter;
			File req = new File(tempDir.getCanonicalFile()+GuiCons.DIR_SEPARATOR+"result_"+policyName);
			//System.out.println(req.getCanonicalPath());
			if(req.exists())
				req.delete();
			req.createNewFile();
			requestWriter = new BufferedWriter(new FileWriter(req.getCanonicalPath()));
			requestWriter.write(content);
			requestWriter.flush();
			requestWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exit(){
		this.driverEXistDB.disconnect();
	}
	//-----------------------------------------------

	private String getStatisticalXQuery() {
		StringBuilder xQueryBuilder = new StringBuilder();
		xQueryBuilder.append(" declare default element namespace \"urn:oasis:names:tc:xacml:2.0:policy:schema:os\"; ");
		xQueryBuilder.append("		(:    Prologo Namespaces    :) ");
		xQueryBuilder.append(" declare namespace xs=\"http://www.w3.org/2001/XMLSchema\"; ");
		xQueryBuilder.append(" declare namespace xacml=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\"; ");
		xQueryBuilder.append(" declare namespace xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"; ");
		xQueryBuilder.append(" declare namespace targetNamespace=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"; ");
		xQueryBuilder.append(" declare namespace schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os        http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\"; ");
		xQueryBuilder.append(" declare namespace xsi=\"http://www.w3.org/2001/XMLSchema-instance\"; ");
		xQueryBuilder.append(" (:      Corpo Xquery  :) ");
		xQueryBuilder.append(" let $root := doc(\"" + this.policyName+ "\")/child::*[1] ");
		xQueryBuilder.append(" let $resultSub := 	<ResultS>{ ");
		xQueryBuilder.append(" for $target in $root/descendant::Subject ");
		xQueryBuilder.append(" return ");
		xQueryBuilder.append(" <countSub>{ count($target/child::*)}</countSub> ");
		xQueryBuilder.append(" }</ResultS> ");
		xQueryBuilder.append("  ");
		xQueryBuilder.append(" let $resultRes := 	<ResultR>{ ");
		xQueryBuilder.append(" for $target in $root/descendant::Resource ");
		xQueryBuilder.append(" return ");
		xQueryBuilder.append(" <countRes>{ count($target/child::*)}</countRes> ");
		xQueryBuilder.append(" }</ResultR> ");
		xQueryBuilder.append(" let $resultAct := 	<ResultA>{ ");
		xQueryBuilder.append(" for $target in $root/descendant::Action ");
		xQueryBuilder.append(" return " );
		xQueryBuilder.append(" <countAct>{ count($target/child::*)}</countAct> ");
		xQueryBuilder.append(" }</ResultA> ");
		xQueryBuilder.append(" let $resultEnv := 	<ResultE>{ ");
		xQueryBuilder.append(" for $target in $root/descendant::Environment ");
		xQueryBuilder.append(" return ");
		xQueryBuilder.append(" <countEnv>{ count($target/child::*)}</countEnv> ");
		xQueryBuilder.append(" }</ResultE> ");
		xQueryBuilder.append(" return <Statistica> ");
		xQueryBuilder.append(" <MaxSubject>{max($resultSub//countSub)}</MaxSubject> ");
		xQueryBuilder.append(" <Subject>{$resultSub}</Subject> ");
		xQueryBuilder.append(" <MaxResource>{max($resultRes//countRes)}</MaxResource> ");
		xQueryBuilder.append(" <Resource>{$resultRes}</Resource> ");
		xQueryBuilder.append(" <MaxAction>{max($resultAct//countAct)}</MaxAction> ");
		xQueryBuilder.append(" <Action>{$resultAct}</Action> ");
		xQueryBuilder.append(" <MaxEnvironment>{max($resultEnv//countEnv)}</MaxEnvironment> ");
		xQueryBuilder.append(" <Environment>{$resultEnv}</Environment> ");
		xQueryBuilder.append(" </Statistica>");

		return xQueryBuilder.toString();
	}
/**
 * metodo che effettua il parsing del risultato statistico
 * e setta le variabili per ogni valore max
 * @param xmlNode
 */
	private void parseStatistical(Node xmlNode) {
		
		NodeList childrenEntityList = xmlNode.getChildNodes();

		if (childrenEntityList.getLength() != 0){
			Node childEntity;
			for (int j = 0; j < childrenEntityList.getLength(); j++) { // ho i nodi principali
				System.out.println(childrenEntityList.item(j)+ " " + j);
				childEntity = childrenEntityList.item(j); // x ese MaxSubject, Subject e gli altri
				
				if(childEntity.getNodeType() != Node.TEXT_NODE){
					System.out.println("-------------------------------");
					System.out.println(childEntity.getNodeName() + " non e' testo... "+ childEntity.getNodeType()+"-tipo");
					if(childEntity.getNodeName().equals(MXsub)){
						
						NodeList prova = childEntity.getChildNodes();
						if(prova.getLength() != 0){
							Node childProva;
							for (int i = 0; i < prova.getLength(); i++){
								childProva = prova.item(i);
								if(childProva.getNodeType() == Node.TEXT_NODE){
									System.out.println("stampo I "+i);
									System.out.println("nome "+childProva.getNodeName());
									System.out.println("valore "+childProva.getNodeValue());
									String maxsub = childProva.getNodeValue();
									this.MaxNumSub = Integer.parseInt(maxsub);
									System.out.println("valore massimo sub --- "+ MaxNumSub);
								}
							}
						}
					}
					else if(childEntity.getNodeName().equals(MXres)){
						
						NodeList prova = childEntity.getChildNodes();
						if(prova.getLength() != 0){
							Node childProva;
							for (int i = 0; i < prova.getLength(); i++){
								childProva = prova.item(i);
								if(childProva.getNodeType() == Node.TEXT_NODE){
									System.out.println("stampo I "+i);
									System.out.println("nome "+childProva.getNodeName());
									System.out.println("valore "+childProva.getNodeValue());
									String maxres = childProva.getNodeValue();
									this.MaxNumRes = Integer.parseInt(maxres);
									System.out.println("valore massimo res --- "+ MaxNumRes);
								}
							}
						}
					}
					else if(childEntity.getNodeName().equals(MXact)){
						
						NodeList prova = childEntity.getChildNodes();
						if(prova.getLength() != 0){
							Node childProva;
							for (int i = 0; i < prova.getLength(); i++){
								childProva = prova.item(i);
								if(childProva.getNodeType() == Node.TEXT_NODE){
									System.out.println("stampo I "+i);
									System.out.println("nome "+childProva.getNodeName());
									System.out.println("valore "+childProva.getNodeValue());
									String maxact = childProva.getNodeValue();
									this.MaxNumAct = Integer.parseInt(maxact);
									System.out.println("valore massimo act --- "+ MaxNumAct);
								}
							}
						}
					}
					else if(childEntity.getNodeName().equals(MXenv)){
						
						NodeList prova = childEntity.getChildNodes();
						if(prova.getLength() != 0){
							Node childProva;
							for (int i = 0; i < prova.getLength(); i++){
								childProva = prova.item(i);
								if(childProva.getNodeType() == Node.TEXT_NODE){
									System.out.println("stampo I "+i);
									System.out.println("nome "+childProva.getNodeName());
									System.out.println("valore "+childProva.getNodeValue());
									String maxenv = childProva.getNodeValue();
									this.MaxNumEnv = Integer.parseInt(maxenv);
									System.out.println("valore massimo env --- "+ MaxNumEnv);
								}
							}
						}
					}
				}
			}
		}	
	}
	/*
	 * creare una variabile per ogni valore max OK
	 * creare un metodo per ogni valore max
	 * utilizzare quest'ultimi per ottenere i risultati. 
	 */
	public int getMaxNumSubject(){
		return this.MaxNumSub;
	}
	public int getMaxNumResource(){
		return this.MaxNumRes;
	}
	public int getMaxNumAction(){
		return this.MaxNumAct;
	}
	public int getMaxNumEnvironment(){
		return this.MaxNumEnv;
	}
	
	/**
	 * da qualche parte prevedere la statistica delle <Condition>
	 * vuoi tener conto della valutazione delle Condition ??
	 * true --- false
	 */
	public void setMaxFromCondition(){
		ConditionAnalyzer cond = new ConditionAnalyzer();
		
		int sub = cond.getNumSubject();
		if(sub > this.MaxNumSub){ // <--- sicuri???
			this.MaxNumSub = sub;
		}
		int res = cond.getNumResource();
		if (res > this.MaxNumRes){
			this.MaxNumRes = res;
		}
		int act = cond.getNumAction();
		if (act > this.MaxNumAct){
			this.MaxNumAct = act;
		}
		int env = cond.getNumEnvironment();
		if (env > this.MaxNumEnv){
			this.MaxNumEnv = env;
		}	
	}
	
}