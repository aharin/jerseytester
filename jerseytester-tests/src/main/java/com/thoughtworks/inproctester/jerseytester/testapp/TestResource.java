package com.thoughtworks.inproctester.jerseytester.testapp;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/")
public class TestResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable status(@HeaderParam("Accept") String accept, @HeaderParam("Cookie") String cookie) {
        HashMap<String, String> model = new HashMap<String, String>();
        model.put("accept", accept);
        model.put("cookie", cookie);
        model.put("dummy_key", DataConstants.AJAX_JSON_DUMMY_DATA_KEY);
        model.put("dummy_value", DataConstants.AJAX_JSON_DUMMY_DATA_VALUE);

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(String postBody) {
        return Response.ok(postBody).build();
    }

    @Path("no-content")
    @GET
    public Response aResourceWithNoContent() {
        return Response.noContent().build();
    }
}
