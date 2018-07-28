package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.constants.InspectionType;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.janeullah.healthinspectionrecords.constants.WebSelectorConstants.*;
import static org.hamcrest.Matchers.hasSize;
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
        Optional<Restaurant> restaurant = restaurantProcessor.createRestaurant();
        assertFalse(restaurant.isPresent());
    }

    @Test
    public void testGenerateProcessedRestaurant_ParseException() {
        Element mockElement = mock(Element.class);
        Elements mockedHiddenDivs = mock(Elements.class);
        when(mockElement.select(anyString())).thenThrow(new Selector.SelectorParseException("Bad selector"));
        RestaurantProcessor restaurantProcessor = new RestaurantProcessor("Clarke", mockElement, mockedHiddenDivs);
        Optional<Restaurant> restaurant = restaurantProcessor.createRestaurant();
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
        Optional<Restaurant> restaurant = restaurantProcessor.createRestaurant();
        assertTrue(restaurant.isPresent());
        assertNotNull(restaurant.get().getEstablishmentInfo());
    }

    @Test
    public void testGenerateProcessedRestaurant_testGetScoreOfMostRecentInspectionException() {
        Document doc =
                Jsoup.parseBodyFragment(
                        "<table class=\"sorted\" id=\"new\" style=\"display:none\"> <thead> <tr> <th id=\"grade\">Score</th> <th id=\"estab\">Establishment</th> <th id=\"type\">Type</th> <th id=\"date\">Date</th> <th class=\"hidden\"></th> <th id=\"violation\">Critical Violations</th> </tr> </thead> <tbody> <tr> <td align=\"center\" class=\"red\"> 78 <div class=\"green followup\"> 91 <div style=\"font-size:.5em\">Follow-Up</div> </div> </td> <td align=\"left\"> <div class=\"estab_name\">JOHNNY'S NEW YORK STYLE PIZZA</div> <div class=\"estab_addr\">1040 GAINES SCHOOL RD. SUITE 219 ATHENS GA, 30605</div> </td> <td align=\"left\" class=\"type\"> Routine / Follow-Up </td> <td align=\"right\" class=\"date\"> 03/19/2018 <span>/ 04/24/2018</span> </td> <td class=\"hidden\">4</td> <td align=\"left\"> <div class=\"crit\"> <a id=\"crit_334708\" href=\"$334708?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 1-2A. PIC present, demonstrates knowledge, performs duties</a> </div> <div class=\"crit\"> <a id=\"crit_334729\" href=\"$334729?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 6-1A. Proper cold holding temperatures</a> </div> <div class=\"crit\"> <a id=\"crit_334730\" href=\"$334730?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 6-1B. Proper hot holding temperatures</a> </div> <div class=\"crit\"> <a id=\"crit_334732\" href=\"$334732?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 6-1D. Time as a public health control: procedures and records</a> </div> <div class=\"ncrit\" id=\"noncrita_51209\"> <a href=\"Javascript:displayNonCrit(51209)\">Display Non-Critical Violations</a> </div> <div style=\"display:none\" id=\"noncrit_51209\"> <div class=\"ncrit\"> <a id=\"crit_334747\" href=\"$334747?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 12B. Personal cleanliness</a> </div> <div class=\"ncrit\"> <a id=\"crit_334749\" href=\"$334749?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 12D. Washing fruits and vegetables</a> </div> <div class=\"ncrit\"> <a id=\"crit_334750\" href=\"$334750?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 13A. Posted: Permit/Inspection/Choking Poster/Handwashing</a> </div> <div class=\"ncrit\"> <a id=\"crit_334756\" href=\"$334756?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 15A. Food and nonfood-contact surfaces cleanable, properly designed, constructed, and used</a> </div> <div class=\"ncrit\"> <a id=\"crit_334762\" href=\"$334762?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 17A. Toilet facilities: properly constructed, supplied, cleaned</a> </div> <div class=\"ncrit\"> <a id=\"crit_334764\" href=\"$334764?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 17C. Physical facilities installed, maintained, and clean</a> </div> <div class=\"ncrit\"> <a id=\"crit_334765\" href=\"$334765?width=300\" class=\"betterTip\" title=\"$content\">Violation of : 17D. Adequate ventilation and lighting; designated areas used</a> </div> </div> </td> </tr> </tbody> </table>");
        Element rowElement = doc.body();
        Elements mockedHiddenDivs = mock(Elements.class);

        RestaurantProcessor restaurantProcessor = new RestaurantProcessor("Clarke", rowElement, mockedHiddenDivs);
        Optional<Restaurant> restaurant = restaurantProcessor.createRestaurant();
        assertTrue(restaurant.isPresent());
        assertNotNull(restaurant.get().getEstablishmentInfo());
        assertThat(restaurant.get().getInspectionReports(), hasSize(1));
        assertThat(restaurant.get().getInspectionReports().get(0).getViolations(), hasSize(11));
        assertEquals(InspectionType.RE_INSPECTION, restaurant.get().getInspectionReports().get(0).getInspectionType());
        assertEquals("2018-04-24", restaurant.get().getInspectionReports().get(0).getDateReported().toString());
        assertEquals(91, restaurant.get().getInspectionReports().get(0).getScore());
    }
}
