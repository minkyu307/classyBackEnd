package com.prototype.classyBackEnd.domain;

import com.prototype.classyBackEnd.vo.CreationRequestMemberVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private String classyIdName;

    @Column(nullable = false)
    private String classyNickName;

    private String authKey;

    private int authStatus;

    @OneToMany(mappedBy = "member")
    private List<Video> videos = new ArrayList<>();

    private String profileImage;

    public void insertCreationRequestMemberVO(CreationRequestMemberVO crm){
        this.setKakaoId(crm.getKakaoId());
        this.setEmail(crm.getEmail());
        this.setPassword(crm.getPassword());
        this.setClassyIdName(crm.getClassyIdName());
        this.setClassyNickName(crm.getClassyNickName());
        this.setAuthKey(crm.getAuthKey());
    }
}
