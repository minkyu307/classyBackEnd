package com.prototype.classyBackEnd.dao;

import com.prototype.classyBackEnd.domain.S3file;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class S3fileDao {

    private final EntityManager em;

    public void save(S3file s3file){
        em.persist(s3file);
    }

    public List<S3file> getAll(){
        return em.createQuery("select s from S3file s").getResultList();
    }
}
