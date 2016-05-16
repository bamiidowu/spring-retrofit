package com.boostrdev.spring.retrofit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RestAdapter;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;
import retrofit.converter.Converter;

import java.io.IOException;
import java.net.*;

public final class WebServiceProxyFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceProxyFactory.class);

    private WebServiceProxyFactory() {

    }

    /**
     * Create an implementation of the API endpoints defined by the {@code webServiceProxy} interface.
     *
     * @param webServiceProxyClazz
     * @param converter
     * @param baseUrl
     * @param proxyHost
     * @param proxyPort
     * @param proxy
     * @param logging
     * @return
     */
    public static <T> T create(Class<T> webServiceProxyClazz, Converter converter, String baseUrl, String proxyHost, int proxyPort, boolean proxy, boolean logging) {
        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder();
        if (proxy) {
            final SocketAddress proxyAddress = new InetSocketAddress(proxyHost, proxyPort);

            UrlConnectionClient httpProxyUrlConnectionClient = new UrlConnectionClient() {

                @Override
                protected HttpURLConnection openConnection(Request request) throws IOException {
                    Proxy httpProxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
                    return (HttpURLConnection) new URL(request.getUrl()).openConnection(httpProxy);
                }
            };
            restAdapterBuilder.setClient(httpProxyUrlConnectionClient);
            LOG.debug("Configured HTTP proxy {}:{}", proxyHost, proxyPort);
        }
        if (logging) {
            restAdapterBuilder.setLogLevel(RestAdapter.LogLevel.FULL);
            LOG.debug("Configured HTTP logging");
        }

        final RestAdapter restAdapter = restAdapterBuilder.setEndpoint(baseUrl).setConverter(converter).build();

        return restAdapter.create(webServiceProxyClazz);
    }
}
