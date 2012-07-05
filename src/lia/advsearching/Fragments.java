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

import org.apache.lucene.search.*;

// From chapter 5
public class Fragments {
  public void frags1() throws Exception {
    String jan1 = null;
    String jan31 = null;
    String modified = null;
    Filter filter;
    // START
    filter = new TermRangeFilter(modified, null, jan31, false, true);
    filter = new TermRangeFilter(modified, jan1, null, true, false);
    filter = TermRangeFilter.Less(modified, jan31);
    filter = TermRangeFilter.More(modified, jan1);
    // END
  }
}