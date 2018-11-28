package it.cnr.isti.labse.xcreate.guiXCREATE;

import it.cnr.isti.labse.xcreate.dbDrivers.EXistDBDriver;
import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.policyAnalyzer.LogicaPolicyAnalyzer;
import it.cnr.isti.labse.xcreate.sql.CreateMySqlDB;
import it.cnr.isti.labse.xcreate.sql.DeletePolicyFromMySqlDB;
import it.cnr.isti.labse.xcreate.sql.InsertNodiTupleSQL;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;
import it.cnr.isti.labse.xcreate.sql.SelectTipi;
import it.cnr.isti.labse.xcreate.xSDResources.XSDUtil;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XCreateMain extends JFrame {
	/*
	 * per la logica X-CREATE
	 */
	private Connection mySqlConnection;
	private EXistDBDriver eXistDBDriver;
	private Hashtable<String, Integer> politicheAsHashtable;
	private CreateMySqlDB mySqlDB;
	private File tempDir;
//	private File currentReqDir;
	/*
	 * per la GUI X-CREATE
	 */
	private Button addPolicyButton;
	private Button exitButton;                                          
	private JTabbedPane jPolicyTabbedPane;                                                
	private JSeparator jSeparator1;
	private Button loadPolicyButton;
	private String canonicalTempDirPath;
	private Button deletePolicyButton;
    
	private static final long serialVersionUID = -8431528320742147553L;

	public static void main(String args[]) {
		try{
//			System.out.println("Avvio !!!");
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					new XCreateMain().setVisible(true);
				}
			});
		}catch (Exception e) {
			System.err.println("Errore Main : "+e.toString());
		}
		
	}


	public XCreateMain() {
		try {
			MySQLCons.init();
			// INIZIALIZZAZIONE PER LA LOGICA
			this.tempDir = GuiCons.TEMP_DIR;
			if(!this.tempDir.exists())
				this.tempDir.mkdirs();
			this.canonicalTempDirPath = this.tempDir.getCanonicalPath();
			//System.out.println(this.canonicalTempDirPath);

			this.politicheAsHashtable = new Hashtable<String, Integer>();
			// creare eXist-db Embedded
			this.eXistDBDriver = new EXistDBDriver();
			// coonnetrsi eXist-db
			this.eXistDBDriver.connect();
			// FIXME parte MySQL
			this.mySqlDB = new CreateMySqlDB();
			this.mySqlConnection = this.mySqlDB.connect();
			// #############################################################
			//##############################################################
			// FIXME da RIvedere molto bene 
			this.mySqlDB.initialize(); //TODO togliere COMMENTO
			// #############################################################
			//##############################################################
			SelectTipi.init(this.mySqlConnection);

			// INIZIALIZZAZIONE PER LA GUI
			initComponents();
			
			this.jPolicyTabbedPane.addTab("Welcome::Home", new WelcomeTab());
			
			
			
		} catch (IOException e) {
			// TODO COME GESIRE LA SEGUENTE SITUAZIONE ? 
			e.printStackTrace();
		}
	}

	private void initComponents() {
		this.setLocation(30, 30);
		this.jPolicyTabbedPane = new JTabbedPane();
		this.jSeparator1 = new JSeparator();
		this.addPolicyButton = new Button();
		this.loadPolicyButton = new Button();
		this.exitButton = new Button();

		this.deletePolicyButton = new Button();

//		setTitle("X-CREATE");
		setTitle("The X-CREATE framework for XACML policy testing");
		
		setResizable(false);
		addWindowListener(getWindowsListener());
		this.addPolicyButton.setLabel("Add Policy");
		this.addPolicyButton.setName("AddPolicy");
		this.addPolicyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addPolicyButtonActionPerformed(e);
			}});

		this.loadPolicyButton.setLabel("Load Policy From DB");
		this.loadPolicyButton.setName("LoadPolicy");
		this.loadPolicyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loadPolicyButtonActionPerformed(evt);
			}
		});

		this.exitButton.setLabel("EXIT");
		this.exitButton.setName("exit");
		this.exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				exitButtonActionPerformed(evt);
			}
		});

		this.deletePolicyButton.setLabel("Delete Policy");
		this.deletePolicyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deletePolicyButtonActionPerformed(evt);
			}
		});		

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.jSeparator1, GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
						.addGap(20, 20, 20)
						.addComponent(this.addPolicyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(this.loadPolicyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)

						.addComponent(this.deletePolicyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)

						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 500, Short.MAX_VALUE)
						.addComponent(this.exitButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18))
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(this.jPolicyTabbedPane, GroupLayout.PREFERRED_SIZE, 863, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.addPolicyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.loadPolicyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.deletePolicyButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.exitButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(this.jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(this.jPolicyTabbedPane, GroupLayout.PREFERRED_SIZE, 443, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}

	protected void deletePolicyButtonActionPerformed(ActionEvent evt) {
		/* TODO
		 * selezionare le politiche presenti in MySQL
		 * visualizzare il risultato per la scelta della politica
		 * se il tab relativo alla politica selezionata e' aperto .. 
		 * 		come comportarsi ?
		 * 		-	chiedere all'utente di chiudere prima il tab relativo alla politica
		 * 		  	e poi scegliere di nuovo la politica da cancellare ...
		 *		-	oppure, chiudere da programma il tab e poi cancellare la politica da MySQL ...
		 * Chiedere comunque conferma all'utente della scelta fatta prima di cancellare in modo 
		 * permanente la politica.
		 *
		 *
		 */
		try {
			ResultSet result;
			Statement stmt = this.mySqlConnection.createStatement();
			result = stmt.executeQuery(SelectSQL.getQuerySelectCardinalitaTabellaPolitche());
			result.next();
			int policyTableSize = result.getInt("TableSize");
			if(policyTableSize == 0)
				JOptionPane.showMessageDialog(this, "\n The database is empty.\n\n ", "The Empty DataBase ", JOptionPane.WARNING_MESSAGE);
			else{
				//JOptionPane.showMessageDialog(this.loadPolicyButton, "\n The database is empty.\n\n Please add new policies in the database. ", "The Empty DataBase", JOptionPane.WARNING_MESSAGE);
				String[] polNames = new String[result.getInt("TableSize")];
				result = stmt.executeQuery(SelectSQL.getQuerySelectNomePolicaFromPolitiche());
				System.out.println(polNames.length);
				String policyName;
				Integer pkPolitica = null;
				int indexForPolicyNames = 0;
				this.politicheAsHashtable.clear();
				while (result.next()) {
					policyName =result.getString("NomePolitica");
					pkPolitica = new Integer(result.getInt("PK_Politica"));
					this.politicheAsHashtable.put(policyName, pkPolitica);
					polNames[indexForPolicyNames] = policyName;
					System.out.println("Nome POlitica :"+polNames[indexForPolicyNames]);
					indexForPolicyNames++;
				}
				result.close();
				stmt.close();
				// deve contenere l'elenco delle politiche presenti nel database
				String policyNameSelected = (String) JOptionPane.showInputDialog(null,
						"Choose Policy to be deleted", "Delete Policy Under Test",
						JOptionPane.INFORMATION_MESSAGE, null,
						polNames, polNames[0]);
				//System.out.println(policyNameSelected);
				if(policyNameSelected != null){
					System.out.println("Cancella politica : "+policyNameSelected);
					DeletePolicyFromMySqlDB.deletePolicy(this.politicheAsHashtable.get(policyNameSelected).intValue(), policyNameSelected, this.mySqlConnection, this.eXistDBDriver);
				}
			}
		} catch (SQLException e) { // TODO COME GESTIRE QUESTO EVENTO ECCEZIONALE ? 
			e.printStackTrace();
		}

		
		
		
		
	}
	private void exitButtonActionPerformed(ActionEvent evt) {                                           
		// FIXME RICORDARSI DI GESTIRE LE CONNESSIONI AI DB SOPRATTUTTO AD EXIST-DB
		this.eXistDBDriver.disconnect();
		try {
			this.mySqlConnection.commit();
			this.mySqlConnection.close();
		} catch (SQLException e) {
			// TODO COME GESTIRE QUESTA SITUAZIONE ? 
			e.printStackTrace();
		}
		System.exit(0);
	}
	// per la gestione della chiusura della finestra principale
	private WindowListener getWindowsListener() {
		// TODO Auto-generated method stub
		WindowListener windowListener = new WindowListener() {
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
				eXistDBDriver.disconnect();
				try {
					mySqlConnection.commit();
					mySqlConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
			}
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		};
		return windowListener;
	}

	private boolean createResource(String policyPath) {
		// TODO Auto-generated method stub
		Document xmlResource = this.eXistDBDriver.uploadFile(policyPath);
		//System.out.println(NodeToString.convertNode(xmlResource));
		return (xmlResource == null) ? false : true;
	}

	private void addPolicyButtonActionPerformed(ActionEvent e) {
		
		
		
		
		/*
		 * permette di caricare una nuova politica nel database
		 * 0) verificare se al politica XACML rispetta lo schema (XACML 2.0)
		 * 1) verifica se la politica esiste
		 * 2) eventualmente carica la politica nel database eXist
		 * 3) esegue il parsing della politica
		 * 4) carica la politica nel database MySQL
		 * 5) genera le combianzioni a partire dalla radice della poltica
		 * 6) aggiunge un nuovo tab relativo alla politica appena aggiunta/caricata/salvata/sottoposta all'attenzione di X-CREATE
		 */
		JFileChooser fc = new JFileChooser(new File("."));
		fc.setDialogTitle("Choose the XACML policy to be added to the database");
		fc.addChoosableFileFilter(new EstensionFilter("XmL"));
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			
			
			
			
			
			
			File file = fc.getSelectedFile();
			String policyPath = file.getAbsolutePath();
			String policyName = file.getName();
			// VALIDARE IL FILE APPENA SELEZIONATO 
			if(!thereIsPolicy(policyName))
				if(isPolicyValid(policyPath)){
					//System.out.println( policyPath );
					//System.out.println(policyName);
					String policyNameWithoutExt = policyName.substring(0, policyName.length()-4); 
					File policyTempDir = new File(this.canonicalTempDirPath+GuiCons.DIR_SEPARATOR+policyNameWithoutExt);
					if(!policyTempDir.exists())
						policyTempDir.mkdirs();
					//try { // TODO COME GESTIRE QUESTA SITUAZIONE ?
						//System.out.println(policyTempDir.getCanonicalPath());
					//} catch (IOException e1) {e1.printStackTrace();}
					if(createResource(policyPath)){
						LogicaPolicyAnalyzer policyAnalyzer = new LogicaPolicyAnalyzer(eXistDBDriver);
						/*
						 * 29 ottobre 2012
						 * cambiata la segnatura del metodo
						 * 
						 * policyAnalyzer.analizzaPolitica(policyName, policyTempDir);
						 */
						policyAnalyzer.analizzaPolitica(this,policyName, policyTempDir);
						
						/********************************************************************************
						 * FIXME 
						 * 
						 * INSERIMENTO POLITICA CON I DATI STATISTICI
						 * MIN E MAX SUB, RES, ACT ED ENV. 
						 * 
						 * *********************************************************************************
						 * 
						 * SEGNATURA DEL METODO
						 * 
						 * insertPolicy(String name, 
						 * 				int minSub, int maxSub, 
						 * 				int minRes, int maxRes, 
						 * 				int minAct, int maxAct, 
						 * 				int minEnv, int maxEnv)
						 * *********************************************************************************
						 */
						//int pkPolitica = this.mySqlDB.insertPolicy(policyName, 0, 10, 0, 10, 0, 10, 0, 10);
						int sub = policyAnalyzer.getMaxNumSubject();
						int res = policyAnalyzer.getMaxNumResource();
						int act = policyAnalyzer.getMaxNumAction();
						int env = policyAnalyzer.getMaxNumEnvironment();// FIXME i valori minimi????
						
						
						
						int pkPolitica = this.mySqlDB.insertPolicy(policyName, 0, sub, 0, res, 0, act, 0, env);
//						System.out.println(pkPolitica);
						
						
						InsertNodiTupleSQL insertNodiTupleSQL = new InsertNodiTupleSQL(mySqlConnection, policyAnalyzer, pkPolitica);
						insertNodiTupleSQL.execute();
						PolicyTab policyTab = new PolicyTab(this.jPolicyTabbedPane, pkPolitica, this.mySqlConnection, policyTempDir);
						policyTab.setVisible(true);
						this.jPolicyTabbedPane.addTab(policyName, policyTab);
						this.jPolicyTabbedPane.setSelectedComponent(policyTab);
						int policyTabPosition = this.jPolicyTabbedPane.getComponentZOrder(policyTab);
						policyTab.setPosition(policyTabPosition);
						
						
						
						
						
						
					}else {
						JOptionPane.showMessageDialog(this, "\n The database is empty.\n\n Please add new policies in the database. ", "The Empty DataBase ", JOptionPane.ERROR_MESSAGE);				
					}
				}else{
					// FIXME FILE NON VALIDO
					JOptionPane.showMessageDialog(this, "\n Policy Invalid. \n\n Please select another policy. ", "Policy invalid", JOptionPane.ERROR_MESSAGE);
				}
			else{
				JOptionPane.showMessageDialog(this, "\n Policy is already in the DataBase. \n\n Please select another policy or load the policy. ", "Policy Already Exists", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean isPolicyValid(String policyPath){
		boolean valid = true;
		try {
			Source xmlFile = new StreamSource(new File(policyPath)); 
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(XSDUtil.getPolicySchemaLocation());
			Schema schema = schemaFactory.newSchema(schemaFile); 
			Validator validator = schema.newValidator(); 
			validator.validate(xmlFile); 
			System.out.println(xmlFile.getSystemId() + "\n is valid"); 
		} catch (SAXException e) { 
			System.out.println(policyPath + "\n is NOT valid"); 
			System.out.println("Causa: " + e.getLocalizedMessage());
			valid = false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			valid = false;
		} catch (IOException e) {
			e.printStackTrace();
			valid = false;
		} 
		return valid;
	}

	private boolean thereIsPolicy(String policyName){
		boolean thereIs = false;
		try {
			ResultSet result;
			Statement stmt = this.mySqlConnection.createStatement();
			result = stmt.executeQuery(SelectSQL.getSelectCountPoliticheConNome(policyName));
			result.next();
			int policyCount = result.getInt("Quanti");
			if(policyCount == 0){
				System.out.println("La Politica :"+policyName+" non e' presente nel database");
				return thereIs;
			}
			else{
				result = stmt.executeQuery(SelectSQL.getQuerySelectNomePolicaFromPolitiche());
				String pName;
				while (result.next()) {
					pName =result.getString("NomePolitica");
					if(policyName.equals(pName))
						thereIs = true;
				}
				result.close();
				stmt.close();
			}
		} catch (SQLException e) { // TODO COME GESTIRE QUESTO EVENTO ECCEZIONALE ? 
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return thereIs;
	}
	private void loadPolicyButtonActionPerformed(ActionEvent evt) {                                                 
		/**
		 * FIXME
		 * Interrogazione a MySQL-DB per selezionare tutte le politiche prensenti nel database.
		 * l'utente selezione una politica, comferma selezione
		 * viene visualizzato il tab relativo alla politica ..
		 * i tempi di risposta, credo, sono legati all'esecuzione delle operazioni di recupero dati dal database
		 *
		 */
		try {
			ResultSet result;
			Statement stmt = this.mySqlConnection.createStatement();
			result = stmt.executeQuery(SelectSQL.getQuerySelectCardinalitaTabellaPolitche());
			result.next();
			int policyTableSize = result.getInt("TableSize");
			if(policyTableSize == 0)
				JOptionPane.showMessageDialog(this, "\n The database is empty.\n\n Please add new policies in the database. ", "The Empty DataBase ", JOptionPane.WARNING_MESSAGE);
			else{
				//JOptionPane.showMessageDialog(this.loadPolicyButton, "\n The database is empty.\n\n Please add new policies in the database. ", "The Empty DataBase", JOptionPane.WARNING_MESSAGE);
				String[] policyNames = new String[result.getInt("TableSize")];
				result = stmt.executeQuery(SelectSQL.getQuerySelectNomePolicaFromPolitiche());
				System.out.println(policyNames.length);
				String policyName;
				Integer pkPolitica;
				int indexForPolicyNames = 0;
				while (result.next()) {
					policyName =result.getString("NomePolitica");
					pkPolitica = new Integer(result.getInt("PK_Politica"));
					this.politicheAsHashtable.put(policyName, pkPolitica);
					policyNames[indexForPolicyNames] = policyName;
					System.out.println(	policyNames[indexForPolicyNames]);
					indexForPolicyNames++;
				}
				result.close();
				stmt.close();
				// deve contenere l'elenco delle politiche presenti nel database
				String policyNameSelected = (String) JOptionPane.showInputDialog(null,
						"Choose Policy", "Load Policy Under Test",
						JOptionPane.INFORMATION_MESSAGE, null,
						policyNames, policyNames[0]);
				//System.out.println(policyNameSelected);
				if(policyNameSelected != null){
					String policyNameWithoutExt = policyNameSelected.substring(0, policyNameSelected.length()-4); 
					File policyTempDir = new File(this.canonicalTempDirPath+GuiCons.DIR_SEPARATOR+policyNameWithoutExt);
					if(!policyTempDir.exists())
						policyTempDir.mkdir();
					PolicyTab policyTab = new PolicyTab(this.jPolicyTabbedPane, this.politicheAsHashtable.get(policyNameSelected).intValue(), this.mySqlConnection, policyTempDir);
					policyTab.setVisible(true);
					this.jPolicyTabbedPane.addTab(policyNameSelected, policyTab);
					this.jPolicyTabbedPane.setSelectedComponent(policyTab);
					int policyTabPosition = this.jPolicyTabbedPane.getComponentZOrder(policyTab);
					policyTab.setPosition(policyTabPosition);
				}
			}
		} catch (SQLException e) { // TODO COME GESTIRE QUESTO EVENTO ECCEZIONALE ? 
			e.printStackTrace();
		}
	}
}
