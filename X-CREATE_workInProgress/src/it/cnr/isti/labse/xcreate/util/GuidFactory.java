package it.cnr.isti.labse.xcreate.util;

public class GuidFactory {
	private int guid;
	public GuidFactory(){
		this.guid = 1;
	}
	public String getGuid(){
	
		return String.valueOf(this.guid++);
	}
	public int getSize() {
		
		return this.guid-1;
	}
}