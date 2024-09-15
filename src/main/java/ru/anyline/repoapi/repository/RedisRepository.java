package ru.anyline.repoapi.repository;

import org.springframework.data.repository.CrudRepository;
import ru.anyline.repoapi.model.GitHubRepository;

public interface RedisRepository extends CrudRepository<GitHubRepository, String> {


}
