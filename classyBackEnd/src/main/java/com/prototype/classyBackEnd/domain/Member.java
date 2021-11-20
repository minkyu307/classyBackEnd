package com.prototype.classyBackEnd.domain;

import com.prototype.classyBackEnd.vo.CreationRequestMemberVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memberId;

    private Long kakaoId;

    private String email;

    private String password;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String classyNickName;

    private String authKey;

    private int authStatus;

    public void insertCreationRequestMemberVO(CreationRequestMemberVO crm){
        this.kakaoId=crm.getKakaoId();
        this.email=crm.getEmail();
        this.password=crm.getPassword();
        this.memberName= crm.getMemberName();
        this.classyNickName= crm.getClassyNickName();
    }
}
