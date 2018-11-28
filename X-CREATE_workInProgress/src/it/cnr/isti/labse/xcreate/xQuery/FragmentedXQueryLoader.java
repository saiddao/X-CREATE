package it.cnr.isti.labse.xcreate.xQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Legge la XQuery da un file e la restituisce come stringa
 *
 */
public class FragmentedXQueryLoader {
	private String xQueryLocation;
	private StringBuilder xQuery;
	private String policyName; 

	
	// modificato il 22 Novembre 2011
	public FragmentedXQueryLoader(String policyName){
		this.policyName = policyName;
		this.xQuery = new StringBuilder();
		
		
		this.xQuery.append(XQueries.getXQPolicyParser_Part1());
		this.xQuery.append(this.policyName);
		this.xQuery.append(XQueries.getXQPolicyParser_Part2());
		this.xQuery.append(this.policyName);
		this.xQuery.append(XQueries.getXQPolicyParser_Part3());
		this.xQuery.append(this.policyName);	
		this.xQuery.append(XQueries.getXQPolicyParser_Part4());
//	System.out.println(this.xQuery.toString());	
	
	
		
	}
	
	
	
	public void FragmentedXQueryLoader_old(String policyName){
		try {
			// FIXME RISTRUTTURARE LA XQURY
			/*
			 * DIVEDERE IL FILE IN PARTI IN MODO CHE IL NOME DELLA POLITICA DA 
			 * ANALIZZARE RISULTI DINAMICO
			 * 
			 */
			this.policyName = policyName;
			this.xQuery = new StringBuilder();
			
			/*
			 * Lettura primo frammento
			 */
			BufferedReader in = new BufferedReader(new FileReader(getXQueryPart1Path()));
			boolean firstLine = true;
			String str;
			while ((str = in.readLine()) != null) {
					if (!firstLine)
						this.xQuery.append("\n");				
					this.xQuery.append(str);
					firstLine = false;
			}
			in.close(); 
			this.xQuery.append(this.policyName);
			
			/*
			 * Lettura secondo grammento
			 */
			firstLine = true;
			in = new BufferedReader(new FileReader(getXQueryPart2Path()));
			while ((str = in.readLine()) != null) {
				if (!firstLine)
					this.xQuery.append("\n");
				this.xQuery.append(str);
				firstLine = false;
			}
			in.close();
			this.xQuery.append(this.policyName);
			
			/*
			 * lettura terzo frammento
			 */
			firstLine = true;
			in = new BufferedReader(new FileReader(getXQueryPart3Path()));
			while ((str = in.readLine()) != null) {
				if (!firstLine)
					this.xQuery.append("\n");
				this.xQuery.append(str);
				firstLine = false;
			}
			in.close();
			this.xQuery.append(this.policyName);
			
			/*
			 * lettura quarto frammento
			 */
			in = new BufferedReader(new FileReader(getXQueryPart4Path()));
			while ((str = in.readLine()) != null) {
				this.xQuery.append(str);
				this.xQuery.append("\n");
			}
			in.close(); 
			//System.out.println(this.xQuery);
		} catch (IOException e){
			System.out.println(e); 
		} 
	}
	public String getXQuery(){
		return this.xQuery.toString();
	}

	public String getXQueryPath(String fragmentedName){
		File d = new File (".");
		return getAbsolutePath (d, fragmentedName);
	}

	public String getXQueryPart1Path(){
		return getXQueryPath("XQPolicyParser_Part1.xquery");
	}
	public String getXQueryPart2Path(){
		return getXQueryPath("XQPolicyParser_Part2.xquery");
	}
	public String getXQueryPart3Path(){
		return getXQueryPath("XQPolicyParser_Part3.xquery");
	}
	public String getXQueryPart4Path(){
		return getXQueryPath("XQPolicyParser_Part4.xquery");
	}

	public String getAbsolutePath (File dir, String fragmentedName){
		File[] entries = dir.listFiles ();
		if (entries != null) {
			for (int i = 0; i < entries.length; i++){
				if (entries[i].isDirectory ())
					getAbsolutePath (entries[i], fragmentedName);
				else
					if(entries[i].getName().equalsIgnoreCase(String.valueOf(fragmentedName)) && 
							entries[i].getAbsolutePath().contains("src"))
						this.xQueryLocation = entries[i].getAbsolutePath();
			}
		}
		return this.xQueryLocation;
	}
}
