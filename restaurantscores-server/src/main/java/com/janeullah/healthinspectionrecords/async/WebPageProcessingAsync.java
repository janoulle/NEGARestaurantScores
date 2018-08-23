package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.domain.FileToBeProcessed;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.exceptions.WebPageProcessAsyncException;
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebPageProcessingAsync {
    private RestaurantService restaurantService;

    @Autowired
    public WebPageProcessingAsync(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public Elements retrieveHiddenDivs(Document doc) {
        try {
            return
                    doc.select("div:not([class])")
                            .stream()
                            .filter(entry -> StringUtils.isNumeric(entry.id()))
                            .collect(Collectors.toCollection(Elements::new));
        } catch (Selector.SelectorParseException e) {
            log.error("Error setting hidden divs which contain violation explanations", e);
        }
        return null;
    }

    @Async
    public CompletableFuture<List<Restaurant>> processWebPage(FileToBeProcessed fileToBeProcessed) throws WebPageProcessAsyncException {
        String county = fileToBeProcessed.getCountyName();

        try (InputStream in = Files.newInputStream(fileToBeProcessed.getFile())) {
            Document doc = Jsoup.parse(in, CharEncoding.UTF_8, WebPageConstants.BASE_URL);

            Elements hiddenDivs = retrieveHiddenDivs(doc);
            Elements data = doc.select(WebSelectorConstants.ALL_ROW);

            List<Restaurant> restaurantsInFile = new ArrayList<>();
            for (Element entry : data) {
                RestaurantProcessor restaurantProcessor =
                        new RestaurantProcessor(county, entry, hiddenDivs);
                Optional<Restaurant> restaurant = restaurantProcessor.createRestaurant();
                restaurant.ifPresent(restaurantsInFile::add);
            }

            log.info("Web Page Processing completed for county: {} size: {}", county, restaurantsInFile.size());

            restaurantService.saveAll(restaurantsInFile);
            return CompletableFuture.completedFuture(restaurantsInFile);
        } catch (Selector.SelectorParseException | IOException e) {
            log.error("Exception processing the downloaded web page for  {}", county, e);
            throw new WebPageProcessAsyncException("Error during processing of " + county + " file", e);
        }
    }


}
