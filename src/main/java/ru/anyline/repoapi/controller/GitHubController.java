package ru.anyline.repoapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.anyline.repoapi.model.UserRepos;
import ru.anyline.repoapi.service.GitHubServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@Tag(name = "GitHub public repos",description = "API для получения списка публичных репо")
public class GitHubController {

    private final GitHubServiceImpl gitHubServiceImpl;

    public GitHubController(GitHubServiceImpl gitHubServiceImpl){
        this.gitHubServiceImpl = gitHubServiceImpl;
    }


@GetMapping("/repos/{username}")
@Tag(name = "GitHub public repos", description = "API для получения списка публичных репо")
public ResponseEntity<List<UserRepos>> getRepositories(@PathVariable String username) {
    if (username == null || username.isEmpty()) {
        return ResponseEntity.badRequest().body(null);
    }

    try {
        List<UserRepos> repos = gitHubServiceImpl.getRepositories(username);
        return ResponseEntity.ok(repos);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
    }
}



    @GetMapping("/cached")
@Tag(name = "Get all repos from DB",description = "Выводит JSON список всех сохраненных репо")
public ResponseEntity<List<UserRepos>> getAllRepos(){
    List<UserRepos> repos = gitHubServiceImpl.getCachedRepos();
    return ResponseEntity.ok(repos);
}



}