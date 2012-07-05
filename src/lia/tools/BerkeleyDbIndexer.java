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

import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.Transaction;
import com.sleepycat.db.DatabaseException;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.db.DbDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;


// From chapter 9
public class BerkeleyDbIndexer {
  public static void main(String[] args) throws IOException, DatabaseException {
    if (args.length != 1) {
      System.err.println("Usage: BerkeleyDbIndexer <index dir>");
      System.exit(-1);
    }

    File indexFile = new File(args[0]);

    if (indexFile.exists()) {
      File[] files = indexFile.listFiles();

      for (int i = 0; i < files.length; i++)
        if (files[i].getName().startsWith("__"))
          files[i].delete();
      indexFile.delete();
    }

    indexFile.mkdir();

    EnvironmentConfig envConfig = new EnvironmentConfig();
    DatabaseConfig dbConfig = new DatabaseConfig();

    envConfig.setTransactional(true);
    envConfig.setInitializeCache(true);
    envConfig.setInitializeLocking(true);
    envConfig.setInitializeLogging(true);
    envConfig.setAllowCreate(true);
    envConfig.setThreaded(true);
    dbConfig.setAllowCreate(true);
    dbConfig.setType(DatabaseType.BTREE);

    Environment env = new Environment(indexFile, envConfig);

    Transaction txn = env.beginTransaction(null, null);
    Database index = env.openDatabase(txn, "__index__", null, dbConfig);
    Database blocks = env.openDatabase(txn, "__blocks__", null, dbConfig);
    txn.commit();

    txn = env.beginTransaction(null, null);
    DbDirectory directory = new DbDirectory(txn, index, blocks);

    IndexWriter writer = new IndexWriter(directory,
                                         new StandardAnalyzer(Version.LUCENE_30),
                                         true,
                                         IndexWriter.MaxFieldLength.UNLIMITED);

    Document doc = new Document();
    doc.add(new Field("contents", "The quick brown fox...", Field.Store.YES, Field.Index.ANALYZED));
    writer.addDocument(doc);

    writer.optimize();
    writer.close();

    directory.close();
    txn.commit();

    index.close();
    blocks.close();
    env.close();

    System.out.println("Indexing Complete");
  }
}
