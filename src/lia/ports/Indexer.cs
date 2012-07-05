
System.String indexDir = args[0];	// A
System.String dataDir = args[1];	// B

private IndexWriter writer;

class Indexer {
  public void Indexer(System.String indexDir) {
    Directory dir = FSDirectory.Open(new System.IO.FileInfo(indexDir));
    IndexWriter writer = new IndexWriter(                                   // C
                           FSDirectory.Open(INDEX_DIR),                     // C
                           new StandardAnalyzer(Version.LUCENE_CURRENT),    // C
                           true, IndexWriter.MaxFieldLength.LIMITED);       // C
  }

  public void Close() {
    writer.Close();                // D
  }

  public int Index(System.String dataDir) {
    System.String[] files = System.IO.Directory.GetFileSystemEntries(file.FullName);
    for (int i = 0; i < files.Length; i++) {
      IndexFile(new System.IO.FileInfo(files[i]));
    }
    return writer.NumDocs();       // E
  }

  protected Document GetDocument(System.IO.FileInfo file) {
    Document doc = new Document();
    doc.Add(new Field("contents",                                  // F
                      new System.IO.StreamReader(file.FullName,    // F
                              System.Text.Encoding.Default)));     // F
	doc.Add(new Field("filename",                              // G
                          file.Name,                               // G
                          Field.Store.YES,                         // G
                          Field.Index.NOT_ANALYZED));              // G
	doc.Add(new Field("fullpath",                              // H
                          file.FullName,                           // H
                          Field.Store.YES,                         // H
                          Field.Index.NOT_ANALYZED));              // H
	return doc;
}

  private void IndexFile(System.IO.FileInfo file) {
    Document doc = GetDocument(file);
    writer.AddDocument(doc);                                       // I
  }
}

/*
  #A Create Lucene index in this directory
  #B Index *.txt from this directory
  #C Create IndexWriter
  #D Close IndexWriter  
  #E Return number of documents indexed
  #F Index file content
  #G Index file name
  #H Index full file path
  #I Add document to index
*/
