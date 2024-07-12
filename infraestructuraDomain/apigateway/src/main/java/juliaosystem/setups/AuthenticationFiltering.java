
package juliaosystem.setups;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;

/**
 *
 * @author daniel
 */
@Builder
@Data
@AllArgsConstructor

public class AuthenticationFiltering extends AbstractGatewayFilterFactory<AuthenticationFiltering.Config> {

    private final WebClient.Builder webClientBuilder;

    private final Key jwtSecret;
    public AuthenticationFiltering(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
        this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Inicialización del jwtSecret
    }


    @Override
        public GatewayFilter apply(Config config) {

            return new OrderedGatewayFilter((exchange, chain) -> {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing  Authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String[] parts = authHeader.split(" ");
                if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad Authorization structure");
                }

                String token = parts[1];

                try {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(jwtSecret)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    // Ahora puedes obtener los detalles del token desde 'claims'
                    // Por ejemplo, para obtener el id de negocio:
                    String idBussines = (String) claims.get("idBussines");

                    // ... continua con tu lógica de filtrado aquí

                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
                }


                return null;
            },1);
        }

    public  static  class Config{

    }
}
