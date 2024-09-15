package ru.anyline.repoapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRepos{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        String username;
        @JsonProperty("name")
        String repoName;
        @JsonProperty("html_url")
        String url;

}
