package nasaltsev.appliedChallenge.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


@Component
public class JwtGenerator {
    private final JwtUtil jwtUtil = new JwtUtil();

    @PostConstruct
    public void generateJwt() {
        String token = jwtUtil.generateToken();
        System.out.println("Generated JWT: " + token);
    }
}
