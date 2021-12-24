package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.component.TempKey;
import com.prototype.classyBackEnd.dao.MemberDao;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.handler.MailHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberDao memberDao;
    private final JavaMailSender javaMailSender;
    private final TempKey tempKey;

    @Override
    public Member save(Member member){
        return memberDao.save(member).get();
    }

    @Override
    public Member findOneByKakaoId(Long id) throws SQLException {
        return memberDao.findOneByKakaoId(id).get();
    }

    @Override
    public Member findOneByClassyNickName(String classyNickName) throws SQLException {
        return memberDao.findOneByClassyNickName(classyNickName).get();
    }

    @Override
    public Member findOneByEmail(String email) throws SQLException {
        return memberDao.findOneByEmail(email).get();
    }

    @Override
    public void persistAndClear() {
        memberDao.persistAndClear();
    }

    @Override
    public boolean kakaoMemberExist(Long id) {
        List<Member> members = memberDao.findMembersByKakaoId(id);
        if(members.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public String emailMemberExistOrNotAuthed(String email) {
        List<Member> members = memberDao.findMembersByEmail(email);
        String temp="Exist";
        if(members.isEmpty()){
            temp="NotExist";
        }
        else{
            if(members.get(0).getAuthStatus()==0)
                temp="NotAuthed";
        }
        return temp;
    }

    @Override
    public boolean classyNickNameExist(String nickName) {
        List<Member> members = memberDao.findMembersByClassyNickName(nickName);
        if(members.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public void emailAuth(Member member) throws MessagingException, UnsupportedEncodingException {
        String key = tempKey.getKey(10,false);
        member.setAuthKey(key);
        memberDao.save(member);
        MailHandler sendMail = new MailHandler(javaMailSender);
        sendMail.setSubject("회원가입 서비스 이메일 인증 입니다.]");
        sendMail.setText(new StringBuffer()
                .append("<h1>가입 메일인증 입니다</h1>")
                .append("<h2>인증코드 : "+key+"</h2>").toString());
        sendMail.setFrom("holli307@gmail.com", "testmail");
        sendMail.setTo(member.getEmail());
        sendMail.send();
    }

    @Override
    public void updateAuthStatus(String email) throws Exception {
        memberDao.authStatusUpdate(email);
    }

    @Override
    public boolean emailAuthCorrect(Member member) {
        if (memberDao.emailAuthCorrect(member.getEmail(),member.getAuthKey())){
            return true;
        }
        return false;
    }

    @Override
    public void deleteMember(Member member) {
        memberDao.deleteMemberByEmail(member);
    }

}
