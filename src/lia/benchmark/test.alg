# The analyzer to use
analyzer=org.apache.lucene.analysis.standard.StandardAnalyzer

# Content source
content.source=org.apache.lucene.benchmark.byTask.feeds.ReutersContentSource

# Directory
directory=FSDirectory

# Turn on stored fields
doc.stored = true

# Turn on term vectors
doc.term.vectors = true

# Don't use compound-file format
compound = false

# Make only one pass through the documents
content.source.forever = false

# Repeat 3 times
{"Rounds"

  # Clear the index
  ResetSystemErase

  # Name the contained tasks "BuildIndex"
  {"BuildIndex"

   # Create a new IndexWriter
   -CreateIndex

   # Add all docs
   { "AddDocs" AddDoc > : *

   # Close the index
    -CloseIndex
  }

  # Start a new round
  NewRound
} : 3

# Report on the BuildIndex task
RepSumByPrefRound BuildIndex 
