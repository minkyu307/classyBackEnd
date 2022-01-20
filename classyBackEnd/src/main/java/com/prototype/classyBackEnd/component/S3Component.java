package com.prototype.classyBackEnd.component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.domain.Video;
import com.prototype.classyBackEnd.service.MemberService;
import com.prototype.classyBackEnd.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class S3Component {

    private final AmazonS3Client amazonS3Client;
    private final MemberService memberService;
    private final VideoService videoService;

    @Value("${cloud.aws.s3.inputMovOrMp4Bucket}")
    public String inputMovOrMp4Bucket;  // S3 버킷 이름

    @Value("${cloud.aws.s3.classy-bucket}")
    public String classyBucket;

    //미디어파일을 s3버킷에 저장
    public void uploadMediaToS3(MultipartFile file, String dirName, Member member) throws Exception{

        String fileName = createFileName(file.getOriginalFilename(), dirName);
        log.info("org={}",file.getOriginalFilename());
        log.info("name={}",file.getName());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        log.info("type={}",file.getContentType());

        try(InputStream inputStream = file.getInputStream()) {
            uploadFile(inputStream, objectMetadata, fileName);
            saveMediaToDB(file.getContentType(), fileName, member);
        }
        catch (IOException e){
            throw new IOException("fileConvertFail");
        }
    }

    //미디어 파일 이름을 알맞는 db에 저장
    private void saveMediaToDB(String type, String fileName, Member member) throws IOException {

        if(type.equals("video/mp4")||type.equals("video/quicktime")){
            Video video = new Video();
            video.setMember(member);
            video.setTitle(fileName.substring(0, fileName.length()-4));
            video.setViews(0L);
            video.setUploadDate(LocalDateTime.now().withNano(0));
            videoService.save(video);
        }
        else if (type.equals("image/jpeg")||type.equals("image/png")){
            member.setProfileImage(fileName);
            memberService.save(member);
        }
        else throw new IOException("NotSupportedFileType");
    }

    //목표 폴더 + 유니크한 이름 + 원래 파일 이름
    private String createFileName(String originalFileName, String dirName) throws Exception {
        return dirName+UUID.randomUUID().toString().concat(originalFileName);
    }

    //실제 s3업로드 메소드
    private void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName){
        String type = objectMetadata.getContentType();
        if(type.equals("image/jpeg")||type.equals("image/png")){
            amazonS3Client.putObject(new PutObjectRequest(classyBucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }
        else {
            amazonS3Client.putObject(new PutObjectRequest(inputMovOrMp4Bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }
    }


    //회원의 프로필 이미지 전송
    public ResponseEntity<byte[]> getProfileImageByMember(Member member) throws Exception{

        String storedFileName;
        log.info(member.getProfileImage());
        if(member.getProfileImage().equals(null)){
            throw new FileNotFoundException("ProfileImageNotFound");
        }
        else{
            storedFileName=member.getProfileImage();
        }

        //s3에서 이미지파일을 불러와 byte로 변환
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(classyBucket,storedFileName));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        //이미지 원래 이름만 추출
        String fileName = URLEncoder.encode(storedFileName,"UTF-8").substring(53);

        //헤더 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok().headers(httpHeaders).body(bytes);
    }

}
