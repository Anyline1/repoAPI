package ru.anyline.repoapi.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import ru.anyline.repoapi.model.GitHubRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.anyline.repoapi.repository.RedisRepository;

@Service
@AllArgsConstructor
public class GitHubService {

    private final WebClient webClient;
    private final RedisRepository redisRepository;

    @Cacheable(value = "user", key = "#username")
    public Flux<GitHubRepository> getPublicRepositories(String username) {
        return this.webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(GitHubRepository.class)
                .doOnNext(repo -> {
                    repo.setUsername(username);
                    redisRepository.save(repo);
                });
    }

    @Cacheable(value = "repos", key = "#username")
    public Flux<GitHubRepository> getPrivateRepositories(String username, String token){
                return this.webClient.get()
                        .uri("/user/repos?visibility=private")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(GitHubRepository.class)
                        .doOnNext(redisRepository::save);

            }
}
