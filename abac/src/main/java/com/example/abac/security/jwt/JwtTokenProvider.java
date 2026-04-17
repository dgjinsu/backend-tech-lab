package com.example.abac.security.jwt;

import com.example.abac.domain.user.Role;
import com.example.abac.security.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// [학습 포인트] ABAC 속성이 '무상태'를 가능하게 하는 법.
// userId/role/departmentId를 claim에 담아 서명하면, 서버는 DB 왕복 없이 토큰만으로 Principal 복원.
// 이 파일이 무엇을 claim에 넣는지가 곧 '무엇이 요청 컨텍스트에 살아 있는지'를 결정한다.
@Component
public class JwtTokenProvider {

    // claim 이름을 상수로 뽑아 두면 generate/parse 양쪽이 문자열 오타로 어긋날 일이 없다.
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_DEPARTMENT_ID = "departmentId";

    private final SecretKey key;
    private final long expireMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expire-ms}") long expireMs
    ) {
        // HMAC-SHA 키. 비밀값이 짧으면 라이브러리가 예외를 던지므로 application.properties에서 충분한 길이로.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMs = expireMs;
    }

    public String generate(CustomUserPrincipal principal) {
        Date now = new Date();
        return Jwts.builder()
                // subject는 관례적으로 식별 문자열. 우리는 username을 사용.
                .subject(principal.getUsername())
                // [ABAC 속성 탑재] 이 세 claim이 이후 SecurityContext의 Principal 복원 재료.
                .claim(CLAIM_USER_ID, principal.getUserId())
                .claim(CLAIM_ROLE, principal.getRole().name())
                .claim(CLAIM_DEPARTMENT_ID, principal.getDepartmentId())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMs))
                // 서명으로 위·변조 차단. 서버만 key를 알기 때문에 클라이언트가 role을 바꿔쓸 수 없다.
                .signWith(key)
                .compact();
    }

    public CustomUserPrincipal parse(String token) {
        // 서명 검증 + 만료 체크 + 파싱을 한 번에. 이상 있으면 JwtException 계열이 튀어나옴
        // → JwtAuthenticationFilter가 받아서 SecurityContext를 비운다.
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        Claims c = jws.getPayload();
        // JSON 숫자는 Integer/Long 어느 쪽으로든 올 수 있어 Number로 받고 longValue()로 맞춘다.
        Long userId = c.get(CLAIM_USER_ID, Number.class).longValue();
        Role role = Role.valueOf(c.get(CLAIM_ROLE, String.class));
        Long departmentId = c.get(CLAIM_DEPARTMENT_ID, Number.class).longValue();
        // password는 빈 문자열. 토큰 경로에선 비밀번호가 필요 없고, 들고 다니는 것도 위험.
        return new CustomUserPrincipal(userId, c.getSubject(), "", role, departmentId);
    }
}
