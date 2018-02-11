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
	public TransportClient client() throws Exception{
		// 定义地址端口
		TransportAddress address=new TransportAddress(InetAddress.getByName("localhost"), 9300);
		// 定义配置信息
		Settings setting = Settings.builder().put("cluster.name",env.getProperty("cluster.name")).build();
		// 创建客户端
		TransportClient client =new PreBuiltTransportClient(setting);
		client.addTransportAddress(address);
		return client;
	}
}
