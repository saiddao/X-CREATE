package it.cnr.isti.labse.xcreate.util;

import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;

public class RequestGenUtil {
	private static final String STRINGA_VUOTA = "";
	// solo per tas3
	public static String xmlns = "xmlns:xac=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"";
	//public static String ns = "xac:";
	public static String ns = "";
	//.. 

	public static String getSubjectAttributeAsXMLString(Tupla subTupla) {
		Tupla tupla = subTupla;
		StringBuilder subAtt = new StringBuilder();
		if (tupla == null)
			subAtt.append("<" + ns + "Subject></" + ns + "Subject>");
		else {
			// 21 Maggio 2013 :: considera subjectcategory
			subAtt.append("<" + ns + "Subject");
			
			if(!tupla.getSubjectCategory().equals(STRINGA_VUOTA)){
				subAtt.append(" SubjectCategory=\"");
				subAtt.append(tupla.getSubjectCategory()+"\"");
			}
			subAtt.append(">");
			subAtt.append("<" + ns + "Attribute AttributeId=\"");
			subAtt.append(tupla.getAttributeId());
			subAtt.append("\" DataType=\"");
			subAtt.append(tupla.getDataType());
			// aggiunto il 05-11-2012
			if(!tupla.getIssuer().equals(STRINGA_VUOTA)){
				subAtt.append("\" Issuer=\"");
				subAtt.append(tupla.getIssuer());
			}
			
			subAtt.append("\"><" + ns + "AttributeValue>");
			subAtt.append(tupla.getAttributeValue());
			subAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");
			subAtt.append("</" + ns + "Subject>");
		}
		return subAtt.toString();
	}

	
	public static String getResourceAttributeAsXMLString(Tupla resTupla) {
		Tupla tupla = resTupla;
		StringBuilder resAtt = new StringBuilder();
		if (tupla == null)
			resAtt.append("<" + ns + "Resource></" + ns + "Resource>");
		else {
			resAtt.append("<" + ns + "Resource>");
			resAtt.append("<" + ns + "Attribute AttributeId=\"");
			resAtt.append(tupla.getAttributeId());
			resAtt.append("\" DataType=\"");
			resAtt.append(tupla.getDataType());
			// aggiunto il 05-11-2012
			if(!tupla.getIssuer().equals(STRINGA_VUOTA)){
				resAtt.append("\" Issuer=\"");
				resAtt.append(tupla.getIssuer());
			}
			
			resAtt.append("\"><" + ns + "AttributeValue>");
			resAtt.append(tupla.getAttributeValue());
			resAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");
			// FIXME AGGIUNGERE RESOURCE-ID
			if (!tupla.hasResourceID()) {
				resAtt.append("<" + ns + "Attribute AttributeId=\"");
				resAtt.append("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
				resAtt.append("\" DataType=\"");
				resAtt.append(tupla.getDataType());
				resAtt.append("\"><" + ns + "AttributeValue>");
				resAtt.append(XcreateConstants.RESOURCE_ID_VALUE);//tupla.getAttributeValue());
				resAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");
			}
			resAtt.append("</" + ns + "Resource>");

			// FIXME AGGIUNGERE RESOURCE-ID

			// TODO 2010-11-26
			/*
			 * 2010-11-26 se la risorsa contiene attributeId e' formato dal
			 * suffisso = "resource-id" - non aggiungere nuova risorsa
			 * modificata altrimenti - aggiungi risorsa modificata
			 */
//			if (!tupla.hasResourceID()) {
//				resAtt.append("<" + ns + "Resource>");
//				resAtt.append("<" + ns + "Attribute AttributeId=\"");
//				resAtt.append("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
//				resAtt.append("\" DataType=\"");
//				resAtt.append(tupla.getDataType());
//				resAtt.append("\"><" + ns + "AttributeValue>");
//				resAtt.append(tupla.getAttributeValue());
//				resAtt.append("</" + ns + "AttributeValue></" + ns
//						+ "Attribute>");
//				resAtt.append("</" + ns + "Resource>");
//			}

		}
		return resAtt.toString();
	}

	public static String getActionAttributeAsXMLString(Tupla actTupla) {
		Tupla tupla = actTupla;
		StringBuilder actAtt = new StringBuilder();
		if (tupla == null)
			actAtt.append("<" + ns + "Action></" + ns + "Action>");
		else {
			actAtt.append("<" + ns + "Action>");
			actAtt.append("<" + ns + "Attribute AttributeId=\"");
			actAtt.append(tupla.getAttributeId());
			actAtt.append("\" DataType=\"");
			actAtt.append(tupla.getDataType());
			// aggiunto il 05-11-2012
			if(!tupla.getIssuer().equals(STRINGA_VUOTA)){
				actAtt.append("\" Issuer=\"");
				actAtt.append(tupla.getIssuer());
			}
			
			actAtt.append("\"><" + ns + "AttributeValue>");
			actAtt.append(tupla.getAttributeValue());
			actAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");
			actAtt.append("</" + ns + "Action>");
		}
		return actAtt.toString();
	}

	public static String getEnvironmentAttributeAsXMLString(Tupla envTupla) {
		Tupla tupla = envTupla;
		StringBuilder envAtt = new StringBuilder();
		if (tupla == null)
			envAtt.append("<" + ns + "Environment></" + ns + "Environment>");
		else {
			envAtt.append("<" + ns + "Environment>");
			envAtt.append("<" + ns + "Attribute AttributeId=\"");
			envAtt.append(tupla.getAttributeId());
			envAtt.append("\" DataType=\"");
			envAtt.append(tupla.getDataType());
			// aggiunto il 05-11-2012
			if(!tupla.getIssuer().equals(STRINGA_VUOTA)){
				envAtt.append("\" Issuer=\"");
				envAtt.append(tupla.getIssuer());
			}
			
			envAtt.append("\"><" + ns + "AttributeValue>");
			envAtt.append(tupla.getAttributeValue());
			envAtt.append("</" + ns + "AttributeValue></" + ns + "Attribute>");
			envAtt.append("</" + ns + "Environment>");
		}
		return envAtt.toString();
	}

	public static String requestHeader() {
		StringBuilder builder = new StringBuilder();
		// solo per tas3
		boolean tas = false;
		if(tas)
			builder.append("<"+ns+"Request "+xmlns +">");
		else{			
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
			builder.append("<"+ns+"Request xmlns=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\" ");
			builder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			builder.append(" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:context:schema:os access_control-xacml-2.0-context-schema-os.xsd\">");
		}

		return builder.toString();
	}

	public static String requestFooter() {
		return String.valueOf("</" + ns + "Request>");
	}

	public static String generateSimpleCombReq(Tupla sub, Tupla res, Tupla act, Tupla env) {
		StringBuilder simpleReq = new StringBuilder();

		simpleReq.append(requestHeader());

		simpleReq.append(getSubjectAttributeAsXMLString(sub));
		simpleReq.append(getResourceAttributeAsXMLString(res));
		simpleReq.append(getActionAttributeAsXMLString(act));
		simpleReq.append(getEnvironmentAttributeAsXMLString(env));

		simpleReq.append(requestFooter());
		return simpleReq.toString();
	}

	public static String makeRequestName(Integer combKey, String suffixReqName) {
		String zeri = "";
		if (combKey < 10)
			zeri = "00000";
		if ((combKey >= 10) && (combKey < 100))
			zeri = "0000";
		if ((combKey >= 100) && (combKey < 1000))
			zeri = "000";
		if ((combKey >= 1000) && (combKey < 10000))
			zeri = "00";
		if ((combKey >= 10000) && (combKey < 100000))
			zeri = "0";
		
		return zeri + combKey + "_" + suffixReqName;		
	}
	
	
	/*
	 * FIXME 
	 * 19 Aprile 2013 :: fixed bug .. request name 
	 */
	public static String makeNewRequestName(String wise, Integer combKey, String suffixReqName) {
		String prefix = String.format("%015d", Integer.valueOf(combKey));
		return wise + "_" + prefix + "_" + suffixReqName;		
	}
	
	
	
	public static String makeNewSimpleRequestName(String wise, Integer combKey, Tupla subTupla, Tupla resTupla, Tupla actTupla, Tupla envTupla) {
		StringBuilder reqName = new StringBuilder();
		reqName.append(wise);
		reqName.append("_");
		reqName.append(String.format("%015d", Integer.valueOf(combKey)));
		reqName.append("_");
		reqName.append((subTupla == null)? "Null" : subTupla.getPkTupla());
		reqName.append("_");
		reqName.append((resTupla == null)? "Null" : resTupla.getPkTupla());
		reqName.append("_");
		reqName.append((actTupla == null)? "Null" : actTupla.getPkTupla());
		reqName.append("_");
		reqName.append((envTupla == null)? "Null" : envTupla.getPkTupla());
		return reqName.toString();
	}
	
}
