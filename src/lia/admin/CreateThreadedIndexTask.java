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

import java.io.IOException;
import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.benchmark.byTask.tasks.CreateIndexTask;
import org.apache.lucene.benchmark.byTask.PerfRunData;
import org.apache.lucene.benchmark.byTask.utils.Config;

// From chapter 11

/** A task that you can use from a contrib/benchmark
 *  algorithm to create a ThreadedIndexWriter. */

public class CreateThreadedIndexTask extends CreateIndexTask {

  public CreateThreadedIndexTask(PerfRunData runData) {
    super(runData);
  }

  public int doLogic() throws IOException {
    PerfRunData runData = getRunData();
    Config config = runData.getConfig();
    IndexWriter writer = new ThreadedIndexWriter(
                                runData.getDirectory(),
                                runData.getAnalyzer(),
                                true,
                                config.get("writer.num.threads", 4),
                                config.get("writer.max.thread.queue.size", 20),        
                                IndexWriter.MaxFieldLength.UNLIMITED);
    CreateIndexTask.setIndexWriterConfig(writer, config);
    runData.setIndexWriter(writer);
    return 1;
  }
}
