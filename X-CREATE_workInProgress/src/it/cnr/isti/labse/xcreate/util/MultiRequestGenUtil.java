
package it.cnr.isti.labse.xcreate.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;

/**
 * @author ARGENIX
 *
 */
public class MultiRequestGenUtil {
	
	// solo per tas3
	public static String xmlns = " xmlns:xac=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"";
	//public static String ns = "xac:"; // solo per TAS
	public static String ns = "";
	//.. 
	
	//se non lo trovo lo faccio aggiungere cosi' passa nel PDP della Sun
	private static boolean resourceID;
	public static String resAttrID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	
	public static String generateMultipleCombReq(List<Tupla> sub, List<Tupla> res, List<Tupla> act, List<Tupla> env, Integer combinazione) {
		
		resourceID = true;
		StringBuilder multipleReq = new StringBuilder();

		multipleReq.append(requestHeader());
		
		//per i Subjects
		if(!sub.isEmpty()){
			int [] appoggio = new int[sub.size()];
			for (int i = 0; i < appoggio.length; i++){
				appoggio[i]= 1;
			}
			for(int k = 0; k < sub.size(); k++){
				if (appoggio[k] == 1){
					String SubCat = sub.get(k).getSubjectCategory();//il primo
					appoggio[k]=0;
					multipleReq.append(getSubjectCategoryAsXMLString(sub.get(k)));
					multipleReq.append(getAttributeAsXMLString(sub.get(k)));			
					for(int j = 1; j < sub.size(); j++){
						if (sub.get(j).getSubjectCategory().equalsIgnoreCase(SubCat)){
							multipleReq.append(getAttributeAsXMLString(sub.get(j)));
							appoggio[j]=0;
						}
					}
					multipleReq.append("</" + ns + "Subject>");					
				}
			}
		} 
		else{
			// Subject vuoto
			multipleReq.append("<" + ns + "Subject></" + ns + "Subject>");
		}

		//per i Resources
		/*
		 * if(tupla.getAttributeId().equals(resAttrID))
				resourceID = true;
		 */
		if(!res.isEmpty()){
			multipleReq.append("<" + ns + "Resource>");
			Iterator<Tupla> iterRes = res.iterator();
			while(iterRes.hasNext()){
				multipleReq.append(getResourceAttributeAsXMLString(iterRes.next()));
			}
			/* mi serve per mettere resource-id cosi' la richiesta passa nel PDP SUN
			if (resourceID == true){
				multipleReq.append(getResourceIDAsXMLString());
			}*/
			multipleReq.append("</" + ns + "Resource>");
		}
		else{// senza almeno una ResourceID non e' valida allora metto quella fittizia 
			multipleReq.append("<" + ns + "Resource>");
			multipleReq.append(getResourceIDAsXMLString());
			multipleReq.append("</" + ns + "Resource>");
		}
	
		//per gli Actions
		if (!act.isEmpty()){
			multipleReq.append("<" + ns + "Action>");
			Iterator<Tupla> iterAct = act.iterator();
			while (iterAct.hasNext()){
				multipleReq.append(getAttributeAsXMLString(iterAct.next()));
			}
			multipleReq.append("</" + ns + "Action>");
		}
		else multipleReq.append("<" + ns + "Action></" + ns + "Action>");
		//per gli Environments
		if (!env.isEmpty()){
			multipleReq.append("<" + ns + "Environment>");
			Iterator<Tupla> iterEnv = env.iterator();
			while (iterEnv.hasNext()){
				multipleReq.append(getAttributeAsXMLString(iterEnv.next()));
			}
			multipleReq.append("<" + ns + "/Environment>");
		}
		else multipleReq.append("<" + ns + "Environment></" + ns + "Environment>");

		multipleReq.append(requestFooter());
		
		System.out.println(multipleReq);
		return multipleReq.toString();
			
	}
	
