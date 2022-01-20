package com.prototype.classyBackEnd.service;

import com.prototype.classyBackEnd.domain.Video;

import java.util.List;

public interface VideoService {
    void save(Video video);
    List<Video> getAllVideos();
    Video getOneVideo();
}
