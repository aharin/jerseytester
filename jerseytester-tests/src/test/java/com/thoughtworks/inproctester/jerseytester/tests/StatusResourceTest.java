package com.thoughtworks.inproctester.jerseytester.tests;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.thoughtworks.inproctester.jerseytester.container.InMemoryTestContainerFactoryEx;
import com.thoughtworks.inproctester.jerseytester.testapp.TestApplication;
import com.thoughtworks.inproctester.jerseytester.webdriver.JerseyClientHtmlunitDriver;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import javax.ws.rs.core.UriBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class StatusResourceTest {

    private static TestContainer testContainer;


    private Client client;

    @BeforeClass
    public static void start() {
        testContainer = new InMemoryTestContainerFactoryEx().create(
                UriBuilder.fromUri("http://localhost/").port(8080).build(),
                new LowLevelAppDescriptor.Builder(TestApplication.resourceConfig()).build());

        testContainer.start();
    }


    @AfterClass
    public static void stop() {
        testContainer.stop();
    }

    @Before
    public void setUp() {
        client = testContainer.getClient();
    }


    @Test
    public void shouldRenderStatusPage() {
        WebDriver webDriver = new JerseyClientHtmlunitDriver(client);
        webDriver.get("http://localhost");
        assertThat(webDriver.getTitle(), is("Status"));
        assertThat(webDriver.findElement(By.id("accept_header.value")).getText(), is("text/html"));
    }


}
