package com.wondersgroup.springboot.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.wondersgroup.springboot.elasticsearch.service.IAPIService;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Service
public class APIService implements IAPIService {
    @Autowired
    TransportClient client;

    @Override
    public String insertUser() {
        Map<String, Object> json = new HashMap<>();
        json.put("user", "kimchy");
        json.put("postDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        json.put("message", "elasticsearch插入学习");
        IndexResponse indexResponse = client.prepareIndex("huxuanhua", "learndata")
                .setSource(json).get();
        return indexResponse.getResult().toString();
    }

    @Override
    public String insertUser2() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "nike")
                .field("postDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .field("message", "这是JAVA书籍插入学习")
                .endObject();
        IndexResponse indexResponse = client.prepareIndex("huxuanhua", "learndata")
                .setSource(builder).get();
        return indexResponse.getResult().toString();
    }

    @Override
    public long delete1() {
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("message", "java"))//查询条件
                .source("huxuanhua")//索引
                .get();
        long deleted = response.getDeleted();//删除文档的数量
        return deleted;
    }

    @Override
    public long delete2() {
        final long[] deleted = {0};
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("message", "学习"))//查询条件
                .source("huxuanhua")//索引
                .execute(new ActionListener<BulkByScrollResponse>() {
                    @Override
                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                        deleted[0] = bulkByScrollResponse.getDeleted();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
        return 0;
    }

    /**
     * 这里不能相同的index,可以作为文本的批量
     *
     * @throws IOException
     */
    @Override
    public void pulkInsert() throws IOException {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        try {
            bulkRequestBuilder.add(client.prepareIndex("huxuanhua1", "esdata1", "2").setSource(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("user", "huxuanhua")
                    .field("postData", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                    .field("message", "红海行动,燃气爱国情")
                    .endObject()));
            bulkRequestBuilder.add(client.prepareIndex("huxuanhua2", "esdata2", "3").setSource(XContentFactory.jsonBuilder()
                    .startObject()
                    .field("user", "jack")
                    .field("postData", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                    .field("message", "美国大使馆说:奶奶的,真行啊啊.不中行不行")
                    .endObject()));
            BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                System.err.println(bulkResponse.buildFailureMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 批量文本插入
     */
    @Override
    public void pulkText() throws IOException {

    }

    @Override
    public String searchRresponse() {
        SearchResponse response = client.prepareSearch().get();
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        List<String> list = new ArrayList<>();


        for (SearchHit searchHit : searchHits) {
            System.err.println(searchHit.getSourceAsString());
            list.add(searchHit.getSourceAsString());
        }
        return JSON.toJSONString(list);
    }

    /**
     * 游标查询
     */
    @Override
    public void testScrolls() {
        QueryBuilder qb = termQuery("content", "中国");//scroll的查询条件

        SearchResponse scrollResp = client.prepareSearch("index")  //指定查询的索引
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH) //检索方式设置为scan
                .setScroll(new TimeValue(60000)) //当前scroll的打开时间，该参数必须在每一个scroll请求中指定
                .setQuery(qb)
                .setSize(100) //每个primary分片返回的文档数
                .execute().actionGet(); //100 hits per shard will be
        SearchHits hits = scrollResp.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits)
            System.err.println(hit.getSourceAsMap().get("content"));
    }

    /**
     * 可以执行多个搜索
     */
    @Override
    public void multiSearch() {

        SearchRequestBuilder builder1 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("红海行动")).setSize(1);//
        SearchRequestBuilder builder2 = client.prepareSearch().setQuery(QueryBuilders.matchQuery("content", "中国")).setSize(1);
        MultiSearchResponse response = client.prepareMultiSearch()
                .add(builder1)
                .add(builder2)
                .get();
        for (MultiSearchResponse.Item item : response.getResponses()) {
            SearchResponse searchResponse = item.getResponse();
            SearchHits hits = searchResponse.getHits();
            SearchHit[] hit = hits.getHits();
            for (SearchHit searchHit : hit) {
                System.err.println(searchHit.getSourceAsString());
            }
        }
    }

}
