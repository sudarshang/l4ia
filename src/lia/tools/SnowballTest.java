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

import junit.framework.TestCase;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;
import lia.analysis.AnalyzerUtils;

// From chapter 8
public class SnowballTest extends TestCase {
  public void testEnglish() throws Exception {
    Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_30, "English");
    AnalyzerUtils.assertAnalyzesTo(analyzer,
                                   "stemming algorithms",
                                   new String[] {"stem", "algorithm"});
  }

  public void testSpanish() throws Exception {
    Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_30, "Spanish");
    AnalyzerUtils.assertAnalyzesTo(analyzer,
                                   "algoritmos",
                                   new String[] {"algoritm"});
  }
}
