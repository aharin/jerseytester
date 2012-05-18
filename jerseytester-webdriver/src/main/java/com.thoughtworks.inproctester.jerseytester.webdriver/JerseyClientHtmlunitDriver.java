package com.thoughtworks.inproctester.jerseytester.webdriver;

import com.sun.jersey.api.client.Client;
import com.thoughtworks.inproctester.jerseytester.htmlunit.JerseyClientWebConnection;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class JerseyClientHtmlunitDriver extends HtmlUnitDriver {
    public JerseyClientHtmlunitDriver(Client httpAppTester) {
        getWebClient().setWebConnection(new JerseyClientWebConnection(httpAppTester, getWebClient().getCookieManager()));
    }
}

