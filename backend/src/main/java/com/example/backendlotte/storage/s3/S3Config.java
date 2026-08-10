package com.example.backendlotte.storage.s3;

import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(
    prefix = "app.storage.s3",
    name = "enabled",
    havingValue = "true"
)
// @EnableConfigurationProperties(S3StorageProperties.class)
public class S3Config {

    @Bean
    public S3Client s3Client(
            S3StorageProperties properties
    ) {
        return S3Client.builder()
            .region(Region.of(properties.region()))
            .credentialsProvider(
                DefaultCredentialsProvider.create()
            )
            .httpClientBuilder(
                ApacheHttpClient.builder()
                    .connectionTimeout(
                        Duration.ofSeconds(5)
                    )
                    .socketTimeout(
                        Duration.ofSeconds(30)
                    )
            )
            .build();
    }
}