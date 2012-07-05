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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.commons.codec.language.Metaphone;

import java.io.IOException;

// From chapter 4
public class MetaphoneReplacementFilter extends TokenFilter {
  public static final String METAPHONE = "metaphone";

  private Metaphone metaphoner = new Metaphone();
  private TermAttribute termAttr;
  private TypeAttribute typeAttr;

  public MetaphoneReplacementFilter(TokenStream input) {
    super(input);
    termAttr = addAttribute(TermAttribute.class);
    typeAttr = addAttribute(TypeAttribute.class);
  }

  public boolean incrementToken() throws IOException {
    if (!input.incrementToken())                    //#A  
      return false;                                 //#A  

    String encoded;
    encoded = metaphoner.encode(termAttr.term());   //#B
    termAttr.setTermBuffer(encoded);                //#C
    typeAttr.setType(METAPHONE);                    //#D
    return true;
  }
}

/*
#A Advance to next token
#B Convert to Metaphone encoding
#C Overwrite with encoded text
#D Set token type
*/
