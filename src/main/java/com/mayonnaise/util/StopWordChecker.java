package com.mayonnaise.util;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 *  A StopWordChecker loads all the "words" in a given file into memory
 *  and then is used to check if a word is a stopword or not at runtime.
 *
 *  @author thuang513@gmail.com (Terry Huang)
 */
public class StopWordChecker {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(StopWordChecker.class.getName());

	private Set<String> stopWordSet = null;

	private StopWordChecker(String stopWordFilePath) throws IOException {
		// Open the file and put every line into the set
		BufferedReader in = new BufferedReader(new FileReader(stopWordFilePath));
		String buffer = null;
		stopWordSet = new TreeSet<String>();

		while ((buffer = in.readLine()) != null) {
			stopWordSet.add(buffer);
		}
	}

	public boolean isStopWord(String candidate) {
		return stopWordSet.contains(candidate);
	}

	public static StopWordChecker createStopWordChecker(String stopWordFilePath)
			throws IOException {
		return new StopWordChecker(stopWordFilePath);
	}
	
}