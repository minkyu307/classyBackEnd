package com.prototype.classyBackEnd.component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.prototype.classyBackEnd.domain.Member;
import com.prototype.classyBackEnd.domain.S3file;
import com.prototype.classyBackEnd.service.MemberService;
import com.prototype.classyBackEnd.service.S3fileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class S3Component {

    private final AmazonS3Client amazonS3Client;
    private final MemberService memberService;
    private final S3fileService s3fileService;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    //1
    public String uploadToProject(MultipartFile multipartFile, String dirName, Member member) throws IOException {
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("FileConvertFail"));

        return uploadToS3(uploadFile, dirName, member);
    }

    //3 S3로 파일 업로드하기
    private String uploadToS3(File uploadFile, String dirName, Member member) {
        String fileName = dirName + "/" + UUID.randomUUID() + "\\+" + uploadFile.getName();   // S3에 저장된 파일 이름

        //Member에 파일 이름 저장
        member.setProfileImage(fileName);
        memberService.save(member);

        /*S3file s3file = new S3file();
        s3file.setFileName(fileName);
        s3fileService.save(s3file);*/

        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    //4 S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    //5 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    //2 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    //s3버킷에 있는 파일 다운로드하기
    public ResponseEntity<byte[]> getOneFile() throws IOException {

        List<S3file> s3files = s3fileService.getAll();
        String storedFileName = s3files.get(0).getFileName();
        log.info("storedFileName = {}",storedFileName);

        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket,storedFileName));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(storedFileName,"UTF-8").substring(56);
        log.info("filename={}",fileName);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok().headers(httpHeaders).body(bytes);
    }

    public ResponseEntity<Map<String,byte[]>> getProfileImageByMember(Member member) throws Exception{

        Map<String,byte[]> map = new LinkedHashMap<>();
        String storedFileName;
        log.info(member.getProfileImage());
        if(member.getProfileImage().equals(null)){
            throw new FileNotFoundException("ProfileImageNotFound");
        }
        else{
            storedFileName=member.getProfileImage();
        }

        //s3에서 이미지파일을 불러와 byte로 변환
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket,storedFileName));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        //이미지 원래 이름만 추출
        String fileName = URLEncoder.encode(storedFileName,"UTF-8").substring(56);

        //이미지만 다운로드할때 헤더 설정
        /*HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);*/

        //이미지와 텍스트를 동시에 Multipart 형식으로 받고싶은데 여기를 잘 모르겠네요
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentDispositionFormData("attachment",fileName);

        map.put(member.getClassyNickName(),bytes);
        return ResponseEntity.ok().headers(httpHeaders).body(map);
    }

    /*public byte[] recieveOneImage() throws IOException {

        List<S3file> s3files = s3fileService.getAll();
        String storedFileName = s3files.get(0).getFileName();
        log.info("storedFileName = {}",storedFileName);

        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket,storedFileName));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(storedFileName,"UTF-8").substring(56);
        log.info("filename={}",fileName);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }*/
}
