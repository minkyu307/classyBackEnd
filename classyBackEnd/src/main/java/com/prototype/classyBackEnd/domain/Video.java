package com.prototype.classyBackEnd.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long videoId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long views;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    private String thumbNailImage;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private Member member;
}
