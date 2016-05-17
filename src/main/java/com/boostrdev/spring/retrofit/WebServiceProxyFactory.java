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
     * <p>
     * Create an implementation of the API endpoints defined by the {@code webServiceProxy} interface.
     *
     * @param <T>                  The web proxy interface type
     * @param webServiceProxyClazz The web proxy interface class
     * @param converter            The JSON converterFactory
     * @param baseUrl              The base location of the web service
     * @param proxyHost            The host name of the HTTP proxy
     * @param proxyPort            The port number of the HTTP proxy
     * @param proxy                Whether a HTTP proxy should be used to send HTTP requests and responses
     * @param logging              Whether the HTTP logging should be turned on
     * @return Returns a Retrofit instance of the web service proxy using
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
