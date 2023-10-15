package com.ShopMe.DAO;

import com.ShopMe.Entity.settings.Setting;
import com.ShopMe.Entity.settings.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepo extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
}
