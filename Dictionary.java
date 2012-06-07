import java.util.*;
import java.io.*;
/**
*	Class to read a file into an arraylist
*	Provides basic accessors
* 	@author Leonard Feehan
* 	@version 1.0 Apr 20, 2012.
*/
public class Dictionary{

	private File filename;
	private ArrayList<String> localFile;
	private int fileLength;
	
	/**	Opens a text file and stores it
	*	@param filename		name of file to open including path
	*/
	public Dictionary(String filename){
		this.filename = new File(filename);
		localFile = new ArrayList<String>();
		fileLength = 0;
		readFile();
	}
	
	/**	Handles file reading
	*	@exception IOException	Exception thrown if problem with path
	*/
	private void readFile(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(this.filename));
			String inputLine;
			while ((inputLine = br.readLine()) != null){
				localFile.add(inputLine);
				fileLength++;
			}
			br.close();
		}catch(IOException e){
			System.out.println("Buffered reader exception");
			System.exit(-1);
		}
	}
	
	/** 	Returns number of lines in the file
	*	@return		number of lines in file
	*/
	public int length(){
		return fileLength;
	}
	
	/**
	*	@param lineNum		line number to return
	*	@return String that represents the specified line number.
	*/
	public String get(int lineNum){
		return localFile.get(lineNum);
	}
	
	/**	Overwrites a specific line with a string
	*	@param lineNum	Number on line to edit
	*	@param line 	string to write to line 
	*/
	public void set(int lineNum, String line){
		this.localFile.set(lineNum, line);
	}
}
