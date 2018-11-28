package it.cnr.isti.labse.xcreate.guiXCREATE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class XCreateMainNewStarter {

	public static void main(String[] args) {
		try{
			JWindow jwin = new JWindow();	
			// inizializza e esegue anaminazione iniziale (attesa iniziale)
			System.out.println("Inizio Animazione");
			init(jwin);
			
			// istanzia X-CREATE
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					XCreateMain xcreate = new XCreateMain();
					xcreate.setVisible(true);
				}
			});
			
			// annulla animazione iniziale ()
			jwin.dispose();
			System.out.println("Fine Animazione");
			
			
		}catch (Exception e) {
			System.err.println("Errore Main : "+e.toString());
		}

	}

	private static void init(JWindow jwin) {
		// creazione di uno splashscreen iniziale

		// si crea la finestra per lo splashscreen

		// pannelo da posizionare sulla finsetra
		JPanel panel = new JPanel();
		/**
		 * dispone gli elementi sul pannello da sinistra versdo destra.
		 */
		panel.setLayout(new FlowLayout());

		Object ob = new Object();
		URL url;
		
		JLabel titoloLabel = new JLabel("X - CREATE");// \n -- XaCml REquests derivAtion for TEsting --");
//		url = ob.getClass().getResource("/icons/xcreate_1.png");
//		url = ob.getClass().getResource("/icons/xcreate_Livelli_2_1.png");
//		panel.add(new JLabel(new ImageIcon(url)));
		titoloLabel.setForeground(Color.GRAY);
		titoloLabel.setFont(new Font("SanSerif", Font.BOLD, 38));
		titoloLabel.setFont(new Font(Font.SERIF, Font.ITALIC, 38));
		
		
		panel.add(titoloLabel);
		// setta lo sfondo del pannello color arancio.
		panel.setBackground(Color.ORANGE);//ORANGE);
		
		JPanel panelSottoTitolo = new JPanel();
		JLabel sottoTitoloLabel = new JLabel("XaCml REquests derivAtion for TEsting");
		sottoTitoloLabel.setFont(new Font("SanSerif", Font.BOLD, 22));
		panelSottoTitolo.add(sottoTitoloLabel);

		/**
		 * creiamo anche una label, con una etichetta con la stringa da
		 * visualizzare
		 */
		JLabel label = new JLabel("X - Loading . . .");//XaCml REquests derivAtion for TEsting");
		// setting del font della label.
		label.setFont(new Font("SanSerif", Font.BOLD, 14));
		label.setForeground(Color.GRAY);
		// creiamo un altro pannello.
		JPanel panel1 = new JPanel();

		// a questo pannello aggiungiamo la label creata poco fa.
		panel1.add(label);

		// settiamo anche lo sfondo di panel1 color arancio
		// in modo che il colore sia omogeneo.
		panel1.setBackground(Color.ORANGE);

		// creiamo un altro pannello ancora e chiamiamolo panel2.
		JPanel panel2 = new JPanel();
		// immagine x-create centrale
		url = ob.getClass().getResource("/icons/xcreate_Livelli_2_1.png");

		JLabel label2 = new JLabel(new ImageIcon(url));

		// a panel2 settiamo invece il colore dello sfondo bianco.
		panel2.setBackground(Color.WHITE);

		// a panel2 aggiungiamo label2.
		panel2.add(label2,BorderLayout.CENTER);//AFTER_LAST_LINE);

		/*
		 * settiamo le dimensioni dello splashscreen e gli aggiungiamo, con
		 * layout BorderLayout, rispettivamente panel in alto nello splashcreen,
		 * panel2 nel centro e panel1 in basso sotto panel2. Inoltre settiamo le
		 * cordinate della posizione iniziale dello splashscreen in modo che sia
		 * centrato allo schermo.
		 */

		// jwin.setBounds(400, 400, 400, 200);
		centerWin(jwin);
		jwin.setSize(400, 170);
		jwin.getContentPane().add(panel, BorderLayout.NORTH);
		
//		jwin.getContentPane().add(panelSottoTitolo, BorderLayout.CENTER);
		
		jwin.getContentPane().add(panel2, BorderLayout.CENTER);
		jwin.getContentPane().add(panel1, BorderLayout.SOUTH);

		jwin.getContentPane().setVisible(true);

		
		
		
		// settiamo visibile lo splashscreen.
		jwin.setVisible(true);
		// resta visibile per 5 secondi.
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		// dopo cinque secondi chiude lo splaschscreen.
//		jwin.dispose();

		
	}

	private static void centerWin(JWindow jwin) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension f = jwin.getPreferredSize();
		jwin.setLocation(screenSize.width / 3 - (f.width / 3), screenSize.height / 3 - (f.height / 3));		
	}

}
