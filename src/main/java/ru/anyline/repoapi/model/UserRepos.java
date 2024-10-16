package ru.anyline.repoapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRepos{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private final String username;
        @JsonProperty("name")
        private final String repoName;
        @JsonProperty("html_url")
        private final String url;

}
