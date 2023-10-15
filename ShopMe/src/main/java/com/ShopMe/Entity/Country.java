package com.ShopMe.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    @NotBlank
    @NotEmpty
    private String name;

    @Column(nullable = false, length = 5)
    @NotBlank
    @NotEmpty
    private String code;

    @OneToMany(mappedBy = "country")
    @JsonManagedReference
    private Set<State> states;

    public Country(Integer countryId) {
        this.id = countryId;
    }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
