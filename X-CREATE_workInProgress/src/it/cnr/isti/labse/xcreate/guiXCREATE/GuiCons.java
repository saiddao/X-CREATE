package it.cnr.isti.labse.xcreate.guiXCREATE;

import java.io.File;

public class GuiCons {
	// PER LE CARTELLE TEMPORANEE .. SONO SALVATE LE RICHIESTE GENERATE
	public static final String TEMP_DIR_NAME = "Temp_X-CREATE";
	public static final String SIMPLE_COMB_DIR_NAME = "Simple_Comb";
	public static final String HIER_SIMPLE_COMB_DIR_NAME = "Hier_Simple_Comb";
	public static final String COMB_FROM_INTER_REQ_DIR_NAME = "Comb_From_Intermediate_Requests";
	public static final String HIER_COMB_FROM_INTER_REQ_DIR_NAME = "Hier_Comb_From_Intermediate_Requests";
	public static final String MULT_COMB_DIR_NAME = "Multiple_Comb";
	public static final String HIER_MULT_COMB_DIR_NAME = "Hier_Multiple_Comb";
	
	// vediamo come trattarli ...
	public static final File TEMP_DIR = new File("."+File.separator+TEMP_DIR_NAME);
	public static final File SIMPLE_COMB_DIR = new File("."+File.separator+SIMPLE_COMB_DIR_NAME);
	public static final File HIER_SIMPLE_COMB_DIR = new File("."+File.separator+HIER_SIMPLE_COMB_DIR_NAME);
	public static final File COMB_FROM_INTER_REQ_DIR = new File("."+File.separator+COMB_FROM_INTER_REQ_DIR_NAME);
	public static final File HIER_COMB_FROM_INTER_REQ_DIR = new File("."+File.separator+HIER_COMB_FROM_INTER_REQ_DIR_NAME);
	public static final File MULT_COMB_DIR = new File("."+File.separator+MULT_COMB_DIR_NAME);
	public static final File HIER_MULT_COMB_DIR = new File("."+File.separator+HIER_MULT_COMB_DIR_NAME);
	
	// il separatore di cartelle
	public static final String DIR_SEPARATOR = File.separator;
}
