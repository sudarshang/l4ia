package lia.analysis.synonym;

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

import lia.analysis.AnalyzerUtils;
import java.io.IOException;

// From chapter 4
public class SynonymAnalyzerViewer {

  public static void main(String[] args) throws IOException {
    //SynonymEngine engine = new WordNetSynonymEngine(new File(args[0]));
    SynonymEngine engine = new TestSynonymEngine();

    AnalyzerUtils.displayTokensWithPositions(
      new SynonymAnalyzer(engine),
      "The quick brown fox jumps over the lazy dog");

    /*
    AnalyzerUtils.displayTokensWithPositions(
      new SynonymAnalyzer(engine),
      "\"Oh, we get both kinds - country AND western!\" - B.B.");
    */
  }
}
