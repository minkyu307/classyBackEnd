package com.prototype.classyBackEnd.controller;
import com.prototype.classyBackEnd.vo.CreationRequestMemberVO;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value = "/member/Hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok().body("TestGood!!");
    }

    @PostMapping(value = "/member/kakaoJoin")
    public ResponseEntity<Map<String,String>> joinKakaoMember(@RequestBody @Valid CreationRequestMemberVO crm, Errors errors){

        Map<String,String> map = new HashMap<>();
        Member member = new Member();
        member.insertCreationRequestMemberVO(crm);
        if(errors.hasErrors()){
            for(FieldError value:errors.getFieldErrors()){
                map.put("ErrorCode",value.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(map);
        }

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
            map.put("SuccessCode","KakaoJoinSuccess");
        }

        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/kakaoLogin")
    public ResponseEntity<Map<String,String>> loginKakoMember(@RequestBody Member member){

        Map<String ,String> map = new HashMap<>();
        try {
            Member findMember = memberService.findOneByKakaoId(member.getKakaoId());
            map.put("ClassyNickName",findMember.getClassyNickName());
        }
        catch (Exception e){
            map.put("ErrorCode","KakaoAccountNotFound");
        }
        return ResponseEntity.ok().body(map);
    }


    @PostMapping(value = "/member/emailAuth")
    public ResponseEntity<Map<String,String>> emailAuth(@RequestBody @Valid CreationRequestMemberVO crm, Errors errors)
            throws MessagingException, UnsupportedEncodingException {

        Map<String, String> map = new HashMap<>();
        Member member = new Member();
        member.insertCreationRequestMemberVO(crm);

        if(errors.hasErrors()){
            for(FieldError value:errors.getFieldErrors()){
                map.put("ErrorCode",value.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(map);
        }

        if (memberService.emailMemberExist(member)){
            map.put("ErrorCode", "EmailOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(memberService.classyNickNameExist(member)){
            map.put("ErrorCode","ClassyNickNameOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            member.setAuthStatus(0);
            memberService.emailAuth(member);
            map.put("SuccessCode","EmailSent");
        }

        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailUseAvailable")
    public ResponseEntity<Map<String,String>> emailUseAvailable(@RequestBody Member member) throws Exception {

        Map<String, String> map = new HashMap<>();
        if (!memberService.emailAuthCorrect(member)){
            map.put("ErrorCode", "CodeNotCorrect");
            return ResponseEntity.internalServerError().body(map);
        }
        memberService.updateAuthStatus(member.getEmail());
        map.put("SuccessCode","EmailAuthCompleted");
        return ResponseEntity.ok().body(map);
    }
}
