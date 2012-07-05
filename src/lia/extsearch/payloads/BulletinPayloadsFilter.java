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

import java.io.IOException;

import org.apache.lucene.index.Payload;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.payloads.PayloadHelper;

// From chapter 6
public class BulletinPayloadsFilter extends TokenFilter {

  private TermAttribute termAtt;
  private PayloadAttribute payloadAttr;
  private boolean isBulletin;
  private Payload boostPayload;

  BulletinPayloadsFilter(TokenStream in, float warningBoost) {
    super(in);
    payloadAttr = addAttribute(PayloadAttribute.class);
    termAtt = addAttribute(TermAttribute.class);
    boostPayload = new Payload(PayloadHelper.encodeFloat(warningBoost));
  }

  void setIsBulletin(boolean v) {
    isBulletin = v;
  }

  public final boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      if (isBulletin && termAtt.term().equals("warning")) {          // #A
        payloadAttr.setPayload(boostPayload);                        // #A
      } else {
        payloadAttr.setPayload(null);                                // #B
      }
      return true;
    } else {
      return false;
    }
  }
}

/*
  #A Add payload boost
  #B Clear payload
*/
