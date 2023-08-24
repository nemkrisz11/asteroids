package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.NearEarthObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class TestApproachDetector {

    private static final ObjectMapper mapper = App.getObjectMapper();
    private NearEarthObject neo1, neo2;
    private LocalDate today;

    @Before
    public void setUp() throws IOException {
        neo1 = mapper.readValue(getClass().getResource("/neo_example.json"), NearEarthObject.class);
        neo2 = mapper.readValue(getClass().getResource("/neo_example2.json"), NearEarthObject.class);
        today = LocalDate.of(2020, 1, 1);
    }

    @Test
    public void testDateComparison() {
        CloseApproachData data = neo1.getCloseApproachData().get(0);
        // Approach is inside 1-week period
        assertTrue(ApproachDetector.isCloseApproachDateWithinInterval(data,  LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1).plusWeeks(1)));
        // Start date overlaps with approach
        assertTrue(ApproachDetector.isCloseApproachDateWithinInterval(data, LocalDate.of(2020, 1, 1),  LocalDate.of(2020, 1, 1)));
        // End date overlaps with approach
        assertTrue(ApproachDetector.isCloseApproachDateWithinInterval(data, LocalDate.of(2019, 12, 31), LocalDate.of(2020, 1, 1)));
        // Approach is before period
        assertFalse(ApproachDetector.isCloseApproachDateWithinInterval(data, LocalDate.of(2019, 12, 30), LocalDate.of(2019, 12, 31)));
        // Invalid interval (end is before start)
        assertFalse(ApproachDetector.isCloseApproachDateWithinInterval(data,  LocalDate.of(2020, 1, 2),  LocalDate.of(2019, 12, 31)));
    }

    @Test
    public void testFiltering() {
        List<NearEarthObject> neos = List.of(neo1, neo2);
        List<NearEarthObject> filtered = ApproachDetector.getClosest(neos, 1, today, today.plusWeeks(1));
        // In Jan 2020, neo1 is closer (5390966 km, vs neo2's at 7644137 km)
        assertEquals(1, filtered.size());
        assertEquals(neo1, filtered.get(0));
    }
}
