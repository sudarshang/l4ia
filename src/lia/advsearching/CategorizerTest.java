package lia.advsearching;

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
import lia.common.TestUtil;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

// From chapter 5
public class CategorizerTest extends TestCase {
  Map categoryMap;

  protected void setUp() throws Exception {
    categoryMap = new TreeMap();

    buildCategoryVectors();
//    dumpCategoryVectors();
  }

  public void testCategorization() throws Exception {
    assertEquals("/technology/computers/programming/methodology",
        getCategory("extreme agile methodology"));
    assertEquals("/education/pedagogy",
        getCategory("montessori education philosophy"));
  }

  private void dumpCategoryVectors() {
    Iterator categoryIterator = categoryMap.keySet().iterator();
    while (categoryIterator.hasNext()) {
      String category = (String) categoryIterator.next();
      System.out.println("Category " + category);

      Map vectorMap = (Map) categoryMap.get(category);
      Iterator vectorIterator = vectorMap.keySet().iterator();
      while (vectorIterator.hasNext()) {
        String term = (String) vectorIterator.next();
        System.out.println("    " + term + " = " + vectorMap.get(term));
      }
    }
  }

  private void buildCategoryVectors() throws IOException {
    IndexReader reader = IndexReader.open(TestUtil.getBookIndexDirectory());

    int maxDoc = reader.maxDoc();

    for (int i = 0; i < maxDoc; i++) {
      if (!reader.isDeleted(i)) {
        Document doc = reader.document(i);
        String category = doc.get("category");

        Map vectorMap = (Map) categoryMap.get(category);
        if (vectorMap == null) {
          vectorMap = new TreeMap();
          categoryMap.put(category, vectorMap);
        }

        TermFreqVector termFreqVector =
            reader.getTermFreqVector(i, "subject");

        addTermFreqToMap(vectorMap, termFreqVector);
      }
    }
  }

  private void addTermFreqToMap(Map vectorMap,
                                TermFreqVector termFreqVector) {
    String[] terms = termFreqVector.getTerms();
    int[] freqs = termFreqVector.getTermFrequencies();

    for (int i = 0; i < terms.length; i++) {
      String term = terms[i];

      if (vectorMap.containsKey(term)) {
        Integer value = (Integer) vectorMap.get(term);
        vectorMap.put(term,
            new Integer(value.intValue() + freqs[i]));
      } else {
        vectorMap.put(term, new Integer(freqs[i]));
      }
    }
  }


  private String getCategory(String subject) {
    String[] words = subject.split(" ");

    Iterator categoryIterator = categoryMap.keySet().iterator();
    double bestAngle = Double.MAX_VALUE;
    String bestCategory = null;

    while (categoryIterator.hasNext()) {
      String category = (String) categoryIterator.next();
//      System.out.println(category);

      double angle = computeAngle(words, category);
//      System.out.println(" -> angle = " + angle + " (" + Math.toDegrees(angle) + ")");
      if (angle < bestAngle) {
        bestAngle = angle;
        bestCategory = category;
      }
    }

    return bestCategory;
  }

  private double computeAngle(String[] words, String category) {
    Map vectorMap = (Map) categoryMap.get(category);

    int dotProduct = 0;
    int sumOfSquares = 0;
    for (String word : words) {
      int categoryWordFreq = 0;

      if (vectorMap.containsKey(word)) {
        categoryWordFreq =
            ((Integer) vectorMap.get(word)).intValue();
      }

      dotProduct += categoryWordFreq;  //#1
      sumOfSquares += categoryWordFreq * categoryWordFreq;
    }


    double denominator;
    if (sumOfSquares == words.length) {
      denominator = sumOfSquares; // #2
    } else {
      denominator = Math.sqrt(sumOfSquares) *
                    Math.sqrt(words.length);
    }

    double ratio = dotProduct / denominator;

    return Math.acos(ratio);
  }
  /*
    #1 Assume each word has frequency 1
    #2 Shortcut to prevent precision issue
  */
}


