package com.ShopMe.UtilityClasses;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Slf4j
public class AmazonS3Util {

    private static final String BUCKET_NAME;

    static {
        BUCKET_NAME = "shopme305";
    }

    public static List<String> listFolder(String folderName) {
        S3Client client = S3Client.builder().build();

        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME).prefix(folderName).build();

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

        PutObjectRequest request = PutObjectRequest.builder().bucket(BUCKET_NAME)
                .key(folderName + "/" + fileName).acl("public-read").build();

        try(inputStream) {
            int contentLength = inputStream.available();
            client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        } catch (IOException e) {
            log.error("Could not upload to Amazon S3");
        }
    }

    public static void deleteFile(String fileName) {

        S3Client client = S3Client.builder().build();

        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(BUCKET_NAME)
                .key(fileName).build();

        client.deleteObject(request);

    }

    public static void removeFolder(String folderName) {
        S3Client client = S3Client.builder().build();

        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME).prefix(folderName).build();

        ListObjectsResponse response = client.listObjects(listRequest);
        List<S3Object> contents = response.contents();

        for (S3Object object : contents) {
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(BUCKET_NAME)
                    .key(object.key()).build();

            client.deleteObject(request);
        }
    }

}
