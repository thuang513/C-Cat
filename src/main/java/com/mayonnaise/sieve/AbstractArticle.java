package com.mayonnaise.sieve;


import com.mayonnaise.sieve.NewsContentHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractArticle implements Article {
	
	/** 
	 *  Setup the logger 
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(AbstractArticle.class.getName());
	private String articleUrl;

	private String category;

	private String description;

	private DefaultHandler newsHandler;

	private String contentDivId;

	private String articleContent = "";

	public AbstractArticle(String articleUrl,
												 String category,
												 String description,
												 DefaultHandler newsHandler,
												 String contentDivId) {
		this.articleUrl = articleUrl;
		this.category = category;
		this.description = description;
		this.newsHandler = newsHandler;
		this.contentDivId = contentDivId;
	}

	/**
	 * {@inherit}
	 */
	public String getArticleUrl() {
		return articleUrl;
	}

	/**
	 * {@inherit}
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * {@inherit}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * {@inherit}
	 */
	public void fetchContent() {
		SAXParserFactory parserFactory = new SAXParserFactory.newInstance();
		NewsContentHandler newsHandler = new NewsContentHandler(contentDivId);

		try {
			SAXParser parser = parserFactory.newSAXParser();
			parser.parse(feedUrl, newsHandler);
		}catch (IOException ioe) {
			LOGGER.severe("IO error occurred!");
		} catch (SAXException sax) {
			LOGGER.severe("Error with the parser!");
		} catch (ParserConfigurationException pce) {
			LOGGER.severe("Parser configuration error!");
		}

		articleContent = newsHandler.getContent();
	}

	/**
	 * {@inherit}
	 */
	public String getContent() {
		return articleContent;
	}

	/**
	 *  Writes the content of the article into a File.
	 */
	public void writeToFile(File outFile) throws IOException {
		FileWriter out = new FileWriter(outFile);
		
		out.write(getContent());
				
		// Do some clean up work.
		out.flush();
		out.close();
	}
}