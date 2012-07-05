package lia.tools;

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

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.messages.MessageImpl;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryParser.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryParser.standard.nodes.WildcardQueryNode;
import org.apache.lucene.queryParser.core.nodes.FieldQueryNode;
import org.apache.lucene.queryParser.core.nodes.QueryNode;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryParser.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.queryParser.standard.StandardQueryParser;
import org.apache.lucene.queryParser.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryParser.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryParser.core.nodes.SlopQueryNode;
import org.apache.lucene.search.MultiPhraseQuery;

// From chapter 9
public class CustomFlexibleQueryParser extends StandardQueryParser {

  public CustomFlexibleQueryParser(Analyzer analyzer) {
    super(analyzer);

    QueryNodeProcessorPipeline processors = (QueryNodeProcessorPipeline) getQueryNodeProcessor();
    processors.addProcessor(new NoFuzzyOrWildcardQueryProcessor());     //A

    QueryTreeBuilder builders = (QueryTreeBuilder) getQueryBuilder();    //B
    builders.setBuilder(TokenizedPhraseQueryNode.class, new SpanNearPhraseQueryBuilder());//B
    builders.setBuilder(SlopQueryNode.class, new SlopQueryNodeBuilder());//B
  }

  private final class NoFuzzyOrWildcardQueryProcessor extends QueryNodeProcessorImpl {
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
      if (node instanceof FuzzyQueryNode || node instanceof WildcardQueryNode) {   //C
        throw new QueryNodeException(new MessageImpl("no"));
      }
      return node;
    }
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
      return node;
    }
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) {
      return children;
    }
  }

  private class SpanNearPhraseQueryBuilder implements StandardQueryBuilder {
    public Query build(QueryNode queryNode) throws QueryNodeException {
      TokenizedPhraseQueryNode phraseNode = (TokenizedPhraseQueryNode) queryNode;
      PhraseQuery phraseQuery = new PhraseQuery();

      List<QueryNode> children = phraseNode.getChildren();   //D

      SpanTermQuery[] clauses;
      if (children != null) {
        int numTerms = children.size();
        clauses = new SpanTermQuery[numTerms];
        for (int i=0;i<numTerms;i++) {
          FieldQueryNode termNode = (FieldQueryNode) children.get(i);
          TermQuery termQuery = (TermQuery) termNode
            .getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
          clauses[i] = new SpanTermQuery(termQuery.getTerm());
        }
      } else {
        clauses = new SpanTermQuery[0];
      }
        
      return new SpanNearQuery(clauses, phraseQuery.getSlop(), true); //E
    }
  }

  public class SlopQueryNodeBuilder implements StandardQueryBuilder {  //F

    public Query build(QueryNode queryNode) throws QueryNodeException {
      SlopQueryNode phraseSlopNode = (SlopQueryNode) queryNode;
      
      Query query = (Query) phraseSlopNode.getChild().getTag(
                           QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
      
      if (query instanceof PhraseQuery) {
        ((PhraseQuery) query).setSlop(phraseSlopNode.getValue());
      } else if (query instanceof MultiPhraseQuery) {
        ((MultiPhraseQuery) query).setSlop(phraseSlopNode.getValue());
      }

      return query;
    }
  }
}

/*
  #A Install our custom node processor
  #B Install our two custom query builders
  #C Prevent Fuzzy and Wildcard queries
  #D Pull all terms for phrase
  #E Create SpanNearQuery
  #F Override slop query node
*/
