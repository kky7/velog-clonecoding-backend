package com.velog.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velog.backend.dto.request.KakaoUserInfoDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.MemberInfoResDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.RefreshToken;
import com.velog.backend.exception.SuccessMsg;
import com.velog.backend.jwt.util.JwtUtil;
import com.velog.backend.jwt.util.TokenProperties;
import com.velog.backend.repository.MemberRepository;
import com.velog.backend.repository.RefreshTokenRepository;
import com.velog.backend.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${rest.api.key}")
    private String clientId;

    @Value("${redirect.uri}")
    private String redirectUri;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final ServiceUtil serviceUtil;

    @Transactional
    public ResponseEntity<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("in");
        System.out.println(code);
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String kakaoAccessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);

        // 3. 필요시 카카오ID로 회원가입 처리
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. 강제 로그인 처리
//        forceLogin(kakaoUser);

        String ourAccessToken = jwtUtil.createToken(kakaoUser.getNickname(), TokenProperties.AUTH_HEADER);
        String ourRefreshToken = jwtUtil.createToken(kakaoUser.getNickname(),TokenProperties.REFRESH_HEADER);

        RefreshToken refreshTokenFromDB = jwtUtil.getRefreshTokenFromDB(kakaoUser);

        if(refreshTokenFromDB == null){
            RefreshToken saveRefreshToken = new RefreshToken(kakaoUser, ourRefreshToken);
            refreshTokenRepository.save(saveRefreshToken);
        } else{
            refreshTokenFromDB.updateValue(ourRefreshToken);
        }

        response.addHeader(TokenProperties.AUTH_HEADER, TokenProperties.TOKEN_TYPE + ourAccessToken);
        response.addHeader(TokenProperties.REFRESH_HEADER, TokenProperties.TOKEN_TYPE + ourRefreshToken);

        MemberInfoResDto memberInfoResDto = new MemberInfoResDto(kakaoUser);

        GlobalResDto<MemberInfoResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.LOGIN_SUCCESS,memberInfoResDto);

        return new ResponseEntity<>(globalResDto,HttpStatus.OK);
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        System.out.println("getAccessToken in");
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에서 카카오 유저 정보 꺼내기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String profileUrl = jsonNode.get("properties")
                .get("profile_image").asText();

        return new KakaoUserInfoDto(id, nickname, profileUrl);
    }

    private Member registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        System.out.println("registerKakaoUserIfNeeded in");
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId).orElse(null);

        // 신규 회원 가입
        if (kakaoUser == null) {
            // 닉네임 중복 체크
            String kakaoNickname = kakaoUserInfo.getNickname();
            Member findMemberByNickname = memberRepository.findByNickname(kakaoNickname).orElse(null);

            while (findMemberByNickname == null){
                kakaoNickname = kakaoNickname + serviceUtil.CreateRandomString();
                findMemberByNickname = memberRepository.findByNickname(kakaoNickname).orElse(null);
            }

            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = new Member(kakaoNickname, encodedPassword, kakaoUserInfo.getProfileUrl(), kakaoNickname + ".log", kakaoId);
            memberRepository.save(kakaoUser);

        }

        return kakaoUser;
    }

    private void forceLogin(Member kakaoUser) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setMember(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
