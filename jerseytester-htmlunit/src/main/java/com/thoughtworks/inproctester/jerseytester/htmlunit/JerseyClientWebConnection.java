package com.thoughtworks.inproctester.jerseytester.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class JerseyClientWebConnection implements WebConnection {

    private Client jerseyClient;
    private CookieManager cookieManager;

    public JerseyClientWebConnection(Client jerseyClient, CookieManager cookieManager) {
        this.jerseyClient = jerseyClient;
        this.cookieManager = cookieManager;
    }


    public WebResponse getResponse(WebRequest webRequest) throws IOException {
        ClientRequest jerseyClientRequest = adaptHtmlunitRequest(webRequest);
        ClientResponse jerseyClientResponse = processJerseyClientRequest(jerseyClientRequest);
        WebResponseData htmlunitResponseData = adaptJerseyClientResponse(jerseyClientResponse);
        return new WebResponse(htmlunitResponseData, webRequest, 0);
    }

    private ClientResponse processJerseyClientRequest(ClientRequest jerseyClientRequest) throws IOException {
//        addCookiesToRequest(jerseyClientRequest);
        ClientResponse testerResponse = jerseyClient.handle(jerseyClientRequest);
//        storeCookiesFromResponse(jerseyClientRequest, testerResponse);
        return testerResponse;
    }

    static WebResponseData adaptJerseyClientResponse(ClientResponse jerseyClientResponse) throws IOException {
        final List<NameValuePair> headers = new ArrayList<NameValuePair>();
        MultivaluedMap<String, String> responseHeaders = jerseyClientResponse.getHeaders();
        for (String headerName : responseHeaders.keySet()) {
            List<String> headerValues = responseHeaders.get(headerName);
            for (String headerValue : headerValues) {
                headers.add(new NameValuePair(headerName, headerValue));
            }
        }

        String content = jerseyClientResponse.getEntity(String.class);
        if (content == null) content = "";
        return new WebResponseData(content.getBytes("UTF-8"), jerseyClientResponse.getStatus(), jerseyClientResponse.getClientResponseStatus().getReasonPhrase(), headers);
    }


    private ClientRequest adaptHtmlunitRequest(WebRequest request) {

        ClientRequest.Builder requestBuilder = ClientRequest.create().type(request.getEncodingType().getName());

        if (request.getHttpMethod() == HttpMethod.POST) {
            if (request.getEncodingType() == FormEncodingType.URL_ENCODED) {
                requestBuilder.entity(new UrlEncodedContent(request.getRequestParameters()).generateFormDataAsString());
            }
        }
        return requestBuilder.build(getRequestUri(request), request.getHttpMethod().name());
    }

    private URI getRequestUri(WebRequest request) {
        try {
            return request.getUrl().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
