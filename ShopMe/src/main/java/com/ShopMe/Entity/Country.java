package com.ShopMe.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Set<State> states;
}
