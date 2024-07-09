package com.ShopMe.Controller;

import com.ShopMe.DAO.CurrencyRepo;
import com.ShopMe.Entity.Currency;
import com.ShopMe.Entity.settings.GeneralSettingBag;
import com.ShopMe.Entity.settings.Setting;
import com.ShopMe.Service.Impl.SettingService;
import com.ShopMe.UtilityClasses.AmazonS3Util;
import com.ShopMe.UtilityClasses.Constants;
import com.ShopMe.UtilityClasses.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;

    private final CurrencyRepo currencyRepo; // TO display list of currencies

    @GetMapping("/settings")
    public String listAll(Model model){
        List<Setting> listSettings = settingService.listAllSetting();
        List<Currency> listCurrency = currencyRepo.findAllByOrderByNameAsc();

        model.addAttribute("listCurrency", listCurrency);
        model.addAttribute("pageTitle", "Settings - Shopme Admin");
        for(Setting setting : listSettings){ // Instead of iterating in html page we did it here
            model.addAttribute(setting.getKey(), setting. getValue());
        }

        model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
        return "settings/settings";
    }

    // Since in the form we are not sending any new Setting() object so we have to use HttpServletRequest to fetch the data
    @PostMapping("/settings/save_general") // Same name as we used in the form
    public String saveGeneralSettings(@RequestParam("fileImage")MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes ra) throws IOException {

        GeneralSettingBag settingBag = settingService.getGeneralSettings();

        saveSiteLogo(multipartFile, settingBag);
        saveCurrencySymbol(request, settingBag);

        updateSettingValuesFromForm(request, settingBag.list());

        ra.addFlashAttribute("message","General Settings have been saved");

        return "redirect:/settings";

    }

    private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
        if(!multipartFile.isEmpty()){ // Saving Site Logo
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            settingBag.updateSiteLogo(value);

//            String uploadDir = "../site-logo";
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            String uploadDir = "site-logo";
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());

        }
    }

    private void saveCurrencySymbol( HttpServletRequest request, GeneralSettingBag settingBag){
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);

        if(findByIdResult.isPresent()){
            Currency currency = findByIdResult.get();
           // System.out.println("Currency" + currency);
            String symbol = currency.getSymbol();
            System.out.println(symbol);
            settingBag.updateCurrencySymbol(symbol);
        }
    }

    private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings){
        for(Setting setting : listSettings){
            String value = request.getParameter(setting.getKey());
          //  System.out.println("Printing Values : " + value);
            if(value != null){
                setting.setValue(value);
            }
        }
        settingService.saveAll(listSettings);
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailServerSetting = settingService.getMailServerSetting();

        updateSettingValuesFromForm(request, mailServerSetting);

        ra.addFlashAttribute("message", "Mail Server Settings have been saved");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTempllateSettings(HttpServletRequest request, RedirectAttributes ra) {
        List<Setting> mailTemplateSetting = settingService.getMailTemplateSetting();

        updateSettingValuesFromForm(request, mailTemplateSetting);

        ra.addFlashAttribute("message", "Mail Template Settings have been saved");

        return "redirect:/settings";
    }

}
