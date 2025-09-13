package project.dev.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    // default 1 hour
    @Value("${app.jwt.expiration-millis:3600000}")
    private long expirationMillis;

    // optional clock skew in seconds (default 60s)
    @Value("${app.jwt.clock-skew-seconds:60}")
    private long clockSkewSeconds;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        this.signingKey = buildSigningKey(secret);
    }

    // ----- Public API -----

    public String generateToken(String subject) {
        return generateToken(Map.of(), subject, Duration.ofMillis(expirationMillis));
    }

    public String generateToken(Map<String, Object> extraClaims, String subject) {
        return generateToken(extraClaims, subject, Duration.ofMillis(expirationMillis));
    }

    public String generateToken(Map<String, Object> extraClaims, String subject, Duration validity) {
        Instant now = Instant.now();
        Instant expiry = now.plus(validity);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // ----- Internal helpers -----

    private Claims extractAllClaims(String token) {
        // Parser with verification
        return Jwts.parser()
                .verifyWith(signingKey)
                .clockSkewSeconds(clockSkewSeconds)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static SecretKey buildSigningKey(String secret) {
        byte[] keyBytes = deriveKeyBytes(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] deriveKeyBytes(String secret) {
        byte[] bytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        // Ensure minimum length (32 bytes for HS256). If shorter, hash with SHA-256 to derive 32 bytes.
        if (bytes.length < 32) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                return digest.digest(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        }
        return bytes;
    }
}
