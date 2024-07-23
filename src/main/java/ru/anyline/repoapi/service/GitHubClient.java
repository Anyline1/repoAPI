package ru.anyline.repoapi.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "githubClient", url = "https://api.github.com")
public interface GitHubClient {

    @GetMapping("/users/{username}/repos")
    List<GitHubRepo> listRepos(@PathVariable("username") String username);

    @Getter
    @Setter
    class GitHubRepo {
        private String name;
        private String html_url;
        private String teams_url;

    }
}
