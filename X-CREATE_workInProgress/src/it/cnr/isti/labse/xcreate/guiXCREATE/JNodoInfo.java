package it.cnr.isti.labse.xcreate.guiXCREATE;
/*
 * Rappresenta un nodo dell'albero da visualizzare 
 */
public class JNodoInfo {
	private int fkPolicy;
	private int pkNodo;
	private int fkPadre;
	private String nodeName;
	
	private boolean red;
	public boolean isRed(){
		return this.red;
	}
	public void setRed(boolean value){
		this.red = value;
	}
	public JNodoInfo(int fkPolicy, int pkNodo, int fkPadre, String nodeName) {
		super();
		this.fkPolicy = fkPolicy;
		this.pkNodo = pkNodo;
		this.fkPadre = fkPadre;
		this.nodeName = nodeName;
	}
	public int getFkPolicy() {
		return this.fkPolicy;
	}
	public void setFkPolicy(int fkPolicy) {
		this.fkPolicy = fkPolicy;
	}
	public int getPkNodo() {
		return this.pkNodo;
	}
	public void setPkNodo(int pkNodo) {
		this.pkNodo = pkNodo;
	}
	public int getFkPadre() {
		return this.fkPadre;
	}
	public void setFkPadre(int fkPadre) {
		this.fkPadre = fkPadre;
	}
	public String getNodeName() {
		return this.nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String toString1() {
		return "JNodoInfo [fkPadre=" + fkPadre + ", fkPolicy=" + fkPolicy
				+ ", nodeName=" + nodeName + ", pkNodo=" + pkNodo + "]";
	}

	@Override
	public String toString() {
		return this.nodeName;
	}
}
