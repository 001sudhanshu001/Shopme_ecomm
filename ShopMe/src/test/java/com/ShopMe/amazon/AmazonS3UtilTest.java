package com.ShopMe.amazon;

import com.ShopMe.UtilityClasses.AmazonS3Util;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AmazonS3UtilTest {

    @Test
    public void tesListIterator() {
        String folderName = "user-photos/5";
        List<String> list = AmazonS3Util.listFolder(folderName);

        list.forEach(System.out::println);
    }

    @Test
    public void testUploadFile() throws FileNotFoundException {
        String folderName = "test-upload";
        String fileName = "dragon-ball-flying-nimbus-kid-goku-wallpaper-preview.jpg";
        String filePath = "/home/sudhanshu/Desktop/" + fileName;

        InputStream inputStream = new FileInputStream(filePath);

        AmazonS3Util.uploadFile(folderName, fileName, inputStream);
    }

    @Test
    public void testDeleteFile() {
        String fileName = "test-upload/dragon-ball-flying-nimbus-kid-goku-wallpaper-preview.jpg";
        AmazonS3Util.deleteFile(fileName);
    }

    @Test
    public void testRemoveFolder() {
        String folderName = "test-upload";
        AmazonS3Util.removeFolder(folderName);
    }
}
