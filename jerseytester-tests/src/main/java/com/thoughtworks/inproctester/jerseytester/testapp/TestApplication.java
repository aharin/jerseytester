package com.thoughtworks.inproctester.jerseytester.testapp;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.freemarker.FreemarkerViewProcessor;
import com.sun.jersey.simple.container.SimpleServerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;

public class TestApplication {
    public static void main(String[] args) throws IOException {
        ResourceConfig resourceConfig = new DefaultResourceConfig(
                new HashSet<Class<?>>() {{
                    add(TestResource.class);
                }}
        );

        resourceConfig.getSingletons().add(new RuntimeExceptionMapper());
        resourceConfig.getSingletons().add(new NotFoundExceptionMapper());
        resourceConfig.getProperties().put(FreemarkerViewProcessor.FREEMARKER_TEMPLATES_BASE_PATH, "/ftl");

        Closeable server = SimpleServerFactory.create("http://0.0.0.0:8080", resourceConfig);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                server.close();
            }
        }
    }
}
