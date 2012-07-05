package lia.tika;

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
*/

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// From chapter 7
public class SAXXMLDocument extends DefaultHandler {

  private StringBuilder elementBuffer = new StringBuilder();
  private Map<String,String> attributeMap = new HashMap<String,String>();

  private Document doc;

  public Document getDocument(InputStream is)  // #1
    throws DocumentHandlerException {

    SAXParserFactory spf = SAXParserFactory.newInstance();
    try {
      SAXParser parser = spf.newSAXParser();
      parser.parse(is, this);
    } catch (Exception e) {
      throw new DocumentHandlerException(
        "Cannot parse XML document", e);
    }

    return doc;
  }

  public void startDocument() {             // #2
    doc = new Document();
  }

  public void startElement(String uri, String localName,  // #3
    String qName, Attributes atts)                        // #3
    throws SAXException {                                 // #3

    elementBuffer.setLength(0);
    attributeMap.clear();
    int numAtts = atts.getLength();
		if (numAtts > 0) {
      for (int i = 0; i < numAtts; i++) {
        attributeMap.put(atts.getQName(i), atts.getValue(i));
      }
    }
  }

  public void characters(char[] text, int start, int length) {  // #4
    elementBuffer.append(text, start, length);
  }

  public void endElement(String uri, String localName, String qName)  // #5
    throws SAXException {
    if (qName.equals("address-book")) {
      return;
    }
    else if (qName.equals("contact")) {
    	for (Entry<String,String> attribute : attributeMap.entrySet()) {
    		String attName = attribute.getKey();
    		String attValue = attribute.getValue();
    		doc.add(new Field(attName, attValue, Field.Store.YES, Field.Index.NOT_ANALYZED));
    	}
    }
    else {
      doc.add(new Field(qName, elementBuffer.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    }
  }

  public static void main(String args[]) throws Exception {
    SAXXMLDocument handler = new SAXXMLDocument();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}

/*
#1 Start parser
#2 Called when parsing begins
#3 Beginning of new XML element
#4 Append element contents to elementBuffer
#5 Called when closing XML elements are processed
*/
