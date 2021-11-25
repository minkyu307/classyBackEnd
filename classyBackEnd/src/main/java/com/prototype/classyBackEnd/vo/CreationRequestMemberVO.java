package com.prototype.classyBackEnd.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CreationRequestMemberVO {

    private Long kakaoId;

    private String authKey;

    @Email(message = "Wrong Email Type")
    @NotBlank(message = "Email No Value")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}",
            message = "Wrong Password Type")
    private String password;

    @NotBlank(message = "MemberName No Value")
    private String memberName;

    @NotBlank(message = "NickName No Value")
    private String classyNickName;
}
