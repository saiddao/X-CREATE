package it.cnr.isti.labse.xcreate.sql;

import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.filler.CombinationsGenerator;
import it.cnr.isti.labse.xcreate.filler.Combinazione;
import it.cnr.isti.labse.xcreate.util.PowerSet;
import it.cnr.isti.labse.xcreate.util.StringToEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.HashSet;

public class InsertCombinationsSQL {
	Connection mySQLConnection;
	private int pkNodo;
	private List<Integer> subList;
	private List<Integer> resList;
	private List<Integer> actList;
	private List<Integer> envList;
	private Hashtable<Integer, String> tipiTupla;
	/*
	 * 4 Hashtable<Integer, HashSet> per l'insieme dei sottoinsiemi;
	 */
	private Hashtable<Integer, HashSet<Integer>> subHash;
	private Hashtable<Integer, HashSet<Integer>> resHash;
	private Hashtable<Integer, HashSet<Integer>> actHash;
	private Hashtable<Integer, HashSet<Integer>> envHash;
	private PowerSet sottoInsiemiS;
	private PowerSet sottoInsiemiR;
	private PowerSet sottoInsiemiA;
	private PowerSet sottoInsiemiE;
	private Integer minimo;
	private Integer minEnv;
	private Integer minSub;
	private Integer minRes;
	private Integer minAct;
	private Integer maxSub;
	private Integer maxRes;
	private Integer maxAct;
	private Integer maxEnv;
	private List<String> subSetSub;
	private List<String> subSetRes;
	private List<String> subSetAct;
	private List<String> subSetEnv;

	private CombinationsGenerator combGenerator;
	private BitSet pattern;
	private Vector<Combinazione> oneWise;
	private Vector<Combinazione> pairWise;
	private Vector<Combinazione> threeWise;
	private Vector<Combinazione> fourWise;
	private Hashtable<String, Integer> tipiComb;
	private PreparedStatement preStmt;

	/*
	 * Data una chiave primaria di un nodo Genera le combinazioni del
	 * sotto_albero che ha come radice il nodo identificato dalla chiave
	 * primaria
	 * 
	 * E' utilizzato sia per il testing gerarchico (la chiave identifica un nodo
	 * interno delle potica) sia per il testing non gerarchico (la chiave
	 * identifica il nodo radice della politica)
	 */
	public InsertCombinationsSQL(Connection mySQLConnection, int pkNodoP) {

		this.mySQLConnection = mySQLConnection;
		this.pkNodo = pkNodoP;
		this.subList = new ArrayList<Integer>();
		this.resList = new ArrayList<Integer>();
		this.actList = new ArrayList<Integer>();
		this.envList = new ArrayList<Integer>();
		this.subHash = new Hashtable<Integer, HashSet<Integer>>();
		this.resHash = new Hashtable<Integer, HashSet<Integer>>();
		this.actHash = new Hashtable<Integer, HashSet<Integer>>();
		this.envHash = new Hashtable<Integer, HashSet<Integer>>();
		this.subSetSub = new ArrayList<String>();
		this.subSetRes = new ArrayList<String>();
		this.subSetAct = new ArrayList<String>();
		this.subSetEnv = new ArrayList<String>();
	}

