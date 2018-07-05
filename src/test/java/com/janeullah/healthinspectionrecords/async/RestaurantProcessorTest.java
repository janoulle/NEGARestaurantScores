package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.janeullah.healthinspectionrecords.constants.WebSelectorConstants.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantProcessorTest {

    @Test
    public void testGenerateProcessedRestaurant_NothingInRowElement() {
        Element mockElement = mock(Element.class);
        Elements mockedHiddenDivs = mock(Elements.class);
        // return empty results on any query
        when(mockElement.select(anyString())).thenReturn(new Elements());
        RestaurantProcessor restaurantProcessor = new RestaurantProcessor("Clarke", mockElement, mockedHiddenDivs);
        Optional<Restaurant> restaurant = restaurantProcessor.generateProcessedRestaurant();
        assertFalse(restaurant.isPresent());
    }

    @Test
    public void testGenerateProcessedRestaurant_ParseException() {
        Element mockElement = mock(Element.class);
        Elements mockedHiddenDivs = mock(Elements.class);
        when(mockElement.select(anyString())).thenThrow(new Selector.SelectorParseException("Bad selector"));
        RestaurantProcessor restaurantProcessor = new RestaurantProcessor("Clarke", mockElement, mockedHiddenDivs);
        Optional<Restaurant> restaurant = restaurantProcessor.generateProcessedRestaurant();
        assertFalse(restaurant.isPresent());
    }

    @Test
    public void testGenerateProcessedRestaurant_testGetDateOfMostRecentInspectionException() {
        Element mockElement = mock(Element.class);
        Elements mockedHiddenDivs = mock(Elements.class);

        Element restaurantNameData = mock(Element.class);
        when(restaurantNameData.text()).thenReturn("Restaurant Name");
        Elements restaurantName = mock(Elements.class);
        when(restaurantName.first()).thenReturn(restaurantNameData);
        when(mockElement.select(RESTAURANT_NAME_SELECTOR)).thenReturn(restaurantName);

        Element restaurantAddressData = mock(Element.class);
        when(restaurantNameData.text()).thenReturn("1600 Pennsylvania Ave");
        Elements restaurantAddress = mock(Elements.class);
        when(restaurantAddress.first()).thenReturn(restaurantAddressData);
        when(mockElement.select(RESTAURANT_ADDRESS_SELECTOR)).thenReturn(restaurantAddress);

        when(mockElement.select(INSPECTION_TYPE_SELECTOR)).thenReturn(new Elements());
        when(mockElement.select(CRITICAL)).thenReturn(new Elements());
        when(mockElement.select(NOT_CRITICAL)).thenReturn(new Elements());

        Element scoreDataElement = mock(Element.class);
        when(scoreDataElement.text()).thenReturn("91");
        Elements scoreData = mock(Elements.class);
        when(scoreData.first()).thenReturn(scoreDataElement);
        when(mockElement.select(SCORE_SELECTOR)).thenReturn(scoreData);

        Elements dateInformation = mock(Elements.class);
        Element dateInformationElement = mock(Element.class);
        when(dateInformationElement.text()).thenReturn("ab/cd/efgh / 04/24/2018");
        when(dateInformation.first()).thenReturn(dateInformationElement);
        when(mockElement.select(DATE_SELECTOR)).thenReturn(dateInformation);

        RestaurantProcessor restaurantProcessor = new RestaurantProcessor("Clarke", mockElement, mockedHiddenDivs);
        Optional<Restaurant> restaurant = restaurantProcessor.generateProcessedRestaurant();
        assertTrue(restaurant.isPresent());
        assertNotNull(restaurant.get().getEstablishmentInfo());
    }
}
