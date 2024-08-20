package ru.anyline.repoapi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.anyline.repoapi.model.GitHubRepository;
import ru.anyline.repoapi.service.GitHubService;

import java.util.Optional;

public interface RedisRepository extends CrudRepository<GitHubRepository, String> {
    Optional<GitHubService> findByUsername(String username);
}
