package com.faizalsidek.osmdroid.custom;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * Created by 608761587 on 21/06/2015.
 */
public class MyHttpClient {
    private CloseableHttpClient httpClient;
    private HttpGet get;
    private int responseCode;

    public MyHttpClient(String url) throws Exception {
        init();
        get = new HttpGet(url);
    }

    public InputStream getResponse() throws Exception {
        CloseableHttpResponse response = httpClient.execute(get);
        HttpEntity entity = response.getEntity();
        responseCode = response.getStatusLine().getStatusCode();
        return entity.getContent();
    }

    public int getResponseCode() throws Exception {
        return responseCode;
    }

    public void init() throws Exception {
        SSLContext sslContext = SSLContexts.createSystemDefault();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                null,
                new X509HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }

                    @Override
                    public void verify(String s, SSLSocket sslSocket) throws IOException {

                    }

                    @Override
                    public void verify(String s, X509Certificate x509Certificate) throws SSLException {

                    }

                    @Override
                    public void verify(String s, String[] strings, String[] strings1) throws SSLException {

                    }
                });
        httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }
}
