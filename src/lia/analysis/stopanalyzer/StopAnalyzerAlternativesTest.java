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

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;

// From chapter 4
public class StopAnalyzerAlternativesTest extends TestCase {
  public void testStopAnalyzer2() throws Exception {
    AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(),
                                   "The quick brown...",
                                   new String[] {"quick", "brown"});
  }

  public void testStopAnalyzerFlawed() throws Exception {
    AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(),
                                   "The quick brown...",
                                   new String[] {"the", "quick", "brown"});
  }

  /**
   * Illustrates that "the" is not removed, although it is lowercased
   */
  public static void main(String[] args) throws Exception {
    AnalyzerUtils.displayTokens(
      new StopAnalyzerFlawed(), "The quick brown...");
  }
}
