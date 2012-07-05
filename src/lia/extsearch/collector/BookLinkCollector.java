package lia.extsearch.collector;

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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.FieldCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// From chapter 6
public class BookLinkCollector extends Collector {
  private Map<String,String> documents = new HashMap<String,String>();
  private Scorer scorer;
  private String[] urls;
  private String[] titles;

  public boolean acceptsDocsOutOfOrder() {
    return true;                            // #A
  }

  public void setScorer(Scorer scorer) {
    this.scorer = scorer;
  }

  public void setNextReader(IndexReader reader, int docBase) throws IOException {
    urls = FieldCache.DEFAULT.getStrings(reader, "url");           // #B
    titles = FieldCache.DEFAULT.getStrings(reader, "title2");      // #B
  }

  public void collect(int docID) {
    try {
      String url = urls[docID];            // #C
      String title = titles[docID];        // #C
      documents.put(url, title);           // #C
      System.out.println(title + ":" + scorer.score());
    } catch (IOException e) {
      // ignore
    }
  }

  public Map<String,String> getLinks() {
    return Collections.unmodifiableMap(documents);
  }
}

/*
  #A Accept docIDs out of order
  #B Load FieldCache values
  #C Store details for the match
*/
