package it.cnr.isti.labse.xcreate.policyAnalyzer;

import it.cnr.isti.labse.xcreate.xSDResources.XacmlDataTypes;

public class Tupla {
	private String attributeValue;
	private String attributeId;
	private String dataType;
	private String issuer;
	private String subjectCategory;
	private String tuplaId;
	
	// per MySql 
	private int pkTupla;
	private String nomeTipoTupla;

	public int getPkTupla() {
		return pkTupla;
	}
	
	public void setPkTupla(int pkTupla) {
		this.pkTupla = pkTupla;
	}

	public Tupla(){
		this.attributeId = new String();
		this.attributeValue = new String();
		this.dataType = new String();
		this.issuer = new String();
		this.subjectCategory = new String();
		this.tuplaId = new String();
	}

	public Tupla(String attributeValue, String attributeId, String dataType, String issuer, String subjectCategory, String tuplaId) {
		setAttributeValue(attributeValue);
		setAttributeId(attributeId);
		setDataType(dataType);
		setIssuer(issuer);
		setSubjectCategory(subjectCategory);
		setTuplaId(tuplaId);
	}

	public void setTuplaId(String tuplaId2) {
		this.tuplaId = tuplaId2;
	}
	public String getTuplaId(){
		return this.tuplaId;
	}
	public String getAttributeId() {
		return (this.attributeId == null)? "" :this.attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public String getAttributeValue() {
		return this.attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSubjectCategory() {
		return this.subjectCategory;
	}

	public void setSubjectCategory(String subjectCategory) {
		this.subjectCategory = subjectCategory;
	}
	
	
	@Override
	public String toString() {
		return "\n Tupla [\n attributeId=" + attributeId + ",\n attributeValue="
				+ attributeValue + ",\n dataType=" + dataType + ",\n issuer="
				+ issuer + ",\n pkTupla=" + pkTupla + ",\n subjectCategory="
				+ subjectCategory + ",\n tuplaId=" + tuplaId + "\n]";
	}

	public void printTupla(){
		
		System.out.println(getTuplaId()+" -> "+getAttributeId()+"\t\t"+getDataType()
				+"\t\t"+getAttributeValue()
				+"\t"+getIssuer()+"\t"+
				getSubjectCategory());
	}
// per la generazione delle richieste 
	public void setTipoTupla(String nomeTipoTupla) {
		// TODO Auto-generated method stub
		this.nomeTipoTupla = nomeTipoTupla;
	}
	public String getTipoTupla() {
		// TODO Auto-generated method stub
		return this.nomeTipoTupla;
	}
	
	
	
// FIXME 2010-11-26
	public boolean hasResourceID() {
		// TODO Auto-generated method stub
//		return this.attributeId.endsWith("resource-id");
		return this.attributeId.contains("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
	}
	
	// FIXME 2012-10-26
	public void solveType(){
		
		if(getDataType().equals(XacmlDataTypes.INTEGER)){
			String attrValuTmp = new String(this.attributeValue);
			Integer hashCode = attrValuTmp.hashCode();
			setAttributeValue(String.valueOf(hashCode));
		}
	}
	
}
