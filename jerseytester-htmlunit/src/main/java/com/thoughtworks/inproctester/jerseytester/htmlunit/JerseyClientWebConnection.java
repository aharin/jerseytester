package com.thoughtworks.inproctester.jerseytester.htmlunit;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.http.HttpHeaders;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        addCookiesToRequest(jerseyClientRequest);
        return jerseyClient.handle(jerseyClientRequest);
    }

    private void addCookiesToRequest(ClientRequest jerseyClientRequest) {
        if (!cookieManager.getCookies().isEmpty()) {
            String cookieHeaderValue = "";
            for (Cookie cookie : cookieManager.getCookies()) {
                if (jerseyClientRequest.getURI().toASCIIString().contains(cookie.getDomain()) &&
                    (cookie.getPath() == null || jerseyClientRequest.getURI().getPath().startsWith(cookie.getPath()))) {
                        cookieHeaderValue = cookieHeaderValue + cookie.getName() + "=" + cookie.getValue() + "; ";
                }
            }
            jerseyClientRequest.getHeaders().putSingle(javax.ws.rs.core.HttpHeaders.COOKIE, cookieHeaderValue);
        }
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

        String contentType = getContentType(request);
        String acceptType = getAcceptType(request);
        ClientRequest.Builder requestBuilder = ClientRequest.create().type(contentType).accept(acceptType);

        if (request.getHttpMethod() == HttpMethod.POST) {
            if ((request.getEncodingType() == FormEncodingType.URL_ENCODED) && (!request.getRequestParameters().isEmpty())) {
                requestBuilder.entity(new UrlEncodedContent(request.getRequestParameters()).generateFormDataAsString());
            } else {
                requestBuilder.entity(request.getRequestBody());
            }
        }
        return requestBuilder.build(getRequestUri(request), request.getHttpMethod().name());
    }

    private String getContentType(WebRequest request) {
        for (Map.Entry<String, String> header : request.getAdditionalHeaders().entrySet()) {
            if (header.getKey().equals(HttpHeaders.CONTENT_TYPE)) {
                return header.getValue();
            }
        }

        return "*/*";
    }

    private String getAcceptType(WebRequest request) {
        for (Map.Entry<String, String> header : request.getAdditionalHeaders().entrySet()) {
            if (header.getKey().equals(HttpHeaders.ACCEPT)) {
                return header.getValue();
            }
        }

        return "*/*";
    }

    private URI getRequestUri(WebRequest request) {
        try {
            return request.getUrl().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
