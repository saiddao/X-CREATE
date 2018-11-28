package it.cnr.isti.labse.xcreate.filler;

import it.cnr.isti.labse.xcreate.comboTest.ComboTester;
import it.cnr.isti.labse.xcreate.dbDrivers.MySQLCons;
import it.cnr.isti.labse.xcreate.util.Util;
import java.io.IOException;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 
 * 
 * @author Said Daoudagh
 *
 */
public class CombinationsGenerator {

	private List<String> subList; 
	private List<String> resList; 
	private List<String> actList; 
	private List<String> envList;

	private String url;
	private String username;
	private String password;
	private String driver;

	private Vector<Combinazione> pairWise;
	private Vector<Combinazione> threeWise;
	private Vector<Combinazione> fourWise;
	private Vector<Combinazione> oneWise;

	private BitSet pattern;
	/**
	 * Costruttore
	 * @param list4 
	 * @param actList 
	 * @param resList 
	 * @param subList 
	 */
	public CombinationsGenerator(List<String> subList, List<String> resList, List<String> actList, List<String> envList){
		this.subList = subList;
		this.resList = resList;
		this.actList = actList;
		this.envList = envList;
		
		this.pairWise = new Vector<Combinazione>();
		this.threeWise = new Vector<Combinazione>();
		this.fourWise = new Vector<Combinazione>();
	}

	public CombinationsGenerator() {
		// TODO Auto-generated constructor stub
		this.url = MySQLCons.URL_MYSQL;
		this.username = MySQLCons.LOGIN_MYSQL;
		this.password = MySQLCons.PASSWORD_MYSQL;
		this.driver = MySQLCons.DRIVER_MYSQL;
		
		this.pairWise = new Vector<Combinazione>();
		this.threeWise = new Vector<Combinazione>();
		this.fourWise = new Vector<Combinazione>();
	}

	public void generates(List<String> subList, List<String> resList, List<String> actList, List<String> envList){
		/*
		 * genera 2-wise
		 * genera 3-wise Puro
		 *  3-wise = 3-wise \ 2-wise
		 * 
		 * genera 4-wise Puro
		 *  4-wise = (4-wise puro \ 3-wise) \ 2-wise
		 * 
		 */
		int patternNumber;
		this.subList = subList;
		this.resList = resList;
		this.actList = actList;
		this.envList = envList;

		int subSize = this.subList.size();
		int resSize = this.resList.size();
		int actSize = this.actList.size();
		int envSize = this.envList.size();

		
		System.out.println("SubSize : "+subSize);
		System.out.println("ResSize : "+resSize);
		System.out.println("ActSize : "+actSize);
		System.out.println("EnvSize : "+envSize);
		
		this.pattern = new BitSet(4);
		this.pattern.clear();

		if(subSize == 0 )
			this.pattern.set(0);
		if(resSize == 0 )
			this.pattern.set(1);
		if(actSize == 0 )
			this.pattern.set(2);
		if(envSize == 0 )
			this.pattern.set(3);
		
		/*
		System.out.println("Pattern 0 : "+this.pattern.get(0));
		System.out.println("Pattern 1: "+this.pattern.get(1));
		System.out.println("Pattern 2: "+this.pattern.get(2));
		System.out.println("Pattern 3: "+this.pattern.get(3));
		*/
		switch ((4-pattern.cardinality())) {
		case 0:
			System.out.println("Pattern 0");
			//Non fare nulla
			break;

		case 1:
			System.out.println("Pattern 1");
			/*
			 *  si generano direttamete le combinazioni
			 *  Abbiamo un solo insieme di valori 
			 */

			if(!this.pattern.get(0)){
				pattern1(this.subList, 0);
				System.out.println("SUBJECT");
			}
			if(!this.pattern.get(1)){
				System.out.println("RESOURCE");
				pattern1(this.resList,1);
			}
			if(!this.pattern.get(2)){
				System.out.println("ACTION");
				pattern1(this.actList,2);
			}
			if(!this.pattern.get(3)){
				System.out.println("ENVIRONMENT");
				pattern1(this.envList,3);
			}
			break;

		case 2:
			System.out.println("Pattern 2");
			/*
			 * si generano direttamente le combinazioni
			 * abbiamo solo due insiemi di valori 
			 */
			patternNumber = Util.patternFor2(getPattern());
			switch (patternNumber) {
			/*
			 * Subject e Resource non vuoti
			 */
			case 1:
				pattern2(this.subList,this.resList);
				break;
				/*
				 * Subject e Action non vuoti
				 */
			case 2:
				pattern2(this.subList,this.actList);
				break;
				/*
				 * Subject e Environment non vuoti
				 */
			case 3:
				pattern2(this.subList,this.envList);
				break;
				/*
				 * Resource e Action non vuoti 
				 */
			case 4:
				pattern2(this.resList,this.actList);
				break;
				/*
				 * Resource e Environment non vuoti
				 */
			case 5:
				pattern2(this.resList,this.envList);
				break;
				/*
				 * Action e Envitonment non vuoti
				 */
			case 6:
				pattern2(this.actList,this.envList);
				break;
			default:
				break;
			}
			break;

		case 3:
			System.out.println("Pattern 3");
			/*
			 * si generano le combinazioni con COMBOTESTER
			 * --> PAIR-WISE
			 * --> 3-WISE
			 * Abbiamo solo 3 insiemi di valori
			 * si possono verificare le seguenti condizioni
			 * 
			 * 1)  Subject, Resource, Action presenti
			 * 2)  Subject, Resource, Environment presenti
			 * 3)  Subject, Action, Environment presenti
			 * 4)  Resource, Action, Environment presenti
			 * 
			 * Per ogni caso manca solo un insieme!!!!
			 */
			patternNumber = Util.patternFor3(this.pattern);
			switch (patternNumber) {
			/*
			 * Subject, Resource, Action presenti
			 */
			case 1:
				pattern3(this.subList, this.resList, this.actList);
				break;
				/*
				 *  2)  Subject, Resource, Environment presenti
				 */
			case 2:
				pattern3(this.subList, this.resList, this.envList);
				break;

				/*
				 *  3)  Subject, Action, Environment presenti
				 */
			case 3:
				pattern3(this.subList, this.actList, this.envList);
				break;

				/*
				 *  4)  Resource, Action, Environment presenti
				 */
			case 4:
				pattern3(this.resList, this.actList, this.envList);
				break;

			default:
				break;
			}
			break;

		default:
			System.out.println("Pattern 4");
			/*
			 * Abbiamo tutti e quattro gli insiemi di valori
			 * si generano le combinazioni con COMBOTESTER
			 * --> PAIR-WISE
			 * --> 3-WISE
			 * --> 4-WISE
			 */
			this.pattern4();
			break;
		}
	}

