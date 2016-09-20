package com.janeullah.healthinspectionrecords.org.async;

import com.google.common.collect.Lists;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.org.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.util.JsoupUtil;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageProcessAsync implements Callable<List<Restaurant>> {

    private Path url;
    private Elements hiddenDivs;
    private final CountDownLatch doneSignal;


    public WebPageProcessAsync(Path url, CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
        this.url = url;
    }

    private List<Restaurant> ingestJsoupData(){
        List<Restaurant> restaurantsInFile = Lists.newArrayList();
        Elements jsoupList = processFile();
        if (jsoupList != null) {
            jsoupList.forEach(entry -> {
                Restaurant r = JsoupUtil.assemblePOJO(entry, hiddenDivs);
                restaurantsInFile.add(r);
            });
        }
        doneSignal.countDown();
        return restaurantsInFile;
    }

    private void setHiddenDivs(Document doc){
        try{
            hiddenDivs = doc.select("div:not([class])")
                    .stream()
                    .filter(entry -> StringUtils.isNumeric(entry.id()))
                    .collect(Collectors.toCollection(Elements::new));
        }catch(Selector.SelectorParseException e){
            System.err.println(e);
        }
    }

    private Elements processFile(){
        try (InputStream in = Files.newInputStream(url)) {
            Document doc = Jsoup.parse(in, CharEncoding.UTF_8, WebPageConstants.BASE_URL);
            setHiddenDivs(doc);
            return doc.select(WebSelectorConstants.ALL_ROW);
        } catch (Selector.SelectorParseException | IOException e) {
            System.err.println(e);
        }
        return null;
    }

    @Override
    public List<Restaurant> call() throws Exception {
        return ingestJsoupData();
    }

}