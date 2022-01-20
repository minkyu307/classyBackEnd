package com.prototype.classyBackEnd.dao;

import com.prototype.classyBackEnd.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class VideoDao {
    private final EntityManager em;

    public void save(Video video){
        em.persist(video);
    }

    public List<Video> findAllVideo(){
        return em.createQuery("select v from Video v").getResultList();
    }
}
