package com.mayonnaise.segmentation;


import com.mayonnaise.common.Sentence;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 *  A {@code Cluster} represents a group of {@code Sentence}s that seem to be
 *  talking about a similar idea/concept.
 *
 *  @author thuang513@gmail.com (Terry Huang)
 */
public class Cluster implements Comparable {
	
	private final Integer clusterId;
	
	private Set<Sentence> sentences;

	public Cluster(int id) {
		clusterId = Integer.valueOf(id);
		sentences = new TreeSet<Sentence>();
	}

	/**
	 *  Add a {@code Sentence} to this cluster. This operation will also add the 
	 *  {@code Cluster} to the {@code Sentence}.
	 */
	public void addSentence(Sentence s) {
		sentences.add(s);
		s.addCluster(this);
	}

	public Set<Sentence> getSentences() {
		return Collections.unmodifiableSet(sentences);
	}

	public int getClusterId() {
		return clusterId.intValue();
	}

	public int compareTo(Object other) {
		Integer otherSize = Integer.valueOf(((Cluster)other).getSentences().size());
		Integer currentSize = Integer.valueOf(getSentences().size());
		return currentSize.compareTo(otherSize);

		/*
			Integer otherId = Integer.valueOf(((Cluster)other).getClusterId());
			return clusterId.compareTo(otherId);
		*/
	}
	
	public boolean equals(Object other) {
		return hashCode() == other.hashCode();
	}
	public String toString() {
		return clusterId.toString();
	}

	public int hashCode() {
		return sentences.hashCode();
	}
}

