package com.ll.exam.app10;

import com.ll.exam.app10.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"base-addi", "test"})   // bas-addi, test 프로필 빈만 로드
public class AppTest {
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원의 수")
    @Rollback(false)
    void t2() throws Exception {
        long count = memberService.count();

        assertThat(count).isEqualTo(4);
    }
}
