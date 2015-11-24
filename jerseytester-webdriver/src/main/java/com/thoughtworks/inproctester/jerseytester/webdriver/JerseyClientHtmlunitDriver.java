package com.thoughtworks.inproctester.jerseytester.webdriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.common.net.HttpHeaders;
import com.thoughtworks.inproctester.jerseytester.htmlunit.JerseyClientWebConnection;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

public class JerseyClientHtmlunitDriver extends HtmlUnitDriver {
    public JerseyClientHtmlunitDriver(Client jerseyClient) {
        setWebConnection(jerseyClient);
    }

    public JerseyClientHtmlunitDriver(BrowserVersion browserVersion, Client jerseyClient) {
        super(browserVersion);
        setWebConnection(jerseyClient);
    }

    @Override
    public void get(String url) {
        getWebClient().addRequestHeader(HttpHeaders.ACCEPT, MediaType.TEXT_HTML);
        super.get(url);
        getWebClient().removeRequestHeader(HttpHeaders.ACCEPT);
    }

    private void setWebConnection(Client jerseyClient) {
        getWebClient().setWebConnection(new JerseyClientWebConnection(jerseyClient, getWebClient().getCookieManager()));
    }
}

