package ru.anyline.repoapi.controller;

import ru.anyline.repoapi.model.Repository;
import ru.anyline.repoapi.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GitHubController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/api/github/users/{username}/repos")
    public List<Repository> getUserRepos(@PathVariable String username) {
        return gitHubService.getRepositories(username);
    }
}
