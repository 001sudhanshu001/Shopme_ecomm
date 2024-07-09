package com.ShopMe.Entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.ShopMe.UtilityClasses.Constants;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, nullable = false, unique = true)
    private String email;
    @Column(length = 64, nullable = false)
    private String password;
    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 45, nullable = false)
    private String lastName;
    @Column(length = 64)
    private String photos;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
            )
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addRole(Role role){ // for testing purpose
        this.roles.add(role);
    }


    @Transient
    public String getPhotoImagePath(){

        if(id == null || photos == null) return "/images/default-user.png";

//        return "user-photos/" + this.id + "/" +this.photos;
//        return "/home/sudhanshu/Documents/MyBackup/D/E-Commerce/ShopMe/ShopMe/user-photos/6/Alfred.png";

       // return "/user-photos/" + this.id + "/" +this.photos; ***
      //  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++" + Constants.S3_BASE_URI);
        return Constants.S3_BASE_URI + "/user-photos/" + this.id + "/" + this.photos;
     //   return "";
    }
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public boolean hasRole(String roleName){
        for (Role role : this.roles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
