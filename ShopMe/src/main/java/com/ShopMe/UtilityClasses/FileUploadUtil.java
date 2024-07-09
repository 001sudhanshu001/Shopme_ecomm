package com.ShopMe.UtilityClasses;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        Path uploadpath = Paths.get(uploadDir);

        if(!Files.exists(uploadpath)) {
            Files.createDirectories(uploadpath);
        }

        try(InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadpath.resolve(fileName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

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
