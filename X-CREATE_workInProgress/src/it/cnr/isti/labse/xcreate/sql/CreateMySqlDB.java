package it.cnr.isti.labse.xcreate.sql;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMySqlDB {
	private Connection connectionMySQL;
	private int PK_Policy;

	public CreateMySqlDB() {
	}
	public Connection connect(){
		try {
			Class.forName(MySQLCons.DRIVER_MYSQL);
			System.out.println(MySQLCons.URL_MYSQL+" : "+ MySQLCons.LOGIN_MYSQL+" : "+  MySQLCons.PASSWORD_MYSQL);
			
			this.connectionMySQL = DriverManager.getConnection(MySQLCons.URL_MYSQL, MySQLCons.LOGIN_MYSQL, MySQLCons.PASSWORD_MYSQL);
			this.connectionMySQL.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			System.out.println("Driver non trovato");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Connessione non riuscita");
			e.printStackTrace();
		}
		return this.connectionMySQL;
	}

	public int insertPolicy(String name, 
							int minSub, int maxSub, 
							int minRes, int maxRes, 
							int minAct, int maxAct, 
							int minEnv, int maxEnv) {
		try {
			ResultSet result;
			String querySQL;
			Statement stmt;
			querySQL = String.valueOf(
					"INSERT INTO "+TableAttributeNamesCons.POLITICHE+" ("+
					TableAttributeNamesCons.NOME_POLITICA+", "+
					TableAttributeNamesCons.MIN_SUB+", "+
					TableAttributeNamesCons.MAX_SUB+", "+
					TableAttributeNamesCons.MIN_RES+", "+
					TableAttributeNamesCons.MAX_RES+", "+
					TableAttributeNamesCons.MIN_ACT+", "+
					TableAttributeNamesCons.MAX_ACT+", "+
					TableAttributeNamesCons.MIN_ENV+", "+
					TableAttributeNamesCons.MAX_ENV+") " +
					"VALUE (\""+name+"\","+minSub+","+maxSub+","+minRes+","+maxRes+","+minAct+","+maxAct+","+minEnv+","+maxEnv+");"
					);
			System.out.println(querySQL);
			this.connectionMySQL.createStatement().execute(querySQL);
			stmt = this.connectionMySQL.createStatement();
			result = stmt.executeQuery("SELECT * FROM "+TableAttributeNamesCons.POLITICHE+" WHERE NomePolitica = \""+name+"\";");
			while (result.next()) {
				this.PK_Policy = result.getInt("PK_Politica");
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.PK_Policy;
	}

	public void initialize() {
		/**
		 * CONTROLLARE SE LE TABELLE SONO SPRESENTI NEL DB
		 * SE NON CI SONO ANDREBBERO CREATE..
		 */
		if(!thereAreTablesNew()){
			System.out.println(" le tabelle non ci sono :::: ");
			// elimina le eventuali tabelle presenti
			dropTablesNew();
			// crea le tabelle 
			createTablesNew();
			// crea le associazioni tra le tabelle
			alterTablesNew();
			// popola le tabelle dei tipi
			initializeTypesTables();
		}
		/*********************************************************
		 * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * * FIXME 
		 * 
		 * si ricrea il database
		 * DA CANCELLARE QUANDO IL PRODOTTO E' PRONTO 
		 *  ****************************************************
		 */
//		// elimina le eventuali tabelle presenti
//		dropTablesNew();
//		// crea le tabelle 
//		createTablesNew();
//		// crea le associazioni tra le tabelle
//		alterTablesNew();
//		// popola le tabelle dei tipi
//		initializeTypesTables();
		/********************************************
		 * RICORDARSI DI CANCELLARE 
		 * *****************************************
		 */
		
		
	}
	private boolean thereAreTablesNew() {
		boolean exist = true;
		int countTables = 0;
		System.out.println("LE TABELLE NEL DATABASE ");
		try {
			
			
			StringBuilder querySQL = new StringBuilder();
			querySQL.append("SELECT table_name ");
			querySQL.append("FROM information_schema.tables; ");
//			querySQL.append("WHERE table_schema = '"+MySQLCons.DATABASE_NAME+"';");
			
			System.out.println(this.connectionMySQL.isClosed());
			
			Statement stmt = this.connectionMySQL.createStatement();
			ResultSet result = stmt.executeQuery(querySQL.toString());
			String tableName;
			while (result.next()) {
				tableName = result.getString("table_name"); 
				if(tableName.toLowerCase().equals(TableAttributeNamesCons.COMBINAZIONI.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TUPLE.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TIPI_TUPLA.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TIPI_COMBINAZIONE.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.DISCENDENTE_ANTENATO.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.NODI.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TIPI_NODO.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.POLITICHE.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.SOTTOINSIEMI_TUPLE.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION.toLowerCase())
					|| tableName.toLowerCase().equals(TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT.toLowerCase()) ){
					countTables++;
					System.out.println(tableName);
				}
			}
			stmt.close();
			if(countTables != 13)
				exist = false;
			else
				System.out.println("Le tabelle esistono");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(exist + " : countTables : "+countTables);
		return exist;
	}
	
	/*
	 * inizializza le tabelle dei tipi :
	 * -- titpiTupla
	 * -- TipiCombinazione
	 * -- TipiNodo
	 */
	private void initializeTypesTables() {
		try {
			String querySQL;
			Statement stmt;
			// popola la tabelle dei tipi TipiTupla 
			stmt = this.connectionMySQL.createStatement();
			querySQL = String.valueOf("INSERT INTO "+TableAttributeNamesCons.TIPI_TUPLA+"" +
					" ("+TableAttributeNamesCons.NOME_TIPO_TUPLA+") VALUES (\"Subject\"),(\"Resource\"),(\"Action\"),(\"Environment\");");
			stmt.execute(querySQL);
			// popola la tabelle dei tipi TipiCombinazione
			querySQL = String.valueOf("INSERT INTO "+TableAttributeNamesCons.TIPI_COMBINAZIONE+"" +
					" ("+TableAttributeNamesCons.NOME_TIPO_COMBINAZIONE+") VALUES (\"1-Wise\"),(\"2-Wise\"),(\"3-Wise\"),(\"4-Wise\");");
			stmt.execute(querySQL);
			// popola la tabella dei tipi TipiNodo
			querySQL = String.valueOf("INSERT INTO "+TableAttributeNamesCons.TIPI_NODO+" " +
					"("+TableAttributeNamesCons.NOME_TIPO_NODO+") VALUES (\"PolicySet\"),(\"Policy\"),(\"Rule\"),(\"Condition\"),(\"Apply\"),(\"Function\");");
			stmt.execute(querySQL);

			stmt.close();
			this.connectionMySQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void dropTablesNew(){
		try {
			String querySQL;
			Statement stmt;
			// DROP
			stmt = this.connectionMySQL.createStatement();
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTupleSSTupleSubject;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTupleSSTupleResource;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTupleSSTupleAction;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTupleSSTupleEnvironment;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateCombinazioni;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateSottoInsiemiTuple;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateDiscendenteAntenato;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTuple;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateNodi;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreatePolitiche;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTipiTupla;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTipiCombinazione;");
			stmt.execute(querySQL);
			querySQL = String.valueOf("DROP TABLE IF EXISTS XcreateTipiNodo;");
			stmt.execute(querySQL);
			stmt.close();
			this.connectionMySQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	public void createTablesNew(){
		try {
			String querySQL;
			Statement stmt;
			// CREATE
			stmt = this.connectionMySQL.createStatement();
			querySQL = String.valueOf("CREATE TABLE XcreatePolitiche(" +
					"NomePolitica VARCHAR(40)," +
					"MinSub INT(10)," +
					"MaxSub INT(10)," +
					"MinRes INT(10)," +
					"MaxRes INT(10)," +
					"MinAct INT(10)," +
					"MaxAct INT(10)," +
					"MinEnv INT(10)," +
					"MaxEnv INT(10)," +
					"Testo TEXT,PK_Politica INT(10) NOT NULL AUTO_INCREMENT," +
					"PRIMARY KEY (PK_Politica) );");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateTipiTupla(" +
					"NomeTipoTupla VARCHAR(20)," +
					"PK_TipoTupla INT(10) NOT NULL AUTO_INCREMENT," +
					"PRIMARY KEY (PK_TipoTupla) );");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateTipiNodo(" +
					"NomeTipoNodo VARCHAR(20)," +
					"PK_TipoNodo INT(10) NOT NULL AUTO_INCREMENT," +
					"PRIMARY KEY (PK_TipoNodo) );");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateTipiCombinazione(" +
					"NomeTipoCombinazione VARCHAR(20)," +
					"PK_TipoCombinazione INT(10) NOT NULL AUTO_INCREMENT," +
					"PRIMARY KEY (PK_TipoCombinazione) );");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateNodi(" +
					"IdXacml VARCHAR (100)," +
					"IdNodo VARCHAR(10)," +
					"Function VARCHAR(80)," +
					"PK_Nodo INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_Politica INT(10)," +
					"FK_Padre INT(10)," +
					"FK_TipoNodo INT(10)," +
					"PRIMARY KEY (PK_Nodo) );");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateDiscendenteAntenato(" +
					"FK_Discendente INT(10)," +
					"FK_Antenato INT(10)," +
					"PK_DiscendenteAntenato INT (10) AUTO_INCREMENT," +
					"PRIMARY KEY (PK_DiscendenteAntenato));");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateTuple(" +
					"IdTupla VARCHAR (10)," +
					"AttributeValue VARCHAR(500)," +
					"AttributeId VARCHAR(100)," +
					"DataType VARCHAR(100)," +
					"Issuer VARCHAR(100)," +
					"SubjectCategory VARCHAR(100)," +
					"PK_Tupla INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_TipoTupla INT(10)," +
					"FK_Nodo INT(10)," +
					"PRIMARY KEY (PK_Tupla));");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateCombinazioni(" +
					"ValoreCombinazione VARCHAR(500)," +
					"PK_Combinazione INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_TipoCombinazione INT(10)," +
					"FK_Nodo INT(10)," +
					"FK_SubSetSub INT(10)," +
					"FK_SubSetRes INT(10)," +
					"FK_SubSetAct INT(10)," +
					"FK_SubSetEnv INT(10)," +
					"PRIMARY KEY (PK_Combinazione) );");
			stmt.execute(querySQL);

			querySQL = String.valueOf("CREATE TABLE XcreateSottoInsiemiTuple(" +
					"ValoreSottoInsieme VARCHAR(1000)," +
					"PK_SottoInsieme INT(10) NOT NULL AUTO_INCREMENT," +
					"PRIMARY KEY (PK_SottoInsieme) );");
			stmt.execute(querySQL);
			
			querySQL = String.valueOf("CREATE TABLE XcreateTupleSSTupleSubject(" +
					"PK_TupleSSTupleSubject INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_SottoInsieme INT(10)," +
					"FK_TuplaSub INT(10)," +
					"PRIMARY KEY (PK_TupleSSTupleSubject) );");
			stmt.execute(querySQL);
			
			querySQL = String.valueOf("CREATE TABLE XcreateTupleSSTupleResource(" +
					"PK_TupleSSTupleResource INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_SottoInsieme INT(10)," +
					"FK_TuplaRes INT(10)," +
					"PRIMARY KEY (PK_TupleSSTupleResource) );");
			stmt.execute(querySQL);
			
			querySQL = String.valueOf("CREATE TABLE XcreateTupleSSTupleAction(" +
					"PK_TupleSSTupleAction INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_SottoInsieme INT(10)," +
					"FK_TuplaAct INT(10)," +
					"PRIMARY KEY (PK_TupleSSTupleAction) );");
			stmt.execute(querySQL);
			
			querySQL = String.valueOf("CREATE TABLE XcreateTupleSSTupleEnvironment(" +
					"PK_TupleSSTupleEnvironment INT(10) NOT NULL AUTO_INCREMENT," +
					"FK_SottoInsieme INT(10)," +
					"FK_TuplaEnv INT(10)," +
					"PRIMARY KEY (PK_TupleSSTupleEnvironment) );");
			stmt.execute(querySQL);

			stmt.close();
			this.connectionMySQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void alterTablesNew() {
		try {
			String querySQL;
			Statement stmt;
			stmt = this.connectionMySQL.createStatement();

			// ALTER .. LE ASSOCIAZIONI
			querySQL = String.valueOf("ALTER TABLE XcreateNodi ADD FOREIGN KEY (FK_TipoNodo) REFERENCES XcreateTipiNodo(PK_TipoNodo);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateNodi ADD FOREIGN KEY (FK_Politica) REFERENCES XcreatePolitiche(PK_Politica);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateNodi ADD FOREIGN KEY (FK_Padre) REFERENCES XcreateNodi(PK_Nodo);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateDiscendenteAntenato ADD FOREIGN KEY (FK_Discendente) REFERENCES XcreateNodi(PK_Nodo);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateDiscendenteAntenato ADD FOREIGN KEY (FK_Antenato) REFERENCES XcreateNodi(PK_Nodo);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateTuple ADD FOREIGN KEY (FK_TipoTupla) REFERENCES XcreateTipiTupla(PK_TipoTupla);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateTuple ADD FOREIGN KEY (FK_Nodo) REFERENCES XcreateNodi(PK_Nodo);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateCombinazioni ADD FOREIGN KEY (FK_TipoCombinazione) REFERENCES XcreateTipiCombinazione(PK_TipoCombinazione);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateCombinazioni ADD FOREIGN KEY (FK_Nodo) REFERENCES XcreateNodi(PK_Nodo);");
			stmt.execute(querySQL);

			querySQL = String.valueOf("ALTER TABLE XcreateCombinazioni ADD FOREIGN KEY (FK_SubSetSub) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateCombinazioni ADD FOREIGN KEY (FK_SubSetRes) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateCombinazioni ADD FOREIGN KEY (FK_SubSetAct) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateCombinazioni ADD FOREIGN KEY (FK_SubSetEnv) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);

			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleSubject ADD FOREIGN KEY (FK_SottoInsieme) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleSubject ADD FOREIGN KEY (FK_TuplaSub) REFERENCES XcreateTuple(PK_Tupla);");
			stmt.execute(querySQL);

			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleResource ADD FOREIGN KEY (FK_SottoInsieme) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleResource ADD FOREIGN KEY (FK_TuplaRes) REFERENCES XcreateTuple(PK_Tupla);");
			stmt.execute(querySQL);

			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleAction ADD FOREIGN KEY (FK_SottoInsieme) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleAction ADD FOREIGN KEY (FK_TuplaAct) REFERENCES XcreateTuple(PK_Tupla);");
			stmt.execute(querySQL);

			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleEnvironment ADD FOREIGN KEY (FK_SottoInsieme) REFERENCES XcreateSottoInsiemiTuple(PK_SottoInsieme);");
			stmt.execute(querySQL);
			querySQL = String.valueOf("ALTER TABLE XcreateTupleSSTupleEnvironment ADD FOREIGN KEY (FK_TuplaEnv) REFERENCES XcreateTuple(PK_Tupla);");
			stmt.execute(querySQL);

			stmt.close();
			this.connectionMySQL.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
