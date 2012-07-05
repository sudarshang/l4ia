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

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TimeLimitingCollector;
import org.apache.lucene.search.TimeLimitingCollector.TimeExceededException;

// From chapter 5
public class TimeLimitingCollectorTest extends TestCase {
  public void testTimeLimitingCollector() throws Exception {
    Directory dir = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(dir);
    Query q = new MatchAllDocsQuery();
    int numAllBooks = TestUtil.hitCount(searcher, q);

    TopScoreDocCollector topDocs = TopScoreDocCollector.create(10, false);
    Collector collector = new TimeLimitingCollector(topDocs,  // #A
                                                    1000);    // #A
    try {
      searcher.search(q, collector);
      assertEquals(numAllBooks, topDocs.getTotalHits());  // #B
    } catch (TimeExceededException tee) {                 // #C
      System.out.println("Too much time taken.");         // #C
    }                                                     // #C
    searcher.close();
    dir.close();
  }
}

/*
  #A Wrap any existing Collector
  #B If no timeout, we should have all hits
  #C Timeout hit
*/
