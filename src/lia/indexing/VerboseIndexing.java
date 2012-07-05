package lia.indexing;

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

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

import java.io.IOException;

// From chapter 2
public class VerboseIndexing {

  private void index() throws IOException {

    Directory dir = new RAMDirectory();

    IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(),
                                         IndexWriter.MaxFieldLength.UNLIMITED);

    writer.setInfoStream(System.out);

    for (int i = 0; i < 100; i++) {
      Document doc = new Document();
      doc.add(new Field("keyword", "goober", Field.Store.YES, Field.Index.NOT_ANALYZED));
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
  }

  public static void main(String[] args) throws IOException {
    VerboseIndexing vi = new VerboseIndexing();
    vi.index();
  }
}
