package lia.tools;

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

import junit.framework.TestCase;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.*;
import org.apache.lucene.util.*;
import org.apache.lucene.queryParser.standard.StandardQueryParser;
import org.apache.lucene.queryParser.core.QueryNodeException;

// From chapter 9
public class FlexibleQueryParserTest extends TestCase {

  public void testSimple() throws Exception {
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
    StandardQueryParser parser = new StandardQueryParser(analyzer);
    Query q = null;
    try {
      q = parser.parse("(agile OR extreme) AND methodology", "subject");
    } catch (QueryNodeException exc) {
      // TODO: handle exc
    }
    System.out.println("parsed " + q);
  }

  public void testNoFuzzyOrWildcard() throws Exception {
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
    StandardQueryParser parser = new CustomFlexibleQueryParser(analyzer);
    try {
      parser.parse("agil*", "subject");
      fail("didn't hit expected exception");
    } catch (QueryNodeException exc) {
      // expected
    }

    try {
      parser.parse("agil~0.8", "subject");
      fail("didn't hit expected exception");
    } catch (QueryNodeException exc) {
      // expected
    }
  }

  public void testPhraseQuery() throws Exception {
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
    StandardQueryParser parser = new CustomFlexibleQueryParser(analyzer);

    Query query = parser.parse("singleTerm", "subject");
    assertTrue("TermQuery", query instanceof TermQuery);

    query = parser.parse("\"a phrase test\"", "subject");
    System.out.println("got query=" + query);
    assertTrue("SpanNearQuery", query instanceof SpanNearQuery);
  }

}
