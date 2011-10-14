package com.mayonnaise.common;

import java.util.List;
import java.util.Set;

import com.mayonnaise.segmentation.Cluster;

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
	double scoreWith(Sentence other);

	/**
	 *  Update the internal map that keeps track of the similarity scores.
	 */
	double scoreWith(Sentence other, double score);

	/**
	 *  Adds a "groupie" to this sentence. A "groupie" is a {@code Sentence}
	 *  that is very similar to the current sentence.
	 */
	void addGroupie(Sentence groupie);

	/**
	 *  Returns the set groupies associated with this {@code Sentence}.
	 */
	Set getGroupies();

	/**
	 *  Get the cluster that this class belongs to.
	 */
	Cluster getCluster();

	/**
	 *  Set the cluster that this sentence belongs to.
	 */
	void setCluster(Cluster c);

	/**
	 * Get the top scoring sentences.
	 */
	List getTopScores();
	
	
}
