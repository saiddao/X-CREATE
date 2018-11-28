package it.cnr.isti.labse.xcreate.util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.List;

public class PowerSet {
	
	private Hashtable<Integer, HashSet<Integer>> power;
	private String nomeInsieme;
	Hashtable<Integer, HashSet<Integer>> insiemePartiDaRestituire;
	
	/**
	 * 
	 * @param nomeInsieme
	 */
	public PowerSet(String nomeInsieme){
		new ArrayList<Integer>();
		this.power = new Hashtable<Integer, HashSet<Integer>>();//LinkedHashSet di LinkedHashSets
		this.nomeInsieme = nomeInsieme;
		System.out.println(nomeInsieme);
	}
	
	/**
	 * 
	 * Returns the power set from the given set
     * Example: S = {a,b,c}
     * P(S) = {[], [c], [b], [b, c], [a], [a, c], [a, b], [a, b, c]}
     *  
     * @param set String[]
	 * @param min
	 * @param max
	 * @return {@link Hashtable<Integer, HashSet<Integer>>}
	 */
	public Hashtable<Integer, HashSet<Integer>> powerset(List<Integer> set, int min, int max){//(String[] set) {
		   this.insiemePartiDaRestituire = new Hashtable<Integer, HashSet<Integer>>();
		// contiene l'insieme delle parti
			Hashtable<Integer, HashSet<HashSet<Integer>>> insiemeParti = new Hashtable<Integer, HashSet<HashSet<Integer>>>(); 
			HashSet<HashSet<Integer>> setOfSets = new HashSet<HashSet<Integer>>();
			// contiene l'insieme di partenza
			int countSet = 1;
			for (int i = 0; i < set.size(); i++) {
				HashSet<Integer> insiemeUno = new HashSet<Integer>();
				insiemeUno.add(set.get(i));
				setOfSets.add(insiemeUno);
				this.insiemePartiDaRestituire.put(countSet++, insiemeUno);
			}
			System.out.println("numero di elementi nell'insieme : "+setOfSets.size());
			System.out.println(setOfSets.toString());
			insiemeParti.put(1, setOfSets);
			// calcolo degli insiemi di 2, 3, ... , max elementi
			
			System.err.println(this.nomeInsieme+" : PowerSET : "+max);
			
			for (int i = 2; i <= max; i++){
				HashSet<HashSet<Integer>> insiemiConIElementi = new HashSet<HashSet<Integer>>();
				HashSet<HashSet<Integer>> insiemiConIMenoUnoElementi = insiemeParti.get(i-1);
				int count = 0, countTot = 0;
				Object[] interiConIMenoUnoElementi = insiemiConIMenoUnoElementi.toArray();
				Object[] interiConIMenoUnoElementiInterno = new Object[interiConIMenoUnoElementi.length];
				System.arraycopy(interiConIMenoUnoElementi, 0, interiConIMenoUnoElementiInterno, 0, interiConIMenoUnoElementi.length);
				System.out.println("Insiemi con "+(i-1)+" Elementi : "+interiConIMenoUnoElementiInterno.length);			
				for (int j = 0; j < interiConIMenoUnoElementi.length; j++) {
					for (int j2 = j+1; j2 < interiConIMenoUnoElementiInterno.length; j2++) {
						HashSet<Integer> insiemeFusione = new HashSet<Integer>();
						insiemeFusione.addAll((HashSet<Integer>)interiConIMenoUnoElementi[j]);
						insiemeFusione.addAll((HashSet<Integer>)interiConIMenoUnoElementiInterno[j2]);
						if(insiemeFusione.size() > i)
							count++;
						if(insiemeFusione.size() == i){
							insiemiConIElementi.add(insiemeFusione);
						}
						countTot++;
					}
				}
				System.out.println(count+" : "+ countTot);
				System.out.println(i+" :  insiemiConIElementi : "+insiemiConIElementi.size());
				insiemeParti.put(i, insiemiConIElementi);
				for (HashSet<Integer> insieme : insiemiConIElementi) {
					this.insiemePartiDaRestituire.put(countSet++, insieme);	
				}
				
			}
			
			Set<Integer> keys = insiemeParti.keySet();
			int insiemePartiInteger = 0;
			for (Integer key : keys) {
				System.out.println(key+" : "+insiemeParti.get(key).size());
				insiemePartiInteger += insiemeParti.get(key).size();
			}
			System.out.println("insiemePartiInteger -> "+insiemePartiInteger);

	       System.out.println(this.insiemePartiDaRestituire.values().toString());
	       
	       this.power = this.insiemePartiDaRestituire;
	       return power;
	   }
}





















































