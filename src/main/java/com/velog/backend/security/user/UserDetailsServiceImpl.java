package com.velog.backend.security.user;

import com.velog.backend.Repository.MemberRepository;
import com.velog.backend.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if (member == null) {
            return null;
        }else{
            UserDetailsImpl userDetails = new UserDetailsImpl();
            userDetails.setMember(member);
            return userDetails;
        }

    }
}
