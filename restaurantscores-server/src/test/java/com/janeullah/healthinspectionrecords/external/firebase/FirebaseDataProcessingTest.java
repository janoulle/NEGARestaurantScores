package com.janeullah.healthinspectionrecords.external.firebase;

import com.google.common.collect.ImmutableMap;
import com.janeullah.healthinspectionrecords.domain.dtos.County;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedInspectionReport;
import com.janeullah.healthinspectionrecords.domain.dtos.FlattenedRestaurant;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.services.external.firebase.FirebaseDataProcessing;
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService;
import com.janeullah.healthinspectionrecords.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseDataProcessingTest {

    @InjectMocks
    private FirebaseDataProcessing firebaseDataProcessing;

    @Mock
    private RestaurantService restaurantService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateAndRetrieveMapOfCounties() {

        when(restaurantService.findByEstablishmentInfoCountyIgnoreCase("Walton"))
                .thenReturn(TestUtil.getRestaurants());

        Map<String, County> results = firebaseDataProcessing.createAndRetrieveMapOfCounties(anyMap());
        assertEquals(10, results.size());
        assertEquals(2, results.get("Walton").getRestaurants().size());
        assertEquals(0, results.get("Clarke").getRestaurants().size());
        assertEquals(0, results.get("Oconee").getRestaurants().size());
        assertEquals(0, results.get("Oglethorpe").getRestaurants().size());
        assertEquals(0, results.get("Morgan").getRestaurants().size());
        assertEquals(0, results.get("Greene").getRestaurants().size());
        assertEquals(0, results.get("Madison").getRestaurants().size());
        assertEquals(0, results.get("Jackson").getRestaurants().size());
    }

    @Test
    public void testFlattenMapOfRestaurants() {
        Restaurant restaurant = TestUtil.getSingleRestaurant();
        restaurant.setId(1L);
        Map<String, List<Restaurant>> data = ImmutableMap.of("" +
                "Walton", Collections.singletonList(restaurant));
        Map<String, FlattenedRestaurant> results = firebaseDataProcessing.flattenMapOfRestaurants(data);
        assertEquals(1, results.size());
        FlattenedRestaurant firstEntry = results.values().iterator().next();
        assertEquals(restaurant.getEstablishmentInfo().getName() + "-" + restaurant.getId(), firstEntry.getNameKey());
    }

    @Test
    public void testCreateAndRetrieveViolations() {
        Restaurant restaurant = TestUtil.getSingleRestaurant();
        restaurant.setId(1L);
        Map<String, List<Restaurant>> data = ImmutableMap.of("" +
                "Walton", Collections.singletonList(restaurant));
        Map<String, FlattenedRestaurant> results = firebaseDataProcessing.flattenMapOfRestaurants(data);
        Map<String, FlattenedInspectionReport> violations = firebaseDataProcessing.createAndRetrieveViolations(results);

        assertEquals(1, violations.size());
    }
}
