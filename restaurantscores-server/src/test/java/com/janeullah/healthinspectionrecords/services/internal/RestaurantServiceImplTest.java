package com.janeullah.healthinspectionrecords.services.internal;

import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService = new RestaurantServiceImpl(restaurantRepository);

    @Test
    public void testFindAllFlattenedRestaurants() {
        when(restaurantRepository.findAllFlattenedRestaurants()).thenReturn(new ArrayList<>());
        assertTrue(restaurantService.findAllFlattenedRestaurants().isEmpty());
    }

    @Test
    public void testFindAll() {
        when(restaurantRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(restaurantService.findAll().isEmpty());
    }


    @Test
    public void testEstablishmentContainingName() {
        assertTrue(restaurantService.findByEstablishmentInfoCountyIgnoreCase("ka").isEmpty());
    }

    @Test
    public void testFindEstablishmentByNameAndCounty() {
        assertTrue(restaurantService.findEstablishmentByNameAndCounty("Wendy's", "Gobe").isEmpty());
    }

    @Test
    public void testFindById() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(new Restaurant()));
        assertNotNull(restaurantService.findById(4L));
    }

    @Test
    public void testFindById_Null() {
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertNull(restaurantService.findById(4L));
    }


    @Test
    public void testRestaurantsWithScoresGreaterThanOrEqual() {
        restaurantService.findRestaurantsWithScoresGreaterThanOrEqual(85);
        verify(restaurantRepository, times(1)).findRestaurantsWithScoresGreaterThanOrEqual(85);
    }

    @Test
    public void testRestaurantsWithScoresLessThanOrEqual() {
        restaurantService.findRestaurantsWithScoresLessThanOrEqual(70);
        verify(restaurantRepository, times(1)).findRestaurantsWithScoresLessThanOrEqual(70);
    }

    @Test
    public void testRestaurantsWithScoresBetween() {
        restaurantService.findRestaurantsWithScoresBetween(70, 85);
        verify(restaurantRepository, times(1)).findRestaurantsWithScoresBetween(70, 85);
    }

    @Test
    public void testFindRestaurantsWithCriticalViolations() {
        restaurantService.findRestaurantsWithCriticalViolations();
        verify(restaurantRepository, times(1)).findRestaurantsWithCriticalViolations();
    }

    @Test
    public void testSaveAll() {
        restaurantService.saveAll(anyList());
        verify(restaurantRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testDeleteAllRecords() {
        restaurantService.deleteAllRecords();
        verify(restaurantRepository, times(1)).deleteAll();
    }

    @Test
    public void testGetCount() {
        when(restaurantRepository.count()).thenReturn(0L);
        assertEquals(0L, restaurantService.getCount());
    }
}
