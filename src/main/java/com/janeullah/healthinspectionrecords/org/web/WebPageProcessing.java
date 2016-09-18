package com.janeullah.healthinspectionrecords.org.web;

import org.apache.commons.lang3.CharEncoding;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: jane
 * Date:  9/17/2016
 */
public class WebPageProcessing {
    private final static Logger logger = Logger.getLogger(WebPageProcessing.class);

    public WebPageProcessing(){

    }

    private void processWebRequest(InputStream stream, String urlString){
        try {
            Document document = Jsoup.parse(stream, CharEncoding.UTF_8,urlString);
            String question = document.select("#question .post-text").text();
            System.out.println("Question: " + question);

            Elements answerers = document.select("#answers .user-details a");
            for (Element answerer : answerers) {
                System.out.println("Answerer: " + answerer.text());
            }
        }catch(IOException e){
            logger.error("event=\"Exception caught\"");
        }
    }
}
