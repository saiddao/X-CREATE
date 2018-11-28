package it.cnr.isti.labse.xcreate.util;

//import it.cnr.isti.taxi.gui.graphics.OpenNewFileTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

//import javax.swing.JDialog;
//import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.parsers.DOMParser;
import org.exist.storage.DBBroker;
import org.exist.xmldb.CollectionManagementServiceImpl;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.XQueryService;
import org.exist.xmldb.XmldbURI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/*******************************************************************************
 * This class is the management of the exist database
 * 
 * @author Administrator
 * 
 */

class ExistDatabase implements DBInterface {
	private static Collection valueCollection = null;

	private static Collection typeCollection = null;

	protected static String driver = "org.exist.xmldb.DatabaseImpl";
	protected static String URI_DB1 = "xmldb:exist://"
			+ DBBroker.ROOT_COLLECTION;
	protected static String URI_DB2 = "xmldb:test://"
			+ DBBroker.ROOT_COLLECTION;
	protected static String URI_EmDB = XmldbURI.EMBEDDED_SERVER_URI.toString();;

	/***************************************************************************
	 * Inital a database
	 * 
	 * @return The initialized database
	 * @throws Exception:
	 *             database can not be initialize
	 */
	public void initialize() throws Exception {
		Class<?> cl = Class.forName(driver);
		
		//String cname = cl.getName();
		//System.out.println("cname is " + cname);
		// create the default database
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");
		DatabaseManager.registerDatabase(database);
		
	}

	/***************************************************************************
	 * Get a database collection
	 * 
	 * @param database
	 *            The database that need to get the collection
	 * @return The collection that get from the database
	 * @throws Exception
	 */
	private static Collection getDBCollection() throws XMLDBException {
		// try to read collection
		System.out.println("getDBCollection () :: Inizio ");
		Collection col = DatabaseManager.getCollection("xmldb:exist:///db",
				"admin", "admin");
		System.out.println("getDBCollection () :: fine "+col.getName());
		return col;
	}

	/***************************************************************************
	 * Upload a file to the Database
	 * 
	 * @param collection
	 *            The collection that need to load the file
	 * @param path
	 *            The path of the file
	 * @throws XMLDBException
	 *             Can not upload the file
	 */
	public Document loadFile(String path) {
		Collection collection;
		Document document = null;
		String xsource = path.substring(path.lastIndexOf(File.separator) + 1);
		try {
			collection = getDBCollection();
			// create new XMLResource; an id will be assigned to the new
			// resource
			// if (hasResource(xsource)) {
			// JOptionPane optionSetMaxOccurs = new JOptionPane(
			// "File "
			// + xsource
			// + " already exists in the database, do you want replace it?",
			// JOptionPane.INFORMATION_MESSAGE,
			// JOptionPane.YES_NO_OPTION);
			// JDialog dialogDB = optionSetMaxOccurs.createDialog(
			// OpenNewFileTree.frame, "Select an option..");
			// dialogDB.pack();
			// dialogDB.setVisible(true);
			// Integer risposta = (Integer) optionSetMaxOccurs.getValue();
			// if (risposta != null && risposta.intValue() == 0)
			// collection.removeResource(collection.getResource(xsource));
			// dialogDB.dispose();
			// }
			XMLResource resource = (XMLResource) collection.createResource(
					xsource, "XMLResource");
			DOMParser parser = new DOMParser();
			parser.parse(path);
			document = parser.getDocument();
			resource.setContent(new File(path));
			collection.storeResource(resource);
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception xmle) {
			xmle.printStackTrace();
			
			
			
			System.out.println("******************************");
			System.out.println(xmle);
		}
		return document;
	}

