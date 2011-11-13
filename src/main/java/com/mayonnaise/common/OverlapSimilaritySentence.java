package com.mayonnaise.common;


import com.mayonnaise.common.AbstractSentence;
import com.mayonnaise.common.Sentence;

import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.common.Similarity;
import edu.ucla.sspace.vector.DoubleVector;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 *  A concrete {@code Sentence} implementation that uses 
 *  overlap similarity to determine the similarity of {@code Sentence}s. The
 *  overlap similarity uses the equation described by Mihalcea et al. in:
 *
 *  <ul><li style="font-family:Garamond, Georgia, serif">Rada Mihalcea and Paul
 *  Tara (2004). "TextRank: Bringing Order into Texts". <i> Association of
 *  Computational Linguistics</i></li></ul>
 *
 *  @author thuang513@gmail.com (Terry Huang).
 */

public class OverlapSimilaritySentence extends AbstractSentence {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(OverlapSimilaritySentence.class.getName());

	/**
	 *  Keep track of what is the highest scoring Sentence so far.
	 */
	private double highestScore = 0.0;

	/**
	 *  Constructor.
	 */
	public OverlapSimilaritySentence(String content, StringBasisMapping basis) {
		super(content, basis);
	}


	/** 
	 *  Get the overlap score.
	 */
	private Double getOverlapScore(Sentence other) {
		DoubleVector thisSentence = getSentenceVector();
		DoubleVector otherSentence = other.getSentenceVector();

		if (thisSentence.length() != otherSentence.length()) {
			LOGGER.severe("Sentence lengths do not overlap!");
			return null;
		}
		
		// Get the number of word overlaps there are with this sentence and the other sentence.
		// Also keep the count of the number of words in each sentence.
		int overlapCount = 0;
		int thisSentenceCount = 0;
		int otherSentenceCount = 0;
		for (int i = 0; i < thisSentence.length(); ++i) {
			if (thisSentence.get(i) != 0 && otherSentence.get(i) != 0) {
				++overlapCount;
			}

			if (thisSentence.get(i) != 0) {
				++thisSentenceCount;
			}

			if (otherSentence.get(i) != 0) {
				++otherSentenceCount;
			}
		}

		// Return the score.
		double score = overlapCount / (Math.log(thisSentenceCount) + Math.log(otherSentenceCount));
		return new Double(score);
	}

	/**
	 *  Calculates the similarity score between this sentence
   *  and another sentence. Internally the Sentence object should
	 *  record the result and also update the other object's internals.
	 */
	public Double scoreWith(Sentence other) {
		Double score =  getOverlapScore(other);
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