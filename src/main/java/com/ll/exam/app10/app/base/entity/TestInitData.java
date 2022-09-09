package com.ll.exam.app10.app.base.entity;

import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.stream.IntStream;

@Configuration
@Profile("test")    // test 프로필로 지정
public class TestInitData {
    // CommandLineRunner : 주로 앱 실행 직후 초기 데이터 세팅, 초기화에 사용
    @Bean
    CommandLineRunner init(MemberService memberService) {
        return args -> {
            IntStream.rangeClosed(1, 4).forEach(id -> {
                MemberCreateForm memberCreateForm = MemberCreateForm.builder()
                        .username("user%d".formatted(id))
                        .email("user%d@test.com".formatted(id))
                        .password1("1234")
                        .password2("1234")
                        .build();
                try {
                    memberService.create(memberCreateForm);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }
}
