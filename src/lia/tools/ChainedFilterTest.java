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

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.misc.ChainedFilter;
import java.util.Calendar;

import lia.common.TestUtil;

// From chapter 9
public class ChainedFilterTest extends TestCase {
  public static final int MAX = 500;
  private RAMDirectory directory;
  private IndexSearcher searcher;
  private Query query;
  private Filter dateFilter;
  private Filter bobFilter;
  private Filter sueFilter;

  public void setUp() throws Exception {
    directory = new RAMDirectory();
    IndexWriter writer =
       new IndexWriter(directory, new WhitespaceAnalyzer(),
                       IndexWriter.MaxFieldLength.UNLIMITED);

    Calendar cal = Calendar.getInstance();
    cal.set(2009, 1, 1, 0, 0); // A

    for (int i = 0; i < MAX; i++) {
      Document doc = new Document();
      doc.add(new Field("key", "" + (i + 1),
                         Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field("owner", (i < MAX / 2) ? "bob" : "sue",
                         Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field("date", DateTools.timeToString(cal.getTimeInMillis(), DateTools.Resolution.DAY),
                         Field.Store.YES, Field.Index.NOT_ANALYZED));
      writer.addDocument(doc);

      cal.add(Calendar.DATE, 1);
    }

    writer.close();

    searcher = new IndexSearcher(directory);

    BooleanQuery bq = new BooleanQuery();           // B
    bq.add(new TermQuery(new Term("owner", "bob")), // B
           BooleanClause.Occur.SHOULD);             // B
    bq.add(new TermQuery(new Term("owner", "sue")), // B
           BooleanClause.Occur.SHOULD);             // B
    query = bq;

    cal.set(2099, 1, 1, 0, 0);
    dateFilter = TermRangeFilter.Less("date",        // C
                      DateTools.timeToString(        // C
                          cal.getTimeInMillis(),     // C
                          DateTools.Resolution.DAY));// C

    bobFilter = new CachingWrapperFilter(                   // D
              new QueryWrapperFilter(                       // D
                 new TermQuery(new Term("owner", "bob")))); // D

    sueFilter = new CachingWrapperFilter(                // E
        new QueryWrapperFilter(                          // E
              new TermQuery(new Term("owner", "sue")))); // E
  }

  /*
    #A 2009 January 01
    #B BooleanQuery that matches all documents
    #C Date filter also matches all documents
    #D Matches only Bob's documents
    #E Matches only Sue's documents
   */

  public void testSingleFilter() throws Exception {
    ChainedFilter chain = new ChainedFilter(
                                            new Filter[] {dateFilter});
    TopDocs hits = searcher.search(query, chain, 10);
    assertEquals(MAX, hits.totalHits);

    chain = new ChainedFilter(new Filter[] {bobFilter});
    assertEquals(MAX / 2, TestUtil.hitCount(searcher, query, chain), hits.totalHits);
  }

  public void testOR() throws Exception {
    ChainedFilter chain = new ChainedFilter(
              new Filter[] {sueFilter, bobFilter});

    assertEquals("OR matches all", MAX, TestUtil.hitCount(searcher, query, chain));
  }

  public void testAND() throws Exception {
    ChainedFilter chain = new ChainedFilter(
              new Filter[] {dateFilter, bobFilter}, ChainedFilter.AND);
    TopDocs hits = searcher.search(query, chain, 10);
    assertEquals("AND matches just Bob", MAX / 2, hits.totalHits);
    Document firstDoc = searcher.doc(hits.scoreDocs[0].doc);
    assertEquals("bob", firstDoc.get("owner"));
  }

  public void testXOR() throws Exception {
    ChainedFilter chain = new ChainedFilter(
      new Filter[]{dateFilter, bobFilter}, ChainedFilter.XOR);
    TopDocs hits = searcher.search(query, chain, 10);
    assertEquals("XOR matches Sue", MAX / 2, hits.totalHits);
    Document firstDoc = searcher.doc(hits.scoreDocs[0].doc);
    assertEquals("sue", firstDoc.get("owner"));
  }

  public void testANDNOT() throws Exception {
    ChainedFilter chain = new ChainedFilter(
      new Filter[]{dateFilter, sueFilter},
        new int[] {ChainedFilter.AND, ChainedFilter.ANDNOT});

    TopDocs hits = searcher.search(query, chain, 10);
    assertEquals("ANDNOT matches just Bob",
                 MAX / 2, hits.totalHits);
    Document firstDoc = searcher.doc(hits.scoreDocs[0].doc);
    assertEquals("bob", firstDoc.get("owner"));
  }
}
