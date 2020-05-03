package com.log4j2.custom.appender.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class ElasticSearchConnection {

    /* This is a thread-safe*/
    private static RestHighLevelClient restHighLevelClient;
    private static final String HOST = "localhost";
    private static final int PORT = 9200;
    private static final String SCHEMA = "HTTP";

    private ElasticSearchConnection(){}

    public static synchronized void connect(){
        if(restHighLevelClient == null){
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(HOST, PORT, SCHEMA))
            );
        }
    }

    public static RestHighLevelClient client(){
        return restHighLevelClient;
    }

    public static synchronized void close(){
        if(restHighLevelClient != null){
            try {
                restHighLevelClient.close();
                restHighLevelClient = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
