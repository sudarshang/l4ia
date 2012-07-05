package lia.analysis.stopanalyzer;

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.util.Set;

// From chapter 4
public class StopAnalyzer2 extends Analyzer {

  private Set stopWords;

  public StopAnalyzer2() {
    stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
  }

  public StopAnalyzer2(String[] stopWords) {
    this.stopWords = StopFilter.makeStopSet(stopWords);
  }

  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new StopFilter(true, new LowerCaseFilter(new LetterTokenizer(reader)),
        stopWords);
  }
}
