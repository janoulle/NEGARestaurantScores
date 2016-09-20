package com.janeullah.healthinspectionrecords.org.async;

import com.janeullah.healthinspectionrecords.org.constants.WebPageConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageRequestAsync implements Callable<String> {

    private String url;
    private String name;
    private final CountDownLatch doneSignal;

    public WebPageRequestAsync(String url,String name, CountDownLatch doneSignal) {
        this.url = url;
        this.name = name;
        this.doneSignal = doneSignal;
    }

    public boolean writeFileToDisk(InputStream reqStream) {
        try {
            String fileName = name + WebPageConstants.PAGE_URL;
            File destinationFile = new File(WebPageConstants.PATH_TO_PAGE_STORAGE + "/" + fileName);
            FileUtils.copyInputStreamToFile(reqStream, destinationFile);
            return true;
        } catch (IOException e) {
            System.err.print("event=\"error writing file to disk\"");
            return false;
        }
    }

    @Override
    public String call() throws Exception {
        try {
            InputStream is = new URL(url).openStream();
            boolean isWriteSuccess = writeFileToDisk(is);
            if (!isWriteSuccess){
                System.err.println("event=\"Unable to write file to disk\"");
            }
            doneSignal.countDown();
            return isWriteSuccess ? name + WebPageConstants.PAGE_URL : StringUtils.EMPTY;
        }catch(IOException  e){
            System.err.println("event=\"Exception opening url\"");
        }
        return StringUtils.EMPTY;
    }

}
