package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.NearEarthObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Receives a set of neo ids and rates them after earth proximity.
 * Retrieves the approach data for them and sorts to the n closest.
 * https://api.nasa.gov/neo/rest/v1/neo/
 * Alerts if someone is possibly hazardous.
 */
public class ApproachDetector {
    private static final String NEO_URL = "https://api.nasa.gov/neo/rest/v1/neo/";
    private List<String> nearEarthObjectIds;
    private Client client;
    private static final ObjectMapper mapper = App.getObjectMapper();

    public ApproachDetector(List<String> ids) {
        this.nearEarthObjectIds = ids;
        this.client = ClientBuilder.newClient();
    }

    /**
     * Get the n closest approaches in this period
     * @param limit - n
     */
    public List<NearEarthObject> getClosestApproaches(int limit) {
        // Send REST API requests in parallel
        List<CompletableFuture<NearEarthObject>> futureNeos = nearEarthObjectIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> queryNearEarthObjectById(id)))
                .collect(Collectors.toList());

        List<NearEarthObject> neos = futureNeos.stream()
                .map(CompletableFuture::join)
                .filter(neo -> neo.getCloseApproachData() != null)
                .collect(Collectors.toList());
        System.out.println("Received " + neos.size() + " neos, now sorting");

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(1);
        return getClosest(neos, limit, startDate, endDate);
    }

    /**
     * Query a NEO from the NASA API by id
     * @param id the id of the NEO
     * @return The NearEarthObject corresponding to the given id
     */
    private NearEarthObject queryNearEarthObjectById(String id) {
        System.out.println("Check passing of object " + id);
        try {
            Response response = client
                    .target(NEO_URL + id)
                    .queryParam("api_key", App.API_KEY)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            return mapper.readValue(response.readEntity(String.class), NearEarthObject.class);
        } catch (IOException e) {
            System.err.println("Failed scanning for asteroids: " + e);
            return null;
        }
    }

    /**
     * Get the closest passing.
     * @param neos the NearEarthObjects
     * @param limit
     * @param startDate the start of the time period
     * @param endDate the end of the time period
     * @return
     */
    public static List<NearEarthObject> getClosest(List<NearEarthObject> neos, int limit, LocalDate startDate, LocalDate endDate) {
        // Filter out close approaches that do not happen in the given interval
        neos.forEach(neo -> neo.getCloseApproachData()
                .removeIf(data -> ! isCloseApproachDateWithinInterval(data, startDate, endDate)));

        return neos.stream()
                .filter(neo -> neo.getCloseApproachData() != null && ! neo.getCloseApproachData().isEmpty())
                .sorted(new VicinityComparator())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Check if the closeApproachDate in closeApproachData is withing the startDate - endDate interval
     * @param closeApproachData
     * @param startDate the start of the time period
     * @param endDate the end of the time period
     * @return whether the date of the approach is within the given interval
     */
    public static boolean isCloseApproachDateWithinInterval(CloseApproachData closeApproachData, LocalDate startDate, LocalDate endDate) {
        LocalDate closeApproachLocalDate = closeApproachData.getCloseApproachDate()
                .toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
        return (closeApproachLocalDate.isEqual(startDate) || closeApproachLocalDate.isAfter(startDate)) &&
                (closeApproachLocalDate.isEqual(endDate) ||closeApproachLocalDate.isBefore(endDate));
    }

}
