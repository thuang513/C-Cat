package com.mayonnaise.extraction;


import com.mayonnaise.common.Sentence;

import edu.ucla.sspace.matrix.Matrices;
import edu.ucla.sspace.matrix.Matrix;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

/**
 *  The {@code PageRankExtractor} uses the PageRank algorithm to compute
 *  {@code Sentence}s that seem to represent the centroid of a given document.
 *  Similar to the TextRank algorithm presented by Mihalcea and Tarau.
 */
public class PageRankExtractor {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(PageRankExtractor.class.getName());

	private List<Sentence> sentences;
	private Matrix similarityMatrix;

	private static final int PAGE_RANK_ITERATIONS = 30;

	public PageRankExtractor() {
		sentences = new ArrayList<Sentence>();
	}

	public void addSentence(Sentence s) {
		sentences.add(s);
	}

	public List<Map.Entry<Sentence, Double>> runPageRank() {
		// Create the Matrix.
		similarityMatrix = Matrices.create(sentences.size(), sentences.size(), false);

		// Fill out the matrix by filling in the similarity score of every
		// Sentence pair.
		for (int i = 0; i < similarityMatrix.rows(); ++i) {
			for (int j = 0; j < similarityMatrix.columns(); ++j) {
				if (i == j) {
					continue;
				}
				
				Sentence rowSentence = sentences.get(i);
				Sentence colSentence = sentences.get(j);
				similarityMatrix.set(i, j, rowSentence.scoreWith(colSentence).doubleValue());
			}
		}

		// Run PageRank by trying to find the eigenvector.
		Matrix steadyState = Matrices.create(1, sentences.size(), false);
		steadyState.set(0, 0, 1.0);

		// Multiply the steady state vector with the similarityMatrix.
		// Repeat this process PAGE_RANK_ITERATION times.
		for (int iteration = 0; iteration < PAGE_RANK_ITERATIONS; ++iteration) {
			steadyState = Matrices.multiply(steadyState, similarityMatrix);
		}

		double[] finalSteadyStates = steadyState.getRow(0);
		List<Map.Entry<Sentence, Double>> computedResult =
			                                      new ArrayList(steadyState.columns());
		for (int k = 0; k < steadyState.columns(); ++k) {
			computedResult.add(new AbstractMap.SimpleEntry<Sentence, Double>
												            (sentences.get(k), finalSteadyStates[k]));
		}

		// Sort the Sentences by their new rank.
		Collections.sort(computedResult, new Comparator () {
				public int compare(Object o1, Object o2) {
					Map.Entry<Sentence, Double> e1 = (Map.Entry<Sentence, Double>)o1;
					Map.Entry<Sentence, Double> e2 = (Map.Entry<Sentence, Double>)o2;

					// NOTE: we want the array in a DESCENDING ORDER, so here we
					// reversed the compare...
					return e2.getValue().compareTo(e1.getValue());
				}
			});

		return computedResult;
	}

}