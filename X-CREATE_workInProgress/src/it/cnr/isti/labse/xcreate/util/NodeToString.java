package it.cnr.isti.labse.xcreate.util;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeToString {

	public static String convertNode(Node nodo){
		
		
		NodeList nodeList = nodo.getChildNodes();
		String result = new String();
		for (int i = 0; i < nodeList.getLength(); i++) {
		//	System.out.println("Conversione numero figli "+i);
			result = result.concat(nodeToString(nodeList.item(i)));
		}
		return result;
	}
		
	public static String nodeToString(Node nodo){
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.STANDALONE,"YES");
			t.transform(new DOMSource(nodo), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	public static String nodeToStringNew(Node nodo) {
		ByteArrayOutputStream sw = new ByteArrayOutputStream();
		try {
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
//			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//			t.setOutputProperty(OutputKeys.STANDALONE,"YES");
			t.setOutputProperty(OutputKeys.INDENT,"yes");
			t.transform( new DOMSource(nodo), new StreamResult(sw));
			
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return new String(sw.toByteArray());
	}
}
