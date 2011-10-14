package com.mayonnaise.common;

import edu.ucla.sspace.common.Similarity;
import java.util.logging.Logger;

import com.mayonniase.common.AbstractSentence;
import com.mayonnaise.common.Sentence;

/**
 *  A concrete {@code Sentence} implementation that uses cosine
 *  similarity to determine the similarity of {@code Sentence}s.
 *
 *  @author thuang513@gmail.com (Terry Huang).
 */

public class CosineSimilaritySentence extends AbstractSentence {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(CosineSimilaritySentence.class.getName());

	/**
	 *  
	 */

	/**
	 *  Constructor.
	 */
	public CosineSimilaritySentence(String content) {
		super(content);
	}

	/**
	 *  Calculates the similarity score between this sentence
   *  and another sentence. Internally the Sentence object should
	 *  record the result and also update the other object's internals.
	 */
	double scoreWith(Sentence other) {
		return Similarity.cosineSimilarity(getSentenceVector(), other.getSentenceVector());
	}

	/**
	 *  Update the internal map that keeps track of the similarity scores.
	 */
	double scoreWith(Sentence other, double score) {

	}



}