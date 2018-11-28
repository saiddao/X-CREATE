package it.cnr.isti.labse.xcreate.dbDrivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MySQLCons {
	public static final String XCREATE_PROPERTIES_FILE_NAME = "xcreate.properties";
	public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    
	private static final String URL_PROPERTY = "jdbc.url";//"jdbc:mysql://localhost:3306/mysql";//
    private static final String USERNAME_PROPERTY = "jdbc.username";//"root";//
    private static final String PASSWORD_PROPERTY = "jdbc.password";//"pippuzzo";//
	
    public static String LOGIN_MYSQL;
	public static String PASSWORD_MYSQL;
	public static String URL_MYSQL;
	public static final String DATABASE_NAME = "xcreate";
	
	  //VALORI MAX E MIN 
    private static final String VALORI_P = "statistic.bool";
    private static final String MIN_ENV_P = "valore.minEnv";
    private static final String MAX_SUB_P = "valore.maxSub";
    private static final String MIN_SUB_P = "valore.minSub";
    private static final String MAX_RES_P = "valore.maxRes";
    private static final String MIN_RES_P = "valore.minRes";
    private static final String MAX_ACT_P = "valore.maxAct";
    private static final String MIN_ACT_P = "valore.minAct";
    private static final String MAX_ENV_P = "valore.maxEnv";
    //opsione sugli AttributeId
    private static final String DIVERSI = "attributeDiversi";
    
    
    public static boolean Valori;
    public static int Min_Env;
    public static int Max_Sub;
    public static int Max_Res;
    public static int Max_Act;
    public static int Max_Env;
    public static int Min_Sub;
    public static int Min_Res;
    public static int Min_Act;
    
    public static boolean Attr_diversi;
    
	// TABLE NAMES
	public static final String COMBINAZIONI = "Combinazioni";
	public static final String TUPLE = "Tuple";
	public static final String TIPI_TUPLA = "TipiTupla";
	public static final String TIPI_COMBINAZIONE = "TipiCombinazione";
	public static final String DISCENDENTE_ANTENATO = "DiscendenteAntenato";
	public static final String NODI = "Nodi";
	public static final String TIPI_NODO = "TipiNodo";
	public static final String POLITICHE = "Politiche";
	
	// TYPE COMBINATION NAMES
	public static final String ONE_WISE = "1-Wise";
	public static final String PAIR_WISE = "2-Wise";
	public static final String THREE_WISE = "3-Wise";
	public static final String FOUR_WISE = "4-Wise";
	
	private static String propertiesLocation;
	
	public static void init(){
		propertiesLocation = getAbsolutePath(new File("."), XCREATE_PROPERTIES_FILE_NAME);
		Properties p = new Properties();
	    FileInputStream fis;
		try {
			fis = new FileInputStream(propertiesLocation);
			p.load(fis);
			fis.close();
			LOGIN_MYSQL = p.getProperty(USERNAME_PROPERTY);//"xcreateuser";
			PASSWORD_MYSQL = p.getProperty(PASSWORD_PROPERTY);//"etaercx";
			URL_MYSQL = p.getProperty(URL_PROPERTY);//"jdbc:mysql://pc-ericlab18.isti.cnr.it:3306/xcreate";
			// per i valori max e min
			Valori = Boolean.valueOf(p.getProperty(VALORI_P));
			Min_Env = Integer.parseInt(p.getProperty(MIN_ENV_P));
			Max_Sub = Integer.parseInt(p.getProperty(MAX_SUB_P));
			Max_Res = Integer.parseInt(p.getProperty(MAX_RES_P));
			Max_Act = Integer.parseInt(p.getProperty(MAX_ACT_P));
			Max_Env = Integer.parseInt(p.getProperty(MAX_ENV_P));
			Min_Sub = Integer.parseInt(p.getProperty(MIN_SUB_P));
			Min_Res = Integer.parseInt(p.getProperty(MIN_RES_P));
			Min_Act = Integer.parseInt(p.getProperty(MIN_ACT_P));
			//per la politica TAS
			Attr_diversi = Boolean.valueOf(p.getProperty(DIVERSI));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getAbsolutePath (File dir, String fileName){
		File[] entries = dir.listFiles ();
		if (entries != null) {
			for (int i = 0; i < entries.length; i++){
				if (entries[i].isDirectory ())
					getAbsolutePath (entries[i], fileName);
				else
					if(entries[i].getName().equalsIgnoreCase(String.valueOf(fileName)))
						propertiesLocation = entries[i].getAbsolutePath();
			}
		}
		return propertiesLocation;
	}
}
