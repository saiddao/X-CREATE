package it.cnr.isti.labse.xcreate.sql;
/*
 * contiene tutte le operazioni ausiliarie
 * 
 * FIXME
 * 
 * DEFINIRE BENE IL SUO RUOLO .. NON LASCIARLO AMBIGUO
 */
public class SelectSQL {

	/*
	 * scrivere le query per sapere quante combinazioni ci sono per la tecnica Simple Combinatorial Generation
	 * dal nodo radice e
	 * dal un nodo intermedio
	 * 
	 * vedere quali metodi si possono cancellare perche' inutilizzati...
	 */
	
	public SelectSQL(){

	}
	/*
	 * PERMETTE DI SELEZIONARE TUTTE LE TUPLE RIFERITE DALLE COMBINAZIONI GENERATE 
	 * A PARTIRE DA NODO IDENTIFICATO DAL PARAMETRO pkNodo APPARTENENTE ALLA POLITCA
	 * IDENTIFICATA DAL PARAMETRO pkPolitica.
	 * pkPolitica e pkNodo SONO LE CHIAVI PRMARIE DELLE TABELLE POLITICHE E NODI.
	 */
	/*
	 * QUESTO POI SI ELIMINA ???
	 *
	public static String selectTupleFromCombinationOLD(int pkPolitica, int pkNodo){
		StringBuilder querySql = new StringBuilder();
		querySql.append("select * ");
		querySql.append("from (  (SELECT T.*, TT.NomeTipoTupla, TC.NomeTipoCombinazione, C.Pk_Combinazione " );
		querySql.append("FROM Politiche P, Nodi N, Combinazioni C, Tuple T, TipiTupla TT, TipiCombinazione TC ");
		querySql.append("WHERE " );

		querySql.append("P.PK_Politica = "+pkPolitica+" AND ");

		querySql.append("N.FK_Politica = P.PK_Politica AND " );

		querySql.append("N.PK_Nodo = "+pkNodo+" AND ");

		querySql.append("C.FK_Nodo = N.PK_Nodo AND " );
		querySql.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND " );
		querySql.append("T.PK_Tupla = C.FK_TuplaSub AND " );
		querySql.append("TT.PK_TipoTupla = T.FK_TipoTupla) " );
		
		querySql.append("UNION ALL " );
		
		querySql.append("(SELECT T.*, TT.NomeTipoTupla, TC.NomeTipoCombinazione, C.Pk_Combinazione " );
		querySql.append("FROM Politiche P, Nodi N, Combinazioni C, Tuple T, TipiTupla TT, TipiCombinazione TC " );
		querySql.append("WHERE " );

		querySql.append("P.PK_Politica = "+pkPolitica+" AND ");

		querySql.append("N.FK_Politica = P.PK_Politica AND " );

		querySql.append("N.PK_Nodo = "+pkNodo+" AND ");

		querySql.append("C.FK_Nodo = N.PK_Nodo AND " );
		querySql.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND " );
		querySql.append("T.PK_Tupla = C.FK_TuplaRes AND " );
		querySql.append("TT.PK_TipoTupla = T.FK_TipoTupla) " );
		querySql.append("UNION ALL " );
		querySql.append("(SELECT T.*, TT.NomeTipoTupla, TC.NomeTipoCombinazione, C.Pk_Combinazione " );
		querySql.append("FROM Politiche P, Nodi N, Combinazioni C, Tuple T, TipiTupla TT, TipiCombinazione TC " );
		querySql.append("WHERE " );

		querySql.append("P.PK_Politica = "+pkPolitica+" AND ");

		querySql.append("N.FK_Politica = P.PK_Politica AND " );

		querySql.append("N.PK_Nodo = "+pkNodo+" AND ");

		querySql.append("C.FK_Nodo = N.PK_Nodo AND " );
		querySql.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND " );
		querySql.append("T.PK_Tupla = C.FK_TuplaAct AND " );
		querySql.append("TT.PK_TipoTupla = T.FK_TipoTupla) " );
		querySql.append("UNION ALL " );
		querySql.append("(SELECT T.*, TT.NomeTipoTupla, TC.NomeTipoCombinazione, C.Pk_Combinazione " );
		querySql.append("FROM Politiche P, Nodi N, Combinazioni C, Tuple T, TipiTupla TT, TipiCombinazione TC " );
		querySql.append("WHERE " );

		querySql.append("P.PK_Politica = "+pkPolitica+" AND ");

		querySql.append("N.FK_Politica = P.PK_Politica AND " );

		querySql.append("N.PK_Nodo = "+pkNodo+" AND ");

		querySql.append("C.FK_Nodo = N.PK_Nodo AND " );
		querySql.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND " );
		querySql.append("T.PK_Tupla = C.FK_TuplaEnv AND " );
		querySql.append("TT.PK_TipoTupla = T.FK_TipoTupla)) as P " );
		querySql.append("order By PK_Combinazione, NomeTipoCombinazione; ");

		//System.out.println(querySql);
		return querySql.toString();
	}*/
	public int countCombinations(){
		// FIXME
		return 0;
	}
	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DELLE COMBINAZIONI GENERATE A PARTIRE DAL NODO IDENTIFICATO 
	 * DAL PARAMETRO pkNodo APPARTENENTE ALLA POLITICA IDENTIFICATA DAL PARAMETRO pkPolitica.
	 * pkPolitica e pkNodo SONO LE CHIAVI PRIMARIE RISPETTIVAEMENTE DELLA TABELLA POLTICHE 
	 * E DELLA TABELLA NODI
	 */
	private String getQueryCountCombSQL(int pkNodo, int pkPlitica){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT count(*) AS Quanti ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C ");
		querySQL.append("WHERE P.PK_Politica = N.FK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("N.PK_Nodo = C.FK_Nodo AND ;");
		return querySQL.toString();
	}
	/*
	 * PERMETTE DI RECUPERARE LE INFORMAZIONI RELATIVE ALLA TABELLA POLITICHE
	 * LE POLITICHE ANALIZZATE FINO A QUESTO MOMENTO 
	 */
	public static String getQuerySelectNomePolicaFromPolitiche(){
		return "SELECT NomePolitica, PK_Politica FROM "+TableAttributeNamesCons.POLITICHE+" ORDER BY NomePolitica;";
	}
	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DELLE POTICHE PRESENTI NEL DATABASE
	 * IL NUMERO DELLE POLITICHE ANALIZZATE FINO A QUESTO MOMENTO
	 */
	public static String getQuerySelectCardinalitaTabellaPolitche() {

		return "SELECT COUNT(*) AS TableSize FROM "+TableAttributeNamesCons.POLITICHE+"";
	}
	/*
	 * permette di stabilire se una poliica e' presente nel database 
	 */
	public static String getSelectCountPoliticheConNome(String nomePolitica){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT COUNT(*) AS Quanti ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" ");
		querySQL.append("WHERE NomePolitica = \""+nomePolitica+"\";");
		return querySQL.toString();
	}
	public static String getQuerySelectNodiForJTreeModel(int pkPolitica){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("select N.*, TN.NomeTipoNodo ");   
		querySQL.append("from "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TIPI_NODO+" TN ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("P.PK_Politica = N.FK_Politica AND ");
		querySQL.append("N.FK_TipoNodo = TN.PK_TipoNodo ");
		querySQL.append("order by N.FK_Padre, N.idNodo; ");
		System.out.println("getQuerySelectNodiForJTreeModel \n"+querySQL.toString());
		return querySQL.toString();
	}
	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DELLE COMBINAZIONI GENERATE A PARTIRE DAL NODO RADICE DELLA 
	 * POLITICA IDENTIFICATA DAL PARAMETRO pkPolitica.
	 * SERVE PER LA STRATEGIA MULTIPLE COMBINATORIAL GENERATION 
	 */
	public static String getQuerySelectCountFromRootNodeCombinazioni(int pkPolitica){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT count(*) AS Quanti ");
		querySQL.append("FROM "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.NODI+" N ");
		querySQL.append("WHERE N.FK_Politica = " +pkPolitica+" AND ");
		querySQL.append("N.PK_Nodo = C.FK_Nodo AND "); 
		querySQL.append("N.FK_Padre IS NULL;");
		System.out.println(querySQL.toString());
		return querySQL.toString();
	}
	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DI COMBINAZIONI GENERATE A PARTIRE DAL NODO IDENTIFICATO DAL 
	 * PARAMETRO pkNodo APPARTENENTE ALLA POLITICA IDENTIFICATA DAL PARAMETRO pkPolitica
	 * SERVE PER LA STRATEGIA Hierarchical Multiple Combinatorial Generation
	 */
	public static String getQuerySelectCountFromPoliticheNodiCombinazioni(int pkPolitica, int pkNodo){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT count(*) AS Quanti ");
		querySQL.append("FROM "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.NODI+" N ");
		querySQL.append("WHERE N.FK_Politica = " +pkPolitica+" AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("N.PK_Nodo = C.FK_Nodo ; ");
		//System.out.println(querySQL.toString());
		return querySQL.toString();
	}
	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DI TUPLE CHE RISIEDONO NEL SOTTOALBERO DI RADICE IL NODO 
	 * IDENTIFICATO DAL PARAMETRO pkNodo.
	 * SERVE PER DISCRIMINARE LA GENERAZIONE DELLE COMBINAZIONI PER IL NODO RADICE DEL SOTTOALBERO
	 */
	public static String getQuerySelectCountTupleIntoSubTree(int pkNodo){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT count(*) AS Quanti ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+
				TableAttributeNamesCons.NODI+" NA, "+TableAttributeNamesCons.NODI+" ND, "+
				TableAttributeNamesCons.DISCENDENTE_ANTENATO+" DA, "+
				TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = NA.FK_Politica AND ");
		querySQL.append("NA.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("NA.PK_Nodo = DA.FK_Antenato AND ");
		querySQL.append("ND.PK_Nodo = DA.FK_Discendente AND ");
		querySQL.append("T.FK_Nodo = ND.PK_Nodo AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL.append("GROUP BY T.FK_TipoTupla, T.AttributeId,T.DataType, T.AttributeValue, T.Issuer, T.SubjectCategory;");
		System.out.println("SelectSQL:: "+querySQL.toString());
		return querySQL.toString();
	}
	
	/*
	 * PERMETTE DI SELEZIONARE TUTTE LE TUPLE RIFERITE DALLE COMBINAZIONI GENERATE 
	 * A PARTIRE DAL NODO IDENTIFICATO DAL PARAMETRO pkNodo APPARTENENTE ALLA POLITCA
	 * IDENTIFICATA DAL PARAMETRO pkPolitica.
	 * pkPolitica e pkNodo SONO LE CHIAVI PRIMARIE DELLE TABELLE POLITICHE E NODI.
	 */
	/**
	 * QUANDO SI E' SICURI CHE FUNZIONA BENE
	 * SISTEMARE I NOMI DELLE TABELLE
	 */
	public static String selectTupleFromCombination(int pkPolitica, int pkNodo){
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("SELECT * ");
		querySQL.append("FROM (");
		querySQL.append("(SELECT XT.*, XTT.NomeTipoTupla, XTC.NomeTipoCombinazione, XC.Pk_Combinazione ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, " +
				TableAttributeNamesCons.NODI+" XN, "+TableAttributeNamesCons.COMBINAZIONI+" XC, " +
				TableAttributeNamesCons.TIPI_COMBINAZIONE+" XTC, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI, " +
				TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" XTSSTS, "+TableAttributeNamesCons.TUPLE+" XT, " +
				TableAttributeNamesCons.TIPI_TUPLA+" XTT ");
		querySQL.append("WHERE XP.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("XN.FK_Politica = XP.PK_Politica AND ");
		querySQL.append("XN.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("XC.FK_Nodo = XN.PK_Nodo AND ");
		querySQL.append("XC.FK_TipoCombinazione = XTC.PK_TipoCombinazione AND ");
		querySQL.append("XSI.PK_SottoInsieme = XC.FK_SubSetSub AND ");
		querySQL.append("XTSSTS.FK_SottoInsieme = XSI.PK_SottoInsieme AND ");
		querySQL.append("XT.PK_Tupla = XTSSTS.FK_TuplaSub AND ");
		querySQL.append("XTT.PK_TipoTupla = XT.FK_TipoTupla) ");
		
		querySQL.append("UNION ALL ");
		querySQL.append("(SELECT XT.*, XTT.NomeTipoTupla, XTC.NomeTipoCombinazione, XC.Pk_Combinazione ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+
				TableAttributeNamesCons.NODI+" XN, "+TableAttributeNamesCons.COMBINAZIONI+" XC, " +
				TableAttributeNamesCons.TIPI_COMBINAZIONE+" XTC, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI, " +
				TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" XTSSTR, "+TableAttributeNamesCons.TUPLE+" XT, " +
				TableAttributeNamesCons.TIPI_TUPLA+" XTT ");
		querySQL.append("WHERE XP.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("XN.FK_Politica = XP.PK_Politica AND ");
		querySQL.append("XN.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("XC.FK_Nodo = XN.PK_Nodo AND ");
		querySQL.append("XC.FK_TipoCombinazione = XTC.PK_TipoCombinazione AND ");

		querySQL.append("XSI.PK_SottoInsieme = XC.FK_SubSetRes AND ");
		querySQL.append("XTSSTR.FK_SottoInsieme = XSI.PK_SottoInsieme AND ");
		querySQL.append("XT.PK_Tupla = XTSSTR.FK_TuplaRes AND ");
		querySQL.append("XTT.PK_TipoTupla = XT.FK_TipoTupla) ");
		
		querySQL.append("UNION ALL ");
		querySQL.append("(SELECT XT.*, XTT.NomeTipoTupla, XTC.NomeTipoCombinazione, XC.Pk_Combinazione ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+
				TableAttributeNamesCons.NODI+" XN, " +TableAttributeNamesCons.COMBINAZIONI+" XC, "+
				TableAttributeNamesCons.TIPI_COMBINAZIONE+" XTC, " +TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI, "+
				TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" XTSSTA, " +TableAttributeNamesCons.TUPLE+" XT, "+
				TableAttributeNamesCons.TIPI_TUPLA+" XTT ");
		querySQL.append("WHERE XP.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("XN.FK_Politica = XP.PK_Politica AND ");
		querySQL.append("XN.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("XC.FK_Nodo = XN.PK_Nodo AND ");
		querySQL.append("XC.FK_TipoCombinazione = XTC.PK_TipoCombinazione AND ");

		querySQL.append("XSI.PK_SottoInsieme = XC.FK_SubSetAct AND ");
		querySQL.append("XTSSTA.FK_SottoInsieme = XSI.PK_SottoInsieme AND ");
		querySQL.append("XT.PK_Tupla = XTSSTA.FK_TuplaAct AND ");
		querySQL.append("XTT.PK_TipoTupla = XT.FK_TipoTupla) ");

		querySQL.append("UNION ALL ");
		querySQL.append("(SELECT XT.*, XTT.NomeTipoTupla, XTC.NomeTipoCombinazione, XC.Pk_Combinazione ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" XP, "+
				TableAttributeNamesCons.NODI+" XN, " +TableAttributeNamesCons.COMBINAZIONI+" XC, "+
				TableAttributeNamesCons.TIPI_COMBINAZIONE+" XTC, " +TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI, "+
				TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" XTSSTE, " +TableAttributeNamesCons.TUPLE+" XT, "+
				TableAttributeNamesCons.TIPI_TUPLA+" XTT ");
		querySQL.append("WHERE XP.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("XN.FK_Politica = XP.PK_Politica AND ");
		querySQL.append("XN.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("XC.FK_Nodo = XN.PK_Nodo AND ");
		querySQL.append("XC.FK_TipoCombinazione = XTC.PK_TipoCombinazione AND ");

		querySQL.append("XSI.PK_SottoInsieme = XC.FK_SubSetEnv AND ");
		querySQL.append("XTSSTE.FK_SottoInsieme = XSI.PK_SottoInsieme AND ");
		querySQL.append("XT.PK_Tupla = XTSSTE.FK_TuplaEnv AND ");
		querySQL.append("XTT.PK_TipoTupla = XT.FK_TipoTupla)) ");
		
		querySQL.append("AS Result ");
		querySQL.append("ORDER BY Result.PK_Combinazione, Result.NomeTipoCombinazione, Result.FK_TipoTupla;");
		
		
		System.out.println("interrpgazione Multiple : \n\n"+querySQL.toString()+"\n\n");
		return querySQL.toString();
	}
	/**
	 * AGGIUNGERE UNA QUERY PER AVERE TUTTE LE COMBINAZIONI IN CUI 
	 * COMPAIONO 1-S, 1-R, 1-A, 1-E
	 * COSI' SI POSSONO FARE LE SIMPLE-REQUEST
	 * CON LA MIA INTERROGAZIONE DEVO SAPERE QUANTI TipiTupla CI SONO
	 */
	public static String selectTupleFromSimpleCombination(int pkPolitica, int pkNodo){
		StringBuilder querySQL = new StringBuilder();
		
		querySQL.append("SELECT * FROM ( ");
		querySQL.append("SELECT * FROM ( ");
		querySQL.append("(SELECT TC.NomeTipoCombinazione, C.Pk_Combinazione, T.*, TT.NomeTipoTupla, COUNT(C.PK_Combinazione) as S ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.TIPI_COMBINAZIONE+" TC, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" TSSTS, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetSub AND ");
		querySQL.append("TSSTS.FK_SottoInsieme = SI.PK_SottoInsieme AND ");
		querySQL.append("T.PK_Tupla = TSSTS.FK_TuplaSub AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT TC.NomeTipoCombinazione, C.Pk_Combinazione, T.*, TT.NomeTipoTupla, COUNT(C.Pk_Combinazione) as R ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.TIPI_COMBINAZIONE+" TC, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" TSSTR, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetRes AND ");
		querySQL.append("TSSTR.FK_SottoInsieme = SI.PK_SottoInsieme AND ");
		querySQL.append("T.PK_Tupla = TSSTR.FK_TuplaRes AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT TC.NomeTipoCombinazione, C.Pk_Combinazione, T.*, TT.NomeTipoTupla, COUNT(C.PK_Combinazione) as A ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.TIPI_COMBINAZIONE+" TC, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" TSSTA, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetAct AND ");
		querySQL.append("TSSTA.FK_SottoInsieme = SI.PK_SottoInsieme AND ");
		querySQL.append("T.PK_Tupla = TSSTA.FK_TuplaAct AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT TC.NomeTipoCombinazione, C.Pk_Combinazione, T.*, TT.NomeTipoTupla, COUNT(C.PK_Combinazione) as E ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.TIPI_COMBINAZIONE+" TC, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" TSSTE, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("C.FK_TipoCombinazione = TC.PK_TipoCombinazione AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetEnv AND ");
		querySQL.append("TSSTE.FK_SottoInsieme = SI.PK_SottoInsieme AND ");
		querySQL.append("T.PK_Tupla = TSSTE.FK_TuplaEnv AND ");
		querySQL.append("T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1)) ");
		
		querySQL.append("AS Result2 ");
		querySQL.append("GROUP BY Result2.Pk_Combinazione, Result2.FK_TipoTupla) ");
		querySQL.append("AS ResultInter ");
		querySQL.append("WHERE ResultInter.PK_Combinazione IN ");
		querySQL.append("(SELECT boh.PK_Combinazione ");
		querySQL.append("FROM (");

		querySQL.append("SELECT *, COUNT(Result.PK_Combinazione) AS uffa ");
		querySQL.append("FROM (");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS S ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" TSSTS ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetSub AND ");
		querySQL.append("TSSTS.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS R ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" TSSTR ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetRes AND ");
		querySQL.append("TSSTR.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS A ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" TSSTA ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetAct AND ");
		querySQL.append("TSSTA.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS E ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" TSSTE ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetEnv AND ");
		querySQL.append("TSSTE.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1)) ");
		querySQL.append("AS Result ");
		querySQL.append("GROUP BY PK_Combinazione) AS boh ");
		querySQL.append("WHERE boh.uffa = ");
		querySQL.append("(SELECT COUNT(*) AS cnt ");
		querySQL.append("FROM (");
		querySQL.append("SELECT T.FK_TipoTupla, TT.NomeTipoTupla ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = T.FK_Nodo AND T.FK_TipoTupla = TT.PK_TipoTupla ");//N.FK_Padre IS NULL AND
		
		//querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND N.FK_Politica = P.PK_Politica AND ");
		//querySQL.append("N.PK_Nodo = "+pkNodo+" AND N.PK_Nodo = T.FK_Nodo AND T.FK_TipoTupla = TT.PK_TipoTupla ");
		querySQL.append("GROUP BY FK_TipoTupla)  AS cnt");
		querySQL.append(")) ORDER BY ResultInter.PK_Combinazione;");
		
		return querySQL.toString();
	}
	
	

	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DELLE COMBINAZIONI GENERATE A PARTIRE DAL NODO RADICE DELLA 
	 * POLITICA IDENTIFICATA DAL PARAMETRO pkPolitica.
	 * SERVE PER LA STRATEGIA SIMPLE COMBINATORIAL GENERATION 
	 */
	public static String getQuerySelectCountFromRootNodeCombSimple(int pkPolitica){
		StringBuilder querySQL = new StringBuilder();
		
		querySQL.append("SELECT count(*) AS Quanti ");
		querySQL.append("FROM (SELECT *, count(Result.PK_Combinazione)  AS uffa ");
		querySQL.append("FROM (");
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS S ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" TSSTS ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.FK_Padre IS NULL AND ");//<------------------------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetSub AND ");
		querySQL.append("TSSTS.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS R ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" TSSTR ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.FK_Padre IS NULL AND ");//<--------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetRes AND ");
		querySQL.append("TSSTR.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS A ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" TSSTA ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.FK_Padre IS NULL AND ");//<--------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetAct AND ");
		querySQL.append("TSSTA.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS E ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" TSSTE ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.FK_Padre IS NULL AND ");//<--------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetEnv AND ");
		querySQL.append("TSSTE.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1)) ");
		querySQL.append("AS Result ");
		querySQL.append("GROUP BY PK_Combinazione) AS boh ");
		querySQL.append("WHERE boh.uffa = ");
		querySQL.append("(SELECT COUNT(*) AS cnt ");
		querySQL.append("FROM (");
		querySQL.append("SELECT T.FK_TipoTupla, TT.NomeTipoTupla ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = T.FK_Nodo AND T.FK_TipoTupla = TT.PK_TipoTupla ");//N.FK_Padre IS NULL AND
		//querySQL.append("N.PK_Nodo = "+pkNodo+" AND N.PK_Nodo = T.FK_Nodo AND T.FK_TipoTupla = TT.PK_TipoTupla ");//N.PK_Nodo = "+pkNodo+" AND
		querySQL.append("GROUP BY FK_TipoTupla)  AS cnt)");
		
		return querySQL.toString();
	}
	/*
	 * PERMETTE DI CALCOLARE IL NUMERO DI COMBINAZIONI GENERATE A PARTIRE DAL NODO IDENTIFICATO DAL 
	 * PARAMETRO pkNodo APPARTENENTE ALLA POLITICA IDENTIFICATA DAL PARAMETRO pkPolitica
	 * SERVE PER LA STRATEGIA Hierarchical Simple Combinatorial Generation
	 */
	public static String getQuerySelectCountFromPoliticheNodiCombSimple(int pkPolitica, int pkNodo){
		StringBuilder querySQL = new StringBuilder();
		
		querySQL.append("SELECT count(*) AS Quanti ");
		querySQL.append("FROM (SELECT *, count(Result.PK_Combinazione)  AS uffa ");
		querySQL.append("FROM (");
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS S ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" TSSTS ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");//<------------------------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetSub AND ");
		querySQL.append("TSSTS.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS R ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" TSSTR ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");//<--------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetRes AND ");
		querySQL.append("TSSTR.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS A ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" TSSTA ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");//<--------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetAct AND ");
		querySQL.append("TSSTA.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1) ");
		querySQL.append("UNION ALL ");
		
		querySQL.append("(SELECT C.Pk_Combinazione, COUNT(C.PK_Combinazione) AS E ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C, "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" SI, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" TSSTE ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND ");
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND ");//<--------
		querySQL.append("C.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("SI.PK_SottoInsieme = C.FK_SubSetEnv AND ");
		querySQL.append("TSSTE.FK_SottoInsieme = SI.PK_SottoInsieme ");
		querySQL.append("GROUP BY C.PK_Combinazione ");
		querySQL.append("HAVING COUNT(C.PK_Combinazione) = 1)) ");
		querySQL.append("AS Result ");
		querySQL.append("GROUP BY PK_Combinazione) AS boh ");
		querySQL.append("WHERE boh.uffa = ");
		querySQL.append("(SELECT COUNT(*) AS cnt ");
		querySQL.append("FROM (");
		querySQL.append("SELECT T.FK_TipoTupla, TT.NomeTipoTupla ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TIPI_TUPLA+" TT ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolitica+" AND N.FK_Politica = P.PK_Politica AND ");
//		querySQL.append("N.PK_Nodo = T.FK_Nodo AND T.FK_TipoTupla = TT.PK_TipoTupla ");//N.FK_Padre IS NULL AND
		querySQL.append("N.PK_Nodo = "+pkNodo+" AND N.PK_Nodo = T.FK_Nodo AND T.FK_TipoTupla = TT.PK_TipoTupla ");//N.PK_Nodo = "+pkNodo+" AND 
		querySQL.append("GROUP BY FK_TipoTupla)  AS cnt)");
		
		return querySQL.toString();
	}
	/**
	 * Serve per prendere i valori max e min degli elementi
	 * @param pkNodo
	 * @return
	 */
	public static String getParamPolicy(int pkNodo){
		StringBuilder querySQL = new StringBuilder();
		
		querySQL.append("SELECT P.* ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N ");
		querySQL.append("WHERE N.PK_Nodo = "+pkNodo+" AND  N.FK_Politica = P.PK_Politica");
		
		return querySQL.toString();
	}
}
