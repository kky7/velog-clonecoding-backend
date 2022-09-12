package com.velog.backend.image;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3ImageUploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String innovationvelogclone;


    public String uploadImage(MultipartFile multipartFile, String dirName) throws IOException {

        // S3 에 저장되는 파일의 이름이 중복되지 않기 위해서 UUID로 생성한 랜덤값과 파일 이름을 연결하여 업로드 (dirName 은 폴더 이름)
        String fileName = dirName + "/" + UUID.randomUUID() + multipartFile.getName();

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(multipartFile.getSize());

        /** PutObjectRequest 는 Aws S3 버킷에 업로드할 객체 메타 데이터와 파일 데이터로 이루어져 있음.
         * 사진 파일을 로컬 스토리지에 저장하지 않고 업로드 하기 위해서는,
         * inputStream 을 통해 Byte 만이 전달 되어 해당 파일에 대한 정보가 없기 때문에
         * objectMetadata 에 파일에 대한 정보를 추가하여 매개변수로 같이 전달해야 함.
         */

        // S3 에 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(innovationvelogclone, fileName, multipartFile.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return amazonS3Client.getUrl(innovationvelogclone, fileName).toString();
    }

}
