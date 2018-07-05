package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.repository.RestaurantRepository;
import com.janeullah.healthinspectionrecords.util.TestFileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class WebPageProcessServiceTest {

    @InjectMocks
    private WebPageProcessService webPageProcessService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitFileForProcessing() throws InterruptedException, URISyntaxException {
        CountDownLatch latch = new CountDownLatch(1);
        File file = TestFileUtil.getFilesInDirectory("./src/test/resources/downloads/webpages")[0];

        webPageProcessService.submitFileForProcessing(file.toPath(), latch);
        latch.await();

        verify(restaurantRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testSubmitMultipleFilesForProcessing() throws InterruptedException, URISyntaxException {
        CountDownLatch latch = new CountDownLatch(10);
        for (File file : TestFileUtil.getFilesInDirectory("./src/test/resources/downloads/webpages")) {
            webPageProcessService.submitFileForProcessing(file.toPath(), latch);
        }

        latch.await();

        verify(restaurantRepository, times(10)).saveAll(anyList());
    }

    @Test
    public void testWaitForAllProcessing() throws InterruptedException {
        CountDownLatch latch = mock(CountDownLatch.class);
        webPageProcessService.waitForAllProcessing(latch);
        verify(latch).await();
    }
}
