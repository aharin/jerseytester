package com.thoughtworks.inproctester.jerseytester.container;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.impl.container.inmemory.TestResourceClientHandler;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * A trimmed down version of InMemoryTestContainerFactory which omits logging by default.
 */
public class InMemoryTestContainerFactoryEx implements TestContainerFactory {
    @SuppressWarnings("unchecked")
    public Class<LowLevelAppDescriptor> supports() {
        return LowLevelAppDescriptor.class;
    }

    public TestContainer create(URI baseUri, AppDescriptor ad) {
        return new InMemoryTestContainerEx(baseUri, (LowLevelAppDescriptor) ad);
    }

    private static class InMemoryTestContainerEx implements TestContainer {

        final URI baseUri;

        final ResourceConfig resourceConfig;

        final WebApplication webApp;

        private InMemoryTestContainerEx(URI baseUri, LowLevelAppDescriptor ad) {
            this.baseUri = UriBuilder.fromUri(baseUri).build();
            this.resourceConfig = ad.getResourceConfig();
            this.webApp = WebApplicationFactory.createWebApplication();
        }

        public Client getClient() {
            ClientConfig clientConfig = new DefaultClientConfig();

            clientConfig.getSingletons().addAll(resourceConfig.getProviderSingletons());

            return new Client(new TestResourceClientHandler(baseUri, webApp), clientConfig);
        }

        public URI getBaseUri() {
            return baseUri;
        }

        public void start() {
            if (!webApp.isInitiated()) {
                webApp.initiate(resourceConfig);
            }
        }

        public void stop() {
            if (webApp.isInitiated()) {
                webApp.destroy();
            }
        }

    }
}
