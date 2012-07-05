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
import java.util.Date;
import java.io.IOException;

import lia.common.TestUtil;

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.CustomScoreProvider;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.util.Version;

// From chapter 5
public class FunctionQueryTest extends TestCase {

  IndexSearcher s;
  IndexWriter w;

  private void addDoc(int score, String content) throws Exception {
    Document doc = new Document();
    doc.add(new Field("score",
                      Integer.toString(score),
                      Field.Store.NO,
                      Field.Index.NOT_ANALYZED_NO_NORMS));
    doc.add(new Field("content",
                      content,
                      Field.Store.NO,
                      Field.Index.ANALYZED));
    w.addDocument(doc);
  }

  public void setUp() throws Exception {
    Directory dir = new RAMDirectory();
    w = new IndexWriter(dir,
                        new StandardAnalyzer(
                                 Version.LUCENE_30),
                        IndexWriter.MaxFieldLength.UNLIMITED);
    addDoc(7, "this hat is green");
    addDoc(42, "this hat is blue");
    w.close();

    s = new IndexSearcher(dir, true);
  }

  public void tearDown() throws Exception {
    super.tearDown();
    s.close();
  }

  public void testFieldScoreQuery() throws Throwable {
    Query q = new FieldScoreQuery("score", FieldScoreQuery.Type.BYTE);
    TopDocs hits = s.search(q, 10);
    assertEquals(2, hits.scoreDocs.length);       // #1
    assertEquals(1, hits.scoreDocs[0].doc);       // #2
    assertEquals(42, (int) hits.scoreDocs[0].score);
    assertEquals(0, hits.scoreDocs[1].doc);
    assertEquals(7, (int) hits.scoreDocs[1].score);
  }

  /*
    #1 All documents match
    #2 Doc 1 is first because its static score (42) is
       higher than doc 0's (7)
  */

  public void testCustomScoreQuery() throws Throwable {
    Query q = new QueryParser(Version.LUCENE_30,
                              "content",
                              new StandardAnalyzer(
                                Version.LUCENE_30))
                 .parse("the green hat");
    FieldScoreQuery qf = new FieldScoreQuery("score",
                                             FieldScoreQuery.Type.BYTE);
    CustomScoreQuery customQ = new CustomScoreQuery(q, qf) {
      public CustomScoreProvider getCustomScoreProvider(IndexReader r) {
        return new CustomScoreProvider(r) {
          public float customScore(int doc,
                                   float subQueryScore,
                                   float valSrcScore) {
            return (float) (Math.sqrt(subQueryScore) * valSrcScore);
          }
        };
      }
    };

    TopDocs hits = s.search(customQ, 10);
    assertEquals(2, hits.scoreDocs.length);
    
    assertEquals(1, hits.scoreDocs[0].doc);           // #1
    assertEquals(0, hits.scoreDocs[1].doc);
  }

  /*
    #1 Even though document 0 is a better match to the
       original query, document 1 gets a better score after
       multiplying in its score field
   */

  static class RecencyBoostingQuery extends CustomScoreQuery {

    double multiplier;
    int today;
    int maxDaysAgo;
    String dayField;
    static int MSEC_PER_DAY = 1000*3600*24;

    public RecencyBoostingQuery(Query q, double multiplier,
                                int maxDaysAgo, String dayField) {
      super(q);
      today = (int) (new Date().getTime()/MSEC_PER_DAY);
      this.multiplier = multiplier;
      this.maxDaysAgo = maxDaysAgo;
      this.dayField = dayField;
    }

    private class RecencyBooster extends CustomScoreProvider {
      final int[] publishDay;

      public RecencyBooster(IndexReader r) throws IOException {
        super(r);
        publishDay = FieldCache.DEFAULT                       // #A
             .getInts(r, dayField);                           // #A
      }

      public float customScore(int doc, float subQueryScore,
                               float valSrcScore) {
        int daysAgo = today - publishDay[doc];                // #B
        if (daysAgo < maxDaysAgo) {                           // #C            
          float boost = (float) (multiplier *                 // #D
                                 (maxDaysAgo-daysAgo)         // #D
                                 / maxDaysAgo);               // #D
          return (float) (subQueryScore * (1.0+boost));
        } else {
          return subQueryScore;                               // #E
        }
      }
    }

    public CustomScoreProvider getCustomScoreProvider(IndexReader r) throws IOException {
      return new RecencyBooster(r);
    }
  }

  /*
    #A Retrieve days from field cache
    #B Compute elapsed days
    #C Skip old books
    #D Compute simple linear boost
    #E Return un-boosted score
  */

  public void testRecency() throws Throwable {
    Directory dir = TestUtil.getBookIndexDirectory();
    IndexReader r = IndexReader.open(dir);
    IndexSearcher s = new IndexSearcher(r);
    s.setDefaultFieldSortScoring(true, true);

    QueryParser parser = new QueryParser(
                            Version.LUCENE_30,
                            "contents",
                            new StandardAnalyzer(
                              Version.LUCENE_30));
    Query q = parser.parse("java in action");       // #A
    Query q2 = new RecencyBoostingQuery(q,          // #B
                                        2.0, 2*365,
                                        "pubmonthAsDay");
    Sort sort = new Sort(new SortField[] {
        SortField.FIELD_SCORE,
        new SortField("title2", SortField.STRING)});
    TopDocs hits = s.search(q2, null, 5, sort);

    for (int i = 0; i < hits.scoreDocs.length; i++) {
      Document doc = r.document(hits.scoreDocs[i].doc);
      System.out.println((1+i) + ": " +
                         doc.get("title") +
                         ": pubmonth=" +
                         doc.get("pubmonth") +
                         " score=" + hits.scoreDocs[i].score);
    }
    s.close();
    r.close();
    dir.close();
  }

  /*
    #A Parse query
    #B Create recency boosting query
  */
}
