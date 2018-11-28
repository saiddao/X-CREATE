package it.cnr.isti.labse.xcreate.sql;

import it.cnr.isti.labse.xcreate.policyAnalyzer.ChildrenSet;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Condition;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Function;
import it.cnr.isti.labse.xcreate.policyAnalyzer.Nodo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class AntenatoDiscendente {


	public static Hashtable<Integer, Vector<Integer>> make(Nodo root) {
		Hashtable<Integer, Vector<Integer>> chiusuraHash = new Hashtable<Integer, Vector<Integer>>();
		// TODO Auto-generated method stub
		Stack<Object> pila = new Stack<Object>();
		root.setRed(true);
		pila.push(root);
		// calcolo della chiusura del nodo radice
		while(!pila.isEmpty()){
//			System.out.println(" contenuto pila (prima): "+pila.toString());
			Object obj = pila.pop();
//			System.out.println(" Nodo prelevato ora : "+obj.toString());
//			System.out.println(" contenuto pila (dopo): "+pila.toString());
			// NODO GENERICO
			if(obj instanceof Nodo){
				 // non e' stata calcolata la chiusura del nodo generico
				if(((Nodo)obj).isRed()){
					((Nodo)obj).setRed(false);
					pila.push(obj);
					if(((Nodo)obj).hasCondition()){
						Condition cond = ((Nodo)obj).getCondition();
						cond.setRed(true);
						pila.push(cond);
					}
					ChildrenSet children = ((Nodo)obj).getChildrenSet();
					Enumeration<String> keys = children.getKeys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						Nodo child = children.getChild(key); 
						child.setRed(true);
						pila.push(child);
					}
				}
				// 	e' stata calcolata la chiusura del nodo generico 
				else{
					Vector<Integer> myClosure = new Vector<Integer>();
					myClosure.add(new Integer(((Nodo)obj).getPkNodo()));
					if(((Nodo)obj).hasCondition()){
						Condition cond = ((Nodo)obj).getCondition();
						///////////////////////////////////////////////
	//					System.out.println(this.chiusuraHash.size());
		//				System.out.println(this.chiusuraHash.toString());
			//			System.out.println(cond.getMyId());
						//////////////////////////////////////////////////
						myClosure.addAll(chiusuraHash.get(new Integer(cond.getPkNodo())) );
					}
					ChildrenSet children = ((Nodo)obj).getChildrenSet();
					Enumeration<String> keys = children.getKeys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						myClosure.addAll(chiusuraHash.get((children.getChild(key)).getPkNodo()));
					}
					chiusuraHash.put(new Integer(((Nodo)obj).getPkNodo()), myClosure);
				}
			}
			// NODO CONDITION
			if(obj instanceof Condition){
				// non e' stata calcolata la chiusura del nodo Condition
				if(((Condition)obj).isRed()){
					((Condition)obj).setRed(false);
					//////////////////////////////////////////////////
				//	System.out.println("In condition : "+((Condition)obj).getMyId());
					//////////////////////////////////////////////////
					pila.push(obj);
					Function fun = ((Condition)obj).getChild();
					fun.setRed(true);
					pila.push(fun);
				}
				// e' stata calcolata la chiusura del nodo condition
				else{
					Vector<Integer> myClosure = new Vector<Integer>();
					myClosure.add(new Integer(((Condition)obj).getPkNodo()));
					Function fun = ((Condition)obj).getChild();
					myClosure.addAll(chiusuraHash.get(fun.getPkNodo()));
					chiusuraHash.put(new Integer(((Condition)obj).getPkNodo()), myClosure);
				}
			}

			// NODO FUNCTION
			if(obj instanceof Function){
				// non e' stata calcolata la chiusura del nodo Function
				if(((Function)obj).isRed()){
					((Function)obj).setRed(false);
					pila.push(obj);
					Vector<Object> args = ((Function)obj).getArgomenti();
					for (Object arg : args) {
						if(arg instanceof Function){
							((Function)arg).setRed(true);
							pila.push(arg);
						}
					}
				}
				// e' stata calcolata la chiusura del nodo Function
				else {
					Vector<Integer> myClosure = new Vector<Integer>();
					myClosure.add( new Integer(((Function)obj).getPkNodo()) );
					Vector<Object> args = ((Function)obj).getArgomenti();
					for (Object arg : args) {
						if(arg instanceof Function){
							myClosure.addAll(chiusuraHash.get(((Function)arg).getPkNodo()));
						}
					}
					chiusuraHash.put(new Integer(((Function)obj).getPkNodo()), myClosure);
				}
			}
		}
		return chiusuraHash;
	}
	
	/*
	public void print(){
		Enumeration<Integer> keys = this.chiusuraHash.keys();
		while (keys.hasMoreElements()) {
			Integer key = (Integer) keys.nextElement();
			Vector<Integer> closure = this.chiusuraHash.get(key);
			System.out.println("########################################");
			System.out.println("Nodo : "+key.intValue());
			System.out.println(" la sua chiusura ");
			for (Integer integer : closure) {
				System.out.println(" -- "+integer.intValue());
			}
			System.out.println("########################################");
		}
	}*/
}
