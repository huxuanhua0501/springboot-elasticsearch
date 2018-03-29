package com.wondersgroup.springboot.elasticsearch.service.impl;

import com.wondersgroup.springboot.elasticsearch.service.IQuery;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Query implements IQuery {
    @Autowired
    private TransportClient client;

    /**
     * match query
     */
    public String match(String text) {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", text);
        SearchResponse searchResponse = client.prepareSearch("car")
                .setTypes("qichezhijia")
                .setQuery(queryBuilder)
                .setFrom(0)
                .setSize(100)
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit :
                hits) {
            System.err.println(hit.getSourceAsString());
        }
        return null;
    }
}
