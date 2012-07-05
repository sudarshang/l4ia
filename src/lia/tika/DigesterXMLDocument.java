package lia.tika;

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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

// From chapter 7
public class DigesterXMLDocument {

  private Digester dig;
  private static Document doc;

  public DigesterXMLDocument() {

    dig = new Digester();
    dig.setValidating(false);

    dig.addObjectCreate("address-book", DigesterXMLDocument.class);   // #1
    dig.addObjectCreate("address-book/contact", Contact.class);       // #2

    dig.addSetProperties("address-book/contact", "type", "type");     // #3

    dig.addCallMethod("address-book/contact/name",                    // #4
                      "setName", 0);                                  // #4
    dig.addCallMethod("address-book/contact/address",
      "setAddress", 0);
    dig.addCallMethod("address-book/contact/city",
      "setCity", 0);
    dig.addCallMethod("address-book/contact/province",
      "setProvince", 0);
    dig.addCallMethod("address-book/contact/postalcode",
      "setPostalcode", 0);
    dig.addCallMethod("address-book/contact/country",
      "setCountry", 0);
    dig.addCallMethod("address-book/contact/telephone",
      "setTelephone", 0);

    dig.addSetNext("address-book/contact", "populateDocument");       // #5
  }

  public synchronized Document getDocument(InputStream is)
    throws DocumentHandlerException {

    try {
      dig.parse(is);                                                  // #6
    }
    catch (IOException e) {
      throw new DocumentHandlerException(
        "Cannot parse XML document", e);
    }
    catch (SAXException e) {
      throw new DocumentHandlerException(
        "Cannot parse XML document", e);
    }

    return doc;
  }

  public void populateDocument(Contact contact) {                     // #7

    doc = new Document();

    doc.add(new Field("type", contact.getType(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("name", contact.getName(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("address", contact.getAddress(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("city", contact.getCity(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("province", contact.getProvince(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("postalcode", contact.getPostalcode(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("country", contact.getCountry(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
    doc.add(new Field("telephone", contact.getTelephone(), Field.Store.YES,
                      Field.Index.NOT_ANALYZED));
  }

  public static class Contact {
    private String type;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalcode;
    private String country;
    private String telephone;

    public void setType(String newType) {
      type = newType;
    }
    public String getType() {
      return type;
    }

    public void setName(String newName) {
      name = newName;
    }
    public String getName() {
      return name;
    }

    public void setAddress(String newAddress) {
      address = newAddress;
    }
    public String getAddress() {
      return address;
    }

    public void setCity(String newCity) {
      city = newCity;
    }
    public String getCity() {
      return city;
    }

    public void setProvince(String newProvince) {
      province = newProvince;
    }
    public String getProvince() {
      return province;
    }

    public void setPostalcode(String newPostalcode) {
      postalcode = newPostalcode;
    }
    public String getPostalcode() {
      return postalcode;
    }

    public void setCountry(String newCountry) {
      country = newCountry;
    }
    public String getCountry() {
      return country;
    }

    public void setTelephone(String newTelephone) {
      telephone = newTelephone;
    }
    public String getTelephone() {
      return telephone;
    }
  }

  public static void main(String[] args) throws Exception {
    DigesterXMLDocument handler = new DigesterXMLDocument();
    Document doc =
      handler.getDocument(new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}

/*
#1 Create DigesterXMLDocument
#2 Create Contact
#3 Set type attribute
#4 Set name property
#5 Call populateDocument
#6 Parse XML InputStream
#7 Create Lucene document
*/
