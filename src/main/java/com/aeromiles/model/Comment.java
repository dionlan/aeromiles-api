package com.aeromiles.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "author")
    private String author;

    @Column(name = "avatar")
    private String avatar;
}
