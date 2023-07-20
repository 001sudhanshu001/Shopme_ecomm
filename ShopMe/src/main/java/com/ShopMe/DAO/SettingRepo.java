package com.ShopMe.DAO;

import com.ShopMe.Entity.Setting;
import com.ShopMe.Entity.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepo extends JpaRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
}
