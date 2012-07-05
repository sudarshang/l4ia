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

import java.io.File;

import lia.common.TestUtil;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.util.*;

// From chapter 3
public class Fragments {

  public void openSearcher() throws Exception {
    Directory dir = FSDirectory.open(new File("/path/to/index"));
    IndexReader reader = IndexReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);
  }

  public void nrtReader() throws Exception {
    IndexReader reader = null;
    IndexSearcher searcher;
    // START
    IndexReader newReader = reader.reopen();
    if (reader != newReader) {
      reader.close();
      reader = newReader;
      searcher = new IndexSearcher(reader);
    }
    // END
  }

  public void testSearchSigs() throws Exception {
    Query query = null;
    Filter filter = null;
    TopDocs hits;
    TopFieldDocs fieldHits;
    Sort sort = null;
    Collector collector = null;
    int n = 10;
    IndexSearcher searcher = null;

    hits = searcher.search(query, n);
    hits = searcher.search(query, filter, n);
    fieldHits = searcher.search(query, filter, n, sort);
    searcher.search(query, collector);
    searcher.search(query, filter, collector);
  }

  public void queryParserOperator() throws Exception {
    Analyzer analyzer = null;
    // START
    QueryParser parser = new QueryParser(Version.LUCENE_30,
                                         "contents", analyzer);
    parser.setDefaultOperator(QueryParser.AND_OPERATOR);
    // END
  }
}