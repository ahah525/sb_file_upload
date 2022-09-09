package com.ll.exam.app10.app.member.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateForm {
    private String username;
    private String password1;
    private String password2;
    private String email;
    private MultipartFile profileImage; // 프로필 이미지 파일

    public static Member toEntity(MemberCreateForm memberCreateForm, String bcryptPassword, String fileName) {
        return Member.builder()
                .username(memberCreateForm.getUsername())
                .password(bcryptPassword)
                .email(memberCreateForm.getEmail())
                .profileImageUrl(fileName)
                .build();
    }
}
