package it.cnr.isti.labse.xcreate.sql;

public class DeleteSQL {

	public static String deleteDiscendenteAntenato(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE DA.*  ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.DISCENDENTE_ANTENATO+" DA "); 
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND "); 
		querySQL.append("(DA.FK_Antenato = N.PK_Nodo OR DA.FK_Discendente = N.PK_Nodo); ");
		System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deleteCombinazioni(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE C.* "); 
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.COMBINAZIONI+" C "); 
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND  ");
		querySQL.append("C.FK_Nodo = N.PK_Nodo; ");
		System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deleteTuple(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE T.*  ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T  ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND  ");
		querySQL.append("T.FK_Nodo = N.PK_Nodo; ");
		//System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String UpdateFkPkadre(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("UPDATE "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.POLITICHE+" P SET N.FK_Padre = NULL "); 
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND ");  
		querySQL.append("N.FK_Politica = P.PK_Politica; "); 
		System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deleteNodi(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE N.* "); 
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N "); 
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND ");
		querySQL.append("N.FK_Politica = P.PK_Politica;");
		System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deletePolicy(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE P.* "); 
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P "); 
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+"; ");
		System.out.println(querySQL);
		return querySQL.toString();
	}
	//delete per i 4 sotto insiemi
	public static String deleteSSTupleSubject(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE TSSTS.*  ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_SUBJECT+" TSSTS ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND  ");
		querySQL.append("T.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("TSSTS.FK_TuplaSub = T.PK_Tupla; ");
		//System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deleteSSTupleResource(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE TSSTR.*  ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_RESOURCE+" TSSTR ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND  ");
		querySQL.append("T.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("TSSTR.FK_TuplaRes = T.PK_Tupla; ");
		//System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deleteSSTupleAction(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE TSSTA.*  ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ACTION+" TSSTA ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND  ");
		querySQL.append("T.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("TSSTA.FK_TuplaAct = T.PK_Tupla; ");
		//System.out.println(querySQL);
		return querySQL.toString();
	}
	public static String deleteSSTupleEnvironment(int pkPolicy){	
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("DELETE TSSTE.*  ");
		querySQL.append("FROM "+TableAttributeNamesCons.POLITICHE+" P, "+TableAttributeNamesCons.NODI+" N, "+TableAttributeNamesCons.TUPLE+" T, "+TableAttributeNamesCons.TUPLE_SS_TUPLE_ENVIRONMENT+" TSSTE ");
		querySQL.append("WHERE P.PK_Politica = "+pkPolicy+" AND  ");
		querySQL.append("N.FK_Politica = P.PK_Politica AND  ");
		querySQL.append("T.FK_Nodo = N.PK_Nodo AND ");
		querySQL.append("TSSTE.FK_TuplaEnv = T.PK_Tupla; ");
		//System.out.println(querySQL);
		return querySQL.toString();
	}
	/**
	 * La delete per la tabella XcreateSottoInsiemiTuple
	 * non prende pkPolicy come parametro perche'
	 * va a cancellare le righe non più riferite 
	 */
	public static String deleteSottoInsiemiTuple(){	
		StringBuilder querySQL = new StringBuilder();
		
		querySQL.append("DELETE XSI.* ");
		querySQL.append("FROM "+TableAttributeNamesCons.SOTTOINSIEMI_TUPLE+" XSI ");
		querySQL.append("WHERE NOT EXISTS ");
		querySQL.append("(SELECT * ");
		querySQL.append("FROM "+TableAttributeNamesCons.COMBINAZIONI+" XC ");
		querySQL.append("WHERE XC.FK_SubSetSub = XSI.PK_SottoInsieme OR ");
		querySQL.append("XC.FK_SubSetRes = XSI.PK_SottoInsieme OR ");
		querySQL.append("XC.FK_SubSetAct = XSI.PK_SottoInsieme OR ");
		querySQL.append("XC.FK_SubSetEnv = XSI.PK_SottoInsieme);");		
		
		return querySQL.toString();
	}
}
