package com.thoughtworks.inproctester.jerseytester.tests;

import com.thoughtworks.inproctester.jerseytester.testapp.TestApplication;
import com.thoughtworks.inproctester.jerseytester.webdriver.JerseyClientHtmlunitDriver;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class StatusResourceTest {

    private static TestContainer testContainer;

    private Client client;

    @BeforeClass
    public static void start() {
        testContainer = new InMemoryTestContainerFactory().create(
                UriBuilder.fromUri("http://localhost/").port(8080).build(),
                new ApplicationHandler(TestApplication.resourceConfig()));

        testContainer.start();
    }


    @AfterClass
    public static void stop() {
        testContainer.stop();
    }

    @Before
    public void setUp() {
        ClientConfig clientConfig = testContainer.getClientConfig();
        client = ClientBuilder.newClient(clientConfig);
    }


    @Test
    public void shouldRenderStatusPage() {
        WebDriver webDriver = new JerseyClientHtmlunitDriver(client);
        webDriver.get("http://localhost");
        assertThat(webDriver.getTitle(), is("Status"));
        assertThat(webDriver.findElement(By.id("accept_header.value")).getText(), is("text/html"));
    }

    @Test
    public void shouldReadCookiesFromRequest() {
        WebDriver webDriver = new JerseyClientHtmlunitDriver(client);
        webDriver.get("http://localhost");
        webDriver.manage().addCookie(new Cookie("cookieone", "valueone", "localhost", "/", null));
        webDriver.get("http://localhost");
        assertThat(webDriver.getTitle(), is("Status"));
        assertThat(webDriver.findElement(By.id("cookie_header.value")).getText(), is("$Version=1;cookieone=valueone"));
    }

    @Test
    public void shouldReadCookiesFromInChildPage() {
        WebDriver webDriver = new JerseyClientHtmlunitDriver(client);
        webDriver.get("http://localhost");
        webDriver.manage().addCookie(new Cookie("cookieone", "valueone", "localhost", "/", null));
        webDriver.get("http://localhost/cookie");
        assertThat(webDriver.getTitle(), is("Cookie"));
        assertThat(webDriver.findElement(By.id("cookie_header.value")).getText(), is("$Version=1;cookieone=valueone"));
    }

    @Test
    public void shouldNotSeeCookieFromDifferentDomain() {
        WebDriver webDriver = new JerseyClientHtmlunitDriver(client);
        webDriver.get("http://localhost");
        webDriver.manage().addCookie(new Cookie("cookieone", "valueone", "localhost", "/", null));
        webDriver.get("http://thoughtworks");
        assertThat(webDriver.getTitle(), is("Status"));
        assertThat(webDriver.findElement(By.id("cookie_header.value")).getText(), is(""));
    }

    @Test
    public void shouldNotSeeCookieForDifferentPath() {
        WebDriver webDriver = new JerseyClientHtmlunitDriver(client);
        webDriver.get("http://localhost");
        webDriver.manage().addCookie(new Cookie("cookieone", "valueone", "localhost", "/cookie", null));
        webDriver.get("http://localhost");
        assertThat(webDriver.getTitle(), is("Status"));
        assertThat(webDriver.findElement(By.id("cookie_header.value")).getText(), is(""));
    }

}
