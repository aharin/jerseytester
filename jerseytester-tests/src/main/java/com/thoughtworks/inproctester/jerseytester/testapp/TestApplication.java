package com.thoughtworks.inproctester.jerseytester.testapp;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerProperties;
import org.glassfish.jersey.simple.SimpleContainerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestApplication {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ResourceConfig resourceConfig = resourceConfig();

        Closeable server = SimpleContainerFactory.create(new URI("http://0.0.0.0:8080"), resourceConfig);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                server.close();
            }
        }
    }

    public static ResourceConfig resourceConfig() {
        ResourceConfig resourceConfig = new ResourceConfig().property(FreemarkerProperties.TEMPLATES_BASE_PATH, "/ftl")
                .register(FreemarkerMvcFeature.class)
                .register(TestResource.class);


        resourceConfig.register(new RuntimeExceptionMapper());
        resourceConfig.register(new WebApplicationExceptionMapper());
        return resourceConfig;
    }
}
