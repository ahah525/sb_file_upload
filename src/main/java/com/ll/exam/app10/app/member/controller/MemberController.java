package com.ll.exam.app10.app.member.controller;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입 폼
    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String joinForm(MemberCreateForm memberCreateForm) {
        return "member/signup_form";
    }

    // 로그인폼
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String loginForm() {
        return "member/login_form";
    }

    // 회원가입과 동시에 로그인 처리
    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(HttpServletRequest req, MemberCreateForm memberCreateForm) throws IOException {
        Member member = memberService.findByUsername(memberCreateForm.getUsername());
        // username(로그인 id) 중복 유효성 검사
        if (member != null) {
            return "redirect:/?errorMsg=Already done";
        }
        // 회원가입
        Long id = memberService.create(memberCreateForm);
        // security 로그인 처리
        try {
            // 로그인 id, 평문 비밀번호
            req.login(memberCreateForm.getUsername(), memberCreateForm.getPassword1());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/member/profile";
    }

    // 회원 정보 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String getProfile() {
        return "member/profile";
    }

    // 회원 이미지 조회
    @GetMapping("/profile/img/{id}")
    public ResponseEntity<Object> showProfileImg(@PathVariable Long id) throws URISyntaxException {
//        return "redirect:" + memberService.findById(id).getProfileImgUrl();
        // 302 cache 붙이기(검색해서 사용)
        URI redirectUri = new URI(memberService.findById(id).getProfileImgUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        // 1시간 마다 새로 요청
        httpHeaders.setCacheControl(CacheControl.maxAge(60 * 60 * 1, TimeUnit.SECONDS));

        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }
}
