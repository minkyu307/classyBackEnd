package com.prototype.classyBackEnd.controller;

import com.prototype.classyBackEnd.component.S3Component;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MemberService memberService;
    private final S3Component s3Component;

    @PostMapping(value = "/profileImageUpload")
    public ResponseEntity<Map<String, String>> imageUpload(@RequestParam("image") MultipartFile multipartFile,
                                                           @RequestParam("classyNickName") String classyNickName) throws Exception {

        Map<String, String> map = new LinkedHashMap<>();
        Member member = memberService.findOneByClassyNickName(classyNickName);

        s3Component.uploadToProject(multipartFile,"classyImage", member);

        map.put("SuccessCode","ImageUploadSuccess");
        return ResponseEntity.ok().body(map);
    }

    @GetMapping(value = "/image/getOneImage")
    public ResponseEntity<byte[]> download() throws IOException {
        return s3Component.getOneFile();
    }

    @GetMapping(value = "/profileImageDownload")
    public ResponseEntity<Map<String,byte[]>> getProfileImage(@RequestParam("classyNickName")String classyNickName) throws Exception{

        Member member = memberService.findOneByClassyNickName(classyNickName);

        return s3Component.getProfileImageByMember(member);
    }
}
