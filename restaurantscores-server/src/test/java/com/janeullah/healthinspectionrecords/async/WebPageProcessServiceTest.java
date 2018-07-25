package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.domain.services.RestaurantPersistenceService;
import com.janeullah.healthinspectionrecords.domain.services.WebPageProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import static com.janeullah.healthinspectionrecords.util.TestFileUtil.FILES;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class WebPageProcessServiceTest {

    @InjectMocks
    private WebPageProcessService webPageProcessService;

    @Mock
    private RestaurantPersistenceService restaurantPersistenceService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitFileForProcessing() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        File file = FILES[0];

        webPageProcessService.submitFileForProcessing(file.toPath(), latch);
        latch.await();

        verify(restaurantPersistenceService, times(1)).saveAll(anyList());
    }

    @Test
    public void testSubmitMultipleFilesForProcessing() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        for (File file : FILES) {
            webPageProcessService.submitFileForProcessing(file.toPath(), latch);
        }

        latch.await();

        verify(restaurantPersistenceService, times(10)).saveAll(anyList());
    }

    @Test
    public void testWaitForAllProcessing() throws InterruptedException {
        CountDownLatch latch = mock(CountDownLatch.class);
        webPageProcessService.waitForAllProcessing(latch);
        verify(latch).await();
    }
}
