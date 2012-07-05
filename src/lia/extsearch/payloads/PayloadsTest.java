package lia.extsearch.payloads;

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

import java.io.IOException;

import junit.framework.TestCase;

import lia.common.TestUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.search.payloads.AveragePayloadFunction;

// From chapter 6
public class PayloadsTest extends TestCase {

  Directory dir;
  IndexWriter writer;
  BulletinPayloadsAnalyzer analyzer;

  protected void setUp() throws Exception {
    super.setUp();
    dir = new RAMDirectory();
    analyzer = new BulletinPayloadsAnalyzer(5.0F);                  // #A
    writer = new IndexWriter(dir, analyzer,
                             IndexWriter.MaxFieldLength.UNLIMITED);
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    writer.close();
  }

  void addDoc(String title, String contents) throws IOException {
    Document doc = new Document();
    doc.add(new Field("title",
                      title,
                      Field.Store.YES,
                      Field.Index.NO));
    doc.add(new Field("contents",
                      contents,
                      Field.Store.NO,
                      Field.Index.ANALYZED));
    analyzer.setIsBulletin(contents.startsWith("Bulletin:"));
    writer.addDocument(doc);
  }

  public void testPayloadTermQuery() throws Throwable {
    addDoc("Hurricane warning",
           "Bulletin: A hurricane warning was issued at " +
           "6 AM for the outer great banks");
    addDoc("Warning label maker",
           "The warning label maker is a delightful toy for " +
           "your precocious seven year old's warning needs");
    addDoc("Tornado warning",
           "Bulletin: There is a tornado warning for " +
           "Worcester county until 6 PM today");

    IndexReader r = writer.getReader();
    writer.close();

    IndexSearcher searcher = new IndexSearcher(r);

    searcher.setSimilarity(new BoostingSimilarity());

    Term warning = new Term("contents", "warning");
    
    Query query1 = new TermQuery(warning);
    System.out.println("\nTermQuery results:");
    TopDocs hits = searcher.search(query1, 10);
    TestUtil.dumpHits(searcher, hits);

    assertEquals("Warning label maker",                                // #B
                 searcher.doc(hits.scoreDocs[0].doc).get("title"));    // #B

    Query query2 = new PayloadTermQuery(warning,
                                        new AveragePayloadFunction());
    System.out.println("\nPayloadTermQuery results:");
    hits = searcher.search(query2, 10);
    TestUtil.dumpHits(searcher, hits);

    assertEquals("Warning label maker",                                // #C
                 searcher.doc(hits.scoreDocs[2].doc).get("title"));    // #C
    r.close();
    searcher.close();
  }
}

/*
  #A Boost by 5.0
  #B Ranks first
  #C Ranks last after boosts
*/
