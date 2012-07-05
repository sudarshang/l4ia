package lia.tools.XmlQueryParser;

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

import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.w3c.dom.Element;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.xmlparser.FilterBuilder;
import org.apache.lucene.xmlparser.DOMUtils;
import org.apache.lucene.xmlparser.ParserException;

// From chapter 9
public class AgoFilterBuilder implements FilterBuilder {

  static HashMap<String,Integer> timeUnits=new HashMap<String,Integer>();

  public Filter getFilter(Element element) throws ParserException {
    String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(element,     // A
                                                                "fieldName");  // A
    String timeUnit = DOMUtils.getAttribute(element, "timeUnit", "days");    // A
    Integer calUnit = timeUnits.get(timeUnit);                                // A
    if (calUnit == null) {                                                 // A
      throw new ParserException("Illegal time unit:"                     // A
                                +timeUnit+" - must be days, months or years");  // A
    }                                                      // A
    int agoStart = DOMUtils.getAttribute(element, "from",0);  // A
    int agoEnd = DOMUtils.getAttribute(element, "to", 0);    // A
    if (agoStart < agoEnd) {
      int oldAgoStart = agoStart;
      agoStart = agoEnd;
      agoEnd = oldAgoStart;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");      // B

    Calendar start = Calendar.getInstance();                        // B
    start.add(calUnit, agoStart*-1);                          // B
		
    Calendar end = Calendar.getInstance();                      // B
    end.add(calUnit, agoEnd*-1);                               // B
		
    return NumericRangeFilter.newIntRange(             // C
                    fieldName,                         // C
                    Integer.valueOf(sdf.format(start.getTime())),       // C
                    Integer.valueOf(sdf.format(end.getTime())),         // C
                    true, true);                       // C
  }

  static {
    timeUnits.put("days", Calendar.DAY_OF_YEAR);
    timeUnits.put("months",Calendar.MONTH);
    timeUnits.put("years", Calendar.YEAR);
  }
}

/*
#A Extract field, time unit, from and to
#B Parse date/times
#C Create NumericRangeFilter
*/
