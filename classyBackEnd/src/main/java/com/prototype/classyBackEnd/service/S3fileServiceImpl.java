package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.dao.S3fileDao;
import com.prototype.classyBackEnd.domain.S3file;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class S3fileServiceImpl implements S3fileService{

    private final S3fileDao s3fileDao;

    @Override
    public void save(S3file s3file) {
        s3fileDao.save(s3file);
    }

    @Override
    public List<S3file> getAll() {

        return s3fileDao.getAll();
    }
}
