package com.mayonnaise.common;

import edu.stanford.nlp.process.WhitespaceTokenizer;
import edu.ucla.sspace.vector.DoubleVector;
import edu.ucla.sspace.basis.StringBasisMapping;
import edu.ucla.sspace.vector.SparseHashDoubleVector;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mayonnaise.common.Sentence;
import com.mayonnaise.segmentation.Cluster;

/**
 *  An abstract Sentence class that provides the general implementation
 *  on what all {@code Sentence}s should do. All {@code Sentence}s should
 *  extend this abstract class.
 *
 *  @author thuang513@gmail.com (Terry Huang).
 */
public abstract class AbstractSentence implements Sentence {

	/**
	 *  The cluster that this sentence is associated with.
	 */
	private Cluster myCluster = null;

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
	 *  Constructor for the abstract Sentence.
	 */
	protected AbstractSentence(String rawContent, StringBasisMapping basis) {
		this.rawContent = rawContent;
		this.basis = basis;
		myGroupies = new TreeSet();
	}

	public void tokenize() {
		StringReader reader = new StringReader(rawContent);
		WhitespaceTokenizer<Word> tokenizer =
			                     WhitespaceTokenizer.newWordWhitespaceTokenizer(reader);

	}

	/**
	 *  Returns the content of the string.
	 */
	public String getRawContent() {
		return rawContent;
	}
	
	public DoubleVector getSentenceVector() {
		// Make sure that the double vector cannot be modified!
		// return new SparseHashDoubleVector(sentenceVector);
		return sentenceVector;
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
	 *  Get the cluster that this class belongs to.
	 */
	public Cluster getCluster() {
		return myCluster;
	}

	/**
	 *  Set the cluster that this sentence belongs to.
	 */
	public void setCluster(Cluster c) {
		myCluster = c;
	}

	/**
	 * Get the top scoring sentences.
	 */
	public List getTopScores();
	
	
}
