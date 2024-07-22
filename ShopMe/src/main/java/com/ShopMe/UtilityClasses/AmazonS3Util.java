package com.ShopMe.UtilityClasses;

import com.ShopMe.constants.AwsConstants;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AmazonS3Util {

    private final AmazonS3 amazonS3;

    public static List<String> listFolder(String folderName) {
        S3Client client = S3Client.builder().build();

        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(AwsConstants.BUCKET_NAME).prefix(folderName).build();

        ListObjectsResponse response = client.listObjects(listRequest);
        List<S3Object> contents = response.contents();

        List<String> listKeys = new ArrayList<>();

        for (S3Object object : contents) {
            listKeys.add(object.key());
        }

        return listKeys;
    }

    public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
        S3Client client = S3Client.builder().build();

        PutObjectRequest request = PutObjectRequest.builder().bucket(AwsConstants.BUCKET_NAME)
                .key(folderName + "/" + fileName).acl(ObjectCannedACL.PUBLIC_READ).build();

        try(inputStream) {
            int contentLength = inputStream.available();
            client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (IOException e) {
            log.error("Could not upload to Amazon S3");
        }
    }

    public static void deleteFile(String fileName) {

        S3Client client = S3Client.builder().build();

        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(AwsConstants.BUCKET_NAME)
                .key(fileName).build();

        client.deleteObject(request);

    }

    public static void removeFolder(String folderName) {
        S3Client client = S3Client.builder().build();

        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(AwsConstants.BUCKET_NAME).prefix(folderName).build();

        ListObjectsResponse response = client.listObjects(listRequest);
        List<S3Object> contents = response.contents();

        for (S3Object object : contents) {
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(AwsConstants.BUCKET_NAME)
                    .key(object.key()).build();

            client.deleteObject(request);
        }
    }

     public String generatePreSignedUrl(String objectKey) {
        Date expiration = new Date(System.currentTimeMillis() + AwsConstants.expirationMinutes * 60 * 1000);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(AwsConstants.BUCKET_NAME, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

}
