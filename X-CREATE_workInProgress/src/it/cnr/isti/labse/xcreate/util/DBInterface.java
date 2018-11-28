package it.cnr.isti.labse.xcreate.util;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface DBInterface {

	abstract void initialize() throws Exception;

	abstract Document loadFile(String path);

	abstract void storeFile(String xsd, String xml);

	abstract String[] listFiles(String xsd);

	abstract Vector<Node> xmlQuery(String xpath);

	abstract String getTypeResource(String nodeName);

	abstract String getValueResource(String nodeName);

	abstract void printTypeResources(String dir);

	abstract void printValueResources(String dir);

	abstract void createTypeResource(String resourceName, Object resourceContent);

	abstract void createValueResource(String resourceName,
			Object resourceContent);

	abstract void removeValueResource(String resourceName);

	abstract String[] listResources();

	abstract void close();

	abstract void createCollections();

	abstract void clearCollections();

	abstract Vector<Node> getResourceSet(Node restrictionNode);

}
