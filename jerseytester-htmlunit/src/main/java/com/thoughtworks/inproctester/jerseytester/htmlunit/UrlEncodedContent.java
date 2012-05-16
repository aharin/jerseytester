package com.thoughtworks.inproctester.jerseytester.htmlunit;


import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class UrlEncodedContent {

    private List<NameValuePair> requestParameters;

    public UrlEncodedContent(List<NameValuePair> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String generateFormDataAsString() {
        StringBuilder s = new StringBuilder();

        for (NameValuePair requestParameter : requestParameters) {

            String key = requestParameter.getName();

            if (s.length() > 0) {
                s.append('&');
            }
            s.append(urlEncode(key)).append("=");
            String value = requestParameter.getValue();

            if (StringUtils.isNotEmpty(value)) {
                s.append(urlEncode(value));
            }

        }

        return s.toString();
    }


    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
