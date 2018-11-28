package it.cnr.isti.labse.xcreate.sql;

import it.cnr.isti.labse.xcreate.policyAnalyzer.ChildrenSet;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Condition;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Function;
import it.cnr.isti.labse.xcreate.policyAnalyzer.LogicaPolicyAnalyzer;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Nodo;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Tupla;
import it.cnr.isti.labse.xcreate.policyAnalyzer.TuplaSet;
import it.cnr.isti.labse.xcreate.xQuery.ElementsName;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
/*
 * TODO 
 * Creare una hashTable <idNodo,pkNodo>
	recuperare PK_TipoTupla
	Navigare gli alberi per estrarre le tuple...
	popolare le tuple …. una tupla appartiene ad un nodo …

	Navigare un albero …
	per ogni nodo .. identificare i suoi discendenti … popolare la tabela descendente-antenato..

	--- per ogni nodo recuperare le tuple che appartengono al suo sottoalbero...
	generare le combinazioni  
	memorizzare le combinazioni 

 */

public class InsertNodiTupleSQL {
	private Connection mySQLConnection;
	private LogicaPolicyAnalyzer policyAnalyzer;
//	private String policyName;
	private Nodo subTree;
	private Nodo resTree;
	private Nodo actTree;
	private Nodo envTree;
	private int pkPolicy;
	private int count = 0;
	private Hashtable<String, Integer> tipiNodo;
	
	private int pkRoot;

	public InsertNodiTupleSQL(Connection mySqlConnection2, LogicaPolicyAnalyzer policyAnalyzer, int pkPolicy2) {
		// TODO Auto-generated constructor stub
		this.policyAnalyzer = policyAnalyzer;
		this.pkPolicy = pkPolicy2;
		this.mySQLConnection = mySqlConnection2;
	}

	public void execute(){
		this.subTree = this.policyAnalyzer.getSubjectSet();
		this.resTree = this.policyAnalyzer.getResourceSet();
		this.actTree = this.policyAnalyzer.getActionSet();
		this.envTree = this.policyAnalyzer.getEnvironmentSet();
		//this.pkPolicy = insertPolicy(this.policyName);
		
		insertNodes();
		insertTuple();
		
		/// ANCRORA DA DEFINIRE 
		/**
		 * FIXME 
		 * ******************************************************************
		 * E' QUI CHE ANDREBBERO INSERITE LE NUOVE COMBINAZIONI 
		 * 
		 * PER IL MOMENTO SONO COMMENTATTE IN  MODO DA PERMETTERE L'INSERIMENTO DEI NODI E DELLE TUPLE NEL DATABASE
		 * 
		 * ******************************************************************
		 * ORA VA BENE...BY ARGENE :)
		 */
		// INIZIO COMMENTO
		InsertCombinationsSQL insertCombinationsSQL = new InsertCombinationsSQL(this.mySQLConnection, this.pkRoot);
		insertCombinationsSQL.insertCombinations();
		// FINE COMMENTO
		
	}

