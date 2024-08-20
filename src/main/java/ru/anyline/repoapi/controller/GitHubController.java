package ru.anyline.repoapi.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.anyline.repoapi.model.GitHubRepository;
import ru.anyline.repoapi.service.GitHubService;

@RestController
public class GitHubController {

    private final GitHubService gitHubService;
    public GitHubController(GitHubService gitHubService){
        this.gitHubService = gitHubService;
    }
    @GetMapping("/repos/{username}")
    public Flux<GitHubRepository> getPublicRepositories(@PathVariable String username) {
        return gitHubService.getPublicRepositories(username);
    }

    @GetMapping("/private/{username}")
    public Flux<GitHubRepository> getPrivateRepositories(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        return gitHubService.getPrivateRepositories(username, token);
    }
}
