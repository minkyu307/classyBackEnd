package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.domain.S3file;

import java.util.List;

public interface S3fileService {
    void save(S3file s3file);
    List<S3file> getAll();
}
