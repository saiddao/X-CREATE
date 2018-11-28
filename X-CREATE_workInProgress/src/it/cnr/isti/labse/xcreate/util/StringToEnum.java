package it.cnr.isti.labse.xcreate.util;

import it.cnr.isti.labse.xcreate.xQuery.ElementsName;

public class StringToEnum {

	private static ElementsName assetClass;
		private static void setAssetClass(String assetClass) throws Exception{
		for(ElementsName ac : ElementsName.values()) {
			if(assetClass.equals(ac.toString())) {
				setAssetClass(ac);
				return;
			}
		}
		throw new Exception("Invalid asset class: " + assetClass);
	}

	private static void setAssetClass(ElementsName assetClass1) {
		assetClass = assetClass1;
	}
	public static ElementsName valueOf(String assetClass1){
		//System.out.println(assetClass1);
		try {
			setAssetClass(assetClass1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return assetClass;
	}
}
