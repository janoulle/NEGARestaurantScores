package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.domain.entities.EstablishmentInfo;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.util.JsoupUtil;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageProcessAsync implements Callable<List<Restaurant>> {
    private static final Logger logger = LoggerFactory.getLogger(WebPageProcessAsync.class);

    private Path url;
    private Elements hiddenDivs;
    private String county;
    private final CountDownLatch doneSignal;


    public WebPageProcessAsync(String county, Path url, CountDownLatch doneSignal) {
        this.doneSignal = doneSignal;
        this.county = county;
        this.url = url;
    }

    private List<Restaurant> ingestJsoupData(){
        List<Restaurant> restaurantsInFile = new ArrayList<>();
        Optional<Elements> jsoupList = processFile();
        jsoupList.ifPresent(iterable -> iterable.forEach(entry -> {
            Optional<Restaurant> restaurant = JsoupUtil.assemblePOJO(entry, hiddenDivs);
            restaurant.ifPresent(createdRestaurant -> {
                if (createdRestaurant.getEstablishmentInfo() == null) {
                    createdRestaurant.setEstablishmentInfo(new EstablishmentInfo());
                }
                createdRestaurant.getEstablishmentInfo().setCounty(county);
                restaurantsInFile.add(createdRestaurant);
            });
        }));
        return restaurantsInFile;
    }

    private void setHiddenDivs(Document doc){
        try{
            hiddenDivs = doc.select("div:not([class])")
                    .stream()
                    .filter(entry -> StringUtils.isNumeric(entry.id()))
                    .collect(Collectors.toCollection(Elements::new));
        }catch(Selector.SelectorParseException e){
            logger.error("Error setting hidden divs which contain violation explanations",e);
        }
    }

    private Optional<Elements> processFile(){
        try (InputStream in = Files.newInputStream(url)) {
            Document doc = Jsoup.parse(in, CharEncoding.UTF_8, WebPageConstants.BASE_URL);
            setHiddenDivs(doc);
            return Optional.of(doc.select(WebSelectorConstants.ALL_ROW));
        } catch (Selector.SelectorParseException | IOException e) {
            logger.error("Exception processing the file from url {}",url,e);
        }
        return Optional.empty();
    }

    @Override
    public List<Restaurant> call() throws Exception {
        try {
            return ingestJsoupData();
        }catch(Exception e){
            logger.error("Exception processing the downloaded web page for  {}", county,e);
        }finally {
            doneSignal.countDown();
        }
        return new ArrayList<>();
    }

}