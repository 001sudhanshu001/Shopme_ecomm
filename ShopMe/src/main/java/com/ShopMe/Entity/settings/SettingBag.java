package com.ShopMe.Entity.settings;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SettingBag {
    private List<Setting> listSettings;

    public Setting get(String key){
        int index = listSettings.indexOf(new Setting(key)); // it will use equals() method of Setting class to compare object
        if(index >= 0){
            return listSettings.get(index);
        }
        return null;
    }

    public String getValue(String key) {
        Setting setting = this.get(key);
        if(setting != null){
            return setting.getValue();
        }
        return null;
    }

    public void update(String key, String value){
        Setting setting = get(key);
        System.out.println("The key is :"+ key + "value: " + value);
        System.out.println("In Update currency Symbol" + setting);
        if(setting != null && value != null) {
            System.out.println("Setting symbol " + value);
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return listSettings;
    }
}
