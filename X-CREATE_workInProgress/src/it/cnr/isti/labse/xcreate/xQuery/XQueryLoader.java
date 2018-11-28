package it.cnr.isti.labse.xcreate.xQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Legge la XQuery da un file e la restituisce come stringa
 *
 */
public class XQueryLoader {
	private String xQueryLocation;
	private StringBuilder xQuery;
	//private String policyName; 
	
	public XQueryLoader(String policyName){
		try {
			// FIXME RISTRUTTURARE LA XQURY
			/*
			 * DIVEDERE IL FILE IN PARTI IN MODO CHE IL NOME DELLA POLITICA DA 
			 * ANALIZZARE RISULTI DINAMICO
			 * 
			 */
			//this.policyName = policyName;
			this.xQuery = new StringBuilder();
			BufferedReader in = new BufferedReader(new FileReader(getXQueryPath()));
			String str;
			while ((str = in.readLine()) != null) {
				this.xQuery.append(str);
				this.xQuery.append("\n");
			}
			in.close(); 
		} catch (IOException e){
			System.out.println(e); 
		} 
	}
	public String getXQuery(){
		return this.xQuery.toString();
	}
	
	public String getXQueryPath(){
	        File d = new File (".");
	        return getAbsolutePath (d);
	}
	
	public String getAbsolutePath (File dir){
	    File[] entries = dir.listFiles ();
        if (entries != null) {
            for (int i = 0; i < entries.length; i++){
                if (entries[i].isDirectory ())
                    getAbsolutePath (entries[i]);
                else
                	if(entries[i].getName().equalsIgnoreCase("XQPolicyParser.xquery") && 
                			entries[i].getAbsolutePath().contains("src"))
                    this.xQueryLocation = entries[i].getAbsolutePath();
            }
        }
        return this.xQueryLocation;
    }
}
