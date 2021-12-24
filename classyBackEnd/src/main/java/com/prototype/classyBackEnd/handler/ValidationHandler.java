package com.prototype.classyBackEnd.handler;

import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import com.prototype.classyBackEnd.vo.CreationRequestMemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.ValidationException;

@Component
@RequiredArgsConstructor
public class ValidationHandler {

    private final MemberService memberService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ValidationHandler HasValidationError(Errors errors) throws Exception{
        if(errors.hasErrors()){
            for(FieldError value:errors.getFieldErrors()){
                throw new ValidationException(value.getDefaultMessage());
            }
        }
        return this;
    }

    public ValidationHandler KakaoMemberExist(Member member) throws Exception {
        if(memberService.kakaoMemberExist(member.getKakaoId())){
            throw new ValidationException("kakaoIdOverlap");
        }
        return this;
    }

    public ValidationHandler EmailMemberExist(Member member) throws Exception{
        if(memberService.emailMemberExistOrNotAuthed(member.getEmail()).equals("Exist")){
            throw new ValidationException("EmailOverlap");
        }
        return this;
    }

    public ValidationHandler ClassyNickNameExist(Member member) throws Exception{
        if(memberService.classyNickNameExist(member.getClassyNickName())){
            throw new ValidationException("ClassyNickNameOverlap");
        }
        return this;
    }

    public ValidationHandler ClassyNickNameExist(CreationRequestMemberVO member) throws Exception{
        if(memberService.classyNickNameExist(member.getClassyNickName())){
            throw new ValidationException("ClassyNickNameOverlap");
        }
        return this;
    }

    public ValidationHandler IsValidEmailAddress(Member member) throws Exception{
        if(!isValidEmailAddress(member.getEmail())||member.getEmail().isEmpty()){
            throw new ValidationException("EmailTypeError");
        }
        return this;
    }

    public String EmailMemberExistOrNotAuthedAndSendMailResult(Member member) throws Exception{
        if(memberService.emailMemberExistOrNotAuthed(member.getEmail()).equals("NotAuthed")){
            memberService.deleteMember(member);
            memberService.emailAuth(member);
            return "ReEmailSent";
        }
        else{
            memberService.emailAuth(member);
            return "EmailSent";
        }
    }

    public ValidationHandler IsAuthCodeCorrect(Member member) throws Exception{
        if(!memberService.emailAuthCorrect(member)){
            throw new ValidationException("CodeNotCorrect");
        }
        return this;
    }

    public ValidationHandler IsAuthed(Member member) throws Exception{
        if(member.getAuthStatus()==0){
            throw new ValidationException("AccountNotAuthed");
        }
        return this;
    }

    public ValidationHandler IsPasswordMatch(Member member, Member newMember) throws Exception{
        if(!passwordEncoder.matches(member.getPassword(),newMember.getPassword())){
            throw new ValidationException("IncorrectPassword");
        }
        return this;
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

