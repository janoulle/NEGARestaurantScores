package com.janeullah.healthinspectionrecords.org.exceptions.config;

/**
 * Author: jane
 * Date:  9/19/2016
 */
public class IncompatibleConfigurationException extends Exception{
    public IncompatibleConfigurationException() {}

    public IncompatibleConfigurationException(String message)
    {
        super(message);
    }
}
