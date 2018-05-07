package com.janeullah.healthinspectionrecords.events;

import com.janeullah.healthinspectionrecords.constants.PathVariables;
import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.FileHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebPageProcessingTest {

    @InjectMocks
    private WebPageProcessing webPageProcessing;

    @Mock
    private PathVariables pathVariables;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWebPageProcessingSuccess() {
        File[] files = FileHelper.getFilesInDirectory("/downloads/webpages");
        when(pathVariables.getFilesInDefaultDirectory()).thenReturn(files);

        webPageProcessing.startProcessingOfDownloadedFiles();
        verify(restaurantRepository, times(10)).saveAll(anyList());
    }
}
