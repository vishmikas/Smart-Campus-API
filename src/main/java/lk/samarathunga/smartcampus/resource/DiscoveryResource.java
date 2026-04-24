package lk.samarathunga.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Discovery endpoint - provides API metadata and navigation links.
 * GET /api/v1
 *
 * Author : S.D.V. Samarathunga
 * UoW ID : 2151919w
 * IIT ID : 20241769
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("apiVersion", "1.0");
        info.put("title", "Smart Campus Sensor & Room Management API");
        info.put("adminContact", "2151919w@westminster.ac.uk");
        info.put("studentId", "S.D.V. Samarathunga | 2151919w / 20241769");

        Map<String, String> links = new HashMap<>();
        links.put("self",          "/api/v1");
        links.put("rooms",         "/api/v1/rooms");
        links.put("sensors",       "/api/v1/sensors");
        links.put("readings",      "/api/v1/sensors/{sensorId}/readings");

        info.put("resources", links);

        return info;
    }
}
