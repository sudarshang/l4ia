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

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.RemoteSearchable;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

// From chapter 9
public class SearchServer {
  private static final String ALPHABET =
      "abcdefghijklmnopqrstuvwxyz";

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: SearchServer <basedir>");
      System.exit(-1);
    }

    String basedir = args[0];    //1
    Directory[] dirs = new Directory[ALPHABET.length()];
    Searchable[] searchables = new Searchable[ALPHABET.length()];
    for (int i = 0; i < ALPHABET.length(); i++) {
      dirs[i] = FSDirectory.open(new File(basedir, ""+ALPHABET.charAt(i)));
      searchables[i] = new IndexSearcher(      //2
          dirs[i]);                            //2
    }

    LocateRegistry.createRegistry(1099);         //3

    Searcher multiSearcher = new MultiSearcher(searchables); //4
    RemoteSearchable multiImpl =                             //4
      new RemoteSearchable(multiSearcher);                   //4
    Naming.rebind("//localhost/LIA_Multi", multiImpl);       //4

    Searcher parallelSearcher =                             //5
        new ParallelMultiSearcher(searchables);             //5
    RemoteSearchable parallelImpl =                         //5
        new RemoteSearchable(parallelSearcher);             //5
    Naming.rebind("//localhost/LIA_Parallel", parallelImpl);//5

    System.out.println("Server started");

    for (int i = 0; i < ALPHABET.length(); i++) {
      dirs[i].close();
    }
  }
}

/*
  #1 Indexes under basedir
  #2 Open IndexSearcher for each index
  #3 Create RMI registry
  #4 MultiSearcher over all indexes
  #5 ParallelMultiSearcher over all indexes
*/
