package com.thoughtworks.inproctester.jerseytester.testapp;

import org.glassfish.jersey.server.mvc.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/")
public class TestResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable status(@HeaderParam("Accept") String accept, @HeaderParam("Cookie") String cookie) {
        HashMap<String, String> model = new HashMap<String, String>();
        model.put("accept", accept);
        model.put("cookie", cookie);


        return new Viewable("/status.ftl", model);
    }

    @Path("cookie")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable cookie(@HeaderParam("Cookie") String cookie) {
        HashMap<String, String> model = new HashMap<String, String>();
        model.put("cookie", cookie);

        return new Viewable("/cookie.ftl", model);
    }
}
