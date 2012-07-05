package lia.meetlucene;

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

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Searcher {

  public static void main(String[] args) throws IllegalArgumentException,
        IOException, ParseException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: java " + Searcher.class.getName()
        + " <index dir> <query>");
    }

    String indexDir = args[0];               //1 
    String q = args[1];                      //2   

    search(indexDir, q);
  }

  public static void search(String indexDir, String q)
    throws IOException, ParseException {

    Directory dir = FSDirectory.open(new File(indexDir)); //3
    IndexSearcher is = new IndexSearcher(dir);   //3   

    QueryParser parser = new QueryParser(Version.LUCENE_30, // 4
                                         "contents",  //4
                     new StandardAnalyzer(          //4
                       Version.LUCENE_30));  //4
    Query query = parser.parse(q);              //4   
    long start = System.currentTimeMillis();
    TopDocs hits = is.search(query, 10); //5
    long end = System.currentTimeMillis();

    System.err.println("Found " + hits.totalHits +   //6  
      " document(s) (in " + (end - start) +        // 6
      " milliseconds) that matched query '" +     // 6
      q + "':");                                   // 6

    for(ScoreDoc scoreDoc : hits.scoreDocs) {
      Document doc = is.doc(scoreDoc.doc);               //7      
      System.out.println(doc.get("fullpath"));  //8  
    }

    is.close();                                //9
  }
}

/*
#1 Parse provided index directory
#2 Parse provided query string
#3 Open index
#4 Parse query
#5 Search index
#6 Write search stats
#7 Retrieve matching document
#8 Display filename
#9 Close IndexSearcher
*/
