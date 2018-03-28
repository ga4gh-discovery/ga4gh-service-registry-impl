package com.dnastack.dos.registry.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class RestTemplateConfig {

    @Value("${service.objects.timeout.connection}")
    private int objectsServiceConnectionTimeout;
    @Value("${service.objects.timeout.read}")
    private int objectsServiceReadTimeout;
    @Value("${service.objects.timeout.connection-request}")
    private int objectsServiceConnectionRequestTimeout;
    //@Value("${service.objects.connection-pool.max-total}")
    private int objectsServiceMaxTotal;
    //@Value("${service.objects.connection-pool.max-per-route}")
    private int objectsServiceMaxPerRoute;
    //@Value("${service.objects.connection-pool.connection-to-live-seconds}")
    private long objectsServiceConnlToLiveTimeout;

    // SSLContext which enables trust in CA signed certificates.
    //@Autowired
    private SSLContext bnsCertificatesTrustingSslContext;

    @Bean
    public RestTemplate objectsServiceRestTemplate(RestTemplateBuilder builder) throws Exception {

        HttpClient httpClient = HttpClientBuilder.create()
                //.setSSLContext(bnsCertificatesTrustingSslContext)
                //.setMaxConnTotal(objectsServiceMaxTotal)
                //.setMaxConnPerRoute(objectsServiceMaxPerRoute)
                //.setConnectionTimeToLive(objectsServiceConnlToLiveTimeout, TimeUnit.SECONDS)
                .build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(objectsServiceConnectionRequestTimeout);
        factory.setConnectTimeout(objectsServiceConnectionTimeout);
        factory.setReadTimeout(objectsServiceReadTimeout);
        factory.setHttpClient(httpClient);

        return builder.requestFactory(factory).build();
    }

}
