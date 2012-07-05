package lia.common;

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

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Filter;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.File;

public class TestUtil {
  public static boolean hitsIncludeTitle(IndexSearcher searcher, TopDocs hits, String title)
    throws IOException {
    for (ScoreDoc match : hits.scoreDocs) {
      Document doc = searcher.doc(match.doc);
      if (title.equals(doc.get("title"))) {
        return true;
      }
    }
    System.out.println("title '" + title + "' not found");
    return false;
  }

  public static int hitCount(IndexSearcher searcher, Query query) throws IOException {
    return searcher.search(query, 1).totalHits;
  }

  public static int hitCount(IndexSearcher searcher, Query query, Filter filter) throws IOException {
    return searcher.search(query, filter, 1).totalHits;
  }

  public static void dumpHits(IndexSearcher searcher, TopDocs hits)
    throws IOException {
    if (hits.totalHits == 0) {
      System.out.println("No hits");
    }

    for (ScoreDoc match : hits.scoreDocs) {
      Document doc = searcher.doc(match.doc);
      System.out.println(match.score + ":" + doc.get("title"));
    }
  }
  
  public static Directory getBookIndexDirectory() throws IOException {
    // The build.xml ant script sets this property for us:
    return FSDirectory.open(new File(System.getProperty("index.dir")));
  }

  public static void rmDir(File dir) throws IOException {
    if (dir.exists()) {
      File[] files = dir.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (!files[i].delete()) {
          throw new IOException("could not delete " + files[i]);
        }
      }
      dir.delete();
    }
  }
}
