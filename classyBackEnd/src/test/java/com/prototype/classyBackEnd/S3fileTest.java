package com.prototype.classyBackEnd;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.prototype.classyBackEnd.component.S3Component;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class S3fileTest {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Test
    public void 파일다운로드테스트() throws IOException {
        /*List<S3file> s3files = s3fileService.getAll();
        String storedFileName = s3files.get(0).getFileName();
        System.out.println("storedFileName = " + storedFileName);


        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucket,storedFileName));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        System.out.println("bytes = " + bytes);

        String fileName = URLEncoder.encode(storedFileName,"UTF-8").substring(56);
        System.out.println("fileName = " + fileName);*/

        /*HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);*/
    }
}
