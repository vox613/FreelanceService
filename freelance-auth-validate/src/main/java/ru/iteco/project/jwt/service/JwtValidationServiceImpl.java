package ru.iteco.project.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.jwt.domain.CustomClaims;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 * Сервис валидации jwt токенов
 */
@Component
public class JwtValidationServiceImpl implements JwtValidationService {

    private final String EMAIL_REGEXP = "^([A-Za-z0-9._-]{1,40})@([A-Za-z0-9._-]{1,20}\\.)([A-Za-z0-9._-]{1,15})$";

    @Override
    public CustomClaims getTokenClaims(String token, String secret) {
        CustomClaims customClaims;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            customClaims = new CustomClaims(claims);

        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token", e);
        }
        return customClaims;
    }


    @Override
    public CustomClaims validateToken(CustomClaims tokenClaims, String secret, String applicationName) {
        if (ObjectUtils.isEmpty(tokenClaims.getId()) ||
                ObjectUtils.isEmpty(tokenClaims.getUserId()) ||
                ObjectUtils.isEmpty(tokenClaims.getSubject()) ||
                ObjectUtils.isEmpty(tokenClaims.getAudience()) ||
                ObjectUtils.isEmpty(tokenClaims.getIssuer()) ||
                ObjectUtils.isEmpty(tokenClaims.getRole())) {
            throw new BadCredentialsException("Invalid token payload");
        }

        Date expiration = tokenClaims.getExpiration();
        if (ObjectUtils.isEmpty(expiration) || new Date(System.currentTimeMillis()).after(expiration)) {
            throw new BadCredentialsException("Expired token or invalid exp value");
        }

        if (!applicationName.equals(tokenClaims.getAudience())) {
            throw new BadCredentialsException("Invalid token audience");
        }

        String email = tokenClaims.getEmail();
        if (ObjectUtils.isEmpty(email) || !email.matches(EMAIL_REGEXP)) {
            throw new BadCredentialsException("Invalid token email");
        }
        return tokenClaims;
    }

}
