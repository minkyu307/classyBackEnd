package com.prototype.classyBackEnd.controller;

import com.prototype.classyBackEnd.domain.Video;
import com.prototype.classyBackEnd.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
public class StreamingController {

    private final VideoService videoService;

    @Value("${cloud.aws.cloudFrontUrl}")
    private static String cloudFrontUrl;

    @GetMapping(value = "streaming/getone")
    public String getOneVideoUrl() throws Exception{
        Video video = videoService.getOneVideo();
        StringBuilder resultUrl = new StringBuilder()
                .append(cloudFrontUrl)
                .append(video.getTitle())
                .append("/Default")
                .append("/HLS/")
                .append(video.getTitle())
                .append("_720.m3u8");
        log.info(resultUrl);
        return "redirect:"+ resultUrl;
    }
}
