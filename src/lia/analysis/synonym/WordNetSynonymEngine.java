package lia.analysis.synonym;

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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import lia.common.AllDocCollector;

// From chapter 9
public class WordNetSynonymEngine implements SynonymEngine {
  IndexSearcher searcher;
  Directory fsDir;

  public WordNetSynonymEngine(File index) throws IOException {
    fsDir = FSDirectory.open(index);
    searcher = new IndexSearcher(fsDir);
  }

  public void close() throws IOException {
    searcher.close();
    fsDir.close();
  }

  public String[] getSynonyms(String word) throws IOException {

    List<String> synList = new ArrayList<String>();

    AllDocCollector collector = new AllDocCollector();  // #A

    searcher.search(new TermQuery(new Term("word", word)), collector);

    for(ScoreDoc hit : collector.getHits()) {    // #B
      Document doc = searcher.doc(hit.doc);

      String[] values = doc.getValues("syn");

      for (String syn : values) {  // #C
        synList.add(syn);
      }
    }

    return synList.toArray(new String[0]);
  }
}

/*
  #A Collect every matching document
  #B Iterate over matching documents
  #C Record synonyms
*/
