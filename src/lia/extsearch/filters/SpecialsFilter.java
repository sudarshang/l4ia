package lia.extsearch.filters;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.OpenBitSet;

import java.io.IOException;

// From chapter 6
public class SpecialsFilter extends Filter {
  private SpecialsAccessor accessor;

  public SpecialsFilter(SpecialsAccessor accessor) {
    this.accessor = accessor;
  }

  public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
    OpenBitSet bits = new OpenBitSet(reader.maxDoc());

    String[] isbns = accessor.isbns();                  // #1

    int[] docs = new int[1];
    int[] freqs = new int[1];

    for (String isbn : isbns) {
      if (isbn != null) {
        TermDocs termDocs =
          reader.termDocs(new Term("isbn", isbn));      // #2
        int count = termDocs.read(docs, freqs);
        if (count == 1) {                               // #3
          bits.set(docs[0]);                            // #3
        }                                               // #3
      }
    }

    return bits;
  }
  /*
#1 Fetch ISBNs
#2 Jump to term
#3 Set corresponding bit
  */

  public String toString() {
    return "SpecialsFilter";
  }
}
