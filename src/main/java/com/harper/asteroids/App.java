/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.harper.asteroids;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.Feed;
import com.harper.asteroids.model.NearEarthObject;
import org.glassfish.jersey.client.ClientConfig;

import java.io.IOException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Main app. Gets the list of closest asteroids from NASA at
 * https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY
 * See documentation on the Asteroids - NeoWs API at https://api.nasa.gov/
 *
 * Prints the 10 closest
 *
 * Risk of getting throttled if we don't sign up for own key on https://api.nasa.gov/
 * Set environment variable 'API_KEY' to override.
 */
public class App {

    private static final String NEO_FEED_URL = "https://api.nasa.gov/neo/rest/v1/feed";

    protected static String API_KEY = "DEMO_KEY";

    private Client client;

    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    public App() {
        ClientConfig configuration = new ClientConfig();
        client = ClientBuilder.newClient(configuration);
    }

    /**
     * Scan space for asteroids close to earth
     */
    private void checkForAsteroids() {
        LocalDate today = LocalDate.now();
        Response response = client
                .target(NEO_FEED_URL)
                .queryParam("start_date",  today.toString())
                .queryParam("end_date", today.toString())
                .queryParam("api_key", API_KEY)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println("Got response: " + response);
        if(response.getStatus() == Response.Status.OK.getStatusCode()) {
            String content = response.readEntity(String.class);


            try {
                Feed neoFeed = mapper.readValue(content, Feed.class);
                ApproachDetector approachDetector = new ApproachDetector(neoFeed.getAllObjectIds());

                List<NearEarthObject> closest =  approachDetector.getClosestApproaches(10);
                System.out.println("Hazard?   Distance(km)    When                             Name");
                System.out.println("----------------------------------------------------------------------");
                for(NearEarthObject neo: closest) {
                    Optional<CloseApproachData> closestPass = neo.getCloseApproachData().stream()
                            .min(Comparator.comparing(CloseApproachData::getMissDistance));

                    if(closestPass.isEmpty()) continue;

                    System.out.println(String.format("%s       %12.3f  %s    %s",
                            (neo.isPotentiallyHazardous() ? "!!!" : " - "),
                            closestPass.get().getMissDistance().getKilometers(),
                            closestPass.get().getCloseApproachDateTime(),
                            neo.getName()
                            ));
                }
            } catch (IOException e) {
                System.err.println("Failed scanning for asteroids: " + e);
            }
        }
        else {
            System.err.println("Failed querying feed, got " + response.getStatus() + " " + response.getStatusInfo());
        }

    }


    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");
        if(apiKey != null && !apiKey.isBlank()) {
            API_KEY = apiKey;
        }
        new App().checkForAsteroids();
    }
}
