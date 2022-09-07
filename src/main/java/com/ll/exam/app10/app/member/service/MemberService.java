package com.ll.exam.app10.app.member.service;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;      // 기본 저장 경로
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long create(MemberCreateForm memberCreateForm) throws IOException {
        String bcryptPassword = passwordEncoder.encode(memberCreateForm.getPassword1());    // 암호화한 비밀번호

        String fileName = "member/" + randomUUID() + ".png";    // 랜덤한 파일명
        String uploadPath = genFileDirPath + "/" + fileName;      // 로컬에 저장할 경로(기본 저장경로 + 파일명)

        File profileImageFile = new File(uploadPath);
        MultipartFile profileImage = memberCreateForm.getProfileImage();

        profileImageFile.mkdirs();  // 업로드할 경로의 디렉토리가 존재하지 않으면 생성

        // 프로필 이미지 파일 로컬 외부 경로에 저장
        try {
            profileImage.transferTo(profileImageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }

        Member member = MemberCreateForm.toEntity(memberCreateForm, bcryptPassword, fileName);
        Member saveMember = memberRepository.save(member);

        return saveMember.getId();
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElse(null);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("member"));  // 생략해도됨

        // User: security User 객체(loginId, password, 권한)
        return new User(member.getUsername(), member.getPassword(), authorities);
    }
}
