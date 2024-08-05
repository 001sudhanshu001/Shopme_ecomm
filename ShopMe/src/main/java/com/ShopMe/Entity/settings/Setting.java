package com.ShopMe.Entity.settings;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Setting {
    @Id
    @Column(name = "`key`", nullable = false, length = 123)
    private String key;

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)

    private SettingCategory category;

    public Setting(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if(getClass() != obj.getClass()) return false;

        Setting other = (Setting) obj;
        if(key == null) {
            return other.key == null;
        }else return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }
}
