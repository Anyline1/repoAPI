package ru.anyline.repoapi.service;

import ru.anyline.repoapi.repository.Repository;
import ru.anyline.repoapi.model.UserRepos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class GitHubService {


    private final Repository repository;

    public GitHubService(Repository repository){
        this.repository = repository;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    public List<UserRepos> getRepositories(String username) {

        List<UserRepos> cachedRepos = repository.findByUsername(username);
        if (!cachedRepos.isEmpty()) {
            return cachedRepos;
        }

        String url = "https://api.github.com/users/" + username + "/repos";
        ResponseEntity<UserRepos[]> response = restTemplate.getForEntity(url, UserRepos[].class);

        List<UserRepos> repositories = Arrays.asList(Objects.requireNonNull(response.getBody()));
        repositories.forEach(repo -> {
            repo.setUsername(username);
            repo.setName(repo.getName());
            repo.setHtml_url(repo.getHtml_url());
            repo.setTeams_url(repo.getTeams_url());
        });
        repository.saveAll(repositories);

        return repositories;
    }

    public List<UserRepos> getAllRepos(){
        return repository.findAll();
    }

}
