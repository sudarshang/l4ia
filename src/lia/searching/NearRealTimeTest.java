package lia.searching;

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

import org.apache.lucene.util.Version;
import org.apache.lucene.store.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import junit.framework.TestCase;

// From chapter 3
public class NearRealTimeTest extends TestCase {
  public void testNearRealTime() throws Exception {
    Directory dir = new RAMDirectory();
    IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), IndexWriter.MaxFieldLength.UNLIMITED);
    for(int i=0;i<10;i++) {
      Document doc = new Document();
      doc.add(new Field("id", ""+i, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
      doc.add(new Field("text", "aaa", Field.Store.NO, Field.Index.ANALYZED));
      writer.addDocument(doc);
    }
    IndexReader reader = writer.getReader();                 // #1
    IndexSearcher searcher = new IndexSearcher(reader);      // #A

    Query query = new TermQuery(new Term("text", "aaa"));
    TopDocs docs = searcher.search(query, 1);
    assertEquals(10, docs.totalHits);                        // #B

    writer.deleteDocuments(new Term("id", "7"));             // #2

    Document doc = new Document();                           // #3
    doc.add(new Field("id",                                  // #3
                      "11",                                  // #3
                      Field.Store.NO,                        // #3
                      Field.Index.NOT_ANALYZED_NO_NORMS));   // #3
    doc.add(new Field("text",                                // #3
                      "bbb",                                 // #3
                      Field.Store.NO,                        // #3
                      Field.Index.ANALYZED));                // #3
    writer.addDocument(doc);                                 // #3
    
    IndexReader newReader = reader.reopen();                 // #4
    assertFalse(reader == newReader);                        // #5
    reader.close();                                          // #6
    searcher = new IndexSearcher(newReader);              

    TopDocs hits = searcher.search(query, 10);               // #7
    assertEquals(9, hits.totalHits);                         // #7

    query = new TermQuery(new Term("text", "bbb"));          // #8
    hits = searcher.search(query, 1);                        // #8
    assertEquals(1, hits.totalHits);                         // #8

    newReader.close();
    writer.close();
  }
}

/*
  #1 Create near-real-time reader
  #A Wrap reader in IndexSearcher
  #B Search returns 10 hits
  #2 Delete 1 document
  #3 Add 1 document
  #4 Reopen reader
  #5 Confirm reader is new
  #6 Close old reader
  #7 Verify 9 hits now
  #8 Confirm new document matched
*/

/*
#1 IndexWriter returns a reader that's able to search all previously committed changes to the index, plus any uncommitted changes.  The returned reader is always readOnly.
#2,#3 We make changes to the index, but do not commit them.
#4,#5,#6 Ask the reader to reopen.  Note that this simply re-calls writer.getReader again under the hood.  Because we made changes, the newReader will be different from the old one so we must close the old one.
#7, #8 The changes made with the writer are reflected in new searches.
*/