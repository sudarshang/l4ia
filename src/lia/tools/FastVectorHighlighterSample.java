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

import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

// From chapter 8
public class FastVectorHighlighterSample {

  static final String[] DOCS = {                                      // #A
    "the quick brown fox jumps over the lazy dog",                    // #A
    "the quick gold fox jumped over the lazy black dog",              // #A
    "the quick fox jumps over the black dog",                         // #A
    "the red fox jumped over the lazy dark gray dog"                  // #A
  };
  static final String QUERY = "quick OR fox OR \"lazy dog\"~1";       // #B
  static final String F = "f";
  static Directory dir = new RAMDirectory();
  static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: FastVectorHighlighterSample <filename>");
      System.exit(-1);
    }
    makeIndex();
    searchIndex(args[0]);
  }

  static void makeIndex() throws IOException {
    IndexWriter writer = new IndexWriter(dir, analyzer,
                                     true, MaxFieldLength.UNLIMITED);
    for(String d : DOCS){
      Document doc = new Document();
      doc.add(new Field(F, d, Store.YES, Index.ANALYZED,
                        TermVector.WITH_POSITIONS_OFFSETS));
      writer.addDocument(doc);
    }
    writer.close();
  }
  
  static void searchIndex(String filename) throws Exception {
    QueryParser parser = new QueryParser(Version.LUCENE_30,
                                         F, analyzer);
    Query query = parser.parse(QUERY);                                
    FastVectorHighlighter highlighter = getHighlighter();             // #C
    FieldQuery fieldQuery = highlighter.getFieldQuery(query);         // #D
    IndexSearcher searcher = new IndexSearcher(dir);           
    TopDocs docs = searcher.search(query, 10);                       

    FileWriter writer = new FileWriter(filename);
    writer.write("<html>");
    writer.write("<body>");
    writer.write("<p>QUERY : " + QUERY + "</p>");
    for(ScoreDoc scoreDoc : docs.scoreDocs) {
      String snippet = highlighter.getBestFragment(                   // #E
          fieldQuery, searcher.getIndexReader(),                      // #E
          scoreDoc.doc, F, 100 );                                     // #E
      if (snippet != null) {
        writer.write(scoreDoc.doc + " : " + snippet + "<br/>");
      }
    }
    writer.write("</body></html>");
    writer.close();
    searcher.close();
  }
  
  static FastVectorHighlighter getHighlighter() {
    FragListBuilder fragListBuilder = new SimpleFragListBuilder();    // #F
    FragmentsBuilder fragmentBuilder =                                // #F
      new ScoreOrderFragmentsBuilder(                                 // #F
        BaseFragmentsBuilder.COLORED_PRE_TAGS,                        // #F
        BaseFragmentsBuilder.COLORED_POST_TAGS);                      // #F
    return new FastVectorHighlighter(true, true,                      // #F
        fragListBuilder, fragmentBuilder);                            // #F
  }
}

/*
#A Index these documents
#B Run this query
#C Get FastVectorHighlighter
#D Create FieldQuery
#E Highlight top fragment
#F Create FastVectorHighlighter
*/
