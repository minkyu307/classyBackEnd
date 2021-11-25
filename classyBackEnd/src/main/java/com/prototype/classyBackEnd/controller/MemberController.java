package com.prototype.classyBackEnd.controller;
import com.prototype.classyBackEnd.vo.CreationRequestMemberVO;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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

        if(memberService.kakaoMemberExist(member.getKakaoId())){
            map.put("ErrorCode","KakaoIdOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(memberService.emailMemberExistOrNotAuthed(member.getEmail()).equals("Exist")){
            map.put("ErrorCode","EmailOvelap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(memberService.classyNickNameExist(member.getClassyNickName())){
            map.put("ErrorCode","ClassyNickNameOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else {
            member.setAuthStatus(1);
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


    /*@PostMapping(value = "/member/emailAuth")
    public ResponseEntity<Map<String,String>> emailAuth(@RequestBody CreationRequestMemberVO crm, Errors errors)
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
    }*/

    @PostMapping(value = "/member/emailAuthRequest")
    public ResponseEntity<Map<String,String>> emailAuthRequest(@RequestBody Member member) throws MessagingException, UnsupportedEncodingException {

        Map<String, String> map = new HashMap<>();
        member.setAuthStatus(0);
        member.setMemberName("temp");
        member.setClassyNickName("temp");
        if(memberService.emailMemberExistOrNotAuthed(member.getEmail()).equals("Exist")){
            map.put("ErrorCode","EmailOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(!isValidEmailAddress(member.getEmail())||member.getEmail().isEmpty()){
            map.put("ErrorCode", "EmailTypeError");
            return ResponseEntity.badRequest().body(map);
        }
        else if(memberService.emailMemberExistOrNotAuthed(member.getEmail()).equals("NotAuthed")){
            memberService.deleteMember(member);
            try {
                memberService.emailAuth(member);
            }
            catch (Exception e){
                map.put("ErrorCode","EmailNotSent");
                return ResponseEntity.internalServerError().body(map);
            }
            map.put("SuccessCode","ReEmailSent");
        }
        else{
            try {
                memberService.emailAuth(member);
            }
            catch (Exception e){
                map.put("ErrorCode","EmailNotSent");
                return ResponseEntity.internalServerError().body(map);
            }
            map.put("SuccessCode","EmailSent");
        }

        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailAuth")
    public ResponseEntity<Map<String, String>> emailAuth(@RequestBody Member member){
        Map<String, String> map = new HashMap<>();
        Member newMember;
        try {
            newMember = memberService.findOneByEmail(member.getEmail());
        }
        catch (Exception e){
            map.put("ErrorCode", "NoSuchEmail");
            return ResponseEntity.badRequest().body(map);
        }

        if(!memberService.emailAuthCorrect(member)){
            map.put("ErrorCode", "CodeNotCorrect");
            return ResponseEntity.internalServerError().body(map);
        }
        newMember.setAuthStatus(1);
        memberService.persistAndClear();
        map.put("SuccessCode","EmailAuthCompleted");
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailJoin")
    public ResponseEntity<Map<String,String>> emailJoin(@RequestBody @Valid CreationRequestMemberVO crm,
                                                                Errors errors) throws Exception {

        Map<String, String> map = new HashMap<>();
        Member newMember;
        try {
            newMember = memberService.findOneByEmail(crm.getEmail());
        }
        catch (Exception e){
            map.put("ErrorCode", "NoSuchEmail");
            return ResponseEntity.badRequest().body(map);
        }


        if(errors.hasErrors()){
            for(FieldError value:errors.getFieldErrors()){
                map.put("ErrorCode",value.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(map);
        }
        else if(memberService.classyNickNameExist(crm.getClassyNickName())){
            map.put("ErrorCode","ClassyNickNameOverlap");
            return ResponseEntity.internalServerError().body(map);
        }
        else if(newMember.getAuthStatus()==0){
            map.put("ErrorCode", "AccountNotAuthed");
            return ResponseEntity.internalServerError().body(map);
        }

        newMember.insertCreationRequestMemberVO(crm);
        memberService.persistAndClear();
        map.put("SuccessCode","EmailJoinCompleted");
        return ResponseEntity.ok().body(map);
    }


    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}
