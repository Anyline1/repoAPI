package ru.anyline.repoapi.model;

import jakarta.persistence.Entity;

import javax.persistence.*;

@Entity
public class UserProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long userId;

    public String getId() {
        return null;
    }

    // Getters and setters
}