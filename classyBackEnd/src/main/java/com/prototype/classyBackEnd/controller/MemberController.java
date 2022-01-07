package com.prototype.classyBackEnd.controller;
import com.prototype.classyBackEnd.handler.ValidationHandler;
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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ValidationHandler validationHandler;

    @GetMapping(value = "/member/Hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok().body("TestGood!!");
    }

    @PostMapping(value = "/member/kakaoJoin")
    public ResponseEntity<Map<String,String>> joinKakaoMember(@RequestBody @Valid CreationRequestMemberVO crm, Errors errors) throws Exception {

        Map<String,String> map = new HashMap<>();
        Member member = new Member();
        member.insertCreationRequestMemberVO(crm);

        validationHandler
                .HasValidationError(errors)
                .KakaoMemberExist(member)
                .EmailMemberExist(member)
                .ClassyNickNameExist(member);

        //카카오 회원가입 성공
        member.setAuthStatus(1);
        memberService.save(member);
        map.put("SuccessCode","KakaoJoinSuccess");

        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/kakaoLogin")
    public ResponseEntity<Map<String,String>> loginKakoMember(@RequestBody Member member) throws SQLException {

        Map<String ,String> map = new HashMap<>();

        //카카오 계정 찾기 시도
        Member findMember = memberService.findOneByKakaoId(member.getKakaoId());
        map.put("ClassyNickName",findMember.getClassyNickName());

        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailAuthRequest")
    public ResponseEntity<Map<String,String>> emailAuthRequest(@RequestBody Member member) throws Exception {

        Map<String, String> map = new HashMap<>();
        member.setAuthStatus(0);
        member.setClassyIdName("temp");
        member.setClassyNickName("temp");

        String EmailSentResult = validationHandler.EmailMemberExist(member)
                .IsValidEmailAddress(member)
                .EmailMemberExistOrNotAuthedAndSendMailResult(member);

        map.put("SuccessCode",EmailSentResult);
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailAuth")
    public ResponseEntity<Map<String, String>> emailAuth(@RequestBody Member member) throws Exception {

        Map<String, String> map = new HashMap<>();
        Member newMember;

        //인증할 이메일 계정 찾기 시도
        newMember = memberService.findOneByEmail(member.getEmail());

        //인증 코드 확인
        validationHandler.IsAuthCodeCorrect(member);

        //인증완료시 계정 인증을 1로 바꾸고 DB에 저장
        newMember.setAuthStatus(1);
        memberService.persistAndClear();
        map.put("SuccessCode","EmailAuthCompleted");
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailJoin")
    public ResponseEntity<Map<String,String>> emailJoin(
            @RequestBody @Valid CreationRequestMemberVO crm, Errors errors) throws Exception {

        Map<String, String> map = new HashMap<>();
        Member newMember;

        //인증할 이메일 계정 찾기 시도
        newMember = memberService.findOneByEmail(crm.getEmail());

        //VO, 닉네임, 인증여부 검증
        validationHandler.HasValidationError(errors)
                .ClassyNickNameExist(crm)
                .IsAuthed(newMember);

        newMember.insertCreationRequestMemberVO(crm);
        //비밀번호 암호화한후
        newMember.setPassword(passwordEncoder.encode(newMember.getPassword()));
        //DB에 저장하고 성공메시지
        memberService.persistAndClear();
        map.put("SuccessCode","EmailJoinCompleted");
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/member/emailLogin")
    public ResponseEntity<Map<String, String>> emailLogin(@RequestBody Member member) throws Exception {

        Map<String, String> map = new LinkedHashMap<>();
        Member newMember;

        newMember = memberService.findOneByEmail(member.getEmail());

        validationHandler.IsPasswordMatch(member, newMember);

        //성공시 회원 이름과 닉네임을 반환
        map.put("SuccessCode","LoginSuccess");
        map.put("MemberName", newMember.getClassyIdName());
        map.put("ClassyNickName",newMember.getClassyNickName());
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
