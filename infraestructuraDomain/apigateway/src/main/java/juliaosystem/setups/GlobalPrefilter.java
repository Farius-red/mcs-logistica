
package juliaosystem.setups;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 * @author daniel
 */
@Slf4j
@Component
public class GlobalPrefilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Global filtred executed");
        try {
            return chain.filter(exchange);
        }catch (Exception e){
            System.out.println(e);
        }
       return null;
    }
    
}