//package it.cnr.isti.labse.xcreate.util;
//import java.util.ArrayList;
////import java.util.HashSet;
////import java.util.Iterator;
//import java.util.HashSet;
//import java.util.Hashtable;
////import java.util.LinkedHashSet;
//import java.util.List;
//
///**
// * @author ARGENIX
// * in input ho List<String> o ArrayList<String>
// */
//public class PowerSet {
//	
//	private List<Integer> set;//LAVORARE CON GLI INT?
//	//public static List<String> unaProva;
//	//private int minimo = 1;
//	//private int massimo;
//	private Hashtable<Integer, HashSet<Integer>> power;
//	
//	public PowerSet(){
//		this.set = new ArrayList<Integer>();
//		this.power = new Hashtable<Integer, HashSet<Integer>>();//LinkedHashSet di LinkedHashSets
//	}
//	   /**
//	     * Returns the power set from the given set by using a binary counter
//	     * Example: S = {a,b,c}
//	     * P(S) = {[], [c], [b], [b, c], [a], [a, c], [a, b], [a, b, c]}
//	     * @param set String[]
//	     * @return LinkedHashSet
//	     */
//	   public Hashtable<Integer, HashSet<Integer>> powerset(List<Integer> set, int min, int max){//(String[] set) {
//	     
//	       //create the empty power set
//		   //LinkedHashSet power = new LinkedHashSet();
//		   
//	       //get the number of elements in the set
//	       int elements = set.size();//set.length;
//	       
//			System.out.println("######## "+set.size());
//
//	      
//	       //the number of members of a power set is 2^n
//	       int powerElements = (int) Math.pow(2,elements);
//	       int count = 1;
//	       //run a binary counter for the number of power elements
//	       for (int i = 0; i < powerElements; i++) {
//	         
//	           //convert the binary number to a string containing n digits
//	           String binary = intToBinary(i, elements);
//	         
//	           //create a new set
//	           HashSet<Integer> innerSet = new HashSet<Integer>();
//	       
//	           //convert each digit in the current binary number to the corresponding element
//	            //in the given set
//	           for (int j = 0; j < binary.length(); j++) {
//	               if (binary.charAt(j) == '1')
//	                   //innerSet.add(set[j]);
//	            	   innerSet.add(set.get(j));//per via che e' List<String>
//	           }
//	         
//	           //add the new set to the power set
//	           if (innerSet.size() >= min && innerSet.size()<=max){
//	        	   power.put(count, innerSet);//solo se innerSet.size e' compreso tra min e max...
//	        	   count++;
//	           }
//	       }
////	       System.out.println(power.size() + "ho fatto un powerset :)");
//	//       System.out.println(power.toString());	     
//	       return power;
//	   }
//	 
//	   /**
//	     * Converts the given integer to a String representing a binary number
//	     * with the specified number of digits
//	     * For example when using 4 digits the binary 1 is 0001
//	     * @param binary int
//	     * @param digits int
//	     * @return String
//	     */
//	   private String intToBinary(int binary, int digits) {
//
//		   String temp = Integer.toBinaryString(binary);
//		   int foundDigits = temp.length();
//
//		   StringBuffer pad = new StringBuffer(digits);
//
//		   for (int i = foundDigits; i < digits; i++) {
//		           pad.append('0');
//		   }
//
//		   pad.append(temp);
//
//		   return pad.toString();
//		}
//	   
//}