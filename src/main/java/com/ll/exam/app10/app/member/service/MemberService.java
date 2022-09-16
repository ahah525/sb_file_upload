package com.ll.exam.app10.app.member.service;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.repository.MemberRepository;
import com.ll.exam.app10.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;      // 기본 저장 경로
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 프로필 이미지 저장할 디렉토리
    private String getCurrentProfileImgDirName() {
        return "member/" + Util.date.getCurrentDateFormatted("yyyy_MM_dd");
    }

    public Long create(MemberCreateForm memberCreateForm) throws IOException {
         // TODO : 리팩토링
        String bcryptPassword = passwordEncoder.encode(memberCreateForm.getPassword1());    // 암호화한 비밀번호

        MultipartFile profileImg = memberCreateForm.getProfileImg();    // 프로필 이미지 파일
        String profileImgDirName = getCurrentProfileImgDirName();
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
        // 2. 프로필 이미지 없으면 기본 이미지 세팅
        if(profileImg == null) {
            setProfileImgByUrl(member, "https://picsum.photos/200/300");
        }
        // 회원 저장
        Member saveMember = memberRepository.save(member);

        return saveMember.getId();
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }




    public long count() {
        return memberRepository.count();
    }

    public void removeProfileImg(Member member) {
        member.removeProfileImgOnStorage(); // 파일삭제
        member.setProfileImg(null);

        memberRepository.save(member);
    }

    public void setProfileImgByUrl(Member member, String url) {
        String filePath = Util.file.downloadImg(url, genFileDirPath + "/" + getCurrentProfileImgDirName() + "/" + UUID.randomUUID());
        member.setProfileImg(getCurrentProfileImgDirName() + "/" + new File(filePath).getName());
    }
}
