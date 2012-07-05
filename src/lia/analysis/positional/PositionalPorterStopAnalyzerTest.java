package lia.analysis.positional;

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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

// From chapter 4
public class PositionalPorterStopAnalyzerTest extends TestCase {
  private static PositionalPorterStopAnalyzer porterAnalyzer =
    new PositionalPorterStopAnalyzer();

  private IndexSearcher searcher;
  private QueryParser parser;

  public void setUp() throws Exception {

    RAMDirectory directory = new RAMDirectory();

    IndexWriter writer =
        new IndexWriter(directory,
                        porterAnalyzer,
                        IndexWriter.MaxFieldLength.UNLIMITED);

    Document doc = new Document();
    doc.add(new Field("contents",
                      "The quick brown fox jumps over the lazy dog",
                      Field.Store.YES,
                      Field.Index.ANALYZED));
    writer.addDocument(doc);
    writer.close();
    searcher = new IndexSearcher(directory, true);
    parser = new QueryParser(Version.LUCENE_30,
                             "contents",
                             porterAnalyzer);
  }

  public void testWithSlop() throws Exception {
    parser.setPhraseSlop(1);

    Query query = parser.parse("\"over the lazy\"");

    assertEquals("hole accounted for", 1, TestUtil.hitCount(searcher, query));
  }

  public void testStems() throws Exception {
    Query query = new QueryParser(Version.LUCENE_30,
                         "contents", porterAnalyzer).parse(
                           "laziness");
    assertEquals("lazi", 1, TestUtil.hitCount(searcher, query));

    query = parser.parse("\"fox jumped\"");
    assertEquals("jump jumps jumped jumping", 1, TestUtil.hitCount(searcher, query));
  }

}
