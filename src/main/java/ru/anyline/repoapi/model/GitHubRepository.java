package ru.anyline.repoapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("GitRepos")
public class GitHubRepository implements Serializable {

    @Id
    private String username;
    @JsonProperty("name")
    private String name;
    @JsonProperty("html_url")
    private String repoUrl;
}
