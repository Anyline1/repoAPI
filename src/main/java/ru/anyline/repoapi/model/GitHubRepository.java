package ru.anyline.repoapi.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("GitRepos")
public class GitHubRepository {

    @Id
    private String username;
    @JsonProperty("name")
    private String repoName;
    @JsonProperty("url")
    private String repoUrl;
    @JsonProperty("teams_url")
    private String teamsUrl;


}
