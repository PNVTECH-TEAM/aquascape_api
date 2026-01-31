package pnvteck.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;
import pnvteck.gateway.security.dto.TokenStatusResponse;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final List<String> protectedPrefixes = List.of(
            "/api/aquariums",
            "/api/assets",
            "/api/catalog",
            "/api/users",
            "/storage"
    );

    @Value("${jwt.secret}")
    private String secret;

    private final WebClient webClient;

    public JwtAuthFilter(WebClient.Builder webClientBuilder,
                         @Value("${auth.service-url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!requiresAuth(path)) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }
        String token = authHeader.substring("Bearer ".length());
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                return unauthorized(exchange);
            }
            return isTokenRevoked(token)
                    .flatMap(revoked -> {
                        if (revoked) {
                            return unauthorized(exchange);
                        }
                        ServerWebExchange mutated = exchange.mutate()
                                .request(builder -> builder.header("X-User-Name", username))
                                .build();
                        return chain.filter(mutated);
                    });
        } catch (Exception ex) {
            return unauthorized(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean requiresAuth(String path) {
        for (String prefix : protectedPrefixes) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Boolean> isTokenRevoked(String token) {
        return webClient.post()
                .uri("/api/auth/token-status")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(TokenStatusResponse.class)
                .map(TokenStatusResponse::isRevoked)
                .onErrorReturn(true);
    }
}
