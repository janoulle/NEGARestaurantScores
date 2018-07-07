package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.constants.WebSelectorConstants;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.exceptions.WebPageProcessAsyncException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/** Author: Jane Ullah Date: 9/18/2016 */
@Slf4j
public class WebPageProcessAsync implements Callable<List<Restaurant>> {
  private Path url;
  private Elements hiddenDivs;
  private String county;

  WebPageProcessAsync(String county, Path url) {
    this.county = county;
    this.url = url;
  }

  private List<Restaurant> ingestJsoupData() throws IOException {
    List<Restaurant> restaurantsInFile = new ArrayList<>();

    for (Element entry : processFile()) {
      RestaurantProcessor restaurantProcessor = new RestaurantProcessor(county, entry, hiddenDivs);
      Optional<Restaurant> restaurant = restaurantProcessor.generateProcessedRestaurant();
      restaurant.ifPresent(restaurantsInFile::add);
    }
    return restaurantsInFile;
  }

  private void setHiddenDivs(Document doc) {
    try {
      hiddenDivs =
          doc.select("div:not([class])")
              .stream()
              .filter(entry -> StringUtils.isNumeric(entry.id()))
              .collect(Collectors.toCollection(Elements::new));
    } catch (Selector.SelectorParseException e) {
      log.error("Error setting hidden divs which contain violation explanations", e);
    }
  }

  private Elements processFile() throws IOException {
    try (InputStream in = Files.newInputStream(url)) {
      Document doc = Jsoup.parse(in, CharEncoding.UTF_8, WebPageConstants.BASE_URL);
      setHiddenDivs(doc);
      return doc.select(WebSelectorConstants.ALL_ROW);
    }
  }

  @Override
  public List<Restaurant> call() throws WebPageProcessAsyncException {
    try {
      return ingestJsoupData();
    } catch (Selector.SelectorParseException | IOException e) {
      log.error("Exception processing the downloaded web page for  {}", county, e);
      throw new WebPageProcessAsyncException("Error during processing of " + county + " file", e);
    }
  }
}
