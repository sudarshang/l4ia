Welcome to Lucene in Action (2nd Edition)!

This source code distribution is a companion to the
Lucene in Action book available from Manning Publications.
To purchase an electronic or printed copy of the book,
visit http://www.manning.com/hatcher3

R E Q U I R E M E N T S
-----------------------
  * JDK 1.6+
  * Ant 1.7+ (to run the automated examples)
  * JUnit 3.8.1+
    - junit.jar should be in ANT_HOME/lib

I N S T A L L A T I O N
-----------------------
You've already unpacked the distribution, since you're reading this 
file. You may now run most of the code using Ant.  We recommend
that you open the code in your favorite editor or IDE to follow how 
the examples work.

R U N N I N G
-------------
We recommend you run the examples using the provided Ant build file.

However, you may run the examples without Ant with some
unsupported and manual configuration.  The JAR files in the
lib directory need to be in your build and execution classpath to
run manually.  Several programs require external information such as 
command-line arguments and JVM system properties - all of which the 
Ant build file takes care of.  Reference the build file and source
for these configuration details.

R U N N I N G     W I T H    A N T
----------------------------------
The code is primarily JUnit test cases, with some Java main()
programs also.  All the JUnit test cases and several of the main() 
programs are easily run using the Ant build.xml file provided.

To run all the JUnit tests, run the "test" target:

     % ant test

The first time the build is executed, the source code is compiled
and a few Lucene indexes are built.  As long as nothing changes,
this is a one-time setup, and successive builds will bypass this 
effort.

Several of the included Java main() programs may be launched 
individually.  From output of "ant -p", these targets are,
including descriptions:

 AnalyzerDemo                 Demonstrates analysis of text
 AnalyzerUtils                Demonstrates analysis of static text
 BooksLikeThis                Demonstrates a term vector use
 ChineseDemo                  Examples of Chinese analysis
 CreateSpellCheckerIndex      Create spell checker index.
 DigesterXMLDocument          Transforms custom XML file into a Document.
 Explainer                    Demonstrates Lucene's Explanation feature
 FastVectorHighlighterSample  Demonstrates Lucene's fast-vector-highlighter
 HighlightIt                  Demonstrates Lucene's highlighter
 Indexer                      Indexes a directory of .txt files
 MetaphoneAnalyzer            Demonstrates analysis of static text using MetaphoneAnalyzer
 NutchExample                 Demonstrates Nutch's analyzer
 OpenFileLimitCheck           Tests how many open files your environment allows
 PrecisionRecall              Measure precision and recall using contrib/benchmark
 SAXXMLDocument               Transforms custom XML file into a Document.
 SearchServer                 Remote and multi-index searching
 Searcher                     Searches an index built by Indexer
 SortingExample               Demonstrates several ways to sort results
 SpatialLucene                Spatial search with Lucene
 SpellCheckerExample          Tests respelling a word
 SynonymAnalyzerViewer        Examples of synonym injection
 TikaIndexer                  Indexes a directory of .txt files
 VerboseIndexing              Shows IndexWriter's infoStream output

To run any of these examples, simply run "ant" followed by the
desired target name.  For example, run the CellPhone application 
using:

	% ant CellPhone

The Ant targets provide information of what is about to execute.
This information is followed by a prompt requiring you to press
return to continue.  Additionally, several examples require user
input, with defaults shown in brackets.

T I P S
-------
  * To run Ant with less decoration, use the -e switch.

  * To run a single test case:
       ant test -Dtest=<name of test without "Test" suffix>
       example: ant test -Dtest=QueryParser

  * To bypass the "Press return to continue..." prompts, add
    the -Dnopause=true command-line switch.

  * Run "ant clean" to force a rebuild of the code and test
    indexes.

  * Follow along with the examples using the source code and the
    book itself.  The code was written for the book and makes much
    more sense in context with elaborate explanations.

K N O W N   I S S U E S
-----------------------
  * Performance tests may fail if your system is heavily loaded at
    the time or has insufficient RAM or processor/disk speed
  
  * SearchServer does not exit, so the Ant launcher forcefully
    times-out its execution.  However, the example of running 
    SearchClient succeeds successfully before this time-out.

C O N T A C T    I N F O R M A T I O N
--------------------------------------
Manning provides an Author Online forum accessible from:
	http://www.manning.com/hatcher3

The authors host a companion website at:
	http://www.lucenebook.com

For general Lucene information and support, please visit
Lucene's website for pointers to FAQ's, articles, the user e-mail 
list, and the informative and ever evolving wiki.  The main Lucene
website is: http://lucene.apache.org