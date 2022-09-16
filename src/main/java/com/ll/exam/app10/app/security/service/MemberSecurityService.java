package com.ll.exam.app10.app.security.service;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.repository.MemberRepository;
import com.ll.exam.app10.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElse(null);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("member"));  // 생략해도됨

        // User: security User 객체(loginId, password, 권한)
        // MemberContext: security User 객체를 상속받은 객체(loginId, passowrd, 권한
        // User -> MemberContext로 변경
        return new MemberContext(member, authorities);
    }
}
