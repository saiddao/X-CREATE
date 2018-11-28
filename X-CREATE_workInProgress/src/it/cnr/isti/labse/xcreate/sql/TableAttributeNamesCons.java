package it.cnr.isti.labse.xcreate.sql;

public class TableAttributeNamesCons {

	// table names
	public static final String COMBINAZIONI = "XcreateCombinazioni";
	public static final String TUPLE = "XcreateTuple";
	public static final String TIPI_TUPLA = "XcreateTipiTupla";
	public static final String TIPI_COMBINAZIONE = "XcreateTipiCombinazione";
	public static final String DISCENDENTE_ANTENATO = "XcreateDiscendenteAntenato";
	public static final String NODI = "XcreateNodi";
	public static final String TIPI_NODO = "XcreateTipiNodo";
	public static final String POLITICHE = "XcreatePolitiche";
	public static final String SOTTOINSIEMI_TUPLE = "XcreateSottoInsiemiTuple";
	public static final String TUPLE_SS_TUPLE_SUBJECT = "XcreateTupleSSTupleSubject";
	public static final String TUPLE_SS_TUPLE_RESOURCE = "XcreateTupleSSTupleResource";
	public static final String TUPLE_SS_TUPLE_ACTION = "XcreateTupleSSTupleAction";
	public static final String TUPLE_SS_TUPLE_ENVIRONMENT = "XcreateTupleSSTupleEnvironment";
	// attribute names
	// politiche
	public static final String NOME_POLITICA = "NomePolitica";
	public static final String MIN_SUB = "MinSub";
	public static final String MAX_SUB = "MaxSub";
	public static final String MIN_RES = "MinRes";
	public static final String MAX_RES = "MaxRes";
	public static final String MIN_ACT = "MinAct";
	public static final String MAX_ACT = "MaxAct";
	public static final String MIN_ENV = "MinEnv";
	public static final String MAX_ENV = "MaxEnv";
	public static final String TESTO_POLITICA = "Testo";
	public static final String PK_POLITICA = "PK_Politica";
	// tipi tupla
	public static final String NOME_TIPO_TUPLA = "NomeTipoTupla";
	public static final String PK_TIPO_TUPLA = "PK_TipoTupla";
	// tipi nodo
	public static final String NOME_TIPO_NODO = "NomeTipoNodo";
	public static final String PK_TIPO_NODO = "PK_TipoNodo";
	// tipi combinazione
	public static final String NOME_TIPO_COMBINAZIONE = "NomeTipoCombinazione";
	public static final String PK_TIPO_COMBINAZIONE = "PK_TipoCombinazione";
	// nodi
	public static final String ID_XACML = "IdXacml";
	public static final String ID_NODO = "IdNodo";
	public static final String FUNCTION = "Function";
	public static final String PK_NODO = "PK_Nodo";
	public static final String FK_POLITICA = "FK_Politica";
	public static final String FK_PADRE = "FK_Padre";
	public static final String FK_TIPO_NODO = "FK_TipoNodo";
	// associazione discendente-antenato
	public static final String FK_DISCENDENTE = "FK_Discendente";
	public static final String FK_ANTENATO = "FK_Antenato";
	public static final String PK_DISCENDENTE_ANTENATO = "PK_DiscendenteAntenato";
	// tuple
	public static final String ID_TIPLA = "IdTupla";
	public static final String ATTRIBUTE_VALUE = "AttributeValue";
	public static final String ATTRIBUTE_ID = "AttributeId";
	public static final String DATA_TYPE = "DataType";
	public static final String ISSUER = "Issuer";
	public static final String SUBJECT_CATEGORY = "SubjectCategory";
	public static final String PK_TUPLA = "PK_Tupla";
	public static final String FK_TIPO_TUPLA = "FK_TipoTupla";
	public static final String FK_NODO = "FK_Nodo";
	// combinazioni
	public static final String VALORE_COMBINAZIONE = "ValoreCombinazione";
	public static final String PK_COMBINAZIONE = "PK_Combinazione";
	public static final String FK_TIPO_COMBINAZIONE = "FK_TipoCombinazione";
	//	public static final String FK_NODO = "FK_Nodo";
	public static final String FK_SUBSET_SUB = "FK_SubSetSub";
	public static final String FK_SUBSET_RES = "FK_SubSetRes";
	public static final String FK_SUBSET_ACT = "FK_SubSetAct";
	public static final String FK_SUBSET_ENV = "FK_SubSetEnv";
	// sottoInsiemi Tuple
	public static final String VALORE_SOTTOINSIEME = "ValoreSottoInsieme";
	public static final String PK_SOTTOINSIEME = "PK_SottoInsieme";
	// associazione sottoInsiemi subject
	public static final String PK_TUPLE_SS_TUPLE_SUBJECT = "PK_TupleSSTupleSubject";
	public static final String FK_SOTTOINSIEME = "FK_SottoInsieme";
	public static final String FK_TUPLA_SUB = "FK_TuplaSub";
	// associazione sottoInsiemi Resource
	public static final String PK_TUPLE_SS_TUPLE_RESOURCE= "PK_TupleSSTupleResource";
	//	public static final String FK_SOTTOINSIEME = "FK_SottoInsieme";
	public static final String FK_TUPLA_RES = "FK_TuplaRes";
	// associazione sottoInsiemi Action
	public static final String PK_TUPLE_SS_TUPLE_ACTION = "PK_TupleSSTupleAction";
	//	public static final String FK_SOTTOINSIEME = "FK_SottoInsieme";
	public static final String FK_TUPLA_ = "FK_TuplaAct";
	// associazione sottoInsiemi Environment
	public static final String PK_TUPLE_SS_TUPLE_ENVIRONMENT = "PK_TupleSSTupleEnvironment";
	//	public static final String FK_SOTTOINSIEME = "FK_SottoInsieme";
	public static final String FK_TUPLA_ENV = "FK_TuplaEnv";

}