	private void pattern4() {
		// TODO Auto-generated method stub
		ComboTester comboTester;
		Vector<String> combPairWise;
		Vector<String> combThreeWise;
		Vector<String> combFourWise;
		StringTokenizer tokenizer;
		Combinazione combinazione;
		String[] variableNames = {"Sub","Res","Act","Env"};

		List<?>[] lists = new List[4];
		lists[0] = this.subList;
		lists[1] = this.resList;
		lists[2] = this.actList;
		lists[3] = this.envList;

		String[][] variables = new String[variableNames.length][];
		for (int i = 0; i < variableNames.length; i++) {
			variables[i] = (String[]) lists[i].toArray(new String[0]);
		}        
		try {
			/*
			 * effettuare il pairwise
			 */
			comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 2);
			combPairWise = comboTester.go();
			/*
			 * effettuare il threewise
			 */
			comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 3);
			combThreeWise = comboTester.go();
			/*
			 * effettuare il fourwise
			 */
			comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 4);
			combFourWise = comboTester.go();
			/*
			 * effettuare la differenza insiemistica 
			 */
			combThreeWise.removeAll(combPairWise);
			combFourWise.removeAll(combPairWise);
			combFourWise.removeAll(combThreeWise);
			/*
			 * creare le combinazioni
			 */
			for (String combination : combPairWise) {
				tokenizer = new StringTokenizer(combination, "\t");
				combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
				this.pairWise.add(combinazione);
			}
			for (String combination : combThreeWise) {
				tokenizer = new StringTokenizer(combination, "\t");
				combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
				this.threeWise.add(combinazione);
			}
			for (String combination : combFourWise) {
				tokenizer = new StringTokenizer(combination, "\t");
				combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(),tokenizer.nextToken());
				this.fourWise.add(combinazione);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pattern3(List<String> list1, List<String> list2, List<String> list3) {
		// TODO Auto-generated method stub
		/*
		System.out.println("Si invoca comboTester per generare il pairWise di tre insiemi");
		System.out.println("Si invoca comboTester per generare il ThreeWise di tre insiemi");
		System.out.println("si setta la variabile di istanza PairWise con il primo insieme generato da ComboTester");
		System.out.println("si setta la vriabile di istanza TreeWise con la differenza degli insiemi generati da comboTester");
		*/
		try {
			ComboTester comboTester;
			Vector<String> combPairWise;
			Vector<String> combThreeWise;
			StringTokenizer tokenizer;
			Combinazione combinazione;
			String[] variableNames = new String[3];
			List<?>[] lists = new List[3];
			lists[0] = list1;
			lists[1] = list2;
			lists[2] = list3;
			String[][] variables = new String[3][];
			for (int i = 0; i < 3; i++) {
				variables[i] = (String[]) lists[i].toArray(new String[0]);
			}        
			int patternNumber = Util.patternFor3(this.pattern);
			switch (patternNumber) {
			/*
			 * Subject, Resource, Action presenti
			 */
			case 1:
				System.out.println("****###   Sub Res Act   ###****");
				variableNames[0] = "Sub";
				variableNames[1] = "Res";
				variableNames[2] = "Act";
				/*
				 * effettuare il pairwise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 2);
				combPairWise = comboTester.go();
				/*
				 * effettuare il threewise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 3);
				combThreeWise = comboTester.go();
				/*
				 * effettuare la differenza insiemistica 
				 */
				combThreeWise.removeAll(combPairWise);
				for (String combination : combPairWise) {
					tokenizer = new StringTokenizer(combination, "\t");
					combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(), null);
					this.pairWise.add(combinazione);
				}
				for (String combination : combThreeWise) {
					tokenizer = new StringTokenizer(combination, "\t");
					combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(), null);
					this.threeWise.add(combinazione);
				}
				break;
				/*
				 *  2)  Subject, Resource, Environment presenti
				 */
			case 2:
				System.out.println("****###   Sub Res Env   ###****");
				variableNames[0] = "Sub";
				variableNames[1] = "Res";
				variableNames[2] = "Env";
				/*
				 * effettuare il pairwise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 2);
				combPairWise = comboTester.go();
				/*
				 * effettuare il threewise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 3);
				combThreeWise = comboTester.go();
				/*
				 * effettuare la differenza insiemistica 
				 */				
				combThreeWise.removeAll(combPairWise);

				for (String combination : combPairWise) {
					//System.out.println(combination);
					tokenizer = new StringTokenizer(combination, "\t");
					//System.out.println("Tokenizer : "+tokenizer.countTokens());
					combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), null, tokenizer.nextToken());
					//System.out.println(combinazione.toString());
					this.pairWise.add(combinazione);
				}
				for (String combination : combThreeWise) {
					tokenizer = new StringTokenizer(combination, "\t");
					combinazione = new Combinazione(tokenizer.nextToken(), tokenizer.nextToken(), null, tokenizer.nextToken());
					this.threeWise.add(combinazione);
				}
				break;
				/*
				 *  3)  Subject, Action, Environment presenti
				 */
			case 3:
				System.out.println("****###   Sub Act Env   ###****");
				variableNames[0] = "Sub";
				variableNames[1] = "Act";
				variableNames[2] = "Env";
				/*
				 * effettuare il pairwise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 2);
				combPairWise = comboTester.go();
				/*
				 * effettuare il threewise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 3);
				combThreeWise = comboTester.go();
				/*
				 * effettuare la differenza insiemistica 
				 */
				combThreeWise.removeAll(combPairWise);
				for (String combination : combPairWise) {
					tokenizer = new StringTokenizer(combination, "\t");
					combinazione = new Combinazione(tokenizer.nextToken(), null, tokenizer.nextToken(), tokenizer.nextToken());
					this.pairWise.add(combinazione);
				}
				for (String combination : combThreeWise) {
					tokenizer = new StringTokenizer(combination, "\t");
					combinazione = new Combinazione(tokenizer.nextToken(), null, tokenizer.nextToken(), tokenizer.nextToken());
					this.threeWise.add(combinazione);
				}
				break;
				/*
				 *  4)  Resource, Action, Environment presenti
				 */
			case 4:
				System.out.println("****###   Res Act Env   ###****");
				variableNames[0] = "Res";
				variableNames[1] = "Act";
				variableNames[2] = "Env";
				/*
				 * effettuare il pairwise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 2);
				combPairWise = comboTester.go();
				/*
				 * effettuare il threewise
				 */
				comboTester = new ComboTester(this.url, this.username, this.password, this.driver, variableNames, variables, 3);
				combThreeWise = comboTester.go();
				
				/*
				 * effettuare la differenza insiemistica 
				 */
				combThreeWise.removeAll(combPairWise);
				//System.out.println("combThreeWise.removeAll(combPairWise).size : "+combThreeWise.size());
				for (String combination : combPairWise) {
					//System.out.println(combination);
					tokenizer = new StringTokenizer(combination, "\t");
					//System.out.println("Tokenizer : "+tokenizer.countTokens());
					combinazione = new Combinazione(null, tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
					//System.out.println(combinazione.toString());
					this.pairWise.add(combinazione);
				}
				//System.out.println("Quanti 2-wise = "+combPairWise.size());
				//System.out.println("#######  3-wise    #######");
				for (String combination : combThreeWise) {
					//System.out.println(combination);
					tokenizer = new StringTokenizer(combination, "\t");
					//System.out.println("Tokenizer : "+tokenizer.countTokens());
					combinazione = new Combinazione(null, tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
					this.threeWise.add(combinazione);
				}
				//System.out.println("Quanti 3-wise = "+combThreeWise.size());
				break;
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BitSet getPattern() {
		return this.pattern;
	}

	public void setPattern(BitSet pattern) {
		this.pattern = pattern;
	}


	public void pattern2(List<String> list1, List<String> list2){
		/*
		 * genera direttamente le combinazioni
		 */
		this.pairWise = new Vector<Combinazione>();
		Combinazione combinazione = null;
		int patternNumber  = Util.patternFor2(getPattern());
		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				switch (patternNumber) {
				/*
				 * Subject e Resource non vuoti
				 */
				case 1:
					combinazione = new Combinazione((String)list1.get(i), (String)list2.get(j), null, null);
					break;
					/*
					 * Subject e Action non vuoti
					 */
				case 2:
					combinazione = new Combinazione((String)list1.get(i), null, (String)list2.get(j), null);
					break;
					/*
					 * Subject e Environment non vuoti
					 */
				case 3:
					combinazione = new Combinazione((String)list1.get(i), null, null,(String)list2.get(j));
					break;
					/*
					 * Resource e Action non vuoti 
					 */
				case 4:
					combinazione = new Combinazione(null, (String)list1.get(i),(String)list2.get(j), null);
					break;
					/*
					 * Resource e Environment non vuoti
					 */
				case 5:
					combinazione = new Combinazione(null,(String)list1.get(i),  null,(String)list2.get(j));
					break;
					/*
					 * Action e Envitonment non vuoti
					 */
				case 6:
					combinazione = new Combinazione(null, null,(String)list1.get(i), (String)list2.get(j));
					break;
				default:
					break;
				}

				this.pairWise.add(combinazione);
			}
		}
	}

	public void pattern1(List<String> list, int i) {
		// TODO Auto-generated method stub
		/*
		 * genera direttamente le tuple 
		 * 
		 */
		this.oneWise = new Vector<Combinazione>();
		//System.out.println("Patterni 1 : valore i = "+i);
		switch (i) {
		case 0:
			for (int j = 0; j < list.size(); j++) {
				this.oneWise.add(new Combinazione((String) list.get(j), null, null, null));
			}
			break;

		case 1:
			for (int j = 0; j < list.size(); j++) {
				this.oneWise.add(new Combinazione(null, (String) list.get(j), null, null));
			}
			break;

		case 2:
			for (int j = 0; j < list.size(); j++) {
				this.oneWise.add(new Combinazione(null, null, (String) list.get(j), null));
			}
			break;

		case 3:
			for (int j = 0; j < list.size(); j++) {
				this.oneWise.add(new Combinazione(null, null, null, (String) list.get(j)));
			}
			break;

		default:
			break;
		}
	}

	public Vector<Combinazione> getOneWise() {
		return this.oneWise;
	}

	public void setOneWise(Vector<Combinazione> oneWise) {
		this.oneWise = oneWise;
	}

	public void setPairWise(Vector<Combinazione> pairWise) {
		this.pairWise = pairWise;
	}

	public void setThreeWise(Vector<Combinazione> threeWise) {
		this.threeWise = threeWise;
	}

	public void setFourWise(Vector<Combinazione> fourWise) {
		this.fourWise = fourWise;
	}

	public Vector<Combinazione> getPairWise(){
		return this.pairWise;
	}

	public Vector<Combinazione> getThreeWise(){
		return this.threeWise;
	}

	public Vector<Combinazione> getFourWise(){
		return this.fourWise;
	}
}
