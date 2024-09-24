package ru.anyline.repoapi.service;

import lombok.AllArgsConstructor;
import ru.anyline.repoapi.repository.Repository;
import ru.anyline.repoapi.model.UserRepos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GitHubServiceImpl implements GitHubService{

    private final Repository repository;
    private final RestTemplate restTemplate;

    public List<UserRepos> getRepositories(String username) {

        List<UserRepos> cachedRepos = repository.findByUsername(username);
        if (!cachedRepos.isEmpty()) {
            return cachedRepos;
        }

        String url = String.format("https://api.github.com/users/%s/repos", username);
        ResponseEntity<UserRepos[]> response = restTemplate.getForEntity(url, UserRepos[].class);

        List<UserRepos> repositories = Arrays.asList(Objects.requireNonNull(response.getBody()));

        repositories.forEach(repo -> {
            repo.setId(null);
            repo.setUsername(username);
            repo.setRepoName(repo.getRepoName());
            repo.setUrl(repo.getUrl());
        });
        repository.saveAll(repositories);
        return repositories;
    }


    public UserRepos getRepository(String username, String repoName) {
        UserRepos cachedRepo = repository.findByUsernameAndRepoName(username, repoName);
        if (cachedRepo != null) {
            return cachedRepo;
        }
        String url = String.format("https://api.github.com/repos/%s/%s", username, repoName);
        ResponseEntity<UserRepos> response = restTemplate.getForEntity(url, UserRepos.class);

        UserRepos repo = response.getBody();
        if (repo != null) {
            repo.setId(null);
            repo.setUsername(username);
            repo.setRepoName(repo.getRepoName());
            repo.setUrl(repo.getUrl());
            repository.save(repo);
        }
        return repo;
    }

    public List<UserRepos> getCachedRepos(){
        return repository.findAll();
    }

    public List<UserRepos> getReposByUsername(String username){ return repository.findByUsername(username);  }

}