package it.cnr.isti.labse.xcreate.guiXCREATE;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class EstensionFilter extends FileFilter {

	private String filter;
	
	public EstensionFilter(String filter){
		super();
		this.filter = filter.toLowerCase();
	}
	
	public boolean accept(File file) { 
		return file.getName().toLowerCase().endsWith("."+filter) || file.isDirectory(); 
	} 
	
	public String getDescription() { 
		return "*."+filter; 
	} 
}

