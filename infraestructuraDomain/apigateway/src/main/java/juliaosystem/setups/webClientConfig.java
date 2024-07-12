package juliaosystem.setups;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class webClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalanceWebClientBuilder(){
        return WebClient.builder();
    }
}
