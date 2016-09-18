package com.janeullah.healthinspectionrecords.org.async;

import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.org.constants.WebSelectorConstants;
import org.apache.commons.lang3.CharEncoding;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageProcess implements Callable<List<Elements>> {

    private Path url;

    public WebPageProcess(Path url) {
        this.url = url;
    }

    private List<Elements> processFile(){
        try (InputStream in = Files.newInputStream(url)) {
            Document doc = Jsoup.parse(in, CharEncoding.UTF_8, WebPageConstants.BASE_URL);
            return Arrays.asList(
                    doc.select(WebSelectorConstants.RESTAURANT_NAME_SELECTOR),
                    doc.select(WebSelectorConstants.RESTAURANT_ADDRESS_SELECTOR),
                    doc.select(WebSelectorConstants.INSPECTION_TYPE_SELECTOR),
                    doc.select(WebSelectorConstants.DATE_SELECTOR),
                    doc.select(WebSelectorConstants.SCORE_SELECTOR),
                    doc.select(WebSelectorConstants.CRITICAL),
                    doc.select(WebSelectorConstants.NOT_CRITICAL),
                    doc.select(WebSelectorConstants.ALL_VIOLATIONS));
        } catch (Selector.SelectorParseException | IOException e) {
            System.err.println(e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Elements> call() throws Exception {
        return processFile();
    }

}