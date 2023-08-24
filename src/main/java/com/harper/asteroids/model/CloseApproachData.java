package com.harper.asteroids.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class CloseApproachData {

    @JsonProperty("close_approach_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date closeApproachDate;

    @JsonProperty("close_approach_date_full")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MMM-dd hh:mm")
    private Date closeApproachDateTime;

    @JsonProperty("epoch_date_close_approach")
    private long closeApproachEpochDate;

    @JsonProperty("relative_velocity")
    private Velocities relativeVelocity;

    @JsonProperty("miss_distance")
    private Distances missDistance;

    @JsonProperty("orbiting_body")
    private String orbitingBody;

    public Date getCloseApproachDate() {
        return closeApproachDate;
    }

    public Date getCloseApproachDateTime() {
        return closeApproachDateTime;
    }

    public long getCloseApproachEpochDate() {
        return closeApproachEpochDate;
    }

    public Velocities getRelativeVelocity() {
        return relativeVelocity;
    }

    public Distances getMissDistance() {
        return missDistance;
    }

    public String getOrbitingBody() {
        return orbitingBody;
    }
}
