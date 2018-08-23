package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.domain.FileToBeProcessed;
import com.janeullah.healthinspectionrecords.domain.entities.Restaurant;
import com.janeullah.healthinspectionrecords.exceptions.WebPageProcessAsyncException;
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.janeullah.healthinspectionrecords.util.TestFileUtil.FILES;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebPageProcessAsyncTest {

    @InjectMocks
    private WebPageProcessingAsync webPageProcessingAsync;

    @Mock
    private RestaurantService restaurantService;

    @Test
    public void testWebPageProcess_Success() throws InterruptedException, ExecutionException, WebPageProcessAsyncException {
        Arrays.sort(FILES, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        Map<String, Integer> fileAndSize = new HashMap<>();
        fileAndSize.put("Barrow", 155);
        fileAndSize.put("Clarke", 528);
        fileAndSize.put("Elbert", 43);
        fileAndSize.put("Greene", 75);
        fileAndSize.put("Jackson", 150);
        fileAndSize.put("Madison", 38);
        fileAndSize.put("Morgan", 63);
        fileAndSize.put("Oconee", 102);
        fileAndSize.put("Oglethorpe", 16);
        fileAndSize.put("Walton", 167);

        for (File file : FILES) {
            FileToBeProcessed fileToBeProcessed = new FileToBeProcessed(file.toPath()) ;
            CompletableFuture<List<Restaurant>> results = webPageProcessingAsync.processWebPage(fileToBeProcessed);
            assertThat(results.get(), hasSize(fileAndSize.get(fileToBeProcessed.getCountyName())));
        }
    }

    @Test(expected = WebPageProcessAsyncException.class)
    public void testWebPageProcess_ExceptionThrow() throws WebPageProcessAsyncException {
        Path nonExistentFile = new File("a").toPath();
        FileToBeProcessed fileToBeProcessed = new FileToBeProcessed(nonExistentFile);
        webPageProcessingAsync.processWebPage(fileToBeProcessed);
    }
}
