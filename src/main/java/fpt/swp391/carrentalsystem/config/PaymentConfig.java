package fpt.swp391.carrentalsystem.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Getter
public class PaymentConfig {

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Value("${payos.return-url:http://localhost:8080/payment/payos-return}")
    private String returnUrl;

    @Value("${payos.cancel-url:http://localhost:8080/payment/payos-cancel}")
    private String cancelUrl;

    @Value("${payos.webhook-url:http://localhost:8080/payment/payos-webhook}")
    private String webhookUrl;

    @Value("${payos.api-url:https://api-merchant.payos.vn}")
    private String apiUrl;

    @Bean
    public WebClient payosWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("x-client-id", clientId)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

