package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class WebClientConfigTest {
    
    private WebClientConfig webClientConfig = new WebClientConfig();

    @Test
    void webClientBuilder_ShouldNotBeNull() {
        WebClient.Builder builder = webClientConfig.webClientBuilder();
        assert builder != null;
    }

    @Test
    void yahooFinanceWebClient_ShouldNotBeNull() {
        WebClient.Builder builder = webClientConfig.webClientBuilder();
        WebClient client = webClientConfig.yahooFinanceWebClient(builder);
        assert client != null;
    }

    @Test
    void currencyWebClient_ShouldNotBeNull() {
        WebClient.Builder builder = webClientConfig.webClientBuilder();
        WebClient client = webClientConfig.currencyWebClient(builder);
        assert client != null;
    }
}
