package com.ll.exam.app10.app.member.service;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.repository.MemberRepository;
import com.ll.exam.app10.util.Util;
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

        MultipartFile profileImg = memberCreateForm.getProfileImg();    // 프로필 이미지 파일
        String profileImgDirName = "member/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");   // 프로필 이미지 저장할 디렉토리
        String fileName = null;

        // 1. 프로필 이미지 있는 경우
        if(profileImg != null) {
            String ext = Util.file.getExt(profileImg.getOriginalFilename());    // 확장자 추출
            fileName = randomUUID() + "." + ext;    // 랜덤한 파일명
            String profileImgDirPath = genFileDirPath + "/" + profileImgDirName;      // 로컬 저장 디렉토리 경로(기본 경로 + 디렉토리)
            String profileImgFilePath = profileImgDirPath + "/" + fileName;             // 로컬 저장 파일 경로

            new File(profileImgDirPath).mkdirs();   // 파일을 저장할 디렉토리 경로 생성

            // 프로필 이미지 파일 로컬 외부 경로에 저장
            try {
                profileImg.transferTo(new File(profileImgFilePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalStateException e) {
                throw new RuntimeException(e);
            }
        }

        String profileImgRelPath = profileImgDirName + "/" + fileName;  // 상대 경로
        Member member = MemberCreateForm.toEntity(memberCreateForm, bcryptPassword, profileImgRelPath);
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

    public long count() {
        return memberRepository.count();
    }

    public void removeProfileImg(Member member) {
        member.removeProfileImgOnStorage(); // 파일삭제
        member.setProfileImg(null);

        memberRepository.save(member);
    }
}
