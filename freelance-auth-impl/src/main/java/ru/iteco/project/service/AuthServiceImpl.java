package ru.iteco.project.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.exception.UnavailableOperationException;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.resource.dto.AuthUserDto;
import ru.iteco.project.resource.dto.TokenDto;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@PropertySource(value = "classpath:errors.properties")
public class AuthServiceImpl implements AuthService {

    /*** Секрет для шифрования подписи токена*/
    @Value("${authentication.jwt.secret}")
    private String secret;

    /*** Время жизни токена*/
    @Value("${authentication.jwt.accessToken.lifetime}")
    private long tokenLifetime;

    /*** Наименование системы сгенерировавшей токен*/
    @Value("${spring.application.name}")
    private String systemName;

    @Value("${errors.auth.data.invalid}")
    private String invalidLoginOrPassMessage;

    @Value("${errors.auth.user.status.invalid}")
    private String unavailableOperationMessage;

    /*** Объект энкодера для шифрования и сверки паролей*/
    private final PasswordEncoder passwordEncoder;

    /*** Объект доступа к репозиторию по работе с сущностью User*/
    private final UserRepository userRepository;


    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    @Override
    public TokenDto generateToken(AuthUserDto authUserDto) {
        TokenDto tokenDto = new TokenDto();
        String accessToken = generateAccessToken(getUser(authUserDto), authUserDto);
        tokenDto.setAccessToken(accessToken);
        return tokenDto;
    }

    private String generateAccessToken(User user, AuthUserDto authUserDto) {
        Map<String, Object> payload = new HashMap<>();
        Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        payload.put("role", user.getRole().name());
        payload.put("email", user.getEmail());
        return Jwts.builder()
                .setClaims(payload)
                .setSubject(user.getUsername())
                .setAudience(authUserDto.getAudience())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenLifetime))
                .setId(UUID.randomUUID().toString())
                .setIssuer(systemName)
                .signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }


    private User getUser(AuthUserDto authUserDto) {
        User user = userRepository.findByUsername(authUserDto.getUsername())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(invalidLoginOrPassMessage));

        if (!passwordEncoder.matches(authUserDto.getPassword(), user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException(invalidLoginOrPassMessage);
        }

        UserStatus status = user.getStatus();
        if ((UserStatus.BLOCKED == status) || (UserStatus.DELETED == status)) {
            throw new UnavailableOperationException(unavailableOperationMessage);
        }
        return user;
    }

}
