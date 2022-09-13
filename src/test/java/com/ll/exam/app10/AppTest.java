package com.ll.exam.app10;

import com.ll.exam.app10.app.home.controller.HomeController;
import com.ll.exam.app10.app.member.controller.MemberController;
import com.ll.exam.app10.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"base-addi", "test"})   // bas-addi, test 프로필 빈만 로드
public class AppTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("메인화면에서는 안녕이 나와야 한다.")
    void t1() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(HomeController.class))
                .andExpect(handler().methodName("main"))
                .andExpect(content().string(containsString("안녕")));
    }

    @Test
    @DisplayName("회원의 수")
    void t2() throws Exception {
        long count = memberService.count();

        assertThat(count).isEqualTo(4);
    }

    @Test
    @DisplayName("user1로 로그인 후 프로필페이지에 접속하면 user1의 이메일이 보여야 한다.")
    @Rollback(false)
    @WithUserDetails("user1")
    void t3() throws Exception {
        // mockMvc로 로그인 처리
        // when
        ResultActions resultActions = mvc
                .perform(get("/member/profile"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getProfile"))
                .andExpect(content().string(containsString("user1@test.com")));
    }

    @Test
    @DisplayName("user2로 로그인 후 프로필페이지에 접속하면 user2의 이메일이 보여야 한다.")
    @Rollback(false)
    @WithUserDetails("user2")
    void t4() throws Exception {
        // mockMvc로 로그인 처리
        // when
        ResultActions resultActions = mvc
                .perform(get("/member/profile"))
                .andDo(print());
        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("getProfile"))
                .andExpect(content().string(containsString("user2@test.com")));
    }

    @Test
    @DisplayName("파일_다운로드하지않고_로컬파일을_form_전송한_방식으로_회원가입")
    @Rollback(false)
    void t5() throws Exception {
        String name = "profileImage";           // form input name
        String originalFileName = "1.png";      // 원래 파일명
        String contentType = "image/png";       // 파일 형식

        String path = "/Users/hanseung-yeon/Desktop/1.png";             // 파일 경로
        FileInputStream fileInputStream = new FileInputStream(path);    // 파일 경로로 생성한 InputStream

        MockMultipartFile profileImage = new MockMultipartFile(
                name,
                originalFileName,
                contentType,
                fileInputStream
        );

        // when(회원가입)
        ResultActions resultActions = mvc.perform(
                        multipart("/member/join")
                                .file(profileImage)
                                .param("username", "user3")
                                .param("password1", "1234")
                                .param("password2", "1234")
                                .param("email", "user3@test.com")
                                .characterEncoding("UTF-8"))
                .andDo(print());

        // then(5번 회원 생성)
    }

    @Test
    @DisplayName("다운로드한_파일을_form_전송한_방식으로_회원가입")
    @Rollback(false)
    void t6() throws Exception {
        String name = "profileImage";           // form input name
        String originalFileName = "test.png";      // 원래 파일명
        String contentType = "image/png";       // 파일 형식

        String testUploadFileUrl = "https://picsum.photos/200/300";

        // wget(파일 다운로드)
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Resource> response = restTemplate.getForEntity(testUploadFileUrl, Resource.class);
        InputStream inputStream = response.getBody().getInputStream();

        MockMultipartFile profileImage = new MockMultipartFile(
                name,
                originalFileName,
                contentType,
                inputStream
        );

        // when(회원가입)
        ResultActions resultActions = mvc.perform(
                        multipart("/member/join")
                                .file(profileImage)
                                .param("username", "user3")
                                .param("password1", "1234")
                                .param("password2", "1234")
                                .param("email", "user3@test.com")
                                .characterEncoding("UTF-8"))
                .andDo(print());

        // then(5번 회원 생성)
    }
}
