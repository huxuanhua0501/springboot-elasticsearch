package com.wondersgroup.springboot.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ESController {

	@Autowired
	private TransportClient client;
	@GetMapping("/")
	public String index(){
		return "index";
	}
	
	@GetMapping("/get/book/novel")
	public ResponseEntity get(@RequestParam(name="id",defaultValue="")String id){
		if (id.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		GetResponse result = this.client.prepareGet("book", "novel", id).get();
		if (!result.isExists()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(result.getSource(),HttpStatus.OK);
	}
	
	@PostMapping("/add/book/novel")
	public ResponseEntity add(
			@RequestParam String name,
			@RequestParam String title,
			@RequestParam("word_count") String wordCount,
			@RequestParam("publishing_date") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date publishingDate
			){
		XContentBuilder json;
		try {
			json = XContentFactory.jsonBuilder().startObject();
			json.field("name", name);
			json.field("title",title);
			json.field("word_count", wordCount);
			json.field("publishing_date", publishingDate.getTime());
			json.endObject();
			IndexResponse result = this.client.prepareIndex("book", "novel").setSource(json).get();
			return new ResponseEntity(result.getId(),HttpStatus.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		
	}
	
	@PutMapping("/update/book/novel")
	public ResponseEntity update(
			@RequestParam(name="id")String id,
			@RequestParam(name="name",required=false)String name,
			@RequestParam(name="title",required=false)String title
			) {
		UpdateRequest request=new UpdateRequest("book","novel",id);
		try {
			XContentBuilder json = XContentFactory.jsonBuilder().startObject();
			if (name!=null) {
				json.field("name", name);
			}
			if (title!=null) {
				json.field("title", title);
			}
			json.endObject();
			request.doc(json);
			UpdateResponse result = this.client.update(request).get();
			return new ResponseEntity(result.getResult().toString(),HttpStatus.OK);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	/**
	 * 删除图书
	 * @param id
	 * @return
	 */
	@DeleteMapping("delete/book/novel")
	public ResponseEntity delete(@RequestParam("id")String id){
		DeleteResponse deleteResponse = this.client.prepareDelete("book", "novel", id).get();
		return new ResponseEntity(deleteResponse.getResult().toString(),HttpStatus.OK);
	}
	
	@PostMapping("query/book/novel")
	public ResponseEntity query(
			@RequestParam(name="name",required=false)String name,
			@RequestParam(name="title",required=false)String title,
			@RequestParam(name="gt_word_count",defaultValue="0")int gtWordCuont,
			@RequestParam(name="lt_word_count",required=false)Integer ltWordCount
			){
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (name!=null) {
			boolQuery.must(QueryBuilders.matchQuery("name", name));
		}
		if (title!=null) {
			boolQuery.must(QueryBuilders.matchQuery("title", title));
		}
		RangeQueryBuilder rangQuery = QueryBuilders.rangeQuery("word_count").from(gtWordCuont);
		if (ltWordCount!=null) {
			rangQuery.to(ltWordCount);
		}
		boolQuery.filter(rangQuery);
		SearchRequestBuilder builder = this.client.prepareSearch("book")
		.setTypes("novel")
		.setQuery(boolQuery)
		.setFrom(0)
		.setSize(10);
		System.out.println(builder);
		SearchResponse searchResponse = builder.get();
		List<Map<String, Object>> result=new ArrayList<>();
		for (SearchHit hit : searchResponse.getHits()) {
			result.add(hit.getSourceAsMap());
		}
		return new  ResponseEntity(result,HttpStatus.OK);
	}
	
	
}

