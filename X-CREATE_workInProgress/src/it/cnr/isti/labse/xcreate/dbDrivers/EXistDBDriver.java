package it.cnr.isti.labse.xcreate.dbDrivers;

import java.io.File;
import java.io.IOException;


import org.apache.xerces.parsers.DOMParser;
import org.exist.xmldb.CollectionManagementServiceImpl;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.XmldbURI;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.Service;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

public class EXistDBDriver {
	private Database database;
	private Collection rootCol;
	private Collection homeCol;
	private boolean connect;

	public EXistDBDriver(){

	}

	public void connect() {
		String driver = "org.exist.xmldb.DatabaseImpl";
		Class<?> cl;
		try {
			cl = Class.forName(driver);
			this.database = (Database) cl.newInstance();
			this.database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(this.database);
			this.rootCol = DatabaseManager.getCollection("xmldb:exist:///db","admin", "");
			setHomeCollection(this.rootCol);
			this.connect = true;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}

	private void setHomeCollection(Collection collection) {
		this.homeCol = collection;
	}

	public void createCollection(String colName) {
		try {
			CollectionManagementServiceImpl mgtService = (CollectionManagementServiceImpl) 
			this.homeCol.getService("CollectionManagementService", "1.0");
			XmldbURI uri = XmldbURI.create(colName);
			Collection newCol = mgtService.createCollection(uri );
			this.homeCol = newCol;
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}

	public XMLResource createResource() {
		XMLResource xmlResource = null;
		try {
			xmlResource = (XMLResource) this.homeCol.createResource(this.homeCol.createId(),"XMLResource");
		} catch (XMLDBException e) {
			return xmlResource;
		}
		return xmlResource;
	}



	public void deleteCollection(String collectionName) {
		try {
			CollectionManagementServiceImpl mgtService = (CollectionManagementServiceImpl)
			this.homeCol.getService("CollectionManagementService", "1.0");
			XmldbURI uri = XmldbURI.create(collectionName);
			mgtService.removeCollection(uri);
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteResource(String resourceName) {
		try {
			this.homeCol.removeResource(this.homeCol.getResource(resourceName));
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			DatabaseManager.deregisterDatabase(this.database);
			DatabaseInstanceManager manager = (DatabaseInstanceManager) this.rootCol.getService("DatabaseInstanceManager", "1.0");
			manager.shutdown();
			this.connect = false;
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}

	public ResourceSet execute(String xQuery) {
		ResourceSet result = null;
		XPathQueryService service;
		try {
			service = (XPathQueryService) this.homeCol.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");
			result = service.query(xQuery);
		} catch (XMLDBException e) {
			return result;
		}
		return result;
	}

	public boolean isConnected() {
		return this.connect;
	}

	public void showCollection() {
		try {
			String[] childrenCol = this.homeCol.listChildCollections();
			String[] childrenRes = this.homeCol.listResources();
			for (int i = 0; i < childrenCol.length; i++) {
				System.out.println(childrenCol[i]);
			}
			for (int i = 0; i < childrenRes.length; i++) {
				System.out.println(childrenRes[i]);
			}
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

	}

	public  String[][] getHomeColContent(){
		String[][] content = null;
		try {
			String[] col = this.homeCol.listChildCollections();
			String[] res = this.homeCol.listResources();
			content = new String[col.length+res.length][2];
			int index = 0;
			for (int i=0; i < col.length; i++) {
				content[index][0] = col[index];
				content[index][1] = "Collection";
				//System.out.println(col[index]);
				index++;
			}
			for (int i = 0; i < res.length; i++) {
				content[index][0] = res[i];
				content[index][1] = "Resource";
				System.out.println(res[i]);
				index++;
			}

		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	public void getServices() {
		// TODO Auto-generated method stub
		try {
			Service service = (CollectionManagementService)this.homeCol.getService("CollectionManagementService", "1.0");
			System.out.println(service.getName());
			Service[] services = this.homeCol.getServices();
			for (int i = 0; i < services.length; i++) {
				System.out.println(services[i].getName());
			}
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Document uploadFile(String path) {
		Document document = null;
		String xsource = path.substring(path.lastIndexOf(File.separator) + 1);
		//System.out.println("xresource : "+xsource);
		try {
			XMLResource resource = (XMLResource) this.homeCol.createResource(
					xsource, "XMLResource");
			DOMParser parser = new DOMParser();
			parser.parse(path);
			document = parser.getDocument();
			resource.setContent(new File(path));
			this.homeCol.storeResource(resource);
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception xmle) {
			xmle.printStackTrace();
			//System.out.println("******************************");
			System.out.println(xmle);
		}
		return document;
	}

	public boolean isPolicy(String policyName) {
		// TODO Auto-generated method stub
		boolean isPolicy = false;
		String[] resourceList;
		try {
			resourceList = this.homeCol.listResources();
			for (int i = 0; i < resourceList.length; i++) {
				if(policyName.equals(String.valueOf(resourceList[i])) )
					isPolicy = true;
			}
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isPolicy;
	}

}
