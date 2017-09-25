package com.janeullah.healthinspectionrecords.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Author: jane
 * Date:  9/24/2017
 */
public class DataUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DataUtil.class);

    public static String writeValueAsString(Object object){
        try{
            return mapper.writeValueAsString(object);
        }catch(Exception e){
            logger.error("Unable to write class={} to string",object.getClass());
        }
        return StringUtils.EMPTY;
    }

    public static String writeStreamToString(InputStream is){
        try {
            return IOUtils.toString(is);
        }catch(Exception e){
            logger.error("Error converting stream to string",e);
        }
        return StringUtils.EMPTY;
    }
}
