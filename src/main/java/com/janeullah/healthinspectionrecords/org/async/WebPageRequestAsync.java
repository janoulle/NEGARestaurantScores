package com.janeullah.healthinspectionrecords.org.async;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Author: jane
 * Date:  9/18/2016
 */
public class WebPageRequestAsync implements Callable<InputStream> {

    private String url;

    public WebPageRequestAsync(String url) {
        this.url = url;
    }

    @Override
    public InputStream call() throws Exception {
        return new URL(url).openStream();
    }

}
