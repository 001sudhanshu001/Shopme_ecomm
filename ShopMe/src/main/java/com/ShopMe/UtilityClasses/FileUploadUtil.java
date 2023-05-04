package com.ShopMe.UtilityClasses;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        Path uploadpath = Paths.get(uploadDir);
    //    System.out.println("Inside util method :: starting");
//
//        File f = new File(uploadDir); // path folder tak ka hai
//        if(!f.exists()){
//            f.mkdir();
//        }


        if(!Files.exists(uploadpath)) {
  //          System.out.println("Inside util method :: before createdir");
            Files.createDirectories(uploadpath);
  //          System.out.println("Inside util method :: after createdir");
        }

        try(InputStream inputStream = multipartFile.getInputStream()){
       //     System.out.println("1");
            Path filePath = uploadpath.resolve(fileName);
      //      System.out.println("2");

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
     //       System.out.println("3");

        }catch (IOException ex){
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    public static void cleanDir(String dir){ // method to delete previous image before uploading new one
        Path dirPath = Paths.get(dir);

        try{
            Files.list(dirPath).forEach(file -> {
                if(!Files.isDirectory(file)){ // purani sabhi images ko delete kr do
                    try {
                        Files.delete(file);
                    }catch (IOException ex){
                        System.out.println("Could not delete file: " + file);
                    }
                }
            });
        }catch (IOException e){
            System.out.println("Could not list directory: "+ dirPath);
        }
    }

    public static void removeDir(String dir){
        cleanDir(dir);

        try{
            Files.delete(Paths.get(dir));
        }catch (IOException ex){
            System.out.println("Could not remove directory :" + dir);
        }
    }
}
