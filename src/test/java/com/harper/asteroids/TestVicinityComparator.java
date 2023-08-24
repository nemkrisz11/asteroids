package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.NearEarthObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


public class TestVicinityComparator {

    private static final ObjectMapper mapper = App.getObjectMapper();
    private NearEarthObject neo1, neo2;

    @Before
    public void setUp() throws IOException {
        neo1 = mapper.readValue(getClass().getResource("/neo_example.json"), NearEarthObject.class);
        neo2 = mapper.readValue(getClass().getResource("/neo_example2.json"), NearEarthObject.class);
    }

    @Test
    public void testOrder() {
        VicinityComparator comparator = new VicinityComparator();

        assertThat(comparator.compare(neo1, neo2), greaterThan(0));
        assertThat(comparator.compare(neo2, neo1), lessThan(0));
        assertEquals(comparator.compare(neo1, neo1), 0);
    }
}
