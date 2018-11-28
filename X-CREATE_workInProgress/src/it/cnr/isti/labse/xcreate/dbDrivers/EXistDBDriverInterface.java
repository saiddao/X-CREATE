package it.cnr.isti.labse.xcreate.dbDrivers;

public interface EXistDBDriverInterface {
	/**
	  	# creare un'istanza del DBMS
		# eliminare istanza DBMS
		# creare una collezione
		# eliminare una collezione (cartella)
		# navigare una collezione
		# creare una risorsa (file xml)
		# eliminare una risorsa
		# visualizzare una risorsa
		# modificare una risorsa
		# eseguire una XQuery
		
		bisogna detrminare con molta cautela la signatura dei metodi.
		
		*** per il momento basta elencarli
		
		
	 */

	//	crea una connessione con il DBMS
	public void connect();
	//	verifica se vi e' una connessione con il DBMS
	public boolean isConnected();
	//	chiude una connessione con il DBMS
	public void disconnect();
	//	crea una collzione
	public void createCollection(String collection);
	//	elimina una collezione
	public void deleteCollection();
	//	mostra il contenuto di una collezione
	public void showCollection();
	// 	crea una risorsa
	public void createResource();
	//	elimina una risorsa
	public void deleteResource();
	//	esegue una query 
	public void execute();
	
}
