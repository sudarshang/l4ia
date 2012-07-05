package lia.advsearching;

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

import lia.common.TestUtil;
import junit.framework.TestCase;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.util.Version;
import org.apache.lucene.store.Directory;

// From chapter 5
public class MultiFieldQueryParserTest extends TestCase {
  public void testDefaultOperator() throws Exception {
    Query query = new MultiFieldQueryParser(Version.LUCENE_30,
                                            new String[]{"title", "subject"},
        new SimpleAnalyzer()).parse("development");

    Directory dir = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(
                               dir,
                               true);
    TopDocs hits = searcher.search(query, 10);

    assertTrue(TestUtil.hitsIncludeTitle(
           searcher,
           hits,
           "Ant in Action"));

    assertTrue(TestUtil.hitsIncludeTitle(     //A
           searcher,                          //A
           hits,                              //A
           "Extreme Programming Explained")); //A
    searcher.close();
    dir.close();
  }

  public void testSpecifiedOperator() throws Exception {
    Query query = MultiFieldQueryParser.parse(Version.LUCENE_30,
        "lucene",
        new String[]{"title", "subject"},
        new BooleanClause.Occur[]{BooleanClause.Occur.MUST,
                  BooleanClause.Occur.MUST},
        new SimpleAnalyzer());

    Directory dir = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(
                               dir,
                               true);
    TopDocs hits = searcher.search(query, 10);

    assertTrue(TestUtil.hitsIncludeTitle(
            searcher,
            hits,
            "Lucene in Action, Second Edition"));
    assertEquals("one and only one", 1, hits.scoreDocs.length);
    searcher.close();
    dir.close();
  }

  /*
    #A Has development in the subject field
   */

}
