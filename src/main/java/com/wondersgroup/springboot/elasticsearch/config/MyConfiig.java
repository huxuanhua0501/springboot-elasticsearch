package com.wondersgroup.springboot.elasticsearch.config;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiig {

	@Bean
	public TransportClient client() throws Exception{
		// 定义地址端口
		TransportAddress address=new TransportAddress(InetAddress.getByName("localhost"), 9300);
		// 定义配置信息
		Settings setting = Settings.builder().put("cluster.name","timou").build();
		// 创建客户端
		TransportClient client =new PreBuiltTransportClient(setting);
		client.addTransportAddress(address);
		return client;
	}
}
