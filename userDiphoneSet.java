import java.util.*;
import java.io.*;

/**
*	Modifed version of the DiphoneSet class
*	Used for take a user entered set of words and applying the
*	greedy algorithm to generate a list of words that contain its diphones
*	By recording this list it should be possible to synthesise a voice 
*	that can speak the users entered phrase.
*
*	Javadoc comments only provided for parts that differ from DiphoneSet
*/
public class userDiphoneSet{
	
	private Dictionary dictionary;
	private int totalWords;
	private int uniqueDiphones;
	private int uniqueDiphonesSubset;
	
	private Word[] diphoneSet;
	private Hashtable<String, Integer> wordCoverageList;
	private Word[] subset;
	private Hashtable<String, Integer> allDiphones;
	
	public userDiphoneSet(Dictionary dictionary){
		this.dictionary = dictionary;
		totalWords = dictionary.length();
		diphoneSet = new Word[totalWords];
		wordCoverageList = new Hashtable<String, Integer>();

		for (int i=0; i<totalWords; i++){
			String[] tokens = dictionary.get(i).split("[ |\\t]+");
			String[] phones = Arrays.copyOfRange(tokens, 1, tokens.length);
			Word x = new Word(tokens[0], phones);
			diphoneSet[i] = x;
		}
	}
	
	/**
	*	Prepares the dictionary by removing unnecessary words and diphones 
	*	stripping it down before applying the set coverage algorithm
	*/
	public void finish(){
		for(int i=0; i<diphoneSet.length; i++){
			Hashtable <String, Integer> blah = diphoneSet[i].getDiphones();

			Enumeration enumerator = blah.keys();
			while( enumerator.hasMoreElements() ) {
				String thisDiphone = (String)enumerator.nextElement();
				if (!(allDiphones.containsKey(thisDiphone))){
					diphoneSet[i].removeDiphone(thisDiphone);
				}
			}
		}
		Arrays.sort(diphoneSet);
	}

	/**
	*	Gets users words and their phones from the dictionary
	*	@param userWords	String of words entered by the user
	*/	
	public void load(String userWords){
		String[] subset = clean(userWords);
		this.subset = new Word[subset.length];
		//for each word of the subset
		for(int i=0; i<subset.length; i++){
			//find it in the dictionary
			for (int j=0; j<totalWords; j++){
				String[] tokens = dictionary.get(j).split("[ |\\t]+");
				String[] diphones = Arrays.copyOfRange(tokens, 1, tokens.length);
				//if it exists add it to the new array
				if(tokens[0].compareTo(subset[i]) == 0){
					Word x = new Word(tokens[0], diphones);
					this.subset[i] = x;
				}
			}
		}
		uniqueDiphones = countUniqueDiphones();
	}

	/**
	*	Method takes user input and cleans it up for processing
	*	@param wordList 	String of user entered words
	*	@return 	Returns the user words stripped of punctuation
	*			and split into an array of unique words.
	*/
	private String[] clean(String wordList){
		Hashtable<String, Integer> subset = new Hashtable<String, Integer>();
		String[] tokens = wordList.split("[ ]+");

		for(int i=0; i<tokens.length; i++){
			tokens[i] = tokens[i].toUpperCase().replaceAll("[^A-Za-z]", "");
			if (!(subset.containsKey(tokens[i]))){
				subset.put(tokens[i], new Integer(1));
			}
		}
		String[] arr= new String[subset.size()];
		int index=0;
		Enumeration enumerator = subset.keys();
		while( enumerator.hasMoreElements() ) {
			String thisWord = (String)enumerator.nextElement();
			arr[index] = thisWord;
			index++;
		}
		return arr;
	}
	
	//remainder is the same as the DiphoneSet class.
	public int countUniqueDiphones(){	
		allDiphones = new Hashtable<String, Integer>();
		int uniqueDiphoneTotal = 0;
		
		for(int i=0; i<subset.length; i++){
			Hashtable<String, Integer> diCount = subset[i].getDiphones();
			
			
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

	public void calculateSetCoverage(){
		Hashtable<String, Integer> diphonesFound = new Hashtable<String, Integer>();
		Arrays.sort(diphoneSet);
		
		boolean done = false;
		int count = 0;
		
		while(!done){
			//get the current word
			String topWord = diphoneSet[diphoneSet.length-1].getWord();
			
			//get the diphone list for that word			
			Hashtable<String, Integer> currentWord = diphoneSet[diphoneSet.length-1].getDiphones();
						
			//for each diphone check...
			Enumeration enumerator = currentWord.keys();
			while( enumerator.hasMoreElements() ) {
				String thisDiphone = (String)enumerator.nextElement();
				//if the diphone is not in the diphonesFound list add it
				if (!(diphonesFound.containsKey(thisDiphone))){
					//add to diphonesFound
					diphonesFound.put(thisDiphone, new Integer(1));
					count++;
					System.out.println(count + " / " + uniqueDiphones);
					
					//remove from all occurences of that diphone from dictionary
					for(int j=0; j<diphoneSet.length; j++){
						if (diphoneSet[j].containsDiphone(thisDiphone)){
							diphoneSet[j].removeDiphone(thisDiphone);
						}
					}

					//add to wordCoverageList
					if (!(wordCoverageList.containsKey(topWord))){
						wordCoverageList.put(topWord, new Integer(1));
					}
					
				//else remove it from that word
				}else{
					diphoneSet[diphoneSet.length-1].removeDiphone(thisDiphone);
				}
			}
			
			diphoneSet[diphoneSet.length-1].kill();
			Arrays.sort(diphoneSet);
		
			if (count >= uniqueDiphones){
				done = true;
			}
		}
	}
	
	public Hashtable<String, Integer> getSetCoverage(){
		return this.wordCoverageList;
	}
	
	public int length(){
		return this.diphoneSet.length;
	}
	
	public int getUniqueDiphones(){
		return this.uniqueDiphones;
	}
	
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
