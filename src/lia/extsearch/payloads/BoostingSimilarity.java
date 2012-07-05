package lia.extsearch.payloads;

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

import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.analysis.payloads.PayloadHelper;

// From chapter 6
public class BoostingSimilarity extends DefaultSimilarity {
  public float scorePayload(int docID, String fieldName, int start, int end, byte[] payload, int offset, int length) {
    if (payload != null) {
      return PayloadHelper.decodeFloat(payload, offset);
    } else {
      return 1.0F;
    }
  }
}
