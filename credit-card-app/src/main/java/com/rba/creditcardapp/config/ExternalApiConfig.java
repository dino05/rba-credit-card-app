package com.rba.creditcardapp.config;

import com.rba.creditcardapp.api.NewCardRequestApi;
import com.rba.creditcardapp.model.NewCardRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Configuration
public class ExternalApiConfig {

    @Bean
    @Lazy
    public NewCardRequestApi newCardRequestApi() {
        return new NewCardRequestApi() {
            @Override
            public ResponseEntity<Void> apiV1CardRequestPost(NewCardRequest newCardRequest) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
        };
    }
}
