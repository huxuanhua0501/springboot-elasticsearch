package com.wondersgroup.springboot.elasticsearch.controller;

import com.wondersgroup.springboot.elasticsearch.service.IQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * query查询业务
 */
@RestController
public class QueryController {
    @Autowired
    private IQuery query;

    @GetMapping("/matchQuery")
    public String matchQuery(String text) {
        query.match(text);
        return null;
    }
}
