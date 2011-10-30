package com.mayonnaise.common;


import com.mayonnaise.common.AbstractSentence;
import com.mayonnaise.common.Sentence;

import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.common.Similarity;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

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
	 *  Keep track of what is the highest scoring Sentence so far.
	 */
	private double highestScore = 0.0;

	/**
	 *  Constructor.
	 */
	public CosineSimilaritySentence(String content, StringBasisMapping basis) {
		super(content, basis);
	}

	/**
	 *  Calculates the similarity score between this sentence
   *  and another sentence. Internally the Sentence object should
	 *  record the result and also update the other object's internals.
	 */
	public Double scoreWith(Sentence other) {
		Double score =  new Double(Similarity.cosineSimilarity(getSentenceVector(),
																													 other.getSentenceVector()));
		checkHighestScore(score);
		sentenceScores.put(other, score);
		other.scoreWith(this, score);
		return score;
	}

	/**
	 *  Update the internal map that keeps track of the similarity scores.
	 */
	public void scoreWith(Sentence other, Double score) {
		checkHighestScore(score);
		sentenceScores.put(other, score);
	}

	private void checkHighestScore(Double score) {
		if (score.doubleValue() > highestScore) {
			highestScore = score.doubleValue();
		}
	}

	public double getHighestScore() {
		return highestScore;
	}
}