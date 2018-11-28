package it.cnr.isti.labse.xcreate.sql;

import it.cnr.isti.labse.xcreate.dbDrivers.EXistDBDriver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DeletePolicyFromMySqlDB {
	private static int pkPolicy; 
	private static String policyName;
	private static Connection mySqlConnection; 
	private static EXistDBDriver eXistDBDriver;

	/**
	 * Permette di cancellare, in modo permanente dal Database, la politica 
	 * identificata dal parametro pkPolicy.
	 * 
	 * @param pkPolicy
	 * @param policyNameSelected 
	 * @param mySqlConnection
	 * @param eXistDBDriver 
	 */
	public static void deletePolicy(int pkPolicy1, String policyNameSelected, Connection mySqlConnection1, EXistDBDriver eXistDBDriver1){
		try {
			pkPolicy = pkPolicy1;
			policyName = policyNameSelected;
			mySqlConnection = mySqlConnection1;
			eXistDBDriver = eXistDBDriver1;
			Statement stmt = mySqlConnection.createStatement();
			
			stmt.execute(DeleteSQL.deleteSSTupleSubject(pkPolicy));
			stmt.execute(DeleteSQL.deleteSSTupleResource(pkPolicy));
			stmt.execute(DeleteSQL.deleteSSTupleAction(pkPolicy));
			stmt.execute(DeleteSQL.deleteSSTupleEnvironment(pkPolicy));
			stmt.execute(DeleteSQL.deleteCombinazioni(pkPolicy));			 
			stmt.execute(DeleteSQL.deleteSottoInsiemiTuple());
			stmt.execute(DeleteSQL.deleteDiscendenteAntenato(pkPolicy));
			stmt.execute(DeleteSQL.deleteTuple(pkPolicy));
			stmt.executeUpdate(DeleteSQL.UpdateFkPkadre(pkPolicy));
			stmt.execute(DeleteSQL.deleteNodi(pkPolicy));
			stmt.execute(DeleteSQL.deletePolicy(pkPolicy));
			
			eXistDBDriver.deleteResource(policyName);
			stmt.close();
			mySqlConnection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
