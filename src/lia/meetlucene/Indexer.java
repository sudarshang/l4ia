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

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.FileReader;

// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Indexer {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: java " + Indexer.class.getName()
        + " <index dir> <data dir>");
    }
    String indexDir = args[0];         //1
    String dataDir = args[1];          //2

    long start = System.currentTimeMillis();
    Indexer indexer = new Indexer(indexDir);
    int numIndexed;
    try {
      numIndexed = indexer.index(dataDir, new TextFilesFilter());
    } finally {
      indexer.close();
    }
    long end = System.currentTimeMillis();

    System.out.println("Indexing " + numIndexed + " files took "
      + (end - start) + " milliseconds");
  }

  private IndexWriter writer;

  public Indexer(String indexDir) throws IOException {
    Directory dir = FSDirectory.open(new File(indexDir));
    writer = new IndexWriter(dir,            //3
                 new StandardAnalyzer(       //3
                     Version.LUCENE_30),//3
                 true,                       //3
                             IndexWriter.MaxFieldLength.UNLIMITED); //3
  }

  public void close() throws IOException {
    writer.close();                             //4
  }

  public int index(String dataDir, FileFilter filter)
    throws Exception {

    File[] files = new File(dataDir).listFiles();

    for (File f: files) {
      if (!f.isDirectory() &&
          !f.isHidden() &&
          f.exists() &&
          f.canRead() &&
          (filter == null || filter.accept(f))) {
        indexFile(f);
      }
    }

    return writer.numDocs();                     //5
  }

  private static class TextFilesFilter implements FileFilter {
    public boolean accept(File path) {
      return path.getName().toLowerCase()        //6
             .endsWith(".txt");                  //6
    }
  }

  protected Document getDocument(File f) throws Exception {
    Document doc = new Document();
    doc.add(new Field("contents", new FileReader(f)));      //7
    doc.add(new Field("filename", f.getName(),              //8
                Field.Store.YES, Field.Index.NOT_ANALYZED));//8
    doc.add(new Field("fullpath", f.getCanonicalPath(),     //9
                Field.Store.YES, Field.Index.NOT_ANALYZED));//9
    return doc;
  }

  private void indexFile(File f) throws Exception {
    System.out.println("Indexing " + f.getCanonicalPath());
    Document doc = getDocument(f);
    writer.addDocument(doc);                              //10
  }
}

/*
#1 Create index in this directory
#2 Index *.txt files from this directory
#3 Create Lucene IndexWriter
#4 Close IndexWriter
#5 Return number of documents indexed
#6 Index .txt files only, using FileFilter
#7 Index file content
#8 Index file name
#9 Index file full path
#10 Add document to Lucene index
*/
