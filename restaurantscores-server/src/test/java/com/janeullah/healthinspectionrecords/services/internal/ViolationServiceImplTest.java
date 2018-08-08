package com.janeullah.healthinspectionrecords.services.internal;

import com.janeullah.healthinspectionrecords.domain.entities.Violation;
import com.janeullah.healthinspectionrecords.repository.ViolationRepository;
import com.janeullah.healthinspectionrecords.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ViolationServiceImplTest {
    @Mock
    private ViolationRepository violationRepository;

    @InjectMocks
    private ViolationService violationService = new ViolationServiceImpl(violationRepository);


    @Test
    public void testFindById() {
        when(violationRepository.findById(anyLong())).thenReturn(Optional.of(new Violation()));
        assertNotNull(violationService.findById(1L));
    }

    @Test
    public void testFindById_Null() {
        when(violationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertNull(violationService.findById(2L));
    }

    @Test
    public void testFindViolationsByRestaurantId() {
        when(violationRepository.findViolationsByRestaurantId(anyLong())).thenReturn(Collections.singletonList(TestUtil.getSingleViolation()));
        assertThat(violationService.findViolationsByRestaurantId(4L), hasSize(1));
    }

    @Test
    public void testFindViolationsByRestaurantId_Empty() {
        when(violationRepository.findViolationsByRestaurantId(anyLong())).thenReturn(new ArrayList<>());
        assertThat(violationService.findViolationsByRestaurantId(9L), hasSize(0));
    }

    @Test
    public void testFindAll() {
        when(violationRepository.findAll()).thenReturn(new ArrayList<>());
        assertThat(violationService.findAll(), hasSize(0));
    }
}
