package lia.extsearch.collector;

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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.util.Map;

// From chapter 6
public class CollectorTest extends TestCase {

  public void testCollecting() throws Exception {
    Directory dir = TestUtil.getBookIndexDirectory();
    TermQuery query = new TermQuery(new Term("contents", "junit"));
    IndexSearcher searcher = new IndexSearcher(dir);

    BookLinkCollector collector = new BookLinkCollector();
    searcher.search(query, collector);

    Map<String,String> linkMap = collector.getLinks();
    assertEquals("ant in action",
                 linkMap.get("http://www.manning.com/loughran"));

    TopDocs hits = searcher.search(query, 10);
    TestUtil.dumpHits(searcher, hits);

    searcher.close();
    dir.close();
  }
}
