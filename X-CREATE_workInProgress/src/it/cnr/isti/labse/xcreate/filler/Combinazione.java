package it.cnr.isti.labse.xcreate.filler;

public class Combinazione {
	private String sub;
	private String res;
	private String act;
	private String env;

	public Combinazione(String sub, String res, String act, String env) {

		this.sub = sub;
		this.res = res;
		this.act = act;
		this.env = env;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getSub() {
		return sub;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public String getRes() {
		return res;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public String getAct() {
		return act;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	public String getEnv() {
		return env;
	}
	public String toString(){
		String subString = (this.sub == null) ? "Null" : getSub();
		String resString = (this.res == null) ? "Null" : getRes();
		String actString = (this.act == null) ? "Null" : getAct();
		String envString = (this.env == null) ? "Null" : getEnv();
		return subString+"_"+resString+"_"+actString+"_"+envString;
	}
	// for MySQL
	public int getSubAsInt(){
		return (this.sub == null) ? -1 : Integer.parseInt(getSub());
	}
	public int getResAsInt(){
		return (this.res == null) ? -1 : Integer.parseInt(getRes());	
	}
	public int getActAsInt(){
		return (this.act == null) ? -1 : Integer.parseInt(getAct());
		
	}
	public int getEnvAsInt(){
		return (this.env == null) ? -1 : Integer.parseInt(getEnv());	
	}
}
