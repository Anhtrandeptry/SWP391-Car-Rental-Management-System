package fpt.swp391.carrentalsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;

@Configuration
public class PaymentConfig {

    @Bean
    public String getVnPayHashSecret() {
        return "${vnpay.hash-secret}";
    }

    @Bean
    public String getVnPayTmnCode() {
        return "${vnpay.tmn-code}";
    }

    @Bean
    public String getVnPayPaymentUrl() {
        return "${vnpay.payment-url}";
    }

    @Bean
    public String getVnPayReturnUrl() {
        return "${vnpay.return-url}";
    }
}

