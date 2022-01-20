package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.dao.VideoDao;
import com.prototype.classyBackEnd.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService{

    private final VideoDao videoDao;

    @Override
    public void save(Video video) {
        videoDao.save(video);
    }

    @Override
    public List<Video> getAllVideos() {
        return videoDao.findAllVideo();
    }

    @Override
    public Video getOneVideo() {
        return videoDao.findAllVideo().get(0);
    }


}
