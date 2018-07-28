package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.domain.PathVariables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Author: Jane Ullah Date: 9/18/2016
 */
@Slf4j
public class WebPageRequestAsync implements Callable<String> {
    private final CountDownLatch doneSignal;
    private String url;
    private String name;
    private String userAgent;
    private PathVariables pathVariables;

    public WebPageRequestAsync(
            String url, String name, String userAgent, CountDownLatch doneSignal, PathVariables pathVariables) {
        this.url = url;
        this.name = name;
        this.userAgent = userAgent;
        this.doneSignal = doneSignal;
        this.pathVariables = pathVariables;
    }

    public String getName() {
        return name;
    }

    // https://stackoverflow.com/questions/2417485/file-separator-vs-slash-in-paths
    // https://stackoverflow.com/questions/5971964/file-separator-or-file-pathseparator
    private boolean writeFileToDisk(InputStream reqStream) {
        try {
            File destinationFile = pathVariables.getDefaultFilePath(name + WebPageConstants.PAGE_URL);
            FileUtils.copyInputStreamToFile(reqStream, destinationFile);
            return true;
        } catch (IOException e) {
            log.error("event=\"error writing file to disk\" for entry {}", name, e);
        }
        return false;
    }

    @Override
    public String call() throws Exception {
        try {
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            conn.setRequestProperty("User-Agent", userAgent);
            conn.connect();
            if (writeFileToDisk(conn.getInputStream())) {
                log.info("event=\"Successfully wrote {} to disk\"", name);
                return name + WebPageConstants.PAGE_URL;
            }
            log.error("event=\"Unable to write {} file to disk\"", url);
        } catch (IOException e) {
            log.error("Error opening connection to url {}", url, e);
        } finally {
            doneSignal.countDown();
        }
        return StringUtils.EMPTY;
    }
}