	public void storeFile(String xsd, String xml) {
		Collection collection;
		Document document = null;
		try {
			collection = getDBCollection();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			Node root = document.createElement("xsdfile");
			XMLResource res = (XMLResource) collection.getResource(xsd);
			if (res != null) {
				Node oldRoot = res.getContentAsDOM().getFirstChild();
				boolean found = true;
				for (int i = 0; i < oldRoot.getChildNodes().getLength(); i++) {
					String value = oldRoot.getChildNodes().item(i)
							.getNodeValue();
					Element elem = document.createElement("xmlfile");
					elem.setTextContent(value);
					root.appendChild(elem);
					if (value.equals(xml))
						found = true;
					if (!found) {
						elem = document.createElement("xmlfile");
						elem.setTextContent(xml);
						root.appendChild(elem);
					}
				}
			} else {
				Element elem = document.createElement("xmlfile");
				elem.setTextContent(xml);
				root.appendChild(elem);
			}
			document.appendChild(root);
			XMLResource resource = (XMLResource) collection.createResource(xsd,
					"XMLResource");
			resource.setContentAsDOM(document);
			collection.storeResource(resource);
		} catch (Exception xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
	}

	public String[] listFiles(String xsd) {
		Collection collection;
		String[] files = new String[0];
		try {
			collection = getDBCollection();
			XMLResource res = (XMLResource) collection.getResource(xsd);
			if (res != null) {
				Document document = (Document) res.getContentAsDOM();
				Element root = document.getDocumentElement();
				NodeList children = root.getChildNodes();
				files = new String[children.getLength()];
				for (int i = 0; i < children.getLength(); i++) {
					files[i] = new String(children.item(i).getNodeValue());
				}
			}
		} catch (Exception xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
		return files;
	}

	boolean hasResource(String xsource) {
		try {
			String[] listResource = getDBCollection().listResources();
			for (int i = 0; i < listResource.length; i++)
				if (listResource[i].equals(xsource))
					return true;
		} catch (XMLDBException e) {
			e.printStackTrace();
			System.out.println("******************************");
			System.out.println(e);
		}
		return false;
	}

	/***************************************************************************
	 * Get a resource set from a collection
	 * 
	 * @param col
	 *            The collection the resource set from
	 * @param xpath
	 *            The xpath grammar
	 * @return The reource set that get from the collection
	 * @throws Exception
	 *             The collection can not be found
	 */
	private static ResourceSet query(String xpath)
			throws XMLDBException {
		Collection col = getDBCollection();
		XQueryService service = (XQueryService) col.getService("XQueryService",
				"1.0");
		service.setProperty("indent", "yes");

		// If the type contains Namespace
		ResourceSet result = null;
		try {
			result = service.query(xpath);
		} catch (XMLDBException e) {
			e.printStackTrace();
			System.out.println("******************************");
			System.out.println(e);
		}
		return result;
	}

	public Vector<Node> xmlQuery(String xpath) {
		Vector<Node> result = new Vector<Node>();
		try {
			ResourceSet set = query(xpath);
			ResourceIterator i = set.getIterator();
			while (i.hasMoreResources()) {
				XMLResource r = (XMLResource) i.nextResource();
				result.add(r.getContentAsDOM());
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			System.out.println("******************************");
			System.out.println(e);
		}
		return result;
	}

	/***************************************************************************
	 * Get the resource set by node type
	 * 
	 * @param node
	 *            The node that need to be searched
	 * @param col
	 *            The collection that the resource set from
	 * @return The resource set that getting from the collection
	 * @throws Exception
	 *             Can not find the type or the collection is not avaliable
	 */
	private static ResourceSet typeSearching(Node node) throws XMLDBException {
		String elementType = "";
		for (int i = 0; i < node.getAttributes().getLength(); i++) {
			Node attr = node.getAttributes().item(i);
			if (attr.getNodeName().equals("type")
					|| attr.getNodeName().equals("ref")
					|| attr.getNodeName().equals("base"))
				elementType = attr.getNodeValue();
		}

		// if the type has namespace prefix
		if (elementType.contains(":")) {
			elementType = elementType.substring(elementType.indexOf(":") + 1,
					elementType.length());
		}
		// TODO Questo cerca in tutti i file XML del DB, correggere!!!
		String xpathComplextype = "//*[@name='" + elementType + "']";
		ResourceSet resourceset = query(xpathComplextype);
		return resourceset;
	}

	public String getTypeResource(String nodeName) {
		try {
			Resource r = typeCollection.getResource(nodeName);
			if (r != null)
				return (String) r.getContent();
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
		return null;
	}

	/***************************************************************************
	 * Get a file from a collection
	 * 
	 * @param fileName
	 *            The name of the searching file
	 * @return The resource getting from the collection that has the same name
	 *         of the fileName
	 */
	public String getValueResource(String nodeName) {
		try {
			Resource r = valueCollection.getResource(nodeName);
			if (r != null)
				return (String) r.getContent();
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
		return null;
	}

	private static void printResources(Collection col, String dir)
			throws XMLDBException {
		String[] resourceList = col.listResources();
		for (int i = 0; i < resourceList.length; i++) {
			Resource resource = col.getResource(resourceList[i]);
			File file = new File(dir + File.separator + resourceList[i]);
			String content = (String) resource.getContent();
			try {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(file)), true);
				writer.println(content);
				writer.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.out.println("******************************");
				System.out.println(ioe);
			}
		}
	}

	public void printTypeResources(String dir) {
		try {
			printResources(typeCollection, dir);
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
	}

	public void printValueResources(String dir) {
		try {
			printResources(valueCollection, dir);
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
	}

	/***************************************************************************
	 * Create a resource in a collection
	 * 
	 * @param col
	 *            The collection that the resource is created in
	 * @param resourceName
	 *            The name of the resource
	 * @param resourceContent
	 *            The content of the creating resource
	 * @return The created resource
	 */
	private static Resource createResource(Collection col, String resourceName,
			Object resourceContent) {
		Resource res = null;
		try {
			String resourceType = "XMLResource";
			res = col.createResource(resourceName, resourceType);
			res.setContent(resourceContent);
			col.storeResource(res);
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
		return res;
	}

	public void createTypeResource(String resourceName, Object resourceContent) {
		createResource(typeCollection, resourceName, resourceContent);
	}

	public void createValueResource(String resourceName, Object resourceContent) {
		createResource(valueCollection, resourceName, resourceContent);
	}

	public void removeValueResource(String resourceName) {
		try {
			Resource r = valueCollection.getResource(resourceName);
			if (r != null)
				valueCollection.removeResource(r);
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
	}

	public String[] listResources() {
		try {
			
			System.out.println("ListResources :: ");
			
			Collection col = getDBCollection();
			
			System.out.println(col.getName());
			
			if (col != null)
				return col.listResources();
		} catch (XMLDBException xmle) {
			
			System.out.println("cannot create the resource " + "resourceName");
			
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
		return null;
	}

	public void close() {
		try {
			Collection col = getDBCollection();
			DatabaseInstanceManager manager = (DatabaseInstanceManager) col
					.getService("DatabaseInstanceManager", "1.0");
			manager.shutdown();
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
	}

	private static Collection createCol(String colName) throws XMLDBException {
		Collection col = getDBCollection();
		CollectionManagementServiceImpl mgtService = (CollectionManagementServiceImpl) col
				.getService("CollectionManagementService", "1.0");
		XmldbURI uri = XmldbURI.create(colName);
		Collection newCol = mgtService.createCollection(uri);
		return newCol;
	}

	public void createCollections() {
		try {
			valueCollection = createCol("valueCol");
			typeCollection = createCol("typeCol");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearCollections() {
		try {
			if (typeCollection != null && typeCollection.isOpen()) {
				String[] resourceList = typeCollection.listResources();
				for (int i = 0; i < resourceList.length; i++) {
					Resource resource = typeCollection
							.getResource(resourceList[i]);
					typeCollection.removeResource(resource);
				}
				typeCollection.close();
			}

			if (valueCollection != null && valueCollection.isOpen()) {
				String[] valueResourceList = valueCollection.listResources();
				for (int i = 0; i < valueResourceList.length; i++) {
					Resource valueResource = valueCollection
							.getResource(valueResourceList[i]);
					valueCollection.removeResource(valueResource);
				}
				valueCollection.close();
			}
		} catch (XMLDBException xmle) {
			xmle.printStackTrace();
			System.out.println("******************************");
			System.out.println(xmle);
		}
	}

	public Vector<Node> getResourceSet(Node restrictionNode) {
		Vector<Node> result = new Vector<Node>();
		try {
			ResourceSet resouceSet = typeSearching(restrictionNode);
			ResourceIterator resultIterator = resouceSet.getIterator();
			while (resultIterator.hasMoreResources()) {
				XMLResource r = (XMLResource) resultIterator.nextResource();
				result.add(r.getContentAsDOM());
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
			System.out.println("******************************");
			System.out.println(e);
		}
		return result;
	}

}
