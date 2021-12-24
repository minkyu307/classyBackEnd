package com.prototype.classyBackEnd;

import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.domain.S3file;
import com.prototype.classyBackEnd.service.MemberService;
import com.prototype.classyBackEnd.service.S3fileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServiceTest {

    @Autowired
    private S3fileService s3fileService;
    @Autowired
    private MemberService memberService;

    @Test
    public void 서비스테스트(){
        S3file s3file = new S3file();
        s3file.setFileName("11");
        s3fileService.save(s3file);
    }

    @Test
    public void 서비스테스트2(){
        Member member = new Member();
        member.setClassyIdName("2");
        member.setClassyNickName("3");
        memberService.save(member);
    }
}
