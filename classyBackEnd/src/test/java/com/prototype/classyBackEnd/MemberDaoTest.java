package com.prototype.classyBackEnd;

import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberDaoTest {

    @Autowired
    MemberService memberService;

    @Test
    void MemberInsert(){
        Member member = new Member();
        member.setKakaoId(123123L);
        member.setClassyIdName("kim2");
        member.setClassyNickName("clas2");
        memberService.save(member);
    }
}
