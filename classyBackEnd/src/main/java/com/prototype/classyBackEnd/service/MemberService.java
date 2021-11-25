package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.domain.Member;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MemberService {
    Member save(Member member);
    Member findOneByKakaoId(Long id);
    boolean kakaoMemberExist(Long id);
    String emailMemberExistOrNotAuthed(String email);
    boolean classyNickNameExist(String nickName);
    void emailAuth(Member member) throws MessagingException, UnsupportedEncodingException;
    void updateAuthStatus(String email)throws Exception;
    boolean emailAuthCorrect(Member member);
    void deleteMember(Member member);
    Member findOneByEmail(String email);
    void persistAndClear();
}
