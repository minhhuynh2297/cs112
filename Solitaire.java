package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() { //Correct
		CardNode current = new CardNode();
		int temp = 0; 
		if(this.deckRear.cardValue==27){
			temp=deckRear.cardValue; 
			deckRear.cardValue=deckRear.next.cardValue;
			deckRear.next.cardValue=temp; 
		}
		else{
			current=deckRear.next;
			while(current.cardValue!=27){
				current=current.next; 
			}
			temp=current.next.cardValue; 
			current.next.cardValue=current.cardValue;
			current.cardValue=temp;
		}
		
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() { //Correct
		CardNode current = new CardNode();
		int temp = 0; 
		if(this.deckRear.cardValue==28){
			temp=deckRear.cardValue;
			deckRear.cardValue=deckRear.next.cardValue;
			deckRear.next.cardValue=temp; 
			temp=deckRear.next.cardValue;
			deckRear.next.cardValue=deckRear.next.next.cardValue;
			deckRear.next.next.cardValue=temp; 
		}
		else{
			current=deckRear.next;
			while(current.cardValue!=28){
				current=current.next; 
			}
			temp=current.cardValue; 
			current.cardValue=current.next.cardValue;
			current.next.cardValue=temp; 
			temp=current.next.cardValue;
			current.next.cardValue=current.next.next.cardValue;
			current.next.next.cardValue=temp; 
		}
		
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */ 
	void tripleCut() { //Correct
		CardNode current=new CardNode(); 
		if(deckRear.next.cardValue==27){ //Works
			current = deckRear.next;
			while(current.cardValue!=28){
				current=current.next;
			}
			deckRear=current; 
		}
		else if(deckRear.next.cardValue==28){ //Works
			current = deckRear.next;
			while(current.cardValue!=27){
				current=current.next;
			}
			deckRear=current; 
		}
		else if(deckRear.cardValue==27){
			current=deckRear.next;
			while(current.next.cardValue!=28){
				current=current.next;
			}
			deckRear=current;
		}
		else if(deckRear.cardValue==28){
			current=deckRear.next;
			while(current.next.cardValue!=27){
				current=current.next;
			}
			deckRear=current;
		}
		else{
			current=deckRear.next;
			CardNode front=deckRear.next;
			CardNode temp=new CardNode(); 
			CardNode jokerOne=new CardNode(); 
			CardNode previous=new CardNode(); 
			while(current.cardValue!=27 && current.cardValue!=28){
				previous=current; 
				current=current.next; 
			}
			if(current.cardValue==28){
				jokerOne=current;
				while(current.cardValue!=27){
					current=current.next;
				}
				temp=current.next; 
				current.next=front;
				deckRear.next=jokerOne; 
				deckRear=previous;
				deckRear.next=temp; 
			}
			else{
				jokerOne=current;
				while(current.cardValue!=28){
					current=current.next;
				}
				temp=current.next; 
				current.next=front;
				deckRear.next=jokerOne; 
				deckRear=previous;
				deckRear.next=temp; 
			}
		}
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() { 
		CardNode prevRear=deckRear;
 		CardNode move=deckRear.next;
		CardNode temp=new CardNode(); 
		int k=0;
		while(prevRear.next!=deckRear){
			prevRear=prevRear.next;
		}
		if(deckRear.cardValue==28 || deckRear.cardValue==27 || deckRear.cardValue==26){
			return;
		}
		else{
			k=deckRear.cardValue; 
		}
		for(int i=1; i<k; i++){
			move=move.next; 
		}
		temp=move.next; 
		move.next=deckRear;
		prevRear.next=deckRear.next; 
		deckRear.next=temp; 
		
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() { //Correct
		int key=0;
		jokerA();
		jokerB();
		tripleCut(); 
		countCut();
		int value=deckRear.next.cardValue;
		CardNode move=deckRear.next;
			for(int i=1; i<value; i++){
				move=move.next; 
			}
			if(move.next.cardValue==27 || move.next.cardValue==28){
				getKey();
			}
				key=move.next.cardValue;
				return key; 
			}

	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */ 
	public String encrypt(String message) {	
		char ch;
		int c; 
		int key; 
		String upperCase="";
		String encrypted=""; 
		for(int i=0; i<message.length(); i++){ //Changes entire string to capital letters. Correct
			if(Character.isLetter(message.charAt(i))){
				upperCase=upperCase+Character.toUpperCase(message.charAt(i));
			}
		}
		for(int j=0; j<upperCase.length(); j++){ //Encrypts the code using getKey(); 
			ch=upperCase.charAt(j);
			c=ch-'A'+1; 
			key=getKey();  
			if(c+key>26){
				c=(c+key)-26;
				ch=(char)(c-1+'A');
				encrypted=encrypted+ch; 
			}
			else{
				c=c+key; 
				ch=(char)(c-1+'A');
				encrypted=encrypted+ch; 
			}
		}
	    return encrypted;
		}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		char ch;
		int c;
		int key; 
		String decrypted="";
		for(int i=0; i<message.length(); i++){
			key=getKey(); 
			ch=message.charAt(i);
			c=ch-'A'+1;
			if(c-key<0){
				key=key-c; 
				c=26-key; 
			}
			else{
				c=c-key;
			}
			ch=(char)(c-1+'A');
			decrypted=decrypted+ch; 
		}
	    return decrypted;
	}
}
