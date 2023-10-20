package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.SettingRepo;
import com.ShopMe.Entity.settings.GeneralSettingBag;
import com.ShopMe.Entity.settings.Setting;
import com.ShopMe.Entity.settings.SettingCategory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SettingService {
    private SettingRepo settingRepo;

    public List<Setting> listAllSetting() {
        return settingRepo.findAll();
    }

    public GeneralSettingBag getGeneralSettings() {
        List<Setting> settings = new ArrayList<>();

        List<Setting> generalSettings = settingRepo.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepo.findByCategory(SettingCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingBag(generalSettings);
    }

    public void saveAll(Iterable<Setting> settings){
        System.out.println("In saveAll of Service, the settings are : " + settings);
        settingRepo.saveAll(settings);
    }

    public List<Setting> getMailServerSetting() {
        return settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
    }

    public List<Setting> getMailTemplateSetting() {
        return settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATE);
    }

    public List<Setting> getCurrencySettings() {
        return settingRepo.findByCategory(SettingCategory.CURRENCY);
    }
}
