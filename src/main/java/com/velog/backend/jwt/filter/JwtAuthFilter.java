package com.velog.backend.jwt.filter;

import com.velog.backend.constant.exception.ErrorMsg;
import com.velog.backend.jwt.util.JwtUtil;
import com.velog.backend.jwt.util.TokenProperties;
import com.velog.backend.security.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Header에서 access token 받기
        String jwtHeader = request.getHeader(TokenProperties.AUTH_HEADER);
        String requestUri = request.getRequestURI();

        if(requestUri.contains("/auth/")){
            if(jwtHeader == null){
                // auth가 포함된 uri는 header 값이 있어야 한다.
                jwtUtil.exceptionResponse(response,HttpStatus.UNAUTHORIZED, ErrorMsg.INVALID_LOGIN);
                return;
            }

            if(!jwtHeader.startsWith(TokenProperties.TOKEN_TYPE)){
                jwtUtil.exceptionResponse(response, HttpStatus.UNAUTHORIZED,ErrorMsg.INVALID_ACCESS_TOKEN);
                return;
            }

            String accessToken = jwtHeader.replace(TokenProperties.TOKEN_TYPE,"");

            String validate = jwtUtil.validateToken(accessToken);

            switch (validate) {
                case TokenProperties.EXPIRED:
                    jwtUtil.exceptionResponse(response, HttpStatus.FORBIDDEN, ErrorMsg.EXPIRED_ACCESS_TOKEN);
                    return;
                case TokenProperties.INVALID:
                    jwtUtil.exceptionResponse(response, HttpStatus.FORBIDDEN,ErrorMsg.INVALID_ACCESS_TOKEN);
                    return;
                case TokenProperties.VALID:
                    // JWT 로부터 권한 값 가져오기
                    String nickname = jwtUtil.getNicknameFromToken(accessToken);

                    if (nickname == null) {
                        jwtUtil.exceptionResponse(response, HttpStatus.FORBIDDEN, ErrorMsg.INVALID_ACCESS_TOKEN);
                        return;
                    }

                    // JWT 검증 성공 -> 권한 값으로부터 유저를 가져온다
                    UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);

                    if (userDetails == null) {
                        jwtUtil.exceptionResponse(response, HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_FOUND);
                        return;
                    }

                    // 모든 예외처리 통과 -> 인증객체 생성, 권한부여
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    chain.doFilter(request, response);
                    break;
            }

        }else{
            // FilterChain chain 해당 필터가 실행 후 다른 필터도 실행할 수 있도록 연결실켜주는 메서드
            // auth가 포함되지 않은 uri는 인가를 거치지 않고 다음 필터로 넘어간다.
            chain.doFilter(request,response);
        }
    }

}
