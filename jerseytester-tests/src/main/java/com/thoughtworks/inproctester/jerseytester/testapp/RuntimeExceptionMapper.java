package com.thoughtworks.inproctester.jerseytester.testapp;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        exception.printStackTrace();
        return Response.serverError().type(MediaType.TEXT_PLAIN_TYPE).entity(stringWriter.toString()).build();
    }
}