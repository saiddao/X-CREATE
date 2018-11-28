package it.cnr.isti.labse.xcreate.xSDResources;

import java.io.File;

public class XSDUtil {
	public static final String CONTEXT_SCHEMA_FILE_NAME = "access_control-xacml-2.0-context-schema-os.xsd";
	public static final String POLICY_SCHEMA_FILE_NAME = "access_control-xacml-2.0-policy-schema-os.xsd";
	private static String schemaLocation;
	
	public static String getContextSchemaLocation(){
		return getAbsolutePath(new File("."), CONTEXT_SCHEMA_FILE_NAME);
	}
	public static String getPolicySchemaLocation(){
		return getAbsolutePath(new File("."), POLICY_SCHEMA_FILE_NAME);
	}
	public static String getAbsolutePath (File dir, String fileName){
		File[] entries = dir.listFiles ();
		
		if (entries != null) {
			for (int i = 0; i < entries.length; i++){
				if (entries[i].isDirectory ())
					getAbsolutePath (entries[i], fileName);
				else
					if(entries[i].getName().equalsIgnoreCase(String.valueOf(fileName)))
						schemaLocation = entries[i].getAbsolutePath();
			}
		}
		return schemaLocation;
	}
}
