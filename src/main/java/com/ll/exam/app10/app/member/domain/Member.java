package com.ll.exam.app10.app.member.domain;

import com.ll.exam.app10.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;    // loginId

    private String password;

    @Column(unique = true)
    private String email;

    private String profileImageUrl; // 프로필 이미지 파일명
}
