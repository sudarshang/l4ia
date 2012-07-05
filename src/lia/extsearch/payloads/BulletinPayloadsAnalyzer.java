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

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

// From chapter 6
public class BulletinPayloadsAnalyzer extends Analyzer {
  private boolean isBulletin;
  private float boost;

  BulletinPayloadsAnalyzer(float boost) {
    this.boost = boost;
  }

  void setIsBulletin(boolean v) {
    isBulletin = v;
  }

  public TokenStream tokenStream(String fieldName, Reader reader) {
    BulletinPayloadsFilter stream = new BulletinPayloadsFilter(new StandardAnalyzer(Version.LUCENE_30).tokenStream(fieldName, reader), boost);
    stream.setIsBulletin(isBulletin);
    return stream;
  }
}
