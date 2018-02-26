package com.wondersgroup.springboot.elasticsearch.service.impl;

import com.wondersgroup.springboot.elasticsearch.service.IAPIService;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class APIService implements IAPIService {
    @Autowired
    TransportClient transportClient;

}
