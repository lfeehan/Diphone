import java.util.*;
import java.io.*;
/**	Class to handle an implementation of the Greedy algorithm for 
*	the purposes of solving a set coverage problem: Word selection to
*	capture a body of diphones for speech synthesis.
*
* 	@author Leonard Feehan
* 	@version 1.0 Apr 20, 2012.
*/
public class DiphoneSet{
	
	private Dictionary dictionary;
	private Word[] wordSet;
	private int totalWords;
	private int uniqueDiphones;
	private int corpusDiphoneCount;
	
	private Hashtable<String, Integer> wordCoverageList;
	
	/**	Constructor takes a dictionary initialises array of Words
	*	@param dictionary	Dictionary of words and their phones 
	*/
	public DiphoneSet(Dictionary dictionary){
		this.dictionary = dictionary;
		totalWords = dictionary.length();
		wordSet = new Word[totalWords];
		wordCoverageList = new Hashtable<String, Integer>();
	}
	
	/** Parses the dictionary and splits the words and phones */
	public void load(){
		for (int i=0; i<totalWords; i++){
			String[] tokens = dictionary.get(i).split("[ |\\t]+");
			String[] phones = Arrays.copyOfRange(tokens, 1, tokens.length);
			Word currentWord = new Word(tokens[0], phones);
			wordSet[i] = currentWord;
		}
		uniqueDiphones = this.countUniqueDiphones();
		corpusDiphoneCount = 0;
	}
	
	/**	Cycles through all diphones taking count of the unique total
	*	@return returns the count of unique diphones that exist in the dictionary
	*/
	public int countUniqueDiphones(){	
		Hashtable<String, Integer> allDiphones = new Hashtable<String, Integer>();
		int uniqueDiphoneTotal = 0;
		
		for(int i=0; i<wordSet.length; i++){
			Hashtable<String, Integer> diCount = wordSet[i].getDiphones();
			Enumeration enumerator = diCount.keys();
			while( enumerator.hasMoreElements() ) {
				String thisDiphone = (String)enumerator.nextElement();
				if (!(allDiphones.containsKey(thisDiphone))){
					allDiphones.put(thisDiphone, new Integer(1));
					uniqueDiphoneTotal++;
				}
			}
		}
		return uniqueDiphoneTotal;
	}

	/**
	*	Method to execute the greedy algorithm
	*	Takes word with most diphones not in the found set
	*	and the least words that have already been found
	*	The word is added to the coverage set and its diphones removed
	*	from the rest of the words, then resorts them before taking the 
	*	next top word until all diphones have been accounted for
	*/
	public void calculateSetCoverage(){
		Hashtable<String, Integer> diphonesFound = new Hashtable<String, Integer>();
		Arrays.sort(wordSet);
		
		boolean done = false;
		int count = 0;
		
		//will loop until all words containing all uniqueDiphones are found
		while(!done){
			//get the word with most diphones (lets be greedy)
			String topWord = wordSet[wordSet.length-1].getWord();

			//get the diphone list for that word			
			Hashtable<String, Integer> currentWord = wordSet[wordSet.length-1].getDiphones();

			//for each of those diphones check...
			Enumeration enumerator = currentWord.keys();
			while( enumerator.hasMoreElements() ) {
				String thisDiphone = (String)enumerator.nextElement();
				//if the diphone is not in the diphonesFound list add it
				if (!(diphonesFound.containsKey(thisDiphone))){
					diphonesFound.put(thisDiphone, new Integer(1));
					count++;
					System.out.println(count + " / " + uniqueDiphones);
					//remove from all occurences of that diphone from the wordlist
					for(int j=0; j<wordSet.length; j++){
						if (wordSet[j].containsDiphone(thisDiphone)){
							wordSet[j].removeDiphone(thisDiphone);
						}
					}
					//add to wordCoverageList
					if (!(wordCoverageList.containsKey(topWord))){
						wordCoverageList.put(topWord, new Integer(1));
						corpusDiphoneCount += wordSet[wordSet.length-1].diphoneCount();
					}
				//else remove it from that word
				}else{
					wordSet[wordSet.length-1].removeDiphone(thisDiphone);
				}
			}
			wordSet[wordSet.length-1].kill();
			Arrays.sort(wordSet);
		
			if (count >= uniqueDiphones){
				done = true;
			}
		}
	}
	
	/**
	*	Method to retrieve the final calculated word set
	*	@return hashtable of the calculated word set 
	*/
	public Hashtable<String, Integer> getSetCoverage(){
		return this.wordCoverageList;
	}
	
	/**
	*	@return 	count of words in the coverage set
	*/
	public int length(){
		return this.wordSet.length;
	}
	
	/**
	*	@return		count of the total actual diphones in the coverage set
	*/
	public int corpusDiphoneCount(){
		return corpusDiphoneCount;	
	}
	
	/**
	*	@return		count of the unique diphone count to search for
	*/
	public int getUniqueDiphones(){
		return this.uniqueDiphones;
	}
	
	/**
	*	@param saveFileName	output name to save a copy of the corpus
	*/
	public void save(String saveFileName){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(saveFileName));

			Enumeration enumerator = wordCoverageList.keys();
			while( enumerator.hasMoreElements() ) {
				String thisWord = (String)enumerator.nextElement();
				bw.write(thisWord);
				bw.newLine();
			}
			bw.close();
		}catch(IOException e){
			System.out.println("Buffered writer exception");
		}
	}
}
