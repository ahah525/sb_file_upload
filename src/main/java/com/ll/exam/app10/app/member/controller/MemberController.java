package com.ll.exam.app10.app.member.controller;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

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
    public String getProfile(Principal principal, Model model) {
        Member loginedMember = memberService.findByUsername(principal.getName());
        model.addAttribute("loginedMember", loginedMember);

        return "member/profile";
    }

    // 로그인폼
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String loginForm() {
        return "member/login_form";
    }
}
