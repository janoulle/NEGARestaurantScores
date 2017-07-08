package com.janeullah.healthinspectionrecords.async;

import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageRequestAsync implements Callable<String> {
    private static final Logger logger = LoggerFactory.getLogger(WebPageRequestAsync.class);

    private String url;
    private String name;
    private final CountDownLatch doneSignal;

    public WebPageRequestAsync(String url,String name, CountDownLatch doneSignal) {
        this.url = url;
        this.name = name;
        this.doneSignal = doneSignal;
    }

    //https://stackoverflow.com/questions/2417485/file-separator-vs-slash-in-paths
    //https://stackoverflow.com/questions/5971964/file-separator-or-file-pathseparator
    private boolean writeFileToDisk(InputStream reqStream) {
        try {
            String fileName = name + WebPageConstants.PAGE_URL;
            File destinationFile = new File(WebPageConstants.PATH_TO_PAGE_STORAGE + File.separator + fileName);
            FileUtils.copyInputStreamToFile(reqStream, destinationFile);
            return true;
        } catch (IOException e) {
            logger.error("event=\"error writing file to disk\" for entry {}",name,e);
        }
        return false;
    }

    @Override
    public String call() throws Exception {
        try {
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();
            conn.setRequestProperty("User-Agent", WebPageConstants.USER_AGENT);
            conn.connect();
            boolean isWriteSuccess = writeFileToDisk(conn.getInputStream());
            if (!isWriteSuccess){
                logger.error("event=\"Unable to write {} file to disk\"",url);
            }
            return isWriteSuccess ? name + WebPageConstants.PAGE_URL : StringUtils.EMPTY;
        }catch(IOException  e){
            logger.error("Error opening connection to url {}",url,e);
        }finally {
            doneSignal.countDown();
        }
        return StringUtils.EMPTY;
    }

}