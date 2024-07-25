package ru.anyline.repoapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.anyline.repoapi.repository.RepositoryRepository;
import ru.anyline.repoapi.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private GitHubClient gitHubClient;


    private final ObjectMapper objectMapper;

    public GitHubService(ObjectMapper objectMapper) {
        this.objectMapper = new ObjectMapper();
        // Настройка PrettyPrinter
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String convertReposToJson(List<Repository> repos) throws IOException, JsonProcessingException {
        return objectMapper.writeValueAsString(repos);
    }

    public List<Repository> getRepositories(String username) {
        List<Repository> cachedRepos = repositoryRepository.findByUsername(username);
        if (!cachedRepos.isEmpty()) {
            return cachedRepos;
        }

        List<Repository> repos = gitHubClient.listRepos(username).stream().map(repo -> {
            Repository repository = new Repository();
            repository.setUsername(username);
            repository.setRepoName(repo.getName());
            repository.setRepoUrl(repo.getHtml_url());
            repository.setTeamsUrl(repo.getTeams_url());
//            repository.setCachedAt(LocalDateTime.now());
            return repository;
        }).collect(Collectors.toList());

        repositoryRepository.saveAll(repos);
        return repos;
    }
}
