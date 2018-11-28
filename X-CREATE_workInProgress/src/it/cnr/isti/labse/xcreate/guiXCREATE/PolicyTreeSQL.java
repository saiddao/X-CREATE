package it.cnr.isti.labse.xcreate.guiXCREATE;


import it.cnr.isti.labse.xcreate.sql.InsertCombinationsSQL;
import it.cnr.isti.labse.xcreate.sql.SelectSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

public class PolicyTreeSQL {
	private static Connection mySqlConnection;

	public static DefaultMutableTreeNode getRoot(Connection mySqlConnec, int pkPolitica) {
		DefaultMutableTreeNode rootPolicy = null;
		Hashtable<Integer, DefaultMutableTreeNode> jNodeHashtable = new Hashtable<Integer, DefaultMutableTreeNode>();
		int fkPolicy;
		int pkNodo;
		int fkPadre;
		String nodeName;
		String idXacml;

		try {
			System.out.println("getRoot(): Inizio");
			
			mySqlConnection = mySqlConnec;

			Statement stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(SelectSQL.getQuerySelectNodiForJTreeModel(pkPolitica));

			while(result.next()){
				fkPadre = result.getInt("FK_Padre");
				pkNodo = result.getInt("PK_Nodo");
				fkPolicy = result.getInt("FK_Politica");
				nodeName = result.getString("NomeTipoNodo");
				idXacml = result.getString("IdXacml");
				
			//	System.out.println(" [pkNode: "+pkNodo+"\n IdXacml: ..."+ ((idXacml != null)? (idXacml.length() > 10)? idXacml.substring(idXacml.length()-10):idXacml :"")+"]");
				
				JNodoInfo jNodoInfo = new JNodoInfo(fkPolicy, pkNodo, fkPadre, nodeName+" [pkNode: "+pkNodo+"\n IdXacml: ..."+ ((idXacml != null)? (idXacml.length() > 10)? idXacml.substring(idXacml.length()-10):idXacml :"")+"]");
				jNodoInfo.setRed(true);

				DefaultMutableTreeNode jNodo = new DefaultMutableTreeNode(jNodoInfo);
				
				if(jNodeHashtable.containsKey(fkPadre)){
					jNodeHashtable.get(fkPadre).add(jNodo);
				}else{
					rootPolicy = jNodo;
					System.out.println(" Nodo radice : "+rootPolicy.toString());
				}
				jNodeHashtable.put(new Integer(pkNodo), jNodo);				
				//System.out.println(jNodoInfo.toString1());
				//				System.out.println(result.getString("NomeTipoNodo"));
				//				System.out.println(result.getInt("FK_Padre"));
			}
			//System.out.println("hashtable size : "+jNodeHashtable.size());
			//System.out.println(jNodeHashtable.toString());
			//rootPolicy = jNodeHashtable.get(1);
			//System.out.println("Root : "+rootPolicy.toString());
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rootPolicy;
	}
	/* per le combinazioni multiple dal nodo radice */
	public static int countCombinationsFromRoot(Connection mySqlConnection2, int pkPolitica){
		int countAllComb = 0;
		mySqlConnection = mySqlConnection2;
		try {
			Statement stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(SelectSQL.getQuerySelectCountFromRootNodeCombinazioni(pkPolitica));
			
			while(result.next())
				countAllComb = result.getInt("Quanti");
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("COUNT ALL COMB : "+countAllComb);
		return countAllComb;
	}
	/* per le combinazioni multiple da un nodo intermedio */
	public static int countCombinationsFromANode(Connection mySqlConnection2, int pkPolitica, int pkNodo){
		int countCombs = 0;
		mySqlConnection = mySqlConnection2;
		
		try {
			/*
			 * verificare se non sono mai state generate combinazioni a partire da questo nodo 
			 * in caso affermativo generare le combinazioni 
			 * e dopo eseguire la query..
			 * 
			 */
			
			Statement stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(SelectSQL.getQuerySelectCountFromPoliticheNodiCombinazioni(pkPolitica, pkNodo));
			while(result.next()){
				countCombs = result.getInt("Quanti");
			}
			result.close();
			
			if(countCombs == 0){
				int countTuple = 0;
				System.out.println(countCombs+" :: non ci sono combinazioni generate dal nodo -> "+pkNodo+" PolicyTreeSQL");
				result = stmt.executeQuery(SelectSQL.getQuerySelectCountTupleIntoSubTree(pkNodo));
				while(result.next()){
					countTuple = result.getInt("Quanti");
				}
				result.close();
				System.out.println(countTuple+" il numero di tuple che risiedono nel sottoalbero di radice -> "+pkNodo);
				if(countTuple != 0){
					InsertCombinationsSQL insertCombinationsSQL = new InsertCombinationsSQL(mySqlConnection, pkNodo);
					insertCombinationsSQL.insertCombinations();
					
					result = stmt.executeQuery(SelectSQL.getQuerySelectCountFromPoliticheNodiCombinazioni(pkPolitica, pkNodo));
					while(result.next()){
						countCombs = result.getInt("Quanti");
					}
					result.close();
					System.out.println("il numero di combinazioni nuovo "+countCombs);
				}
			}else{
				System.out.println("Ci sono combinazioni ");
			}
				
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return countCombs;
	}
	//per le richieste semplici, dal nodo radice
	public static int countSimpleCombinationsFromRoot(Connection mySqlConnection2, int pkPolitica){
		int countAllComb = 0;
		mySqlConnection = mySqlConnection2;
		try {
			Statement stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(SelectSQL.getQuerySelectCountFromRootNodeCombSimple(pkPolitica));
			
			while(result.next()){
				countAllComb = result.getInt("Quanti");
			}
			result.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("COUNT ALL COMB : "+countAllComb);
		return countAllComb;
	}
	/* per le combinazioni simple da un nodo intermedio */
	public static int countSimpleCombinationsFromANode(Connection mySqlConnection2, int pkPolitica, int pkNodo){
		int countCombs = 0;
		mySqlConnection = mySqlConnection2;
		
		try {
			/*
			 * verificare se non sono mai state generate combinazioni a partire da questo nodo 
			 * in caso affermativo generare le combinazioni 
			 * e dopo eseguire la query..
			 * 
			 */
			
			Statement stmt = mySqlConnection.createStatement();
			ResultSet result = stmt.executeQuery(SelectSQL.getQuerySelectCountFromPoliticheNodiCombSimple(pkPolitica, pkNodo));
			while(result.next()){
				countCombs = result.getInt("Quanti");
			}
			result.close();
			
			if(countCombs == 0){
				int countTuple = 0;
				System.out.println(countCombs+" :: non ci sono combinazioni generate dal nodo -> "+pkNodo);
				result = stmt.executeQuery(SelectSQL.getQuerySelectCountTupleIntoSubTree(pkNodo));
				while(result.next()){
					countTuple = result.getInt("Quanti");
				}
				result.close();
				System.out.println(countTuple+" il numero di tuple che risiedono nel sottoalbero di radice -> "+pkNodo);
				if(countTuple != 0){
					InsertCombinationsSQL insertCombinationsSQL = new InsertCombinationsSQL(mySqlConnection, pkNodo);
					insertCombinationsSQL.insertCombinations();
					
					result = stmt.executeQuery(SelectSQL.getQuerySelectCountFromPoliticheNodiCombSimple(pkPolitica, pkNodo));
					while(result.next()){
						countCombs = result.getInt("Quanti");
					}
					result.close();
					System.out.println("il numero di combinazioni nuovo "+countCombs);
				}
			}else{
				System.out.println("Ci sono combinazioni ");
			}
				
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return countCombs;
	}
}
