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

import junit.framework.TestCase;

import lia.common.TestUtil;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

// From chapter 3
public class PrefixQueryTest extends TestCase {
  public void testPrefix() throws Exception {
    Directory dir = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(dir);

    Term term = new Term("category",                              //#A
                         "/technology/computers/programming");    //#A
    PrefixQuery query = new PrefixQuery(term);                    //#A

    TopDocs matches = searcher.search(query, 10);                 //#A
    int programmingAndBelow = matches.totalHits;

    matches = searcher.search(new TermQuery(term), 10);           //#B
    int justProgramming = matches.totalHits;

    assertTrue(programmingAndBelow > justProgramming);
    searcher.close();
    dir.close();
  }
}

/*
  #A Search, including subcategories
  #B Search, without subcategories
*/