	public void insertCombinations() {
		System.out.println("[START] INSERT COMBINATIONS ");
		createTupleAsList();// 12/03/2011 ok
		System.out.println("[START] CREATE COMBINATION , *COME SOTTOINSIEMI*");
		/*
		 * ######################################################################
		 * ######## QUI PRENDO I VALORI DAL PROPERTIES O
		 * 
		 * FARE L'INTERROGAZIONE E PRENDERE I VALORI MAX E MIN se la politica ha
		 * <Condition> metterli a mano
		 * /#########################################
		 * #####################################
		 */
		if (Boolean.valueOf(MySQLCons.Valori)) {
			this.minSub = MySQLCons.Min_Sub;
			this.minRes = MySQLCons.Min_Res;
			this.minAct = MySQLCons.Min_Act;
			this.minEnv = MySQLCons.Min_Env;
/*
 *  trovato bug il 06-12-2012
 */
//			this.minimo = MySQLCons.Min_Env;
			this.maxSub = MySQLCons.Max_Sub;
			this.maxRes = MySQLCons.Max_Res;
			this.maxAct = MySQLCons.Max_Act;
			this.maxEnv = MySQLCons.Max_Env;
		} else {
			this.minEnv = 1;
			this.minSub = 1;
			this.minRes = 1;
			this.minAct = 1;
			ResultSet result;
			Statement stmt;
			try {
				stmt = this.mySQLConnection.createStatement();
				result = stmt.executeQuery(SelectSQL
						.getParamPolicy(this.pkNodo));
				while (result.next()) {
					this.maxSub = result.getInt("MaxSub");
					this.maxRes = result.getInt("MaxRes");
					this.maxAct = result.getInt("MaxAct");
					this.maxEnv = result.getInt("MaxEnv");
				}
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		/* sse Lista non vouta */

		if (subList.size() > 0) {
			this.sottoInsiemiS = new PowerSet("Subject");
			subHash = this.sottoInsiemiS.powerset(subList, minSub, maxSub);
		}
		System.out.println(subHash.size() + " sottoinsiemi dei Subjects");
		if (resList.size() > 0) {
			this.sottoInsiemiR = new PowerSet("Resource");
			resHash = this.sottoInsiemiR.powerset(resList, minRes, maxRes);
		}
		System.out.println(resHash.size() + " sottoinsiemi dei Resources");
		if (actList.size() > 0) {
			this.sottoInsiemiA = new PowerSet("Action");
			actHash = this.sottoInsiemiA.powerset(actList, minAct, maxAct);
		}
		System.out.println(actHash.size() + " sottoinsiemi degli Actions");
		if (envList.size() > 0) {
			System.out.println("InsertCombinationsSQL.insertCombinations()");
			System.out.println("######## envList.size() :" + envList.size());
			System.out.println(envList.toString());
			System.out.println("minEnv = "+minEnv+". maxEnv = "+ maxEnv);
			this.sottoInsiemiE = new PowerSet("Environment");
			envHash = this.sottoInsiemiE.powerset(envList, minEnv, maxEnv);
			
		}
		System.out.println(envHash.size() + " sottoinsiemi degli Environment");
		// 12/03/2011 fino qui ok!!!!
		insertSubSets();// ok!!! -- salvarsi le tuple man mano in una
						// strutt.dati
		// recupero i sottoinsiemi
		// recuperaSottoInsiemi();//dopo di che... servir� ancora??? furba :-\

		/*
		 * Qui mettere se uno vuole i sottoinsiemi Subject dove risulta che gli
		 * AttributeId sono tutti diversi tra loro
		 */
		if (MySQLCons.Attr_diversi)
			chooseSubSetSubject();

		/*
		 * CREA LE COMBINAZIONI
		 */

		this.combGenerator = new CombinationsGenerator();
		this.combGenerator.generates(this.subSetSub, this.subSetRes,
				this.subSetAct, this.subSetEnv);
		this.pattern = this.combGenerator.getPattern();
		System.out.println("[FINISH] CREATE COMBINATION ");
		/*
		 * SALVA LE COMBINAZIONI
		 */
		insertComb();
		System.out.println("[FINISH] INSERT COMBINATIONS ");

	}

	private void chooseSubSetSubject() {// FIXME
	// prendo la lista che ha le chiavi di tutti i sottoinsiemi subject
		int primo = Integer.parseInt((this.subSetSub.get(0)));
		int ultimo = Integer.parseInt((this.subSetSub
				.get(this.subSetSub.size() - 1)));
		System.out.println("primo " + primo + " e ultimo " + ultimo);

		try {
			ResultSet result;
			Statement stmt;
			String querySQL;

			querySQL = "SELECT STS.FK_SottoInsieme, T.AttributeId " + "FROM "
					+ TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT + " STS, "
					+ TableAttributeNamesCons.TUPLE + " T "
					+ "WHERE STS.FK_SottoInsieme >= " + primo + " AND "
					+ "STS.FK_SottoInsieme <= " + ultimo + " AND "
					+ "STS.FK_TuplaSub = T.PK_Tupla; ";
			stmt = this.mySQLConnection.createStatement();
			result = stmt.executeQuery(querySQL);

			int keySubSet;
			// int appoggio;
			Hashtable<Integer, HashSet<String>> proof = new Hashtable<Integer, HashSet<String>>();
			while (result.next()) {
				keySubSet = result.getInt("FK_SottoInsieme");

				if (!proof.containsKey(keySubSet))
					proof.put(keySubSet, new HashSet<String>());
				boolean esiste = proof.get(keySubSet).add(
						result.getString("AttributeId"));
				if (!esiste) {
					// tolgo la chiave dalla lista
					this.subSetSub.remove(String.valueOf(keySubSet));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * si devono recuperare le PK_SottoInsieme (scrivere un metodo che
	 * interroga) divise per TipoTupla se ne fanno 4 List<String> e si danno al
	 * COMBOTEST
	 */
	// public void recuperaSottoInsiemi(){// FIXME metodo da eliminare :))))
	// System.out.println("Recupero le chiavi dei sottoinsiemi di questa politica dal nodo "+this.pkNodo);
	// Statement stmt;
	// int fktipoTupla;
	// int pksottoInsieme;
	// try {
	// stmt = this.mySQLConnection.createStatement();
	// ResultSet result = stmt.executeQuery(getQuerySelectSubSets());
	// while(result.next()){
	// fktipoTupla = result.getInt("FK_TipoTupla");
	// pksottoInsieme = result.getInt("PK_SottoInsieme");
	// switch (StringToEnum.valueOf(this.tipiTupla.get(fktipoTupla))) {
	// case Subject:
	// this.subSetSub.add(String.valueOf(pksottoInsieme));
	// break;
	// case Resource:
	// this.subSetRes.add(String.valueOf(pksottoInsieme));
	// break;
	// case Action:
	// this.subSetAct.add(String.valueOf(pksottoInsieme));
	// break;
	// case Environment:
	// this.subSetEnv.add(String.valueOf(pksottoInsieme));
	// break;
	// default:
	// break;
	// }
	// System.out.println(pksottoInsieme);
	// }
	// Iterator<String> iterS = subSetSub.iterator();
	// while (iterS.hasNext()){
	// System.out.println("SottoInsieme soggetti: "+iterS.next());
	// }
	// Iterator<String> iterR = subSetRes.iterator();
	// while (iterR.hasNext()){
	// System.out.println("SottoInsieme risorse: "+iterR.next());
	// }
	// Iterator<String> iterA = subSetAct.iterator();
	// while (iterA.hasNext()){
	// System.out.println("SottoInsieme azioni: "+iterA.next());
	// }
	// Iterator<String> iterE = subSetEnv.iterator();
	// while (iterE.hasNext()){
	// System.out.println("SottoInsieme ambienti: "+iterE.next());
	// }
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	/*
	 * Popola la tabella COMBINAZIONI
	 */
	private void insertComb() {

		try {
			this.tipiComb = SelectTipi.getTipiCombinazione();
			int pkOneWise = this.tipiComb.get("1-Wise").intValue();
			int pkPairWise = this.tipiComb.get("2-Wise").intValue();
			int pkThreeWise = this.tipiComb.get("3-Wise").intValue();
			int pkFourWise = this.tipiComb.get("4-Wise").intValue();

			this.preStmt = this.mySQLConnection
					.prepareStatement(getQueryInsertCombBach());

			switch ((4 - pattern.cardinality())) {
			case 0:
				/*
				 * non abbiamo combinazioni -- non si effettuano inserimenti
				 * nella tabella delle combinazioni
				 */
				break;

			case 1:
				System.out.println("1 :: Insert combinations in MySQL ");
				// abbiamo combinazion di tipo OneWise
				this.oneWise = this.combGenerator.getOneWise();
				for (Combinazione comb : this.oneWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkOneWise, comb.toString());
				}
				this.preStmt.executeBatch();

				break;
			case 2:
				System.out.println("2 :: Insert combinations in MySQL ");
				// abbiamo sombinazioni di tipo PairWise
				this.pairWise = this.combGenerator.getPairWise();
				for (Combinazione comb : this.pairWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkPairWise, comb.toString());
				}
				this.preStmt.executeBatch();

				break;
			case 3:
				System.out.println("3 :: Insert combinations in MySQL ");
				// abbiamo combinazioni di tipo ThreeWise
				this.pairWise = this.combGenerator.getPairWise();
				for (Combinazione comb : this.pairWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkPairWise, comb.toString());
				}
				this.preStmt.executeBatch();

				this.threeWise = this.combGenerator.getThreeWise();
				for (Combinazione comb : this.threeWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkThreeWise, comb.toString());
				}
				this.preStmt.executeBatch();

				break;
			default:
				System.out.println("4 :: Insert combinations in MySQL ");
				// abbiamo combinazioni di tipo FourWise
				this.pairWise = this.combGenerator.getPairWise();
				for (Combinazione comb : this.pairWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkPairWise, comb.toString());
				}
				this.preStmt.executeBatch();

				this.threeWise = this.combGenerator.getThreeWise();
				for (Combinazione comb : this.threeWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkThreeWise, comb.toString());
				}
				this.preStmt.executeBatch();

				this.fourWise = this.combGenerator.getFourWise();
				for (Combinazione comb : this.fourWise) {
					addInBach(comb.getSubAsInt(), comb.getResAsInt(), comb
							.getActAsInt(), comb.getEnvAsInt(), this.pkNodo,
							pkFourWise, comb.toString());
				}
				this.preStmt.executeBatch();

				break;
			}
			this.preStmt.close();
			this.mySQLConnection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addInBach(int sub, int res, int act, int env, int fkNodo,
			int fktTipo, String value) {
		try {

			/*
			 * FK_TuplaSub, FK_TuplaRes, FK_TuplaAct, FK_TuplaEnv, FK_Nodo,
			 * FK_TipoCombinazione, ValoreCombinazione
			 */
			if (sub == -1)
				this.preStmt.setString(1, null);
			else
				this.preStmt.setInt(1, sub);
			if (res == -1)
				this.preStmt.setString(2, null);
			else
				this.preStmt.setInt(2, res);
			if (act == -1)
				this.preStmt.setString(3, null);
			else
				this.preStmt.setInt(3, act);
			if (env == -1)
				this.preStmt.setString(4, null);
			else
				this.preStmt.setInt(4, env);

			this.preStmt.setInt(5, fkNodo);
			this.preStmt.setInt(6, fktTipo);
			this.preStmt.setString(7, value);
			this.preStmt.addBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTupleAsList() {
		try {
			this.tipiTupla = SelectTipi.getTitpiTuplaKeyInteger();
			Statement stmt = this.mySQLConnection.createStatement();
			int fkTipoTupla;
			int pkTupla;
			ResultSet result = stmt.executeQuery(getQuerySelectPkTuple());// <-----
																			// 12/03/2011
			while (result.next()) {
				fkTipoTupla = result.getInt("FK_TipoTupla");
				pkTupla = result.getInt("PK_Tupla");
				switch (StringToEnum.valueOf(this.tipiTupla.get(fkTipoTupla))) {
				case Subject:
					this.subList.add((pkTupla));
					break;
				case Resource:
					this.resList.add((pkTupla));
					break;

				case Action:
					this.actList.add((pkTupla));
					break;

				case Environment:
					this.envList.add((pkTupla));
					break;

				// default:
				// break;
				}
				System.out.println("FK_TipoTupla = " + fkTipoTupla
						+ "  PK_Tupla = " + pkTupla);
			}
			System.out.println(this.subList.size());
			System.out.println(this.resList.size());
			System.out.println(this.actList.size());
			System.out.println(this.envList.size());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getQueryInsertCombBach() {
		String querySQL = "INSERT INTO "
				+ TableAttributeNamesCons.COMBINAZIONI
				+ "(FK_SubSetSub, FK_SubSetRes, FK_SubSetAct, FK_SubSetEnv, FK_Nodo, FK_TipoCombinazione, ValoreCombinazione) VALUES (?,?,?,?,?,?,?);";
		return querySQL;

	}

	/*
	 * 22 Dicembre 2010 permette di selezionare tutte le tuple contenute in un
	 * sotto_albero di radice PK_Nodo e nei nodi antenati: senza duplicati
	 * ..#### per evitare di generare combinazioni ripetute ####
	 */
	public String getQuerySelectPkTuple() {
		StringBuilder querySQL = new StringBuilder();
		querySQL
				.append("SELECT  Temp.FK_TipoTupla, Temp.AttributeId,Temp.DataType, Temp.AttributeValue, Temp.Issuer, Temp.SubjectCategory, MAX(Temp.PK_Tupla) AS PK_Tupla ");
		querySQL.append("FROM( (");
		querySQL
				.append("SELECT  T.FK_TipoTupla, T.AttributeId,T.DataType, T.AttributeValue, T.Issuer, T.SubjectCategory, T.PK_Tupla ");
		querySQL.append("FROM " + TableAttributeNamesCons.POLITICHE + " P, "
				+ TableAttributeNamesCons.NODI + " NA, "
				+ TableAttributeNamesCons.NODI + " ND, "
				+ TableAttributeNamesCons.DISCENDENTE_ANTENATO + " DA, "
				+ TableAttributeNamesCons.TUPLE + " T, "
				+ TableAttributeNamesCons.TIPI_TUPLA + " TT ");
		querySQL.append("WHERE P.PK_Politica = NA.FK_Politica ");
		querySQL.append("AND NA.PK_Nodo = " + this.pkNodo + " ");
		querySQL.append("AND NA.PK_Nodo = DA.FK_Antenato AND ");
		querySQL.append("ND.PK_Nodo = DA.FK_Discendente AND ");
		querySQL.append("T.FK_Nodo = ND.PK_Nodo AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla) ");
		querySQL.append("union all ");
		querySQL.append("( ");
		querySQL.append(" ");
		querySQL
				.append("SELECT T.FK_TipoTupla, T.AttributeId, T.DataType, T.AttributeValue, T.Issuer, T.SubjectCategory, T.PK_Tupla ");
		querySQL.append("FROM " + TableAttributeNamesCons.POLITICHE + " P, "
				+ TableAttributeNamesCons.NODI + " NA, "
				+ TableAttributeNamesCons.NODI + " ND, "
				+ TableAttributeNamesCons.DISCENDENTE_ANTENATO + " DA, "
				+ TableAttributeNamesCons.TUPLE + " T, "
				+ TableAttributeNamesCons.TIPI_TUPLA + " TT ");
		querySQL.append("WHERE P.PK_Politica = NA.FK_Politica ");
		querySQL.append("AND ND.PK_Nodo = " + this.pkNodo + " ");
		querySQL.append("AND NA.PK_Nodo = DA.FK_Antenato ");
		querySQL.append("AND ND.PK_Nodo = DA.FK_Discendente ");
		querySQL.append("AND T.FK_Nodo = NA.PK_Nodo ");
		querySQL.append("AND T.FK_TipoTupla = TT.PK_TipoTupla)) as Temp ");
		querySQL
				.append("GROUP BY Temp.FK_TipoTupla, Temp.AttributeId, Temp.DataType, Temp.AttributeValue, Temp.Issuer, Temp.SubjectCategory; ");
		System.out.println(querySQL.toString());
		return querySQL.toString();
	}

	/*
	 * MA CHI LO USA??? permette di selezionare tutte le tuple contenute in un
	 * sotto_albero di radice PK_Nodo senza duplicati .. #### per evitare di
	 * generare combinazioni ripetute ####
	 */
	public String getQuerySelectPkTuple_Stable() {
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT  T.*");
		querySQL.append("FROM " + TableAttributeNamesCons.POLITICHE + " P, "
				+ TableAttributeNamesCons.NODI + " NA, "
				+ TableAttributeNamesCons.NODI + " ND, "
				+ TableAttributeNamesCons.DISCENDENTE_ANTENATO + " DA, "
				+ TableAttributeNamesCons.TUPLE + " T, "
				+ TableAttributeNamesCons.TIPI_TUPLA + " TT ");
		querySQL.append("WHERE P.PK_Politica = NA.FK_Politica AND ");
		querySQL.append("NA.PK_Nodo = " + this.pkNodo + " AND ");
		querySQL.append("NA.PK_Nodo = DA.FK_Antenato AND ");
		querySQL.append("ND.PK_Nodo = DA.FK_Discendente AND ");
		querySQL.append("T.FK_Nodo = ND.PK_Nodo AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL
				.append("GROUP BY T.FK_TipoTupla, T.AttributeId,T.DataType, T.AttributeValue, T.Issuer, T.SubjectCategory;");
		return querySQL.toString();
	}

	// public String getQuerySelectPkTuple_Stable() {
	// StringBuilder querySQL = new StringBuilder();
	// querySQL.append("SELECT  T.FK_TipoTupla, T.AttributeId,T.DataType, T.AttributeValue, T.Issuer, T.SubjectCategory, MAX(T.PK_Tupla) AS PK_Tupla ");
	// querySQL.append("FROM Politiche P, Nodi NA, Nodi ND, DiscendenteAntenato DA, Tuple T, TipiTupla TT ");
	// querySQL.append("WHERE P.PK_Politica = NA.FK_Politica AND ");
	// querySQL.append("NA.PK_Nodo = " + this.pkNodo + " AND ");
	// querySQL.append("NA.PK_Nodo = DA.FK_Antenato AND ");
	// querySQL.append("ND.PK_Nodo = DA.FK_Discendente AND ");
	// querySQL.append("T.FK_Nodo = ND.PK_Nodo AND ");
	// querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
	// querySQL.append("GROUP BY T.FK_TipoTupla, T.AttributeId,T.DataType, T.AttributeValue, T.Issuer, T.SubjectCategory;");
	// return querySQL.toString();
	// }

	/**
	 * CHE FA??? Seleziona tutti sottoinsiemi contenuti in un sotto_albero di
	 * radice PK_Nodo. Restituisce PK_SottoInsieme e il PK_TipoTupla NO!!! Mi da
	 * tutti i sottoinsiemi in cui compaiono le tuple che appartengono a quel
	 * sotto-albero non solo i sottoinsiemi creati a partire da quel nodo!!!
	 * come faccio a sapere che un sottoinsieme � stato creato a partire da quel
	 * nodo???
	 */
	// public String getQuerySelectSubSets() {
	// //vedi selectSottoInsiemiTipiTupla.sql + SelectSQL.java
	// StringBuilder querySQL = new StringBuilder();
	// querySQL.append("SELECT * ");
	// querySQL.append("FROM (");
	// querySQL.append("( SELECT XT.AttributeValue, XT.FK_TipoTupla, XSI.PK_SottoInsieme ");
	// querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+TableAttributeNamesCons.NODI+" XNA, "+TableAttributeNamesCons.NODI+" XND, "+TableAttributeNamesCons.DISCENDENTE_ANTENATO+" XDA, "+TableAttributeNamesCons.TUPLE+" XT, "+TableAttributeNamesCons.TIPI_TUPLA+" XTT, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" XSSTS, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI ");
	// querySQL.append("WHERE XP.PK_Politica = XNA.FK_Politica AND ");
	// querySQL.append("XNA.PK_Nodo = "+this.pkNodo+" AND ");
	// querySQL.append("XNA.PK_Nodo = XDA.FK_Antenato AND ");
	// querySQL.append("XND.PK_Nodo = XDA.FK_Discendente AND ");
	// querySQL.append("XT.FK_Nodo = XND.PK_Nodo AND ");
	// querySQL.append("XT.FK_TipoTupla = XTT.PK_TipoTupla AND ");
	// querySQL.append("XT.PK_Tupla = XSSTS.FK_TuplaSub AND ");
	// querySQL.append("XSSTS.FK_SottoInsieme = XSI.PK_SottoInsieme)");
	// querySQL.append(" UNION ALL ");
	// querySQL.append("( SELECT XT.AttributeValue, XT.FK_TipoTupla, XSI.PK_SottoInsieme ");
	// querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+TableAttributeNamesCons.NODI+" XNA, "+TableAttributeNamesCons.NODI+" XND, "+TableAttributeNamesCons.DISCENDENTE_ANTENATO+" XDA, "+TableAttributeNamesCons.TUPLE+" XT, "+TableAttributeNamesCons.TIPI_TUPLA+" XTT, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" XSSTR, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI ");
	// querySQL.append("WHERE XP.PK_Politica = XNA.FK_Politica AND ");
	// querySQL.append("XNA.PK_Nodo = "+this.pkNodo+" AND ");
	// querySQL.append("XNA.PK_Nodo = XDA.FK_Antenato AND ");
	// querySQL.append("XND.PK_Nodo = XDA.FK_Discendente AND ");
	// querySQL.append("XT.FK_Nodo = XND.PK_Nodo AND ");
	// querySQL.append("XT.FK_TipoTupla = XTT.PK_TipoTupla AND ");
	// querySQL.append("XT.PK_Tupla = XSSTR.FK_TuplaRes AND ");
	// querySQL.append("XSSTR.FK_SottoInsieme = XSI.PK_SottoInsieme)");
	// querySQL.append(" UNION ALL ");
	// querySQL.append("( SELECT XT.AttributeValue, XT.FK_TipoTupla, XSI.PK_SottoInsieme ");
	// querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+TableAttributeNamesCons.NODI+" XNA, "+TableAttributeNamesCons.NODI+" XND, "+TableAttributeNamesCons.DISCENDENTE_ANTENATO+" XDA, "+TableAttributeNamesCons.TUPLE+" XT, "+TableAttributeNamesCons.TIPI_TUPLA+" XTT, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" XSSTA, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI ");
	// querySQL.append("WHERE XP.PK_Politica = XNA.FK_Politica AND ");
	// querySQL.append("XNA.PK_Nodo = "+this.pkNodo+" AND ");
	// querySQL.append("XNA.PK_Nodo = XDA.FK_Antenato AND ");
	// querySQL.append("XND.PK_Nodo = XDA.FK_Discendente AND ");
	// querySQL.append("XT.FK_Nodo = XND.PK_Nodo AND ");
	// querySQL.append("XT.FK_TipoTupla = XTT.PK_TipoTupla AND ");
	// querySQL.append("XT.PK_Tupla = XSSTA.FK_TuplaAct AND ");
	// querySQL.append("XSSTA.FK_SottoInsieme = XSI.PK_SottoInsieme)");
	// querySQL.append(" UNION ALL ");
	// querySQL.append("( SELECT XT.AttributeValue, XT.FK_TipoTupla, XSI.PK_SottoInsieme ");
	// querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+TableAttributeNamesCons.NODI+" XNA, "+TableAttributeNamesCons.NODI+" XND, "+TableAttributeNamesCons.DISCENDENTE_ANTENATO+" XDA, "+TableAttributeNamesCons.TUPLE+" XT, "+TableAttributeNamesCons.TIPI_TUPLA+" XTT, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" XSSTE, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI ");
	// querySQL.append("WHERE XP.PK_Politica = XNA.FK_Politica AND ");
	// querySQL.append("XNA.PK_Nodo = "+this.pkNodo+" AND ");
	// querySQL.append("XNA.PK_Nodo = XDA.FK_Antenato AND ");
	// querySQL.append("XND.PK_Nodo = XDA.FK_Discendente AND ");
	// querySQL.append("XT.FK_Nodo = XND.PK_Nodo AND ");
	// querySQL.append("XT.FK_TipoTupla = XTT.PK_TipoTupla AND ");
	// querySQL.append("XT.PK_Tupla = XSSTE.FK_TuplaEnv AND ");
	// querySQL.append("XSSTE.FK_SottoInsieme = XSI.PK_SottoInsieme)) ");
	// querySQL.append("AS Result GROUP BY Result.PK_SottoInsieme;");
	// System.out.println(this.pkNodo+
	// " il nodo da cui sto prendendo i sottoinsiemi------------");
	// return querySQL.toString();
	// }

	/**
	 * Questo metodo si occupa di chiamare i a metodi che si occuperanno di
	 * inserire gli insiemi degli elementi XACML nelle tabelle del DB.
	 */
	public void insertSubSets() {
		// se il powerset fosse vuoto ?
		if (subHash.size() > 0)
			insertSubSetSubject();
		if (resHash.size() > 0)
			insertSubSetResource();
		if (actHash.size() > 0)
			insertSubSetAction();
		if (envHash.size() > 0)
			insertSubSetEnvironment();
	}

	/**
	 * Questo metodo prende l'insieme dei sottoinsiemi dei Subjects e lo va ad
	 * inserire nel DB, inserendo prima una riga nella tabella
	 * XcreateSottoInsiemiTuple, ne recupera subito la chiave primaria e usa
	 * questo dato per popolare la tabella XcreateTupleSSTupleSubject insieme
	 * alle chiavi primarie delle tuple che compongono il sottoinsieme.
	 */
	public void insertSubSetSubject() {
		try {
			ResultSet result;
			Statement stmt;
			String querySQL;

			/*
			 * Prima Enumeration<Integer> enum = hashtable.keys();
			 * while(enum.hasMoreElements()){ leggo l'HashSet
			 * 
			 * INSERT INTO XcreateSottoInsiemiTuple(ValoreSottoInsieme)
			 * VALUES(sottoInsieme sotto forma di stringa); e recupero la chiave
			 * subito SELECT MAX(PK_SottoInsieme) AS PK_subset FROM
			 * XcreateSottoInsiemiTuple;
			 */

			Enumeration<Integer> enumera = subHash.keys();
			while (enumera.hasMoreElements()) {
				Integer kyy = (Integer) enumera.nextElement();// per ogni
																// sottoinsieme

				HashSet<Integer> subset = subHash.get(kyy);
				String SubSS = subset.toString();
				System.out.println("Insieme " + kyy.toString() + " SUBJECT "
						+ SubSS);
				// inerisco la rappresentazione in stringa del sottoinsieme
				// nella tabella
				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
						+ "(ValoreSottoInsieme) VALUES (" + "\"" + SubSS
						+ "\");";
				// System.out.println(querySQL);
				stmt = this.mySQLConnection.createStatement();
				stmt.execute(querySQL);
				// vado a recuperare la chiave del sottoinsieme appena inserito
				Integer pksubset = null;
				result = stmt
						.executeQuery("SELECT MAX(PK_SottoInsieme) AS PK_SubSet FROM "
								+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
								+ ";");
				while (result.next()) {
					pksubset = result.getInt("PK_SubSet");// me la devo salvare
					// la struttura dati e' la lista di cui sopra, quindi
					this.subSetSub.add(String.valueOf(pksubset));
				}
				stmt.close();

				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT
						+ "(FK_SottoInsieme, FK_TuplaSub) VALUES (?,?);";
				PreparedStatement preStmt = this.mySQLConnection
						.prepareStatement(querySQL);
				// per ogni elemento del sottoinsieme scrivo una riga con chiave
				// sottoinsieme e chiave tupla
				Iterator<Integer> iter = subset.iterator();
				while (iter.hasNext()) {
					Integer fktupla = iter.next();
					preStmt.setInt(1, pksubset);
					preStmt.setInt(2, fktupla);
					preStmt.addBatch();
					// System.out.println(kyy +" "+pksubset+" "+fktupla);
				}
				preStmt.executeBatch();
				preStmt.close();
			}
			this.mySQLConnection.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Questo metodo prende l'insieme dei sottoinsiemi dei Resources e lo va ad
	 * inserire nel DB, inserendo prima una riga nella tabella
	 * XcreateSottoInsiemiTuple, ne recupera subito la chiave primaria e usa
	 * questo dato per popolare la tabella XcreateTupleSSTupleResource insieme
	 * alle chiavi primarie delle tuple che compongono il sottoinsieme.
	 */
	public void insertSubSetResource() {
		try {
			ResultSet result;
			Statement stmt;
			String querySQL;

			Enumeration<Integer> enumera = resHash.keys();
			while (enumera.hasMoreElements()) {
				Integer kyy = (Integer) enumera.nextElement();// per ogni
																// sottoinsieme

				HashSet<Integer> subset = resHash.get(kyy);
				String SubSR = subset.toString();
				System.out.println("Insieme " + kyy.toString() + " RESOURCE "
						+ SubSR);
				// inerisco la rappresentazione in stringa del sottoinsieme
				// nella tabella
				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
						+ "(ValoreSottoInsieme) VALUES (" + "\"" + SubSR
						+ "\");";
				// System.out.println(querySQL);
				stmt = this.mySQLConnection.createStatement();
				stmt.execute(querySQL);
				// vado a recuperare la chiave del sottoinsieme appena inserito
				Integer pksubset = null;
				result = stmt
						.executeQuery("SELECT MAX(PK_SottoInsieme) AS PK_SubSet FROM "
								+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
								+ ";");
				while (result.next()) {
					pksubset = result.getInt("PK_SubSet");
					this.subSetRes.add(String.valueOf(pksubset));
				}
				stmt.close();

				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE
						+ "(FK_SottoInsieme, FK_TuplaRes) VALUES (?,?);";
				PreparedStatement preStmt = this.mySQLConnection
						.prepareStatement(querySQL);
				// per ogni elemento del sottoinsieme scrivo una riga con chiave
				// sottoinsieme e chiave tupla
				Iterator<Integer> iter = subset.iterator();
				while (iter.hasNext()) {
					Integer fktupla = iter.next();
					preStmt.setInt(1, pksubset);
					preStmt.setInt(2, fktupla);
					preStmt.addBatch();
					// System.out.println(kyy +" "+pksubset+" "+fktupla);
				}
				preStmt.executeBatch();
				preStmt.close();
			}
			this.mySQLConnection.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Questo metodo prende l'insieme dei sottoinsiemi dei Actions e lo va ad
	 * inserire nel DB, inserendo prima una riga nella tabella
	 * XcreateSottoInsiemiTuple, ne recupera subito la chiave primaria e usa
	 * questo dato per popolare la tabella XcreateTupleSSTupleAction insieme
	 * alle chiavi primarie delle tuple che compongono il sottoinsieme.
	 */
	public void insertSubSetAction() {
		try {
			ResultSet result;
			Statement stmt;
			String querySQL;

			Enumeration<Integer> enumera = actHash.keys();
			while (enumera.hasMoreElements()) {
				Integer kyy = (Integer) enumera.nextElement();// per ogni
																// sottoinsieme

				HashSet<Integer> subset = actHash.get(kyy);
				String SubSA = subset.toString();
				System.out.println("Insieme " + kyy.toString() + " ACTION "
						+ SubSA);
				// inerisco la rappresentazione in stringa del sottoinsieme
				// nella tabella
				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
						+ "(ValoreSottoInsieme) VALUES (" + "\"" + SubSA
						+ "\");";
				// System.out.println(querySQL);
				stmt = this.mySQLConnection.createStatement();
				stmt.execute(querySQL);
				// vado a recuperare la chiave del sottoinsieme appena inserito
				Integer pksubset = null;
				result = stmt
						.executeQuery("SELECT MAX(PK_SottoInsieme) AS PK_SubSet FROM "
								+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
								+ ";");
				while (result.next()) {
					pksubset = result.getInt("PK_SubSet");
					this.subSetAct.add(String.valueOf(pksubset));
				}
				stmt.close();

				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION
						+ "(FK_SottoInsieme, FK_TuplaAct) VALUES (?,?);";
				PreparedStatement preStmt = this.mySQLConnection
						.prepareStatement(querySQL);
				// per ogni elemento del sottoinsieme scrivo una riga con chiave
				// sottoinsieme e chiave tupla
				Iterator<Integer> iter = subset.iterator();
				while (iter.hasNext()) {
					Integer fktupla = iter.next();
					preStmt.setInt(1, pksubset);
					preStmt.setInt(2, fktupla);
					preStmt.addBatch();
					// System.out.println(kyy +" "+pksubset+" "+fktupla);
				}
				preStmt.executeBatch();
				preStmt.close();

			}
			this.mySQLConnection.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Questo metodo prende l'insieme dei sottoinsiemi dei Environments e lo va
	 * ad inserire nel DB, inserendo prima una riga nella tabella
	 * XcreateSottoInsiemiTuple, ne recupera subito la chiave primaria e usa
	 * questo dato per popolare la tabella XcreateTupleSSTupleEnvironment
	 * insieme alle chiavi primarie delle tuple che compongono il sottoinsieme.
	 */
	public void insertSubSetEnvironment() {
		try {
			ResultSet result;
			Statement stmt;
			String querySQL;

			Enumeration<Integer> enumera = envHash.keys();
			while (enumera.hasMoreElements()) {
				Integer kyy = (Integer) enumera.nextElement();// per ogni
																// sottoinsieme

				HashSet<Integer> subset = envHash.get(kyy);
				String SubSE = subset.toString();
				System.out.println("Insieme " + kyy.toString() + " ENVIRONMENT "
						+ SubSE);
				// inerisco la rappresentazione in stringa del sottoinsieme
				// nella tabella
				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
						+ "(ValoreSottoInsieme) VALUES (" + "\"" + SubSE
						+ "\");";
				// System.out.println(querySQL);
				stmt = this.mySQLConnection.createStatement();
				stmt.execute(querySQL);
				// vado a recuperare la chiave del sottoinsieme appena inserito
				Integer pksubset = null;
				result = stmt
						.executeQuery("SELECT MAX(PK_SottoInsieme) AS PK_SubSet FROM "
								+ TableAttributeNamesCons.SOTTOINSIEMI_TUPLE
								+ ";");
				while (result.next()) {
					pksubset = result.getInt("PK_SubSet");
					this.subSetEnv.add(String.valueOf(pksubset));
				}
				stmt.close();

				querySQL = "INSERT INTO "
						+ TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT
						+ "(FK_SottoInsieme, FK_TuplaEnv) VALUES (?,?);";
				PreparedStatement preStmt = this.mySQLConnection
						.prepareStatement(querySQL);
				// per ogni elemento del sottoinsieme scrivo una riga con chiave
				// sottoinsieme e chiave tupla
				Iterator<Integer> iter = subset.iterator();
				while (iter.hasNext()) {
					Integer fktupla = iter.next();
					preStmt.setInt(1, pksubset);
					preStmt.setInt(2, fktupla);
					preStmt.addBatch();
					// System.out.println(kyy +" "+pksubset+" "+fktupla);
				}
				preStmt.executeBatch();
				preStmt.close();

			}
			this.mySQLConnection.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
