package com.ShopMe;

import com.ShopMe.DAO.SettingRepo;
import com.ShopMe.Entity.Setting;
import com.ShopMe.Entity.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepoTest {
    @Autowired
    SettingRepo repo;

    @Test
    public void testCreateGeneralSettings() {
//        Setting siteName = new Setting("SITE_NAME", "Shopme", SettingCategory.GENERAL);
//        Setting siteLogo = new Setting("SITE_LOGO", "Shopme.png", SettingCategory.GENERAL);
//        Setting copyRight = new Setting("COPYRIGHT", "Copyright (C) 2023 Shopme Ltd.", SettingCategory.GENERAL);
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "Before Price", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        repo.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandsPointType));

        Iterable<Setting> iterable = repo.findAll();

        for(Setting setting : iterable){
            assertThat(setting).isNotNull();
        }
//        Setting savedSetting = repo.save(siteName);

//        assertThat(savedSetting).isNotNull();
    }

    @Test
    public void testListSettingCategory() {
        List<Setting> settings = repo.findByCategory(SettingCategory.GENERAL);

        settings.forEach(setting -> System.out.println(setting));
    }

}
