package com.janeullah.healthinspectionrecords.exceptions;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.janeullah.healthinspectionrecords.util.TestFileUtil;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class HerokuClientExceptionTest {

    private static final String  CAUSE =
    "{\"root_cause\":[{\"type\":\"index_already_exists_exception\",\"reason\":\"index [restaurants/y-529269fb1459] already exists\",\"index_uuid\":\"y-529269fb1459\",\"index\":\"restaurants\"}],\"reason\":\"index [restaurants/y-529269fb1459] already exists\",\"index_uuid\":\"y-529269fb1459\",\"index\":\"restaurants\"}";

    @Test
    public void testExceptionConstruction_Success() {
        HerokuClientException exception = new HerokuClientException(400, "", TestFileUtil.INDEX_ALREADY_EXISTS);
        assertEquals("index_already_exists_exception", exception.getErrorType());
    }

    @Test
    public void testGetErrorFromResponse_Code() throws IOException {
        JsonObject jsonResponse = readHerokuError();
        HerokuClientException exception = new HerokuClientException(400, "", null);
        assertEquals("index_already_exists_exception", exception.getErrorFromResponse(jsonResponse));
    }

    @Test
    public void testGetErrorFromResponse_ErrorType() throws IOException {
        JsonObject jsonResponse = readHerokuError();
        jsonResponse.get("error").getAsJsonObject().remove("type");
        HerokuClientException exception = new HerokuClientException(400, "", null);
        assertEquals(CAUSE, exception.getErrorFromResponse(jsonResponse));

    }

    private JsonObject readHerokuError() throws IOException {
        String mappingResponse = TestFileUtil.readFile("src/test/resources/heroku/restaurants-index-already-exists.json", Charset.forName("UTF-8"));
        return new Gson().fromJson(mappingResponse, JsonObject.class);
    }
}
