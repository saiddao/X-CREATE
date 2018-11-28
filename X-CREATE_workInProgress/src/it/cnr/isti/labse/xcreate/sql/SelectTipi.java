package it.cnr.isti.labse.xcreate.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

public class SelectTipi {
	private static Hashtable<String, Integer> tipiNodo;
	private static Hashtable<String, Integer> tipiTupla;
	private static Hashtable<String, Integer> tipiCombinazione;
	private static Connection mySqlConnection;
	private static Statement stmt;
	private static Hashtable<Integer, String> tipiTuplaKeyInteger;
	
	public static Hashtable<Integer, String> getTitpiTuplaKeyInteger() {
		return tipiTuplaKeyInteger;
	}
	private static void setTitpiTuplaKeyInteger(
			Hashtable<Integer, String> titpiTuplaKey) {
		tipiTuplaKeyInteger = titpiTuplaKey;
	}
	public static void init(Connection connection){
		mySqlConnection = connection;
		setTipiNodo(new Hashtable<String, Integer>());
		setTipiTupla(new Hashtable<String, Integer>());
		setTipiCombinazione(new Hashtable<String, Integer>());
		setTitpiTuplaKeyInteger(new Hashtable<Integer, String>());
		execute();
	}
	public SelectTipi(Connection connection) {
		// TODO Auto-generated constructor stub
		mySqlConnection = connection;
		setTipiNodo(new Hashtable<String, Integer>());
		setTipiTupla(new Hashtable<String, Integer>());
		setTipiCombinazione(new Hashtable<String, Integer>());
	}
	private static void execute(){
		selectTipiNodo();
		selectTipiTupla();
		selectTipiCombinazioni();
	}
	private static void selectTipiCombinazioni() {
		// TODO Auto-generated method stub
		try {
			//System.out.println("########################################### TIPICOMBINAZIONE  ##########");
			stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(getQuerySelectTipiCombinazione());
			while (result.next()) {
				int pkTipo = result.getInt("PK_TipoCombinazione");
				String nomeTipo = result.getString("NomeTipoCombinazione");
				//System.out.println("PK_TipoNodo : "+pkTipo+" ___ NomeTipoCOMBINAZIONE : "+nomeTipo);
				tipiCombinazione.put(nomeTipo, new Integer(pkTipo));
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	private static String getQuerySelectTipiCombinazione() {
		// TODO Auto-generated method stub
		String querySQL = "SELECT * FROM "+TableAttributeNamesCons.TIPI_COMBINAZIONE+"";
		return querySQL;
	}
	private static void selectTipiTupla() {
		// TODO Auto-generated method stub
		try {
			//System.out.println("########################################### TIPITUPLA  ##########");
			stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(getQuerySelectTipiTupla());
			while (result.next()) {
				int pkTipo = result.getInt("PK_TipoTupla");
				String nomeTipo = result.getString("NomeTipoTupla");
				//System.out.println("PK_TipoNodo : "+pkTipo+" ___ NomeTipoTUPLA : "+nomeTipo);
				tipiTupla.put(nomeTipo, new Integer(pkTipo));
				tipiTuplaKeyInteger.put(new Integer(pkTipo), nomeTipo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String getQuerySelectTipiTupla() {
		// TODO Auto-generated method stub
		String querySQL = "SELECT * FROM "+TableAttributeNamesCons.TIPI_TUPLA+"";
		return querySQL;
	}
	private static void selectTipiNodo() {
		// TODO Auto-generated method stub
		try {
			//System.out.println("########################################### TIPINODO  ##########");
			stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(getQuerySelectTipiNodo());
			while (result.next()) {
				int pkTipo = result.getInt("PK_TipoNodo");
				String nomeTipo = result.getString("NomeTipoNodo");
				//System.out.println("PK_TipoNodo : "+pkTipo+" ___ NomeTipoNodo : "+nomeTipo);
				tipiNodo.put(nomeTipo, new Integer(pkTipo));
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String getQuerySelectTipiNodo() {
		// TODO Auto-generated method stub
		String querySQL = "SELECT * FROM "+TableAttributeNamesCons.TIPI_NODO+" ";
		return querySQL;
	}
	public static void setTipiNodo(Hashtable<String, Integer> tipiN) {
		tipiNodo = tipiN;
	}
	public static Hashtable<String, Integer> getTipiNodo() {
		return tipiNodo;
	}
	public static void setTipiTupla(Hashtable<String, Integer> tipiT) {
		tipiTupla = tipiT;
	}
	public static Hashtable<String, Integer> getTipiTupla() {
		return tipiTupla;
	}
	public static void setTipiCombinazione(Hashtable<String, Integer> tipiComb) {
		tipiCombinazione = tipiComb;
	}
	public static Hashtable<String, Integer> getTipiCombinazione() {
		return tipiCombinazione;
	}
}
