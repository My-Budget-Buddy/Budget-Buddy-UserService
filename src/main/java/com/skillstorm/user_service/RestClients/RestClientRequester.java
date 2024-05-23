package com.skillstorm.user_service.RestClients;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

public class RestClientRequester {

    private RestClient restClient = RestClient.builder().build();

    public ResponseEntity<Void> sendRequest(String uri) {
        
        return restClient
                .delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();
    }
    
}
