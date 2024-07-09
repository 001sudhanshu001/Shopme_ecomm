package com.ShopMe.UtilityClasses;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

   @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//       registry.addResourceHandler("user-photos/**")
//               .addResourceLocations("/home/sudhanshu/Documents/MyBackup/D/E-Commerce/ShopMe/ShopMe/user-photos/34/target.png");

       // for Older version
       /*
        exposeDirectory("user-photos", registry);
        exposeDirectory("../category-images", registry);
        exposeDirectory("../brand-logos", registry);
        exposeDirectory("../product-images", registry);
       exposeDirectory("../site-logo", registry);
       */

       exposeDirectory("user-photos", registry);
       exposeDirectory("category-images", registry);
       exposeDirectory("brand-logos", registry);
       exposeDirectory("product-images", registry);
       exposeDirectory("site-logo", registry);
   }

   private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry){
       Path path = Paths.get(pathPattern);

       String absolutePath = path.toFile().getAbsolutePath();
       String logicalPath = pathPattern.replace("../","") + "/**";

       registry.addResourceHandler(logicalPath)
               .addResourceLocations("file://" + absolutePath + "/");
       /*
       System.out.println("--------------------------------");
       System.out.println(pathPattern);
       Path path = Paths.get(pathPattern);

       String absolutePath = path.toFile().getAbsolutePath();
       System.out.println(absolutePath);

       String logicalPath = pathPattern.replace("../","") + "/**";

       System.out.println(logicalPath);
       String filePath = "file://" + absolutePath + "/";
       System.out.println(filePath);


       System.out.println("----------------------------------");
       registry.addResourceHandler(pathPattern)
               .addResourceLocations(filePath);

        */

       //       /home/sudhanshu/Documents/MyBackup/D/E-Commerce/ShopMe/ShopMe/user-photos/34/target.png
//       2023-10-14 16:56:55.995  WARN 64877 --- [nio-8080-exec-7] o.s.w.s.resource.PathResourceResolver    :
//       "Resource path "34/target.png" was successfully resolved but resource
//       "file://home/sudhanshu/Documents/MyBackup/D/E-Commerce/ShopMe/ShopMe/user-photos/34/target.png"
//       is neither under the current location
//       "file://home/sudhanshu/Documents/MyBackup/D/E-Commerce/ShopMe/ShopMe/user-photos/**"
//       nor under any of the allowed locations [
//       URL [file://home/sudhanshu/Documents/MyBackup/D/E-Commerce/ShopMe/ShopMe/user-photos/**]]"

   }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String dirName ="user-photos";
//        Path userPhotoDir = Paths.get(dirName);
//
//        String userPhotosPath = userPhotoDir.toFile().getAbsolutePath();
//                                                        // all
//        registry.addResourceHandler("/" + dirName + "/**")
//                .addResourceLocations("file:/" + userPhotosPath + "/");
//
//
//        // -------------- for categories --------------
//        String categoryImagesDirName ="../category-images";
//        Path categoryImagesDir = Paths.get(categoryImagesDirName);
//
//        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
//        // all
//        registry.addResourceHandler("/category-images/**")
//                .addResourceLocations("file:/" + categoryImagesPath + "/");
//
//        //----------------------- for brands -------------------
//        String brandLogosDirName = "../brand-logos";
//        Path brandLogosDir = Paths.get(brandLogosDirName);
//
//        String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();
//
//        registry.addResourceHandler("/brand-logos/**")
//                .addResourceLocations("file:/" + brandLogosPath + "/");
//
//    }
}
