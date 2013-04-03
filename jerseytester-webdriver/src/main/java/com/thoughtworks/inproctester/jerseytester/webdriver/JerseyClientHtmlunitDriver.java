package com.thoughtworks.inproctester.jerseytester.webdriver;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.Client;
import com.thoughtworks.inproctester.jerseytester.htmlunit.JerseyClientWebConnection;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.ws.rs.core.MediaType;

public class JerseyClientHtmlunitDriver extends HtmlUnitDriver {
    public JerseyClientHtmlunitDriver(Client jerseyClient) {
        getWebClient().setWebConnection(new JerseyClientWebConnection(jerseyClient, getWebClient().getCookieManager()));
    }

    @Override
    public void get(String url) {
        getWebClient().addRequestHeader(HttpHeaders.ACCEPT, MediaType.TEXT_HTML);
        super.get(url);
        getWebClient().removeRequestHeader(HttpHeaders.ACCEPT);
    }
}

