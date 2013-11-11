package com.thoughtworks.inproctester.jerseytester.htmlunit;


import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JerseyClientWebConnectionTest {

    @Mock
    private CookieManager cookieManager;
    @Mock
    private Client jerseyClient;
    @Captor
    private ArgumentCaptor<ClientRequest> clientRequestCaptor;

    @Test
    public void shouldAddCookiesToRequestWhenDomainAndPathMatches() throws Exception {
        Set<Cookie> cookies = new HashSet<Cookie>();
        cookies.add(new Cookie("google.co.uk", "cookieone", "valueone"));
        cookies.add(new Cookie("google.co.uk", "cookietwo", "valuetwo"));
        cookies.add(new Cookie("bbc.co.uk", "cookiethree", "valuethree"));
        cookies.add(new Cookie("google.co.uk", "cookiefour", "valuefour", "/search", 1, true));
        when(cookieManager.getCookies()).thenReturn(cookies);

        ClientResponse clientResponse = mock(ClientResponse.class);
        when(jerseyClient.handle(any(ClientRequest.class))).thenReturn(clientResponse);
        when(clientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());
        when(clientResponse.getClientResponseStatus()).thenReturn(ClientResponse.Status.OK);

        WebRequest request = mock(WebRequest.class);
        when(request.getUrl()).thenReturn(URI.create("http://www.google.co.uk").toURL());
        when(request.getHttpMethod()).thenReturn(HttpMethod.GET);

        JerseyClientWebConnection jerseyClientWebConnection = new JerseyClientWebConnection(jerseyClient, cookieManager);
        jerseyClientWebConnection.getResponse(request);

        verify(jerseyClient).handle(clientRequestCaptor.capture());

        ClientRequest clientRequestCaptorValue = clientRequestCaptor.getValue();
        assertThat((String) clientRequestCaptorValue.getHeaders().getFirst(javax.ws.rs.core.HttpHeaders.COOKIE), is("cookietwo=valuetwo; cookieone=valueone; "));
    }

}
