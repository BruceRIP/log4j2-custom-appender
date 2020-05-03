package com.log4j2.custom.appender.client;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElasticSearchClient {

    public static void append(final String index, final String logName, final String message) {
        Map<String, String> logMessage = new LinkedHashMap<>();
        logMessage.put(logName, message);
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.source(logMessage);
        save(indexRequest);
    }

    private static void save(final IndexRequest message){
        try {
            ElasticSearchConnection.client().index(message, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
