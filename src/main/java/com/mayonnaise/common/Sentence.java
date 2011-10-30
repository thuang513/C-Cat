package com.mayonnaise.common;


import com.mayonnaise.segmentation.Cluster;

import java.util.List;
import java.util.Set;
import java.util.Map;
import edu.ucla.sspace.vector.DoubleVector;

/**
 *  Represents a sentence of an article. Different {@code Sentence} objects
 *  could have different ways to measure similarity between other {@code Sentence}s.
 *  @author thuang513@gmail.com (Terry Huang).
 */
public interface Sentence {

	/**
	 *  Returns the content of the string.
	 */
	String getRawContent();

	/**
	 *  Calculates the similarity score between this sentence
   *  and another sentence. Internally the Sentence object should
	 *  record the result and also update the other object's internals.
	 */
	Double scoreWith(Sentence other);

	/**
	 *  Update the internal map that keeps track of the similarity scores.
	 */
	void scoreWith(Sentence other, Double score);

	/**
	 *  Adds a "groupie" to this sentence. A "groupie" is a {@code Sentence}
	 *  that is very similar to the current sentence.
	 */
	void addGroupie(Sentence groupie);

	/**
	 *  Returns the set groupies associated with this {@code Sentence}.
	 */
	Set<Sentence> getGroupies();

	/**
	 *  Gets the set of {@code Cluster}s that this Sentence belongs to.
	 */
	Set<Cluster> getClusterSet();

	/**
	 *  Mark this {@code Sentence} as belonging to a {@code Cluster}.
	 */
	void addCluster(Cluster c);

	/**
	 * Get the top scoring sentences. The {@code Set} contains {@code Map} entries
	 * which are sorted in DESCENDING order. The key entry is the similarity score
	 * of this sentence to other sentence. The value entry is the "other sentence".
	 */
	Map.Entry<Sentence, Double>[] getTopScores();

	double getHighestScore();
	
	DoubleVector getSentenceVector();
	
}
