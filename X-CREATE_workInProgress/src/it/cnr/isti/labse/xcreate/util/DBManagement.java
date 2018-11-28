package it.cnr.isti.labse.xcreate.util;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DBManagement {
	private static DBInterface db;

	public static void initialize() throws Exception {
		db = new ExistDatabase();
		db.initialize();
	}

	public static Document loadFile(String path) {
		return db.loadFile(path);
	}

	public static void storeFile(String xsd, String xml) {
		db.storeFile(xsd, xml);
	}

	public static String[] listFiles(String xsd) {
		return db.listFiles(xsd);
	}

	public static Vector<Node> xmlQuery(String xpath) {
		return db.xmlQuery(xpath);
	}

	public static String getTypeResource(String nodeName) {
		return db.getTypeResource(nodeName);
	}

	public static String getValueResource(String nodeName) {
		return db.getValueResource(nodeName);
	}

	public static void printTypeResources(String dir) {
		db.printTypeResources(dir);
	}

	public static void printValueResources(String dir) {
		db.printValueResources(dir);
	}

	public static void createTypeResource(String resourceName,
			Object resourceContent) {
		db.createTypeResource(resourceName, resourceContent);
	}

	public static void createValueResource(String resourceName,
			Object resourceContent) {
		db.createValueResource(resourceName, resourceContent);
	}

	public static void removeValueResource(String resourceName) {
		db.removeValueResource(resourceName);
	}

	public static String[] listResources() {
		return db.listResources();
	}

	public static void close() {
		db.close();
	}

	public static void createCollections() {
		db.createCollections();
	}

	public static void clearCollections() {
		db.clearCollections();
	}

	public static Vector<Node> getResourceSet(Node restrictionNode) {
		return db.getResourceSet(restrictionNode);
	}

}
