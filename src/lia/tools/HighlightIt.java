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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;

import java.io.FileWriter;
import java.io.StringReader;

// From chapter 8
public class HighlightIt {
  private static final String text =
    "In this section we'll show you how to make the simplest " +
    "programmatic query, searching for a single term, and then " +
    "we'll see how to use QueryParser to accept textual queries. " +
    "In the sections that follow, we’ll take this simple example " +
    "further by detailing all the query types built into Lucene. " +
    "We begin with the simplest search of all: searching for all " +
    "documents that contain a single term.";

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("Usage: HighlightIt <filename-out>");
      System.exit(-1);
    }

    String filename = args[0];

    String searchText = "term";                               // #1
    QueryParser parser = new QueryParser(Version.LUCENE_30,      // #1
                                         "f",                         // #1
                                         new StandardAnalyzer(Version.LUCENE_30));// #1
    Query query = parser.parse(searchText);                           // #1

    SimpleHTMLFormatter formatter =                                   // #2
      new SimpleHTMLFormatter("<span class=\"highlight\">",           // #2
                              "</span>");                             // #2

    TokenStream tokens = new StandardAnalyzer(Version.LUCENE_30)  // #3
        .tokenStream("f", new StringReader(text));                    // #3

    QueryScorer scorer = new QueryScorer(query, "f");                 // #4

    Highlighter highlighter = new Highlighter(formatter, scorer);     // #5
    highlighter.setTextFragmenter(                                    // #6
                  new SimpleSpanFragmenter(scorer));                  // #6

    String result =                                                   // #7
        highlighter.getBestFragments(tokens, text, 3, "...");         // #7

    FileWriter writer = new FileWriter(filename);                     // #8
    writer.write("<html>");                                           // #8
    writer.write("<style>\n" +                                        // #8
        ".highlight {\n" +                                            // #8
        " background: yellow;\n" +                                    // #8
        "}\n" +                                                       // #8
        "</style>");                                                  // #8
    writer.write("<body>");                                           // #8
    writer.write(result);                                             // #8
    writer.write("</body></html>");                                   // #8
    writer.close();                                                   // #8
  }
}

/*
#1 Create the query
#2 Customize surrounding tags
#3 Tokenize text
#4 Create QueryScorer
#5 Create highlighter
#6 Use SimpleSpanFragmenter to fragment
#7 Highlight best 3 fragments
#8 Write highlighted HTML
*/
