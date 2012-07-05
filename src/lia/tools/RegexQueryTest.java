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
import org.apache.lucene.search.regex.RegexQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import lia.common.TestUtil;

// From chapter 8
public class RegexQueryTest extends TestCase {

  public void testRegexQuery() throws Exception {
    Directory directory = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(directory);
    RegexQuery q = new RegexQuery(new Term("title", ".*st.*"));
    TopDocs hits = searcher.search(q, 10);
    assertEquals(2, hits.totalHits);
    assertTrue(TestUtil.hitsIncludeTitle(searcher, hits,
                                         "Tapestry in Action"));
    assertTrue(TestUtil.hitsIncludeTitle(searcher, hits,
                                         "Mindstorms: Children, Computers, And Powerful Ideas"));
    searcher.close();
    directory.close();
  }
}
