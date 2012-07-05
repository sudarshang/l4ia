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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.SpanQueryFilter;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.FieldCacheRangeFilter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;

// From chapter 5
public class FilterTest extends TestCase {
  private Query allBooks;
  private IndexSearcher searcher;
  private Directory dir;

  protected void setUp() throws Exception {    // #1
    allBooks = new MatchAllDocsQuery();
    dir = TestUtil.getBookIndexDirectory();
    searcher = new IndexSearcher(dir);
  }

  protected void tearDown() throws Exception {
    searcher.close();
    dir.close();
  }

  public void testTermRangeFilter() throws Exception {
    Filter filter = new TermRangeFilter("title2", "d", "j", true, true);
    assertEquals(3, TestUtil.hitCount(searcher, allBooks, filter));
  }

  /*
    #1 setUp() establishes baseline book count
  */

  public void testNumericDateFilter() throws Exception {
    // pub date of Lucene in Action, Second Edition and
    // JUnit in ACtion, Second Edition is May 2010
    Filter filter = NumericRangeFilter.newIntRange("pubmonth",
                                                   201001,
                                                   201006,
                                                   true,
                                                   true);
    assertEquals(2, TestUtil.hitCount(searcher, allBooks, filter));
  }

  public void testFieldCacheRangeFilter() throws Exception {
    Filter filter = FieldCacheRangeFilter.newStringRange("title2", "d", "j", true, true);
    assertEquals(3, TestUtil.hitCount(searcher, allBooks, filter));

    filter = FieldCacheRangeFilter.newIntRange("pubmonth",
                                               201001,
                                               201006,
                                               true,
                                               true);
    assertEquals(2, TestUtil.hitCount(searcher, allBooks, filter));
  }
  
  public void testFieldCacheTermsFilter() throws Exception {
    Filter filter = new FieldCacheTermsFilter("category",
                      new String[] {"/health/alternative/chinese",
                                    "/technology/computers/ai",
                                    "/technology/computers/programming"});
    assertEquals("expected 7 hits",
                 7,
                 TestUtil.hitCount(searcher, allBooks, filter));
  }

  public void testQueryWrapperFilter() throws Exception {
    TermQuery categoryQuery =
       new TermQuery(new Term("category", "/philosophy/eastern"));

    Filter categoryFilter = new QueryWrapperFilter(categoryQuery);

    assertEquals("only tao te ching",
                 1,
                 TestUtil.hitCount(searcher, allBooks, categoryFilter));
  }

  public void testSpanQueryFilter() throws Exception {
    SpanQuery categoryQuery =
       new SpanTermQuery(new Term("category", "/philosophy/eastern"));

    Filter categoryFilter = new SpanQueryFilter(categoryQuery);

    assertEquals("only tao te ching",
                 1,
                 TestUtil.hitCount(searcher, allBooks, categoryFilter));
  }

  public void testFilterAlternative() throws Exception {
    TermQuery categoryQuery =
       new TermQuery(new Term("category", "/philosophy/eastern"));

    BooleanQuery constrainedQuery = new BooleanQuery();
    constrainedQuery.add(allBooks, BooleanClause.Occur.MUST);
    constrainedQuery.add(categoryQuery, BooleanClause.Occur.MUST);

    assertEquals("only tao te ching",
                 1,
                 TestUtil.hitCount(searcher, constrainedQuery));
  }

  public void testPrefixFilter() throws Exception {
    Filter prefixFilter = new PrefixFilter(
                            new Term("category",
                                     "/technology/computers"));
    assertEquals("only /technology/computers/* books",
                 8,
                 TestUtil.hitCount(searcher,
                                   allBooks,
                                   prefixFilter));
  }

  public void testCachingWrapper() throws Exception {
    Filter filter = new TermRangeFilter("title2",
                                        "d", "j",
                                        true, true);

    CachingWrapperFilter cachingFilter;
    cachingFilter = new CachingWrapperFilter(filter);
    assertEquals(3,
                 TestUtil.hitCount(searcher,
                                   allBooks,
                                   cachingFilter));
  }
}
