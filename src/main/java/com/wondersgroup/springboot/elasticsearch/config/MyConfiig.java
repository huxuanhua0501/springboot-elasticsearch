package com.wondersgroup.springboot.elasticsearch.config;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MyConfiig {
    @Autowired
    Environment env;

    @Bean
    public TransportClient client() throws Exception {
        // 定义地址端口
        TransportAddress address = new TransportAddress(InetAddress.getByName("localhost"), 9300);
        // 定义配置信息
        Settings setting = Settings.builder()
                .put("cluster.name", env.getProperty("cluster.name"))//设置ES实例的名称
                .put("client.transport.sniff", true)//自动嗅探整个集群的状态,把集群的中的其他es节点的ip添加到本地的客户端列表中
                .build();
        // 创建客户端
        TransportClient client = new PreBuiltTransportClient(setting);//初始化client
        client.addTransportAddress(address);//至少添加一个节点
        return client;
    }
}
