package com.example.securityjwt.global.security.property;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TokenProperties {

    private final long accessTokenExpTime;
    private final Key key;

    public TokenProperties(@Value("${jwt.expiration_time}") long accessTokenExpTime, @Value("${jwt.secret}") String secretKey) {
        this.accessTokenExpTime = accessTokenExpTime;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}
