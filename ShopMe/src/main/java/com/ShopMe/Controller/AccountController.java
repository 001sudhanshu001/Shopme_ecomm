package com.ShopMe.Controller;

import com.ShopMe.Entity.User;
import com.ShopMe.Security.ShopmeUserDetails;
import com.ShopMe.Service.UserService;
import com.ShopMe.UtilityClasses.AmazonS3Util;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser, Model model){
        String email = loggedUser.getUsername();
        User user = userService.getByEmail(email);
        model.addAttribute(user);
        model.addAttribute("pageTitle", "Your Account");

        return "users/account_form";
    }

    @PostMapping("/account/update")
    public String saveDetails(User user, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal ShopmeUserDetails loggedUser,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()){

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            user.setPhotos(fileName);
            User savedUser = this.userService.updateAccount(user);

            String uploadDir = "user-photos/" +savedUser.getId();

            // cleaning dir before uploading a new one
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); // this is Custom class

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

            redirectAttributes.addFlashAttribute("message","Your account details have been updated");

            return "redirect:/account";

        }else {
            // saving only remaining if file is empty
            if(user.getPhotos().isEmpty()){
                user.setPhotos(null);
            }
            this.userService.updateAccount(user);

            loggedUser.setFirstName(user.getFirstName());
            loggedUser.setlastName(user.getLastName());

            redirectAttributes.addFlashAttribute("message","Your account details have been updated");

            return "redirect:/account";
        }

    }

}
