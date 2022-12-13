package com.velog.backend.service;

import com.velog.backend.repository.MemberRepository;
import com.velog.backend.repository.RefreshTokenRepository;
import com.velog.backend.dto.request.EmailReqDto;
import com.velog.backend.dto.request.LoginReqDto;
import com.velog.backend.dto.request.SignupReqDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.ProfileInfoDto;
import com.velog.backend.dto.response.MemberInfoResDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.RefreshToken;
import com.velog.backend.constant.response.ErrorMsg;
import com.velog.backend.constant.response.SuccessMsg;
import com.velog.backend.jwt.util.JwtUtil;
import com.velog.backend.constant.TokenProperties;
import com.velog.backend.security.user.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 이메일 중복체크
    public ResponseEntity<?> emailCheck(EmailReqDto emailReqDto){
        String email = emailReqDto.getEmail();

        if(! emailFormatChek(email)) {
            return ServiceUtil.dataNullResponse(HttpStatus.BAD_REQUEST, ErrorMsg.INVALID_EMAIL);
        }
        else if(isEmailDuplicate(email)) {
            return ServiceUtil.dataNullResponse(HttpStatus.BAD_REQUEST, ErrorMsg.DUPLICATE_EMAIL);
        }
        else{
            GlobalResDto<EmailReqDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.SIGNUP_OK, emailReqDto);
            return new ResponseEntity<>(globalResDto, HttpStatus.OK);
        }
    }

    // 회원가입
    @Transactional
    public ResponseEntity<?> signup(SignupReqDto signupReqDto){
        String password = signupReqDto.getPassword();
        String passwordConfirm = signupReqDto.getPasswordConfirm();
        String nickname = signupReqDto.getNickname();

        if(isNicknameDuplicate(nickname)){
            return ServiceUtil.dataNullResponse(HttpStatus.BAD_REQUEST, ErrorMsg.DUPLICATE_NICKNAME);
        } else if (!isSamePassword(password, passwordConfirm)){
            return ServiceUtil.dataNullResponse(HttpStatus.BAD_REQUEST, ErrorMsg.PASSWORD_NOT_MATCHED);
        } else {
            signupReqDto.setPassword(passwordEncoder.encode(password));

            String velogTitle = nickname + ".log";
            Member member = new Member(signupReqDto, velogTitle);

            memberRepository.save(member);
            MemberInfoResDto memberInfoResDto = new MemberInfoResDto(member);
            GlobalResDto<MemberInfoResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.SIGNUP_SUCCESS, memberInfoResDto);
            return new ResponseEntity<>(globalResDto, HttpStatus.OK);
        }
    }

     // 일반 로그인
    @Transactional
    public ResponseEntity<?> login(LoginReqDto loginReqDto, HttpServletResponse response){
        String email = loginReqDto.getEmail();
        Member member = findMemberByEmail(email);

        if(member == null){
            return ServiceUtil.dataNullResponse(HttpStatus.BAD_REQUEST, ErrorMsg.EMAIL_NOT_FOUND);
        }


        if(!member.validatePassword(passwordEncoder, loginReqDto.getPassword())){
            return ServiceUtil.dataNullResponse(HttpStatus.BAD_REQUEST, ErrorMsg.INVALID_PASSWORD);
        }

        String nickname = member.getNickname();
        // 토큰 발급
        String accessToken = jwtUtil.createToken(nickname, TokenProperties.AUTH_HEADER);
        String refreshToken = jwtUtil.createToken(nickname, TokenProperties.REFRESH_HEADER);

        RefreshToken refreshTokenInDB = jwtUtil.getRefreshTokenFromDB(member);
        // 로그인 경력이 있는 사용자 -> DB에 Refresh Token 있음 -> 새로 로그인 했으면 새로 발급받는 토큰으로 변경
        // 로그인이 처음인 사용자 -> DB에 Refresh Token 없음 -> 발급받은 Refresh 토큰 저장
        if(refreshTokenInDB == null){
            RefreshToken saveRefreshToken = new RefreshToken(member, refreshToken);
            refreshTokenRepository.save(saveRefreshToken);
        } else{
            refreshTokenInDB.updateValue(refreshToken);
        }
        
        // 응답 헤더에 토큰 담아서 보내기
        TokenToHeaders(response, accessToken, refreshToken);
        MemberInfoResDto memberInfoResDto = new MemberInfoResDto(member);
        GlobalResDto<MemberInfoResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.LOGIN_SUCCESS, memberInfoResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> logout(HttpServletRequest request, UserDetailsImpl userDetails){

        Member member = userDetails.getMember();

        String refreshHeader = request.getHeader(TokenProperties.REFRESH_HEADER);

        if(refreshHeader == null){
            return ServiceUtil.dataNullResponse(HttpStatus.UNAUTHORIZED, ErrorMsg.NEED_REFRESH_TOKEN);
        }

        if(!refreshHeader.startsWith(TokenProperties.TOKEN_TYPE)){
            return ServiceUtil.dataNullResponse(HttpStatus.UNAUTHORIZED, ErrorMsg.INVALID_REFRESH_TOKEN);
        }

        String refreshToken = refreshHeader.replace(TokenProperties.TOKEN_TYPE,"");
        // 토큰 검증
        String refreshTokenValidate = jwtUtil.validateToken(refreshToken);
        switch (refreshTokenValidate) {
            case TokenProperties.VALID:
            case TokenProperties.EXPIRED:
                RefreshToken refreshTokenFromDB = jwtUtil.getRefreshTokenFromDB(member);
                if (refreshTokenFromDB != null && refreshToken.equals(refreshTokenFromDB.getTokenValue())) {
                    refreshTokenRepository.delete(refreshTokenFromDB);
                    return ServiceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.LOGOUT_SUCCESS);
                } else {
                    return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.REFRESH_TOKEN_NOT_MATCHED);
                }
            default:
                return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.INVALID_REFRESH_TOKEN);
        }
    }

    @Transactional
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshHeader = request.getHeader(TokenProperties.REFRESH_HEADER);

        if(refreshHeader == null){
            return ServiceUtil.dataNullResponse(HttpStatus.UNAUTHORIZED, ErrorMsg.NEED_REFRESH_TOKEN);
        }

        if(!refreshHeader.startsWith(TokenProperties.TOKEN_TYPE)){
            return ServiceUtil.dataNullResponse(HttpStatus.UNAUTHORIZED, ErrorMsg.INVALID_REFRESH_TOKEN);
        }

        String refreshToken = refreshHeader.replace(TokenProperties.TOKEN_TYPE, "");

        // Refresh 토큰 검증
        String refreshTokenValidate = jwtUtil.validateToken(refreshToken);
        switch (refreshTokenValidate) {
            case TokenProperties.EXPIRED:
                return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.EXPIRED_REFRESH_TOKEN);
            case TokenProperties.VALID:
                String nickname = jwtUtil.getNicknameFromToken(refreshToken);
                Member member = findMemberByEmail(nickname);

                if (member == null) {
                    return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_FOUND);
                } else {
                    RefreshToken refreshTokenFromDB = jwtUtil.getRefreshTokenFromDB(member);
                    if (refreshTokenFromDB != null && refreshToken.equals(refreshTokenFromDB.getTokenValue())) {
                        String newAccessToken = jwtUtil.createToken(member.getNickname(), TokenProperties.AUTH_HEADER);
                        response.addHeader(TokenProperties.AUTH_HEADER, TokenProperties.TOKEN_TYPE + newAccessToken);
                        return ServiceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.REISSUE_ACCESS_TOKEN);
                    } else {
                        return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.REFRESH_TOKEN_NOT_MATCHED);
                    }
                }
            default:
                return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.INVALID_REFRESH_TOKEN);
        }
    }

    @Transactional
    public ResponseEntity<?> getProfileInfo(UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        GlobalResDto<ProfileInfoDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.PROFILE_SUCCESS, new ProfileInfoDto(member));
        return new ResponseEntity<>(globalResDto,HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member findMemberByNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        return optionalMember.orElse(null);
    }

    private boolean isEmailDuplicate(String email){
        Member member = findMemberByEmail(email);
        return member != null;
    }

    private boolean emailFormatChek(String email){
        String email_regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        return email.matches(email_regex) && email.endsWith(".com");
    }

    private boolean isNicknameDuplicate(String nickname){
        Member member = findMemberByNickname(nickname);
        return member != null;
    }

    private  boolean isSamePassword(String password, String ConfirmPassword){
        return password.equals(ConfirmPassword);
    }

    private void TokenToHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(TokenProperties.AUTH_HEADER, TokenProperties.TOKEN_TYPE + accessToken);
        response.addHeader(TokenProperties.REFRESH_HEADER, TokenProperties.TOKEN_TYPE + refreshToken);
    }
}
