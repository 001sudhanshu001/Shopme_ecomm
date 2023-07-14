package com.ShopMe.Controller;

import com.ShopMe.Entity.Role;
import com.ShopMe.Entity.User;
import com.ShopMe.ExceptionHandler.UserNotFoundException;
import com.ShopMe.Exporter.User.UserCsvExporter;
import com.ShopMe.Exporter.User.UserExcelExporter;
import com.ShopMe.Exporter.User.UserPdfExporter;
import com.ShopMe.Service.Impl.UserServiceImpl;
import com.ShopMe.Service.UserService;
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


    //----------- this url will always show first page -----------
    @GetMapping("/users")
    public String getAllUsers(Model model) { // by default sorting will be asc based on firstName

        return this.listByPage(1, model, "firstName", "asc", null); // firstName ki spelling same honi chaiye jo user entity mai hai
    }                                                                                   // starting mai koi searching nahi ahi
            //--------------- paging ----------------
    @GetMapping("/users/page/{pageNumber}") // @Param is used to get value from query
    public String listByPage(@PathVariable(name = "pageNumber") int pageNumber, Model model,
                                @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                @Param("keyword") String keyword) {

        Page<User> page = this.userService.listByPage(pageNumber, sortField, sortDir, keyword);
        List<User> allUser = page.getContent();

        long startCount = (long) (pageNumber - 1) * UserServiceImpl.USER_PER_PAGE + 1;
        long endCount = startCount + UserServiceImpl.USER_PER_PAGE - 1;
        System.out.println("In user controller" + startCount);

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

    // -------- to show a form to create new user -----------
    @GetMapping("/users/new")
    public String newUser(Model model){

        List<Role> listRoles = this.userService.listRoles();

        User user = new User();
        user.setEnabled(true); // by default user is enabled
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Create New User");

        return "users/user_form";
    }

    // new user or update user ke liye form handler yahi hai
    @PostMapping("/users/save") //@RequestParam is used to extract data from query
    public String saveUser(User user, RedirectAttributes redirectAttributes, // 'user' will come from Form
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        System.out.println("4");

        // ham sabhi user ki id ke naam se ek folder create krege(user-photos) mai
        if(!multipartFile.isEmpty()){ // checking if file is uploaded or not

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            user.setPhotos(fileName); // DB mai image name hoga aur Photo System mai save hoge
            User savedUser = this.userService.save(user);

            //   String uploadDir = "user-photos";
            // pehle is naam se ek foldar banaya fir uske andar id's se subfolder

            String uploadDir = "user-photos/" +savedUser.getId();

            // cleaning dir before uploading a new one
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); // this is Custom class

        }else {
            // saving only remaining if file is empty
            if(user.getPhotos().isEmpty()){
                user.setPhotos(null);
            }
            this.userService.save(user);
            // to show success message

        }

        redirectAttributes.addFlashAttribute("message","The user has been saved successfully.");
        return getRedirectURLtoAffectedUser(user);

    }
    private String getRedirectURLtoAffectedUser(User user){ // to show only affected user after updating
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }


    //-------------------- edit(Update) the user ------------------
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        Optional<User> optionalUser = this.userService.get(id);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error_message", "User Not Found With Id " + id
            );
            return "redirect:/users"; // redirect to the users page with message error
        }

        User user = optionalUser.get();// humne DB main pehle se pade user ko le kr page pe bhej diya jisse update kiya jaa sktha hai

        List<Role> listRoles = this.userService.listRoles();
        model.addAttribute("user",user);
        model.addAttribute("listRoles",listRoles);
        model.addAttribute("pageTitle","Edit the user(ID : "+id+ ")");

        return "users/user_form";
    }


    // ------------------ delete user -----------------
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes){
        try {

            this.userService.deleteUser(id);
            // if we want to delete user photo also
//            String userDir = "../user-photos/" + id;
//            FileUploadUtil.removeDir(userDir);
            redirectAttributes.addFlashAttribute("message","User with Id " + id +  " has been deleted successfully");


        }catch (UserNotFoundException e){ // message mai vo message ayega jo humne UserService class se bheja hai
            redirectAttributes.addFlashAttribute("error_message",e.getMessage());
        }
        return "redirect:/users"; // redirect to the users page with proper message

    }


    // ----------- handling enableing and disabling the user

   // th:href="@{'/users/' + ${user.id} + '/enabled/true'}"
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){

        this.userService.updateUSerEnabledStatus(id, enabled);

        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }

    // ----------- export to csv -------------

    @GetMapping("/users/export/csv")
    public void exportTOCSV(HttpServletResponse response) throws IOException {
        List<User> allUser = this.userService.getAllUser();
        UserCsvExporter exporter = new UserCsvExporter();

        exporter.export(allUser, response);
    }

    // ----------- export ot Excel -------------
    @GetMapping("/users/export/excel")
    public void exportTOExcel(HttpServletResponse response) throws IOException {
        List<User> allUser = this.userService.getAllUser();

        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(allUser, response);
    }

    // ----------- export ot PDF -------------
    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> allUser = this.userService.getAllUser();

        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(allUser, response);
    }

}
