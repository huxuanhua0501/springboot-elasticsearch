package com.wondersgroup.springboot.elasticsearch.controller;

import com.wondersgroup.springboot.elasticsearch.service.IAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/user")
public class APIController {
    @Autowired
    private IAPIService apiService;

    @GetMapping("/insert")
    public String insertUser() {
//        return apiService.insertUser();
        try {
            return apiService.insertUser2();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping("/delete")
    public long delete() {
//        return apiService.delete1();
        return apiService.delete2();
    }

    @GetMapping("/bulk")
    public void pulkInsert() throws IOException {
        apiService.pulkInsert();
    }

    @GetMapping("/search")
    public String searchRresponse() {
        return apiService.searchRresponse();
    }

    @GetMapping("/scroll")
    public void testScrolls() {
        apiService.testScrolls();
    }

    @GetMapping("/multi")
    public void multiSearch() {
        apiService.multiSearch();
    }
    @GetMapping("/aggre")
    public void aggregations() {
        apiService.terminate();
    }
    @GetMapping("/getFang")
    public  void getFang() {
        apiService.getFang();
    }
}
