package com.example.booksstory.security;

import com.example.booksstory.entity.RefreshToken;
import com.example.booksstory.entity.User;
import com.example.booksstory.payload.AuthTokenPayload;
import com.example.booksstory.payload.UserPayload;
import com.example.booksstory.repository.RefreshTokenRepository;
import com.example.booksstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenUtils refreshTokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthTokenPayload createToken(UserPayload payload) {
        User user = userRepository.findByUsername(payload.getUsername());
        if (user == null) {
            throw new RuntimeException("Bu username li user mavjud emas");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword()));
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        System.out.println(token.toString());
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Iltimos qayta urining biror nima hato ketdi");
        }

        RefreshToken refreshToken = refreshTokenUtils.createRefreshToken(user);
        AuthTokenPayload authTokenPayload = new AuthTokenPayload();
        authTokenPayload.setAccess_token(token);
        authTokenPayload.setRefresh_token(refreshToken.getRefreshToken());
        authTokenPayload.setUsername(user.getUsername());
        authTokenPayload.setSucces(true);

        return (authTokenPayload);
    }

    public AuthTokenPayload createTokenByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Bu username li user mavjud emas");
        }
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        System.out.println(token.toString());
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Iltimos qayta urining biror nima hato ketdi");
        }

        RefreshToken refreshToken = refreshTokenUtils.createRefreshToken(user);
        AuthTokenPayload authTokenPayload = new AuthTokenPayload();
        authTokenPayload.setAccess_token(token);
        authTokenPayload.setRefresh_token(refreshToken.getRefreshToken());
        authTokenPayload.setUsername(user.getUsername());
        authTokenPayload.setSucces(true);

        return (authTokenPayload);
    }

    public AuthTokenPayload refreshToken(AuthTokenPayload authTokenPayload) {

        RefreshToken refreshToken=refreshTokenRepository.findFirstByRefreshTokenOrderByCreatedAtDesc(authTokenPayload.getRefresh_token()).orElseThrow(()->new RuntimeException("refreshToken not found"));
        if (!refreshTokenUtils.validateRefreshToken(refreshToken)){
            throw new RuntimeException("refresh_token is expired");
        }
        return createTokenByUsername(refreshToken.getUser().getUsername());
    }

}
