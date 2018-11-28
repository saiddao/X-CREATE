package it.cnr.isti.labse.xcreate.policyAnalyzer;

import java.util.Hashtable;

import it.cnr.isti.labse.xcreate.util.GuidFactory;
import it.cnr.isti.labse.xcreate.util.NodeToString;
import it.cnr.isti.labse.xcreate.util.StringToEnum;
import it.cnr.isti.labse.xcreate.xQuery.ElementsName;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ParserDOM {

	private GuidFactory nodeGuidFactory;
	private GuidFactory subGuidFactory;
	private GuidFactory	resGuidFactory;
	private GuidFactory	actGuidFactory;
	private GuidFactory	envGuidFactory;

	// TODO 
	//private GuidFactory factory;

	private Nodo subjectSetTree;
	private Nodo resourceSetTree;
	private Nodo actionSetTree;
	private Nodo environmentSetTree;
	private Hashtable<String, Nodo> sets;

	public ParserDOM(){
		//this.factory = new GuidFactory();

		this.sets = new Hashtable<String, Nodo>(4);

		this.subGuidFactory = new GuidFactory();
		this.resGuidFactory = new GuidFactory();
		this.actGuidFactory = new GuidFactory();
		this.envGuidFactory = new GuidFactory();
		this.nodeGuidFactory = new GuidFactory();

		String rootId = this.nodeGuidFactory .getGuid();
		this.subjectSetTree = new Nodo(null, rootId);
		this.resourceSetTree = new Nodo(null, rootId);
		this.actionSetTree = new Nodo(null, rootId);
		this.environmentSetTree = new Nodo(null, rootId);

		this.sets.put(String.valueOf(ElementsName.SubjectSet),this.subjectSetTree );
		this.sets.put(String.valueOf(ElementsName.ResourceSet),this.resourceSetTree);
		this.sets.put(String.valueOf(ElementsName.ActionSet), this.actionSetTree);
		this.sets.put(String.valueOf(ElementsName.EnvironmentSet), this.environmentSetTree);
	}

	public Hashtable<String, Nodo> getSets(Node nodoXML){
		
		this.subjectSetTree.setIdXACML(nodoXML.getAttributes().getNamedItem(String.valueOf(ElementsName.Id)).getNodeValue());
				
		resultParser(nodoXML, this.subjectSetTree, this.resourceSetTree, this.actionSetTree, this.environmentSetTree);

		this.subjectSetTree.setCardinalityTuplaSet(this.subGuidFactory.getSize());
		this.resourceSetTree.setCardinalityTuplaSet(this.resGuidFactory.getSize());
		this.actionSetTree.setCardinalityTuplaSet(this.actGuidFactory.getSize());
		this.environmentSetTree.setCardinalityTuplaSet(this.envGuidFactory.getSize());

		return this.sets;
	}

	public void resultParser(Node nodoXML, Nodo nodoSubject, 
			Nodo nodoResource, Nodo nodoAction, 
			Nodo nodoEnvironment){
		Node xmlNode;
		String idString;
		Nodo nodoSubjectChild;
		Nodo nodoResourceChild;
		Nodo nodoActionChild;
		Nodo nodoEnvironmentChild;

		//System.out.println("PARSER DOM : : :   NomeNodo = "+nodoXML.getLocalName());
		nodoSubject.setNodoName(nodoXML.getLocalName());
		

		xmlNode = nodoXML.getFirstChild();

		while(xmlNode != null){

			if(xmlNode.getNodeType() == Node.ELEMENT_NODE){
				switch (StringToEnum.valueOf(xmlNode.getLocalName())) {
				case RisultatoTarget:
					/**
					 * ANALIZZA RISULTATO
					 * popolamento  
					 */
					risultatoTargetParser(xmlNode, nodoSubject, 
							nodoResource, nodoAction,
							nodoEnvironment);
					break;
				case Condition:
					conditionParser(xmlNode, nodoSubject, 
							nodoResource, nodoAction,
							nodoEnvironment);
					break;
				default:
					//System.out.println("Nome Nodo ::: "+xmlNode.getLocalName());
					idString = this.nodeGuidFactory.getGuid();
					nodoSubjectChild = new Nodo(nodoSubject,idString);
					nodoResourceChild = new Nodo(nodoResource, idString); 
					nodoActionChild = new Nodo (nodoAction, idString);
					nodoEnvironmentChild = new Nodo (nodoEnvironment, idString);
					nodoSubject.addChild(nodoSubjectChild);
					nodoResource.addChild(nodoResourceChild);
					nodoAction.addChild(nodoActionChild);
					nodoEnvironment.addChild(nodoEnvironmentChild);
					
					nodoSubjectChild.setIdXACML(xmlNode.getAttributes().getNamedItem(String.valueOf(ElementsName.Id)).getNodeValue());
					//System.out.println("NomeNodo : "+xmlNode.getLocalName()+" ## idXacml "+nodoSubjectChild.getIdXACML());
					
					resultParser(xmlNode, 
							nodoSubjectChild,
							nodoResourceChild, 
							nodoActionChild,
							nodoEnvironmentChild);
					break;
				}
			}
			xmlNode = xmlNode.getNextSibling();
		}
	}

	private void risultatoTargetParser(Node risTargetXmlNode,
			Nodo nodoSubject, Nodo nodoResource,
			Nodo nodoAction,Nodo nodoEnvironment){
		if(risTargetXmlNode.hasChildNodes()){
			NodeList children = risTargetXmlNode.getChildNodes();
			Node child;
			for(int i = 0; i < children.getLength(); i++){
				child = children.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE){
					switch (StringToEnum.valueOf(child.getLocalName())) {
					case SubjectSet:
						NodeList subjectSetChildren = child.getChildNodes();
						Node subjectSetTupla;
						for(int j = 0; j < subjectSetChildren.getLength(); j++){
							subjectSetTupla = subjectSetChildren.item(j);
							if(subjectSetTupla.getNodeType() == Node.ELEMENT_NODE){
								Tupla tupla = createTupla(subjectSetTupla);
								// TODO 
								tupla.setTuplaId(this.subGuidFactory.getGuid());
								nodoSubject.getTuplaSet().addTupla(tupla);
							}
						}
						break;
					case ResourceSet:
						NodeList resourceSetChildren = child.getChildNodes();
						Node resourceSetTupla;
						for(int j = 0; j < resourceSetChildren.getLength(); j++){
							resourceSetTupla = resourceSetChildren.item(j);
							if(resourceSetTupla.getNodeType() == Node.ELEMENT_NODE){
								Tupla tupla = createTupla(resourceSetTupla);
								// TODO 
								tupla.setTuplaId(this.resGuidFactory.getGuid());
								nodoResource.getTuplaSet().addTupla(tupla);
							}
						}
						break;
					case ActionSet:
						NodeList actionSetChildren = child.getChildNodes();
						Node actionSetTupla;
						for(int j = 0; j < actionSetChildren.getLength(); j++){
							actionSetTupla = actionSetChildren.item(j);
							if(actionSetTupla.getNodeType() == Node.ELEMENT_NODE){
								Tupla tupla = createTupla(actionSetTupla);
								// TODO 
								tupla.setTuplaId(this.actGuidFactory.getGuid());
								nodoAction.getTuplaSet().addTupla(tupla);
							}
						}
						break;
					case EnvironmentSet:
						NodeList environSetChildren = child.getChildNodes();
						Node environSetTupla;
						for(int j = 0; j < environSetChildren.getLength(); j++){
							environSetTupla = environSetChildren.item(j);
							if(environSetTupla.getNodeType() == Node.ELEMENT_NODE){
								Tupla tupla = createTupla(environSetTupla);
								// TODO 
								tupla.setTuplaId(this.envGuidFactory.getGuid());
								nodoEnvironment.getTuplaSet().addTupla(tupla);
							}
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

	private Tupla createTupla(Node setTupla) {
		Tupla tupla = new Tupla();
		//tupla.setTuplaId(this.factory.getGuid());
		NodeList elementsList = setTupla.getChildNodes();
		Node element;
		for(int j = 0; j < elementsList.getLength(); j++){
			element = elementsList.item(j);
			if(element.getNodeType() == Node.ELEMENT_NODE){
				switch (StringToEnum.valueOf(element.getLocalName())) {
				case AttributeValue:
					String nodeValue;
					if (element.hasChildNodes())
						nodeValue = NodeToString.convertNode(element);
					else 
						nodeValue = element.getTextContent();
					tupla.setAttributeValue(nodeValue);
					break;
				case AttributeId:
					nodeValue = "";
					if (element.hasChildNodes())
						nodeValue = NodeToString.convertNode(element);
					else {
						//System.out.println("AttributeId : "+element.getChildNodes().getLength());
						//nodeValue = element.getTextContent();
					}
					tupla.setAttributeId(nodeValue);
					break;
				case DataType:
					if (element.hasChildNodes())
						nodeValue = NodeToString.convertNode(element);
					else 
						nodeValue = element.getTextContent();
					tupla.setDataType(nodeValue);
					break;
				case Issuer:
					if (element.hasChildNodes())
						nodeValue = NodeToString.convertNode(element);
					else 
						nodeValue = element.getTextContent();
					tupla.setIssuer(nodeValue);
					break;
				case SubjectCategory:
					if (element.hasChildNodes())
						nodeValue = NodeToString.convertNode(element);
					else 
						nodeValue = element.getTextContent();
					tupla.setSubjectCategory(nodeValue);
					break;
				default:
					break;
				}
			}
		}
		// FIXME 2012-10-26
		tupla.solveType();
		
		
		
		return tupla;
	}


	/**
	 * Condition
	 */
	private  void conditionParser(Node conditionNode,
			Nodo nodoSubject, Nodo nodoResource,
			Nodo nodoAction,Nodo nodoEnvironment){
		//System.out.println("****** CONDITION *******");

		Node child;
		String function;
		String id;

		String condId = this.nodeGuidFactory.getGuid();
		//nodoSubject.setNodoName(conditionNode.getLocalName());
		//System.out.println("NODO CONDITION :::: = "+nodoSubject.getNodoName());
		Condition subCond = new Condition(condId);
		Condition resCond = new Condition(condId);
		Condition actCond = new Condition(condId);
		Condition envCond = new Condition(condId);
		subCond.setNomeNodo(String.valueOf(ElementsName.Condition));
		
		
		nodoSubject.setCondition(subCond);
		nodoResource.setCondition(resCond);
		nodoAction.setCondition(actCond);
		nodoEnvironment.setCondition(envCond);
		/*
		 *parsing del nodo condition 
		 */
		NodeList childrenList = conditionNode.getChildNodes();
		for(int j = 0; j < childrenList.getLength(); j++){
			child = childrenList.item(j);
			if(child.getNodeType() == Node.ELEMENT_NODE){
				switch (StringToEnum.valueOf(child.getLocalName())) {
				case Apply:
				case Function:
					/*
					 * creare S_R_A_E...Function
					 * aggiungi S_R_A_E...function a S_R_A_E...condition
					 * 
					 */
					id = this.nodeGuidFactory.getGuid();
					function = child.getAttributes().getNamedItem(ElementsName.Function.toString()).getNodeValue();

					Function subFun = new Function(subCond, id, function);
					Function resFun = new Function(resCond, id, function);
					Function actFun = new Function(actCond, id, function);
					Function envFun = new Function(envCond, id, function);
					subCond.setChild(subFun);
					resCond.setChild(resFun);
					actCond.setChild(actFun);
					envCond.setChild(envFun);

					functionApplyParser(child, subFun, resFun, actFun, envFun);
					break;
				default:
					break;
				}
			}
		}
	}	
	private  void functionApplyParser(Node funApplyNode,
			Function nodoSubject, Function nodoResource,
			Function nodoAction, Function nodoEnvironment){
		Node arg;
		String argId;
		String function;
		if(!funApplyNode.getLocalName().startsWith("caso")){
			nodoSubject.setNomeNodo(funApplyNode.getLocalName());
			//System.out.println("NODO FUNCTION APPLY :::::: = "+nodoSubject.getNomeNodo());
		}
		NodeList argumentList = funApplyNode.getChildNodes();
		for(int i = 0; i < argumentList.getLength(); i++){
			arg = argumentList.item(i);
			if(arg.getNodeType() == Node.ELEMENT_NODE){
				switch (StringToEnum.valueOf(arg.getLocalName())) {
				case Apply:
				case Function:
					argId = this.nodeGuidFactory.getGuid(); 
					function = arg.getAttributes().getNamedItem(ElementsName.Function.toString()).getNodeValue();//TextContent();
					Function subFun = new Function(nodoSubject, argId, function);
					Function resFun = new Function(nodoResource, argId, function);
					Function actFun = new Function(nodoAction, argId, function);
					Function envFun = new Function(nodoEnvironment, argId, function);
					nodoSubject.addArgomento(subFun);
					nodoResource.addArgomento(resFun);
					nodoAction.addArgomento(actFun);
					nodoEnvironment.addArgomento(envFun);
					functionApplyParser(arg, subFun, resFun, actFun, envFun);
					break;
				case SubjectSet:
					NodeList subjectSetChildren = arg.getChildNodes();
					Node subjectSetTupla;
					TuplaSet subTupleSet = new TuplaSet();
					for(int j = 0; j < subjectSetChildren.getLength(); j++){
						subjectSetTupla = subjectSetChildren.item(j);
						if(subjectSetTupla.getNodeType() == Node.ELEMENT_NODE){
							Tupla tupla = createTupla(subjectSetTupla);
							// TODO 
							tupla.setTuplaId(this.subGuidFactory.getGuid());
							subTupleSet.addTupla(tupla);
						}
					}
					nodoSubject.addArgomento(subTupleSet);
					break;
				case ResourceSet:
					NodeList resSetChildren = arg.getChildNodes();
					Node resSetTupla;
					TuplaSet resTupleSet = new TuplaSet();
					for(int j = 0; j < resSetChildren.getLength(); j++){
						resSetTupla = resSetChildren.item(j);
						if(resSetTupla.getNodeType() == Node.ELEMENT_NODE){
							Tupla tupla = createTupla(resSetTupla);
							// TODO 
							tupla.setTuplaId(this.resGuidFactory.getGuid());
							resTupleSet.addTupla(tupla);
						}
					}
					nodoResource.addArgomento(resTupleSet);
					break;
				case ActionSet:
					NodeList actSetChildren = arg.getChildNodes();
					Node actSetTupla;
					TuplaSet actTupleSet = new TuplaSet();
					for(int j = 0; j < actSetChildren.getLength(); j++){
						actSetTupla = actSetChildren.item(j);
						if(actSetTupla.getNodeType() == Node.ELEMENT_NODE){
							Tupla tupla = createTupla(actSetTupla);
							// TODO 
							tupla.setTuplaId(this.actGuidFactory.getGuid());
							actTupleSet.addTupla(tupla);
						}
					}
					nodoAction.addArgomento(actTupleSet);
					break;
				case EnvironmentSet:
					NodeList envSetChildren = arg.getChildNodes();
					Node envSetTupla;
					TuplaSet envTupleSet = new TuplaSet();
					for(int j = 0; j < envSetChildren.getLength(); j++){
						envSetTupla = envSetChildren.item(j);
						if(envSetTupla.getNodeType() == Node.ELEMENT_NODE){
							Tupla tupla = createTupla(envSetTupla);
							// TODO 
							tupla.setTuplaId(this.envGuidFactory.getGuid());
							envTupleSet.addTupla(tupla);
						}
					}
					nodoEnvironment.addArgomento(envTupleSet);
					break;
				default:
					functionApplyParser(arg, nodoSubject, nodoResource, nodoAction, nodoEnvironment);
					break;
				}
			}
		}
	}
}