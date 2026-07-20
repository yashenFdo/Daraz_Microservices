package lk.daraz.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lk.daraz.userservice.entity.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service – creates, validates and parses JSON Web Tokens.
 *
 * Claims injected into the token:
 *   sub  → customer ID (String)
 *   email → customer email
 *   role  → customer role (e.g. ROLE_CUSTOMER)
 *   iat  → issued at
 *   exp  → expiration
 */
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // ── Token generation ──────────────────────────────────────────────────

    public String generateToken(Customer customer) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", customer.getEmail());
        extraClaims.put("role", customer.getRole().name());
        return buildToken(extraClaims, customer, jwtExpiration);
    }

    public String generateRefreshToken(Customer customer) {
        return buildToken(new HashMap<>(), customer, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, Customer customer, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(String.valueOf(customer.getId()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    // ── Token validation ──────────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ── Claims extraction ─────────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