	public static String requestHeader() {
		StringBuilder builder = new StringBuilder();
		// solo per tas3
		boolean tas = false;
		if(tas)
			builder.append("<"+ns+"Request "+xmlns +">");
		else{	
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
			builder.append("<Request xmlns=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" ");
			builder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			builder.append(" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:context:schema:os access_control-xacml-2.0-context-schema-os.xsd\">");
		}
		return builder.toString();
	}
	
	public static String requestFooter() {
		return String.valueOf("</" + ns + "Request>");
	}

	public static String getAttributeAsXMLString(Tupla genTupla) {
		Tupla tupla = genTupla;
		StringBuilder subAtt = new StringBuilder();

		subAtt.append("<" + ns + "Attribute AttributeId=\"");
		subAtt.append(tupla.getAttributeId());
		subAtt.append("\" DataType=\"");
		subAtt.append(tupla.getDataType());
		subAtt.append("\"");
		if(tupla.getIssuer()!=null){
			subAtt.append(" Issuer=\"");
			subAtt.append(tupla.getIssuer());
			subAtt.append("\"");
		}

		subAtt.append(">");
		
		subAtt.append("<" + ns + "AttributeValue>");
		subAtt.append(tupla.getAttributeValue());
		subAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");

		return subAtt.toString();
	}
	//da aggiungere per le risorse
	
	public static String getResourceAttributeAsXMLString(Tupla resTupla) {
		Tupla tupla = resTupla;
		StringBuilder resAtt = new StringBuilder();
		//resAtt.append("<Resource>");
		/*	mi serve per mettere resource-id cosi' la richiesta passa nel PDP SUN
		if(tupla.getAttributeId().equals(resAttrID)){
			resourceID = false; //cosi' poi non lo dovro' aggiungere
			System.out.println(resourceID);
		}*/
		resAtt.append("<" + ns + "Attribute AttributeId=\"");
		resAtt.append(tupla.getAttributeId());
		resAtt.append("\" DataType=\"");
		resAtt.append(tupla.getDataType());
		resAtt.append("\"><" + ns + "AttributeValue>");
		resAtt.append(tupla.getAttributeValue());
		resAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");

		return resAtt.toString();
	}
	
	public static String getResourceIDAsXMLString(){
		StringBuilder resAttId = new StringBuilder();
		
		resAttId.append("<" + ns + "Attribute AttributeId=\"");
		resAttId.append("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
		resAttId.append("\" DataType=\"");
		resAttId.append("http://www.w3.org/2001/XMLSchema#string");
		resAttId.append("\"><" + ns + "AttributeValue>");
		resAttId.append(XcreateConstants.RESOURCE_ID_VALUE);
		resAttId.append("</" + ns + "AttributeValue></" + ns + "Attribute>");
	
		return resAttId.toString();
	}
	
	/*
	 *  se c'e' il SubjectCategory devo scrivere diversamente il tag	
	 */
	public static String getSubjectCategoryAsXMLString(Tupla subTupla){
		Tupla tupla = subTupla;
		StringBuilder subAtt = new StringBuilder();
		
		subAtt.append("<" + ns + "Subject");
		if (!tupla.getSubjectCategory().equals("")){
			subAtt.append(" SubjectCategory=\"");
			subAtt.append(tupla.getSubjectCategory());
			subAtt.append("\">");
		}			
		else
			subAtt.append(">");
		
		return subAtt.toString();
	}

	public static Hashtable<String, String> generateMultipleCombReq(String makeRequestName,
			List<Tupla> subs, List<Tupla> ress, List<Tupla> acts,
			List<Tupla> envs, Integer combKey) {
		
		Hashtable<String, String> requests = new Hashtable<String, String>();
		
		Vector<Tupla> resourcesID = new Vector<Tupla>();
		for (int i = 0; i < ress.size(); i++) {
			Tupla resource = ress.get(i);
			if(resource.hasResourceID())
				resourcesID.add(resource);
		}
		
		
		
		System.out.println("NUMERO DI RISORSE PRIMA : "+ress.size());
		ress.removeAll(resourcesID);
		switch (resourcesID.size()) {
		case 0:
			makeMultipleReqWithResource( makeRequestName, 
					subs, null, acts, envs, combKey, requests, resourcesID.size(), ress);
			break;
			
		case 1:
			makeMultipleReqWithResource( makeRequestName, 
					subs, resourcesID.get(0), acts, envs, combKey, requests, resourcesID.size(), ress);
			break;
			
		default:
			makeMultipleReqWithMultipleResource( makeRequestName, 
					subs, ress, acts, envs, combKey, resourcesID, requests);
			
			
			
//			System.exit(0);	
			break;
		}
		
		
		System.out.println("NUMERO DI RISORSE DOPO : "+ress.size());		
		
		return requests;
		
		
		
		
		
		
	}

	private static void makeMultipleReqWithResource(String makeRequestName,
			List<Tupla> subs, Tupla tupla, List<Tupla> acts, List<Tupla> envs,
			Integer combKey, Hashtable<String, String> requests, int size, List<Tupla> ress) {
		
		
		
		System.out.println(makeRequestName);
		StringBuilder multipleReq = new StringBuilder();

		multipleReq.append(requestHeader());
		
		//per i Subjects
		if(!subs.isEmpty()){
			int [] appoggio = new int[subs.size()];
			for (int i = 0; i < appoggio.length; i++){
				appoggio[i]= 1;
			}
			for(int k = 0; k < subs.size(); k++){
				if (appoggio[k] == 1){
					String SubCat = subs.get(k).getSubjectCategory();//il primo
					appoggio[k]=0;
					multipleReq.append(getSubjectCategoryAsXMLString(subs.get(k)));
					multipleReq.append(getAttributeAsXMLString(subs.get(k)));			
					for(int j = 1; j < subs.size(); j++){
						if (subs.get(j).getSubjectCategory().equalsIgnoreCase(SubCat)){
							multipleReq.append(getAttributeAsXMLString(subs.get(j)));
							appoggio[j]=0;
						}
					}
					multipleReq.append("</" + ns + "Subject>");	
				}
			}
		} 
		else{
			// Subject vuoto
			multipleReq.append("<" + ns + "Subject></" + ns + "Subject>");
		}

		//per i Resources
		/*
		 * if(tupla.getAttributeId().equals(resAttrID))
				resourceID = true;
		 */
		multipleReq.append("<" + ns + "Resource>");
		
		if(tupla == null){
			multipleReq.append(getResourceIDAsXMLString());
		}else{
			multipleReq.append(getResourceAttributeAsXMLString(tupla));
		}
		for (int i = 0; i < ress.size(); i++) {
			multipleReq.append(getResourceAttributeAsXMLString(ress.get(i)));
		}
		multipleReq.append("</" + ns + "Resource>");
		
		//per gli Actions
		if (!acts.isEmpty()){
			multipleReq.append("<" + ns + "Action>");
			Iterator<Tupla> iterAct = acts.iterator();
			while (iterAct.hasNext()){
				multipleReq.append(getAttributeAsXMLString(iterAct.next()));
			}
			multipleReq.append("</" + ns + "Action>");
		}
		else multipleReq.append("<" + ns + "Action></" + ns + "Action>");
		//per gli Environments
		if (!envs.isEmpty()){
			multipleReq.append("<" + ns + "Environment>");
			Iterator<Tupla> iterEnv = envs.iterator();
			while (iterEnv.hasNext()){
				multipleReq.append(getAttributeAsXMLString(iterEnv.next()));
			}
			multipleReq.append("<" + ns + "/Environment>");
		}
		else multipleReq.append("<" + ns + "Environment></" + ns + "Environment>");

		multipleReq.append(requestFooter());
		
		System.out.println(multipleReq);

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		requests.put(makeRequestName, multipleReq.toString());
		
		
		
		
	}

	private static void makeMultipleReqWithMultipleResource(
			String requestName, List<Tupla> subs, List<Tupla> ress,
			List<Tupla> acts, List<Tupla> envs, Integer combKey,
			Vector<Tupla> resoourcesID, Hashtable<String, String> requests) {
		
		int i = 0;
		for (Tupla resId : resoourcesID) {
			makeMultipleReqWithResource(requestName.substring(0, requestName.length() - 4)+"_"+(i++),
					subs, resId, acts,
					envs, combKey,
					requests, 1, ress); 
		}
	}

	
	
	

}
