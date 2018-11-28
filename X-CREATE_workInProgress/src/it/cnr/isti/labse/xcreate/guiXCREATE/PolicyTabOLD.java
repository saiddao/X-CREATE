package it.cnr.isti.labse.xcreate.guiXCREATE;

import it.cnr.isti.labse.xcreate.filler.RequestsGeneratorFromIntermediateRequests;
import it.cnr.isti.labse.xcreate.filler.SimpleRequestsGenerator;
import it.cnr.isti.labse.xcreate.util.Util;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class PolicyTabOLD extends JPanel {
	private static final long serialVersionUID = -7554503318973121641L;
	// per la logica X-CREATE
	private int pkPolitica;
	private Connection mySqlConnection;
	private int countCombinationsFromRoot;
	private File policyTempDir;
	// per la gui X-CREATE
	private JButton closeTabButton;
	private JButton generateButton;
	private JTree jPolicyTree;
	private JScrollPane jPolicyTreeScrollPane;
	private Label nameStrategyLabel;
	private JComboBox strategy;
	private JButton viewGeneretedReqButton;
	private final JTabbedPane myParent;
	private int myPosition;
	private JNodoInfo jPolicyRootNodeInfo;
	private DefaultMutableTreeNode jPolicyRootNode;
	private File simpleCombDir;
	private File combFromInterDir;
	private File hierSimpleCombDir;
	private File hierCombFromInterDir;
	private File currentReqDir;

	/** Creates new form PolicyPanel 
	 * @param pkPolitica 
	 * @param mySqlConnection 
	 * @param tempDirPath 
	 * @param policyNameSelected 
	 */
	public PolicyTabOLD(JTabbedPane myParent, int pkPolitica, Connection mySqlConnection, File policyTempDir) {
		super();
		this.myParent = myParent;
		this.pkPolitica = pkPolitica;	
		this.mySqlConnection = mySqlConnection;
		this.policyTempDir = policyTempDir;
		initComponents();
	}

	private void initComponents() {
		try {
			String policyCanonicalDirPath;

			policyCanonicalDirPath = this.policyTempDir.getCanonicalPath();

			//System.out.println(policyCanonicalDirPath);
			this.simpleCombDir = new File(policyCanonicalDirPath+GuiCons.DIR_SEPARATOR+GuiCons.SIMPLE_COMB_DIR_NAME);
			this.combFromInterDir = new File(policyCanonicalDirPath+GuiCons.DIR_SEPARATOR+GuiCons.COMB_FROM_INTER_REQ_DIR_NAME);
			this.hierSimpleCombDir = new File(policyCanonicalDirPath+GuiCons.DIR_SEPARATOR+GuiCons.HIER_SIMPLE_COMB_DIR_NAME);
			this.hierCombFromInterDir = new File(policyCanonicalDirPath+GuiCons.DIR_SEPARATOR+GuiCons.HIER_COMB_FROM_INTER_REQ_DIR_NAME);
			if(!this.simpleCombDir.exists())
				this.simpleCombDir.mkdirs();
			if(!this.combFromInterDir.exists())
				this.combFromInterDir.mkdirs();
			if(!this.hierSimpleCombDir.exists())
				this.hierSimpleCombDir.mkdirs();
			if(!this.hierCombFromInterDir.exists())
				this.hierCombFromInterDir.mkdirs();
			
			this.jPolicyTreeScrollPane = new JScrollPane();
			/*
			 * FIXME FARE MOLTA ATTENZIONE !!!!!!!!
			 */
			this.jPolicyTree = new JTree(PolicyTreeSQL.getRoot(this.mySqlConnection, this.pkPolitica));
			this.countCombinationsFromRoot = PolicyTreeSQL.countCombinationsFromRoot(this.mySqlConnection, this.pkPolitica);		
			this.jPolicyTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

			this.jPolicyRootNode = (DefaultMutableTreeNode) this.jPolicyTree.getModel().getRoot();
			this.jPolicyRootNodeInfo = (JNodoInfo)this.jPolicyRootNode.getUserObject();

			this.nameStrategyLabel = new Label();
			this.generateButton = new JButton();
			this.closeTabButton = new JButton();
			this.strategy = new JComboBox();
			this.viewGeneretedReqButton = new JButton();

			//jPolicyTree.setModel(new PolicyTreeModel(jPolicyTree));

			this.jPolicyTreeScrollPane.setViewportView(jPolicyTree);

			this.nameStrategyLabel.setText("Select Request Generation Strategy");

			this.generateButton.setText("Generate Requests");
			this.generateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					generateButtonActionPerformed(evt);
				}
			});

			this.closeTabButton.setText("Close Tab");
			this.closeTabButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					closeTabButtonActionPerformed(evt);
				}
			});

			this.strategy.setModel(getStrategyModel());
			this.strategy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					strategyActionPerformed(evt);
				}
			});

			this.viewGeneretedReqButton.setText("View generated requests");
			this.viewGeneretedReqButton.setEnabled(false);
			this.viewGeneretedReqButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					viewGeneretedReqButtonActionPerformed(evt);
				}
			});
			GroupLayout layout = new GroupLayout(this);
			this.setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addComponent(jPolicyTreeScrollPane, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
											.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
													.addComponent(strategy, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
													.addComponent(nameStrategyLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
													.addGroup(layout.createSequentialGroup()
															.addComponent(viewGeneretedReqButton)
															.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
															.addComponent(generateButton)))
															.addGap(109, 109, 109))
															.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
																	.addComponent(closeTabButton)
																	.addGap(24, 24, 24))))
			);
			layout.setVerticalGroup(
					layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addGroup(layout.createSequentialGroup()
											.addComponent(jPolicyTreeScrollPane, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
											.addContainerGap())
											.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
													.addComponent(closeTabButton)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 167, Short.MAX_VALUE)
													.addComponent(nameStrategyLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(strategy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
															.addComponent(generateButton)
															.addComponent(viewGeneretedReqButton))
															.addGap(154, 154, 154))))
			);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void strategyActionPerformed(ActionEvent evt) {                                         
		// TODO
		System.out.println(this.strategy.getSelectedIndex());
		System.out.println(this.strategy.getItemAt(this.strategy.getSelectedIndex()));
	}                                        

	private void closeTabButtonActionPerformed(ActionEvent evt) {                                               
		// TODO
		System.out.println(this.myPosition);
		this.myPosition = this.myParent.getComponentZOrder(this);
		this.myParent.remove(this.myPosition);
	}                                              

	private void generateButtonActionPerformed(ActionEvent evt) {                                               
		String ppm;
		int count;
		JFileChooser fc;
		File[] files;
		switch (this.strategy.getSelectedIndex()) {
		case 0:
			System.out.println(0);
			/*
			 * recuparare il numero di richieste generabili
			 *  effettuare il controllo del valore immesso
			 *
			 * generare le richieste
			 * rendere attivo il bottone view
			 */
			ppm = JOptionPane.showInputDialog((String) this.strategy.getItemAt(this.strategy.getSelectedIndex()) + "   \n \nMaximum number of requests generated : " + this.countCombinationsFromRoot+"\n\n  ");
			//			ppm = JOptionPane.showInputDialog((String) this.strategy.getItemAt(this.strategy.getSelectedIndex()) + "\n Max " + count);
			if (ppm != null) {
				if(!ppm.equals(""))
					if(Util.isANumber(ppm))
						if (Integer.parseInt(ppm) <= this.countCombinationsFromRoot && Integer.parseInt(ppm)>0) {
							// generare le combinazioni
							DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.jPolicyTree.getModel().getRoot();
							//System.out.println("Nodo radice "+((JNodoInfo)root.getUserObject()).toString1());

//							System.out.println("JROOTNODE : "+this.jPolicyRootNodeInfo.toString1());
//							System.out.println("JROOTNODE : "+((JNodoInfo)root.getUserObject()).toString1());

							SimpleRequestsGenerator simpleRequestsGenerator = 
								new SimpleRequestsGenerator(this.mySqlConnection, Integer.parseInt(ppm), 
										this.pkPolitica, ((JNodoInfo)root.getUserObject()).getPkNodo(), this.simpleCombDir);
							this.currentReqDir = simpleRequestsGenerator.generatesRequests();
							System.out.println(Integer.parseInt(ppm));


							this.viewGeneretedReqButton.setEnabled(true);
						} else {
							JOptionPane.showMessageDialog(this.myParent, "\n il numero immesso non valido  \n si prega di ripetere la procedura ", "Numero immesso sbagliato", JOptionPane.WARNING_MESSAGE);
						}
					else
						JOptionPane.showMessageDialog(this.myParent, "\n Immettere un numero !!  \n si prega di ripetere la procedura ", "Numero immesso sbagliato", JOptionPane.WARNING_MESSAGE);
			}
			break;
		case 1:
			/*
			 * generazione delle richieste a partire dalla richieste intermedie 
			 */
			fc = new JFileChooser(new File("."));
			fc.addChoosableFileFilter(new EstensionFilter("XmL")); 
			fc.setDialogTitle("Choose Intermediate Requests");
			fc.setMultiSelectionEnabled(true);
			fc.showOpenDialog(this);
//			System.out.println("Nodo radice "+this.jPolicyRootNodeInfo.toString1());
			files = fc.getSelectedFiles();
			RequestsGeneratorFromIntermediateRequests requestsGenFromInterReqs = 
				new RequestsGeneratorFromIntermediateRequests(this.pkPolitica, 
						this.jPolicyRootNodeInfo.getPkNodo(),
						this.mySqlConnection, files, this.combFromInterDir);
			//requestsGenFromInterReqs.generates();
	//		System.out.println("############################################");
			requestsGenFromInterReqs.generatesRequests();
		//	System.out.println("############################################");
			/*			for (int i = 0; i < files.length; i++) {
				try {
					System.out.println(files[i].getCanonicalFile());
				} catch (IOException ex) {
					System.out.println("Errore Qui ::::: ");
					//Logger.getLogger(PolicyTab.class.getName()).log(Level.SEVERE, null, ex);
				}
			}*/
			break;
		case 2:

			if(this.jPolicyTree.getSelectionCount() == 0)
				JOptionPane.showMessageDialog(this.myParent, "Please select a node ", "Select a node", JOptionPane.WARNING_MESSAGE);
			else {
				DefaultMutableTreeNode policyTreeNode = (DefaultMutableTreeNode) this.jPolicyTree.getLastSelectedPathComponent();
				JNodoInfo nodoInfo = (JNodoInfo) policyTreeNode.getUserObject();
				System.out.println(policyTreeNode.isRoot());
				// seleziona il nodo radice FIXME 
				//DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.jPolicyTree.getModel().getRoot();
//				System.out.println("Nodo radice "+((JNodoInfo)root.getUserObject()).toString1());
//				System.out.println(" Nodo selezionato : "+nodoInfo.toString1());
//				System.out.println("JROOTNODE : "+this.jPolicyRootNodeInfo.toString1());
				if(policyTreeNode.isRoot())
					count = this.countCombinationsFromRoot;
				else 
					count = PolicyTreeSQL.countCombinationsFromANode(this.mySqlConnection, this.pkPolitica, nodoInfo.getPkNodo());

				ppm = JOptionPane.showInputDialog((String) this.strategy.getItemAt(this.strategy.getSelectedIndex()) + "   \n \nMaximum number of requests generated : " + count+"\n\n  ");

				if (ppm != null) {
					if(!ppm.equals("")  )
						if(Util.isANumber(ppm))
							if (Integer.parseInt(ppm) <= count && Integer.parseInt(ppm)>0) {
								// generare le combinazioni
								SimpleRequestsGenerator simpleRequestsGenerator = 
									new SimpleRequestsGenerator(this.mySqlConnection, 
											Integer.parseInt(ppm), this.pkPolitica, nodoInfo.getPkNodo(), this.hierSimpleCombDir);	
								this.currentReqDir = simpleRequestsGenerator.generatesRequests();
								//System.out.println(Integer.parseInt(ppm));	
								this.viewGeneretedReqButton.setEnabled(true);
							} else {
								JOptionPane.showMessageDialog(this.myParent, "\n Il numero immesso non valido  \n si prega di ripetere la procedura. ", "Numero immesso sbagliato", JOptionPane.WARNING_MESSAGE);
							}
						else
							JOptionPane.showMessageDialog(this.myParent, "\n Immettere un numero !!  \n si prega di ripetere la procedura ", "Numero immesso sbagliato", JOptionPane.WARNING_MESSAGE);
				}
			}
			break;
		case 3:
			if(this.jPolicyTree.getSelectionCount() == 0)
				JOptionPane.showMessageDialog(this.myParent, "Please select a node. ", "Select a node", JOptionPane.WARNING_MESSAGE);
			else {
				fc = new JFileChooser(new File("."));
				fc.setDialogTitle("Choose Intermediate Requests");
				fc.addChoosableFileFilter(new EstensionFilter("XmL"));
				fc.setMultiSelectionEnabled(true);
				fc.showOpenDialog(this);
				files = fc.getSelectedFiles();
				for (int i = 0; i < files.length; i++) {
					try {
						System.out.println(files[i].getCanonicalFile());
					} catch (IOException ex) {
						Logger.getLogger(PolicyTab.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			break;
		default:
			break;
		}
	}                                              

	private void viewGeneretedReqButtonActionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		/*
		JFileChooser fc = new JFileChooser(this.currentReqDir);
		fc.setDialogTitle("Generated Requests");
		fc.addChoosableFileFilter(new EstensionFilter("XmL"));
		fc.setMultiSelectionEnabled(true);
		fc.showOpenDialog(this);
		*/
		try {
		Frame f = new Frame();
		FileDialog fd = new FileDialog(f);
		fd.setTitle("Generated Requests");
		
			fd.setDirectory(this.currentReqDir.getCanonicalPath());
			//fd.setFilenameFilter((FilenameFilter) new EstensionFilter("xml"));
			fd.setVisible(true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public DefaultComboBoxModel getStrategyModel() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(String.valueOf("Simple Combinatorial Generation"));
		model.addElement(String.valueOf("Combinatorial Generation from Intermediate Requests"));
		model.addElement(String.valueOf("Hierarchical Simple Combinatorial Generation"));
		model.addElement(String.valueOf("Hierarchical Combinatorial Generation from Intermediate Requests"));
		return model;
	}
	void setPosition(int countTabs) {
		this.myPosition = countTabs;
	}
}
