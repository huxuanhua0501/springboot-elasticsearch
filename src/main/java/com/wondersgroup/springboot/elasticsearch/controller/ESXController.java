package com.wondersgroup.springboot.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 为了idea
 */
@RestController
@RequestMapping("/book")
public class ESXController {
    @Autowired
    private TransportClient client;

    @GetMapping("/getbookById")
    public String getBookById(String id) {
        if (id != null && !id.isEmpty()) {
            GetResponse result = client.prepareGet("book", "novel", id).get();
            System.err.println(JSON.toJSONString(result));
            System.err.println("--------------------");
            return result.getSourceAsString();
        } else {
            return JSON.toJSONString(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

    }

    @PostMapping("/insertBook")
    public String insertBook(String name, String title, String word) {
        XContentBuilder json;
        try {
            json = XContentFactory.jsonBuilder().startObject();
            json.field("name", name);
            json.field("title", title);
            json.field("word_count", word);
            json.field("publishing_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            json.endObject();
            IndexResponse result = client.prepareIndex("book", "novel").setSource(json).get();
            return result.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @PutMapping("/updateBook")
    public ResponseEntity updateBook(String name, String title, String id) {
        UpdateRequest updateRequest = new UpdateRequest("book", "novel", id);
        XContentBuilder json = null;
        try {
            json = XContentFactory.jsonBuilder().startObject();
            json.field("name", name);
            json.field("title", title);
            json.field("id", id);
            json.endObject();
            updateRequest.doc(json);
            UpdateResponse result = client.update(updateRequest).get();
            return new ResponseEntity(result.getResult().toString(), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteBook() {
//        DeleteResponse deleteResponse = this.client.prepareDelete("index", "fulltext", "_search -H").get();
        DeleteRequestBuilder deleteResponse = this.client.prepareDelete();
//        deleteResponse = this.client.prepareDelete("index", "fulltext", "2 -d").get();
//        deleteResponse = this.client.prepareDelete("index", "fulltext", "3 -d").get();
//        deleteResponse = this.client.prepareDelete("index", "fulltext", "4 -d").get();
//        deleteResponse = this.client.prepareDelete("index", "fulltext", "1 -d").get();


        return null;
//        return new ResponseEntity(deleteResponse.getResult().toString(), HttpStatus.OK);
    }

    @GetMapping("/index")
    public void findBook() {
        SearchResponse response = client.prepareSearch().execute().actionGet();// 获取全部
        System.err.println(response);
    }

    @GetMapping("serch")
    public void searchRequest() {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("book").setFrom(0).setSize(100);

        //执行查询
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        SearchHits searchHits = response.getHits();
        System.out.println("总数：" + searchHits.getTotalHits());
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            System.err.println(json);
        }
    }


    @GetMapping("/getJSON")
    public String getJSON() {
        Map<String, String> map = new HashMap<>();
        map.put("county", "日本");
        map.put("pranvice", "日本");
        return JSON.toJSONString(map);
    }

}
