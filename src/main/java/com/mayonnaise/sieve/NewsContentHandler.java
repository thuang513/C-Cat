package com.mayonnaise.sieve;

import java.util.logging.Logger;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public cliass NewsContentHandler extends DefaultHandler {

	/** 
	 *  Setup the logger 
	 */
	private static final Logger LOGGER = 
		Logger.getLogger(NewsHandler.class.getName());

	private static final String NEWLINE_TAG = "p";

	/**
	 *  The id of the div that holds all the content.
	 */
	private String contentHolderId;
 
	private boolean isInContentDiv = false;
	private boolean shouldGrabContent = false;

	private StringBuilder newsContent = null;

	public NewsContentHandler(String contentHolderId) {
		this.contentHolderId = contentHolderId;
		newsContent = new StringBuilder();
	}
	
	public String getContent() {
		return newsContent.toString();
	}

	
	public void startElement(String uri,
	                         String localName,
	                         String qName,
	                         Attributes attributes) throws SAXException {
		if (qName.equals(contentHolderId)) {
			isInContentDiv = true;
		}

		if (isInContentDiv && qName.equals(NEWLINE_TAG)) {
			// We should grab all the content
			shouldGrabContent = true;
		}
	}


	public void characters(char[] ch,
												 int start,
												 int length) throws SAXException {

		// Just for safety, make sure we are at least in the content div.
		if (isInContentDiv && shouldGrabContent) {
			newsContent.append(ch, start, length).append("\n");
		}
	}

	public void endElement(String uri,
												 String localName,
												 String qName) throws SAXException {
		
		if (isInContentDiv && qName.equals(NEWLINE_TAG)) {
			shouldGrabContent = false;
		}

		if (qName.equals(contentHolderId)) {
			isInContentDiv = false;
		}
	}

}