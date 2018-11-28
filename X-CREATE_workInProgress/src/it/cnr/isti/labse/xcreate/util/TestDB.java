package it.cnr.isti.labse.xcreate.util;


public class TestDB {		
	public static void main(String args[]) throws Exception {

		
		ExistDatabase  existDatabase = new ExistDatabase();
		existDatabase.initialize();
		
		
		System.out.println();		
		String[] strings = existDatabase.listResources();
		
		for (int i = 0; i <  strings.length; i++) {
			System.out.println(strings);
		}
		
		/*
		existDatabase.createCollections();
		
		
		for (int i = 0; i <  strings.length; i++) {
			System.out.println(strings);
		}
		*/
		existDatabase.close();
	}
}