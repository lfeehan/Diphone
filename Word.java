/*

Screencast of project demo visible at link below:
	http://student.computing.dcu.ie/~feehanl2/screencast/screencast.mp4

*/
import java.util.*;
/** This class Stores a Word, its associated diphones and a count of them.
*
* @author Leonard Feehan
* @version 1.0 Apr 20, 2012.
*/
public class Word implements Comparable<Word>{

	private String word;
	private String[] phones;
	private Hashtable<String, Integer> diphones;
	private int diphoneCount;
	private int notFoundCount;
	private int isFoundCount;
	
	/** 	Constructor takes a word string and an array of its associated phones
		@param word 	String word
		@param phones	String array of associated phones
	*/
	public Word(String word, String[] phones){	
		this.word = word;
		this.phones = phones;
		phonesToDiphones();
		diphoneCount = diphones.size();
		notFoundCount = diphoneCount;
		isFoundCount = 0;
	}

	/** Implementation for compareTo, class is comparable on number of diphones
	*  Compared in descending order.
	* 
	* @param compareWord		Word to compare to
	* @return			returns zero for equal positive if this is greater
	*/
	public int compareTo(Word compareWord){
		int compareQuantity = ((Word) compareWord).notFoundCount() - ((Word) compareWord).isFoundCount();
		return (this.notFoundCount - this.isFoundCount) - compareQuantity;
	} 
	/*
	//this version of compareTo was used for the basic version of the greedy algorithm
	public int compareTo(Word compareWord){
		int compareQuantity = ((Word) compareWord).notFoundCount();
		
		return this.notFoundCount - compareQuantity;
	}
	*/

	/** 	Sets the count to 50 keeping it in the list but effectively
	*	putting it to the bottom of the sort pile.
	*/
	public void kill(){
		this.isFoundCount = 50;	
	}
	
	/** Revised to take diphones to a hashtable rather than an array: quicker searching
		Converts all phones to relevant diphones.
	*/
	private void phonesToDiphones(){
		diphones = new Hashtable<String, Integer>();
		String start = "#" + phones[0];
		String end = phones[phones.length-1] + "#";
		
		//if diphones does not contain start or end diph, then add it
		if (!diphones.containsKey(start)){
			diphones.put(start, new Integer(1));
		}
		if (!diphones.containsKey(end)){
			diphones.put(end, new Integer(1));
		}
		
		if (phones.length == 2){
			String diph = phones[0] + phones[1];
			if (!diphones.containsKey(diph)){
				diphones.put(diph, new Integer(1));
			}
		}else if (phones.length >= 3){	
			for(int i=0; i<phones.length-2; i++){
				//concatenate two phones into a diphone
				String diph = phones[i] + "-" + phones[i+1];
				if (!diphones.containsKey(diph)){
					diphones.put(diph, new Integer(1));
				}
			}
		}//and do nothing for length <= 1. Already entered with #'s above
	}

	//basic accessors
	/**	Returns this word
	*	@return		Returns the actual word 
	*/
	public String getWord(){
		return this.word;
	}
	
	/**	String array of diphones associated with the word
	*	@return		Returns array of associated diphones 
	*/
	public Hashtable<String, Integer> getDiphones(){
		return this.diphones;
	}
	
	/**	Number of diphones not yet added to the search set 
	*	@return		Returns number of diphones not yet added to the search set 
	*/
	public int notFoundCount(){
		return this.notFoundCount;	
	}
	
	/**	number of diphones previously added to the search set.
	*	(Used for the optimal version of greedy algorithm)
	*	@return		Returns number of diphones previously added to the search set 
	*/
	public int isFoundCount(){
		return this.isFoundCount;
	}
	
	/**	Count total diphones in this word
	*	@return		Returns total number of diphones in the word 
	*/
	public int diphoneCount(){
		return diphoneCount;
	}
	
	/**	Used for the optimal version of greedy algorithm
	*	@return		Returns number of diphones not yet added to the search set 
	*/
	public boolean containsDiphone(String diphone){
		return diphones.containsKey(diphone);
	}
	
	/**	Removes a diphone from the word
	*	@param remove	String representing the diphone to be removed
	*/
	public void removeDiphone(String remove){
		diphones.remove(remove);
		notFoundCount--;
		isFoundCount++;
		if (notFoundCount <= 0){
			//Once the word contains no more diphones force it to the bottom of the list
			//Javas mergesort is Stable therefore wont sort these values any more only copy them 
			this.kill();	
		}
	}
}
