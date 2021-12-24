package com.prototype.classyBackEnd.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.entity.ContentType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@ToString
public class S3file {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long S3fileId;

    private String fileName;
}
