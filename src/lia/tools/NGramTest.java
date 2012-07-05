
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

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;

// From chapter 8
public class NGramTest extends TestCase {

  private static class NGramAnalyzer extends Analyzer {
    public TokenStream tokenStream(String fieldName, Reader reader) {
      return new NGramTokenFilter(new KeywordTokenizer(reader), 2, 4);
    }
  }

  private static class FrontEdgeNGramAnalyzer extends Analyzer {
    public TokenStream tokenStream(String fieldName, Reader reader) {
      return new EdgeNGramTokenFilter(new KeywordTokenizer(reader), EdgeNGramTokenFilter.Side.FRONT, 1, 4);
    }
  }

  private static class BackEdgeNGramAnalyzer extends Analyzer {
    public TokenStream tokenStream(String fieldName, Reader reader) {
      return new EdgeNGramTokenFilter(new KeywordTokenizer(reader), EdgeNGramTokenFilter.Side.BACK, 1, 4);
    }
  }

  public void testNGramTokenFilter24() throws IOException {
    AnalyzerUtils.displayTokensWithPositions(new NGramAnalyzer(), "lettuce");
  }

  public void testEdgeNGramTokenFilterFront() throws IOException {
    AnalyzerUtils.displayTokensWithPositions(new FrontEdgeNGramAnalyzer(), "lettuce");
  }

  public void testEdgeNGramTokenFilterBack() throws IOException {
    AnalyzerUtils.displayTokensWithPositions(new BackEdgeNGramAnalyzer(), "lettuce");
  }
}
