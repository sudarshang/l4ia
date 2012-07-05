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

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

// From chapter 5
public class SecurityFilterTest extends TestCase {

  private IndexSearcher searcher;

  protected void setUp() throws Exception {
    Directory directory = new RAMDirectory();
    IndexWriter writer = new IndexWriter(directory,
                                         new WhitespaceAnalyzer(),
                                         IndexWriter.MaxFieldLength.UNLIMITED);

    Document document = new Document();                  // 1
    document.add(new Field("owner",                      // 1
                           "elwood",                     // 1
                           Field.Store.YES,              // 1
                           Field.Index.NOT_ANALYZED));   // 1
    document.add(new Field("keywords",                   // 1
                           "elwood's sensitive info",    // 1
                           Field.Store.YES,              // 1
                           Field.Index.ANALYZED));       // 1
    writer.addDocument(document);

    document = new Document();                           // 2
    document.add(new Field("owner",                      // 2
                           "jake",                       // 2
                           Field.Store.YES,              // 2
                           Field.Index.NOT_ANALYZED));   // 2
    document.add(new Field("keywords",                   // 2
                           "jake's sensitive info",      // 2
                           Field.Store.YES,              // 2
                           Field.Index.ANALYZED));       // 2
    writer.addDocument(document);

    writer.close();
    searcher = new IndexSearcher(directory);
  }
  /*
#1 Elwood
#2 Jake
  */

  public void testSecurityFilter() throws Exception {
    TermQuery query = new TermQuery(                   //#1
                        new Term("keywords", "info")); //#1

    assertEquals("Both documents match",               //#2
                 2,                                    //#2
                 TestUtil.hitCount(searcher, query));  //#2

    Filter jakeFilter = new QueryWrapperFilter(        //#3
      new TermQuery(new Term("owner", "jake")));       //#3

    TopDocs hits = searcher.search(query, jakeFilter, 10);
    assertEquals(1, hits.totalHits);                   //#4
    assertEquals("elwood is safe",                     //#4
                 "jake's sensitive info",              //#4
        searcher.doc(hits.scoreDocs[0].doc)            //#4
                 .get("keywords"));                    //#4
  }
  /*
    #1 TermQuery for "info"
    #2 Returns documents containing "info"
    #3 Filter
    #4 Same TermQuery, constrained results
  */
}
