package com.mayonnaise.common;


import com.mayonnaise.common.Sentence;
import com.mayonnaise.segmentation.Cluster;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.WhitespaceTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import edu.ucla.sspace.vector.DoubleVector;
import edu.ucla.sspace.vector.SparseHashDoubleVector;
import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.vector.SparseHashDoubleVector;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *  An abstract Sentence class that provides the general implementation
 *  on what all {@code Sentence}s should do. All {@code Sentence}s should
 *  extend this abstract class.
 *
 *  @author thuang513@gmail.com (Terry Huang).
 */
public abstract class AbstractSentence implements Sentence, Comparable {

	/**
	 *  The set of Clusters that this Sentence is associated with.
	 */
	private Set<Cluster> clusterSet;

	/**
	 *  The raw text content of the sentence.
	 */
	private final String rawContent;

	/**
	 *  The groupies of this {@code Sentence}.
	 */
	private Set<Sentence> myGroupies;

	/**
	 *  Basis mapping for the sentence.
	 */
	private final StringBasisMapping basis;

	/**
	 *  A vector for this current sentence.
	 */
	private final DoubleVector sentenceVector;

	/**
	 *  Internal map that keeps track of the similarity score with others.
	 */
	Map<Sentence, Double> sentenceScores;
	
	public static final double WORD_FOUND_SCORE = 1.0;

	/** 
	 *  Constructor for the abstract Sentence. {@code basis} is expected to
	 *  be a read-only StringBasisMapping. The reason for this is we need to
	 *  know the size of the vector ahead of time.
	 *
	 *  If {@code basis} is not set to read-only, then an IllegalStateException
	 *  will occur.
	 */
	protected AbstractSentence(String rawContent, StringBasisMapping basis) {
		if (!basis.isReadOnly()) {
			throw new IllegalStateException("The basis mapping must be read-only!");
		}

		this.rawContent = rawContent;
		this.basis = basis;
		clusterSet = new TreeSet<Cluster>();
		myGroupies = new TreeSet();
		sentenceScores = new HashMap();
		sentenceVector = new SparseHashDoubleVector(basis.numDimensions());
		tokenize();
	}

	/**
	 *  Take the raw sentence and tokenize it by whitespace.
	 */
	protected void tokenize() {
		// TODO(thuang513): discard stop words.
		StringReader reader = new StringReader(rawContent);

		WhitespaceTokenizer<Word> tokenizer =
			new WhitespaceTokenizer<Word>(new WordTokenFactory(),
																		reader,
																		true);
		
		while (tokenizer.hasNext()) {
			String word = tokenizer.next().word();
			System.out.print(word);

			// Take every token, get its dimension and add it to the basis.
			int wordDimension = basis.getDimension(word);
			sentenceVector.set(wordDimension, WORD_FOUND_SCORE);
		}
	}

	/**
	 *  Returns the content of the string.
	 */
	public String getRawContent() {
		return rawContent;
	}

	/**
	 *  Adds a "groupie" to this sentence. A "groupie" is a {@code Sentence}
	 *  that is very similar to the current sentence.
	 */
	public void addGroupie(Sentence groupie) {
		myGroupies.add(groupie);
	}

	/**
	 *  Returns the set groupies associated with this {@code Sentence}.
	 */
	public Set<Sentence> getGroupies() {
		return Collections.unmodifiableSet(myGroupies);
	}

	/**
	 *  {@inherit}
	 */
	public Set<Cluster> getClusterSet() {
		return Collections.unmodifiableSet(clusterSet);
	}

	/**
	 *  {@inherit}
	 */
	public void addCluster(Cluster c) {
		clusterSet.add(c);
	}

	/**
	 * {@inherit}
	 */
	public Map.Entry<Sentence, Double>[] getTopScores() {
		// Takes the map entries and puts them in an ArrayList.
		Map.Entry<Sentence, Double>[] entryArray =
			sentenceScores.entrySet().toArray(new Map.Entry[0]);
		

		// Sort the list based off of the score of the Sentence.
		Arrays.sort(entryArray, new Comparator () {
				public int compare(Object o1, Object o2) {
					Map.Entry<Sentence, Double> e1 = (Map.Entry<Sentence, Double>)o1;
					Map.Entry<Sentence, Double> e2 = (Map.Entry<Sentence, Double>)o2;

					// NOTE: we want the array in a DESCENDING ORDER, so here we
					// reversed the compare...
					return e2.getValue().compareTo(e1.getValue());
				}
			});
		
		return entryArray;		
	}

	/**
	 *  Returns a vector that represents the sentence in the semantic space.
	 */
	public DoubleVector getSentenceVector() {
		// TODO(thuang513): Make sure that the double vector cannot be modified!
		// return new SparseHashDoubleVector(sentenceVector);
		return sentenceVector;
	}

	public String toString() {
		return rawContent;
	}

	public int hashCode() {
		return rawContent.hashCode();
	}

	public int compareTo(Object other) {
		return rawContent.compareTo(((Sentence)other).getRawContent());
	}

	public abstract double getHighestScore();
}
