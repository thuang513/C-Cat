package com.mayonnaise.main;


import com.mayonnaise.common.CosineSimilaritySentence;
import com.mayonnaise.common.Sentence;

import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.vector.DoubleVector;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.WhitespaceTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 *  This is the main class used to compare the similarity of a sentence in a
 *  file to every sentence in another file (seperated by newline).
 *  @author thuang513@gmail.com (Terry Huang)
 */
public class SentenceDocumentMain {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(SentenceDocumentMain.class.getName());

	
	/** 
	 * Returns all of the contents of a file as a string.
	 *	@param filePath the name of the file to open.
	 */ 
	private static String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static void usage() {
		System.out.println("SentenceDocumentMain <file of first sentence> <file of second sentence>");
	}

	public static void main(String[] args) {
		// Check that there are enough args to begin with.
		if (args.length < 2) {
			usage();
			System.exit(-1);
		}

		String mainSentenceString, secondSentenceString;
		try {
			// Get the file that contains the first sentence.
			mainSentenceString = readFileAsString(args[0]);
			
			// Gets the file that contains the second, or more sentence.
			secondSentenceString = readFileAsString(args[1]);
		} catch (IOException io) {
			LOGGER.severe("Couldn't read from the given files!");;
			return;
		}
	
		StringBasisMapping basis = new StringBasisMapping();

		// Fill up the basis mapping.
		StringReader mainSentenceReader = new StringReader(mainSentenceString);
		StringReader secondSentenceReader = new StringReader(secondSentenceString);

		WhitespaceTokenizer<Word> mainSentenceTokenizer =
			new WhitespaceTokenizer<Word>(new WordTokenFactory(),
																		mainSentenceReader,
																		true);

		WhitespaceTokenizer<Word> secondSentenceTokenizer =
			new WhitespaceTokenizer<Word>(new WordTokenFactory(),
																		secondSentenceReader,
																		true);
		while (mainSentenceTokenizer.hasNext()) {
			basis.getDimension(mainSentenceTokenizer.next().word());
		}
		while (secondSentenceTokenizer.hasNext()) {
			basis.getDimension(secondSentenceTokenizer.next().word());
		}
		basis.setReadOnly(true);

		Sentence mainSentence = new CosineSimilaritySentence(mainSentenceString, basis);



		// Print out the basis.
		System.out.print("BASIS: ");
		for (int i = 0; i < basis.numDimensions(); ++i) {
			System.out.print(basis.getDimensionDescription(i));

			if (i != basis.numDimensions() - 1) {
				System.out.print(", ");
			}	else {
				System.out.println("");
			}
		}

		System.out.println("MAIN: " + mainSentenceString);
		printOutVector(mainSentence.getSentenceVector());

		// Print out the results of all the other sentences.
		BufferedReader bufferReader = new BufferedReader(new StringReader(secondSentenceString));
		String buffer = null;

		try {
			Sentence compareSentence = null;
			while ((buffer = bufferReader.readLine()) != null) {
				
				System.out.println("BUFFER = " + buffer); 

				compareSentence = new CosineSimilaritySentence(buffer, basis);
				Double compareResults = mainSentence.scoreWith(compareSentence);
				System.out.println("COMPARE: " + buffer);
				printOutVector(compareSentence.getSentenceVector());
				System.out.println("SCORE: " + compareResults);
				System.out.println("--------------------------------------------------------------");
			}
		} catch (IOException ioe) {
			LOGGER.severe("Error trying to read file's string!");
		}
	}

	private static void printOutVector(DoubleVector v) {
		System.out.print("[");
		double[] vArray = v.toArray();
		for (int i = 0; i < vArray.length; ++i) {
			System.out.print(vArray[i]);

			if(i != vArray.length) {
				System.out.print(", ");
			} else {
				System.out.print("]");
			}
		}
		System.out.println("]");
	}
}

