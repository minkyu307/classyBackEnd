package com.prototype.classyBackEnd.domain;

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

}
