package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.domain.Member;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MemberService {
    Member save(Member member);
    Member findOneByKakaoId(Long id);
    boolean kakaoMemberExist(Member member);
    boolean emailMemberExist(Member member);
    boolean classyNickNameExist(Member member);
    void emailAuth(Member member) throws MessagingException, UnsupportedEncodingException;
    public void updateAuthStatus(String email)throws Exception;
    boolean emailAuthCorrect(Member member);
}
