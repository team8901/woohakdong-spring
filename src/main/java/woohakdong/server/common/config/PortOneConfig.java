package woohakdong.server.common.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class PortOneConfig {

    @Value("${portone.rest.api.key}")
    private String restApiKey;
    @Value("${portone.rest.api.secret}")
    private String restApiSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(restApiKey, restApiSecret);
    }
}
