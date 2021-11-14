package com.prototype.classyBackEnd.controller;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value = "/Hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok().body("Test Good!!!");
    }

    @PostMapping(value = "/member/kakaoJoin")
    public ResponseEntity<Map<String,String>> joinKakaoMember(@RequestBody Member member){

        Map<String,String> map = new HashMap<>();

        if(memberService.kakaoMemberExist(member)){
            map.put("ErrorCode","KakaoIdOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(memberService.emailMemberExist(member)){
            map.put("ErrorCode","EmailOvelap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(memberService.classyNickNameExist(member)){
            map.put("ErrorCode","ClassyNickNameOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else {
            memberService.save(member);
            map.put("ClassyNickName",member.getClassyNickName());
        }

        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/kakaoLogin")
    public ResponseEntity<Map<String,String>> loginKakoMember(@RequestBody Member member){

        log.error(member.getKakaoId());
        Map<String ,String> map = new HashMap<>();
        try {
            Member findMember = memberService.findOneByKakaoId(member.getKakaoId());
            map.put("ClassyNickName",findMember.getClassyNickName());
        }
        catch (Exception e){
            map.put("ErrorCdoe","KakaoAccountNotFound");
        }
        return ResponseEntity.ok().body(map);
    }


    @PostMapping(value = "/member/emailAuth")
    public ResponseEntity<String> emailAuth(@RequestBody Member member) throws MessagingException, UnsupportedEncodingException {

        log.error(member);
        if (memberService.emailMemberExist(member)){
            return ResponseEntity.internalServerError().body("EmailOverLap");
        }
        else {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            member.setAuthStatus(0);
            memberService.emailAuth(member);
        }

        return ResponseEntity.ok().body("EmailSent");
    }

    @PostMapping(value = "/member/emailUseAvailable")
    public ResponseEntity<String> emailJoin(@RequestBody Member member) throws Exception {
        Map<String, String> map = new HashMap<>();
        if (memberService.emailAuthCorrect(member)){
            memberService.updateAuthStatus(member.getEmail());
            return ResponseEntity.ok().body("EmailAuthCompleted");
        }
        return ResponseEntity.internalServerError().body("CodeNotCorrect");
    }
}
