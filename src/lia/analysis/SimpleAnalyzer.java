package lia.analysis;

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
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.Tokenizer;

// From chapter 4
public final class SimpleAnalyzer extends Analyzer {
  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new LowerCaseTokenizer(reader);
  }
  public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
    Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
    if (tokenizer == null) {
      tokenizer = new LowerCaseTokenizer(reader);
      setPreviousTokenStream(tokenizer);
    } else
      tokenizer.reset(reader);
    return tokenizer;
  }

  public static void main(String[] args) throws IOException {
    AnalyzerUtils.displayTokensWithFullDetails(new SimpleAnalyzer(),
        "The quick brown fox....");
  }
}
