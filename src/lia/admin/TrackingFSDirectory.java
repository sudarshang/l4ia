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
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.store.BufferedIndexInput;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

// From chapter 11

/** Drop-in replacement for FSDirectory that tracks open
 *  files. */

public class TrackingFSDirectory extends SimpleFSDirectory {

  private Set<String> openOutputs = new HashSet<String>();              //A
  private Set<String> openInputs = new HashSet<String>();

  public TrackingFSDirectory(File path) throws IOException {
    super(path);
  }

  synchronized public int getFileDescriptorCount() {        //B
    return openOutputs.size() + openInputs.size();
  }

  synchronized private void report(String message) {
    System.out.println(System.currentTimeMillis() + ": " +
                       message + "; total " + getFileDescriptorCount());
  }

  synchronized public IndexInput openInput(String name)     //C
    throws IOException {
    return openInput(name, BufferedIndexInput.BUFFER_SIZE);
  }

  synchronized public IndexInput openInput(String name, int bufferSize)
    throws IOException {                                    //C
    openInputs.add(name);
    report("Open Input: " + name);
    return new TrackingFSIndexInput(name, bufferSize);
  }

  synchronized public IndexOutput createOutput(String name)
    throws IOException {                                    //D
    openOutputs.add(name);
    report("Open Output: " + name);
    File file = new File(getFile(), name);
    if (file.exists() && !file.delete())
      throw new IOException("Cannot overwrite: " + file);
    return new TrackingFSIndexOutput(name);
  }

  protected class TrackingFSIndexInput extends SimpleFSIndexInput { //E
    String name;
    public TrackingFSIndexInput(String name, int bufferSize) throws IOException {
      super(new File(getFile(), name), bufferSize, getReadChunkSize());
      this.name = name;
    }

    boolean cloned = false;

    public Object clone() {
      TrackingFSIndexInput clone = (TrackingFSIndexInput)super.clone();
      clone.cloned = true;
      return clone;
    }

    public void close() throws IOException {
      super.close();
      if (!cloned) {
        synchronized(TrackingFSDirectory.this) {
          openInputs.remove(name);
        }
      }
      report("Close Input: " + name);
    }
  }

  protected class TrackingFSIndexOutput extends SimpleFSIndexOutput { //E
    String name;
    public TrackingFSIndexOutput(String name) throws IOException {
      super(new File(getFile(), name));
      this.name = name;
    }
    public void close() throws IOException {
      super.close();
      synchronized(TrackingFSDirectory.this) {
        openOutputs.remove(name);
      }
      report("Close Output: " + name);
    }
  }
}

/*
#A Holds all open file names
#B Returns total open file count
#C Opens tracking input
#D Opens tracking output
#E Tracks eventual close
*/
