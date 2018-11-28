package it.cnr.isti.labse.xcreate.util;

import java.util.BitSet;

public class Util {

	public static int patternFor2(BitSet pattern){
		/*
		 * Subject e Resource non vuoti
		 */
		if(!pattern.get(0) && !pattern.get(1))
			return 1;
		/*
		 * Subject e Action non vuoti
		 */
		if(!pattern.get(0) && !pattern.get(2))
			return 2;
		/*
		 * Subject e Environment non vuoti
		 */
		if(!pattern.get(0) && !pattern.get(3))
			return 3;
		/*
		 * Resource e Action non vuoti 
		 */
		if(!pattern.get(1) && !pattern.get(2))
			return 4;
		/*
		 * Resource e Environment non vuoti
		 */
		if(!pattern.get(1) && !pattern.get(2))
			return 5;
		/*
		 * Action e Envitonment non vuoti
		 */
		return 6;

	}

	public static int patternFor3(BitSet pattern){
		//System.out.println("Pattern.toString ::. "+pattern.toString());
		/*
		 * Subject, Resource, Action presenti
		 */
		if(!pattern.get(0) && !pattern.get(1) && !pattern.get(2))
			return 1;
		/*
		 *  2)  Subject, Resource, Environment presenti
		 */
		if(!pattern.get(0) && !pattern.get(1) && !pattern.get(3))
			return 2;
		/*
		 *  3)  Subject, Action, Environment presenti
		 */
		if(!pattern.get(0) && !pattern.get(2) && !pattern.get(3))
			return 3;
		/*
		 *  4)  Resource, Action, Environment presenti
		 */
		return 4;
	}
	
	public static int patternForRequestGenerator(BitSet pattern){
		// zero insiemi --- case 0 
		if(pattern.get(0)&& pattern.get(1)&& pattern.get(2)&& pattern.get(3))
			return 0;
		
		/**
		 * abbiamo un solo insieme
		 */
		// subject --- case 1
		if(!pattern.get(0) && pattern.get(1)&& pattern.get(2)&& pattern.get(3))
			return 1;
		// resource --- case 2
		if(pattern.get(0) && !pattern.get(1)&& pattern.get(2)&& pattern.get(3))
			return 2;
		// action --- case 3
		if(pattern.get(0) && pattern.get(1) && !pattern.get(2)&& pattern.get(3)){
			/*
			System.out.println((!pattern.get(0)&& !pattern.get(1)&& pattern.get(2)&& !pattern.get(3)));
			System.out.println(pattern.get(0));
			System.out.println(pattern.get(1));
			System.out.println(pattern.get(2));
			System.out.println(pattern.get(3));
			*/
			return 3;
			
		}
		// environment --- case 4
		if(pattern.get(0) && pattern.get(1) && pattern.get(2) && !pattern.get(3)){
			/*
			System.out.println(!pattern.get(0)&& !pattern.get(1)&& !pattern.get(2)&& pattern.get(3));
			System.out.println(!pattern.get(0));
			System.out.println(!pattern.get(1));
			System.out.println(!pattern.get(2));
			System.out.println(!pattern.get(3));
			*/
			return 4;
		}
		/**
		 * abbiamo due insiemi
		 */
		// sub res --- case 5
		if(!pattern.get(0)&& !pattern.get(1)&& pattern.get(2)&& pattern.get(3))
			return 5;
		// sub act --- case 6
		if(!pattern.get(0) && pattern.get(1) && !pattern.get(2)&& pattern.get(3))
			return 6;
		// sub env --- case 7
		if(!pattern.get(0 )&& pattern.get(1)&& pattern.get(2)&& !pattern.get(3))
			return 7;
		// res act --- case 8
		if(pattern.get(0) && !pattern.get(1) && !pattern.get(2)&& pattern.get(3))
			return 8;
		// res env --- case 9
		if(pattern.get(0) && !pattern.get(1) && pattern.get(2) && !pattern.get(3))
			return 9;
		// act env --- case 10
		if(pattern.get(0) && pattern.get(1) && !pattern.get(2) && !pattern.get(3))
			return 10;
		/**
		 * abbiamo tre insiemi
		 */
		// sub res act --- case 11
		if(!pattern.get(0) && !pattern.get(1) && !pattern.get(2)&& pattern.get(3))
			return 11;
		// sub res env --- case 12
		if(!pattern.get(0) && !pattern.get(1) && pattern.get(2) && !pattern.get(3)){
			/*
			System.out.println(!pattern.get(0) && !pattern.get(1)&& pattern.get(2)&& !pattern.get(3));
			System.out.println(pattern.get(0));
			System.out.println(pattern.get(1));
			System.out.println(pattern.get(2));
			System.out.println(pattern.get(3));
			*/
			return 12;
		}
		// sub act env --- case 13
		if(!pattern.get(0) && pattern.get(1) && !pattern.get(2) && !pattern.get(3))
			return 13;
		// res act env --- case 14
		if(pattern.get(0) && !pattern.get(1) && !pattern.get(2) && !pattern.get(3))
			return 14;
		/**
		 * abbiamo 4 insiemi --- case 15
		 */
		return 15;
	}
	
	public static boolean isANumber(String value){
		boolean isNumber = true;
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int charToAscii = chars[i];
			if(charToAscii < 48 || charToAscii > 57){
				return false;
			}
				
		}
		return isNumber;
	}
}
