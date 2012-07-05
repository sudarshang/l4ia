package lia.tools.remote;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TermQuery;

import java.rmi.Naming;
import java.util.Date;
import java.util.HashMap;

// From chapter 9
public class SearchClient {
  private static HashMap searcherCache = new HashMap();

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: SearchClient <query>");
      System.exit(-1);
    }

    String word = args[0];

    for (int i=0; i < 5; i++) {       //1
      search("LIA_Multi", word);      //1
      search("LIA_Parallel", word);   //1
    }                                 //1
  }

  private static void search(String name, String word)
      throws Exception {
    TermQuery query = new TermQuery(new Term("word", word));

    MultiSearcher searcher =                       //2
      (MultiSearcher) searcherCache.get(name);     //2

    if (searcher == null) {
      searcher =                                  //3
        new MultiSearcher(                       //3
          new Searchable[]{lookupRemote(name)}); //3
      searcherCache.put(name, searcher);
    }    

    long begin = new Date().getTime();         //4
    TopDocs hits = searcher.search(query, 10); //4
    long end = new Date().getTime();           //4

    System.out.print("Searched " + name +
        " for '" + word + "' (" + (end - begin) + " ms): ");

    if (hits.scoreDocs.length == 0) {
      System.out.print("<NONE FOUND>");
    }

    for (ScoreDoc sd : hits.scoreDocs) {
      Document doc = searcher.doc(sd.doc);
      String[] values = doc.getValues("syn");
      for (String syn : values) {
        System.out.print(syn + " ");
      }
    }
    System.out.println();
    System.out.println();
                                             // 5
  }

  private static Searchable lookupRemote(String name)
      throws Exception {
    return (Searchable) Naming.lookup("//localhost/" + name); // 6
  }
}

/*
  #1 Multiple identical searches
  #2 Cache searchers
  #3 Wrap Searchable in MultiSearcher
  #4 Time searching
  #5 Don't close searcher!
  #6 RMI lookup
*/