	public void insertNodes(){
		try {
			ResultSet result;
			String querySQL;
			Statement stmt;
	//		int pkTipo;
			this.tipiNodo = new Hashtable<String, Integer>();
			
			this.tipiNodo = SelectTipi.getTipiNodo();
			/*
			querySQL = "SELECT * FROM TipiNodo;";
			stmt = this.mySQLConnection.createStatement();
			result = stmt.executeQuery(querySQL);
			while (result.next()) {
				pkTipo = result.getInt("PK_TipoNodo");
				String nomeTipo = result.getString("NomeTipoNodo");
				//System.out.println("PK_TipoNodo : "+pkTipo+" ___ NomeTipoNodo : "+nomeTipo);
				this.tipiNodo.put(nomeTipo, new Integer(pkTipo));
			}
			*/
			/*
			 * insert in mysql-db il nodo radice
			 * 
			 */
			int pkTipoNodo = this.tipiNodo.get(this.subTree.getNodoName().replace("Target", ""));
			int idNodo = Integer.parseInt(this.subTree.getMyId());
			
			String idXacml = this.subTree.getIdXACML();
			//System.out.println(" Nodo Radice ::: idXacml : "+idXacml);
			
			querySQL = "INSERT INTO "+TableAttributeNamesCons.NODI+"(IdXacml, IdNodo, FK_Politica, FK_TipoNodo) VALUES ("+"\""+idXacml+"\","+idNodo+","+this.pkPolicy+","+pkTipoNodo+");";
			stmt = this.mySQLConnection.createStatement();
			stmt.execute(querySQL);
			/*
			 * RECUPERARE LA SUA CHIAVE PRIMARIA
			 */
			result = stmt.executeQuery("SELECT MAX(PK_Nodo) AS PK_Root FROM "+TableAttributeNamesCons.NODI+" ;");
			//System.out.println("QUERY ESEGUITA : "+result.hashCode());
			
			while (result.next()) {
				this.pkRoot = result.getInt("PK_Root");
				//System.out.println("pkNodoInsert  = "+pkNodoInsert);
				this.subTree.setPkNodo(this.pkRoot);
			}
			stmt.close();
			// inserire 
			insertOtherNodes(this.subTree);

			//System.out.println(" INIZIO : prova di derivazione chiusura dei Nodi per l'associazione Antenato discendente");
			// prova di derivazione chiusura dei Nodi per l'associazione Antenato discendente
			//AntenatoDiscendente ad = new AntenatoDiscendente(this.subTree);
			//ad.make();
			//ad.print();
			querySQL = "INSERT INTO "+TableAttributeNamesCons.DISCENDENTE_ANTENATO+"(FK_Antenato, FK_Discendente) VALUES (?,?);";
			PreparedStatement preStmt = this.mySQLConnection.prepareStatement(querySQL);
			Hashtable<Integer, Vector<Integer>> antDisc = AntenatoDiscendente.make(this.subTree);
			Enumeration<Integer> pkAntenato = antDisc.keys();
			while (pkAntenato.hasMoreElements()) {
				Integer pkAnt = (Integer) pkAntenato.nextElement();
				Vector<Integer> pkDiscendenti = antDisc.get(pkAnt);
				for (Integer pkDis : pkDiscendenti) {
					preStmt.setInt(1, pkAnt.intValue());
					preStmt.setInt(2, pkDis.intValue());
					preStmt.addBatch();
				}
			}
			preStmt.executeBatch();
			preStmt.close();
			//System.out.println(" FINE : prova di derivazione chiusura dei Nodi per l'associazione Antenato discendente");

			this.mySQLConnection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//////////////////////////////////////
	public void insertOtherNodes(Nodo node){
		try {
			ResultSet result;
			String querySQL;
			Statement stmt;
			ChildrenSet childrenSet;
			// get children
			childrenSet = node.getChildrenSet();
			//System.out.println(this.count);
			this.count++;
			// insert tuple...
			//node.printTuplaSet();
			/*
			System.out.println("Presenza di : "+node.getTuplaSet().getSize()+" Tuple");
			System.out.println("Pk_Nodo : "+node.getPkNodo());
			System.out.println("FK_Padre : "+node.getFkPadre());
			 */
			/*
			 *  se il presente nodo ha figlio condition :: 
			 *  il che significa che siamo in presenza di un nodo Rule 
			 */
			if(node.hasCondition()){
				Condition condition = node.getCondition(); 
				condition.setFkPadre(node.getPkNodo());
				//System.out.println("SQL condition nome nodo : = "+condition.getNomeNodo());
				Function function = condition.getChild();
				int idNodo = Integer.parseInt(condition.getMyId());
				int pkTipoNodo = this.tipiNodo.get(condition.getNomeNodo());
				querySQL = "INSERT INTO "+TableAttributeNamesCons.NODI+"(IdNodo, FK_Politica, FK_Padre, FK_TipoNodo) VALUES ("+idNodo+","+this.pkPolicy+","+node.getPkNodo()+","+pkTipoNodo+");";
				
				System.out.println(querySQL);
				
				
				stmt = this.mySQLConnection.createStatement();
				stmt.execute(querySQL);
				/*
				 * INSERIRE IL NODO FIGLIO NEL DB
				 * RECUPERARE LA SUA CHIAVE PRIMARIA
				 */
				result = stmt.executeQuery("SELECT MAX(PK_Nodo) AS p FROM "+TableAttributeNamesCons.NODI+" ;");
				while (result.next()) {
					int pkNodoInsert = result.getInt("p");
					condition.setPkNodo(pkNodoInsert);
				}
				// inserire il nodo function
				insertFunctionNode(function, condition.getPkNodo());	
			}
			for(Enumeration<String> enumerationId = childrenSet.allChildrenId(); enumerationId.hasMoreElements();){
				Nodo child = node.getChild(enumerationId.nextElement());
				child.setFkPadre(node.getPkNodo());
				int pkTipoNodo = this.tipiNodo.get(child.getNodoName().replace("Target", ""));
				int idNodo = Integer.parseInt(child.getMyId());
				String idXacml = child.getIdXACML();
				//System.out.println(" Nodo Interno : "+ child.getNodoName() +" :: idXacml : "+idXacml);
				
				querySQL = "INSERT INTO "+TableAttributeNamesCons.NODI+"(IdXacml, IdNodo, FK_Politica, FK_Padre, FK_TipoNodo) VALUES ("+"\""+idXacml+"\","+idNodo+","+this.pkPolicy+","+node.getPkNodo()+","+pkTipoNodo+");";
				stmt = this.mySQLConnection.createStatement();
				stmt.execute(querySQL);
				/*
				 * INSERIRE IL NODO FIGLIO NEL DB
				 * RECUPERARE LA SUA CHIAVE PRIMARIA
				 */
				result = stmt.executeQuery("SELECT MAX(PK_Nodo) AS p FROM "+TableAttributeNamesCons.NODI+" ;");
				//	System.out.println("QUERY ESEGUITA : "+result.hashCode());
				while (result.next()) {
					int pkNodoInsert = result.getInt("p");
					//	System.out.println("pkNodoInsert  = "+pkNodoInsert);
					child.setPkNodo(pkNodoInsert);
				}
				// inserire i nipoti
				insertOtherNodes(child);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * INSERIRE IL NODO FUNCTION
	 */
	public void insertFunctionNode(Function function, int fkPadre){
		try {
			Object obj;
			Vector<Object> arg = function.getArgomenti();
			int idNodo = Integer.parseInt(function.getMyId());
			String fun = function.getFunction();
			int pkTipoNodo = this.tipiNodo.get(function.getNomeNodo());
			String querySQL = "INSERT INTO "+TableAttributeNamesCons.NODI+"(IdNodo, Function, FK_Politica, FK_Padre, FK_TipoNodo) VALUES ("+idNodo+",\""+fun+"\","+this.pkPolicy+","+fkPadre+","+pkTipoNodo+");";
			Statement stmt;
			stmt = this.mySQLConnection.createStatement();
			stmt.execute(querySQL);
			/*
			 * INSERIRE IL NODO FIGLIO NEL DB
			 * RECUPERARE LA SUA CHIAVE PRIMARIA
			 */
			ResultSet result = stmt.executeQuery("SELECT MAX(PK_Nodo) AS p FROM "+TableAttributeNamesCons.NODI+" ;");
			//System.out.println("QUERY ESEGUITA : "+result.hashCode());
			while (result.next()) {
				int pkNodoInsert = result.getInt("p");
				function.setPkNodo(pkNodoInsert);
			}
			// INSERIRE I FIGLI FUNCTION NEL DB
			for(int i = 0; i < arg.size(); i++){
				obj = arg.get(i);
				if(obj instanceof Function){
					insertFunctionNode((Function)obj,function.getPkNodo() );
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * inserire le tuple nel database 
	 */
	public void insertTuple(){
		try {
			// creare una corrispondenza tra idNodo e pkNodo
			//Enumeration<String> keys;
			ResultSet result;
			String querySQL;
			Statement stmt;
			Hashtable<String, Integer> tipiTupla = new Hashtable<String, Integer>();
			Hashtable<String, Integer> idPkAsHashTable = this.subTree.getIdPkAsHashTable();
			// PER I TIPI DELLE TUPLE :: RECUPERARE LE CHIAVI PRIMARIE
			querySQL = "SELECT PK_TipoTupla, NomeTipoTupla FROM "+TableAttributeNamesCons.TIPI_TUPLA+"; ";
			stmt = this.mySQLConnection.createStatement();
			result = stmt.executeQuery(querySQL);
			while (result.next()) {
				Integer pkTT = new Integer(result.getInt("PK_TipoTupla"));
				String tTString = result.getString("NomeTipoTupla");
				tipiTupla.put(tTString, pkTT);
			}
			//keys = tipiTupla.keys();
			/*
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				System.out.println("pkTT = "+tipiTupla.get(key)+" value = "+key);
			}
			*/
			// inserire le tuple di tipo subject
			insertSubTuple(tipiTupla.get(String.valueOf(ElementsName.Subject)), idPkAsHashTable);
			// inserire le tuple di tipo Resource
			insertResTuple(tipiTupla.get(String.valueOf(ElementsName.Resource)), idPkAsHashTable);
			// inserire le tuple di tipo Action
			insertActTuple(tipiTupla.get(String.valueOf(ElementsName.Action)), idPkAsHashTable);
			// inserire le tuple di tipo Environment
			insertEnvTuple(tipiTupla.get(String.valueOf(ElementsName.Environment)), idPkAsHashTable);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertSubTuple(int pkTipoTupla, Hashtable<String, Integer> idPkAsHashTable){
		try {
			/*
			 *  CORRISPONDENZA TRA ID NELL'ALBERO E LA CHIAVE PRIMARIA 
			 *  ASSEGNATA DAL DB NELLA FASE I INSERIMENTO DEI NODI
			 */
			Hashtable<String, Integer> idPkAsHash = idPkAsHashTable;
			/*
			 * CORRISPONDENZA TRA ID DEL NODO 
			 */
			ResultSet result;
			String querySQL;
			Statement stmt;
			Hashtable<String, Object> idNodoAsHash = this.subTree.getIdNodoAsHash();
			//System.out.println("DIMENSIONE : "+idNodoNodoAsHash.size());
			Enumeration<String> keys = idNodoAsHash.keys();
			while (keys.hasMoreElements()) {
				int pkNodo;
				String key = (String) keys.nextElement();
				Object obj = idNodoAsHash.get(key);
				pkNodo = idPkAsHash.get(key).intValue();
				TuplaSet tuplaSet = new TuplaSet();
				if(obj instanceof Nodo){
					//System.out.println("Nodo : "+((Nodo)obj).getMyId());
					tuplaSet =  ((Nodo)obj).getTuplaSet();
					//System.out.println("TuplaSetSize : "+tuplaSet.getSize());
					//System.out.println("INSERT TUPLA SQL ::: NOME NODO = "+((Nodo)obj).getNodoName());
				}
				if(obj instanceof Condition){
					//System.out.println("Condition : "+((Condition)obj).getMyId());
				}
				if(obj instanceof Function){
					//System.out.println("Function : "+((Function)obj).getMyId());
					Vector<Object> args = ((Function)obj).getArgomenti();

					for (Object arg : args) {
						if (arg instanceof TuplaSet){
							tuplaSet = (TuplaSet)arg;
							//System.out.println("TuplaSetSize : "+tuplaSet.getSize());
						}
					}
				}
				if(!tuplaSet.isEmpty()){
					// Inserimento delle tuple nel db :::
					//System.out.println("Dimensione tupla set : "+tuplaSet.getSize());
					stmt = this.mySQLConnection.createStatement();
					for (Iterator<Tupla> iterator = tuplaSet.iterator(); iterator.hasNext();) {
						Tupla tupla =  iterator.next();
						querySQL = "INSERT INTO "+TableAttributeNamesCons.TUPLE+"(" +
						"IdTupla, AttributeValue, AttributeId, DataType, Issuer, SubjectCategory, FK_TipoTupla, FK_Nodo)" +
						" VALUES ("+tupla.getTuplaId()+",\""+makeAttributeValue(tupla.getAttributeValue(),pkNodo)+"\",\""+tupla.getAttributeId()+"\",\""+tupla.getDataType()+"\",\""+tupla.getIssuer()+"\",\""+tupla.getSubjectCategory()+"\","+pkTipoTupla+","+pkNodo+");";
						//						System.out.println(querySQL);
						stmt.execute(querySQL);
						this.mySQLConnection.commit();
						result = stmt.executeQuery("SELECT MAX(PK_Tupla) AS pkTupla FROM "+TableAttributeNamesCons.TUPLE+";");
						while (result.next()) {
							int pkTuplaInsert = result.getInt("pkTupla");
							//System.out.println("pkNodoInsert  = "+pkNodoInsert);
							tupla.setPkTupla(pkTuplaInsert);
							//System.out.println("Tupla :: pk = "+pkTuplaInsert+" id = "+tupla.getTuplaId());
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void insertResTuple(int pkTipoTupla, Hashtable<String, Integer> idPkAsHashTable){
		try {
			ResultSet result;
			String querySQL;
			Statement stmt;
			Hashtable<String, Integer> idPkAsHash = idPkAsHashTable;
			Hashtable<String, Object> idNodoAsHash = this.resTree.getIdNodoAsHash();
			//System.out.println("DIMENSIONE : "+idNodoNodoAsHash.size());
			Enumeration<String> keys = idNodoAsHash.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				Object obj = idNodoAsHash.get(key);
				int pkNodo = idPkAsHash.get(key).intValue();
				TuplaSet tuplaSet = new TuplaSet();
				if(obj instanceof Nodo){
					//System.out.println("Nodo : "+((Nodo)obj).getMyId());
					tuplaSet =  ((Nodo)obj).getTuplaSet();
				}
				if(obj instanceof Condition){
					//System.out.println("Condition : "+((Condition)obj).getMyId());
				}
				if(obj instanceof Function){
					//System.out.println("Function : "+((Function)obj).getMyId());
					Vector<Object> args = ((Function)obj).getArgomenti();
					for (Object arg : args) {
						if (arg instanceof TuplaSet)
							tuplaSet = (TuplaSet)arg;
					}
				}
				if(!tuplaSet.isEmpty()){
					// Inserimento delle tuple nel db :::
					//System.out.println("Dimensione tupla set : "+tuplaSet.getSize());
					stmt = this.mySQLConnection.createStatement();
					for (Iterator<Tupla> iterator = tuplaSet.iterator(); iterator.hasNext();) {
						Tupla tupla =  iterator.next();
						querySQL = "INSERT INTO "+TableAttributeNamesCons.TUPLE+"(" +
						"IdTupla, AttributeValue, AttributeId, DataType, Issuer, FK_TipoTupla, FK_Nodo)" +
						" VALUES ("+tupla.getTuplaId()+",\""+makeAttributeValue(tupla.getAttributeValue(),pkNodo)+"\",\""+tupla.getAttributeId()+"\",\""+tupla.getDataType()+"\",\""+tupla.getIssuer()+"\","+pkTipoTupla+","+pkNodo+");";
						//System.out.println(querySQL);
						stmt.execute(querySQL);
						this.mySQLConnection.commit();
						result = stmt.executeQuery("SELECT MAX(PK_Tupla) AS pkTupla FROM "+TableAttributeNamesCons.TUPLE+";");
						while (result.next()) {
							int pkTuplaInsert = result.getInt("pkTupla");
							//System.out.println("pkNodoInsert  = "+pkNodoInsert);
							tupla.setPkTupla(pkTuplaInsert);
							//System.out.println("Tupla :: pk = "+pkTuplaInsert+" id = "+tupla.getTuplaId());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertActTuple(int pkTipoTupla, Hashtable<String, Integer> idPkAsHashTable){
		try{
			ResultSet result;
			String querySQL;
			Statement stmt;
			Hashtable<String, Integer> idPkAsHash = idPkAsHashTable;
			Hashtable<String, Object> idNodoAsHash = this.actTree.getIdNodoAsHash();
			//System.out.println("DIMENSIONE : "+idNodoNodoAsHash.size());
			Enumeration<String> keys = idNodoAsHash.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				Object obj = idNodoAsHash.get(key);
				int pkNodo = idPkAsHash.get(key).intValue();
				TuplaSet tuplaSet = new TuplaSet();
				if(obj instanceof Nodo){
					//System.out.println("Nodo : "+((Nodo)obj).getMyId());
					tuplaSet =  ((Nodo)obj).getTuplaSet();
				}
				if(obj instanceof Condition){
					//System.out.println("Condition : "+((Condition)obj).getMyId());
				}
				if(obj instanceof Function){
					//System.out.println("Function : "+((Function)obj).getMyId());
					Vector<Object> args = ((Function)obj).getArgomenti();
					for (Object arg : args) {
						if (arg instanceof TuplaSet)
							tuplaSet = (TuplaSet)arg;
					}
				}
				// Inserimento delle tuple nel db :::
				if(!tuplaSet.isEmpty()){
					// Inserimento delle tuple nel db :::
					//System.out.println("Dimensione tupla set : "+tuplaSet.getSize());
					stmt = this.mySQLConnection.createStatement();
					
					for (Iterator<Tupla> iterator = tuplaSet.iterator(); iterator.hasNext();) {
						Tupla tupla =  iterator.next();
						querySQL = "INSERT INTO "+TableAttributeNamesCons.TUPLE+"(" +
						"IdTupla, AttributeValue, AttributeId, DataType, Issuer, FK_TipoTupla, FK_Nodo)" +
						" VALUES ("+tupla.getTuplaId()+",\""+makeAttributeValue(tupla.getAttributeValue(),pkNodo)+"\",\""+tupla.getAttributeId()+"\",\""+tupla.getDataType()+"\",\""+tupla.getIssuer()+"\","+pkTipoTupla+","+pkNodo+");";
						//						System.out.println(querySQL);
						stmt.execute(querySQL);
						this.mySQLConnection.commit();
						result = stmt.executeQuery("SELECT MAX(PK_Tupla) AS pkTupla FROM "+TableAttributeNamesCons.TUPLE+";");
						while (result.next()) {
							int pkTuplaInsert = result.getInt("pkTupla");
							//System.out.println("pkNodoInsert  = "+pkNodoInsert);
							tupla.setPkTupla(pkTuplaInsert);
							//System.out.println("Tupla :: pk = "+pkTuplaInsert+" id = "+tupla.getTuplaId());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void insertEnvTuple(int pkTipoTupla, Hashtable<String, Integer> idPkAsHashTable){
		try{
			String querySQL;
			Statement stmt;
			ResultSet result;

			Hashtable<String, Integer> idPkAsHash = idPkAsHashTable;
			Hashtable<String, Object> idNodoAsHash = this.envTree.getIdNodoAsHash();
			//System.out.println("DIMENSIONE : "+idNodoNodoAsHash.size());
			Enumeration<String> keys = idNodoAsHash.keys();
			while (keys.hasMoreElements()){
				String key = (String) keys.nextElement();
				Object obj = idNodoAsHash.get(key);
				int pkNodo = idPkAsHash.get(key).intValue();
				TuplaSet tuplaSet = new TuplaSet();
				if(obj instanceof Nodo){
					//System.out.println("Nodo : "+((Nodo)obj).getMyId());
					tuplaSet =  ((Nodo)obj).getTuplaSet();
				}
				if(obj instanceof Condition){
					//System.out.println("Condition : "+((Condition)obj).getMyId());
				}
				if(obj instanceof Function){
					//System.out.println("Function : "+((Function)obj).getMyId());
					Vector<Object> args = ((Function)obj).getArgomenti();
					for (Object arg : args) {
						if (arg instanceof TuplaSet)
							tuplaSet = (TuplaSet)arg;
					}
				}
				// Inserimento delle tuple nel db :::
				if(!tuplaSet.isEmpty()){
					//System.out.println("Dimensione tupla set : "+tuplaSet.getSize());
					stmt = this.mySQLConnection.createStatement();
					
					for (Iterator<Tupla> iterator = tuplaSet.iterator(); iterator.hasNext();) {
						Tupla tupla =  iterator.next();
						querySQL = "INSERT INTO "+TableAttributeNamesCons.TUPLE+"(" +
						"IdTupla, AttributeValue, AttributeId, DataType, Issuer, FK_TipoTupla, FK_Nodo)" +
						" VALUES ("+tupla.getTuplaId()+",\""+makeAttributeValue(tupla.getAttributeValue(),pkNodo)+"\",\""+tupla.getAttributeId()+"\",\""+tupla.getDataType()+"\",\""+tupla.getIssuer()+"\","+pkTipoTupla+","+pkNodo+");";
						//System.out.println(querySQL);
						stmt.execute(querySQL);
						this.mySQLConnection.commit();
						result = stmt.executeQuery("SELECT MAX(PK_Tupla) AS pkTupla FROM "+TableAttributeNamesCons.TUPLE+";");
						while (result.next()) {
							int pkTuplaInsert = result.getInt("pkTupla");
							//System.out.println("pkNodoInsert  = "+pkNodoInsert);
							tupla.setPkTupla(pkTuplaInsert);
							//System.out.println("Tupla :: pk = "+pkTuplaInsert+" id = "+tupla.getTuplaId()+"    "+tupla.getPkTupla());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String makeAttributeValue(String oldAttributeValue, int pkNodo){
		if(oldAttributeValue.equals(String.valueOf("caso2"))
				|| oldAttributeValue.equals(String.valueOf("caso6"))
				|| oldAttributeValue.equals(String.valueOf("caso7")))
			return String.valueOf("RandomValue_").concat(oldAttributeValue.concat("_"+String.valueOf(pkNodo)));
		return oldAttributeValue;
	}
	
	/////////////////////////////////////////////////
}
