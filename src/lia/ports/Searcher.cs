
class Searcher {
  String indexDir = args[0];     // A
  String q = args[1];            // B

  public static void search(String indexDir, String q) {
    Directory dir = FSDirectory.Open(new System.IO.FileInfo(indexDir)); // C
    IndexSearcher searcher = new IndexSearcher(dir, true); // D
    QueryParser parser = new QueryParser("contents",
                                         new StandardAnalyzer(Version.LUCENE_CURRENT)); // E
    Query query = parser.Parse(q); // E
    Lucene.Net.Search.TopDocs hits = searcher.Search(query, 10); // F
    System.Console.WriteLine("Found " +
                             hits.totalHits +
                             " document(s) that matched query '" + q + "':");
    for (int i = 0; i < hits.scoreDocs.Length; i++) {
      ScoreDoc scoreDoc = hits.ScoreDocs[i];         // G
      Document doc = searcher.Doc(scoreDoc.doc);     // G
      System.Console.WriteLine(doc.Get("filename")); // G
    }
    searcher.Close();                // H
}
/*
  #A Index directory created by Indexer
  #B Query string
  #C Open index
  #D Open searcher
  #E Parse query
  #F Search index
  #G Retrieve & display result
  #H Close searcher
*/
