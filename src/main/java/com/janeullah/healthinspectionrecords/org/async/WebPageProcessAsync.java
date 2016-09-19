package com.janeullah.healthinspectionrecords.org.async;

import com.google.common.collect.Lists;
import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.org.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.org.model.Restaurant;
import com.janeullah.healthinspectionrecords.org.util.JsoupUtil;
import org.apache.commons.lang3.CharEncoding;
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

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageProcessAsync implements Callable<List<Restaurant>> {

    private Path url;
    Elements hiddenDivs;

    public WebPageProcessAsync(Path url) {
        this.url = url;
    }

    private List<Restaurant> ingestJsoupData(){
        List<Restaurant> restaurantsInFile = Lists.newArrayList();
        Elements jsoupList = processFile();
        jsoupList.forEach(entry -> {
            Restaurant r = JsoupUtil.assemblePOJO(entry,hiddenDivs);
            restaurantsInFile.add(r);
        });
        return restaurantsInFile;
    }

    private Elements processFile(){
        try (InputStream in = Files.newInputStream(url)) {
            Document doc = Jsoup.parse(in, CharEncoding.UTF_8, WebPageConstants.BASE_URL);
            //var hiddenDiv = $("div").filter(":hidden")
            //https://api.jquery.com/hidden-selector/
            hiddenDivs = doc.select(WebSelectorConstants.HIDDEN_DIVS);
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