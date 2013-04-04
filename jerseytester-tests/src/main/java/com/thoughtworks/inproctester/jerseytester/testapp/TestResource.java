package com.thoughtworks.inproctester.jerseytester.testapp;

import com.sun.jersey.api.view.Viewable;

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
    public Viewable status(@HeaderParam("Accept") final String accept) {
        return new Viewable("/status.ftl", new HashMap<String, String>() {{put("accept", accept);}});
    }
}
