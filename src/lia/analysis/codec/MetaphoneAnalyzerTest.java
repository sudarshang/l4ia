package lia.analysis.codec;

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

import lia.analysis.AnalyzerUtils;

import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.io.StringReader;

// From chapter 4
public class MetaphoneAnalyzerTest extends TestCase {
  public void testKoolKat() throws Exception {
    RAMDirectory directory = new RAMDirectory();
    Analyzer analyzer = new MetaphoneReplacementAnalyzer();

    IndexWriter writer = new IndexWriter(directory, analyzer, true,
                                         IndexWriter.MaxFieldLength.UNLIMITED);

    Document doc = new Document();
    doc.add(new Field("contents", //#A
                      "cool cat",
                      Field.Store.YES,
                      Field.Index.ANALYZED));
    writer.addDocument(doc);
    writer.close();

    IndexSearcher searcher = new IndexSearcher(directory);

    Query query = new QueryParser(Version.LUCENE_30,  //#B
                                  "contents", analyzer)    //#B
                              .parse("kool kat");          //#B

    TopDocs hits = searcher.search(query, 1);
    assertEquals(1, hits.totalHits);   //#C
    int docID = hits.scoreDocs[0].doc;
    doc = searcher.doc(docID);
    assertEquals("cool cat", doc.get("contents"));   //#D

    searcher.close();
  }

  /*
    #A Index document
    #B Parse query text
    #C Verify match
    #D Retrieve original value
  */

  public static void main(String[] args) throws IOException {
    MetaphoneReplacementAnalyzer analyzer =
                                 new MetaphoneReplacementAnalyzer();
    AnalyzerUtils.displayTokens(analyzer,
                   "The quick brown fox jumped over the lazy dog");

    System.out.println("");
    AnalyzerUtils.displayTokens(analyzer,
                   "Tha quik brown phox jumpd ovvar tha lazi dag");
  }
}
