package com.ShopMe.Controller;

import com.ShopMe.Entity.User;
import com.ShopMe.Security.ShopmeUserDetails;
import com.ShopMe.Service.UserService;
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

    @GetMapping("/account") // module 7, video 8 @ 7:00
    public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser, Model model){
        String email = loggedUser.getUsername();
        User user = userService.getByEmail(email);
        model.addAttribute(user);
        model.addAttribute("pageTitle", "Your Account");

        return "users/account_form";
    }

    @PostMapping("/account/update") //@RequestParam is used to extract data from query
    public String saveDetails(User user, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal ShopmeUserDetails loggedUser,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        System.out.println("4");

        // ham sabhi user ki id ke naam se ek folder create krege(user-photos) mai
        if(!multipartFile.isEmpty()){ // checking if file is uploaded or not

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            user.setPhotos(fileName); // DB mai image name hoga aur Photo System mai save hoge
            User savedUser = this.userService.updateAccount(user);

            //   String uploadDir = "user-photos";
            // pehle is naam se ek foldar banaya fir uske andar id's se subfolder

            String uploadDir = "user-photos/" +savedUser.getId();

            // cleaning dir before uploading a new one
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); // this is Custom class

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
            // to show success message
            redirectAttributes.addFlashAttribute("message","Your account details have been updated");

            return "redirect:/account";
        }

    }

}
