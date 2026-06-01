package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "authors")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 2000)
    private String name;

    @Column(name = "surname", nullable = false, length = 2000)
    private String surname;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "pseudonym")
    private String pseudonym;
}