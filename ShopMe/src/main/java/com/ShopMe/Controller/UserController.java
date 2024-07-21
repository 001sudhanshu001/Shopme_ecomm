package com.ShopMe.Controller;

import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import com.ShopMe.ExceptionHandler.UserNotFoundException;
import com.ShopMe.Exporter.User.UserCsvExporter;
import com.ShopMe.Exporter.User.UserExcelExporter;
import com.ShopMe.Exporter.User.UserPdfExporter;
import com.ShopMe.Service.Impl.UserServiceImpl;
import com.ShopMe.Service.UserService;
import com.ShopMe.UtilityClasses.AmazonS3Util;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final ModelMapper modelMapper;


    @GetMapping("/users")
    public String getAllUsers(Model model) {

        return this.listByPage(1, model, "firstName", "asc", null);
    }

    @GetMapping("/users/page/{pageNumber}") // @Param is used to get value from query
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
                                @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                @Param("keyword") String keyword) {

        Page<User> page = this.userService.listByPage(pageNumber, sortField, sortDir, keyword);
        List<User> allUser = page.getContent();

        long startCount = (long) (pageNumber - 1) * UserServiceImpl.USER_PER_PAGE + 1;
        long endCount = startCount + UserServiceImpl.USER_PER_PAGE - 1;

        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("allUser", allUser);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "users/users";

    }

    @GetMapping("/users/new")
    public String newUser(Model model){

        List<Role> listRoles = this.userService.listRoles();

        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Create New User");

        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()){

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            user.setPhotos(fileName);
            User savedUser = this.userService.save(user);

//            String uploadDir = "/user-photos/" + savedUser.getId();
            String uploadDir = "user-photos/" + savedUser.getId();

            // cleaning dir before uploading a new one
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

        }else {
            if(user.getPhotos().isEmpty()){
                user.setPhotos(null);
            }
            this.userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message","The user has been saved successfully.");
        return getRedirectURLtoAffectedUser(user);

    }
    private String getRedirectURLtoAffectedUser(User user){
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }


    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        Optional<User> optionalUser = this.userService.get(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error_message", "User Not Found With Id " + id
            );
            return "redirect:/users";
        }

        User user = optionalUser.get();

        List<Role> listRoles = this.userService.listRoles();
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Edit the user(ID : "+id+ ")");

        return "users/user_form";
    }


    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try {

            this.userService.deleteUser(id);
            // if we want to delete user photo also
//            String userDir = "../user-photos/" + id;
//            FileUploadUtil.removeDir(userDir);

            String userDir = "user-photos/" + id;
            AmazonS3Util.removeFolder(userDir);
            redirectAttributes.addFlashAttribute("message",
                    "User with Id " + id +  " has been deleted successfully");


        }catch (UserNotFoundException e){
            redirectAttributes.addFlashAttribute("error_message",e.getMessage());
        }
        return "redirect:/users";

    }

   // th:href="@{'/users/' + ${user.id} + '/enabled/true'}"
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes){

        this.userService.updateUSerEnabledStatus(id, enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }


    @GetMapping("/users/export/csv")
    public void exportTOCSV(HttpServletResponse response) throws IOException {
        List<User> allUser = this.userService.getAllUser();
        UserCsvExporter exporter = new UserCsvExporter();

        exporter.export(allUser, response);
    }

    @GetMapping("/users/export/excel")
    public void exportTOExcel(HttpServletResponse response) throws IOException {
        List<User> allUser = this.userService.getAllUser();

        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(allUser, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> allUser = this.userService.getAllUser();

        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(allUser, response);
    }

}
