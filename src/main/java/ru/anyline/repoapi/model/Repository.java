package ru.anyline.repoapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String repoName;
    private String repoUrl;
    private String teamsUrl;
//    private LocalDateTime cachedAt;


}
