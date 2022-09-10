package com.velog.backend.service;

import com.velog.backend.Repository.MemberRepository;
import com.velog.backend.dto.request.EmailReqDto;
import com.velog.backend.dto.request.SignupReqDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.MemberInfoResDto;
import com.velog.backend.entity.Member;
import com.velog.backend.exception.ErrorMsg;
import com.velog.backend.exception.SuccessMsg;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {

    MemberRepository memberRepository;

    // 이메일 중복체크
    @Transactional
    public ResponseEntity<?> emailCheck(EmailReqDto emailReqDto){
        String email = emailReqDto.getEmail();
        HttpStatus httpStatus;

        if(! emailFormatChek(email)) {
            httpStatus = HttpStatus.BAD_REQUEST;
            GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, ErrorMsg.INVALID_EMAIL, null);
            return new ResponseEntity<>(globalResDto, httpStatus);
        }
        else if(isEmailInDB(email)) {
            httpStatus = HttpStatus.BAD_REQUEST;
            GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, ErrorMsg.DUPLICATE_EMAIL,null);
            return new ResponseEntity<>(globalResDto, httpStatus);
        }
        else{
            httpStatus = HttpStatus.OK;
            GlobalResDto<EmailReqDto> globalResDto = new GlobalResDto<>(httpStatus, SuccessMsg.SIGNUP_OK, emailReqDto);
            return new ResponseEntity<>(globalResDto,httpStatus);
        }
    }

    // 회원가입
    @Transactional
    public ResponseEntity<?> signup(SignupReqDto signupReqDto){
        String password = signupReqDto.getPassword();
        String passwordConfirm = signupReqDto.getPasswordConfirm();
        String nickname = signupReqDto.getNickname();
        HttpStatus httpStatus;

        if(isNicknameInDB(nickname)){
            httpStatus = HttpStatus.BAD_REQUEST;
            GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, ErrorMsg.DUPLICATE_NICKNAME, null);
            return new ResponseEntity<>(globalResDto,httpStatus);
        } else if (!isSamePassword(password,passwordConfirm)){
            httpStatus = HttpStatus.BAD_REQUEST;
            GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, ErrorMsg.PASSWORD_NOT_MATCHED, null);
            return new ResponseEntity<>(globalResDto,httpStatus);
        } else {
            httpStatus = HttpStatus.OK;
            Member member = new Member(signupReqDto);

            memberRepository.save(member);

            MemberInfoResDto memberInfoResDto = new MemberInfoResDto(member.getMemberId(), signupReqDto);

            GlobalResDto<MemberInfoResDto> globalResDto = new GlobalResDto<>(httpStatus, SuccessMsg.SIGNUP_SUCCESS, memberInfoResDto);
            return new ResponseEntity<>(globalResDto, httpStatus);
        }
    }


    private boolean isEmailInDB(String email){
        Member member = isPresentMemberByEmail(email);

        return member != null;
    }

    private boolean emailFormatChek(String email){
        String email_regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";

        return email.matches(email_regex) && email.endsWith(".com");
    }

    private boolean isNicknameInDB(String nickname){
        Member member = isPresentMemberByNickname(nickname);

        return member != null;
    }

    private  boolean isSamePassword(String password, String ConfirmPassword){
        return password.equals(ConfirmPassword);
    }


    @Transactional(readOnly = true)
    public Member isPresentMemberByEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member isPresentMemberByNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        return optionalMember.orElse(null);
    }
}
