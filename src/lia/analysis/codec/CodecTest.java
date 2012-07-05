package lia.analysis.codec;

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
import org.apache.commons.codec.language.Metaphone;

// From chapter 4
public class CodecTest extends TestCase {
  public void testMetaphone() throws Exception {
    Metaphone metaphoner = new Metaphone();
    assertEquals(metaphoner.encode("cute"),
                 metaphoner.encode("cat"));
  }
}
