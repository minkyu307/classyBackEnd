package com.prototype.classyBackEnd.controller;

import com.prototype.classyBackEnd.component.S3Component;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MemberService memberService;
    private final S3Component s3Component;
    private Map<String, String> strStrMap = new LinkedHashMap<>();


    @GetMapping(value = "/upload/profileImage")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("img") MultipartFile file,
                                                           @RequestParam("classyNickName") String classyNickName) throws Exception {

        strStrMap.clear();
        Member member = memberService.findOneByClassyNickName(classyNickName);

        s3Component.uploadMediaToS3(file, "classyImage/", member);

        strStrMap.put("SuccessCode", "ProfileImageUploadSuccess");
        return ResponseEntity.ok().body(strStrMap);
    }

    @GetMapping(value = "/upload/video")
    public ResponseEntity<Map<String, String>> uploadMedia(@RequestParam("video") MultipartFile file,
                                                           @RequestParam("classyNickName") String classyNickName) throws Exception {

        strStrMap.clear();
        Member member = memberService.findOneByClassyNickName(classyNickName);

        s3Component.uploadMediaToS3(file, "", member);

        strStrMap.put("SuccessCode", "VideoUploadSuccess");
        return ResponseEntity.ok().body(strStrMap);
    }



    @GetMapping(value = "/download/profileImage")
    public ResponseEntity<byte[]> getProfileImage(@RequestParam("classyNickName")String classyNickName) throws Exception{

        Member member = memberService.findOneByClassyNickName(classyNickName);

        return s3Component.getProfileImageByMember(member);
    }

}
