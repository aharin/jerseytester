package com.thoughtworks.inproctester.jerseytester.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.http.HttpHeaders;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
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
        Invocation jerseyInvocation = adaptHtmlunitRequest(webRequest);
        Response jerseyClientResponse = jerseyInvocation.invoke();
        WebResponseData htmlunitResponseData = adaptJerseyClientResponse(jerseyClientResponse);
        return new WebResponse(htmlunitResponseData, webRequest, 0);
    }

    private void addCookiesToRequest(URI uri, Invocation.Builder invocationBuilder) {
        if (!cookieManager.getCookies().isEmpty()) {

            for (Cookie cookie : cookieManager.getCookies()) {
                if (uri.toASCIIString().contains(cookie.getDomain()) &&
                    (cookie.getPath() == null || uri.getPath().startsWith(cookie.getPath()))) {
                        invocationBuilder.cookie(cookie.getName(), cookie.getValue());
                }
            }
        }
    }

    static WebResponseData adaptJerseyClientResponse(Response jerseyClientResponse) throws IOException {
        final List<NameValuePair> headers = new ArrayList<NameValuePair>();
        MultivaluedMap<String, Object> responseHeaders = jerseyClientResponse.getHeaders();
        for (String headerName : responseHeaders.keySet()) {
            List<Object> headerValues = responseHeaders.get(headerName);
            for (Object headerValue : headerValues) {
                headers.add(new NameValuePair(headerName, headerValue.toString()));
            }
        }

        String content = jerseyClientResponse.readEntity(String.class);
        if (content == null) content = "";
        return new WebResponseData(content.getBytes("UTF-8"), jerseyClientResponse.getStatus(), jerseyClientResponse.getStatusInfo().getReasonPhrase(), headers);
    }


    private Invocation adaptHtmlunitRequest(WebRequest request) {

        String contentType = getContentType(request);
        String acceptType = getAcceptType(request);

        URI requestUri = getRequestUri(request);
        WebTarget jerseyWebTarget = jerseyClient.target(requestUri);
        Invocation.Builder builder = jerseyWebTarget.request().accept(acceptType);

        addCookiesToRequest(requestUri, builder);

        if (request.getHttpMethod() == HttpMethod.POST) {
            String content;
            if (request.getEncodingType() == FormEncodingType.URL_ENCODED && contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
                content = new UrlEncodedContent(request.getRequestParameters()).generateFormDataAsString();
            } else {
                content = request.getRequestBody();
            }
            return builder.buildPost(Entity.<String>entity(content, contentType));
        }
        return builder.build(request.getHttpMethod().name());
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
