package com.ll.exam.app10.app.home.controller;

import com.ll.exam.app10.app.member.domain.Member;
import com.ll.exam.app10.app.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberService memberService;

    @RequestMapping("/")
    public String main(Model model, Principal principal) {
        Member loginMember = null;
        String loginMemberProfileImgUrl = null;

        if (principal != null && principal.getName() != null) {
            loginMember = memberService.findByUsername(principal.getName());
        }
        if (loginMember != null) {
            loginMemberProfileImgUrl = loginMember.getProfileImageUrl();
        }

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("loginMemberProfileImgUrl", loginMemberProfileImgUrl);

        return "home/main";
    }

    @GetMapping("/test/upload")
    public String uploadForm() {
        return "home/test/upload";
    }
}