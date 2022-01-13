package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.component.TempKey;
import com.prototype.classyBackEnd.dao.MemberDao;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.handler.MailHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
    @Async
    public void emailAuth(Member member) throws MessagingException, UnsupportedEncodingException {
        String key = tempKey.getKey(10,false);
        String htmlContent = emailHtmlContentAppendAuthkey(key);

        member.setAuthKey(key);
        memberDao.save(member);

        MailHandler sendMail = new MailHandler(javaMailSender);
        sendMail.setSubject("회원가입 서비스 이메일 인증 입니다.");
        sendMail.setText(htmlContent);
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

    public String emailHtmlContentAppendAuthkey(String key){
        return "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid lightgreen; margin: 100px auto; padding: 30px 0; box-sizing: border-box;\">\n" +
                "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">\n" +
                "\t\t<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">CLASSY</span><br />\n" +
                "\t\t<span style=\"color: lightgreen;\">메일 인증</span> 안내입니다.\n" +
                "\t</h1>\n" +
                "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">\n" +
                "\t\t안녕하세요.<br />\n" +
                "\t\tCLASSY에 가입해 주셔서 진심으로 감사드립니다.<br />\n" +
                "\t\t아래 <b style=\"color: lightgreen;\">'인증 코드'</b> 를 CLASSY앱의 '인증 코드 입력'란에 입력하여<br/> 인증을 진행해주세요.<br />\n" +
                "\t\t감사합니다.\n" +
                "\t</p>\n" +
                "\n" +
                "\t<p style=\"font-size: 16px; margin: 40px 5px 20px; line-height: 28px;\">\n" +
                "\t\t인증 코드: <br />\n" +
                "\t\t<span style=\"font-size: 24px;\">"+key+"</span>\n" +
                "\t</p>\n" +
                "\t\n" +
                "\t\n" +
                "</div>";
    }
}
