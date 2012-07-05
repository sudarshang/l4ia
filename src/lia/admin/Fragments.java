package lia.admin;

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

import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.*;
import java.util.Collection;

// From chapter 11

public class Fragments {
  public void test() throws Exception {
    Directory dir = null;
    Analyzer analyzer = null;
    // START
    IndexDeletionPolicy policy = new KeepOnlyLastCommitDeletionPolicy();
    SnapshotDeletionPolicy snapshotter = new SnapshotDeletionPolicy(policy);
    IndexWriter writer = new IndexWriter(dir, analyzer, snapshotter,
                                         IndexWriter.MaxFieldLength.UNLIMITED);
    // END

    try {
      IndexCommit commit = (IndexCommit) snapshotter.snapshot();
      Collection<String> fileNames = commit.getFileNames();
      /*<iterate over & copy files from fileNames>*/
    } finally {
      snapshotter.release();
    }
  }
}