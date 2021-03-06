package it.cnr.isti.labse.xcreate.guiXCREATE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class WelcomeTab extends JPanel {

    // Variables declaration - do not modify
    private JLabel jLabel2;
    private JPanel jScrollPane2;
    private JTextArea jTextArea1;
    // End of variables declaration
	/**
	 * 
	 */
	private static final long serialVersionUID = 2461744805568429350L;

//	public WelcomeTab() {
//		// TODO ancora da fare ... presentazione di x-create .. una piccola quida utente.. le principali funzionalita'
//		super();
//		
//	}
	
    /**
     * Creates new form Welcome
     */
    public WelcomeTab() {
    	super();
    	initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        
        jLabel2 = new JLabel();
        jScrollPane2 = new JPanel();
        jTextArea1 = new JTextArea();

        
//        jLabel2.setIcon(new ImageIcon("C:\\Users\\Nice\\Desktop\\xcreate_small.png")); // NOI18N

        jTextArea1.setBackground(new Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setFont(new Font(Font.SERIF, Font.ITALIC, 14));
        String testo = "<html><body>" +
        		"<p align=\"justify\">" +
        		"X-CREATE (XaCml REquests derivAtion for TEsting) is a framework for the " +
		"automated derivation of a test suite starting by a XACML policy. " +
		"It exploits the XACML Context Schema to derive a universally valid " +
		"conforming test suite of XACML requests that are customizable by " +
		"the X-CREATE framework, to any specific policy. " +
		"</p>" +
		"<br />" +
		"The framework consists of three main components: " +
		"<ol>" +
		"<li>" +
		"an intermediate-request generator, which is based on the XML Partition " +
		"Testing (XPT) approach for intermediate instances (request structures) " +
		"generation;" +
		"<li>" +
		"a policy analyzer which selects the input values from the policy " +
		"specification;" +
		"<li>a values manager which distributes the input values " +
		"to the request structures. " +
		"</ol>" +
		"<p align=\"justify\">" +
		"X-CREATE implements three different tests derivation strategies " +
		"based on a combinatorial approach." +
		"</p>" +
		"</body></html>";
        
//        jTextArea1.setText(
//        		"<html>\nX-CREATE (XaCml REquests derivAtion for TEsting) is a framework for the " +
//        		"\nautomated derivation of a test suite starting by a XACML policy. " +
//        		"\nIt exploits the XACML Context Schema to derive a universally valid " +
//        		"\nconforming test suite of XACML requests that are customizable by " +
//        		"\nthe X-CREATE framework, to any specific policy. " +
//        		"\n\nThe framework consists of three main components: " +
//        		"\nan intermediate-request generator, which is based on the XML Partition " +
//        		"\nTesting (XPT) approach for intermediate instances (request structures) " +
//        		"\ngeneration;" +
//        		"\na policy analyzer which selects the input values from the policy " +
//        		"\nspecification and a values manager, which distributes the input values " +
//        		"\nto the request structures. " +
//        		"\n\nX-CREATE implements three different tests derivation strategies based on a combinatorial approach." +
//        		"");
        jTextArea1.setBorder(BorderFactory.createTitledBorder(null, "X-CREATE Framework for XACML", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 17))); // NOI18N
//        jScrollPane2.add(jTextArea1);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
 
                .addComponent(jLabel2)
                .addGap(41, 41, 41)
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 620, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 354, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        
        
        JLabel testoHtml = new JLabel(testo);
        testoHtml.setBorder(BorderFactory.createTitledBorder(null, "X-CREATE Framework for XACML", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 17)));
        
        
        
        JLabel said = new JLabel(testo) {
            /**
			 * 
			 */
			private static final long serialVersionUID = -8968936499896337304L;
			public Dimension getPreferredSize() {
                return new Dimension(615, 300);
            }
            public Dimension getMinimumSize() {
                return new Dimension(615, 300);
            }
            public Dimension getMaximumSize() {
                return new Dimension(615, 300);
            }
        };
        said.setBorder(BorderFactory.createTitledBorder(null, "X-CREATE Framework for XACML", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 17)));
        
        
        jScrollPane2.add(said);
//        jScrollPane2.add(new JLabel(testo));
    }// </editor-fold>


	
	
	
	
	
	
	
	
}
