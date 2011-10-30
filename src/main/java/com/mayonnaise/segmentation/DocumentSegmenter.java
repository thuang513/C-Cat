package com.mayonnaise.segmentation;


import com.mayonnaise.common.Sentence;
import com.mayonnaise.common.CosineSimilaritySentence;
import com.mayonnaise.util.StopWordChecker;
import com.mayonnaise.segmentation.Cluster;

import edu.ucla.sspace.basis.StringBasisMapping;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.WhitespaceTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.apache.commons.math.stat.StatUtils;

/**
 *  This class takes a document, analyzes the document (i.e. figures out
 *  what are the {@code Cluster}s in this document), then assigns each
 *  sentence to a {@code Cluster}. A "sentence" in a document is a 
 *  line in a document (a newline).
 *
 *  @author thuang513@gmail.com (Terry Huang)
 */
public class DocumentSegmenter {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(DocumentSegmenter.class.getName());

	private DocumentSegmenter() {
	}

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

	public static List<Sentence> createClustersOfDocument(String documentFilePath,
																												String stopWordPath) {
		// Open up the file and read from the document.
		// For now, store the entire document in memory...
		// TODO(thuang513): figure out a better way to do this in the future.
		List<Sentence> sentences = new ArrayList();
		try {
			String documentString = readFileAsString(documentFilePath);
			StringReader documentReader = new StringReader(documentString);
			
			// Figure out the basis for the document.
			final boolean TREAT_NEWLINE_AS_TOKEN = true;
			WhitespaceTokenizer<Word> tokenizer =
				new WhitespaceTokenizer<Word>(new WordTokenFactory(),
																			documentReader,
																			TREAT_NEWLINE_AS_TOKEN);
			
			StringBasisMapping basis = new StringBasisMapping();
			StopWordChecker stopWordChecker =
				StopWordChecker.createStopWordChecker(stopWordPath);

			while (tokenizer.hasNext()) {
				String nextWord = tokenizer.next().word();
				if (stopWordChecker.isStopWord(nextWord)) {
					continue;
				}
				basis.getDimension(nextWord);
			}
			basis.setReadOnly(true);
			
			BufferedReader bufferReader =
				new BufferedReader(new StringReader(documentString));
			
			// Make every line a sentence, and keep track of it.
			String buffer;
			while ((buffer = bufferReader.readLine()) != null) {
				sentences.add(new CosineSimilaritySentence(buffer, basis));
			}
		} catch (IOException ioe) {
			LOGGER.severe("IO Exception occured!");
			System.exit(-1);
		}
		
		// Figure out a sentence's cosine similarity with everyone else.
		// TODO(thuang513): O(n^2) operation now...figure out how to make this
		// better.
		for (int i = 0; i < sentences.size(); ++i) {
			Sentence main = sentences.get(i);
			for (int j = i; j < sentences.size(); ++j) {

				// Skip the same Sentence.
				if (i == j) {
					continue;
				}
					
				Sentence toCompare = sentences.get(j);
				main.scoreWith(toCompare);
			}
		}

		// Get all the top scores for every sentence and calculate some statistics
		// that will be used to determine the cluster similarity threshold.
		List<Double> highestScoreList = new ArrayList<Double>(sentences.size());
		for (int i = 0; i < sentences.size(); ++i) {
			Double highestSentenceScore = new Double(sentences.get(i).getHighestScore());
			// Skip zero values
			if (highestSentenceScore.doubleValue() == 0.0) {
				continue;
			}

			highestScoreList.add(highestSentenceScore.doubleValue());
			System.out.println(highestSentenceScore + ", ");
		}
		//DEBUG 
		System.out.println("");

		double[] highestScores = new double[highestScoreList.size()];
		for (int i = 0; i < highestScoreList.size(); ++i) {
			highestScores[i] = highestScoreList.get(i).doubleValue();
		}

		// Go through each sentence, find their top "buddies", and if their buddies
		// are within the threshold, form them as a Cluster.
		// double CLUSTER_SIMILARITY_THRESHOLD = StatUtils.geometricMean(highestScores);
		//		double CLUSTER_SIMILARITY_THRESHOLD = StatUtils.percentile(highestScores, 0.90);
		//		double CLUSTER_SIMILARITY_THRESHOLD = 0.1;
		
		double variance = StatUtils.variance(highestScores);
		double mean = StatUtils.geometricMean(highestScores);
		double CLUSTER_SIMILARITY_THRESHOLD = mean + 0.25 * Math.sqrt(variance);


		LOGGER.info("Using threshold = " + CLUSTER_SIMILARITY_THRESHOLD);
		int currentClusterId = 0;
		Collection<Cluster> clusterSet = new TreeSet<Cluster>();

		for (Sentence s : sentences) {
			Map.Entry<Sentence, Double>[] topSentences = s.getTopScores();

			// Check to see if we should create a Cluster.
			// Do this by checking if the highest score is greater the threshold.
			double highestScore = topSentences.length < 0 ? Double.MIN_VALUE : 
				topSentences[0].getValue().doubleValue();

			// DEBUG
			System.out.println("HIGHEST SCORE = " + highestScore);

			if (highestScore < CLUSTER_SIMILARITY_THRESHOLD) {
				// No need to create a Cluster, or check its top Sentences.
				continue;
			}

			// Create a new cluster.
			Cluster newCluster = new Cluster(currentClusterId);
			++currentClusterId;
			newCluster.addSentence(s);

			// Add Sentences to the cluster.
			for (Map.Entry<Sentence, Double> candidate : topSentences) {
				if (candidate.getValue().doubleValue() < CLUSTER_SIMILARITY_THRESHOLD) {
					// Since things are sorted, if this value is not over the threshold,
					// everyone else won't be either. No point in checking them.
					break;
				}

				// Add this sentence to the cluster.
				newCluster.addSentence(candidate.getKey());
			}
			clusterSet.add(newCluster);
		}

		// DEBUG
		// Go through the clusters and print out what sentences are in what clusters.
		for (Cluster c : clusterSet) {
			System.out.println("CLUSTER: " + c.getClusterId());
			for (Sentence s : c.getSentences()) {
				System.out.println(s);
			}
			System.out.println("");
		}

		return sentences;
	}

}