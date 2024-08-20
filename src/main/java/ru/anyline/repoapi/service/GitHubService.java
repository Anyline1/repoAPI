package ru.anyline.repoapi.service;

import org.springframework.cache.annotation.Cacheable;
import ru.anyline.repoapi.model.GitHubRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.anyline.repoapi.repository.RedisRepository;

@Service
public class GitHubService {

    private final WebClient webClient;
    private final RedisRepository redisRepository;

    public GitHubService(WebClient.Builder webClientBuilder, RedisRepository redisRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
        this.redisRepository = redisRepository;
    }
    @Cacheable(value = "repos", key = "#username")
    public Flux<GitHubRepository> getPublicRepositories(String username) {
        return this.webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(GitHubRepository.class)
                .doOnNext(redisRepository::save);
    }

    public Flux<GitHubRepository> getPrivateRepositories(String username, String token){
                return this.webClient.get()
                        .uri("/user/repos?visibility=private")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToFlux(GitHubRepository.class)
                        .doOnNext(redisRepository::save);

            }
}