package com.ftn.udd.controller;

import com.ftn.udd.repository.ElasticArticleRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/elastic")
public class ElasticController {

    @Autowired
    private ElasticArticleRepository elasticArticleRepository;

    @Autowired
    private ElasticsearchOperations es;

    @RequestMapping(value = "/getArticleCombined", method = RequestMethod.POST, produces = "application/json")
    public String getCombined(@RequestParam("articleName") String articleName, @RequestParam("authors") String[] authors, @RequestParam("members") String[] members, @RequestParam("articleKeywords") String[] articleKeywords, @RequestParam("articleContent") String articleContent, @RequestParam("areaName") String areaName, @RequestParam("operationType") String operationType, @RequestParam("fieldCount") String fieldCount){

        Client client = es.getClient();
        String shouldMatch;
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        if (operationType.equals("AND")){
            shouldMatch = fieldCount.toString();
        }else {
            shouldMatch = "1";
        }

        sourceBuilder.query(QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("title", articleName))
                                                        .should(QueryBuilders.matchQuery("author.firstName", Arrays.asList(authors)))
                                                        .should(QueryBuilders.matchQuery("author.lastName", Arrays.asList(authors)))
                                                        .should(QueryBuilders.matchQuery("commiteeMembers.firstName", Arrays.asList(members)))
                                                        .should(QueryBuilders.matchQuery("commiteeMembers.lastName", Arrays.asList(members)))
                                                        .should(QueryBuilders.matchQuery("keywords", Arrays.asList(articleKeywords)))
                                                        .should(QueryBuilders.matchPhraseQuery("content", articleContent))
                                                        .should(QueryBuilders.matchPhraseQuery("areaCode.name", areaName)).minimumShouldMatch(shouldMatch)
                        );

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("article").source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest).actionGet();
        System.out.println(searchResponse.toString());

        return searchResponse.toString();
    }

}
