package com.mayonnaise.main;


import com.mayonnaise.common.Sentence;
import com.mayonnaise.segmentation.DocumentSegmenter;

import java.util.List;
import java.util.logging.Logger;

public class ClusterSentences {

	/**
	 * Setup the logger.
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(ClusterSentences.class.getName());

	public static void usage() {
		System.out.println("ClusterSentences  <stopword file> <document to cluster>");
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
			System.exit(-1);
		}

		// Find the document to read.
		String stopWordFile = args[0];
		String document = args[1];

		// Put the sentences in clusters.
		List<Sentence> sentences =
			DocumentSegmenter.createClustersOfDocument(document, stopWordFile);

		/*
		// Go through all the sentences and print what cluster they belong to.
		for (Sentence s : sentences) {
			System.out.println(s);
			System.out.println("CLUSTERS = " + s.getClusterSet() + "\n");
		}
		*/

		// Go through all the clusters, and print out Sentences in their clusters.
		//		for (
	}
}