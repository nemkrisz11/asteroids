package com.harper.asteroids.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.App;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestParsing {

    private static final ObjectMapper mapper = App.getObjectMapper();


    @Before
    public void init() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void testFeedExampleDump() throws IOException {
        URL input = getClass().getResource("/feed_example.json");
        Feed feed = mapper.readValue(input, Feed.class);
        System.out.println("Check feed");
        assertNotNull(feed);
        assertEquals(feed.getElementCount(), feed.getAllObjectIds().size());

    }

    @Test
    public void testNeoExampleDump() throws IOException {
        URL input = getClass().getResource("/neo_example.json");
        NearEarthObject neo = mapper.readValue(input, NearEarthObject.class);
        System.out.println("Check neo: " + neo);
        assertNotNull(neo);

    }

}
