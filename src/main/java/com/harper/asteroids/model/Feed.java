package com.harper.asteroids.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Response for a feed query of Neos.
 */
public class Feed {
    @JsonProperty("element_count")
    private int elementCount;

    @JsonProperty("near_earth_objects")
    private Map<String, List<NearEarthObjectIds>> nearEarthObjects;

    public int getElementCount() {
        return elementCount;
    }

    public Map<String, List<NearEarthObjectIds>> getNearEarthObjects() {
        return nearEarthObjects;
    }

    public List<String> getAllObjectIds() {
        return nearEarthObjects.values().stream()
                .flatMap(l -> l.stream())
                .map(n -> n.getId())
                .collect(Collectors.toList());
    }
}
