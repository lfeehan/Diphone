import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;

/**
*	GUI for diphone set coverage project
* 	@author Leonard Feehan
* 	@version 1.0 Apr 20, 2012.
*/
public class GUI extends JPanel implements ActionListener
{
	//GUI Elements
	private JButton open, save, process, processUser;
	private JFileChooser chooser;
	private JTextArea userInput;
	private JEditorPane wordSet, stats, stats2, output1;
	private JScrollPane scrollpaneWords, scrollpaneOutput;
	
	//Size in pixels of main GUI window
	private static final int xDim = 550;
	private static final int yDim = 600;
	
	//Object Elements
	Dictionary dictionary;
	DiphoneSet workingSet;
	userDiphoneSet userSet;
	Hashtable<String, Integer> list;

	public JPanel createContentPane ()
	{
		chooser = new JFileChooser();
        	chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() 
		{
			public boolean accept(File f) 
			{
				return f.getName().toLowerCase().endsWith(".txt")|| f.isDirectory();
			}
			public String getDescription() 
			{
				return ".txt Files";
			}
		});

		//Bottom JPanel to place everything on.
		JPanel totalGUI = new JPanel();

		//Label to contain all the Buttons.
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		//Create the open button.
		open = new JButton("Open");
		open.addActionListener(this);
		
		//Create the process button.
		process = new JButton("Proccess");
		process.addActionListener(this);
		process.setEnabled(false);
		
		//Create the processUser button.
		processUser = new JButton("Proccess2");
		processUser.addActionListener(this);
		processUser.setEnabled(false);
		
		//Create the save button.
		save = new JButton("Save List");
		save.addActionListener(this);
		save.setEnabled(false);

		//Panel to hold word set output
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		textPanel.setPreferredSize(new Dimension(300, 250));
		
		//Displayword set after processing
		wordSet = new JEditorPane();
		wordSet.setContentType("text/html");
		wordSet.setEditable(true);
		wordSet.setOpaque(false);
		wordSet.setText("<br/><br/><br/><br/><br/><br/><center><B> Open a Dictionary File... </B>");
		scrollpaneWords = new JScrollPane(wordSet);
		
		//StatPanel - stats about processed document
		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.PAGE_AXIS));
		statPanel.setPreferredSize(new Dimension(200, 250));

		stats = new JEditorPane();
		stats.setContentType("text/html");
		stats.setEditable(true);
		stats.setOpaque(false);
		stats.setText("STATS:<hr>");
		
		stats2 = new JEditorPane();
		stats2.setContentType("text/html");
		stats2.setEditable(true);
		stats2.setOpaque(false);
		stats2.setText("");

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
		inputPanel.setPreferredSize(new Dimension(500, 25));
		
		userInput = new JTextArea ();
		output1 = new JEditorPane();
		output1.setContentType("text/html");
		output1.setEditable(false);
		output1.setOpaque(false);
		scrollpaneOutput = new JScrollPane(output1);
	
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.LINE_AXIS));
		outputPanel.setPreferredSize(new Dimension(500, 200));
		
		buttonPanel.add(open);
		buttonPanel.add(Box.createRigidArea(new Dimension(20,20))); //spacer
		buttonPanel.add(process);
		buttonPanel.add(Box.createRigidArea(new Dimension(20,20))); //spacer
		buttonPanel.add(save);
		
		inputPanel.add(userInput);
		inputPanel.add(processUser);
		outputPanel.add(scrollpaneOutput);
		
		statPanel.add(stats);
		statPanel.add(stats2);

		textPanel.add(scrollpaneWords);
		
		totalGUI.add(textPanel);
		totalGUI.add(statPanel);
		totalGUI.add(buttonPanel);
		totalGUI.add(inputPanel);
		totalGUI.add(outputPanel);

		return totalGUI;
	}
	
	/** Handles main program logic based on buttons clicked */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == process){
			long start, end;
			
			start = System.currentTimeMillis();
			workingSet.calculateSetCoverage();	
			end = System.currentTimeMillis();
					
			System.out.println("Algorithm Time: " + (end - start));
			
			list = workingSet.getSetCoverage();
			String suggestions = "";
			Enumeration enumerator = list.keys();
			while( enumerator.hasMoreElements() ) {
				String thisWord = (String)enumerator.nextElement();
				suggestions += thisWord + "<br>";
			}
			wordSet.setText(suggestions);
			wordSet.repaint();

			int corpus = list.size();
			int count = workingSet.corpusDiphoneCount();
			
			//String split over multiple lines here for code clarity of html		
			String statsDisplay = "<table border=0>";
			statsDisplay += "<tr><td>";
			statsDisplay += 	"Corpus Size: ";
			statsDisplay += "</td><td><b>";
			statsDisplay += 	corpus;
			statsDisplay += "</td></tr><tr><td>";
			statsDisplay += 	"Total Diphones:";
			statsDisplay += "</td><td><b>";
			statsDisplay += 	count;
			statsDisplay += "</td></tr>";
			statsDisplay += "</table>";

			stats2.setText(statsDisplay);
			stats2.repaint();

			//make the save button clickable now
			save.setEnabled(true);
			process.setEnabled(false);
		
		}else if (e.getSource() == processUser){
			
			userSet = new userDiphoneSet(dictionary);
			String input = userInput.getText();
			userSet.load(input);
			userSet.finish();
			userSet.calculateSetCoverage();		
			list = userSet.getSetCoverage();
			
			String suggestions = "";
			Enumeration enumerator = list.keys();
			while( enumerator.hasMoreElements() ) {
				String thisWord = (String)enumerator.nextElement();
				suggestions += thisWord + "<br>";
			}
			output1.setText(suggestions);
			output1.repaint();
		}else if (e.getSource() == open) {
			int returnVal = chooser.showOpenDialog(GUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();

				//Handle file opening and initial processing
				dictionary = new Dictionary("Dictionary/" + file.getName());

				workingSet = new DiphoneSet(dictionary);
				workingSet.load();
				int numWords = workingSet.length();
				int numDiphones = workingSet.getUniqueDiphones();

				//String split over multiple lines here for code clarity of html
				String statsDisplay = "STATS: <i>" + file.getName() + "</i><hr>";

				statsDisplay += "<table border=0>";
				statsDisplay += "<tr><td>";
				statsDisplay += 	"Words: ";
				statsDisplay += "</td><td><b>";
				statsDisplay += 	numWords;
				statsDisplay += "</td></tr><tr><td>";
				statsDisplay += 	"Unique Diphones: ";
				statsDisplay += "</td><td><b>";
				statsDisplay += 	numDiphones;
				statsDisplay += "</td></tr>";
				statsDisplay += "</table>";
				
				stats.setText(statsDisplay);
				stats.repaint();
				
				process.setEnabled(true);
				processUser.setEnabled(true);
			}

			//Handle save button action.
		} else if (e.getSource() == save) {
			int returnVal = chooser.showSaveDialog(GUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				//Save the corpus as a text file
				workingSet.save(file.getName());
			} 
		}
	}

	/** Swing creation of holder frame */
	private static void createAndShowGUI() 
	{
		GUI diphoneApp = new GUI();
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Diphone Corpus Gen - Version B");
		frame.setContentPane(diphoneApp.createContentPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(xDim, yDim);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
