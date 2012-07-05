package lia.analysis.nutch;

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

import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.QueryFilters;
import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

import java.io.IOException;
import java.io.StringReader;

// From chapter 4
public class NutchExample {
                              
  public static void main(String[] args) throws IOException {
    Configuration conf = new Configuration();
    conf.addResource("nutch-default.xml");
    NutchDocumentAnalyzer analyzer = new NutchDocumentAnalyzer(conf);   //1

    TokenStream ts = analyzer.tokenStream("content",
                                  new StringReader("The quick brown fox..."));
    int position = 0;
    while(true) {                                           // 2
      Token token = ts.next();
      if (token == null) {
        break;
      }
      int increment = token.getPositionIncrement();

      if (increment > 0) {
        position = position + increment;
        System.out.println();
        System.out.print(position + ": ");
      }

      System.out.print("[" +
                       token.termText() + ":" +
                       token.startOffset() + "->" +
                       token.endOffset() + ":" +
                       token.type() + "] ");
    }
    System.out.println();

    Query nutchQuery = Query.parse("\"the quick brown\"", conf);  // 3
    org.apache.lucene.search.Query luceneQuery;
    luceneQuery = new QueryFilters(conf).filter(nutchQuery); // A
    System.out.println("Translated: " + luceneQuery);
  }
}

/*
#1 Custom analyzer
#2 Display token details
#3 Parse to Nutch's Query
#A Create corresponding translated Lucene Query
*/
