package com.ll.exam.app10.app.fileUpload.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class FileUploadController {
    // 설정파일에 있는 custom.genFileDirPath 가져옴
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;      // 기본 저장 경로

    @PostMapping("")
    @ResponseBody
    public String upload(@RequestParam("img1") MultipartFile img1, @RequestParam("img2") MultipartFile img2) {
        try {
            img1.transferTo(new File(genFileDirPath + "/1.png"));
            img2.transferTo(new File(genFileDirPath + "/2.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "업로드 완료!";
    }
}
