package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	

	public HashMap<String,Occurrence> loadKeyWords(String docFile) throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		FileNotFoundException a = new FileNotFoundException(); 
		if(docFile.length()==0){
			throw a; 
		}
		
		HashMap<String, Occurrence> finished = new HashMap<String, Occurrence>(); 
		int i=0;
		
		while(i<docFile.length()-1){
			Occurrence y;
			String word="";
			String fixed=""; 
			while(docFile.charAt(i)!=' '){ //Create the word in its entirety until there is a space
				word=word+docFile.charAt(i);
				if(i<docFile.length()-1){
					i++;
				}
				else{
					break;
				}
			}
			
			if(docFile.charAt(i)==' ' || i==docFile.length()-1){ //Reaches a space
				fixed=getKeyWord(word); //Creates a proper word or returns null
				//System.out.println(fixed);
			}
		
			if(word!=null){
				if(!finished.containsKey(word)){ //First occurrence of the word in this document
					y=new Occurrence(docFile, 1); 
					finished.put(fixed, y); 
					//System.out.println("HEY:"+finished.keySet().toString());
					//System.out.println(finished.size());
				}
				
				else{ //Increments the occurrence of the word for this document
					finished.get(fixed).frequency=finished.get(fixed).frequency+1; 
				}	
			}
			
			while(docFile.charAt(i)==' '){//Increment i until reaches new word
				i++;
			}
		}
		
		//System.out.println("HEY:"+finished.keySet().toString());
		return finished; 
}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		for(String key:kws.keySet()){
			ArrayList<Occurrence> a=new ArrayList<Occurrence>(); 
			if(keywordsIndex.containsKey(key)){
				a=keywordsIndex.get(key); 
				a.add(kws.get(key));
				keywordsIndex.put(key, a); 
				insertLastOccurrence(keywordsIndex.get(key)); 
			}
			else{
				a.add(kws.get(key)); 
				keywordsIndex.put(key, a);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		String noPunct="";
		innerLoop: 
		for(int i=0; i<word.length(); i++){
			if(Character.isAlphabetic(word.charAt(i))){ //word until first punctuation. 
				noPunct=noPunct+word.charAt(i);
			}
			else{ //if punctuation, check if only at the end, or in the middle of the word.
				int j=i+1;
				int count=0; 
				while(j<word.length()){
					if(Character.isAlphabetic(word.charAt(j))){
						count++; 
						j++;
					}
					else{
						j++;
					}
				}
				if(count>0){
					return null;
				}
				else{
					break innerLoop; 
				}
			}
		}
		 if(noiseWords.containsKey(noPunct)){ //check if noiseWord
			 return null;
		 }
		return noPunct;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	
	private int reverseBinarySearch(int[] a, int insert, int min, int max, ArrayList<Integer> midpoints){ // searches the array
		//returns 0 if location has the same insert. returns 1 if location has greater value
		//returns 2 if the location has lower value. We will insert depending on what it returns
			if(min>max){
				return -1;
			}
		int mid=(max+min)/2;
		midpoints.add(mid);
			if(a[mid]==insert){
				return 0;
			}
			else if(min==max){
				if(a[min]>insert){
				return 1;
			}
			else{
				return 2;
			}
		}
			else if(a[mid]<insert){
				return reverseBinarySearch(a, insert, min, mid-1, midpoints);
			}
			else{
				return reverseBinarySearch(a, insert, mid+1, max, midpoints);
			}
	}
	
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int size=occs.size(); //size of occs after removing the last object.
		ArrayList<Integer> finished=new ArrayList<Integer>(); 
		int[] occurrences=new int[occs.size()-1];
		for(int i=0; i<occs.size()-1; i++){ //Fill up the array, except for the last occurence, which is the one to be inserted in descending order
			occurrences[i]=occs.get(i).frequency;
		}
		
		int insert=occs.get(occs.size()-1).frequency; //The frequency to insert
		int decision=reverseBinarySearch(occurrences, insert, 0, occurrences.length-1, finished); 
		int location=finished.get(finished.size()-1); //Where to insert
		Occurrence insertion=occs.remove(occs.size()-1); //The Occurrence object to insert and removes it from the list
		size=occs.size(); //size of occs after removing the last object.
		ArrayList<Occurrence> temp=new ArrayList<Occurrence>(); 
		
		loop: 
		for(int i=0; i<size; i++){
			if(i!=location){
				temp.add(occs.get(i));
			}
			else{
				if(decision==0){ //order doesn't matter
					temp.add(insertion);
					temp.add(occs.get(i));
					i=i+1;
					while(i<occs.size()-1){
						temp.add(occs.get(i));
					}
					break loop; 
				}
				else if(decision==1){ //insertion is smaller. put in the greater one first
					temp.add(occs.get(i));
					temp.add(insertion);
					while(i<occs.size()-1){
						temp.add(occs.get(i));
					}
					break loop; 
				}
				else if(decision==2){ //insertion is greater. put in the greater one first
					temp.add(insertion);
					temp.add(occs.get(i)); 
					while(i<occs.size()-1){
						temp.add(occs.get(i));
					}
					break loop; 
				}
			}
		}
		occs.clear();
		
		for(int j=0; j<temp.size(); j++){
			occs.add(temp.get(j));
		}
		return 	finished;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<String> finished=new ArrayList<String>();
		
		if(!keywordsIndex.containsKey(kw1) || !keywordsIndex.containsKey(kw2)){ //check if it even has both words
			if(!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
				return null;
			}
			else if(keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2)){
				for(int i=0; i<keywordsIndex.get(kw1).size() && i<5 ; i++){
					finished.add(keywordsIndex.get(kw1).get(i).document); 
				}
			}
			else if(!keywordsIndex.containsKey(kw1) && keywordsIndex.containsKey(kw2)){
				for(int i=0;i<keywordsIndex.get(kw2).size() && i<5 ; i++){
					finished.add(keywordsIndex.get(kw2).get(i).document); 
				}
			}
		}
		
		else{
			String document1;
			String document2;
			int a; 
			if(keywordsIndex.get(kw1).size()<keywordsIndex.get(kw2).size()){
				a=keywordsIndex.get(kw1).size();
			}
			else{
				a=keywordsIndex.get(kw2).size();
			}
			loop: 
				for(int i=0; i<a; i++){
					document1=keywordsIndex.get(kw1).get(i).document;
					document2=keywordsIndex.get(kw2).get(i).document;
					int freq1=keywordsIndex.get(kw1).get(i).frequency;
					int freq2=keywordsIndex.get(kw2).get(i).frequency;
					
					if(finished.size()==5){
						break loop; 
					}
					else if(finished.size()>5){
						while(finished.size()!=5){
							finished.remove(finished.size()-1); 
						}
						break loop; 
					}
					
					if(document1.compareTo(document2)==0){ //Same document. Choose higher frequency
						if(!finished.contains(document1) && freq1>freq2){
							finished.add(document1);
						}
						else if(!finished.contains(document2) && freq2>freq1){
							finished.add(document2); 
						}
						else if(!finished.contains(document2) && freq2==freq1){ //Same frequency and same document. Choose which ever
							finished.add(document1); 
						}
					}
					else if(freq1==freq2){ //Same frequency. Choose doc 1
						if(finished.contains(document1) && finished.contains(document2)){
							;
						}
						else if(finished.contains(document1)){
							finished.add(document2);
						}
						else if(finished.contains(document2)){
							finished.add(document1);
						}
						else{
							finished.add(document1); 
						}
					}
					if(i==a-1 && finished.size()<5){
						i++; 
						if(keywordsIndex.get(kw2).size()!=a && keywordsIndex.get(kw1).size()==a){
							while(i<keywordsIndex.get(kw2).size() && finished.size()!=5){
								finished.add(keywordsIndex.get(kw2).get(i).document);
								i++; 
							}
							break loop; 
						}
						else if(keywordsIndex.get(kw1).size()!=a && keywordsIndex.get(kw2).size()==a){
							while(i<keywordsIndex.get(kw1).size() && finished.size()!=5){
								finished.add(keywordsIndex.get(kw1).get(i).document);
								i++; 
							}
							break loop; 
						}
						else if(keywordsIndex.get(kw1).size()==a && keywordsIndex.get(kw2).size()==a){
							break loop; 
						}
					}
			}
		}
		return finished;
}
}