package com.velog.backend.jwt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velog.backend.Repository.RefreshTokenRepository;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.RefreshToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    @Value("${jwt.secret.key}")
    private String secretKey;
    Key key;
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }


    // 토큰 생성
    public String createToken(String email, String type){
        Date date = new Date();
        int time = type.equals(TokenProperties.AUTH_HEADER)? TokenProperties.ACCESS_TOKEN_VALID_TIME : TokenProperties.REFRESH_TOKEN_VALID_TIME;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(new Date(System.currentTimeMillis() + time))
                .signWith(key,signatureAlgorithm)
                .compact();
    }

    public String validateToken( String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return TokenProperties.VALID;
        } catch (ExpiredJwtException e) {
            return TokenProperties.EXPIRED;
        } catch ( JwtException | IllegalArgumentException | NullPointerException e) {
            return TokenProperties.INVALID;
        }
    }

    // 예외 응답
    public void exceptionResponse(HttpServletResponse response, HttpStatus httpStatus, String errorMsg) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus.value());
        GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, errorMsg);
        String httpResponse = objectMapper.writeValueAsString(globalResDto);
        response.getWriter().write(httpResponse);
    }


    // DB에 있는 refreshToken가져오기
    public RefreshToken getRefreshTokenFromDB(Member member){
        Optional<RefreshToken> refreshTokenFromDB = refreshTokenRepository.findByMember(member);
        return refreshTokenFromDB.orElse(null);
    }

    // token에서 payload 권한 값 중 subject값 가져오기 (토큰으로부터 이메일 가져오기)
    public String getEmailFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }


}
