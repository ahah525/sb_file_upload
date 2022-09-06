package com.ll.exam.app10.app.member.controller;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.domain.MemberCreateForm;
import com.ll.exam.app10.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입 폼
    @GetMapping("/join")
    public String joinForm(MemberCreateForm memberCreateForm) {
        return "member/signup_form";
    }

    // 회원가입과 동시에 로그인 처리
    @PostMapping("/join")
    public String join(MemberCreateForm memberCreateForm, HttpSession session) throws IOException {
        Member member = memberService.findByUsername(memberCreateForm.getUsername());
        // username(로그인 id) 중복 유효성 검사
        if (member != null) {
            return "redirect:/?errorMsg=Already done";
        }

        Long id = memberService.create(memberCreateForm);
        // 세션 정보 저장(로그인 처리)
        session.setAttribute("loginedMemberId", id);

        return "redirect:/member/profile";
    }

    // 회원 정보 조회
    @GetMapping("/profile")
    public String getProfile(HttpSession session, Model model) {
        Long loginedMemberId = (Long) session.getAttribute("loginedMemberId");
        boolean isLogined = loginedMemberId != null;

        // 로그인 여부 유효성 검사
        if(!isLogined) {
            return "redirect:/?errorMsg=Need to login";
        }

        Member member = memberService.findById(loginedMemberId);
        model.addAttribute("member", member);

        return "member/profile";
    }
}
