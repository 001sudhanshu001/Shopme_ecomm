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
        String dirName ="user-photos";
        Path userPhotoDir = Paths.get(dirName);

        String userPhotosPath = userPhotoDir.toFile().getAbsolutePath();
                                                        // all
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + userPhotosPath + "/");


        // -------------- for categories --------------
        String categoryImagesDirName ="../category-images";
        Path categoryImagesDir = Paths.get(categoryImagesDirName);

        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
        // all
        registry.addResourceHandler("/category-images/**")
                .addResourceLocations("file:/" + categoryImagesPath + "/");

        //----------------------- for brands -------------------
        String brandLogosDirName = "../brand-logos";
        Path brandLogosDir = Paths.get(brandLogosDirName);

        String brandLogosPath = brandLogosDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/brand-logos/**")
                .addResourceLocations("file:/" + brandLogosPath + "/");

    }
}
