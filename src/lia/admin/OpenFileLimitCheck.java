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
import java.io.RandomAccessFile;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

// From chapter 11

/** Run this to see what your JRE's open file limit is. */

public class OpenFileLimitCheck {
  public static void main(String[] args) throws IOException {
    List<RandomAccessFile> files = new ArrayList<RandomAccessFile>();
    try {
      while(true) {
        files.add(new RandomAccessFile("tmp" + files.size(), "rw"));
      }
    } catch (IOException ioe) {
      System.out.println("IOException after  " + files.size() + " open files:");
      ioe.printStackTrace(System.out);
      int i = 0;
      for (RandomAccessFile raf : files) {
      	raf.close();
      	new File("tmp" + i++).delete();
      }
    }
  }
}
