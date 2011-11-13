package com.mayonnaise.sieve;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface Article {

	/**
	 *  Gets the article's URL
	 */
	String getArticleUrl();

	/**
	 * Gets the category of the article. This is probably going to be dependent
	 * on what feed this article originated from.
	 */
	String getCategory();

	/**
	 *  Gets a short description of the article.
	 */
	String getDescription();
	
	/**
	 *  Fetches the content from the article URL and extracts out the main 
	 *  article content.
	 */
	void fetchContent();

	String getContent();
}