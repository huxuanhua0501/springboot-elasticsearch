package com.wondersgroup.springboot.elasticsearch.util;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.webcollector.example.DemoDepthCrawler;
import cn.edu.hfut.dmic.webcollector.example.TutorialCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.ObjectInputFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


public class CSDNCrawler extends BreadthCrawler {

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     * information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     * links which match regex rules from page
     */
    public CSDNCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /*start page*/
//        this.addSeed("https://car.autohome.com.cn/price/list-0-101-0-0-0-0-0-0-0-0-0-0-0-0-0-1.html");
        this.addSeed("http://newhouse.fang.com/house/s/");
        /*fetch url like the value by setting up RegEx filter rule */
        this.addRegex(".*");
        /*do not fetch jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*do not fetch url contains #*/
        this.addRegex("-.*#.*");
    }
    @Override
    public void visit(Page page, CrawlDatums next) {

        String hoursetitle = page.select("div[class=nlcd_name]").first().text().replace("�o","");
        String price = page.select("div[class=nhouse_price]").first().text().replace("/�O","");
        String type = page.select("span[class=inSale]").first().text().replace("/�O","");
        String address1 = page.select("div[class=address]").first().text().replace("/�O","");
        String tel = page.select("div[class=tel]").first().text().replace("/�O","");



//           String price = page.select("span[class=font-arial]").first().text().replace("/�O","");
//           String name = page.select("div[class=main-title]").first().text().replace("/�O","");
//           String type = page.select("span[class=info-gray]").first().text().replace("/�O","");
//           String pingfen = page.select("div[class=score-cont]").first().text().replace("/�O","");





        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder()
                    .startObject()
//                    .field("name", name)
//                    .field("postDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
//                    .field("price", price)
//                    .field("type", type)
//                    .field("pingfen", pingfen)
                    .field("tel", tel)
                    .field("hoursetitle", hoursetitle)
                    .field("price", price)
                    .field("type", type)
                    .field("address", address1)
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
//          client.prepareIndex("car", "qichezhijia")
          client.prepareIndex("house", "xinfang")
                .setSource(builder).get();

    }
    static TransportClient client;

    {
        try {
            client = client();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        CSDNCrawler crawler = new CSDNCrawler("path", true);
        client();
        crawler.setThreads(50);
//        crawler.setTopN(100);
        crawler.conf.setTopN(5000);
//        crawler.setResumable(true);
        /*start crawl with depth 3*/
        crawler.start(4);
    }

    public static  TransportClient client() throws Exception {
        // 定义地址端口
        TransportAddress address = new TransportAddress(InetAddress.getByName("localhost"), 9300);
        // 定义配置信息
        Settings setting = Settings.builder()
                .put("cluster.name", "elasticsearch")//设置ES实例的名称
                .put("client.transport.sniff", true)//自动嗅探整个集群的状态,把集群的中的其他es节点的ip添加到本地的客户端列表中
                .build();
        // 创建客户端
        TransportClient client = new PreBuiltTransportClient(setting);//初始化client
        client.addTransportAddress(address);//至少添加一个节点
        return client;
    }
}
