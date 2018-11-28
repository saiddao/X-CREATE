package nessos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class ToolChain {

	public static String cartellaPrincipale = "/home/said/PC_Nice/DATA/Said/eclipse/workspace/X-CREATE_WorkInProgress/Temp_X-CREATE/XACMLrules(1)_Mod/Multiple_Comb";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File mainDir = new File(cartellaPrincipale);
		System.out.println(mainDir.getAbsolutePath());
		File[] contentMainDir = mainDir.listFiles();
		for (int i = 0; i < contentMainDir.length; i++) {
			File subDir = contentMainDir[i];
			if (subDir.isDirectory()) {
				System.out.println(subDir.getName());
				File[] contentSubDir = subDir.listFiles();
				for (int j = 0; j < contentSubDir.length; j++) {
					File request = contentSubDir[j];
					System.out.println(request.getName());
					StringTokenizer tokenizer = new StringTokenizer(request.getName(), "_");
					tokenizer.nextToken();
					System.out.println(tokenizer.nextToken());
					
				}
			}
		}
	}

	private static void saveRequest(File requestFile, String dirName) {
		// TODO Auto-generated method stub
		try {
			File currentResultDir = new File(requestFile.getParentFile().getAbsolutePath() + File.separator + dirName);
			
			if (!currentResultDir.exists()){
				currentResultDir.mkdir();
			}
			
			FileInputStream fis = new FileInputStream(requestFile);
			FileOutputStream fos = new FileOutputStream(currentResultDir.getAbsoluteFile() + File.separator+ requestFile.getName());
			
			byte[] dati = new byte[fis.available()];
			
			fis.read(dati);
			fos.write(dati);
			
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
